package com.example.myfinance.Fragments;

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

import com.example.myfinance.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment"; // Правильное объявление TAG

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private FragmentLoginBinding binding;

    private TextView usernameErrorTextView;
    private TextView passwordErrorTextView;

    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;

    private FirebaseAuth auth;

    /**
     * Объявляем поле для коллбэка
     */
    private OnLoginSuccessListener loginSuccessListener;

    /**
     * Интерфейс для коллбэка
     * Убран метод onRegSuccess(), так как этот фрагмент больше не занимается регистрацией
     */
    public interface OnLoginSuccessListener {
        void onLoginSuccess();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Вызывается при прикреплении фрагмента к активности
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginSuccessListener) {
            loginSuccessListener = (OnLoginSuccessListener) context;
        } else {
            // Если Activity не реализует OnLoginSuccessListener, это ошибка
            throw new RuntimeException(context.toString() + " must implement OnLoginSuccessListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        auth = FirebaseAuth.getInstance(); // Инициализация FirebaseAuth

        usernameEditText = binding.usernameEditText;
        passwordEditText = binding.passwordEditText;
        loginButton = binding.loginButton;
        usernameErrorTextView = binding.usernameErrorTextView;
        passwordErrorTextView = binding.passwordErrorTextView;

        loginButton.setEnabled(false);

        setupTextWatchers();

        loginButton.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked.");
            if (isUsernameValid && isPasswordValid) {
                signInUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            } else {
                Toast.makeText(getContext(), "Пожалуйста, исправьте ошибки ввода", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Вход пользователя
     *
     * @param email    Email пользователя
     * @param password Пароль пользователя
     */
    private void signInUser(String email, String password) {
        Log.d(TAG, "Attempting to sign in user: " + email);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(getContext(), "Вход выполнен! Добро пожаловать, " + user.getEmail(), Toast.LENGTH_LONG).show();
                    if (loginSuccessListener != null) {
                        loginSuccessListener.onLoginSuccess(); // Вызов коллбэка об успехе входа
                    }
                    Log.d(TAG, "Sign-in successful! User: " + user.getEmail());
                } else {
                    Log.e(TAG, "Sign-in failed: " + task.getException().getMessage(), task.getException()); // Использовать Log.e для ошибок
                    String errorMessage = "Ошибка входа.";
                    Exception exception = task.getException();

                    if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Неверный Email или пароль.";
                    } else if (exception instanceof FirebaseAuthInvalidUserException) {
                        errorMessage = "Пользователь с таким Email не зарегистрирован.";
                    } else {
                        errorMessage = "Произошла неизвестная ошибка при входе.";
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Настройка слушателей для EditText для проверки ввода
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
                updateLoginButtonState();
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
                updateLoginButtonState();
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
     * Обновление состояния кнопки входа (активна, если оба поля валидны)
     */
    private void updateLoginButtonState() {
        loginButton.setEnabled(isUsernameValid && isPasswordValid);
    }

    /**
     * Очистка ресурсов binding при уничтожении View фрагмента
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
        binding = null;
    }

    /**
     * Очистка ресурсов при откреплении фрагмента от активности
     */
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach called");
        loginSuccessListener = null;
    }
}