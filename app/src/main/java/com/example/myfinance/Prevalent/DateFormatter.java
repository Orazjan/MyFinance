package com.example.myfinance.Prevalent;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
