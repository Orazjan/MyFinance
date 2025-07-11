package com.example.myfinance.Models;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.MyApplication;
import com.example.myfinance.data.Categories;
import com.example.myfinance.data.CategoryRepository;
import com.google.android.gms.tasks.Task; // Добавлен импорт Task

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final LiveData<List<Categories>> allCategories;

    public CategoryViewModel(CategoryRepository repository) {
        this.repository = repository;
        this.allCategories = repository.getAllCategories();
    }

    public LiveData<List<Categories>> getAllCategories() {
        return allCategories;
    }

    public void updateCategorySumByName(String name, double newSum) {
        repository.updateCategorySumByName(name, newSum);
    }

    public void insert(Categories categories) {
        repository.insert(categories);
    }

    public void update(Categories categories) {
        repository.update(categories);
    }

    public void delete(Categories categories) {
        repository.delete(categories);
    }

    public LiveData<Double> getSumForCategory(String categoryName) {
        return repository.getTotalSumByCategory(categoryName);
    }

    public LiveData<Categories> getCategoryName(Categories category) {
        return repository.getCategoryByName(category.getCategoryName());
    }

    // НОВЫЙ МЕТОД: Прокси для getCategoryByNameAsync из репозитория
    public Task<Categories> getCategoryByNameAsync(String categoryName) {
        return repository.getCategoryByNameAsync(categoryName);
    }

    public static class TaskViewModelFactory implements ViewModelProvider.Factory {
        private final Application application;

        public TaskViewModelFactory(@NonNull Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
                CategoryRepository categoryRepository = ((MyApplication) application).getCategoryRepository();
                return (T) new CategoryViewModel(categoryRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
