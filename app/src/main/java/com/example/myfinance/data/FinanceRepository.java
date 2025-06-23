package com.example.myfinance.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.myfinance.DAO.DAOFinances;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
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

    public LiveData<List<String>> getDateById(int id) {
        return daoFinances.getDateById(id);
    }

    /**
     * Для кругового графика
     * Теперь получает List<CategorySum>
     *
     * @return LiveData с данными для PieChart
     */
    public LiveData<List<PieEntry>> getExpensesForPieChart() {
        MediatorLiveData<List<PieEntry>> result = new MediatorLiveData<>();
        result.addSource(daoFinances.getExpensesByCategory(), categorySums -> {
            List<PieEntry> entries = new ArrayList<>();
            if (categorySums != null) {
                for (CategorySum item : categorySums) {
                    if (item.getTotal() > 0) {
                        entries.add(new PieEntry(
                                (float) item.getTotal(),
                                item.getCategory()
                        ));
                    }
                }
            }
            result.setValue(entries);
        });
        return result;
    }

    /**
     * Для линейного графика
     * Теперь получает List<DateSum>
     *
     * @return LiveData с данными для LineChart
     */
    public LiveData<List<Entry>> getExpensesForLineChart() {
        MediatorLiveData<List<Entry>> result = new MediatorLiveData<>();
        result.addSource(daoFinances.getExpensesByDate(), dateSums -> {
            List<Entry> entries = new ArrayList<>();

        });
        return null;
    }

    public LiveData<List<DateSum>> getExpensesDateSums() {
        return daoFinances.getExpensesByDate();
    }


    /**
     * Для кругового графика (доходы по категориям)
     * Теперь получает List<CategorySum>
     *
     * @return LiveData с данными для PieChart
     */
    public LiveData<List<PieEntry>> getIncomeForPieChart() {
        MediatorLiveData<List<PieEntry>> result = new MediatorLiveData<>();
        result.addSource(daoFinances.getIncomeByCategory(), categorySums -> {
            List<PieEntry> entries = new ArrayList<>();
            if (categorySums != null) {
                for (CategorySum item : categorySums) {
                    if (item.getTotal() > 0) {
                        entries.add(new PieEntry(
                                (float) item.getTotal(),
                                item.getCategory()
                        ));
                    }
                }
            }
            result.setValue(entries);
        });
        return result;
    }
}