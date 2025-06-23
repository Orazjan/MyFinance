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
    void insert(Finances finances);

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

    @Query("SELECT `Finance result` AS category, SUM(suma) AS total " +
            "FROM Finances " +
            "WHERE suma > 0 " +
            "GROUP BY `Finance result`")
    LiveData<List<CategorySum>> getExpensesByCategory();

    @Query("SELECT `Finance result` AS category, SUM(suma) AS total " +
            "FROM Finances " +
            "WHERE `Finance result` = 'Доход' " +
            "GROUP BY `Finance result`")
    LiveData<List<CategorySum>> getIncomeByCategory();

    @Query("SELECT date, SUM(suma) AS total " +
            "FROM Finances " +
            "WHERE suma > 0 " +
            "GROUP BY date ORDER BY date ASC")
    LiveData<List<DateSum>> getExpensesByDate();
}