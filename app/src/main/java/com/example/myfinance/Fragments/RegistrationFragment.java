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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myfinance.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

    private TextView textViewForLogin;
    private EditText usernameEditText, emailEditText, surnameEditText, passwordEditText;

    private Button regButton;

    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;
    private TextView surnameErrorTextView;
    private TextView usernameErrorTextView;

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
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();

        textViewForLogin = view.findViewById(R.id.textViewForLogin);
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        regButton = view.findViewById(R.id.reg_button);
        emailErrorTextView = view.findViewById(R.id.email_error_text_view);
        passwordErrorTextView = view.findViewById(R.id.password_error_text_view);
        usernameEditText = view.findViewById(R.id.username_edit_text);
        surnameEditText = view.findViewById(R.id.surname_edit_text);
        usernameErrorTextView = view.findViewById(R.id.username_error_text_view);
        surnameErrorTextView = view.findViewById(R.id.surname_error_text_view);

        regButton.setEnabled(false);

        setupTextWatchers();

        regButton.setOnClickListener(v -> {
            if (isEmailValid && isPasswordValid && isUsernameValid && isSurnameValid) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();

                createUser(email, password, username, surname);
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

        surnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateSurname(editable.toString());
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
            usernameErrorTextView.setText("Имя пользователя не может быть пустым");
            usernameErrorTextView.setVisibility(View.VISIBLE);
        } else if (username.length() < 2) {
            isUsernameValid = false;
            usernameErrorTextView.setText("Имя пользователя должно быть больше 2х букв");
            usernameErrorTextView.setVisibility(View.VISIBLE);
        } else {
            usernameErrorTextView.setVisibility(View.GONE);
            isUsernameValid = true;
        }
    }

    /**
     * Проверка ввода пользователя (Фамилия)
     *
     * @param surname
     */
    private void validateSurname(String surname) {
        if (surname.trim().isEmpty()) {
            isSurnameValid = false;
            surnameErrorTextView.setText("Имя пользователя не может быть пустым");
            surnameErrorTextView.setVisibility(View.VISIBLE);
        } else if (surname.length() < 2) {
            isSurnameValid = false;
            surnameErrorTextView.setText("Фамилия пользователя должно быть больше 2х букв");
            surnameErrorTextView.setVisibility(View.VISIBLE);
        } else {
            surnameErrorTextView.setVisibility(View.GONE);
            isSurnameValid = true;
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
            emailErrorTextView.setText("Email не может быть пустым");
            emailErrorTextView.setVisibility(View.VISIBLE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false;
            emailErrorTextView.setText("Введите корректный Email адрес");
            emailErrorTextView.setVisibility(View.VISIBLE);
        } else {
            isEmailValid = true;
            emailErrorTextView.setVisibility(View.GONE);
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
        regButton.setEnabled(isEmailValid && isPasswordValid && isUsernameValid && isSurnameValid);
    }

    /**
     * Создание пользователя в Firebase
     * @param email
     * @param password
     * @param username
     * @param surname
     */
    private void createUser(String email, String password, String username, String surname) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(ContextCompat.getMainExecutor(requireContext()), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            if (RegSuccessListener != null)
                                RegSuccessListener.onRegSuccess();
                            saveToFirebase(email, username, surname);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Ошибка при регистрации", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    /**
     * Сохранение данных пользователя в Firebase
     *
     * @param email
     * @param name
     * @param surname
     */
    private void saveToFirebase(String email, String name, String surname) {
        DocumentReference userRef = fb.collection("users").document(email);
        Map<String, String> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("surname", surname);
        userRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error saving user data to Firebase: " + e.getMessage());
                Toast.makeText(getContext(), "Ошибка при сохранении данных пользователя", Toast.LENGTH_SHORT).show();
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