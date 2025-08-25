package com.example.myfinance.Prevalent;

public class Months {

    private static final String[] MONTHS_RU = new String[]{"За всё время", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    private static final String[] MONTHS_EN = new String[]{"All Time", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static String[] getMonthsRu() {
        return MONTHS_RU;
    }

    public static String getMonthRu(int index) {
        if (index >= 0 && index < MONTHS_RU.length) {
            return MONTHS_RU[index];
        }
        return null;
    }

    public static String getMonthEn(int index) {
        if (index >= 0 && index < MONTHS_EN.length) {
            return MONTHS_EN[index];
        }
        return null;
    }
}
