package com.example.myfinance.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.Objects;

public class ProfileChangeFragment extends Fragment {
    private TextView emailEditText, nameEditText, famEditText, regDataTextView;
    private FirebaseUser Fuser;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_change_fragment, container, false);
    }

    /**
     * При изменении состояния активности
     */
    @Override
    public void onResume() {
        super.onResume();
        if (Fuser != null) {
            long creationTimestamp = Objects.requireNonNull(Fuser.getMetadata()).getCreationTimestamp();
            java.util.Date creationDate = new java.util.Date(creationTimestamp);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault());
            String formattedDate = sdf.format(creationDate);

            emailEditText.setText(Fuser.getEmail());
            regDataTextView.setText(formattedDate);
            emailEditText.setEnabled(false);
            regDataTextView.setEnabled(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUserProfileUI();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = view.findViewById(R.id.emailTextView);
        nameEditText = view.findViewById(R.id.nameTextView);
        famEditText = view.findViewById(R.id.famTextView);
        regDataTextView = view.findViewById(R.id.regDataTextView);
        Fuser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        emailEditText.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });
    }

    private void updateUserProfileUI() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            long creationTimestamp = Objects.requireNonNull(currentUser.getMetadata()).getCreationTimestamp();
            Date creationDate = new Date(creationTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(creationDate);

            emailEditText.setText(userEmail != null ? userEmail : "Email не указан");
            regDataTextView.setText(formattedDate);

            emailEditText.setEnabled(false);
            regDataTextView.setEnabled(false);

        }
    }
}
