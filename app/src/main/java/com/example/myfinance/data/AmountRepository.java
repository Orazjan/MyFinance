package com.example.myfinance.data;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.myfinance.DAO.DAOTotalAmount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AmountRepository {
    private final DAOTotalAmount DAOtotalAmount;

    private LiveData<Double> totalSumOfAmounts;
    private LiveData<Double> lastAmount;
    private LiveData<Double> summa;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public AmountRepository(DAOTotalAmount daOtotalAmount) {
        this.DAOtotalAmount = daOtotalAmount;
        this.lastAmount = DAOtotalAmount.getLastAmount();
        this.summa = DAOtotalAmount.getSumma();
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(TotalAmount totalAmount) {
        databaseWriteExecutor.execute(() -> DAOtotalAmount.insert(totalAmount));
    }

    public void update(TotalAmount totalAmount) {
        databaseWriteExecutor.execute(() -> DAOtotalAmount.update(totalAmount));
    }

    public LiveData<Double> getTotalSumOfAmounts() {
        return totalSumOfAmounts;
    }

    public LiveData<Double> getLastAmount() {
        return lastAmount;
    }

    public LiveData<Double> getSumma() {
        return summa;
    }

    public void deleteAll() {
        databaseWriteExecutor.execute(DAOtotalAmount::deleteAll);
    }

    public void delete(TotalAmount totalAmount) {
        databaseWriteExecutor.execute(() -> DAOtotalAmount.delete(totalAmount));
    }

}