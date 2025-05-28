package com.example.myfinance.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.SharedViewModel;
import com.example.myfinance.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddingNewFinance extends Fragment {
    private Spinner standart_variant;
    private Button currentSelectedButton, btnAdd;
    private Map<String, Double> standarts;
    private TextInputEditText sumEditText, reasonEditText;
    private TextInputLayout sumInputLayout, reasonInputLayout;
    private String selectedCategory;
    ArrayAdapter<String> adapter;
    SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adding_new_finance_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        standart_variant = view.findViewById(R.id.standart_variant);
        btnAdd = view.findViewById(R.id.btnAdd);
        sumEditText = view.findViewById(R.id.sumEditText);
        reasonEditText = view.findViewById(R.id.reasonEditText);
        sumInputLayout = view.findViewById(R.id.sumInputLayout);
        reasonInputLayout = view.findViewById(R.id.reasonInputLayout);

        loadObjectAndDisplay();

        standart_variant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString();
                switch (selectedCategory) {
                    case "Оплата за транспорт":
                        selectButton(btnAdd);
                        sumEditText.setText("20");
                        reasonEditText.setText(selectedCategory);
                        break;
                    case "Оплата оператора":
                        selectButton(btnAdd);
                        sumEditText.setText("200");
                        reasonEditText.setText(selectedCategory);
                        break;
                    case "Оплата за кофе":
                        selectButton(btnAdd);
                        sumEditText.setText("");
                        reasonEditText.setText(selectedCategory);
                        focusAndOpenKeyboard(sumEditText);
                        break;
                    case "Оплата за еду":
                        selectButton(btnAdd);
                        sumEditText.setText("");
                        reasonEditText.setText(selectedCategory);
                        focusAndOpenKeyboard(sumEditText);
                        break;
                    case "Другое":
                        selectButton(btnAdd);
                        sumEditText.setText("");
                        reasonEditText.setText("");
                        focusAndOpenKeyboard(sumEditText);
                        break;
                    default:
                        selectButton(btnAdd);
                        sumEditText.setText("");
                        reasonEditText.setText(selectedCategory);
                        focusAndOpenKeyboard(sumEditText);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sumEditText.setText("");
                reasonEditText.setText("Категория не выбрана");
            }
        });

        btnAdd.setOnClickListener(View -> {
            sendData();
            Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendData() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        String sumStr = sumEditText.getText().toString().trim();
        String reason = reasonEditText.getText().toString().trim();

        if (sumStr.isEmpty()) {
            sumInputLayout.setError("Введите сумму");
            return;
        } else {
            sumInputLayout.setError(null);
        }

        if (reason.isEmpty()) {
            reasonInputLayout.setError("Введите причину");
            return;
        } else {
            reasonInputLayout.setError(null);
        }

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(getContext(), "Пожалуйста, выберите категорию", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double sum = Double.parseDouble(sumStr);
            sharedViewModel.setSelectedCategory(selectedCategory);
            sharedViewModel.setSum(sum);
            Toast.makeText(getContext(), "Успешно!", Toast.LENGTH_SHORT).show();

            sumEditText.setText("");
            reasonEditText.setText("");
            standart_variant.setSelection(0);

            getParentFragmentManager().popBackStack();
        } catch (NumberFormatException e) {
            sumInputLayout.setError("Неверный формат суммы");
        }
    }

    private void selectButton(Button buttonToSelect) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setSelected(false);
        }
        buttonToSelect.setSelected(true);
        currentSelectedButton = buttonToSelect;
    }

    private void focusAndOpenKeyboard(TextView textView) {
        textView.requestFocus();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(sumEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
    }

    private void loadObjectAndDisplay() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("PATTERNS", Context.MODE_PRIVATE);
        // Получаем сохраненную категорию и сумму. Если их нет, используем значения по умолчанию.
        String categoryName = sharedPreferences.getString("reason", ""); // Ключ "reason" - это имя категории
        double sum = sharedPreferences.getFloat("sum", 0); // Ключ "sum" - это сумма

        // Инициализируем Map для стандартных категорий
        standarts = new HashMap<>();
        standarts.put("Другое", null); // "Другое" обычно без фиксированной суммы
        standarts.put("Оплата за транспорт", 20.0);
        standarts.put("Оплата оператора", 200.0);
        standarts.put("Оплата за кофе", 0.0); // Возможно, 0.0 или null, если нужно вводить

        // Добавляем загруженную категорию и сумму, если они не пустые.
        // Это может быть категория, добавленная пользователем.
        if (!categoryName.isEmpty()) {
            standarts.put(categoryName, sum);
        }
        // Создаем адаптер для Spinner, используя ключи (названия категорий) из Map
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>(standarts.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standart_variant.setAdapter(adapter);

        // Уведомляем адаптер об изменении данных (не обязательно, если данные статичны или загружены до установки)
        adapter.notifyDataSetChanged();
    }
}
