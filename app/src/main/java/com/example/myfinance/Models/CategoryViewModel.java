package com.example.myfinance.Models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.data.Categories;
import com.example.myfinance.data.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final LiveData<List<Categories>> allCategories;

    public CategoryViewModel(CategoryRepository repository) {
        this.repository = repository;
        this.allCategories = repository.getAllCategories();
        repository.initializeDefaultCategories();
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

    public static class TaskViewModelFactory implements ViewModelProvider.Factory {
        private final CategoryRepository repository;

        public TaskViewModelFactory(CategoryRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
                return (T) new CategoryViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}