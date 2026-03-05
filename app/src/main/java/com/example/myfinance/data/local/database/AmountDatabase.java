package com.example.myfinance.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myfinance.data.TotalAmount;
import com.example.myfinance.data.local.dao.DAOTotalAmount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TotalAmount.class}, version = 1, exportSchema = false)
public abstract class AmountDatabase extends RoomDatabase {
    public abstract DAOTotalAmount daoTotalAmount();

    private static volatile AmountDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AmountDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AmountDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AmountDatabase.class, "Amount_Database").build();
                }
            }
        }
        return INSTANCE;
    }
}
