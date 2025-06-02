package com.example.myfinance.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myfinance.DAO.DAOFinances;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Finances.class}, version = 1, exportSchema = false)
public abstract class FinanceDatabase extends RoomDatabase {
    public abstract DAOFinances daoFinances();

    private static volatile FinanceDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FinanceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FinanceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FinanceDatabase.class, "finance_Database").build();
                }
            }
        }
        return INSTANCE;
    }
}
