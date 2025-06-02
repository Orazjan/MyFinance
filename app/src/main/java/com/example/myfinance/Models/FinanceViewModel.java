package com.example.myfinance.Models;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.data.FinanceRepository;
import com.example.myfinance.data.Finances;

import java.util.List;

public class FinanceViewModel extends ViewModel {
    private final FinanceRepository finrepository;
    private final LiveData<List<Finances>> allFinances;

    public FinanceViewModel(FinanceRepository repository) {
        this.finrepository = repository;
        this.allFinances = repository.getAllFinances();
    }

    public LiveData<List<Finances>> getAllFinances() {
        return allFinances;
    }

    public void insert(Finances finances) {
        finrepository.insert(finances);
    }

    public void update(Finances finances) {
        finrepository.update(finances);
    }

    public void delete(Finances finances) {
        finrepository.delete(finances);
    }

    public static class TaskViewModelFactory implements ViewModelProvider.Factory {
        private final FinanceRepository repository;

        public TaskViewModelFactory(FinanceRepository rep) {
            this.repository = rep;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(FinanceViewModel.class)) {
                return (T) new FinanceViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

    }
}
