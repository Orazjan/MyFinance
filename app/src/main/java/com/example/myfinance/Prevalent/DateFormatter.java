package com.example.myfinance.Prevalent;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat FORMATTER =
            new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());

    // Форматтер для извлечения только названия месяца (на английском)
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat MONTH_NAME_FORMATTER =
            new SimpleDateFormat("MMMM", Locale.ENGLISH);

    public static String formatDate(Date date) {
        return FORMATTER.format(date);
    }

    public static Date parseDate(String dateString) {
        try {
            return FORMATTER.parse(dateString);
        } catch (ParseException e) {
            return new Date();
        }
    }

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat MONTH_NUMBER_FORMATTER =
            new SimpleDateFormat("MM", Locale.getDefault());

    public static String getMonthNumber(Date date) {
        return MONTH_NUMBER_FORMATTER.format(date);
    }

    public static int getMonthIndex(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static String getMonthRu(Date date) {
        return Months.getMonthsRu()[getMonthIndex(date)];
    }

    /**
     * Извлекает название месяца (на английском) из строки даты.
     * @param dateString Строка даты в формате "HH:mm dd.MM.yyyy".
     * @return Название месяца на английском.
     */
    public static String getMonthName(String dateString) {
        // Добавлена проверка на null, чтобы избежать NullPointerException
        if (dateString == null) {
            return null;
        }
        try {
            Date date = FORMATTER.parse(dateString);
            if (date != null) {
                return MONTH_NAME_FORMATTER.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
