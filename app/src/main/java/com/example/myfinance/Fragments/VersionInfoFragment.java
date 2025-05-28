package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.R;

import java.util.ArrayList;

public class VersionInfoFragment extends Fragment {
    private TextView InfoText;
    private Spinner mySpinner;
    private ArrayList<String> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.version_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InfoText = view.findViewById(R.id.InfoText);
        mySpinner = view.findViewById(R.id.mySpinner);

        categories = new ArrayList<>();
        categories.add("V 0.1");
        categories.add("V 0.2");
        categories.add("V 0.3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mySpinner.setAdapter(adapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                switch (selectedItem) {
                    case "V 0.1":
                        InfoText.setText(selectedItem + ". Добавлено: Категории внутри Профиль");
                        break;
                    case "V 0.2":
                        InfoText.setText(selectedItem + ". Добавлено: Свайп между страницами");
                        break;
                    case "V 0.3":
                        InfoText.setText(selectedItem + ". Добавлено: Фикс багов. Фрагменты исправлены и работают между собой." +
                                "'Экран Расчёта и вывода финансовых расходов исправлен");
                        break;
                    default:
                        InfoText.setText(selectedItem + ". Добавлено: Свайп между страницами");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
