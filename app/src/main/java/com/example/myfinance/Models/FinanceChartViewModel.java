package com.example.myfinance.Models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myfinance.MyApplication;
import com.example.myfinance.data.DateSum;
import com.example.myfinance.data.FinanceRepository;
import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

public class FinanceChartViewModel extends AndroidViewModel {
    private final FinanceRepository repository;

    // LiveData для различных типов PieChart данных
    private final LiveData<List<PieEntry>> expensesPieChartData; // Данные для расходов
    private final LiveData<List<PieEntry>> incomesPieChartData;  // Данные для доходов
    private final LiveData<List<PieEntry>> allTransactionsPieChartData; // Данные для всех транзакций (общее)

    private final LiveData<List<DateSum>> lineChartDateSums;

    private final LiveData<Double> totalIncomesSum;

    public FinanceChartViewModel(@NonNull Application application) {
        super(application);
        this.repository = ((MyApplication) application).getFinanceRepository();

        // Инициализация LiveData для разных типов графиков
        expensesPieChartData = repository.getExpensesForPieChart();
        incomesPieChartData = repository.getIncomesForPieChart();
        allTransactionsPieChartData = repository.getAllTransactionsForPieChart();

        lineChartDateSums = repository.getExpensesDateSums();

        totalIncomesSum = repository.getTotalIncomesSum();
    }

    /**
     * Возвращает LiveData с данными для кругового графика расходов.
     */
    public LiveData<List<PieEntry>> getPieChartDataForExpenses() {
        return expensesPieChartData;
    }

    /**
     * Возвращает LiveData с данными для кругового графика доходов.
     */
    public LiveData<List<PieEntry>> getPieChartDataForIncomes() {
        return incomesPieChartData;
    }

    /**
     * Возвращает LiveData с данными для кругового графика всех транзакций (общее).
     */
    public LiveData<List<PieEntry>> getPieChartDataForAll() {
        return allTransactionsPieChartData;
    }

    public LiveData<List<DateSum>> getLineChartDateSums() {
        return lineChartDateSums;
    }

    /**
     * Возвращает LiveData с общей суммой всех доходов.
     */
    public LiveData<Double> getTotalIncomesSum() {
        return totalIncomesSum;
    }

    public void syncDataFromFirestore(FinanceRepository.SyncCallback callback) {
        repository.syncFromFirestore(callback);
    }
}
