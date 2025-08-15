package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private AutoCompleteTextView versionSpinner;
    private List<String> versionNames;
    private Map<String, String> versionDescriptions;

    private static final String TAG = "VersionInfoFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.version_info_fragment, container, false); // Предполагаем, что это R.layout.some_new_fragment
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setupVersionData();
        setupSpinner();
        setupSpinnerListener();

        // Устанавливаем начальный текст для InfoText и AutoCompleteTextView
        if (!versionNames.isEmpty()) {
            String initialVersionName = versionNames.get(0);
            infoText.setText(versionDescriptions.get(initialVersionName));
            versionSpinner.setText(initialVersionName, false); // Устанавливаем текст и не фильтруем
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
     * Подготавливает названия версий для AutoCompleteTextView
     * и их подробные описания.
     */
    private void setupVersionData() {
        versionDescriptions = new LinkedHashMap<>();
        versionDescriptions.put(
                "V 0.9", "Добавлено:\n- Новый тип графика (линейный)\nИсправлено:\n\nИзменено: Полностью пересмотрен дизайн\n\n\n" + "0.9.1\n - Добавлено: В главной странице возможномть выбирать месяц"
        );
        versionDescriptions.put(
                "V 0.8", "Добавлено: \n- Страница анализа. Теперь можно посмотреть на что Вы тратите" +
                        "\n- Синхронизация с помощью базы данных\n- Выбор данных для анализа по типу операций\n- Тип операции Расход и доход" +
                        "\n-Исправлено: \n- Вкладки расходы и остатки\n -Шаблоны по умолчанию не работали\n- Страница профиль" +
                        "\nИзменено:\n- Логика синхронизации. \n- Дизайн в фрагменте шаблоны"
        );

        versionDescriptions.put("V 0.7", "Добавлено: \n-Кнопка Сохранить в профиле сохраняет и выходит\n-Расходы\n-Изменения имени и фамилии\n-Регистрация и авторизация\n-Время добавления записи в список\n-Страница Профиль изменена\n-Вход в учётную запись через логин и пароль\n" +
                "\n-Исправлено:\n-Кнопка Выход. При нажатии на неё выходит из аккаунта\n-Ориентация экрана зафиксирована на Портретной\n-Выход из приложение когда несколько раз перезаходишь в шаблоны\n-После выхода из второстепенных фрагментов Список финансов снова работает");

        versionDescriptions.put("V 0.6",
                "Исправлено: \n-Фикс багов.\n-Ориентация экрана теперь только портретная\n" +
                "-Страница настройки\n-Работа над темой\n" +
                "\nДобавлено: \n-Уведомление при нажатии на финанс\n-Долгое нажатие выводит окно для изменения\n-Комментарии к записям.\n-Возможность изменять категории, сумму и комментарий\n-При выборе варианта темы и вида валюты, данные добавляются в память устройства\n" +
                "\nИзменено: \n-Работа с вычислением основнойо суммы");

        versionDescriptions.put("V 0.5",
                "Исправлено:  \n-Фикс багов.\n" +
                "-При выборе категории если сумма равна 0.0 то открывается клавиатура\n" +
                "-В диалоговом окне можно устонавливать новую сумму и она загружается в ROOM\n" +
                "-Выбор списания раньше сумма автоматически не вставлялось\n" +
                "\nДобавлено: \n-Логотип\n" +
                "\nИзменено: \n-В фрагменте 'Шаблоны' изменено место суммы и причины. \n" +
                "-Удалено работа с Internal Storage\n" +
                "-Изменение суммы в категориях");

        versionDescriptions.put("V 0.4",
                "Исправлено: Фикс багов. При нажатии на сумму открывается диалог по изменению её. Это исправлено и в шаблонах и в добавлении в список\n" +
                "\nДобавлено: Добавлена локальная БД Room. " +
                "Загрузка данных производится из ROOM. " +
                "Общая сумма загружается и выводится на экран из ROOM\n" +
                "\nИзменено: Удалена передача данных из SharedViewModel");

        versionDescriptions.put("V 0.3",
                "Добавлено: Фикс багов. Фрагменты исправлены и работают между собой.\n" +
                "Экран расчёта и вывода финансовых расходов исправлен");

        versionDescriptions.put("V 0.2",
                "Добавлено: Свайп между страницами");

        versionDescriptions.put("V 0.1",
                "Добавлено: Категории внутри Профиль");

        versionNames = new ArrayList<>(versionDescriptions.keySet());
    }

    /**
     * Настраивает ArrayAdapter и устанавливает его для AutoCompleteTextView.
     */
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                versionNames
        );
        versionSpinner.setAdapter(adapter);
    }

    /**
     * Устанавливает слушатель для AutoCompleteTextView, который обновляет текст
     * при выборе новой версии.
     */
    private void setupSpinnerListener() {
        versionSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedVersion = versionNames.get(position);
            String description = versionDescriptions.get(selectedVersion);

            if (description != null) {
                infoText.setText(description);
            } else {
                infoText.setText(selectedVersion + ". Информация отсутствует.");
            }
            Log.d(TAG, "Version selected: " + selectedVersion);
        });

        versionSpinner.setOnClickListener(v -> versionSpinner.showDropDown());
    }
}
