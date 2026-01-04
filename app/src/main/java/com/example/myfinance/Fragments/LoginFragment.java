package com.example.myfinance.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.myfinance.databinding.FragmentLoginBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private FragmentLoginBinding binding;
    private FirebaseAuth auth;

    // UI элементы
    private TextInputLayout usernameInputLayout, passwordInputLayout;
    private TextInputEditText usernameEditText, passwordEditText;
    private MaterialButton loginButton;
    private TextView tvRegisterLink, tvForgotPassword;

    private boolean isUsernameValid = false;
    private boolean isPasswordValid = false;

    // Слушатель событий фрагмента
    private LoginFragmentListener loginFragmentListener;

    /**
     * Интерфейс для общения с Activity
     */
    public interface LoginFragmentListener {
        void onLoginSuccess();

        void onSwitchToRegister();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragmentListener) {
            loginFragmentListener = (LoginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();

        // Инициализация View через Binding
        usernameInputLayout = binding.usernameInputLayout;
        usernameEditText = binding.usernameEditText;
        passwordInputLayout = binding.passwordInputLayout;
        passwordEditText = binding.passwordEditText;
        loginButton = binding.loginButton;

        // Новые элементы из XML
        tvRegisterLink = binding.tvRegisterLink;
        tvForgotPassword = binding.tvForgotPassword;

        loginButton.setEnabled(false);
        setupTextWatchers();

        // Логика кнопки Вход
        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            validateUsername(email);
            validatePassword(password);

            if (isUsernameValid && isPasswordValid) {
                signInUser(email, password);
            } else {
                Toast.makeText(getContext(), "Пожалуйста, проверьте введенные данные", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegisterLink.setOnClickListener(v -> {
            if (loginFragmentListener != null) {
                loginFragmentListener.onSwitchToRegister();
            }
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    /**
     * Авторизация пользователя
     * @param email
     * @param password
     */
    private void signInUser(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                Log.d(TAG, "SignIn: Success. User: " + (user != null ? user.getEmail() : "null"));
                if (loginFragmentListener != null) {
                    loginFragmentListener.onLoginSuccess();
                }
            } else {
                Log.e(TAG, "SignIn: Failed", task.getException());
                handleAuthError(task.getException());
            }
        });
    }

    /**
     * Обработка ошибок авторизации
     *
     * @param exception
     */
    private void handleAuthError(Exception exception) {
        String errorMessage = "Ошибка входа.";
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            errorMessage = "Неверный пароль или Email.";
            passwordInputLayout.setError(errorMessage);
            passwordInputLayout.requestFocus();
        } else if (exception instanceof FirebaseAuthInvalidUserException) {
            errorMessage = "Пользователь не найден.";
            usernameInputLayout.setError(errorMessage);
            usernameInputLayout.requestFocus();
        } else {
            errorMessage = exception != null ? exception.getMessage() : "Неизвестная ошибка";
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Диалог для сброса пароля
     */
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Восстановление пароля");

        // Создаем поле ввода программно
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("Введите ваш Email");
        input.setBackgroundResource(android.R.drawable.edit_text);

        // Создаем контейнер для отступов
        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int marginInDp = 24;
        int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);

        params.leftMargin = marginInPx;
        params.rightMargin = marginInPx;

        input.setLayoutParams(params);
        container.addView(input);

        builder.setView(container);

        // Если пользователь уже ввел email в поле логина, подставим его
        if (usernameEditText.getText() != null) {
            input.setText(usernameEditText.getText().toString());
        }

        builder.setPositiveButton("Сбросить", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Введите корректный Email", Toast.LENGTH_SHORT).show();
                return;
            }
            sendPasswordResetEmail(email);
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Отправка email для сброса пароля
     *
     * @param email
     */
    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Инструкции отправлены на " + email, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Настройка слушателей для текстовых полей
     */
    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateUsername(usernameEditText.getText().toString());
                validatePassword(passwordEditText.getText().toString());
                updateLoginButtonState();
            }
        };

        usernameEditText.addTextChangedListener(watcher);
        passwordEditText.addTextChangedListener(watcher);
    }

    /**
     * Валидация Email
     * @param username
     */
    private void validateUsername(String username) {
        if (username.trim().isEmpty()) {
            isUsernameValid = false;
            usernameInputLayout.setError("Email не может быть пустым");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            isUsernameValid = false;
            usernameInputLayout.setError("Некорректный Email");
        } else {
            isUsernameValid = true;
            usernameInputLayout.setError(null);
        }
    }

    /**
     * Валидация пароля
     * @param password
     */
    private void validatePassword(String password) {
        if (password.trim().isEmpty()) {
            isPasswordValid = false;
            passwordInputLayout.setError("Введите пароль");
        } else if (password.length() < 6) {
            isPasswordValid = false;
            passwordInputLayout.setError("Минимум 6 символов");
        } else {
            isPasswordValid = true;
            passwordInputLayout.setError(null);
        }
    }

    /**
     * Обновление состояния кнопки Вход
     */
    private void updateLoginButtonState() {
        loginButton.setEnabled(isUsernameValid && isPasswordValid);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}