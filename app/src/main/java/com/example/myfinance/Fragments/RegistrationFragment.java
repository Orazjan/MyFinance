package com.example.myfinance.Fragments;

// import static android.content.ContentValues.TAG; // Удалите эту строку

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationFragment extends Fragment {
    private static final String TAG = "RegistrationFragment"; // Правильное объявление TAG

    private TextView textViewForLogin;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button regButton;

    private TextView usernameErrorTextView;
    private TextView passwordErrorTextView;

    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;
    private FirebaseAuth auth; // Объявлено, но не инициализировано

    private OnRegSuccessListener RegSuccessListener; // Тип RegistrationFragment.OnRegSuccessListener не нужен, просто OnRegSuccessListener

    public RegistrationFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRegSuccessListener) {
            RegSuccessListener = (OnRegSuccessListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegSuccessListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        auth = FirebaseAuth.getInstance();

        textViewForLogin = view.findViewById(R.id.textViewForLogin);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        regButton = view.findViewById(R.id.reg_button);
        usernameErrorTextView = view.findViewById(R.id.username_error_text_view);
        passwordErrorTextView = view.findViewById(R.id.password_error_text_view);

        Log.d("RegistrationFragment", "usernameEditText: " + (usernameEditText != null));
        Log.d("RegistrationFragment", "passwordEditText: " + (passwordEditText != null));
        Log.d("RegistrationFragment", "regButton: " + (regButton != null));

        regButton.setEnabled(false);

        setupTextWatchers();

        regButton.setOnClickListener(v -> {
            Log.d(TAG, "Registration button clicked.");
            if (isUsernameValid && isPasswordValid) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                createUser(email, password);
            } else {
                Toast.makeText(getContext(), "Пожалуйста, исправьте ошибки ввода", Toast.LENGTH_SHORT).show();
            }
        });


        textViewForLogin.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    public interface OnRegSuccessListener {
        void onRegSuccess();
    }

    // --- Метод setupTextWatchers теперь вызывается ---
    private void setupTextWatchers() {
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int int_after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int int_after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateUsername(editable.toString());
                updateRegistrationButtonState(); // Изменено название
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int int_after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int int_after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePassword(editable.toString());
                updateRegistrationButtonState(); // Изменено название
            }
        });
    }

    /**
     * Проверка ввода пользователя (Email)
     *
     * @param username Введенный текст email
     */
    private void validateUsername(String username) {
        // Убраны неактуальные проверки Build.VERSION.SDK_INT
        if (username.trim().isEmpty()) {
            isUsernameValid = false;
            usernameErrorTextView.setText("Email не может быть пустым");
            usernameErrorTextView.setVisibility(View.VISIBLE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            isUsernameValid = false;
            usernameErrorTextView.setText("Введите корректный Email адрес");
            usernameErrorTextView.setVisibility(View.VISIBLE);
        } else {
            isUsernameValid = true;
            usernameErrorTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Проверка ввода пароля
     *
     * @param password Введенный текст пароля
     */
    private void validatePassword(String password) {
        if (password.trim().isEmpty()) {
            isPasswordValid = false;
            passwordErrorTextView.setText("Пароль не может быть пустым");
            passwordErrorTextView.setVisibility(View.VISIBLE);
        } else if (password.length() < 6) { // Firebase требует минимум 6 символов
            isPasswordValid = false;
            passwordErrorTextView.setText("Пароль должен быть не менее 6 символов");
            passwordErrorTextView.setVisibility(View.VISIBLE);
        } else {
            isPasswordValid = true;
            passwordErrorTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Обновление состояния кнопки регистрации (активна, если оба поля валидны)
     */
    private void updateRegistrationButtonState() { // Переименовано для ясности
        regButton.setEnabled(isUsernameValid && isPasswordValid);
    }

    /**
     * Регистрация пользователя
     *
     * @param email
     * @param password
     */
    private void createUser(String email, String password) {
        Log.d(TAG, "Attempting to create user: " + email);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(getContext(), "Регистрация успешна! Добро пожаловать, " + user.getEmail(), Toast.LENGTH_LONG).show();
                    if (RegSuccessListener != null) {
                        RegSuccessListener.onRegSuccess(); // Вызов коллбэка об успехе
                        Log.d(TAG, "RegSuccessListener.onRegSuccess() called.");
                    } else {
                        Log.w(TAG, "RegSuccessListener is null. Cannot notify activity of successful registration.");
                    }
                } else {
                    Log.e(TAG, "Registration failed: " + task.getException().getMessage(), task.getException()); // Использовать Log.e для ошибок
                    String errorMessage = "Ошибка регистрации.";
                    Exception exception = task.getException();

                    if (exception instanceof FirebaseAuthWeakPasswordException) {
                        errorMessage = "Пароль слишком слабый (минимум 6 символов).";
                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Некорректный Email адрес.";
                    } else if (exception instanceof FirebaseAuthUserCollisionException) {
                        errorMessage = "Пользователь с таким Email уже существует.";
                    } else {
                        errorMessage = "Произошла неизвестная ошибка при регистрации.";
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        // Здесь можно обнулять binding, если вы его использовали (в этом фрагменте нет)
    }

    /**
     * Очистка ресурсов при откреплении фрагмента от активности
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach called");
        RegSuccessListener = null; // Избегаем утечек памяти
    }
}