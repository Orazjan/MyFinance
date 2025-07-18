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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Adapters.ShowFinancesAdapter;
import com.example.myfinance.MainActivity;
import com.example.myfinance.Models.AmountViewModel;
import com.example.myfinance.Models.FinanceViewModel;
import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.MyApplication;
import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.Prevalent.DateFormatter;
import com.example.myfinance.R;
import com.example.myfinance.data.AmountDatabase;
import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.FinanceRepository;
import com.example.myfinance.data.Finances;
import com.example.myfinance.data.TotalAmount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment {
    private AddSettingToDataStoreManager appSettingsManager;
    private TextView sumTextView;
    private TextView valutaTextView, summaTextView, SecondvalutaTextView;
    private ImageButton changeSumButton;
    private FloatingActionButton btnAddNewCheck;
    private ListView mainCheck;
    private List<ShowFinances> financeList;
    private ShowFinancesAdapter financeAdapter;
    private double currentBalance;
    private double totalExpenses;
    private AmountViewModel amountViewModel;
    private FinanceViewModel finViewModel;
    private FinanceRepository financeRepository;

    private static final String TAG = "MainFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    /**
     * Вызывается после создания View.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mainCheck != null) {
            mainCheck.post(() -> {
                mainCheck.requestLayout();
                mainCheck.invalidate();
            });
        }
        updateCurrencyDisplay();
    }

    /**
     * Вызывается после создания View.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sumTextView = view.findViewById(R.id.sum);
        valutaTextView = view.findViewById(R.id.valutaTextView);
        summaTextView = view.findViewById(R.id.summaTextView);
        changeSumButton = view.findViewById(R.id.changeSum);
        btnAddNewCheck = view.findViewById(R.id.btnAddNewCheck);
        mainCheck = view.findViewById(R.id.mainCheck);
        SecondvalutaTextView = view.findViewById(R.id.SecondvalutaTextView);

        appSettingsManager = new AddSettingToDataStoreManager(requireContext());

        financeRepository = ((MyApplication) requireActivity().getApplication()).getFinanceRepository();

        AmountDatabase amdb = AmountDatabase.getDatabase(requireActivity().getApplication());
        AmountRepository amrepo = new AmountRepository(amdb.daoTotalAmount());

        FinanceViewModel.TaskViewModelFactory finViewModelTaskFactory = new FinanceViewModel.TaskViewModelFactory(financeRepository);
        finViewModel = new ViewModelProvider(requireActivity(), finViewModelTaskFactory).get(FinanceViewModel.class);

        AmountViewModel.TaskViewModelFactory amViewModelTaskFactory = new AmountViewModel.TaskViewModelFactory(amrepo);
        amountViewModel = new ViewModelProvider(requireActivity(), amViewModelTaskFactory).get(AmountViewModel.class);

        if (financeList == null) {
            financeList = new ArrayList<>();
        }
        if (financeAdapter == null) {
            financeAdapter = new ShowFinancesAdapter(requireContext(), financeList);
            mainCheck.setAdapter(financeAdapter);
        }

        getParentFragmentManager().setFragmentResultListener("ValueSumAndType", getViewLifecycleOwner(), (requestKey, bundle) -> {
            if (requestKey.equals("ValueSumAndType")) {
                double newFinanceSum = bundle.getDouble("Sum");
                String operationType = bundle.getString("OperationType");
                updateBalanceAndExpensesOnNewFinance(newFinanceSum, operationType);
            }
        });
        updateCurrencyDisplay();
        getFinanceList();

        changeSumButton.setOnClickListener(v -> {
            showAlertDialogForAddingSum(currentBalance);
        });
        sumTextView.setOnClickListener(v -> {
            showAlertDialogForAddingSum(currentBalance);
        });
        btnAddNewCheck.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.openSecondaryFragment(new AddingNewFinance(), "AddingNewFinance");
            } else {
                Log.e(TAG, "Родительская Activity не является MainActivity. Невозможно открыть AddingNewFinance.");
                Toast.makeText(getContext(), "Ошибка: Не удалось открыть окно добавления.", Toast.LENGTH_SHORT).show();
            }
        });
        mainCheck.setOnItemLongClickListener((parent, v, position, id) -> {
            ShowFinances clickedItem = (ShowFinances) parent.getItemAtPosition(position);
            showDialogForChangingData(clickedItem);
            return true;
        });
        mainCheck.setOnItemClickListener((parent, v, position, id) -> {
            ShowFinances clickedItem = (ShowFinances) parent.getItemAtPosition(position);
            if (Objects.equals(clickedItem.getComments(), "")) {
                Toast.makeText(requireContext(), "Запись не имеет комментариев", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(requireContext(), clickedItem.getComments(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Обновление отображения валюты и инициализация текущего баланса и общей суммы расходов из БД.
     * Обзерверы LiveData будут обновлять поля currentBalance и totalExpenses.
     */
    private void updateCurrencyDisplay() {
        valutaTextView.setText(appSettingsManager.getCurrencyType());
        SecondvalutaTextView.setText(appSettingsManager.getCurrencyType());

        amountViewModel.getLastAmount().observe(getViewLifecycleOwner(), lastAmount -> {
            if (lastAmount == null) {
                currentBalance = 0.0;
                sumTextView.setText("0.0");
                Log.d(TAG, "updateCurrencyDisplay: Initial currentBalance set to 0.0 (lastAmount is null)");
            } else {
                currentBalance = lastAmount;
                sumTextView.setText(String.valueOf(currentBalance));
                Log.d(TAG, "updateCurrencyDisplay: currentBalance updated to " + currentBalance + " from lastAmount observer");
            }
        });

        amountViewModel.getSumma().observe(getViewLifecycleOwner(), totalExpensesFromDb -> {
            if (totalExpensesFromDb == null) {
                totalExpenses = 0.0;
                summaTextView.setText("0.0");
                Log.d(TAG, "updateCurrencyDisplay: Initial totalExpenses set to 0.0 (totalExpensesFromDb is null)");
            } else {
                totalExpenses = totalExpensesFromDb;
                summaTextView.setText(String.valueOf(totalExpenses));
                Log.d(TAG, "updateCurrencyDisplay: totalExpenses updated to " + totalExpenses + " from summa observer");
            }
        });
    }

    /**
     * Отображение диалогового окна для изменения данных.
     *
     * @param clickedItem Объект ShowFinances, который нужно изменить.
     */
    private void showDialogForChangingData(ShowFinances clickedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_reason_sum_somment, null);
        TextView categoryChangeEditText = view.findViewById(R.id.category_edit_text);
        ImageView imgClose = view.findViewById(R.id.imgClose);
        EditText sumChangeEditText = view.findViewById(R.id.sum_edit_text);
        EditText commentsChangeEditText = view.findViewById(R.id.comments_edit_text);
        EditText dateChangeEditText = view.findViewById(R.id.date_edit_text);

        // Устанавливаем текущие значения
        categoryChangeEditText.setText(clickedItem.getName()); // Имя категории
        sumChangeEditText.setText(String.valueOf(clickedItem.getSum()));
        commentsChangeEditText.setText(clickedItem.getComments());
        dateChangeEditText.setHint(clickedItem.getDate());
        sumChangeEditText.setSelection(sumChangeEditText.getText().length());

        builder.setView(view);

        builder.setPositiveButton("Изменить", (dialogInterface, i) -> {
            String newCategoryName = categoryChangeEditText.getText().toString().trim();
            String sumText = sumChangeEditText.getText().toString().trim();
            String newComments = commentsChangeEditText.getText().toString().trim();
            String date = getCurrentDate();

            if (sumText.isEmpty()) {
                Toast.makeText(requireContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
                return;
            }

            double newSum;
            try {
                newSum = Double.parseDouble(sumText);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Некорректная сумма", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newSum == 0.0) {
                Toast.makeText(requireContext(), "Сумма не может быть нулевой", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- ИЗМЕНЕНИЕ: Создаем объект Finances для обновления с сохранением operationType ---
            Finances updatedFinance = new Finances(newCategoryName, newSum, clickedItem.getOperationType(), newComments, date);
            updatedFinance.setId(clickedItem.getId());
            updatedFinance.setFirestoreId(clickedItem.getFirestoreId());
            updatedFinance.setSynced(false);

            // Получаем текущие значения баланса и расходов из LiveData перед расчетом
            double currentBalanceBeforeUpdate = amountViewModel.getLastAmount().getValue() != null ? amountViewModel.getLastAmount().getValue() : 0.0;
            double totalExpensesBeforeUpdate = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

            double newCalculatedBalance = currentBalanceBeforeUpdate;
            double newCalculatedExpenses = totalExpensesBeforeUpdate;

            // Отменяем эффект старой записи
            if ("Доход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance -= clickedItem.getSum();
            } else if ("Расход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance += clickedItem.getSum();
                newCalculatedExpenses -= clickedItem.getSum(); // Отменяем старый расход
            }

            // Применяем эффект новой записи
            if ("Доход".equals(updatedFinance.getOperationType())) {
                newCalculatedBalance += updatedFinance.getSumma();
            } else if ("Расход".equals(updatedFinance.getOperationType())) {
                newCalculatedBalance -= updatedFinance.getSumma();
                newCalculatedExpenses += updatedFinance.getSumma(); // Применяем новый расход
            }

            // Сохраняем новые значения в базу данных через AmountViewModel
            amountViewModel.insert(new TotalAmount(newCalculatedBalance, newCalculatedExpenses));
            Log.d(TAG, "showDialogForChangingData (Update): Updated TotalAmount to Balance=" + newCalculatedBalance + ", Expenses=" + newCalculatedExpenses);

            finViewModel.update(updatedFinance); // Обновляем запись в базе данных

            Toast.makeText(requireContext(), "Запись обновлена", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });

        builder.setNegativeButton("Удалить", (dialogInterface, i) -> {
            Finances financeToDelete = new Finances(clickedItem.getName(), clickedItem.getSum(), clickedItem.getOperationType(), clickedItem.getComments(), clickedItem.getDate());
            financeToDelete.setId(clickedItem.getId());
            financeToDelete.setFirestoreId(clickedItem.getFirestoreId());

            finViewModel.delete(financeToDelete); // Удаляем запись из базы данных

            // --- ИЗМЕНЕНИЕ: Корректное изменение баланса и расходов при удалении ---
            // Получаем текущие значения баланса и расходов из LiveData перед расчетом
            double currentBalanceBeforeDelete = amountViewModel.getLastAmount().getValue() != null ? amountViewModel.getLastAmount().getValue() : 0.0;
            double totalExpensesBeforeDelete = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

            double newCalculatedBalance = currentBalanceBeforeDelete;
            double newCalculatedExpenses = totalExpensesBeforeDelete;

            if ("Доход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance -= clickedItem.getSum(); // Уменьшаем баланс на сумму дохода
            } else if ("Расход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance += clickedItem.getSum(); // Увеличиваем баланс на сумму расхода
                newCalculatedExpenses -= clickedItem.getSum(); // Уменьшаем общие расходы
            }

            // Сохраняем новые значения в базу данных через AmountViewModel
            amountViewModel.insert(new TotalAmount(newCalculatedBalance, newCalculatedExpenses));
            Log.d(TAG, "showDialogForChangingData (Delete): Updated TotalAmount to Balance=" + newCalculatedBalance + ", Expenses=" + newCalculatedExpenses);
            // --- КОНЕЦ ИЗМЕНЕНИЯ ---

            Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });


        AlertDialog dialog = builder.create();
        imgClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Обновление общей суммы и баланса на основе новой операции.
     * @param amount Сумма операции.
     * @param operationType Тип операции ("Доход" или "Расход").
     */
    private void updateBalanceAndExpensesOnNewFinance(double amount, String operationType) {
        // Получаем текущие значения баланса и расходов из LiveData перед расчетом
        double currentBalanceBefore = amountViewModel.getLastAmount().getValue() != null ? amountViewModel.getLastAmount().getValue() : 0.0;
        double totalExpensesBefore = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

        double newCalculatedBalance = currentBalanceBefore;
        double newCalculatedExpenses = totalExpensesBefore;

        // Обновление currentBalance в зависимости от типа операции
        if ("Доход".equals(operationType)) {
            newCalculatedBalance += amount;
        } else if ("Расход".equals(operationType)) {
            newCalculatedBalance -= amount;
            newCalculatedExpenses += amount; // Обновляем общие расходы только для "Расход"
        } else {
            Log.w(TAG, "updateBalanceAndExpensesOnNewFinance: Неизвестный тип операции: " + operationType);
        }

        // Сохраняем новые значения в базу данных через AmountViewModel
        amountViewModel.insert(new TotalAmount(newCalculatedBalance, newCalculatedExpenses));
        Log.d(TAG, "updateBalanceAndExpensesOnNewFinance: Updated TotalAmount to Balance=" + newCalculatedBalance + ", Expenses=" + newCalculatedExpenses);
    }

    /**
     * Отображение диалогового окна для изменения общей суммы.
     *
     * @param initialSum Исходная сумма для отображения в диалоге.
     */
    private void showAlertDialogForAddingSum(double initialSum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_input_sum, null);
        TextView sumChange = view.findViewById(R.id.sumForChange);
        EditText editTextSum = view.findViewById(R.id.dialog_edit_text);
        sumChange.setText("Введите начальный баланс");
        editTextSum.setText(String.valueOf(initialSum));
        editTextSum.setSelection(editTextSum.getText().length());

        builder.setView(view);
        builder.setTitle("Установить начальный баланс");

        builder.setPositiveButton("Установить", null);
        builder.setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveBtn.setEnabled(!editTextSum.getText().toString().trim().isEmpty());

            positiveBtn.setOnClickListener(v -> {
                String sumText = editTextSum.getText().toString().trim();
                if (!sumText.isEmpty()) {
                    try {
                        double newSum = Double.parseDouble(sumText);
                        double currentTotalExpenses = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

                        // Сохраняем новые значения в базу данных через AmountViewModel
                        amountViewModel.insert(new TotalAmount(newSum, currentTotalExpenses));
                        Log.d(TAG, "showAlertDialogForAddingSum: Set initial balance to " + newSum + ", Total Expenses remain " + currentTotalExpenses);

                        dialogInterface.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Введите корректное числовое значение", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Ошибка парсинга суммы: " + sumText, e);
                    }
                }
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

    /**
     * Получение списка транзакций из базы данных.
     * Этот метод теперь только загружает список финансов для отображения.
     * Баланс и общие расходы управляются инкрементально через AmountViewModel.
     */
    private void getFinanceList() {
        finViewModel.getAllFinances().observe(getViewLifecycleOwner(), new Observer<List<Finances>>() {
            @Override
            public void onChanged(@Nullable List<Finances> finances) {
                financeList.clear();

                if (finances != null) {
                    for (Finances finance : finances) {
                        financeList.add(new ShowFinances(finance.getId(), finance.getSumma(), finance.getFinanceResult(), finance.getOperationType(), finance.getComments(), finance.getDate(), finance.getFirestoreId()));
                    }
                }
                Collections.reverse(financeList);

                financeAdapter.setItems(financeList);
                financeAdapter.notifyDataSetChanged();
                mainCheck.requestLayout();
                mainCheck.invalidate();
                Log.d(TAG, "getFinanceList: Finance list updated for display.");
            }
        });
    }

    /**
     * Возвращает текущую дату в формате "dd.MM.yyyy".
     *
     * @return Текущая дата в строковом формате.
     */
    public String getCurrentDate() {
        return DateFormatter.formatDate(new Date());
    }
}
