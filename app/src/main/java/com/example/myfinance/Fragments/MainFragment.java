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
    private double plusSumma;
    private AmountViewModel amountViewModel;
    private FinanceViewModel finViewModel;
    private FinanceRepository financeRepository;

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

        //  Получение суммы из родительской активности.
        getParentFragmentManager().setFragmentResultListener("ValueSum", getViewLifecycleOwner(), (requestKey, bundle) -> {
            if (requestKey.equals("ValueSum")) {
                double sum = bundle.getDouble("ValueSum");
                updateTotalSum(sum);
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
                Log.e("FragmentError", "Родительская Activity не является MainActivity. Невозможно открыть AddingNewFinance.");
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
     * Обновление отображения валюты.
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
        amountViewModel.getSumma().observe(getViewLifecycleOwner(), aDouble -> {
            if (aDouble == null) {
                summaTextView.setText("0.0");
            } else {
                plusSumma = aDouble;
                summaTextView.setText(String.valueOf(plusSumma));
            }
        });
    }

    /**
     * Отображение диалогового окна для изменения данных.
     *
     * @param clickedItem
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
            String newCategory = categoryChangeEditText.getText().toString().trim();
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

            // Создаем объект Finances для обновления
            Finances updatedFinance = new Finances(newCategory, newSum, newComments, date);
            updatedFinance.setId(clickedItem.getId());
            updatedFinance.setFirestoreId(clickedItem.getFirestoreId());
            updatedFinance.setSynced(false);


            double sumDifference = newSum - clickedItem.getSum();
            double balanceChange = 0;
            if (clickedItem.getName().equals("Доход") && newCategory.equals("Доход")) {
                balanceChange = newSum - clickedItem.getSum();
            } else if (clickedItem.getName().equals("Расход") && newCategory.equals("Расход")) {
                balanceChange = -(newSum - clickedItem.getSum());
            } else if (clickedItem.getName().equals("Доход") && newCategory.equals("Расход")) {
                balanceChange = -clickedItem.getSum() - newSum;
            } else if (clickedItem.getName().equals("Расход") && newCategory.equals("Доход")) {
                balanceChange = clickedItem.getSum() + newSum;
            }

            currentBalance += balanceChange;
            setSumTextView(currentBalance);

            finViewModel.update(updatedFinance);

            Toast.makeText(requireContext(), "Запись обновлена", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });

        builder.setNegativeButton("Удалить", (dialogInterface, i) -> {
            Finances financeToDelete = new Finances(clickedItem.getName(), clickedItem.getSum(), clickedItem.getComments(), clickedItem.getDate());
            financeToDelete.setId(clickedItem.getId());
            financeToDelete.setFirestoreId(clickedItem.getFirestoreId());

            finViewModel.delete(financeToDelete);

            double balanceChange = 0;
            if (clickedItem.getName().equals("Доход")) {
                balanceChange = -clickedItem.getSum();
            } else if (clickedItem.getName().equals("Расход")) {
                balanceChange = clickedItem.getSum();
            }
            currentBalance += balanceChange;
            setSumTextView(currentBalance);

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
     * Устанавливает значение суммы в TextView.
     *
     * @param sum
     */
    private void setSumTextView(double sum) {
        currentBalance = sum;
        sumTextView.setText(String.valueOf(currentBalance));
        amountViewModel.insert(new TotalAmount(currentBalance, plusSumma));
    }

    /**
     * Устанавливает значение суммы в TextView.
     * @param summa
     */
    private void setPlusSummaTextView(double summa) {
        plusSumma = summa;
        summaTextView.setText(String.valueOf(plusSumma));
    }

    /**
     * Обновление общей суммы.
     * @param expenseAmount - это будет сумма расхода (положительная)
     */
    private void updateTotalSum(double expenseAmount) {
        if (expenseAmount == 0.0) {
            return;
        }

        plusSumma = plusSumma + expenseAmount;
        currentBalance = currentBalance - expenseAmount;
        setPlusSummaTextView(plusSumma);
        setSumTextView(currentBalance);
    }

    /**
     * Отображение диалогового окна для изменения суммы.
     *
     * @param initialSum
     */
    private void showAlertDialogForAddingSum(double initialSum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_input_sum, null);
        TextView sumChange = view.findViewById(R.id.sumForChange);
        EditText editTextSum = view.findViewById(R.id.dialog_edit_text);
        sumChange.setText("Введите сумму для изменеия");
        editTextSum.setText(String.valueOf(initialSum));
        editTextSum.setSelection(editTextSum.getText().length());

        builder.setView(view);
        builder.setTitle("Изменить сумму");

        builder.setPositiveButton("Изменить", null);
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
                        setSumTextView(newSum);
                        dialogInterface.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Введите корректное числовое значение", Toast.LENGTH_SHORT).show();
                        Log.e("MainFragment", "Ошибка парсинга суммы: " + sumText, e);
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
     */
    private void getFinanceList() {
        finViewModel.getAllFinances().observe(getViewLifecycleOwner(), new Observer<List<Finances>>() {
            @Override
            public void onChanged(@Nullable List<Finances> finances) {
                financeList.clear();

                if (finances != null) {
                    for (Finances finance : finances) {
                        financeList.add(new ShowFinances(finance.getId(), finance.getSumma(), finance.getFinanceResult(), finance.getComments(), finance.getDate(), finance.getFirestoreId()));
                    }
                }
                Collections.reverse(financeList);

                financeAdapter.setItems(financeList);
                financeAdapter.notifyDataSetChanged();
                mainCheck.requestLayout();
                mainCheck.invalidate();
            }
        });
    }

    /**
     * Возвращает текущую дату в формате "dd.MM.yyyy".
     *
     * @return
     */
    public String getCurrentDate() {
        return DateFormatter.formatDate(new Date());
    }
}