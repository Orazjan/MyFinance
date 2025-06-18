package com.example.myfinance.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.myfinance.LoginActivity;
import com.example.myfinance.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * Фрагмент для изменения профиля пользователя.
 */
public class ProfileChangeFragment extends Fragment {
    private static final String TAG = "ProfileChangeFragment";

    private TextView emailTextView, nameTextView, famTextView, regDataTextView;

    private FirebaseAuth auth;
    private FirebaseFirestore fb;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_change_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailTextView = view.findViewById(R.id.emailTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        famTextView = view.findViewById(R.id.famTextView);
        regDataTextView = view.findViewById(R.id.regDataTextView);

        auth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUserProfileUI();
            }
        };

        nameTextView.setOnClickListener(v -> {
            showDialogToChangeNameSurname(nameTextView);
        });
        famTextView.setOnClickListener(v -> {
            showDialogToChangeNameSurname(famTextView);
        });
        emailTextView.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                Toast.makeText(getContext(), "Это ваш Email. Для изменения потребуется переаутентификация.", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });
    }

    /**
     * Отображение диалогового окна для изменения имени или фамилии.
     *
     * @param nameSurnameTextViewToUpdate
     */
    private void showDialogToChangeNameSurname(TextView nameSurnameTextViewToUpdate) {
        fb = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_name_surname, null);

        TextView dialogTitleTextView = view.findViewById(R.id.NameTextView);
        EditText nameChangeEditText = view.findViewById(R.id.name_edit_text);

        if (nameSurnameTextViewToUpdate != null) {
            String currentText = nameSurnameTextViewToUpdate.getText().toString();
            nameChangeEditText.setText(currentText);

            if (nameSurnameTextViewToUpdate.getId() == R.id.nameTextView) {
                dialogTitleTextView.setText("Изменить имя");
                nameChangeEditText.setHint("Введите имя");
            } else if (nameSurnameTextViewToUpdate.getId() == R.id.famTextView) {
                dialogTitleTextView.setText("Изменение фамилии");
                nameChangeEditText.setHint("Введите фамилию");
            } else {
                dialogTitleTextView.setText("Изменить данные");
                nameChangeEditText.setHint("Введите новое значение");
            }
        } else {
            dialogTitleTextView.setText("Изменить данные");
            nameChangeEditText.setHint("Введите новое значение");
            Log.w("Dialog", "TextView для обновления данных был null.");
        }

        builder.setView(view).setTitle("Редактирование").setPositiveButton("Сохранить", (dialog, id) -> {
            String newValue = nameChangeEditText.getText().toString().trim();
            if (!newValue.isEmpty()) {
                String fieldToUpdate;
                if (nameSurnameTextViewToUpdate.getId() == R.id.nameTextView) {
                    fieldToUpdate = "name";
                } else if (nameSurnameTextViewToUpdate.getId() == R.id.famTextView) {
                    fieldToUpdate = "surname";
                } else {
                    Log.w("Dialog", "Не удалось определить поле для обновления.");
                    return;
                }
                updateUserDataInFirebase(fieldToUpdate, newValue);
                nameSurnameTextViewToUpdate.setText(newValue);
            } else {
                Toast.makeText(requireContext(), "Поле не может быть пустым.", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Отмена", (dialog, id) -> {
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
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

            assert userEmail != null;
            fb.collection("users").document(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String surname = documentSnapshot.getString("surname");
                        if (nameTextView != null) nameTextView.setText(name);
                        if (famTextView != null) famTextView.setText(surname);
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

                    nameTextView.setText("Ошибка загрузки");
                    famTextView.setText("Ошибка загрузки");
                }
            });

            long creationTimestamp = currentUser.getMetadata() != null ? currentUser.getMetadata().getCreationTimestamp() : 0;
            Date creationDate = new Date(creationTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(creationDate);

            emailTextView.setText(userEmail != null ? userEmail : "Нажмите чтобы войти");
            regDataTextView.setHint(formattedDate);

            emailTextView.setEnabled(true);
            regDataTextView.setEnabled(false);

        } else {
            emailTextView.setText("Не авторизован");
            if (nameTextView != null) nameTextView.setText("Не авторизован");
            if (famTextView != null) famTextView.setText("Не авторизован");
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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