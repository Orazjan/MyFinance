package com.example.myfinance;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myfinance.Adapters.PagerAdapter;
import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.Prevalent.StatusBarColorHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

/**
 * Главное активити приложения
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private FrameLayout fragmentContainer;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int ANALIZ_FRAGMENT_POSITION = 0;
    private static final int MAIN_FRAGMENT_POSITION = 1;
    private static final int PROFILE_FRAGMENT_POSITION = 2;
    private AddSettingToDataStoreManager appSettingsManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Вызывается при запуске активити.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    /**
     * Вызывается при создании активити.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarColorHelper.setStatusBarColorFromPrimaryVariant(this);
        appSettingsManager = new AddSettingToDataStoreManager(getApplicationContext());
        applySavedTheme(appSettingsManager.getTheme());

        progressBar = findViewById(R.id.progressBar);
        viewPager = findViewById(R.id.viewPager);
        fragmentContainer = findViewById(R.id.fragment_container);
        tabLayout = findViewById(R.id.tab_layout);

        if (progressBar != null) {
            progressBar.setVisibility(VISIBLE);
        }

        mAuth = FirebaseAuth.getInstance();

        // Добавляем слушатель состояния аутентификации и обновляем UI
        mAuthListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (progressBar != null) {
                progressBar.setVisibility(INVISIBLE);
            }

            if (currentUser != null) {
                progressBar.setVisibility(INVISIBLE);
            } else {
                progressBar.setVisibility(INVISIBLE);
            }
        };

        // Проверяем, что ViewPager не равен null, чтобы избежать NullPointerException
        if (viewPager == null) {
            Toast.makeText(this, "Application error: ViewPager not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (tabLayout == null) {
            Log.w(TAG, "Warning: TabLayout (R.id.tab_layout) not found!");
        }

        if (fragmentContainer == null) {
            Toast.makeText(this, "Critical error: Fragment container not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        PagerAdapter adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);

        if (tabLayout != null) {
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case ANALIZ_FRAGMENT_POSITION:
                                tab.setText("Анализ");
                                break;
                            case MAIN_FRAGMENT_POSITION:
                                tab.setText("Главное");
                                break;
                            case PROFILE_FRAGMENT_POSITION:
                                tab.setText("Профиль");
                                break;
                            default:
                                tab.setText("Page " + (position + 1));
                        }
                    }).attach();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                }
            });
        }

        // Добавляем слушатель состояния BackStack
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

            if (backStackCount == 0) {
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.GONE);
                    fragmentContainer.setClickable(false);
                    fragmentContainer.setFocusable(false);
                }
                if (viewPager != null) {
                    viewPager.setVisibility(VISIBLE);
                    viewPager.setClickable(true);
                    viewPager.setFocusable(true);
                    viewPager.bringToFront();
                }
            } else {
                if (viewPager != null) {
                    viewPager.setVisibility(View.GONE);
                    viewPager.setClickable(false);
                    viewPager.setFocusable(false);
                }
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.VISIBLE);
                    fragmentContainer.setClickable(true);
                    fragmentContainer.setFocusable(true);
                }
            }
        });

        // Добавляем обработчик OnBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Log.d(TAG, "Back button pressed. BackStackEntryCount: " + fragmentManager.getBackStackEntryCount());

                if (fragmentManager.getBackStackEntryCount() > 0) {
                    // Если есть фрагменты в BackStack, извлекаем их
                    fragmentManager.popBackStack();
                    doubleBackToExitPressedOnce = false;
                    return;
                }

                if (viewPager.getCurrentItem() != MAIN_FRAGMENT_POSITION) {
                    viewPager.setCurrentItem(MAIN_FRAGMENT_POSITION, true);
                    doubleBackToExitPressedOnce = false;
                } else {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                    } else {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(MainActivity.this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, TimeUnit.SECONDS.toMillis(2));
                    }
                }
            }
        });

        viewPager.post(() -> {
            viewPager.setCurrentItem(MAIN_FRAGMENT_POSITION, false);
            if (fragmentContainer != null)
                fragmentContainer.setVisibility(View.GONE);
            if (viewPager != null) viewPager.setVisibility(VISIBLE);
        });
    }

    /**
     * Вызывается при остановке активити.
     * Открепляем слушатель состояния аутентификации, чтобы избежать утечек памяти.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Отрисовывает встроенные фрагменты в контейнере, используя show/hide.
     *
     * @param fragment     Фрагмент, который нужно показать.
     * @param backStackTag Тег для Back Stack.
     */
    public void openSecondaryFragment(Fragment fragment, String backStackTag) {
        if (fragmentContainer == null) {
            Log.e(TAG, "fragment_container не найден. Невозможно открыть фрагмент.");
            Toast.makeText(this, "Ошибка: Контейнер для фрагментов не найден.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewPager != null) {
            viewPager.setVisibility(View.GONE);
        }
        fragmentContainer.setVisibility(View.VISIBLE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentByTag(backStackTag);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Скрываем все видимые фрагменты в контейнере, которые не являются целевым фрагментом
        for (Fragment f : fragmentManager.getFragments()) {
            if (f != null && f.isVisible() && f.getId() == R.id.fragment_container && !f.equals(existingFragment)) {
                transaction.hide(f);
                Log.d(TAG, "Hiding existing fragment: " + f.getClass().getSimpleName());
            }
        }

        if (existingFragment == null) {
            // Если фрагмента нет, добавляем его
            transaction.add(R.id.fragment_container, fragment, backStackTag);
            Log.d(TAG, "Adding new secondary fragment: " + fragment.getClass().getSimpleName());
        } else {
            // Если фрагмент уже существует, просто показываем его
            transaction.show(existingFragment);
            Log.d(TAG, "Showing existing secondary fragment: " + existingFragment.getClass().getSimpleName());
        }

        // Добавляем транзакцию в Back Stack.
        transaction.addToBackStack(backStackTag);
        transaction.commit();
        Log.d(TAG, "Secondary fragment transaction committed.");
    }

    /**
     * Применяет сохраненную тему из хранилища.
     *
     * @param themeKey
     */
    private void applySavedTheme(String themeKey) {
        switch (themeKey) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Log.d("ThemeApply", "Applying Light Theme.");
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Log.d("ThemeApply", "Applying Dark Theme.");
                break;
            case "system_default":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                Log.d("ThemeApply", "Applying System Default Theme.");
                break;
        }
    }
}
