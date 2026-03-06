package com.example.myfinance.prevalent;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static String fromDate(Date date) {
        return DateFormatter.formatDate(date);
    }

    @TypeConverter
    public static Date toDate(String dateString) {
        return DateFormatter.parseDate(dateString);
    }
}