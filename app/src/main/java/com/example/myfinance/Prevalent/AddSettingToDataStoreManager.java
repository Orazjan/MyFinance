package com.example.myfinance.Prevalent;

import android.content.Context;
import android.content.SharedPreferences;

public class AddSettingToDataStoreManager {
    private static final String PREF_NAME = "MySettings";
    private static final String KEY_THEME = "app_theme";
    private static final String KEY_CURRENCY_TYPE = "currency_type";

    private SharedPreferences sharedPreferences;

    public AddSettingToDataStoreManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Сохраняет выбранную тему приложения.
     *
     * @param theme Строковое значение темы (например, "light", "dark", "system_default").
     */
    public void saveTheme(String theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_THEME, theme);
        editor.apply();
        // commit()
    }

    /**
     * Сохраняет выбранный тип валюты.
     *
     * @param currencyType Строковое значение типа валюты (например, "USD", "EUR", "RUB").
     */
    public void saveCurrencyType(String currencyType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CURRENCY_TYPE, currencyType);
        editor.apply();
    }

    /**
     * Получает текущую тему приложения.
     *
     * @return Строка темы. По умолчанию "system_default", если не установлена.
     */
    public String getTheme() {
        return sharedPreferences.getString(KEY_THEME, "Системная");
    }

    /**
     * Получает текущий тип валюты.
     *
     * @return Строка типа валюты. По умолчанию "USD", если не установлен.
     */
    public String getCurrencyType() {
        return sharedPreferences.getString(KEY_CURRENCY_TYPE, "USD");
    }
}