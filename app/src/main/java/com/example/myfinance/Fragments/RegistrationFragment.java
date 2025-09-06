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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myfinance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Фрагмент для регистрации пользователя
 */
public class RegistrationFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";

    private MaterialButton textViewForLogin;
    private TextInputEditText usernameEditText, emailEditText, passwordEditText;
    private TextInputLayout usernameInputLayout, emailInputLayout, passwordInputLayout;
    private MaterialButton regButton;

    private boolean isUsernameValid = false;
    private boolean isEmailValid = false;
    private boolean isSurnameValid = false;
    private boolean isPasswordValid = false;

    private FirebaseAuth auth;
    FirebaseFirestore fb;

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
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();

        textViewForLogin = view.findViewById(R.id.textViewForLogin);
        usernameInputLayout = view.findViewById(R.id.usernameInputLayout);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        regButton = view.findViewById(R.id.reg_button); // MaterialButton

        regButton.setEnabled(false);

        setupTextWatchers();

        regButton.setOnClickListener(v -> {
            validateUserName(usernameEditText.getText().toString());
            validateEmail(emailEditText.getText().toString());
            validatePassword(passwordEditText.getText().toString());

            if (isEmailValid && isPasswordValid && isUsernameValid && isSurnameValid) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();

                createUser(email, password, username);
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateUserName(editable.toString());
                updateRegistrationButtonState();
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int int_after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int int_after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEmail(editable.toString());
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
     * Проверка ввода пользователя (Имя)
     *
     * @param username
     */
    private void validateUserName(String username) {
        if (username.trim().isEmpty()) {
            isUsernameValid = false;
            usernameInputLayout.setError("Имя пользователя не может быть пустым");
        } else if (username.length() < 2) {
            isUsernameValid = false;
            usernameInputLayout.setError("Имя пользователя должно быть больше 2х букв");
        } else {
            usernameInputLayout.setError(null); // Очищаем ошибку
            isUsernameValid = true;
        }
    }


    /**
     * Проверка ввода пользователя (Email)
     *
     * @param email Введенный текст email
     */
    @SuppressLint("SetTextI18n")
    private void validateEmail(String email) {
        if (email.trim().isEmpty()) {
            isEmailValid = false;
            emailInputLayout.setError("Email не может быть пустым");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false;
            emailInputLayout.setError("Введите корректный Email адрес");
        } else {
            isEmailValid = true;
            emailInputLayout.setError(null); // Очищаем ошибку
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
     * Обновление состояния кнопки регистрации (активна, если все поля валидны)
     */
    private void updateRegistrationButtonState() {
        regButton.setEnabled(isEmailValid && isPasswordValid && isUsernameValid && isSurnameValid);
    }

    /**
     * Создание пользователя в Firebase
     * @param email
     * @param password
     * @param username
     */
    private void createUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(ContextCompat.getMainExecutor(requireContext()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            if (RegSuccessListener != null)
                                RegSuccessListener.onRegSuccess();
                            saveToFirebase(email, username);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Ошибка при регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // Добавлено сообщение об ошибке
                        }
                    }

                });

    }

    /**
     * Сохранение данных пользователя в Firebase
     *
     * @param email
     * @param name
     */
    private void saveToFirebase(String email, String name) {
        DocumentReference userRef = fb.collection("users").document(email);
        Map<String, String> userData = new HashMap<>();
        userData.put("name", name);
        userRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User data saved to Firebase successfully.");
                } else {
                    Log.e(TAG, "Error saving user data to Firebase: " + task.getException().getMessage());
                    Toast.makeText(getContext(), "Ошибка при сохранении данных пользователя: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error saving user data to Firebase (failure listener): " + e.getMessage());
                Toast.makeText(getContext(), "Ошибка при сохранении данных пользователя: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Очистка ресурсов при уничтожении View фрагмента
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Очищаем ссылки на TextInputLayout и TextInputEditText
        usernameInputLayout = null;
        usernameEditText = null;
        emailInputLayout = null;
        emailEditText = null;
        passwordInputLayout = null;
        passwordEditText = null;
        textViewForLogin = null;
        regButton = null;
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
