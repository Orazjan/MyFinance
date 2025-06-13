package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
    private Spinner currencySpinner;
    private Spinner themeSpinner;
    private List<String> currencyOptions;
    private List<String> themeOptions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        appSettingsManager = new AddSettingToDataStoreManager(requireContext());
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        initUI(view);
        setupSpinnerData();
        setupSpinners();
        loadSettingsIntoSpinners();
        setupSpinnerListeners();
    }

    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            applySavedTheme(appSettingsManager.getTheme());
            requireActivity().getSupportFragmentManager().popBackStack();
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
     * Подготавливает данные (списки опций) для Spinner'ов.
     */
    private void setupSpinnerData() {
        currencyOptions = new ArrayList<>(Arrays.asList("TMT", "RUB", "USD", "EUR"));
        themeOptions = new ArrayList<>(Arrays.asList("Системная", "Тёмная", "Светлая"));
    }

    /**
     * Создает и устанавливает адаптеры для Spinner'ов.
     */
    private void setupSpinners() {
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, currencyOptions);
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, themeOptions);

        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySpinner.setAdapter(currencyAdapter);
        themeSpinner.setAdapter(themeAdapter);
    }

    /**
     * Загружает текущие сохраненные настройки из SharedPreferences
     * и устанавливает выбранные элементы в Spinner'ах.
     */
    private void loadSettingsIntoSpinners() {
        String currentCurrency = appSettingsManager.getCurrencyType();
        int currencyPosition = currencyOptions.indexOf(currentCurrency);
        if (currencyPosition >= 0) {
            currencySpinner.setSelection(currencyPosition);
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
            themeSpinner.setSelection(themePosition);
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
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Принудительно светлая
                Log.d("ThemeApply", "Applying Light Theme.");
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Принудительно темная
                Log.d("ThemeApply", "Applying Dark Theme.");
                break;
            case "system_default":
            default: // По умолчанию или при неизвестном ключе
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); // Следовать системной настройке
                Log.d("ThemeApply", "Applying System Default Theme.");
                break;
        }
    }
    /**
     * Устанавливает слушателей выбора элементов для Spinner'ов,
     * чтобы сохранять изменения в настройках.
     */
    private void setupSpinnerListeners() {
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedCurrency = currencyOptions.get(position);
                appSettingsManager.saveCurrencyType(selectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}