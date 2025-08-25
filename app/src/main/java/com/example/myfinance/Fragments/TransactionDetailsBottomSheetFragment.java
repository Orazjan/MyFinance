package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myfinance.Adapters.TransactionAdapter;
import com.example.myfinance.R;
import com.example.myfinance.data.Finances;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class TransactionDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "TransactionDetailsBottomSheetFragment";

    public TransactionDetailsBottomSheetFragment() {
        // Обязательный пустой конструктор
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheetdialogfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView transactionsListView = view.findViewById(R.id.transactionsListView);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("transactions")) {
            // Теперь мы извлекаем ArrayList<Finances>
            ArrayList<Finances> transactions = (ArrayList<Finances>) bundle.getSerializable("transactions");
            if (transactions != null) {
                // И передаем его в наш обновленный адаптер
                TransactionAdapter adapter = new TransactionAdapter(requireContext(), transactions);
                transactionsListView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Список транзакций пуст", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Не удалось получить список транзакций из аргументов", Toast.LENGTH_SHORT).show();
        }
    }
}
