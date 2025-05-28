package com.example.myfinance.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class PatternFragment extends Fragment {
    private TextInputLayout reasonInputLayout, sumInputLayout;
    private TextInputEditText reasonEditText, sumEditText;
    private Button btnAddPattern;
    private double sumInDouble;
    private String reason;

    public final String FILENAME = "PATTERNS";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patterns_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reasonInputLayout = view.findViewById(R.id.reasonInputLayout);
        sumInputLayout = view.findViewById(R.id.sumInputLayout);
        reasonEditText = view.findViewById(R.id.reasonEditText);
        sumEditText = view.findViewById(R.id.sumEditText);
        btnAddPattern = view.findViewById(R.id.btnaddPattern);

        reasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (reasonEditText.getText().toString().trim().isEmpty()) { // Добавил .trim()
                    reasonInputLayout.setError("Причина не может быть пустой"); // Более информативная ошибка
                    reason = "";
                } else {
                    reasonInputLayout.setError(null);
                    reason = Objects.requireNonNull(reasonEditText.getText()).toString().trim(); // Добавил .trim()
                }
            }
        });
        sumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sumText = sumEditText.getText().toString().trim(); // Получаем текст и обрезаем пробелы
                if (sumText.isEmpty()) {
                    sumInputLayout.setError("Сумма не может быть пустой"); // Более информативная ошибка
                    sumInDouble = 0;
                } else {
                    try {
                        sumInDouble = Double.parseDouble(sumText);
                        sumInputLayout.setError(null);
                    } catch (NumberFormatException e) {
                        sumInputLayout.setError("Неверный формат суммы");
                        sumInDouble = 0;
                    }
                }
            }
        });

        btnAddPattern.setOnClickListener(v -> {
            if (reason.isEmpty() || sumInDouble <= 0) {
                Toast.makeText(requireContext(), "Пожалуйста, введите корректные данные для шаблона.", Toast.LENGTH_SHORT).show();
                return;
            }
            addingToInternalStorage(reason, sumInDouble);
        });
    }

    public void addingToInternalStorage(String categoryName, double sum) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reason", categoryName);
        editor.putFloat("sum", (float) sum);
        editor.apply();

        reasonEditText.setText("");
        sumEditText.setText("");
        reasonInputLayout.setError(null);
        sumInputLayout.setError(null);


    }

}