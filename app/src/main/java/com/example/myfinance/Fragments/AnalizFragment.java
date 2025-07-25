package com.example.myfinance.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.AmountViewModel;
import com.example.myfinance.Models.FinanceChartViewModel;
import com.example.myfinance.Prevalent.DateLabelFormatter;
import com.example.myfinance.R;
import com.example.myfinance.data.AmountDatabase;
import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.DateSum;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AnalizFragment extends Fragment {
    private PieChart pieChart;
    private LineChart lineChart;
    private FinanceChartViewModel financeChartViewModel;
    private AmountViewModel amountViewModel;
    private Double currentTotalExpenses = 0.0;
    private Double currentTotalIncomes = 0.0;
    private Double currentTotalBalance = 0.0;
    private List<String> xAxisLabels = new ArrayList<>();
    private AutoCompleteTextView variant_for_display;

    private static final String TAG = "AnalizFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.analiz_fragment, container, false);
    }

    /**
     * Вызывается после создания View.
     *
     * @param view               The View, возвращенный {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState Если не null, этот фрагмент восстанавливается
     *                           из предыдущего сохраненного состояния.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        lineChart = view.findViewById(R.id.lineChart);
        variant_for_display = view.findViewById(R.id.variant_for_display);

        // Инициализация ViewModels
        financeChartViewModel = new ViewModelProvider(this).get(FinanceChartViewModel.class);
        AmountDatabase amdb = AmountDatabase.getDatabase(requireActivity().getApplication());
        AmountRepository amrepo = new AmountRepository(amdb.daoTotalAmount());
        AmountViewModel.TaskViewModelFactory amViewModelTaskFactory = new AmountViewModel.TaskViewModelFactory(amrepo);
        amountViewModel = new ViewModelProvider(requireActivity(), amViewModelTaskFactory).get(AmountViewModel.class);

        // Настройка AutoCompleteTextView и обзерверов
        setupAutoCompleteTextViewAndObservers();
    }

    /**
     * Настраивает AutoCompleteTextView для выбора типа отображения и соответствующие LiveData обзерверы.
     */
    private void setupAutoCompleteTextViewAndObservers() {
        List<String> operationTypeOptions = new ArrayList<>();
        operationTypeOptions.add("Общее"); // Общий баланс
        operationTypeOptions.add("Доход"); // Только доходы
        operationTypeOptions.add("Расход"); // Только расходы
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, operationTypeOptions);
        variant_for_display.setAdapter(adapter);

        amountViewModel.getSumma().observe(getViewLifecycleOwner(), totalExpensesFromDb -> {
            currentTotalExpenses = (totalExpensesFromDb != null) ? totalExpensesFromDb : 0.0;
            Log.d(TAG, "AmountViewModel Observer: currentTotalExpenses updated to " + currentTotalExpenses);
            updatePieChartCenterText();
        });

        // Общий баланс (amount)
        amountViewModel.getLastAmount().observe(getViewLifecycleOwner(), totalBalanceFromDb -> {
            currentTotalBalance = (totalBalanceFromDb != null) ? totalBalanceFromDb : 0.0;
            Log.d(TAG, "AmountViewModel Observer: currentTotalBalance updated to " + currentTotalBalance);
            updatePieChartCenterText(); // Обновляем центральный текст круговой диаграммы
        });

        // --- Наблюдаем за общей суммой доходов из FinanceChartViewModel ---
        financeChartViewModel.getTotalIncomesSum().observe(getViewLifecycleOwner(), totalIncomesFromDb -> {
            currentTotalIncomes = (totalIncomesFromDb != null) ? totalIncomesFromDb : 0.0;
            Log.d(TAG, "FinanceChartViewModel Observer: currentTotalIncomes updated to " + currentTotalIncomes);
            updatePieChartCenterText(); // Обновляем центральный текст круговой диаграммы
        });

        // Добавлен OnClickListener для отображения выпадающего списка при клике на поле
        variant_for_display.setOnClickListener(v -> variant_for_display.showDropDown());

        // Слушатель для AutoCompleteTextView variant_for_display
        variant_for_display.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "AutoCompleteTextView selected: " + selectedOption);
            // Обновляем данные графика в зависимости от выбранной опции
            updateChartData(selectedOption);
        });

        // Устанавливаем начальный выбор и обновляем графики
        if (!operationTypeOptions.isEmpty()) {
            // Установка текста для AutoCompleteTextView
            variant_for_display.setText(operationTypeOptions.get(0), false); // Выбираем "Общее" по умолчанию, false предотвращает фильтрацию
            updateChartData(operationTypeOptions.get(0)); // Обновляем графики для "Общее"
        }
    }

    /**
     * Обновляет данные круговой и линейной диаграмм на основе выбранного типа операции.
     *
     * @param selectedOption Выбранная опция ("Общее", "Доход", "Расход").
     */
    private void updateChartData(String selectedOption) {
        switch (selectedOption) {
            case "Общее":
                financeChartViewModel.getPieChartDataForAll().observe(getViewLifecycleOwner(), pieEntries -> {
                    if (pieEntries != null && !pieEntries.isEmpty()) {
                        setupPieChart(pieEntries, currentTotalBalance); // Используем общий баланс
                    } else {
                        clearPieChart("Нет данных для общего кругового графика");
                    }
                });
                // Обновление LineChart для "Общее"
                financeChartViewModel.getAllTransactionsLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
                    if (dateSums != null && !dateSums.isEmpty()) {
                        setupLineChart(dateSums, "Все транзакции по дням");
                    } else {
                        clearLineChart("Нет данных для общего линейного графика");
                    }
                });
                break;
            case "Доход":
                financeChartViewModel.getPieChartDataForIncomes().observe(getViewLifecycleOwner(), pieEntries -> {
                    if (pieEntries != null && !pieEntries.isEmpty()) {
                        setupPieChart(pieEntries, currentTotalIncomes); // Используем общую сумму доходов
                    } else {
                        clearPieChart("Нет данных для кругового графика доходов");
                    }
                });
                // Обновление LineChart для "Доход"
                financeChartViewModel.getIncomesLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
                    if (dateSums != null && !dateSums.isEmpty()) {
                        setupLineChart(dateSums, "Доходы по дням");
                    } else {
                        clearLineChart("Нет данных для линейного графика доходов");
                    }
                });
                break;
            case "Расход":
                financeChartViewModel.getPieChartDataForExpenses().observe(getViewLifecycleOwner(), pieEntries -> {
                    if (pieEntries != null && !pieEntries.isEmpty()) {
                        setupPieChart(pieEntries, currentTotalExpenses); // Используем общую сумму расходов
                    } else {
                        clearPieChart("Нет данных для кругового графика расходов");
                    }
                });
                // Обновление LineChart для "Расход"
                financeChartViewModel.getExpensesLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
                    if (dateSums != null && !dateSums.isEmpty()) {
                        setupLineChart(dateSums, "Расходы по дням");
                    } else {
                        clearLineChart("Нет данных для линейного графика расходов");
                    }
                });
                break;
            default:
                clearPieChart("Выберите тип отображения");
                clearLineChart("Выберите тип отображения");
                break;
        }
    }

    /**
     * Обновляет центральный текст круговой диаграммы на основе текущего выбранного типа.
     */
    private void updatePieChartCenterText() {
        String selectedOption = variant_for_display.getText().toString();
        if (selectedOption == null || selectedOption.isEmpty()) { // Добавлена проверка на пустоту
            selectedOption = "Общее"; // Значение по умолчанию
        }

        Double valueToDisplay = 0.0;
        switch (selectedOption) {
            case "Общее":
                valueToDisplay = currentTotalBalance;
                break;
            case "Доход":
                valueToDisplay = currentTotalIncomes;
                break;
            case "Расход":
                valueToDisplay = currentTotalExpenses;
                break;
        }

        if (pieChart.getData() != null) {
            pieChart.setCenterText(String.valueOf(valueToDisplay));
            pieChart.invalidate();
            Log.d(TAG, "updatePieChartCenterText: Центральный текст обновлен до " + valueToDisplay + " для опции " + selectedOption);
        }
    }

    /**
     * Очищает круговую диаграмму и устанавливает текст "нет данных".
     *
     * @param noDataMessage Сообщение для отображения, когда данные недоступны.
     */
    private void clearPieChart(String noDataMessage) {
        pieChart.clear();
        pieChart.invalidate();
        pieChart.setNoDataText(noDataMessage);
        pieChart.setNoDataTextColor(Color.BLACK);
        pieChart.setCenterText(""); // Очищаем центральный текст при отсутствии данных
    }

    /**
     * Настраивает круговую диаграмму.
     *
     * @param entries Список PieEntry для диаграммы.
     * @param centerValue Общая сумма для отображения в центре диаграммы (может быть общим балансом, доходом или расходами).
     */
    private void setupPieChart(List<PieEntry> entries, Double centerValue) {
        PieDataSet dataSet = new PieDataSet(entries, "Категории");

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(10f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText(String.valueOf(centerValue)); // Используем переданное значение
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.animateY(1400);
        pieChart.invalidate();
    }

    /**
     * Очищает линейную диаграмму и устанавливает текст "нет данных".
     *
     * @param noDataMessage Сообщение для отображения, когда данные недоступны.
     */
    private void clearLineChart(String noDataMessage) {
        lineChart.clear();
        lineChart.invalidate();
        lineChart.setNoDataText(noDataMessage);
        lineChart.setNoDataTextColor(Color.BLACK);
    }

    /**
     * Настраивает линейную диаграмму на основе списка DateSum.
     *
     * @param dateSums Список DateSum (дата и сумма) для диаграммы.
     * @param label Метка для набора данных (например, "Расходы по дням", "Доходы по дням").
     */
    private void setupLineChart(List<DateSum> dateSums, String label) {
        List<Entry> entries = new ArrayList<>();
        xAxisLabels.clear(); // Очищаем старые метки оси X

        // Преобразуем DateSum в Entry и собираем метки оси X
        for (int i = 0; i < dateSums.size(); i++) {
            DateSum dateSum = dateSums.get(i);
            entries.add(new Entry(i, (float) dateSum.getTotal()));
            xAxisLabels.add(dateSum.getDate()); // Добавляем дату как метку оси X
        }

        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Настройка оси X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DateLabelFormatter(xAxisLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xAxisLabels.size(), true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);

        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1500);
        lineChart.invalidate();
    }
}
