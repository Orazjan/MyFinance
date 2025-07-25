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
import com.example.myfinance.R;
import com.example.myfinance.data.AmountDatabase;
import com.example.myfinance.data.AmountRepository;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
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

    private List<String> operationTypeOptions;
    private ArrayAdapter<String> spinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.analiz_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        variant_for_display = view.findViewById(R.id.variant_for_display);

        financeChartViewModel = new ViewModelProvider(this).get(FinanceChartViewModel.class);
        AmountDatabase amdb = AmountDatabase.getDatabase(requireActivity().getApplication());
        AmountRepository amrepo = new AmountRepository(amdb.daoTotalAmount());
        AmountViewModel.TaskViewModelFactory amViewModelTaskFactory = new AmountViewModel.TaskViewModelFactory(amrepo);
        amountViewModel = new ViewModelProvider(requireActivity(), amViewModelTaskFactory).get(AmountViewModel.class);

        // Инициализируем список опций один раз
        operationTypeOptions = new ArrayList<>();
        operationTypeOptions.add("Общее");
        operationTypeOptions.add("Доход");
        operationTypeOptions.add("Расход");

        // Создаем адаптер здесь, но будем повторно устанавливать его в onResume
        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, operationTypeOptions);

        // Настраиваем слушатели один раз в onViewCreated
        variant_for_display.setOnItemClickListener((parent, v, position, id) -> {
            String selectedOption = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "AutoCompleteTextView selected: " + selectedOption);
            updateChartData(selectedOption);
        });

        variant_for_display.setOnClickListener(v -> {
            variant_for_display.showDropDown();
        });

        // Обзерверы настраиваются один раз
        amountViewModel.getSumma().observe(getViewLifecycleOwner(), totalExpensesFromDb -> {
            currentTotalExpenses = (totalExpensesFromDb != null) ? totalExpensesFromDb : 0.0;
            Log.d(TAG, "AmountViewModel Observer: currentTotalExpenses updated to " + currentTotalExpenses);
            updatePieChartCenterText();
        });

        amountViewModel.getLastAmount().observe(getViewLifecycleOwner(), totalBalanceFromDb -> {
            currentTotalBalance = (totalBalanceFromDb != null) ? totalBalanceFromDb : 0.0;
            Log.d(TAG, "AmountViewModel Observer: currentTotalBalance updated to " + currentTotalBalance);
            updatePieChartCenterText();
        });

        financeChartViewModel.getTotalIncomesSum().observe(getViewLifecycleOwner(), totalIncomesFromDb -> {
            currentTotalIncomes = (totalIncomesFromDb != null) ? totalIncomesFromDb : 0.0;
            Log.d(TAG, "FinanceChartViewModel Observer: currentTotalIncomes updated to " + currentTotalIncomes);
            updatePieChartCenterText();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Re-setting adapter and initial selection for variant_for_display.");
        // Повторно устанавливаем адаптер и начальный текст, чтобы убедиться, что он обновлен после смены темы
        if (spinnerAdapter != null) {
            variant_for_display.setAdapter(spinnerAdapter); // Повторно устанавливаем адаптер
        }
        if (!operationTypeOptions.isEmpty()) {
            variant_for_display.setText(operationTypeOptions.get(0), false); // Повторно устанавливаем начальный текст
            updateChartData(operationTypeOptions.get(0)); // Повторно обновляем данные графика
        }
    }

    /**
     * Обновляет данные круговой диаграммы на основе выбранного типа операции.
     *
     * @param selectedOption Выбранная опция ("Общее", "Доход", "Расход").
     */
    private void updateChartData(String selectedOption) {
        switch (selectedOption) {
            case "Общее":
                financeChartViewModel.getPieChartDataForAll().observe(getViewLifecycleOwner(), pieEntries -> {
                    if (pieEntries != null && !pieEntries.isEmpty()) {
                        setupPieChart(pieEntries, currentTotalBalance);
                    } else {
                        clearPieChart("Нет данных для общего графика");
                    }
                });
                break;
            case "Доход":
                financeChartViewModel.getPieChartDataForIncomes().observe(getViewLifecycleOwner(), pieEntries -> {
                    if (pieEntries != null && !pieEntries.isEmpty()) {
                        setupPieChart(pieEntries, currentTotalIncomes);
                    } else {
                        clearPieChart("Нет данных для графика доходов");
                    }
                });
                break;
            case "Расход":
                financeChartViewModel.getPieChartDataForExpenses().observe(getViewLifecycleOwner(), pieEntries -> {
                    if (pieEntries != null && !pieEntries.isEmpty()) {
                        setupPieChart(pieEntries, currentTotalExpenses);
                    } else {
                        clearPieChart("Нет данных для графика расходов");
                    }
                });
                break;
            default:
                clearPieChart("Выберите тип отображения");
                break;
        }
    }

    /**
     * Обновляет центральный текст круговой диаграммы на основе выбранного типа.
     */
    private void updatePieChartCenterText() {
        String selectedOption = variant_for_display.getText().toString();

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
            Log.d(TAG, "updatePieChartCenterText: Center text updated to " + valueToDisplay + " for option " + selectedOption);
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
        pieChart.setCenterText("");
        Log.d(TAG, "PieChart cleared with message: " + noDataMessage);
    }

    /**
     * Для круговой диаграммы
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
        pieChart.setCenterText(String.valueOf(centerValue));
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.animateY(1400);
        pieChart.invalidate();
        Log.d(TAG, "setupPieChart: Pie chart drawn with center text: " + centerValue);
    }

    /**
     * Для линейной диаграммы (для будущего использования)
     *
     * @param entries
     */
    private void setupLineChart(List<Entry> entries) {
        /*
        LineDataSet dataSet = new LineDataSet(entries, "Расходы по дням");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

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
        Log.d(TAG, "setupLineChart: Line chart drawn.");
        */
    }
}
