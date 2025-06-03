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
        categories.add("V 0.5");
        categories.add("V 0.4");
        categories.add("V 0.3");
        categories.add("V 0.2");
        categories.add("V 0.1");

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
                    case "V 0.4":
                        InfoText.setText(selectedItem + ". Исправлено: Фикс багов. При нажатии на сумму открывается диалог по изменеию её\n" +
                                "\nДобавлено: Добавлено локальная БД room " +
                                "Загрузка данных производиться из ROOM. " +
                                "Общая сумма загружается и выводится на экран из ROOM\n" +
                                "\nИзменено: удалено Передача данных из SharedViewModel");
                        break;
                    case "V 0.5":
                        InfoText.setText(selectedItem + ". Исправлено: " +
                                "\n-В диалоговом окне можно устонавливать новую сумму и она загружается в ROOM\n" +
                                "-Выбор списания раньше сумма автоматически не вставлялось\n" + "\nДобавлено: \n-Логотип" + "\nИзменено: -В фрагменте 'Шаблоны' изменено место суммы и причины. " + "\n-Удалено раблта с Internal Storage" + "\n-Изменение суммы в категориях");
                        break;
                    default:
                        InfoText.setText(selectedItem + ". Ошибка при загрузке информации");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
