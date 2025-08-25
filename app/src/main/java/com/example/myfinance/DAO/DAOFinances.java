package com.example.myfinance.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfinance.data.CategorySum;
import com.example.myfinance.data.DateSum;
import com.example.myfinance.data.Finances;

import java.util.List;

@Dao
public interface DAOFinances {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Finances finances);

    @Update
    void update(Finances finances);

    @Delete
    void delete(Finances finances);

    @Query("SELECT * FROM Finances")
    LiveData<List<Finances>> getAllFinances();

    @Query("SELECT * FROM Finances WHERE id = :id")
    LiveData<Finances> getFinancesById(int id);

    @Query("DELETE FROM Finances")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM Finances")
    int getFinanceCount();

    @Query("SELECT comments FROM Finances")
    LiveData<List<String>> getComments();

    @Query("SELECT date FROM finances WHERE id = :id")
    LiveData<List<String>> getDateById(int id);

    // Для расходов по категориям
    @Query("SELECT financeResult AS category, SUM(summa) AS total FROM Finances WHERE operationType = 'Расход' GROUP BY financeResult")
    LiveData<List<CategorySum>> getExpensesByCategory();

    // Для доходов по категориям
    @Query("SELECT financeResult AS category, SUM(summa) AS total FROM Finances WHERE operationType = 'Доход' GROUP BY financeResult")
    LiveData<List<CategorySum>> getIncomesByCategory();

    // Для всех транзакций по категориям
    @Query("SELECT financeResult AS category, SUM(summa) AS total FROM Finances GROUP BY financeResult")
    LiveData<List<CategorySum>> getAllTransactionsByCategory();

    // AS sum на AS total для соответствия DateSum ---
    @Query("SELECT date, SUM(summa) AS total FROM Finances WHERE operationType = 'Расход' GROUP BY date ORDER BY date ASC")
    LiveData<List<DateSum>> getExpensesByDate();

    // Запрос для доходов по датам ---
    @Query("SELECT date, SUM(summa) AS total FROM Finances WHERE operationType = 'Доход' GROUP BY date ORDER BY date ASC")
    LiveData<List<DateSum>> getIncomesByDate();

    // Запрос для всех транзакций по датам ---
    @Query("SELECT date, SUM(summa) AS total FROM Finances GROUP BY date ORDER BY date ASC")
    LiveData<List<DateSum>> getAllTransactionsByDate();

    // Получение общей суммы доходов ---
    @Query("SELECT SUM(summa) FROM Finances WHERE operationType = 'Доход'")
    LiveData<Double> getTotalIncomesSum();

    @Query("SELECT * FROM Finances WHERE isSynced = 0")
    List<Finances> getUnsyncedFinances();

    @Query("UPDATE Finances SET isSynced = :isSynced, firestoreId = :firestoreId WHERE id = :roomId")
    void updateSyncStatus(int roomId, String firestoreId, boolean isSynced);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFinances(Finances finances);

}
