package com.example.myfinance.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    // MutableLiveData для изменения данных
    private final MutableLiveData<String> _selectedCategory = new MutableLiveData<>();
    private final MutableLiveData<Double> _sum = new MutableLiveData<>();

    // LiveData для наблюдения (доступ только для чтения)
    public LiveData<String> getSelectedCategory() {
        return _selectedCategory;
    }

    public LiveData<Double> getSum() {
        return _sum;
    }

    // Методы для установки данных
    public void setSelectedCategory(String category) {
        _selectedCategory.setValue(category);
    }

    public void setSum(double sum) {
        _sum.setValue(sum);
    }

    // Методы для очистки данных после их использования
    public void clearSelectedCategory() {
        _selectedCategory.setValue(null); // Или "" если хотите пустую строку
    }

    public void clearSum() {
        _sum.setValue(0.0); // Сбрасываем сумму на 0.0
    }

}
