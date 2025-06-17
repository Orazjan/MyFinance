package com.example.myfinance.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.LoginActivity;
import com.example.myfinance.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Фрагмент для изменения профиля пользователя.
 */
public class ProfileChangeFragment extends Fragment {
    private static final String TAG = "ProfileChangeFragment";

    private TextView emailTextView, nameTextView, famTextView, regDataTextView;

    private FirebaseAuth auth;
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
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUserProfileUI();
            }
        };

        emailTextView.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                Toast.makeText(getContext(), "Это ваш Email. Для изменения потребуется переаутентификация.", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        });

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

    /**
     * Обновление UI профиля пользователя
     */
    private void updateUserProfileUI() {
        FirebaseUser currentUser = auth.getCurrentUser();
        Log.d(TAG, "updateUserProfileUI: Current FirebaseUser is " + (currentUser != null ? currentUser.getEmail() : "null (user not logged in)"));

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            long creationTimestamp = currentUser.getMetadata() != null ? currentUser.getMetadata().getCreationTimestamp() : 0;
            Date creationDate = new Date(creationTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(creationDate);

            emailTextView.setText(userEmail != null ? userEmail : "Email не указан");
            regDataTextView.setText(formattedDate);

            emailTextView.setEnabled(false);
            regDataTextView.setEnabled(false);
            if (nameTextView != null) nameTextView.setEnabled(false);
            if (famTextView != null) famTextView.setEnabled(false);


        } else {
            emailTextView.setText("Не авторизован");
            regDataTextView.setText("Не доступно");
            if (nameTextView != null) nameTextView.setText("Не авторизован");
            if (famTextView != null) famTextView.setText("Не авторизован");
        }
    }
}