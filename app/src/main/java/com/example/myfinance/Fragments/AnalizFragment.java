package com.example.myfinance.Fragments;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.FinanceChartViewModel;
import com.example.myfinance.Prevalent.Months;
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

    private Spinner spinnerForMonth;
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

        spinnerForMonth = rootView.findViewById(R.id.spinnerForMonth);

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

        // Настройка Spinner для выбора месяца
        setupMonthSpinner();

        return rootView;
    }

    /**
     * Вызывается при включении фрагмента.
     */
    @Override
    public void onResume() {
        super.onResume();
        int selectedPosition = spinnerForMonth.getSelectedItemPosition();
        if (selectedPosition == 0) {
            viewModel.showAllData();
        } else {
            viewModel.filterByMonth(Months.getMonthEn(selectedPosition));
        }
    }

    /**
     * Настраивает Spinner для выбора месяца.
     * Этот метод заменяет `uploadAndShowMonth` и `updateFinancesListForMonth`,
     * поскольку они содержали некорректную логику для этого фрагмента.
     */
    private void setupMonthSpinner() {
        String[] items = Months.getMonthsRu();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForMonth.setAdapter(adapter);

        // Устанавливаем текущий месяц в качестве значения по умолчанию
        spinnerForMonth.setSelection(0);

        spinnerForMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Если выбрана позиция 0 ("За всё время")
                if (position == 0) {
                    viewModel.showAllData();
                } else {
                    // Фильтруем по выбранному месяцу.
                    viewModel.filterByMonth(Months.getMonthEn(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем
            }
        });
    }

    /**
     * Настраивает наблюдателей (Observers) для LiveData.
     */
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

        // ЕСЛИ НЕТ ДАННЫХ, ВЫВОДИМ СООБЩЕНИЕ
        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Нет данных для отображения");
            pieChart.invalidate();
            return;
        }

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

    /**
     * @param entries
     */
    private void setupPieChartForIncomes(List<PieEntry> entries) {
        // ЕСЛИ НЕТ ДАННЫХ, ВЫВОДИМ СООБЩЕНИЕ
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Нет данных по доходам");
            pieChart.invalidate();
            return;
        }

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

    /**
     * @param entries
     */
    private void setupPieChartForExpenses(List<PieEntry> entries) {
        // ЕСЛИ НЕТ ДАННЫХ, ВЫВОДИМ СООБЩЕНИЕ
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Нет данных по расходам");
            pieChart.invalidate();
            return;
        }
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
     * Создает данные для LineChart.Если вдруг нужно будет показывать линейный график в разделе общее
     * @param entriesIncome
     * @param entriesExpense
     * @return
     */
    @NonNull
    private static LineData getLineData(ArrayList<Entry> entriesIncome, ArrayList<Entry> entriesExpense) {
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
        return data;
    }

    /**
     * @param dateSums
     */
    private void setupLineChartForIncomes(List<DateSum> dateSums) {
        // ЕСЛИ НЕТ ДАННЫХ, ВЫВОДИМ СООБЩЕНИЕ
        if (dateSums == null || dateSums.isEmpty()) {
            lineChart.clear();
            lineChart.setNoDataText("Нет данных по доходам");
            lineChart.invalidate();
            return;
        }

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

    /**
     * @param dateSums
     */
    private void setupLineChartForExpenses(List<DateSum> dateSums) {
        // ЕСЛИ НЕТ ДАННЫХ, ВЫВОДИМ СООБЩЕНИЕ
        if (dateSums == null || dateSums.isEmpty()) {
            lineChart.clear();
            lineChart.setNoDataText("Нет данных по расходам");
            lineChart.invalidate();
            return;
        }

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
