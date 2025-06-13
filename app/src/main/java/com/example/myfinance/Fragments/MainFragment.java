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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Adapters.ShowFinancesAdapter;
import com.example.myfinance.MainActivity;
import com.example.myfinance.Models.AmountViewModel;
import com.example.myfinance.Models.FinanceViewModel;
import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.Prevalent.DateFormatter;
import com.example.myfinance.R;
import com.example.myfinance.data.AmountDatabase;
import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.FinanceDatabase;
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

    private TextView sumTextView;
    private ImageButton changeSumButton;
    private FloatingActionButton btnAddNewCheck;
    private ListView mainCheck;
    private List<ShowFinances> financeList;
    private ShowFinancesAdapter financeAdapter;
    private double currentBalance;
    private AmountViewModel amountViewModel;
    private FinanceViewModel finViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sumTextView = view.findViewById(R.id.sum);
        changeSumButton = view.findViewById(R.id.changeSum);
        btnAddNewCheck = view.findViewById(R.id.btnAddNewCheck);
        mainCheck = view.findViewById(R.id.mainCheck);

        FinanceDatabase finDb = FinanceDatabase.getDatabase(requireActivity().getApplication());
        AmountDatabase amdb = AmountDatabase.getDatabase(requireActivity().getApplication());

        FinanceRepository repo = new FinanceRepository(finDb.daoFinances());
        AmountRepository amrepo = new AmountRepository(amdb.daoTotalAmount());

        FinanceViewModel.TaskViewModelFactory finViewModelTaskFactory = new FinanceViewModel.TaskViewModelFactory(repo);
        finViewModel = new ViewModelProvider(requireActivity(), finViewModelTaskFactory).get(FinanceViewModel.class);

        AmountViewModel.TaskViewModelFactory amViewModelTaskFactory = new AmountViewModel.TaskViewModelFactory(amrepo);
        amountViewModel = new ViewModelProvider(requireActivity(), amViewModelTaskFactory).get(AmountViewModel.class);

        Log.d(requireContext().toString(), "startBalance " + sumTextView.getText().toString());

        if (financeList == null) {
            financeList = new ArrayList<>();
        }
        if (financeAdapter == null) {
            financeAdapter = new ShowFinancesAdapter(requireContext(), financeList);
            mainCheck.setAdapter(financeAdapter);
        }

        getParentFragmentManager().setFragmentResultListener("ValueSum", getViewLifecycleOwner(), (requestKey, bundle) -> {
            if (requestKey.equals("ValueSum")) {
                double sum = bundle.getDouble("ValueSum");

                updateTotalSum(sum);
            }
        });
        getSumFromRoom();
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
     * Отображение диалогового окна для изменения данных.
     *
     * @param clickedItem
     */
    private void showDialogForChangingData(ShowFinances clickedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_reason_sum_somment, null);
        TextView categoryChangeEditText = view.findViewById(R.id.category_edit_text);
        EditText sumChangeEditText = view.findViewById(R.id.sum_edit_text);
        EditText commentsChangeEditText = view.findViewById(R.id.comments_edit_text);
        EditText dateChangeEditText = view.findViewById(R.id.date_edit_text);

        categoryChangeEditText.setText(clickedItem.getName());
        sumChangeEditText.setText(String.valueOf(clickedItem.getSum()));
        commentsChangeEditText.setText(clickedItem.getComments());
        dateChangeEditText.setText(clickedItem.getDate());
        sumChangeEditText.setSelection(sumChangeEditText.getText().length());

        builder.setView(view);
        builder.setTitle("Редактирование");

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

            double sumDifference = newSum - clickedItem.getSum();

            double newBalance = currentBalance - sumDifference;
            setSumTextView(newBalance);

            Finances updatedFinance = new Finances(newCategory, newSum, newComments, date);
            updatedFinance.setId(clickedItem.getId());
            finViewModel.update(updatedFinance);

            Toast.makeText(requireContext(), "Запись обновлена", Toast.LENGTH_SHORT).show();
            getFinanceList();
            dialogInterface.dismiss();
        });

        builder.setNegativeButton("Удалить", (dialogInterface, i) -> {
            Finances financeToDelete = new Finances(clickedItem.getName(), clickedItem.getSum(), clickedItem.getComments(), clickedItem.getDate());
            financeToDelete.setId(clickedItem.getId());

            finViewModel.delete(financeToDelete);

            double newBalance = currentBalance + clickedItem.getSum();
            setSumTextView(newBalance);

            Toast.makeText(requireContext(), "Запись удалена", Toast.LENGTH_SHORT).show();
            getFinanceList();
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
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
        sumTextView.setText(String.valueOf(sum));
        amountViewModel.insert(new TotalAmount(sum));
    }

    /**
     * Получение суммы из базы данных.
     */
    private void getSumFromRoom() {
        amountViewModel.getLastAmount().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double lastAmount) {
                if (lastAmount == null) {
                    currentBalance = 0.0;
                    sumTextView.setText("0.0");
                } else {
                    currentBalance = lastAmount;
                    sumTextView.setText(String.valueOf(currentBalance));
                }
            }
        });
    }

    /**
     * Обновление суммы на экране.
     *
     * @param expenseAmount
     */
    private void updateTotalSum(double expenseAmount) {
        double newBalance = currentBalance - expenseAmount;
        setSumTextView(newBalance);
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
        //      repo.deleteAll();        На всякий если удалить нужно

        finViewModel.getAllFinances().observe(getViewLifecycleOwner(), new Observer<List<Finances>>() {
            @Override
            public void onChanged(@Nullable List<Finances> finances) {
                financeList.clear();

                if (finances != null) {
                    for (Finances finance : finances) {
                        financeList.add(new ShowFinances(finance.getId(), finance.getSumma(), finance.getFinanceResult(), finance.getComments(), finance.getDate()));
                    }
                }
                Collections.reverse(financeList);

                financeAdapter.setItems(financeList);
                financeAdapter.notifyDataSetChanged();
            }
        });

        amountViewModel.update(new TotalAmount(currentBalance));
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
