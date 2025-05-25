package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.Models.ShowFinancesViewModel;
import com.example.myfinance.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AddingNewFinance extends Fragment {
    private Spinner standart_variant;
    private Button currentSelectedButton, btnAdd;
    private List<String> standarts;
    private TextInputEditText sumEditText, reasonEditText;
    private TextInputLayout sumInputLayout, reasonInputLayout;
    private String selectedCategory;
    private ShowFinancesViewModel SFM;

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
        SFM = new ViewModelProvider(requireActivity()).get(ShowFinancesViewModel.class);


        standarts = new ArrayList<>();
        standarts.add("Оплата за транспорт");
        standarts.add("Оплата оператора");
        standarts.add("Оплата за кофе");
        standarts.add("Оплата за еду");
        standarts.add("Оплата за продукты");
        standarts.add("Другое");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, standarts);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standart_variant.setAdapter(adapter);

        standart_variant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString();
                if (!selectedCategory.isEmpty()) {
                    selectButton(btnAdd);
                    sumEditText.setText("20");
                    reasonEditText.setText(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = null;
            }
        });

        btnAdd.setOnClickListener(View -> {
            sendData();
            Toast.makeText(requireContext(), "Добавлено", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendData() {
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
            int sum = Integer.parseInt(sumStr);
            List<ShowFinances> currentFinances = SFM.getFinancesList().getValue();
            int newId;

            if (currentFinances != null && !currentFinances.isEmpty()) {
                newId = currentFinances.size();
            } else {
                newId = 0;
            }

            ShowFinances newFinance = new ShowFinances(newId, sum, reason); // Используем сгенерированный уникальный ID

            SFM.addFinance(newFinance);

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
}
