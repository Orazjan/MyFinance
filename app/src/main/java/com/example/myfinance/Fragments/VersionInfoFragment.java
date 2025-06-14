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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VersionInfoFragment extends Fragment {

    private TextView infoText;
    private Spinner versionSpinner;
    private List<String> versionNames;
    private Map<String, String> versionDescriptions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.version_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setupVersionData();
        setupSpinner();
        setupSpinnerListener();

        if (!versionNames.isEmpty()) {
            infoText.setText(versionDescriptions.get(versionNames.get(0)));
        }
    }

    /**
     * Инициализирует элементы пользовательского интерфейса (UI) фрагмента.
     */
    private void initUI(@NonNull View view) {
        infoText = view.findViewById(R.id.InfoText);
        versionSpinner = view.findViewById(R.id.mySpinner);
    }

    /**
     * Подготавливает названия версий для Spinner'а
     * и их подробные описания.
     */
    private void setupVersionData() {
        versionDescriptions = new LinkedHashMap<>();

        versionDescriptions.put("V 0.7", "V 0.7\n" +
                "\nДобавлено: \n-Время добавления записи в список\n-Страница Профиль изменена\n" +
                "\n-Исправлено:\n-Выход из приложение когда несколько раз перезаходишь в шаблоны\n-После выхода из второстепенных фрагментов Список финансов снова работает");

        versionDescriptions.put("V 0.6", "V 0.6\n" +
                "Исправлено: \n-Фикс багов.\n-Ориентация экрана теперь только портретная\n" +
                "-Страница настройки\n-Работа над темой\n" +
                "\nДобавлено: \n-Уведомление при нажатии на финанс\n-Долгое нажатие выводит окно для изменения\n-Комментарии к записям.\n-Возможность изменять категории, сумму и комментарий\n-При выборе варианта темы и вида валюты, данные добавляются в память устройства\n" +
                "\nИзменено: \n-Работа с вычислением основнойо суммы");

        versionDescriptions.put("V 0.5", "V 0.5\n" +
                "Исправлено:  \n-Фикс багов.\n" +
                "-При выборе категории если сумма равна 0.0 то открывается клавиатура\n" +
                "-В диалоговом окне можно устонавливать новую сумму и она загружается в ROOM\n" +
                "-Выбор списания раньше сумма автоматически не вставлялось\n" +
                "\nДобавлено: \n-Логотип\n" +
                "\nИзменено: \n-В фрагменте 'Шаблоны' изменено место суммы и причины. \n" +
                "-Удалено работа с Internal Storage\n" +
                "-Изменение суммы в категориях");

        versionDescriptions.put("V 0.4", "V 0.4\n" +
                "Исправлено: Фикс багов. При нажатии на сумму открывается диалог по изменению её. Это исправлено и в шаблонах и в добавлении в список\n" +
                "\nДобавлено: Добавлена локальная БД Room. " +
                "Загрузка данных производится из ROOM. " +
                "Общая сумма загружается и выводится на экран из ROOM\n" +
                "\nИзменено: Удалена передача данных из SharedViewModel");

        versionDescriptions.put("V 0.3", "V 0.3\n" +
                "Добавлено: Фикс багов. Фрагменты исправлены и работают между собой.\n" +
                "Экран расчёта и вывода финансовых расходов исправлен");

        versionDescriptions.put("V 0.2", "V 0.2\n" +
                "Добавлено: Свайп между страницами");

        versionDescriptions.put("V 0.1", "V 0.1\n" +
                "Добавлено: Категории внутри Профиль");

        versionNames = new ArrayList<>(versionDescriptions.keySet());
    }

    /**
     * Настраивает ArrayAdapter и устанавливает его для Spinner'а.
     */
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                versionNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        versionSpinner.setAdapter(adapter);
    }

    /**
     * Устанавливает слушатель для Spinner'а, который обновляет текст
     * при выборе новой версии.
     */
    private void setupSpinnerListener() {
        versionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedVersion = versionNames.get(position); // Получаем название выбранной версии
                String description = versionDescriptions.get(selectedVersion); // Получаем описание по названию

                if (description != null) {
                    infoText.setText(description);
                } else {
                    infoText.setText(selectedVersion + ". Информация отсутствует.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}