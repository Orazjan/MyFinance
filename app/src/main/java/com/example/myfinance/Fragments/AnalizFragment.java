package com.example.myfinance.Fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.FinanceChartViewModel;
import com.example.myfinance.R;
import com.example.myfinance.data.DateSum;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AnalizFragment extends Fragment {

    private PieChart pieChart;
    private LineChart lineChart;
    private AutoCompleteTextView variantForDisplay;
    private String[] variants;
    private FinanceChartViewModel viewModel;

    // Переменные для хранения данных, полученных из ViewModel
    private List<PieEntry> expensesPieData;
    private List<PieEntry> incomesPieData;
    private List<PieEntry> allTransactionsPieData;

    private List<DateSum> expensesLineData;
    private List<DateSum> incomesLineData;

    // Новые переменные для хранения сумм доходов и расходов
    private Double totalIncomesSum = 0.0;
    private Double totalExpensesSum = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.analiz_fragment, container, false);

        // Инициализация графиков
        pieChart = rootView.findViewById(R.id.pieChart);
        lineChart = rootView.findViewById(R.id.lineChart);

        // Инициализация выпадающего списка
        variantForDisplay = rootView.findViewById(R.id.variant_for_display);

        // Получение вариантов из файла ресурсов (arrays.xml)
        variants = getResources().getStringArray(R.array.display_variants_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, variants);
        variantForDisplay.setAdapter(adapter);

        // Установка "Общее" в качестве значения по умолчанию
        variantForDisplay.setText(variants[0], false);

        // Добавление обработчика кликов для показа выпадающего списка
        variantForDisplay.setOnClickListener(v -> variantForDisplay.showDropDown());

        // Получение экземпляра ViewModel
        viewModel = new ViewModelProvider(this).get(FinanceChartViewModel.class);

        // Настройка наблюдателей (Observers) для LiveData
        setupObservers();

        // Добавляем слушатель для обработки выбора элемента из списка
        variantForDisplay.setOnItemClickListener((parent, view, position, id) -> {
            String selectedVariant = variants[position];
            updateAnalysis(selectedVariant);
        });

        return rootView;
    }

    private void setupObservers() {
        // Наблюдатель для данных кругового графика расходов
        viewModel.getPieChartDataForExpenses().observe(getViewLifecycleOwner(), pieEntries -> {
            this.expensesPieData = pieEntries;
            updateAnalysis(variantForDisplay.getText().toString());
        });

        // Наблюдатель для данных кругового графика доходов
        viewModel.getPieChartDataForIncomes().observe(getViewLifecycleOwner(), pieEntries -> {
            this.incomesPieData = pieEntries;
            updateAnalysis(variantForDisplay.getText().toString());
        });

        // Наблюдатель для данных кругового графика всех транзакций
        viewModel.getPieChartDataForAll().observe(getViewLifecycleOwner(), pieEntries -> {
            this.allTransactionsPieData = pieEntries;
            updateAnalysis(variantForDisplay.getText().toString());
        });

        // Наблюдатель для данных линейного графика расходов
        viewModel.getExpensesLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
            this.expensesLineData = dateSums;
            // Вычисляем общую сумму расходов
            this.totalExpensesSum = dateSums.stream().mapToDouble(DateSum::getTotal).sum();
            updateAnalysis(variantForDisplay.getText().toString());
        });

        // Наблюдатель для данных линейного графика доходов
        viewModel.getIncomesLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
            this.incomesLineData = dateSums;
            updateAnalysis(variantForDisplay.getText().toString());
        });

        // Наблюдатель для общей суммы доходов
        viewModel.getTotalIncomesSum().observe(getViewLifecycleOwner(), totalSum -> {
            this.totalIncomesSum = totalSum != null ? totalSum : 0.0;
            updateAnalysis(variantForDisplay.getText().toString());
        });
    }

    /**
     * Обновляет диаграммы на основе выбранного варианта и имеющихся данных.
     * @param selectedVariant Выбранный вариант ("Общее", "Доходы", "Расходы").
     */
    private void updateAnalysis(String selectedVariant) {
        // Очистка предыдущих данных
        pieChart.clear();
        lineChart.clear();

        switch (selectedVariant) {
            case "Общее":
                // Передаем общие суммы доходов и расходов для круговой диаграммы
                setupPieChartForGeneral(totalIncomesSum, totalExpensesSum);
                // Используем отдельные списки данных для сравнения на линейном графике
//                setupLineChartForGeneralComparison(incomesLineData, expensesLineData);
                lineChart.setVisibility(INVISIBLE);
                break;
            case "Доходы":
                lineChart.setVisibility(VISIBLE);
                setupPieChartForIncomes(incomesPieData);
                setupLineChartForIncomes(incomesLineData);
                break;
            case "Расходы":
                lineChart.setVisibility(VISIBLE);
                setupPieChartForExpenses(expensesPieData);
                setupLineChartForExpenses(expensesLineData);
                break;
        }

        // Обновление диаграмм
        pieChart.invalidate();
        lineChart.invalidate();
    }

    // --- Методы для настройки PieChart ---

    /**
     * Настраивает круговой график для общего анализа доходов и расходов.
     * @param totalIncomes Общая сумма доходов.
     * @param totalExpenses Общая сумма расходов.
     */
    private void setupPieChartForGeneral(Double totalIncomes, Double totalExpenses) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (totalIncomes > 0) {
            entries.add(new PieEntry(totalIncomes.floatValue(), "Доходы"));
        }
        if (totalExpenses > 0) {
            entries.add(new PieEntry(totalExpenses.floatValue(), "Расходы"));
        }

        // Если данных нет, ничего не строим
        if (entries.isEmpty()) return;

        PieDataSet dataSet = new PieDataSet(entries, "Общий анализ");
        dataSet.setColors(new int[]{Color.GREEN, Color.RED});
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Общий анализ");
        pieChart.setCenterTextSize(16f);
    }

    private void setupPieChartForIncomes(List<PieEntry> entries) {
        if (entries == null) return;
        PieDataSet dataSet = new PieDataSet(entries, "Доходы по категориям");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Анализ доходов");
        pieChart.setCenterTextSize(16f);
    }

    private void setupPieChartForExpenses(List<PieEntry> entries) {
        if (entries == null) return;
        PieDataSet dataSet = new PieDataSet(entries, "Расходы по категориям");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Анализ расходов");
        pieChart.setCenterTextSize(16f);
    }

    // --- Методы для настройки LineChart ---

    /**
     * Настраивает линейный график для сравнения доходов и расходов.
     * @param incomesList Список данных для доходов по датам.
     * @param expensesList Список данных для расходов по датам.
     */
    private void setupLineChartForGeneralComparison(List<DateSum> incomesList, List<DateSum> expensesList) {
        // Если данные отсутствуют, ничего не строим
        if (incomesList == null && expensesList == null) return;

        ArrayList<Entry> entriesIncome = new ArrayList<>();
        if (incomesList != null) {
            for (int i = 0; i < incomesList.size(); i++) {
                entriesIncome.add(new Entry(i, (float) incomesList.get(i).getTotal()));
            }
        }

        ArrayList<Entry> entriesExpense = new ArrayList<>();
        if (expensesList != null) {
            for (int i = 0; i < expensesList.size(); i++) {
                entriesExpense.add(new Entry(i, (float) expensesList.get(i).getTotal()));
            }
        }

        LineDataSet dataSetIncome = new LineDataSet(entriesIncome, "Доходы");
        dataSetIncome.setColor(Color.GREEN);
        dataSetIncome.setCircleColor(Color.GREEN);
        dataSetIncome.setLineWidth(2f);
        dataSetIncome.setDrawValues(false);

        LineDataSet dataSetExpense = new LineDataSet(entriesExpense, "Расходы");
        dataSetExpense.setColor(Color.RED);
        dataSetExpense.setCircleColor(Color.RED);
        dataSetExpense.setLineWidth(2f);
        dataSetExpense.setDrawValues(false);

        LineData data = new LineData(dataSetIncome, dataSetExpense);
        lineChart.setData(data);
        lineChart.getDescription().setText("Сравнение доходов и расходов");
        lineChart.getDescription().setTextSize(12f);
    }

    private void setupLineChartForIncomes(List<DateSum> dateSums) {
        if (dateSums == null) return;
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dateSums.size(); i++) {
            entries.add(new Entry(i, (float) dateSums.get(i).getTotal()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Динамика доходов");
        dataSet.setColor(Color.GREEN);
        dataSet.setCircleColor(Color.GREEN);
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.getDescription().setText("Динамика доходов");
        lineChart.getDescription().setTextSize(12f);
    }

    private void setupLineChartForExpenses(List<DateSum> dateSums) {
        if (dateSums == null) return;
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dateSums.size(); i++) {
            entries.add(new Entry(i, (float) dateSums.get(i).getTotal()));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Динамика расходов");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.getDescription().setText("Динамика расходов");
        lineChart.getDescription().setTextSize(12f);
    }
}
