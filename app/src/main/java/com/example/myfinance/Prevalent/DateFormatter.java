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
    private static final SimpleDateFormat MONTH_FORMATTER =
            new SimpleDateFormat("MM", Locale.getDefault()); // Полное имя месяца

    public static String getMonthName(Date date) {
        return MONTH_FORMATTER.format(date);
    }

    public static int getMonthIndex(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH); // 0 - январь, 11 - декабрь
    }
}
