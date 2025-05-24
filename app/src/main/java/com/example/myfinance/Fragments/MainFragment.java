package com.example.myfinance.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.myfinance.Adapters.ShowFinancesAdapter;
import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private TextView sum;
    private ImageButton changeSum;
    private ListView mainCheck;
    private List<ShowFinances> CheckList;
    private ShowFinancesAdapter adapter;

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
        sum = view.findViewById(R.id.sum);
        changeSum = view.findViewById(R.id.changeSum);

        CheckList = new ArrayList<>();
        CheckList.add(new ShowFinances(0, 200, "Huyna"));
        CheckList.add(new ShowFinances(1, 451, "Oraz"));
        mainCheck = view.findViewById(R.id.mainCheck);
        adapter = new ShowFinancesAdapter(requireActivity(), CheckList);
        mainCheck.setAdapter(adapter);

        changeSum.setOnClickListener(View -> {
            showAlertDialogForAddingSum();
        });
    }

    private void showAlertDialogForAddingSum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_input_sum, null);
        EditText editTextSum = view.findViewById(R.id.dialog_edit_text);

        builder.setView(view);
        builder.setTitle("Изменить сумму");

        builder.setPositiveButton("Изменить", null);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveBtn.setEnabled(false);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String sumText = editTextSum.getText().toString().trim();
                        sum.setText(sumText);
                        dialogInterface.dismiss();
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
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
