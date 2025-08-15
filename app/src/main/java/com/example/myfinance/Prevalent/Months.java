package com.example.myfinance.Prevalent;

public class Months {
    private static String[] monthsRu = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    public static String[] getMonthsRu() {
        return monthsRu;
    }

    public static String getMonthByIndex(int index) {
        return monthsRu[index-1];
    }

    public static int getIndexByName(String month) {
        int index = 0;
        for (int i = 0; i < monthsRu.length; i++) {
            if (monthsRu[i].equals(month)) {
                index = i;
            }
        }
        return index;
    }
}