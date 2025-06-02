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

    // ИЗМЕНЕНИЕ: Теперь возвращаем LiveData<List<Categories>>
    @Query("SELECT * FROM finance_table")
    LiveData<List<Categories>> getAllCategories(); // Переименовано для ясности

    // ИЗМЕНЕНИЕ: Теперь возвращаем LiveData<Categories>
    @Query("SELECT * FROM finance_table WHERE id = :id")
    LiveData<Categories> getCategoryById(int id); // Переименовано для ясности

    @Query("DELETE FROM finance_table")
    void deleteAll();

    // ИЗМЕНЕНИЕ: Теперь возвращаем LiveData<Categories>
    @Query("SELECT * FROM finance_table WHERE categoryName = :categoryName")
    LiveData<Categories> getCategoryByName(String categoryName); // Переименовано для ясности

    @Query("SELECT COUNT(*) FROM finance_table")
    int getCategoryCount();

    // ИЗМЕНЕНИЕ: Теперь возвращаем LiveData<Categories>
    @Query("SELECT * FROM finance_table WHERE sum = :sum")
    LiveData<Categories> getCategoryBySum(double sum); // Переименовано для ясности
}