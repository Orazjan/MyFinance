package com.example.myfinance.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.MainActivity;
import com.example.myfinance.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Класс фрагмента профиля
 */
public class ProfileFragment extends Fragment {
    private TextView VERSIONOFAPP;
    private Button btnProfileChange, btnPattern, btnSettings, btnSync, btnEsc;
    private int clickCount = 0;
    private static final long RESET_CLICK_COUNT_DELAY = 1000;
    private FirebaseUser cuurentUser;

    @Override
    public void onStart() {
        super.onStart();
        updateUserProfileUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPattern = view.findViewById(R.id.btnPattern);
        btnProfileChange = view.findViewById(R.id.btnProfileChange);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnSync = view.findViewById(R.id.btnSync);
        btnEsc = view.findViewById(R.id.btnEsc);
        VERSIONOFAPP = view.findViewById(R.id.VERSIONOFAPP);

        VERSIONOFAPP.setText("VERSION 0.7");

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            if (btnProfileChange != null) {
                btnProfileChange.setOnClickListener(v -> {
                    mainActivity.openSecondaryFragment(new ProfileChangeFragment(), "ProfileChange");

                });
            }

            if (VERSIONOFAPP != null) {
                VERSIONOFAPP.setOnClickListener(v -> {
                    clickCount++;

                    if (clickCount == 3) {
                        mainActivity.openSecondaryFragment(new VersionInfoFragment(), "VersionInfo");

                        clickCount = 0;
                    } else {
                        new android.os.Handler().postDelayed(() -> {
                            if (clickCount < 3) {
                                clickCount = 0;
                            }
                        }, RESET_CLICK_COUNT_DELAY);
                    }
                });
            }

            if (btnSettings != null) {
                btnSettings.setOnClickListener(v -> {
                    mainActivity.openSecondaryFragment(new SettingsFragment(), "Settings");
                });
            }

            if (btnPattern != null) {
                btnPattern.setOnClickListener(v -> {
                    mainActivity.openSecondaryFragment(new PatternFragment(), "Pattern");
                });
            }
        } else {
            Log.e("ProfileFragment", "Родительская Activity не является MainActivity!");
        }

        btnSync.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Синхронизация", Toast.LENGTH_SHORT).show();
        });

        btnEsc.setOnClickListener(v -> {
            cuurentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (cuurentUser != null) {
                btnEsc.setEnabled(true);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            } else {
                btnEsc.setEnabled(false);
                Toast.makeText(getContext(), "Эта кнопка для выхода из учётной записи", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Обновление профиля
     */
    private void updateUserProfileUI() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("ProfileFragment", "User is logged in: " + currentUser.getEmail());
            btnEsc.setEnabled(true);
        } else {
            Log.d("ProfileFragment", "User is not logged in.");
            btnEsc.setEnabled(false);
        }
    }
}