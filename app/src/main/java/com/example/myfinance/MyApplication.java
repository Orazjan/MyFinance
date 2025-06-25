package com.example.myfinance;

import android.app.Application;

import com.example.myfinance.data.FinanceDatabase;
import com.example.myfinance.data.FinanceRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApplication extends Application {
    private FinanceRepository financeRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        FinanceDatabase database = FinanceDatabase.getDatabase(this);
        financeRepository = new FinanceRepository(database.daoFinances());
        FirebaseApp.initializeApp(this);
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public FinanceRepository getFinanceRepository() {
        return financeRepository;
    }
}
