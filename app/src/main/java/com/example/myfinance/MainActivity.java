/**
 *
 * Atanyazov Oraz 2024
 *
 */
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
import com.example.myfinance.Fragments.AddingNewFinance;
import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.Prevalent.AppTutorialManager;
import com.example.myfinance.Prevalent.StatusBarColorHelper;
import com.example.myfinance.Prevalent.TutorialController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private FrameLayout fragmentContainer;
    private FloatingActionButton btnAddNewCheck;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int ANALIZ_FRAGMENT_POSITION = 0;
    private static final int MAIN_FRAGMENT_POSITION = 1;
    private static final int PROFILE_FRAGMENT_POSITION = 2;
    private AddSettingToDataStoreManager appSettingsManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TutorialController tutorialController;
    private AppTutorialManager appTutorialManager;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

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
        btnAddNewCheck = findViewById(R.id.btnAddNewCheck);

        if (progressBar != null) {
            progressBar.setVisibility(VISIBLE);
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (progressBar != null) {
                progressBar.setVisibility(INVISIBLE);
            }
        };

        if (viewPager == null) {
            Toast.makeText(this, "Application error: ViewPager not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (fragmentContainer == null) {
            Toast.makeText(this, "Critical error: Fragment container not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Настройка адаптера ViewPager
        PagerAdapter adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Настройка TabLayout (Нижнее меню)
        if (tabLayout != null) {
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case ANALIZ_FRAGMENT_POSITION:
                                tab.setText("Анализ");
                                tab.setIcon(android.R.drawable.ic_menu_sort_by_size);
                                break;
                            case MAIN_FRAGMENT_POSITION:
                                tab.setText("Главное");
                                tab.setIcon(android.R.drawable.ic_menu_rotate);
                                break;
                            case PROFILE_FRAGMENT_POSITION:
                                tab.setText("Профиль");
                                tab.setIcon(android.R.drawable.ic_menu_myplaces);
                                break;
                            default:
                                tab.setText("Page " + (position + 1));
                        }
                    }).attach();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    handleTabSelection(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }

        // Логика видимости кнопки добавления (FAB)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == MAIN_FRAGMENT_POSITION) {
                    btnAddNewCheck.show();
                } else {
                    btnAddNewCheck.hide();
                }
            }
        });

        // Слушатель стека фрагментов (скрывает ViewPager, если открыт вторичный фрагмент)
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
                if (viewPager.getCurrentItem() == MAIN_FRAGMENT_POSITION) {
                    btnAddNewCheck.show();
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
                btnAddNewCheck.hide();
            }
        });

        // Обработка кнопки "Назад"
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Log.d(TAG, "Back button pressed. BackStackEntryCount: " + fragmentManager.getBackStackEntryCount());

                if (fragmentManager.getBackStackEntryCount() > 0) {
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

        // Начальное состояние
        viewPager.post(() -> {
            viewPager.setCurrentItem(MAIN_FRAGMENT_POSITION, false);
            if (fragmentContainer != null)
                fragmentContainer.setVisibility(View.GONE);
            if (viewPager != null) viewPager.setVisibility(VISIBLE);

            btnAddNewCheck.setVisibility(VISIBLE);
        });

        // Туториал
        tutorialController = new TutorialController(this);
        appTutorialManager = new AppTutorialManager(this, tutorialController);
        appTutorialManager.setupMainActivityTutorial(this);

        if (tabLayout != null) {
            tabLayout.post(() -> {
                // Сначала переключаем на главный экран
                viewPager.setCurrentItem(MAIN_FRAGMENT_POSITION, false);

                // Скрываем/показываем контейнеры
                if (fragmentContainer != null) fragmentContainer.setVisibility(View.GONE);
                if (viewPager != null) viewPager.setVisibility(VISIBLE);
                btnAddNewCheck.show();

                // И только теперь настраиваем туториал (когда табы точно есть)
                appTutorialManager.setupMainActivityTutorial(MainActivity.this);
            });
        }

        btnAddNewCheck.setOnClickListener(v -> {
            openSecondaryFragment(new AddingNewFinance(), "AddingNewFinance");
        });

    }

    private void handleTabSelection(int position) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.d(TAG, "Tab selection handled. Popped back stack.");
        }
    }

    public AppTutorialManager getAppTutorialManager() {
        return appTutorialManager;
    }

    public TutorialController getTutorialController() {
        return tutorialController;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void openSecondaryFragment(Fragment fragment, String backStackTag) {
        if (fragmentContainer == null) {
            Log.e(TAG, "fragment_container not found. Cannot open fragment.");
            Toast.makeText(this, "Error: Fragment container not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewPager != null) {
            viewPager.setVisibility(View.GONE);
        }
        fragmentContainer.setVisibility(View.VISIBLE);

        btnAddNewCheck.hide();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment existingFragment = fragmentManager.findFragmentByTag(backStackTag);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment f : fragmentManager.getFragments()) {
            if (f != null && f.isVisible() && f.getId() == R.id.fragment_container && !f.equals(existingFragment)) {
                transaction.hide(f);
            }
        }

        if (existingFragment == null) {
            transaction.add(R.id.fragment_container, fragment, backStackTag);
        } else {
            transaction.show(existingFragment);
        }

        transaction.addToBackStack(backStackTag);
        transaction.commit();
    }

    private void applySavedTheme(String themeKey) {
        switch (themeKey) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "system_default":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Публичный метод для переключения вкладок из кода (например, для туториала).
     *
     * @param position Индекс вкладки (0 - Анализ, 1 - Главная, 2 - Профиль)
     */
    public void selectTab(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position, true); // true для плавной анимации
        }
    }
}