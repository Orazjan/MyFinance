package com.example.myfinance;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myfinance.Fragments.AnalizFragment;
import com.example.myfinance.Fragments.MainFragment;
import com.example.myfinance.Fragments.ProfileFragment;
import com.example.myfinance.Prevalent.StatusBarColorHelper;

public class MainActivity extends AppCompatActivity {

    private Button btnAnaliz, btnMain, btnProfile;
    private Button currentSelectedButton;
    private OnBackPressedCallback onBackPressedCallback;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarColorHelper.setStatusBarColorFromPrimaryVariant(this);

        btnAnaliz = findViewById(R.id.btnAnaliz);
        btnMain = findViewById(R.id.btnMain);
        btnProfile = findViewById(R.id.btnProfile);

        btnAnaliz.setOnClickListener(v -> selectFragment(new AnalizFragment(), btnAnaliz, true));
        btnMain.setOnClickListener(v -> selectFragment(new MainFragment(), btnMain, false));
        btnProfile.setOnClickListener(v -> selectFragment(new ProfileFragment(), btnProfile, true));

        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    doubleBackToExitPressedOnce = false;
                } else {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                    } else {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(MainActivity.this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

                    }
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        getSupportFragmentManager().addOnBackStackChangedListener(this::onBackStackChanged);

        if (savedInstanceState == null) {
            selectFragment(new MainFragment(), btnMain, false);
        }
    }

    /**
     * Загружает указанный фрагмент в контейнер и выделяет соответствующую кнопку.
     *
     * @param fragment       Фрагмент, который нужно загрузить.
     * @param selectedButton Кнопка, которую нужно выделить.
     * @param addToBackStack Флаг, указывающий, нужно ли добавить фрагмент в Back Stack.
     */
    private void selectFragment(Fragment fragment, Button selectedButton, boolean addToBackStack) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
        fragmentTransaction.commit();
        selectButton(selectedButton);
    }

    /**
     * Метод для выделения кнопки. Выделяет переданную кнопку и сбрасывает выделение с предыдущей.
     * Вынесен отдельно, чтобы избежать рекурсии и упростить логику.
     *
     * @param buttonToSelect Кнопка, которую нужно выделить.
     */
    private void selectButton(Button buttonToSelect) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setSelected(false);
        }
        buttonToSelect.setSelected(true);
        currentSelectedButton = buttonToSelect;
    }

    private void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof MainFragment) {
            selectButton(btnMain);
        } else if (currentFragment instanceof AnalizFragment) {
            selectButton(btnAnaliz);
        } else if (currentFragment instanceof ProfileFragment) {
            selectButton(btnProfile);
        }
    }

}