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
import java.util.List;

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
                Toast.makeText(requireContext(), "Данные получены: " + sum, Toast.LENGTH_SHORT).show();
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
        mainCheck.setOnItemClickListener((parent, v, position, id) -> {
            ShowFinances clickedItem = (ShowFinances) parent.getItemAtPosition(position);
            Toast.makeText(requireContext(), "Запись добавлена: " + clickedItem.getSum(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setSumTextView(double sum) {
        sumTextView.setText(String.valueOf(sum));
        amountViewModel.insert(new TotalAmount(sum));
    }

    private void getSumFromRoom() {
        amountViewModel.getLastAmount().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double lastAmount) {
                if (lastAmount == null) {
                    currentBalance = 0.0;
                } else {
                    currentBalance = lastAmount;
                    setSumTextView(currentBalance);
                }
            }
        });
    }

    private void updateTotalSum(double totalExpenses) {
        double Totalsum = currentBalance - totalExpenses;
        setSumTextView(Totalsum);
    }

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

    private void getFinanceList() {
        //      repo.deleteAll();        На всякий если удалить нужно

        finViewModel.getAllFinances().observe(getViewLifecycleOwner(), new Observer<List<Finances>>() {
            @Override
            public void onChanged(@Nullable List<Finances> finances) {
                financeList.clear();

                if (finances != null) {
                    for (Finances finance : finances) {
                        financeList.add(new ShowFinances(finance.getId(), finance.getSumma(), finance.getFinanceResult()));
                    }
                }
                Collections.reverse(financeList);

                financeAdapter.setItems(financeList);
                financeAdapter.notifyDataSetChanged();
                Log.d("Main Fragment", "Список после обновления: " + financeList.size() + " элементов.");
            }
        });

        amountViewModel.update(new TotalAmount(currentBalance));
    }
}
