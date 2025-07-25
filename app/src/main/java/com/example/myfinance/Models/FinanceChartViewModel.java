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
    private final LiveData<List<PieEntry>> expensesPieChartData;
    private final LiveData<List<PieEntry>> incomesPieChartData;
    private final LiveData<List<PieEntry>> allTransactionsPieChartData;

    // LiveData для различных типов LineChart данных
    private final LiveData<List<DateSum>> expensesLineChartDateSums;
    private final LiveData<List<DateSum>> incomesLineChartDateSums;
    private final LiveData<List<DateSum>> allTransactionsLineChartDateSums;


    private final LiveData<Double> totalIncomesSum;

    public FinanceChartViewModel(@NonNull Application application) {
        super(application);
        this.repository = ((MyApplication) application).getFinanceRepository();

        // Инициализация LiveData для разных типов графиков
        expensesPieChartData = repository.getExpensesForPieChart();
        incomesPieChartData = repository.getIncomesForPieChart();
        allTransactionsPieChartData = repository.getAllTransactionsForPieChart();

        // Инициализация LiveData для линейных графиков
        expensesLineChartDateSums = repository.getExpensesDateSums();
        incomesLineChartDateSums = repository.getIncomesDateSums();
        allTransactionsLineChartDateSums = repository.getAllTransactionsDateSums();

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

    /**
     * Возвращает LiveData с данными для линейного графика расходов по датам.
     */
    public LiveData<List<DateSum>> getExpensesLineChartDateSums() {
        return expensesLineChartDateSums;
    }

    /**
     * НОВОЕ: Возвращает LiveData с данными для линейного графика доходов по датам.
     */
    public LiveData<List<DateSum>> getIncomesLineChartDateSums() {
        return incomesLineChartDateSums;
    }

    /**
     * НОВОЕ: Возвращает LiveData с данными для линейного графика всех транзакций по датам.
     */
    public LiveData<List<DateSum>> getAllTransactionsLineChartDateSums() {
        return allTransactionsLineChartDateSums;
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
