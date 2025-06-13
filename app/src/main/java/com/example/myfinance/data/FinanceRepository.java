package com.example.myfinance.data;

import androidx.lifecycle.LiveData;

import com.example.myfinance.DAO.DAOFinances;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceRepository {
    private final DAOFinances daoFinances;
    private LiveData<List<Finances>> allFinances;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExeutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public FinanceRepository(DAOFinances financesDao) {
        this.daoFinances = financesDao;
        this.allFinances = financesDao.getAllFinances();
    }

    public void insert(Finances finances) {
        databaseWriteExeutor.execute(() -> daoFinances.insert(finances));
    }

    public void update(Finances finances) {
        databaseWriteExeutor.execute(() -> daoFinances.update(finances));
    }

    public void delete(Finances finances) {
        databaseWriteExeutor.execute(() -> daoFinances.delete(finances));
    }

    public LiveData<List<String>> getComments() {
        return daoFinances.getComments();
    }

    public void deleteAll() {
        databaseWriteExeutor.execute(daoFinances::deleteAll);
    }

    public LiveData<List<Finances>> getAllFinances() {
        return allFinances;
    }

    public LiveData<Finances> getFinancesById(int id) {
        return daoFinances.getFinancesById(id);
    }

    public int getFinanceCount() {
        return daoFinances.getFinanceCount();
    }

    public LiveData<List<Date>> getDateById(int id) {
        return daoFinances.getDateById(id);
    }
}
