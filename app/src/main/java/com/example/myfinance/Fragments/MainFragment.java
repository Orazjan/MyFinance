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
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Adapters.ShowFinancesAdapter;
import com.example.myfinance.MainActivity;
import com.example.myfinance.Models.SharedViewModel;
import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.R;
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
    private SharedViewModel sharedViewModel;
    private String pendingCategory;
    private Double pendingSum;
    private int id = 0;

    public MainFragment() {
    }

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

        if (financeList == null) {
            financeList = new ArrayList<>();
        }
        if (financeAdapter == null) {
            financeAdapter = new ShowFinancesAdapter(requireContext(), financeList);
            mainCheck.setAdapter(financeAdapter);
        }

        getSharedViewModel();

        changeSumButton.setOnClickListener(v -> {
            showAlertDialogForAddingSum();
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
        updateTotalSum();
    }

    private void getSharedViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), s -> {
            pendingCategory = s;
        });

        sharedViewModel.getSum().observe(getViewLifecycleOwner(), s -> {
            pendingSum = s;
            addFinanceEntryIfReady();
        });
    }

    private void updateTotalSum() {
        double totalExpenses = 5000;
        for (ShowFinances item : financeList) {
            totalExpenses -= item.getSum();
        }
        sumTextView.setText(String.valueOf(totalExpenses));
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
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveBtn.setEnabled(false);
            positiveBtn.setOnClickListener(v -> {
                String sumText = editTextSum.getText().toString().trim();
                sumTextView.setText(sumText);
                dialogInterface.dismiss();
                updateTotalSum();
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
        id += 1;
        if (pendingCategory != null && !pendingCategory.isEmpty() && pendingSum != null) {
            ShowFinances newEntry = new ShowFinances(id, pendingSum, pendingCategory);
            financeList.add(newEntry);
            Collections.reverse(financeList);
            financeAdapter.setItems(financeList);
            financeAdapter.notifyDataSetChanged();
            updateTotalSum();

            pendingCategory = null;
            pendingSum = null;

        } else {
            Log.d("MainFragment", "Cannot add entry: " +
                    "pendingCategory=" + pendingCategory +
                    ", pendingSum=" + pendingSum);
        }
    }
}
