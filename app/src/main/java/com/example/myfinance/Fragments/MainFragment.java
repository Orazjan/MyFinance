package com.example.myfinance.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.example.myfinance.Adapters.CategorySummary;
import com.example.myfinance.Adapters.ShowFinancesAdapter;
import com.example.myfinance.MainActivity;
import com.example.myfinance.Models.AmountViewModel;
import com.example.myfinance.Models.FinanceViewModel;
import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.MyApplication;
import com.example.myfinance.Prevalent.AddSettingToDataStoreManager;
import com.example.myfinance.Prevalent.DateFormatter;
import com.example.myfinance.Prevalent.Months;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainFragment extends Fragment {
    private AddSettingToDataStoreManager appSettingsManager;
    private TextView sumTextView;
    private TextView valutaTextView, summaTextView, SecondvalutaTextView;
    private FloatingActionButton btnAddNewCheck;
    private ListView mainCheck;
    private List<ShowFinances> financeList;
    private ShowFinancesAdapter financeAdapter;
    private double currentBalance;
    private double totalExpenses;
    private AmountViewModel amountViewModel;
    private FinanceViewModel finViewModel;
    private FinanceRepository financeRepository;
    private Spinner spinnerForMonth;
    private CardView cardViewOstatok;
    private static final String TAG = "MainFragment";

    // Список для хранения всех финансовых операций, полученных из базы данных
    private List<Finances> allFinances;

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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sumTextView = view.findViewById(R.id.sum);
        valutaTextView = view.findViewById(R.id.valutaTextView);
        summaTextView = view.findViewById(R.id.summaTextView);
        btnAddNewCheck = view.findViewById(R.id.btnAddNewCheck);
        mainCheck = view.findViewById(R.id.mainCheck);
        SecondvalutaTextView = view.findViewById(R.id.SecondvalutaTextView);
        spinnerForMonth = view.findViewById(R.id.spinnerForMonth);
        cardViewOstatok = view.findViewById(R.id.cardViewOstatok);

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

        // Загружаем все финансы один раз и наблюдаем за изменениями
        finViewModel.getAllFinances().observe(getViewLifecycleOwner(), new Observer<List<Finances>>() {
            @Override
            public void onChanged(@Nullable List<Finances> finances) {
                // Сохраняем весь список финансов
                allFinances = finances;
                // При каждом изменении данных, обновляем отображение с учетом текущего выбранного месяца
                updateFinancesListForMonth(spinnerForMonth.getSelectedItemPosition());
            }
        });

        uploadAndShowMonth();
        updateCurrencyDisplay();

        btnAddNewCheck.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.openSecondaryFragment(new AddingNewFinance(), "AddingNewFinance");
            } else {
                Toast.makeText(getContext(), "Ошибка: Не удалось открыть окно добавления.", Toast.LENGTH_SHORT).show();
            }
        });

        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.change_bounds_transition));

        cardViewOstatok.setOnClickListener(v -> {
            double sum = Double.parseDouble(sumTextView.getText().toString());
            String valuta = valutaTextView.getText().toString();

            // Создаем сводный список категорий
            ArrayList<CategorySummary> categories = createCategorySummaryList(spinnerForMonth.getSelectedItemPosition());

            // Создаем новый фрагмент и передаем в него данные, включая полный список финансов
            DetailsOstatokFragment detailsFragment = DetailsOstatokFragment.newInstance(sum, valuta, "ostatok_card_transition", categories, (ArrayList<Finances>) allFinances);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.addSharedElement(cardViewOstatok, "ostatok_card_transition");
            transaction.replace(R.id.fragment_container, detailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        mainCheck.setOnItemLongClickListener((parent, v, position, id) -> {
            ShowFinances clickedItem = (ShowFinances) parent.getItemAtPosition(position);
            showDialogForChangingData(clickedItem);
            return true;
        });
    }

    /**
     * Создает сводный список категорий и их общих сумм на основе отфильтрованных финансов.
     *
     * @param monthIndex Индекс выбранного месяца.
     * @return ArrayList<CategorySummary> сводный список категорий и сумм.
     */
    private ArrayList<CategorySummary> createCategorySummaryList(int monthIndex) {
        if (allFinances == null) {
            return new ArrayList<>();
        }

        List<Finances> filteredFinances;
        if (monthIndex == 0) {
            filteredFinances = allFinances;
        } else {
            String selectedMonth = Months.getMonthEn(monthIndex);
            filteredFinances = allFinances.stream().filter(finance -> {
                String financeMonth = DateFormatter.getMonthName(finance.getDate());
                return financeMonth != null && financeMonth.equalsIgnoreCase(selectedMonth);
            }).collect(Collectors.toList());
        }

        Map<String, Double> categorySums = new HashMap<>();
        for (Finances finance : filteredFinances) {
            if ("Расход".equals(finance.getOperationType())) {
                String category = finance.getFinanceResult();
                double sum = finance.getSumma();
                categorySums.put(category, categorySums.getOrDefault(category, 0.0) + sum);
            }
        }

        ArrayList<CategorySummary> categorySummaries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categorySums.entrySet()) {
            categorySummaries.add(new CategorySummary(entry.getKey(), entry.getValue()));
        }

        Collections.sort(categorySummaries, (o1, o2) -> Double.compare(o2.getTotalSum(), o1.getTotalSum()));

        return categorySummaries;
    }

    /**
     * Загрузка и отображение текущего месяца.
     */
    private void uploadAndShowMonth() {
        String[] items = Months.getMonthsRu();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForMonth.setAdapter(adapter);

        spinnerForMonth.setSelection(0);

        spinnerForMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFinancesListForMonth(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Обновляет отображаемый список финансовых операций в зависимости от выбранного месяца.
     * @param monthIndex Индекс выбранного месяца (0-12).
     */
    private void updateFinancesListForMonth(int monthIndex) {
        if (allFinances == null) {
            return;
        }

        financeList.clear();

        if (monthIndex == 0) {
            for (Finances finance : allFinances) {
                financeList.add(new ShowFinances(finance.getId(), finance.getSumma(), finance.getFinanceResult(), finance.getOperationType(), finance.getComments(), finance.getDate(), finance.getFirestoreId()));
            }
        } else {
            String selectedMonth = Months.getMonthEn(monthIndex);

            List<Finances> filteredFinances = allFinances.stream().filter(finance -> {
                String financeMonth = DateFormatter.getMonthName(finance.getDate());
                return financeMonth != null && financeMonth.equalsIgnoreCase(selectedMonth);
            }).collect(Collectors.toList());


            for (Finances finance : filteredFinances) {
                financeList.add(new ShowFinances(finance.getId(), finance.getSumma(), finance.getFinanceResult(), finance.getOperationType(), finance.getComments(), finance.getDate(), finance.getFirestoreId()));
            }
        }

        Collections.reverse(financeList);
        financeAdapter.setItems(financeList);
        financeAdapter.notifyDataSetChanged();
        mainCheck.requestLayout();
        mainCheck.invalidate();
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
            } else {
                currentBalance = lastAmount;
                sumTextView.setText(String.valueOf(currentBalance));
            }
        });

        amountViewModel.getSumma().observe(getViewLifecycleOwner(), totalExpensesFromDb -> {
            if (totalExpensesFromDb == null) {
                totalExpenses = 0.0;
                summaTextView.setText("0.0");
            } else {
                totalExpenses = totalExpensesFromDb;
                summaTextView.setText(String.valueOf(totalExpenses));
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

        categoryChangeEditText.setText(clickedItem.getName());
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

            Finances updatedFinance = new Finances(newCategoryName, newSum, clickedItem.getOperationType(), newComments, date);
            updatedFinance.setId(clickedItem.getId());
            updatedFinance.setFirestoreId(clickedItem.getFirestoreId());
            updatedFinance.setSynced(false);

            double currentBalanceBeforeUpdate = amountViewModel.getLastAmount().getValue() != null ? amountViewModel.getLastAmount().getValue() : 0.0;
            double totalExpensesBeforeUpdate = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

            double newCalculatedBalance = currentBalanceBeforeUpdate;
            double newCalculatedExpenses = totalExpensesBeforeUpdate;

            if ("Доход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance -= clickedItem.getSum();
            } else if ("Расход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance += clickedItem.getSum();
                newCalculatedExpenses -= clickedItem.getSum();
            }

            if ("Доход".equals(updatedFinance.getOperationType())) {
                newCalculatedBalance += updatedFinance.getSumma();
            } else if ("Расход".equals(updatedFinance.getOperationType())) {
                newCalculatedBalance -= updatedFinance.getSumma();
                newCalculatedExpenses += updatedFinance.getSumma();
            }

            amountViewModel.insert(new TotalAmount(newCalculatedBalance, newCalculatedExpenses));

            finViewModel.update(updatedFinance);

            Toast.makeText(requireContext(), "Запись обновлена", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });

        builder.setNegativeButton("Удалить", (dialogInterface, i) -> {
            Finances financeToDelete = new Finances(clickedItem.getName(), clickedItem.getSum(), clickedItem.getOperationType(), clickedItem.getComments(), clickedItem.getDate());
            financeToDelete.setId(clickedItem.getId());
            financeToDelete.setFirestoreId(clickedItem.getFirestoreId());

            finViewModel.delete(financeToDelete);

            double currentBalanceBeforeDelete = amountViewModel.getLastAmount().getValue() != null ? amountViewModel.getLastAmount().getValue() : 0.0;
            double totalExpensesBeforeDelete = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

            double newCalculatedBalance = currentBalanceBeforeDelete;
            double newCalculatedExpenses = totalExpensesBeforeDelete;

            if ("Доход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance -= clickedItem.getSum();
            } else if ("Расход".equals(clickedItem.getOperationType())) {
                newCalculatedBalance += clickedItem.getSum();
                newCalculatedExpenses -= clickedItem.getSum();
            }

            amountViewModel.insert(new TotalAmount(newCalculatedBalance, newCalculatedExpenses));

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
        double currentBalanceBefore = amountViewModel.getLastAmount().getValue() != null ? amountViewModel.getLastAmount().getValue() : 0.0;
        double totalExpensesBefore = amountViewModel.getSumma().getValue() != null ? amountViewModel.getSumma().getValue() : 0.0;

        double newCalculatedBalance = currentBalanceBefore;
        double newCalculatedExpenses = totalExpensesBefore;

        if ("Доход".equals(operationType)) {
            newCalculatedBalance += amount;
        } else if ("Расход".equals(operationType)) {
            newCalculatedBalance -= amount;
            newCalculatedExpenses += amount;
        } else {
            Log.w(TAG, "updateBalanceAndExpensesOnNewFinance: Неизвестный тип операции: " + operationType);
        }

        amountViewModel.insert(new TotalAmount(newCalculatedBalance, newCalculatedExpenses));
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

                        amountViewModel.insert(new TotalAmount(newSum, currentTotalExpenses));

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
     * Возвращает текущую дату в формате "dd.MM.yyyy".
     *
     * @return Текущая дата в строковом формате.
     */
    public String getCurrentDate() {
        return DateFormatter.formatDate(new Date());
    }
}
