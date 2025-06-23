package com.example.myfinance.Prevalent;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class DateLabelFormatter extends ValueFormatter {
    private final List<String> labels;

    public DateLabelFormatter(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index >= 0 && index < labels.size()) {
            String dateString = labels.get(index);
            if (dateString != null && dateString.length() == 10 && dateString.contains("-")) {
                String[] parts = dateString.split("-");
                if (parts.length == 3) {
                    return parts[2] + "." + parts[1];
                }
            }
            return dateString;
        }
        return "";
    }
}
