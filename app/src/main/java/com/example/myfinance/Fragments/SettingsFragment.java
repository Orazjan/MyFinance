package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    private AddSettingToDataStoreManager appSettingsManager;
    private AutoCompleteTextView currencySpinner;
    private AutoCompleteTextView themeSpinner;
    private List<String> currencyOptions;
    private List<String> themeOptions;

    private static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appSettingsManager = new AddSettingToDataStoreManager(requireContext());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        initUI(view);
        setupSpinnerData();
        setupAdapters();
        loadSettingsIntoSpinners();
        setupSpinnerListeners();
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
        // ИЗМЕНЕНО: findViewById для AutoCompleteTextView
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
    private void setupAdapters() { // Переименован метод
        // ИЗМЕНЕНО: Использование android.R.layout.simple_dropdown_item_1line для AutoCompleteTextView
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
            currencySpinner.setText(currencyOptions.get(0), false); // Устанавливаем первый элемент по умолчанию
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
