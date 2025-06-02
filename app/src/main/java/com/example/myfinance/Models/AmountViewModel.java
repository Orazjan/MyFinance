package com.example.myfinance.Models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.TotalAmount;

public class AmountViewModel extends ViewModel {
    private AmountRepository repository;
    private LiveData<Double> lastAmount;

    public AmountViewModel(AmountRepository repository) {
        this.repository = repository;
        this.lastAmount = repository.getLastAmount();
    }

    public void insert(TotalAmount totalAmount) {
        repository.insert(totalAmount);
    }

    public void update(TotalAmount totalAmount) {
        repository.update(totalAmount);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(TotalAmount totalAmount) {
        repository.delete(totalAmount);
    }

    public LiveData<Double> getLastAmount() { // Метод для предоставления LiveData в UI
        return lastAmount;
    }

    public static class TaskViewModelFactory implements ViewModelProvider.Factory {
        private final AmountRepository repository;

        public TaskViewModelFactory(AmountRepository rep) {
            this.repository = rep;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AmountViewModel.class)) {
                return (T) new AmountViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

    }
}
