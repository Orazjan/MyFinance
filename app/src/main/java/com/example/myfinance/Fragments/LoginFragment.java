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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Фрагмент для входа пользователя в систему.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";


    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;

    private MaterialButton loginButton;
    private FragmentLoginBinding binding;

    private TextInputLayout usernameInputLayout;
    private TextInputLayout passwordInputLayout;

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
            throw new RuntimeException(context.toString() + " must implement OnLoginSuccessListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();

        usernameInputLayout = binding.usernameInputLayout;
        usernameEditText = binding.usernameEditText;
        passwordInputLayout = binding.passwordInputLayout;
        passwordEditText = binding.passwordEditText;
        loginButton = binding.loginButton;

        loginButton.setEnabled(false);

        setupTextWatchers();

        loginButton.setOnClickListener(v -> {
            // Убеждаемся, что валидация запускается перед попыткой входа
            validateUsername(usernameEditText.getText().toString());
            validatePassword(passwordEditText.getText().toString());

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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    Log.d(TAG, "SignIn: Пользователь успешно вошел! Email: " + (user != null ? user.getEmail() : "NULL_USER_AFTER_SUCCESS"));
                    if (loginSuccessListener != null) {
                        loginSuccessListener.onLoginSuccess();
                    }
                } else {
                    Log.e(TAG, "Sign-in failed: " + task.getException().getMessage(), task.getException());

                    String errorMessage = "Произошла неизвестная ошибка при входе.";
                    Exception exception = task.getException();

                    if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMessage = "Неверный Email или пароль.";
                        // Устанавливаем ошибку на соответствующее поле
                        usernameInputLayout.setError(errorMessage);
                        passwordInputLayout.setError(null); // Очищаем, если была
                    } else if (exception instanceof FirebaseAuthInvalidUserException) {
                        errorMessage = "Пользователь с таким Email не зарегистрирован.";
                        // Устанавливаем ошибку на поле email
                        usernameInputLayout.setError(errorMessage);
                        passwordInputLayout.setError(null); // Очищаем, если была
                    } else {
                        usernameInputLayout.setError(errorMessage);
                        passwordInputLayout.setError(null);
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Sign-in failed: " + errorMessage, exception);
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
    @SuppressLint("SetTextI18n")
    private void validateUsername(String username) {
        if (username.trim().isEmpty()) {
            isUsernameValid = false;
            usernameInputLayout.setError("Email не может быть пустым");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            isUsernameValid = false;
            usernameInputLayout.setError("Введите корректный Email адрес");
        } else {
            isUsernameValid = true;
            usernameInputLayout.setError(null); // Очищаем ошибку
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
            passwordInputLayout.setError("Пароль не может быть пустым");
        } else if (password.length() < 6) {
            isPasswordValid = false;
            passwordInputLayout.setError("Пароль должен быть не менее 6 символов");
        } else {
            isPasswordValid = true;
            passwordInputLayout.setError(null); // Очищаем ошибку
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
        binding = null;
        usernameInputLayout = null;
        usernameEditText = null;
        passwordInputLayout = null;
        passwordEditText = null;
        loginButton = null;
    }

    /**
     * Очистка ресурсов при откреплении фрагмента от активности
     */
    @Override
    public void onDetach() {
        super.onDetach();
        loginSuccessListener = null;
    }
}
