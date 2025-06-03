package com.example.myfinance.data;

import androidx.lifecycle.LiveData;

import com.example.myfinance.DAO.DAOcategories;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryRepository {
    private final DAOcategories daoCategories;
    private final LiveData<List<Categories>> allCategories;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public CategoryRepository(DAOcategories daoCategories) {
        this.daoCategories = daoCategories;
        this.allCategories = daoCategories.getAllCategories();
    }

    public void insert(Categories categories) {
        databaseWriteExecutor.execute(() -> daoCategories.insert(categories));
    }

    public void update(Categories categories) {
        databaseWriteExecutor.execute(() -> daoCategories.update(categories));
    }

    public void delete(Categories categories) {
        databaseWriteExecutor.execute(() -> daoCategories.delete(categories));
    }

    public void deleteAll() {
        databaseWriteExecutor.execute(() -> daoCategories.deleteAll());
    }

    public LiveData<List<Categories>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Categories> getCategoryById(int id) {
        return daoCategories.getCategoryById(id);
    }

    public LiveData<Categories> getCategoryByName(String categoryName) {
        return daoCategories.getCategoryByName(categoryName);
    }
//
//    public void initializeDefaultCategories() {
//        CategoryDataBase.databaseWriteExecutor.execute(() -> {
//            int count = daoCategories.getCategoryCount();
//            if (count == 0) {
//                daoCategories.insert(new Categories("Оплата за транспорт", 0.0));
//                daoCategories.insert(new Categories("Оплата оператора", 50.0));
//                daoCategories.insert(new Categories("Оплата за еду", 0.0));
//                daoCategories.insert(new Categories("Другое", 0.0));
//            }
//        });
//    }

    public void updateCategorySumByName(String name, double newSum) {
        CategoryDataBase.databaseWriteExecutor.execute(() -> {
            daoCategories.updateCategorySumByName(name, newSum);
        });
    }
    public LiveData<Double> getTotalSumByCategory(String categoryName) {
        return daoCategories.getTotalSumByCategory(categoryName); // Просто передаем LiveData из DAO
    }

    public LiveData<Categories> getCategoryBySum(double sum) {
        return daoCategories.getCategoryBySum(sum);
    }
}