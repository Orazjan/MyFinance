package com.example.myfinance.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfinance.data.TotalAmount;

@Dao
public interface DAOTotalAmount {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TotalAmount totalAmount);

    @Update
    void update(TotalAmount totalAmount);

    @Delete
    void delete(TotalAmount totalAmount);

    @Query("SELECT SUM(amount) FROM Total_amount")
    Double getSumAmounts();

    @Query("DELETE FROM total_amount")
    void deleteAll();

    @Query("SELECT amount FROM total_amount ORDER BY id DESC LIMIT 1")
    LiveData<Double> getLastAmount();

    @Query("SELECT summa FROM total_amount ORDER BY id DESC LIMIT 1")
    LiveData<Double> getSumma();

    @Query("SELECT * FROM total_amount WHERE id = :id LIMIT 1")
    LiveData<TotalAmount> getTotalAmountById(int id);

    @Query("SELECT * FROM total_amount WHERE id = :id LIMIT 1")
    TotalAmount getSingleTotalAmountById(int id);
}
