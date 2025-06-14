package com.example.myfinance;

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
import androidx.viewpager2.widget.ViewPager2;

import com.example.myfinance.Adapters.PagerAdapter;
import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.Prevalent.StatusBarColorHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private FrameLayout fragmentContainer;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int ANALIZ_FRAGMENT_POSITION = 0;
    private static final int MAIN_FRAGMENT_POSITION = 1;
    private static final int PROFILE_FRAGMENT_POSITION = 2;
    private AddSettingToDataStoreManager appSettingsManager;

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

        if (viewPager == null) {
            Log.e("MainActivity", "Error: ViewPager2 (R.id.viewPager) not found!");
            Toast.makeText(this, "Application error: ViewPager not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (tabLayout == null) {
            Log.w("MainActivity", "Warning: TabLayout (R.id.tab_layout) not found!");
        }
        if (fragmentContainer == null) {
            Log.e("MainActivity", "Error: FrameLayout (R.id.fragment_container) not found! Check your XML layout.");
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
                        Log.d("TabSelection", "Closed secondary fragments on tab selection.");
                    }
                    Log.d("TabSelection", "Selected tab: " + tab.getText() + ", position: " + tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        Log.d("TabReselection", "Closed secondary fragments on tab reselection.");
                    }
                }
            });
        }
        /**
         * Обработчик событий изменения состояния фрагментов.
         */
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
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
/**
 * Обработчик события нажатия кнопки "Назад" для выхода из приложения.
 */
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();

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

        viewPager.post(() -> {
            viewPager.setCurrentItem(MAIN_FRAGMENT_POSITION, false);
            if (fragmentContainer != null)
                fragmentContainer.setVisibility(View.GONE);
            if (viewPager != null) viewPager.setVisibility(VISIBLE);
        });
    }

    /**
     * Отрисовывает встроенные фрагменты в контейнере.
     *
     * @param fragment
     * @param backStackTag
     */
    public void openSecondaryFragment(Fragment fragment, String backStackTag) {
        if (fragmentContainer == null) {
            Log.e("MainActivity", "fragment_container не найден. Невозможно открыть фрагмент.");
            Toast.makeText(this, "Ошибка: Контейнер для фрагментов не найден.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewPager != null) {
            viewPager.setVisibility(View.GONE);
        }

        fragmentContainer.setVisibility(VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(backStackTag)
                .commit();
        Log.d("FragmentTransaction", "Secondary fragment " + fragment.getClass().getSimpleName() + " committed.");
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