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
    long insert(Categories categories);

    @Update
    void update(Categories categories);

    @Delete
    void delete(Categories categories);

    @Query("SELECT * FROM categories_table")
    LiveData<List<Categories>> getAllCategories();

    @Query("SELECT * FROM categories_table")
    List<Categories> getAllCategoriesBlocking();

    @Query("SELECT * FROM categories_table WHERE id = :id")
    LiveData<Categories> getCategoryById(int id);

    @Query("DELETE FROM categories_table")
    void deleteAll();

    @Query("SELECT * FROM categories_table WHERE categoryName = :categoryName")
    LiveData<Categories> getCategoryByName(String categoryName);

    @Query("SELECT * FROM categories_table WHERE categoryName = :categoryName LIMIT 1")
    Categories getSingleCategoryByNameBlocking(String categoryName);

    @Query("SELECT COUNT(*) FROM categories_table")
    int getCategoryCount();

    @Query("SELECT * FROM categories_table WHERE sum = :sum")
    LiveData<Categories> getCategoryBySum(double sum);

    @Query("SELECT SUM(sum) FROM categories_table WHERE categoryName = :categoryName")
    LiveData<Double> getTotalSumByCategory(String categoryName);

    // Метод getSumForCategory удален, так как getTotalSumByCategory выполняет ту же функцию.
    // @Query("SELECT SUM(sum) FROM categories_table WHERE categoryName = :categoryName")
    // LiveData<Double> getSumForCategory(String categoryName); // УДАЛЕНО

    @Query("UPDATE categories_table SET sum = :newSum, isSynced = 0 WHERE categoryName = :name")
    void updateCategorySum(String name, double newSum);

    @Query("SELECT * FROM categories_table WHERE isSynced = 0")
    List<Categories> getUnsyncedCategoriesBlocking();

    @Query("SELECT * FROM categories_table WHERE firestoreId = :firestoreId LIMIT 1")
    Categories getCategoryByFirestoreId(String firestoreId);
}
