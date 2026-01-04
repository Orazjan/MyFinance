package com.example.myfinance.Fragments;

import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.FinanceChartViewModel;
import com.example.myfinance.Prevalent.Months;
import com.example.myfinance.R;
import com.example.myfinance.data.DateSum;
import com.example.myfinance.data.Finances;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AnalizFragment extends Fragment implements OnChartValueSelectedListener {

    private AutoCompleteTextView spinnerForMonth;
    private PieChart pieChart;
    private LineChart lineChart;
    private AutoCompleteTextView variantForDisplay;
    private TextView tvTotalExpenses, tvAverageExpense, tvBalanceStats;
    private String[] variants;
    private FinanceChartViewModel viewModel;

    private List<PieEntry> expensesPieData;
    private List<PieEntry> incomesPieData;
    private List<PieEntry> allTransactionsPieData;
    private List<DateSum> expensesLineData;
    private List<DateSum> incomesLineData;

    private Double totalIncomesSum = 0.0;
    private Double totalExpensesSum = 0.0;

    private List<Finances> allIncomes;
    private List<Finances> allExpenses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.analiz_fragment, container, false);

        // Инициализация View
        pieChart = rootView.findViewById(R.id.pieChart);
        lineChart = rootView.findViewById(R.id.lineChart);
        spinnerForMonth = rootView.findViewById(R.id.spinnerForMonth);
        variantForDisplay = rootView.findViewById(R.id.variant_for_display);

        // Инициализация TextView статистики
        tvTotalExpenses = rootView.findViewById(R.id.tvTotalExpenses);
        tvAverageExpense = rootView.findViewById(R.id.tvAverageExpense);
        tvBalanceStats = rootView.findViewById(R.id.tvBalanceStats);

        // Базовая настройка PieChart (делаем его бубликом)
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);

        // Настройка Spinner варианта отображения
        variants = getResources().getStringArray(R.array.display_variants_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, variants);
        variantForDisplay.setAdapter(adapter);
        variantForDisplay.setText(variants[0], false);
        variantForDisplay.setOnClickListener(v -> variantForDisplay.showDropDown());
        variantForDisplay.setOnItemClickListener((parent, view, position, id) -> {
            String selectedVariant = variants[position];
            updateAnalysis(selectedVariant);
        });

        // ViewModel и Observers
        viewModel = new ViewModelProvider(this).get(FinanceChartViewModel.class);
        setupObservers();

        setupMonthSpinner();

        // Слушатель графиков
        pieChart.setOnChartValueSelectedListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentText = spinnerForMonth.getText().toString();

        String[] items = Months.getMonthsRu();

        if (currentText.equals(items[0])) {
            viewModel.showAllData();
        } else {
            for (int i = 1; i < items.length; i++) {
                if (currentText.equals(items[i])) {
                    viewModel.filterByMonth(Months.getMonthEn(i));
                    break;
                }
            }
        }
    }

    private void setupMonthSpinner() {
        String[] items = Months.getMonthsRu();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                items
        );

        spinnerForMonth.setAdapter(adapter);

        // Устанавливаем текст по умолчанию (индекс 0 - "За всё время")
        spinnerForMonth.setText(items[0], false);

        // Открытие списка по клику
        spinnerForMonth.setOnClickListener(v -> spinnerForMonth.showDropDown());

        // Обработка выбора (для AutoCompleteTextView используется OnItemClickListener)
        spinnerForMonth.setOnItemClickListener((parent, view, position, id) -> {
            // position совпадает с индексом в массиве items
            if (position == 0) {
                // Выбрано "За всё время"
                viewModel.showAllData();
            } else {
                viewModel.filterByMonth(Months.getMonthEn(position));
            }
        });
    }

    private void setupObservers() {
        viewModel.getPieChartDataForExpenses().observe(getViewLifecycleOwner(), pieEntries -> {
            this.expensesPieData = pieEntries;
            updateAnalysis(variantForDisplay.getText().toString());
        });
        viewModel.getPieChartDataForIncomes().observe(getViewLifecycleOwner(), pieEntries -> {
            this.incomesPieData = pieEntries;
            updateAnalysis(variantForDisplay.getText().toString());
        });
        viewModel.getPieChartDataForAll().observe(getViewLifecycleOwner(), pieEntries -> {
            this.allTransactionsPieData = pieEntries;
            updateAnalysis(variantForDisplay.getText().toString());
        });
        viewModel.getExpensesLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
            this.expensesLineData = dateSums;
            this.totalExpensesSum = dateSums.stream().mapToDouble(DateSum::getTotal).sum();
            updateStatsUI(); // Обновляем цифры статистики
            updateAnalysis(variantForDisplay.getText().toString());
        });
        viewModel.getIncomesLineChartDateSums().observe(getViewLifecycleOwner(), dateSums -> {
            this.incomesLineData = dateSums;
            updateAnalysis(variantForDisplay.getText().toString());
        });
        viewModel.getTotalIncomesSum().observe(getViewLifecycleOwner(), totalSum -> {
            this.totalIncomesSum = totalSum != null ? totalSum : 0.0;
            updateStatsUI();
            updateAnalysis(variantForDisplay.getText().toString());
        });
        viewModel.getAllIncomes().observe(getViewLifecycleOwner(), incomes -> this.allIncomes = incomes);
        viewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> this.allExpenses = expenses);
    }

    /**
     * Обновляет текстовые поля статистики (Всего, Среднее, Баланс)
     */
    private void updateStatsUI() {
        if (tvTotalExpenses == null) return;

        // Всего расходов
        tvTotalExpenses.setText(String.format(Locale.getDefault(), "%.1f", totalExpensesSum));

        // Баланс (Доходы - Расходы)
        double balance = totalIncomesSum - totalExpensesSum;
        tvBalanceStats.setText(String.format(Locale.getDefault(), "%.1f", balance));
        if (balance < 0) tvBalanceStats.setTextColor(Color.RED);
        else tvBalanceStats.setTextColor(Color.parseColor("#4CAF50")); // Green

        // 3. Средний расход
        double average = 0.0;
        if (allExpenses != null && !allExpenses.isEmpty()) {
            average = totalExpensesSum / allExpenses.size();
        }
        tvAverageExpense.setText(String.format(Locale.getDefault(), "%.1f", average));
    }

    private void updateAnalysis(String selectedVariant) {
        pieChart.clear();
        lineChart.clear();

        switch (selectedVariant) {
            case "Общее":
                setupPieChartForGeneral(totalIncomesSum, totalExpensesSum);
                lineChart.setVisibility(VISIBLE);
                setupLineChartForGeneralComparison(incomesLineData, expensesLineData);
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
        pieChart.invalidate();
        lineChart.invalidate();
    }

    /**
     * Генерирует SpannableString для центрированного текста в PieChart.
     * @param title
     * @param amount
     * @param subtitle
     * @return
     */
    private SpannableString generateCenterSpannableText(String title, double amount, String subtitle) {
        String amountStr = String.format(Locale.getDefault(), "%.1f", amount);
        String fullText = title + "\n" + amountStr + "\n" + subtitle;

        SpannableString s = new SpannableString(fullText);

        int titleLen = title.length();
        int amountLen = amountStr.length();
        int subtitleLen = subtitle.length();

        int startAmount = titleLen + 1;
        int endAmount = startAmount + amountLen;
        int startSubtitle = endAmount + 1;

        // Стиль Заголовка (верх)
        s.setSpan(new RelativeSizeSpan(0.9f), 0, titleLen, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, titleLen, 0);

        // Стиль Суммы (центр) - Жирный и Крупный
        s.setSpan(new RelativeSizeSpan(1.8f), startAmount, endAmount, 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), startAmount, endAmount, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), startAmount, endAmount, 0);

        // Стиль Подписи (низ)
        s.setSpan(new RelativeSizeSpan(0.9f), startSubtitle, fullText.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), startSubtitle, fullText.length(), 0);

        return s;
    }

    /**
     * @param totalIncomes
     * @param totalExpenses
     */
    private void setupPieChartForGeneral(Double totalIncomes, Double totalExpenses) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Формируем доли для визуализации (Зеленая и Красная часть)
        if (totalIncomes > 0) entries.add(new PieEntry(totalIncomes.floatValue(), "Доходы"));
        if (totalExpenses > 0) entries.add(new PieEntry(totalExpenses.floatValue(), "Расходы"));

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Нет данных");
            // Если данных нет, пишем 0
            pieChart.setCenterText(generateCenterSpannableText("Расходы", 0.0, "Общий анализ"));
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#F44336")); // Зеленый, Красный
        dataSet.setValueTextSize(0f); // Убираем текст значений с самого графика для чистоты
        dataSet.setDrawValues(false); // Или true, для процентоы на дугах

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);

        // Здесь выводим именно РАСХОДЫ в центре
        pieChart.setCenterText(generateCenterSpannableText("Общий анализ", totalExpenses, "Расходы"));

        pieChart.invalidate(); // Принудительная перерисовка
    }

    /**
     * @param entries
     */
    private void setupPieChartForExpenses(List<PieEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Нет данных");
            pieChart.setCenterText(generateCenterSpannableText("Расходы", 0.0, "Всего"));
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(new PieData(dataSet));
        pieChart.setUsePercentValues(true);

        pieChart.setCenterText(generateCenterSpannableText("Категории", totalExpensesSum, "Расходы"));

        pieChart.invalidate();
    }

    /**
     * @param entries
     */
    private void setupPieChartForIncomes(List<PieEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Нет данных");
            pieChart.setCenterText(generateCenterSpannableText("Доходы", 0.0, "Всего"));
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(new PieData(dataSet));
        pieChart.setUsePercentValues(true);

        pieChart.setCenterText(generateCenterSpannableText("Категории", totalIncomesSum, "Доходы"));

        pieChart.invalidate();
    }

    private void setupLineChartForGeneralComparison(List<DateSum> incomesList, List<DateSum> expensesList) {
        if ((incomesList == null || incomesList.isEmpty()) && (expensesList == null || expensesList.isEmpty())) {
            lineChart.setNoDataText("Нет данных");
            return;
        }
        ArrayList<Entry> entriesIncome = new ArrayList<>();
        if (incomesList != null) for (int i = 0; i < incomesList.size(); i++)
            entriesIncome.add(new Entry(i, (float) incomesList.get(i).getTotal()));

        ArrayList<Entry> entriesExpense = new ArrayList<>();
        if (expensesList != null) for (int i = 0; i < expensesList.size(); i++)
            entriesExpense.add(new Entry(i, (float) expensesList.get(i).getTotal()));

        lineChart.setData(getLineData(entriesIncome, entriesExpense));
        lineChart.getDescription().setText("Доходы vs Расходы");
    }

    @NonNull
    private static LineData getLineData(ArrayList<Entry> entriesIncome, ArrayList<Entry> entriesExpense) {
        LineDataSet dataSetIncome = new LineDataSet(entriesIncome, "Доходы");
        dataSetIncome.setColor(Color.parseColor("#4CAF50"));
        dataSetIncome.setCircleColor(Color.parseColor("#4CAF50"));
        dataSetIncome.setLineWidth(2f);
        dataSetIncome.setDrawValues(false);

        LineDataSet dataSetExpense = new LineDataSet(entriesExpense, "Расходы");
        dataSetExpense.setColor(Color.parseColor("#F44336"));
        dataSetExpense.setCircleColor(Color.parseColor("#F44336"));
        dataSetExpense.setLineWidth(2f);
        dataSetExpense.setDrawValues(false);

        return new LineData(dataSetIncome, dataSetExpense);
    }

    private void setupLineChartForIncomes(List<DateSum> dateSums) {
        if (dateSums == null || dateSums.isEmpty()) return;
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dateSums.size(); i++)
            entries.add(new Entry(i, (float) dateSums.get(i).getTotal()));
        LineDataSet dataSet = new LineDataSet(entries, "Доходы");
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setCircleColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawValues(false);
        lineChart.setData(new LineData(dataSet));
        lineChart.getDescription().setText("Динамика доходов");
    }

    private void setupLineChartForExpenses(List<DateSum> dateSums) {
        if (dateSums == null || dateSums.isEmpty()) return;
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dateSums.size(); i++)
            entries.add(new Entry(i, (float) dateSums.get(i).getTotal()));
        LineDataSet dataSet = new LineDataSet(entries, "Расходы");
        dataSet.setColor(Color.parseColor("#F44336"));
        dataSet.setCircleColor(Color.parseColor("#F44336"));
        dataSet.setDrawValues(false);
        lineChart.setData(new LineData(dataSet));
        lineChart.getDescription().setText("Динамика расходов");
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e instanceof PieEntry) {
            PieEntry selectedEntry = (PieEntry) e;
            String selectedCategory = selectedEntry.getLabel();
            String currentVariant = variantForDisplay.getText().toString();
            List<Finances> transactions = new ArrayList<>();

            if ("Доходы".equals(currentVariant) && allIncomes != null) {
                transactions = allIncomes.stream().filter(f -> f.getFinanceResult() != null && f.getFinanceResult().equals(selectedCategory)).collect(Collectors.toList());
            } else if ("Расходы".equals(currentVariant) && allExpenses != null) {
                transactions = allExpenses.stream().filter(f -> f.getFinanceResult() != null && f.getFinanceResult().equals(selectedCategory)).collect(Collectors.toList());
            }

            if (!transactions.isEmpty()) {
                TransactionDetailsBottomSheetFragment bottomSheetFragment = new TransactionDetailsBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("transactions", (Serializable) new ArrayList<>(transactions));
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
            }
        }
    }

    @Override
    public void onNothingSelected() {
    }
}