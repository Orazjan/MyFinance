package com.example.myfinance;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.myfinance.Prevalent.StatusBarColorHelper;
import com.example.myfinance.Prevalent.TutorialController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
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
    private ImageView button_tutorial;
    private FloatingActionButton btnAddNewCheck; // Объявляем FloatingActionButton

    private boolean doubleBackToExitPressedOnce = false;
    private static final int ANALIZ_FRAGMENT_POSITION = 0;
    private static final int MAIN_FRAGMENT_POSITION = 1;
    private static final int PROFILE_FRAGMENT_POSITION = 2;
    private AddSettingToDataStoreManager appSettingsManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TutorialController tutorialController;

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
        button_tutorial = findViewById(R.id.button_tutorial);
        btnAddNewCheck = findViewById(R.id.btnAddNewCheck); // Находим FloatingActionButton

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

            // Удаляем сложную логику popBackStack из слушателя
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    // Обработка логики перехода в отдельном методе
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

        // Добавляем слушатель для ViewPager2, чтобы управлять видимостью FAB
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Показываем кнопку только на главной вкладке
                if (position == MAIN_FRAGMENT_POSITION) {
                    btnAddNewCheck.setVisibility(VISIBLE);
                } else {
                    btnAddNewCheck.setVisibility(INVISIBLE);
                }
            }
        });

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
                // Показываем FAB, если возвращаемся на главную вкладку
                if (viewPager.getCurrentItem() == MAIN_FRAGMENT_POSITION) {
                    btnAddNewCheck.setVisibility(VISIBLE);
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
                // Скрываем FAB, когда открывается любой дополнительный фрагмент
                btnAddNewCheck.setVisibility(INVISIBLE);
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

            // Инициализация TutorialController и добавление шагов
            initTutorialController();

            // При запуске убеждаемся, что FAB виден, так как мы на главной вкладке
            btnAddNewCheck.setVisibility(VISIBLE);
        });

        // Добавляем обработчик нажатия на FAB
        btnAddNewCheck.setOnClickListener(v -> {
            // Заглушка: здесь нужно будет открыть фрагмент для добавления нового чека
            Log.d(TAG, "FAB clicked. Opening AddNewCheckFragment.");
            openSecondaryFragment(new AddingNewFinance(), "AddingNewFinance");
        });
    }

    /**
     * Обрабатывает выбор вкладки, включая очистку Back Stack.
     *
     * @param position Позиция выбранной вкладки.
     */
    private void handleTabSelection(int position) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            // Очищаем Back Stack при любом выборе вкладки, если он не пуст
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.d(TAG, "Tab selection handled. Popped back stack.");
        }
    }

    /**
     * Инициализация контроллера обучения
     */
    private void initTutorialController() {
        tutorialController = new TutorialController(this);

        // Добавляем шаги обучения для главного активити
        View tutorialButton = findViewById(R.id.button_tutorial);
        if (tutorialButton != null) {
            tutorialController.addStep(
                    tutorialButton,
                    "Эта кнопка запускает обучение по работе с приложением",
                    "Обучение",
                    null,
                    false // В этом шаге нужен тултип, не только подсветка
            );
        }

        // Шаг: Подсветка главной вкладки для добавления финансов
        if (tabLayout != null && tabLayout.getTabAt(MAIN_FRAGMENT_POSITION) != null) {
            View mainTab = Objects.requireNonNull(tabLayout.getTabAt(MAIN_FRAGMENT_POSITION)).view;
            tutorialController.addStep(mainTab, "Здесь вы можете наблюдать за остатками и историей операций", "Добавление финансов", () -> {
                // В этом шаге не требуется дополнительный переход, так как мы уже находимся на главной вкладке.
            }, true);
        }

        // Пример: подсветка вкладки анализа
        if (tabLayout != null && tabLayout.getTabAt(ANALIZ_FRAGMENT_POSITION) != null) {
            View analizTab = tabLayout.getTabAt(ANALIZ_FRAGMENT_POSITION).view;
            tutorialController.addStep(analizTab, "В этом разделе вы можете анализировать свои финансы с помощью графиков и отчетов", "Анализ финансов", () -> {
                        // Просто переключаем страницу.
                        viewPager.setCurrentItem(ANALIZ_FRAGMENT_POSITION, true);
                    }, true // В этом шаге нужна только подсветка
            );
        }

        // Пример: подсветка вкладки профиля
        if (tabLayout != null && tabLayout.getTabAt(PROFILE_FRAGMENT_POSITION) != null) {
            View profileTab = tabLayout.getTabAt(PROFILE_FRAGMENT_POSITION).view;
            tutorialController.addStep(profileTab, "В профиле вы можете управлять своими настройками и учетной записью", "Управление профилем", () -> {
                        // Просто переключаем страницу.
                        viewPager.setCurrentItem(PROFILE_FRAGMENT_POSITION, true);
                    }, true // В этом шаге нужна только подсветка
            );
        }

        // Шаг: Подсветка кнопки добавления операции
        if (btnAddNewCheck != null) {
            tutorialController.addStep(
                    btnAddNewCheck,
                    "Нажмите на эту кнопку, чтобы добавить новую финансовую операцию — доход или расход.",
                    "Добавление операции",
                    null,
                    false
            );
        }

        // Запуск обучения по нажатию на кнопку
        if (tutorialButton != null) {
            button_tutorial.setOnClickListener(v -> {
                Log.d(TAG, "Starting tutorial.");
                tutorialController.startTutorial();
            });
        }
    }

    public TutorialController getTutorialController() {
        return tutorialController;
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

        // Скрываем FAB, когда открывается вторичный фрагмент
        btnAddNewCheck.setVisibility(View.GONE);

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
}
