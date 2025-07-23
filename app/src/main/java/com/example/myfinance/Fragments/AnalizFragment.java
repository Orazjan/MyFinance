package com.example.myfinance.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
    private List<String> xAxisLabels = new ArrayList<>(); // Для line chart
    private Spinner variant_for_display;

    private static final String TAG = "AnalizFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.analiz_fragment, container, false);
    }

    /**
     * Called after the View is created.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
//        lineChart = view.findViewById(R.id.lineChart); // Initialize LineChart
        variant_for_display = view.findViewById(R.id.variant_for_display);

        financeChartViewModel = new ViewModelProvider(this).get(FinanceChartViewModel.class);
        AmountDatabase amdb = AmountDatabase.getDatabase(requireActivity().getApplication());
        AmountRepository amrepo = new AmountRepository(amdb.daoTotalAmount());
        AmountViewModel.TaskViewModelFactory amViewModelTaskFactory = new AmountViewModel.TaskViewModelFactory(amrepo);
        amountViewModel = new ViewModelProvider(requireActivity(), amViewModelTaskFactory).get(AmountViewModel.class);

        setupSpinnersAndObservers();
    }

    /**
     * Sets up the Spinner for display type selection and corresponding LiveData observers.
     */
    private void setupSpinnersAndObservers() {
        List<String> operationTypeOptions = new ArrayList<>();
        operationTypeOptions.add("Общее"); // Overall balance
        operationTypeOptions.add("Доход"); // Income only
        operationTypeOptions.add("Расход"); // Expense only
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, operationTypeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        variant_for_display.setAdapter(adapter);

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

        variant_for_display.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Spinner selected: " + selectedOption);
                updateChartData(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateChartData("Общее");
            }
        });
    }

    /**
     * Updates PieChart data based on the selected operation type.
     *
     * @param selectedOption Selected option ("Overall", "Income", "Expense").
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
     * Updates the center text of the pie chart based on the currently selected type.
     */
    private void updatePieChartCenterText() {
        String selectedOption = (String) variant_for_display.getSelectedItem();
        if (selectedOption == null) {
            selectedOption = "Общее";
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
            Log.d(TAG, "updatePieChartCenterText: Center text updated to " + valueToDisplay + " for option " + selectedOption);
        }
    }

    /**
     * Clears the PieChart and sets "no data" text.
     *
     * @param noDataMessage Message to display when no data is available.
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
     * For the pie chart
     *
     * @param entries List of PieEntry for the chart.
     * @param centerValue Total sum to display in the center of the chart (can be overall balance, income, or expenses).
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
     * For the line chart (for future use)
     *
     * @param entries
     */
    private void setupLineChart(List<Entry> entries) {
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

        // X-axis setup
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
    }
}
