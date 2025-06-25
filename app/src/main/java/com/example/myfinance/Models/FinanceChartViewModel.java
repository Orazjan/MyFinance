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
    private final LiveData<List<PieEntry>> pieChartData;
    private final LiveData<List<DateSum>> lineChartDateSums;

    public FinanceChartViewModel(@NonNull Application application) {
        super(application);
        this.repository = ((MyApplication) application).getFinanceRepository();
        pieChartData = repository.getExpensesForPieChart();
        lineChartDateSums = repository.getExpensesDateSums();
    }

    public LiveData<List<PieEntry>> getPieChartData() {
        return pieChartData;
    }

    public LiveData<List<DateSum>> getLineChartDateSums() {
        return lineChartDateSums;
    }

    public void syncDataFromFirestore(FinanceRepository.SyncCallback callback) {
        repository.syncFromFirestore(callback);
    }
}