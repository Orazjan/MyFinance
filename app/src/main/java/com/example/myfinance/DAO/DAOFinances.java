package com.example.myfinance.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfinance.data.Finances;

import java.util.Date;
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
    LiveData<List<Date>> getDateById(int id);

}
