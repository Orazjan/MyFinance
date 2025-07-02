package com.example.myfinance.Models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.MyApplication;
import com.example.myfinance.data.Categories;
import com.example.myfinance.data.CategoryRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final LiveData<List<Categories>> allCategories;

    // Конструктор ViewModel принимает CategoryRepository.
    // Это стандартный подход для ViewModel.
    public CategoryViewModel(CategoryRepository repository) {
        this.repository = repository;
        this.allCategories = repository.getAllCategories();
    }

    public LiveData<List<Categories>> getAllCategories() {
        return allCategories;
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


    // Асинхронно получает категорию по имени, возвращая Task
    public Task<Categories> getCategoryByNameAsync(String categoryName) {
        return repository.getCategoryByNameAsync(categoryName);
    }

    // Эта фабрика отвечает за правильное создание CategoryViewModel,
    // получая синглтон CategoryRepository из MyApplication.
    public static class TaskViewModelFactory implements ViewModelProvider.Factory {
        private final Application application; // <-- ИСПРАВЛЕНО: Теперь это Application

        // Конструктор фабрики принимает Application
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
