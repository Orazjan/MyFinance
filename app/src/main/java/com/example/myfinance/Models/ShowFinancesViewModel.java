package com.example.myfinance.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShowFinancesViewModel extends ViewModel {
    private final MutableLiveData<List<ShowFinances>> _financesList = new MutableLiveData<>();

    public LiveData<List<ShowFinances>> getFinancesList() {
        return _financesList;
    }

    public void addFinance(ShowFinances newFinance) {
        List<ShowFinances> currentList = _financesList.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(newFinance);
        _financesList.setValue(currentList);
    }

    public void setFinances(List<ShowFinances> newFinances) {
        _financesList.setValue(newFinances);
    }

    public int getMaxId() {
        List<ShowFinances> currentList = _financesList.getValue();
        if (currentList == null || currentList.isEmpty()) {
            return 0; // Или 1, в зависимости от того, с какого ID вы хотите начинать
        }
        int maxId = 0;
        for (ShowFinances finance : currentList) {
            if (finance.getId() > maxId) {
                maxId = finance.getId();
            }
        }
        return maxId;
    }
}
