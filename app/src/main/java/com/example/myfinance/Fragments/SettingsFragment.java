package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.AmountViewModel;
import com.example.myfinance.Models.CategoryViewModel;
import com.example.myfinance.Models.FinanceViewModel;
import com.example.myfinance.MyApplication;
import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.R;
import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.CategoryRepository;
import com.example.myfinance.data.FinanceRepository;
import com.example.myfinance.data.TotalAmount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    private AddSettingToDataStoreManager appSettingsManager;
    private AutoCompleteTextView currencySpinner;
    private AutoCompleteTextView themeSpinner;
    private List<String> currencyOptions;
    private List<String> themeOptions;
    private FirebaseAuth auth;
    private FirebaseFirestore fb;
    private CardView deleteAllPatterns, deleteAllFinances;
    private CategoryViewModel categoryViewModel;
    private FinanceViewModel finViewModel;
    private AmountViewModel amountViewModel;

    private static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        fb = FirebaseFirestore.getInstance();
        deleteAllFinances = view.findViewById(R.id.deleteAllFinances);
        deleteAllPatterns = view.findViewById(R.id.deleteAllPatterns);
        appSettingsManager = new AddSettingToDataStoreManager(requireContext());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Инициализация репозиториев
        MyApplication myApplication = (MyApplication) requireActivity().getApplication();
        CategoryRepository categoryRepository = myApplication.getCategoryRepository();
        FinanceRepository financeRepository = myApplication.getFinanceRepository();
        AmountRepository amountRepository = myApplication.getAmountRepository();

        // Инициализация ViewModel с использованием пользовательских фабрик
        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new CategoryViewModel.TaskViewModelFactory(requireActivity().getApplication())
        ).get(CategoryViewModel.class);

        finViewModel = new ViewModelProvider(
                requireActivity(),
                new FinanceViewModel.TaskViewModelFactory(financeRepository)
        ).get(FinanceViewModel.class);

        amountViewModel = new ViewModelProvider(
                requireActivity(),
                new AmountViewModel.TaskViewModelFactory(amountRepository)
        ).get(AmountViewModel.class);

        initUI(view);
        setupSpinnerData();
        setupAdapters();
        loadSettingsIntoSpinners();
        setupSpinnerListeners();

        deleteAllPatterns.setOnClickListener(v -> {
            deletePatterns();
        });

        deleteAllFinances.setOnClickListener(v -> {
            deleteFinances();
        });
    }

    /**
     * Удаляет все шаблоны из Firestore и локальной базы Room.
     */
    private void deletePatterns() {
        if (auth.getCurrentUser() != null) {
            String userEmail = auth.getCurrentUser().getEmail();
            assert userEmail != null;
            fb.collection("users").document(userEmail).collection("categories").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    WriteBatch batch = fb.batch();
                    for (DocumentSnapshot document : task.getResult()) {
                        batch.delete(document.getReference());
                    }
                    batch.commit().addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Все шаблоны успешно удалены", Toast.LENGTH_SHORT).show();
                        categoryViewModel.deleteAll();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.w("Delete", "Error getting documents", task.getException());
                }
            });
        }
    }

    /**
     * Удаляет все финансовые операции из Firestore и локальной базы Room.
     */
    private void deleteFinances() {
        if (auth.getCurrentUser() != null) {
            String userEmail = auth.getCurrentUser().getEmail();
            assert userEmail != null;
            fb.collection("users").document(userEmail).collection("finances").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    WriteBatch batch = fb.batch();
                    for (DocumentSnapshot document : task.getResult()) {
                        batch.delete(document.getReference());
                    }
                    batch.commit().addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Все финансы успешно удалены", Toast.LENGTH_SHORT).show();
                        finViewModel.deleteAllFinances();
                        amountViewModel.insert(new TotalAmount(0.0, 0.0));
                    }).addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Ошибка удаления", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.w("Delete", "Error getting documents", task.getException());
                }
            });
        }
    }

    /**
     * Обработчик события нажатия кнопки "Назад"
     */
    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            requireActivity().getSupportFragmentManager().popBackStack();
            this.setEnabled(false);
        }
    };

    /**
     * Инициализирует все элементы пользовательского интерфейса (UI) фрагмента.
     *
     * @param view Корневой View фрагмента.
     */
    private void initUI(@NonNull View view) {
        currencySpinner = view.findViewById(R.id.currencySpinner);
        themeSpinner = view.findViewById(R.id.themeSpinner);
    }

    /**
     * Подготавливает данные (списки опций) для AutoCompleteTextView.
     */
    private void setupSpinnerData() {
        currencyOptions = new ArrayList<>(Arrays.asList("СОМ", "TMT", "RUB", "USD", "EUR"));
        themeOptions = new ArrayList<>(Arrays.asList("Системная", "Тёмная", "Светлая"));
    }

    /**
     * Создает и устанавливает адаптеры для AutoCompleteTextView.
     */
    private void setupAdapters() {
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, currencyOptions);
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, themeOptions);

        currencySpinner.setAdapter(currencyAdapter);
        themeSpinner.setAdapter(themeAdapter);
    }

    /**
     * Загружает текущие сохраненные настройки из SharedPreferences
     * и устанавливает выбранные элементы в AutoCompleteTextView.
     */
    private void loadSettingsIntoSpinners() {
        String currentCurrency = appSettingsManager.getCurrencyType();
        int currencyPosition = currencyOptions.indexOf(currentCurrency);
        if (currencyPosition >= 0) {
            currencySpinner.setText(currencyOptions.get(currencyPosition), false);
        } else if (!currencyOptions.isEmpty()) {
            currencySpinner.setText(currencyOptions.get(0), false);
        }

        String currentThemeKey = appSettingsManager.getTheme();
        String currentThemeDisplayName = "";

        if ("system_default".equals(currentThemeKey)) {
            currentThemeDisplayName = "Системная";
        } else if ("dark".equals(currentThemeKey)) {
            currentThemeDisplayName = "Тёмная";
        } else if ("light".equals(currentThemeKey)) {
            currentThemeDisplayName = "Светлая";
        }

        int themePosition = themeOptions.indexOf(currentThemeDisplayName);
        if (themePosition >= 0) {
            themeSpinner.setText(themeOptions.get(themePosition), false);
        } else if (!themeOptions.isEmpty()) {
            themeSpinner.setText(themeOptions.get(0), false);
        }
    }

    /**
     * Применяет сохраненную тему из SharedPreferences
     *
     * @param themeKey
     */
    private void applySavedTheme(String themeKey) {
        switch (themeKey) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Log.d(TAG, "Applying Light Theme.");
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Log.d(TAG, "Applying Dark Theme.");
                break;
            case "system_default":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                Log.d(TAG, "Applying System Default Theme.");
                break;
        }
    }

    /**
     * Устанавливает слушателей выбора элементов для AutoCompleteTextView,
     * чтобы сохранять изменения в настройках.
     */
    private void setupSpinnerListeners() {
        currencySpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCurrency = currencyOptions.get(position);
            appSettingsManager.saveCurrencyType(selectedCurrency);
            Log.d(TAG, "Currency selected: " + selectedCurrency);
        });

        currencySpinner.setOnClickListener(v -> currencySpinner.showDropDown());

        themeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedThemeDisplayName = themeOptions.get(position);
            String themeKeyToSave = "";
            if ("Системная".equals(selectedThemeDisplayName)) {
                themeKeyToSave = "system_default";
            } else if ("Тёмная".equals(selectedThemeDisplayName)) {
                themeKeyToSave = "dark";
            } else if ("Светлая".equals(selectedThemeDisplayName)) {
                themeKeyToSave = "light";
            }

            appSettingsManager.saveTheme(themeKeyToSave);
            applySavedTheme(themeKeyToSave);
            Log.d(TAG, "Theme selected: " + selectedThemeDisplayName + " (key: " + themeKeyToSave + ")");
        });
        themeSpinner.setOnClickListener(v -> themeSpinner.showDropDown());
    }
}
