package com.example.myfinance;

import android.app.Application;
import android.util.Log;

import com.example.myfinance.data.AmountDatabase;
import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.CategoryDataBase;
import com.example.myfinance.data.CategoryRepository;
import com.example.myfinance.data.FinanceDatabase;
import com.example.myfinance.data.FinanceRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApplication extends Application {
    private FinanceRepository financeRepository;
    private CategoryRepository categoryRepository;
    private AmountRepository amountRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            FirebaseFirestore.setLoggingEnabled(true); // Можно включить для детального логирования Firestore
        } catch (IllegalStateException e) {
            Log.e("MyApplication", "FirebaseApp already initialized or failed to initialize: " + e.getMessage());
        }

        // Инициализация синглтона для FinanceRepository
        FinanceDatabase financeDb = FinanceDatabase.getDatabase(this);
        financeRepository = new FinanceRepository(financeDb.daoFinances());
        Log.d("MyApplication", "FinanceRepository initialized.");

        // Инициализация синглтона для CategoryRepository
        CategoryDataBase categoryDb = CategoryDataBase.getDatabase(this);
        categoryRepository = new CategoryRepository(categoryDb.daoCategories());
        Log.d("MyApplication", "CategoryRepository initialized.");

        // Инициализация синглтона для AmountRepository
        AmountDatabase amountDb = AmountDatabase.getDatabase(this);
        amountRepository = new AmountRepository(amountDb.daoTotalAmount());
        Log.d("MyApplication", "AmountRepository initialized.");
    }

    // Геттер для FinanceRepository
    public FinanceRepository getFinanceRepository() {
        return financeRepository;
    }

    // Геттер для CategoryRepository
    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    // Геттер для AmountRepository
    public AmountRepository getAmountRepository() {
        return amountRepository;
    }
}
