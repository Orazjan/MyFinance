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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myfinance.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";

    private TextView textViewForLogin;
    private TextInputEditText usernameEditText, emailEditText, passwordEditText;
    private TextInputLayout usernameInputLayout, emailInputLayout, passwordInputLayout;
    private MaterialButton regButton;

    private boolean isUsernameValid = false;
    private boolean isEmailValid = false;
    private boolean isPasswordValid = false;

    private FirebaseAuth auth;
    FirebaseFirestore fb;

    private OnRegSuccessListener RegSuccessListener;

    public RegistrationFragment() {
    }

    /**
     * Интерфейс для коллбэка
     */
    public interface OnRegSuccessListener {
        void onRegSuccess();
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
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();

        // Инициализация View
        textViewForLogin = view.findViewById(R.id.textViewForLogin);
        usernameInputLayout = view.findViewById(R.id.usernameInputLayout);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        regButton = view.findViewById(R.id.reg_button);

        regButton.setEnabled(false);
        setupTextWatchers();

        // Кнопка регистрации
        regButton.setOnClickListener(v -> {
            validateUserName(usernameEditText.getText().toString());
            validateEmail(emailEditText.getText().toString());
            validatePassword(passwordEditText.getText().toString());

            if (isEmailValid && isPasswordValid && isUsernameValid) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                createUser(email, password, username);
            } else {
                Toast.makeText(getContext(), "Пожалуйста, исправьте ошибки ввода", Toast.LENGTH_SHORT).show();
            }
        });

        // Ссылка "Уже есть аккаунт? Войти"
        textViewForLogin.setOnClickListener(v -> {
            // Просто возвращаемся назад в стеке, так как LoginFragment лежит под нами
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                // Если вдруг стека нет (экран открылся первым), заменяем на Login
                getParentFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.fragment_container, new LoginFragment()).commit();
            }
        });
    }

    /**
     * Настройка слушателей для TextWatchers
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
                validateUserName(usernameEditText.getText().toString());
                validateEmail(emailEditText.getText().toString());
                validatePassword(passwordEditText.getText().toString());
                updateRegistrationButtonState();
            }
        };
        usernameEditText.addTextChangedListener(watcher);
        emailEditText.addTextChangedListener(watcher);
        passwordEditText.addTextChangedListener(watcher);
    }

    /**
     * Валидация имени пользователя
     * @param username
     */
    private void validateUserName(String username) {
        if (username.trim().isEmpty()) {
            isUsernameValid = false;
            usernameInputLayout.setError("Имя не может быть пустым");
        } else if (username.length() < 2) {
            isUsernameValid = false;
            usernameInputLayout.setError("Минимум 2 символа");
        } else {
            usernameInputLayout.setError(null);
            isUsernameValid = true;
        }
    }

    /**
     * Валидация Email
     * @param email
     */
    private void validateEmail(String email) {
        if (email.trim().isEmpty()) {
            isEmailValid = false;
            emailInputLayout.setError("Email обязателен");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false;
            emailInputLayout.setError("Некорректный Email");
        } else {
            isEmailValid = true;
            emailInputLayout.setError(null);
        }
    }

    /**
     * Валидация пароля
     * @param password
     */
    private void validatePassword(String password) {
        if (password.trim().isEmpty()) {
            isPasswordValid = false;
            passwordInputLayout.setError("Пароль обязателен");
        } else if (password.length() < 6) {
            isPasswordValid = false;
            passwordInputLayout.setError("Минимум 6 символов");
        } else {
            isPasswordValid = true;
            passwordInputLayout.setError(null);
        }
    }

    /**
     * Обновление состояния кнопки регистрации
     */
    private void updateRegistrationButtonState() {
        regButton.setEnabled(isEmailValid && isPasswordValid && isUsernameValid);
    }

    /**
     * Создание пользователя
     * @param email
     * @param password
     * @param username
     */
    private void createUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(ContextCompat.getMainExecutor(requireContext()), task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "createUser:success");
                saveToFirebase(email, username);
                if (RegSuccessListener != null) RegSuccessListener.onRegSuccess();
            } else {
                Log.w(TAG, "createUser:failure", task.getException());
                Toast.makeText(getContext(), "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Сохранение данных пользователя в Firebase
     * @param email
     * @param name
     */
    private void saveToFirebase(String email, String name) {
        DocumentReference userRef = fb.collection("users").document(email);
        Map<String, String> userData = new HashMap<>();
        userData.put("name", name);
        userRef.set(userData).addOnFailureListener(e -> Log.e(TAG, "Error saving user data: " + e.getMessage()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очистка ссылок
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RegSuccessListener = null;
    }
}