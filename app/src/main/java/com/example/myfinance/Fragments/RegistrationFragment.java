package com.example.myfinance.Fragments;

import android.annotation.SuppressLint;
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

/**
 * Фрагмент для регистрации пользователя
 */
public class RegistrationFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";

    private TextView textViewForLogin;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button regButton;

    private TextView usernameErrorTextView;
    private TextView passwordErrorTextView;

    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;
    private FirebaseAuth auth;

    private OnRegSuccessListener RegSuccessListener;

    public RegistrationFragment() {

    }

    /**
     * Вызывается при прикреплении фрагмента к активности
     *
     * @param context
     */
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
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    /**
     * Этот метод вызывается после создания View фрагмента.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        textViewForLogin = view.findViewById(R.id.textViewForLogin);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        regButton = view.findViewById(R.id.reg_button);
        usernameErrorTextView = view.findViewById(R.id.username_error_text_view);
        passwordErrorTextView = view.findViewById(R.id.password_error_text_view);

        regButton.setEnabled(false);

        setupTextWatchers();

        regButton.setOnClickListener(v -> {
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

    /**
     * Интерфейс для коллбэка
     */
    public interface OnRegSuccessListener {
        void onRegSuccess();
    }

    /**
     * Настройка слушателей для текстовых полей
     */
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
                updateRegistrationButtonState();
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
                updateRegistrationButtonState();
            }
        });
    }

    /**
     * Проверка ввода пользователя (Email)
     *
     * @param username Введенный текст email
     */
    @SuppressLint("SetTextI18n")
    private void validateUsername(String username) {
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
        } else if (password.length() < 6) {
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
    private void updateRegistrationButtonState() {
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
                    Log.d(TAG, "Registration: Пользователь успешно создан! Email: " + (user != null ? user.getEmail() : "NULL_USER_AFTER_REG_SUCCESS"));
                    if (RegSuccessListener != null) {
                        RegSuccessListener.onRegSuccess();
                    }
                } else {
                    Log.e(TAG, "Registration failed: " + task.getException().getMessage(), task.getException());

                    String errorMessage = "Произошла неизвестная ошибка при регистрации.";
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthWeakPasswordException) {
                        errorMessage = "Пароль слишком слабый (минимум 6 символов).";
                    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Некорректный Email адрес.";
                    } else if (exception instanceof FirebaseAuthUserCollisionException) {
                        errorMessage = "Пользователь с таким Email уже существует.";
                    }
                    Log.e(TAG, "Registration failed: " + errorMessage, exception);
                }
            }
        });
    }

    /**
     * Очистка ресурсов при уничтожении View фрагмента
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Очистка ресурсов при откреплении фрагмента от активности
     */
    @Override
    public void onDetach() {
        super.onDetach();
        RegSuccessListener = null;
    }
}