package com.example.myfinance.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myfinance.data.Categories;

import java.util.List;

@Dao
public interface DAOcategories {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Categories categories);

    @Update
    void update(Categories categories);

    @Delete
    void delete(Categories categories);

    @Query("SELECT * FROM finance_table")
    LiveData<List<Categories>> getAllCategories();

    @Query("SELECT * FROM finance_table WHERE id = :id")
    LiveData<Categories> getCategoryById(int id);

    @Query("DELETE FROM finance_table")
    void deleteAll();

    @Query("SELECT * FROM finance_table WHERE categoryName = :categoryName")
    LiveData<Categories> getCategoryByName(String categoryName);

    @Query("SELECT COUNT(*) FROM finance_table")
    int getCategoryCount();

    @Query("SELECT * FROM finance_table WHERE sum = :sum")
    LiveData<Categories> getCategoryBySum(double sum);

    @Query("SELECT SUM(sum) FROM finance_table WHERE categoryName = :categoryName")
    LiveData<Double> getTotalSumByCategory(String categoryName);

    @Query("UPDATE finance_table SET sum = :newSum WHERE categoryName = :name")
    void updateCategorySumByName(String name, double newSum);
}