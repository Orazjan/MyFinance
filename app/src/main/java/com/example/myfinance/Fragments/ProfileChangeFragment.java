package com.example.myfinance.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.myfinance.LoginActivity;
import com.example.myfinance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Фрагмент для изменения профиля пользователя.
 */
public class ProfileChangeFragment extends Fragment {
    private static final String TAG = "ProfileChangeFragment";

    private TextInputEditText emailEditText, nameEditText, famEditText, regDataEditText;
    private MaterialButton btnSave;
    private MaterialButton btnForAutentification;

    private FirebaseAuth auth;
    private FirebaseFirestore fb;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_change_fragment, container, false);
    }

    /**
     * Вызывается после создания View.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        famEditText = view.findViewById(R.id.famEditText);
        regDataEditText = view.findViewById(R.id.regDataEditText);
        btnSave = view.findViewById(R.id.btnForSave);
        btnForAutentification = view.findViewById(R.id.btnForAutentification);

        auth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();

        authStateListener = firebaseAuth -> updateUserProfileUI();

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        } else {
            Log.e(TAG, "btnSave is null, cannot set OnClickListener.");
        }

        // Проверяем, что поля ввода не null перед установкой слушателей
        if (nameEditText != null) {
            nameEditText.setOnClickListener(v -> {
                if ("Не авторизован".equals(nameEditText.getText().toString())) {
                    Log.d(TAG, "Click on nameEditText ignored: Not authorized.");
                    return; // Ничего не делаем, если не авторизован
                }
                showDialogToChangeNameSurname(nameEditText);
            });
        }
        if (famEditText != null) {
            famEditText.setOnClickListener(v -> {
                if ("Не авторизован".equals(famEditText.getText().toString())) {
                    Log.d(TAG, "Click on famEditText ignored: Not authorized.");
                    return; // Ничего не делаем, если не авторизован
                }
                showDialogToChangeNameSurname(famEditText);
            });
        }
        if (emailEditText != null) {
            emailEditText.setOnClickListener(v -> {
                // Если пользователь авторизован, показываем Toast
                if (auth.getCurrentUser() != null) {
                    Toast.makeText(getContext(), "Это ваш Email. Для изменения потребуется аутентификация.", Toast.LENGTH_SHORT).show();
                } else {
                    // ИЗМЕНЕНО: Если пользователь не авторизован, показываем Toast и не переходим на LoginActivity
                    Toast.makeText(getContext(), "Вы не авторизованы. Нажмите кнопку 'Зарегистрироваться' для входа.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Click on emailEditText ignored: User not authenticated.");
                    return; // Ничего не делаем
                }
            });
        }
        if (regDataEditText != null) {
            regDataEditText.setOnClickListener(v -> {
                if ("Не авторизован".equals(regDataEditText.getText().toString())) {
                    Log.d(TAG, "Click on regDataEditText ignored: Not authorized.");
                    return; // Ничего не делаем, если не авторизован
                }
                Toast.makeText(getContext(), "Дата регистрации не редактируется напрямую.", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnForAutentification != null) {
            btnForAutentification.setOnClickListener(v -> {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            });
        } else {
            Log.e(TAG, "btnForAutentification is null, cannot set OnClickListener.");
        }
    }

    /**
     * Отображение диалогового окна для изменения имени или фамилии.
     *
     * @param nameSurnameEditTextToUpdate TextInputEditText, который нужно обновить.
     */
    private void showDialogToChangeNameSurname(TextInputEditText nameSurnameEditTextToUpdate) {
        fb = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_name_surname, null);

        TextView dialogTitleTextView = view.findViewById(R.id.NameTextView);
        EditText nameChangeEditText = view.findViewById(R.id.name_edit_text);
        ImageView imgClose = view.findViewById(R.id.imgClose);

        if (nameSurnameEditTextToUpdate != null) {
            String currentText = Objects.requireNonNull(nameSurnameEditTextToUpdate.getText()).toString();
            nameChangeEditText.setText(currentText);

            if (nameSurnameEditTextToUpdate.getId() == R.id.nameEditText) {
                dialogTitleTextView.setText("Изменить имя");
                nameChangeEditText.setHint("Введите имя");
            } else if (nameSurnameEditTextToUpdate.getId() == R.id.famEditText) {
                dialogTitleTextView.setText("Изменение фамилии");
                nameChangeEditText.setHint("Введите фамилию");
            } else {
                dialogTitleTextView.setText("Изменить данные");
                nameChangeEditText.setHint("Введите новое значение");
            }
        } else {
            dialogTitleTextView.setText("Изменить данные");
            nameChangeEditText.setHint("Введите новое значение");
            Log.w("Dialog", "TextInputEditText для обновления данных был null.");
        }

        builder.setView(view).setPositiveButton("Сохранить", (dialog, id) -> {
            String newValue = nameChangeEditText.getText().toString().trim();
            if (!newValue.isEmpty()) {
                String fieldToUpdate;
                if (nameSurnameEditTextToUpdate.getId() == R.id.nameEditText) {
                    fieldToUpdate = "name";
                } else if (nameSurnameEditTextToUpdate.getId() == R.id.famEditText) {
                    fieldToUpdate = "surname";
                } else {
                    Log.w("Dialog", "Не удалось определить поле для обновления.");
                    return;
                }
                updateUserDataInFirebase(fieldToUpdate, newValue);
                nameSurnameEditTextToUpdate.setText(newValue);
            } else {
                Toast.makeText(requireContext(), "Поле не может быть пустым.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Отмена", (dialog, id) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
        imgClose.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    /**
     * Обновление данных пользователя в Firebase.
     *
     * @param fieldName
     * @param newValue
     */
    private void updateUserDataInFirebase(String fieldName, String newValue) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String userEmail = currentUser.getEmail();
            DocumentReference userRef = fb.collection("users").document(userEmail);

            Map<String, Object> updates = new HashMap<>();
            updates.put(fieldName, newValue);

            userRef.set(updates, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Поле " + fieldName + " успешно обновлено/создано.");
                Toast.makeText(requireContext(), fieldName + " успешно обновлено!", Toast.LENGTH_SHORT).show();
                updateUserProfileUI();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Ошибка обновления " + fieldName + ": " + e.getMessage(), e);
                Toast.makeText(requireContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(requireContext(), "Пользователь не авторизован.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Попытка обновить данные Firebase, когда пользователь не авторизован.");
        }
    }

    /**
     * Обновление UI профиля пользователя
     */
    private void updateUserProfileUI() {
        FirebaseUser currentUser = auth.getCurrentUser();
        Log.d(TAG, "updateUserProfileUI: Current FirebaseUser is " + (currentUser != null ? currentUser.getEmail() : "null (user not logged in)"));

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            long creationTimestamp = Objects.requireNonNull(currentUser.getMetadata()).getCreationTimestamp();
            Date creationDate = new Date(creationTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(creationDate);

            if (emailEditText != null)
                emailEditText.setText(userEmail != null ? userEmail : "Нажмите чтобы войти");
            if (regDataEditText != null) regDataEditText.setText(formattedDate);

            assert userEmail != null;
            fb.collection("users").document(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String surname = documentSnapshot.getString("surname");
                        if (nameEditText != null) nameEditText.setText(name);
                        if (famEditText != null) famEditText.setText(surname);
                    } else {
                        Log.d(TAG, "Документ не найден");
                        Toast.makeText(getContext(), "Документ не найден", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Ошибка при загрузке данных пользователя: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    if (nameEditText != null) nameEditText.setText("Ошибка загрузки");
                    if (famEditText != null) famEditText.setText("Ошибка загрузки");
                }
            });

        } else {
            if (emailEditText != null) emailEditText.setText("Не авторизован");
            if (regDataEditText != null) regDataEditText.setText("Не авторизован");
            if (nameEditText != null) nameEditText.setText("Не авторизован");
            if (famEditText != null) famEditText.setText("Не авторизован");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
        updateUserProfileUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserProfileUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Устанавливаем ссылки на null, чтобы избежать утечек памяти
        emailEditText = null;
        nameEditText = null;
        famEditText = null;
        regDataEditText = null;
        btnSave = null;
        btnForAutentification = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
