package com.example.myfinance.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Adapters.ShowFinancesAdapter;
import com.example.myfinance.Models.SharedViewModel;
import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private TextView sumTextView; // Изменено с 'sum' для ясности
    private ImageButton changeSumButton; // Изменено с 'changeSum' для ясности
    private FloatingActionButton btnAddNewCheck;
    private ListView mainCheck; // Используем это имя для ListView
    private List<ShowFinances> financeList;
    private ShowFinancesAdapter financeAdapter;
    private SharedViewModel sharedViewModel;

    private String pendingCategory;
    private Double pendingSum;

    public MainFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация элементов UI
        sumTextView = view.findViewById(R.id.sum); // Инициализация TextView
        changeSumButton = view.findViewById(R.id.changeSum); // Инициализация ImageButton
        btnAddNewCheck = view.findViewById(R.id.btnAddNewCheck); // Инициализация FloatingActionButton
        mainCheck = view.findViewById(R.id.mainCheck); // Инициализация ListView с правильным именем

        // Инициализация списка финансовых записей и адаптера только один раз
        if (financeList == null) {
            financeList = new ArrayList<>();
        }
        if (financeAdapter == null) {
            financeAdapter = new ShowFinancesAdapter(requireContext(), financeList);
            mainCheck.setAdapter(financeAdapter); // Устанавливаем адаптер здесь, один раз
        }

        // Получаем экземпляр SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Наблюдаем за изменениями категории и суммы.
        // Оба наблюдателя вызывают addFinanceEntryIfReady(), который проверит,
        // доступны ли оба значения.
        sharedViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), s -> {
            pendingCategory = s;
            Log.d("MainFragment", "Category received: " + s);
            addFinanceEntryIfReady(); // <--- Здесь вызывается метод, который ждет оба значения
        });

        sharedViewModel.getSum().observe(getViewLifecycleOwner(), s -> {
            pendingSum = s;
            Log.d("MainFragment", "Sum received: " + s);
            addFinanceEntryIfReady(); // <--- Здесь вызывается метод, который ждет оба значения
        });

        // Слушатели для кнопок
        changeSumButton.setOnClickListener(v -> { // Используем v вместо View для лямбда-выражений
            showAlertDialogForAddingSum();
        });

        btnAddNewCheck.setOnClickListener(v -> { // Используем v вместо View для лямбда-выражений
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.viewPager, new AddingNewFinance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Слушатель для нажатия на элемент списка
        mainCheck.setOnItemClickListener((parent, v, position, id) -> { // Используем 'mainCheck'
            ShowFinances clickedItem = (ShowFinances) parent.getItemAtPosition(position);
            Toast.makeText(requireContext(), "Нажата запись: " + clickedItem.getName() + " - " + clickedItem.getSum(), Toast.LENGTH_SHORT).show();
        });

        // Обновляем общую сумму при первом запуске
        updateTotalSum();
    }

    private void updateTotalSum() {
        double currentTotal = 0.0;
        try {
            String sumText = sumTextView.getText().toString(); // Используем sumTextView
            if (!sumText.isEmpty()) {
                currentTotal = Double.parseDouble(sumText);
            }
        } catch (NumberFormatException e) {
            Log.e("MainFragment", "Ошибка парсинга начальной суммы: " + e.getMessage());
            currentTotal = 0.0;
        }

        // Логика для подсчета общей суммы (предполагая, что это общие расходы)
        // Если sumTextView - это баланс, то логика должна быть другой:
        // currentTotal = <начальный_баланс> - totalExpenses;
        double totalExpenses = 0.0;
        for (ShowFinances item : financeList) {
            totalExpenses += item.getSum();
        }
        sumTextView.setText(String.valueOf(totalExpenses)); // Обновляем sumTextView
    }

    private void showAlertDialogForAddingSum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_input_sum, null);
        EditText editTextSum = view.findViewById(R.id.dialog_edit_text);

        builder.setView(view);
        builder.setTitle("Изменить сумму");

        builder.setPositiveButton("Изменить", null);
        builder.setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss()); // Лямбда-выражение

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> { // Лямбда-выражение
            Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveBtn.setEnabled(false);
            positiveBtn.setOnClickListener(v -> { // Лямбда-выражение
                String sumText = editTextSum.getText().toString().trim();
                sumTextView.setText(sumText); // Используем sumTextView
                dialogInterface.dismiss();
                updateTotalSum(); // Пересчитываем и отображаем баланс
            });
            editTextSum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    positiveBtn.setEnabled(!charSequence.toString().trim().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void addFinanceEntryIfReady() {
        if (pendingCategory != null && !pendingCategory.isEmpty() && pendingSum != null) {
            ShowFinances newEntry = new ShowFinances(0, pendingSum, pendingCategory);
            financeList.add(newEntry); // Добавление элемента
            financeAdapter.notifyDataSetChanged(); // Уведомление адаптера
            updateTotalSum(); // Обновление UI с общей суммой

            Log.d("MainFragment", "Financial entry added: " + pendingCategory + " - " + pendingSum);
            Toast.makeText(requireContext(), "Entry added: " + pendingCategory + " - " + pendingSum, Toast.LENGTH_SHORT).show();

            // ОЧЕНЬ ВАЖНО: Сбросить временные переменные после использования
            pendingCategory = null;
            pendingSum = null;

            // Если вы хотите, чтобы SharedViewModel "забывал" данные после использования,
            // добавьте в него метод типа clearCategoryAndSum() и вызовите его здесь:
            // sharedViewModel.clearCategoryAndSum();
        } else {
            // Логирование для отладки, почему запись не добавляется
            Log.d("MainFragment", "Cannot add entry: " +
                    "pendingCategory=" + pendingCategory +
                    ", pendingSum=" + pendingSum);
        }
    }
}
