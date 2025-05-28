package com.example.myfinance;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myfinance.Adapters.PagerAdapter;
import com.example.myfinance.Prevalent.StatusBarColorHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnAnaliz, btnMain, btnProfile;
    private ProgressBar progressBar;
    private FrameLayout fragmentContainer;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int MAIN_FRAGMENT_POSITION = 1;
    private static final int PROFILE_FRAGMENT_POSITION = 2;

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
                            case 0:
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
        }

        btnAnaliz = findViewById(R.id.btnAnaliz);
        btnMain = findViewById(R.id.btnMain);
        btnProfile = findViewById(R.id.btnProfile);

        if (btnAnaliz == null) Log.e("MainActivity", "btnAnaliz is null!");
        if (btnMain == null) Log.e("MainActivity", "btnMain is null!");
        if (btnProfile == null) Log.e("MainActivity", "btnProfile is null!");

        setupBottomNavigationButtons();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (fragmentContainer != null) {
                    fragmentContainer.setVisibility(View.GONE);
                }
                if (viewPager != null) {
                    viewPager.setVisibility(VISIBLE);
                }
                if (tabLayout != null) {
                    tabLayout.setVisibility(VISIBLE);
                }
            }
        });

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
            updateBottomNavSelection(MAIN_FRAGMENT_POSITION);
        });
    }

    private void setupBottomNavigationButtons() {
        if (btnAnaliz != null) {
            btnAnaliz.setOnClickListener(v -> {
                fragmentContainer.setVisibility(View.GONE);
                viewPager.setVisibility(VISIBLE);
                Log.d("ButtonDebug", "btnAnaliz clicked, setting ViewPager item 0");
                viewPager.setCurrentItem(0, true);
            });
        }
        if (btnMain != null) {
            btnMain.setOnClickListener(v -> {
                Log.d("ButtonDebug", "btnMain clicked, setting ViewPager item MAIN_FRAGMENT_POSITION");
                fragmentContainer.setVisibility(View.GONE);
                viewPager.setVisibility(VISIBLE);
                viewPager.setCurrentItem(MAIN_FRAGMENT_POSITION, true);
            });
        }
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                fragmentContainer.setVisibility(View.GONE);
                Log.d("ButtonDebug", "btnProfile clicked, setting ViewPager item PROFILE_FRAGMENT_POSITION");
                viewPager.setVisibility(VISIBLE);
                viewPager.setCurrentItem(PROFILE_FRAGMENT_POSITION, true);
            });
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("ViewPagerCallback", "Page selected: " + position);
                updateBottomNavSelection(position);
            }
        });
    }

    private void updateBottomNavSelection(int selectedPosition) {
        if (btnAnaliz != null) btnAnaliz.setSelected(selectedPosition == 0);
        if (btnMain != null) btnMain.setSelected(selectedPosition == MAIN_FRAGMENT_POSITION);
        if (btnProfile != null)
            btnProfile.setSelected(selectedPosition == PROFILE_FRAGMENT_POSITION);
    }

    public void openSecondaryFragment(Fragment fragment, String backStackTag) {
        if (fragmentContainer == null) {
            Log.e("MainActivity", "fragment_container не найден. Невозможно открыть фрагмент.");
            Toast.makeText(this, "Ошибка: Контейнер для фрагментов не найден.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewPager != null) {
            viewPager.setVisibility(View.GONE);
            Log.d("VisibilityDebug", "ViewPager2 hidden.");
        }
        if (tabLayout != null) {
            tabLayout.setVisibility(View.GONE);
            Log.d("VisibilityDebug", "TabLayout hidden.");
        }
        fragmentContainer.setVisibility(VISIBLE);
        Log.d("VisibilityDebug", "fragment_container set to VISIBLE.");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(backStackTag)
                .commit();
        Log.d("FragmentTransaction", "Secondary fragment " + fragment.getClass().getSimpleName() + " committed.");
    }

    public void showProgressBar(boolean check) {
        if (progressBar != null) {
            if (check) {
                progressBar.setVisibility(VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}