package com.example.myfinance.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.myfinance.DAO.DAOFinances;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinanceRepository {
    private final String TAG = "FinanceRepository";

    private static final String USERS_COLLECTION = "users";
    private static final String FIRESTORE_COLLECTION_NAME = "finances";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final DAOFinances daoFinances;
    private LiveData<List<Finances>> allFinances;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExeutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public FinanceRepository(DAOFinances financesDao) {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.daoFinances = financesDao;
        this.allFinances = financesDao.getAllFinances();

        auth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // Пользователь авторизовался. Запускаем фоновую синхронизацию.
                System.out.println("Auth state changed: User is logged in. Starting background sync.");
                syncUnsyncedDataToFirestore();
            } else {
                System.out.println("Auth state changed: User is logged out.");
            }
        });
    }

    public LiveData<List<String>> getComments() {
        return daoFinances.getComments();
    }

    public LiveData<List<Finances>> getAllFinances() {
        return allFinances;
    }

    public LiveData<Finances> getFinancesById(int id) {
        return daoFinances.getFinancesById(id);
    }

    public int getFinanceCount() {
        return daoFinances.getFinanceCount();
    }

    public LiveData<List<String>> getDateById(int id) {
        return daoFinances.getDateById(id);
    }

    /**
     * Вспомогательный метод для получения ссылки на коллекцию пользователя
     *
     * @return
     */
    private CollectionReference getUserFinancesCollection() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return db.collection(USERS_COLLECTION)
                    .document(Objects.requireNonNull(user.getEmail()))
                    .collection(FIRESTORE_COLLECTION_NAME);
        }
        return null;
    }

    public void insert(Finances finances) {
        finances.setSynced(false);
        finances.setFirestoreId(null);

        databaseWriteExeutor.execute(() -> {
            long roomId = daoFinances.insert(finances);
            finances.setId((int) roomId);

            attemptFirestoreSync(finances);
        });
    }

    /**
     * Обновление данных в Room
     *
     * @param finances
     */
    public void update(Finances finances) {
        finances.setSynced(false);

        databaseWriteExeutor.execute(() -> {
            daoFinances.update(finances);
            attemptFirestoreSync(finances);
        });
    }

    /**
     * Удаление данных из Room
     *
     * @param finances
     */
    public void delete(Finances finances) {
        databaseWriteExeutor.execute(() -> {

            daoFinances.delete(finances);

            CollectionReference userFinancesRef = getUserFinancesCollection();
            if (userFinancesRef != null && finances.getFirestoreId() != null) {
                userFinancesRef.document(finances.getFirestoreId()).delete()
                        .addOnSuccessListener(aVoid ->
                                Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
            } else {
                Log.e(TAG, "User not authenticated or Firestore ID is missing for delete.");
            }
        });
    }

    /**
     * Удаление всех данных из Room
     */
    public void deleteAll() {
        databaseWriteExeutor.execute(() -> {
            daoFinances.deleteAll();

            CollectionReference userFinancesRef = getUserFinancesCollection();
            if (userFinancesRef != null) {
                userFinancesRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        databaseWriteExeutor.execute(() -> {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                            Log.d(TAG, "All documents deleted from Firestore.");
                        });
                    } else {
                        Log.d(TAG, "Error getting documents from Firestore: ", task.getException());
                    }
                });
            }
        });
    }

    /**
     * Для кругового графика
     * Теперь получает List<CategorySum>
     *
     * @return LiveData с данными для PieChart
     */
    public LiveData<List<PieEntry>> getExpensesForPieChart() {
        MediatorLiveData<List<PieEntry>> result = new MediatorLiveData<>();
        result.addSource(daoFinances.getExpensesByCategory(), categorySums -> {
            List<PieEntry> entries = new ArrayList<>();
            if (categorySums != null) {
                for (CategorySum item : categorySums) {
                    if (item.getTotal() > 0) {
                        entries.add(new PieEntry(
                                (float) item.getTotal(),
                                item.getCategory()
                        ));
                    }
                }
            }
            result.setValue(entries);
        });
        return result;
    }

    /**
     * Для линейного графика
     * Теперь получает List<DateSum>
     *
     * @return LiveData с данными для LineChart
     */
    public LiveData<List<Entry>> getExpensesForLineChart() {
        MediatorLiveData<List<Entry>> result = new MediatorLiveData<>();
        result.addSource(daoFinances.getExpensesByDate(), dateSums -> {
            List<Entry> entries = new ArrayList<>();
        });
        return null;
    }

    public LiveData<List<DateSum>> getExpensesDateSums() {
        return daoFinances.getExpensesByDate();
    }

    /**
     * Для кругового графика (доходы по категориям)
     * Теперь получает List<CategorySum>
     *
     * @return LiveData с данными для PieChart
     */
    public LiveData<List<PieEntry>> getIncomeForPieChart() {
        MediatorLiveData<List<PieEntry>> result = new MediatorLiveData<>();
        result.addSource(daoFinances.getIncomeByCategory(), categorySums -> {
            List<PieEntry> entries = new ArrayList<>();
            if (categorySums != null) {
                for (CategorySum item : categorySums) {
                    if (item.getTotal() > 0) {
                        entries.add(new PieEntry(
                                (float) item.getTotal(),
                                item.getCategory()
                        ));
                    }
                }
            }
            result.setValue(entries);
        });
        return result;
    }

    /**
     * Синхронизация данных в Firestore
     *
     * @param finances
     */
    private void attemptFirestoreSync(Finances finances) {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference userFinancesRef = getUserFinancesCollection();

        if (user != null && userFinancesRef != null) {
            if (finances.getFirestoreId() != null && !finances.getFirestoreId().isEmpty()) {
                userFinancesRef.document(finances.getFirestoreId()).set(finances)
                        .addOnSuccessListener(aVoid -> {
                            databaseWriteExeutor.execute(() -> {
                                finances.setSynced(true);
                                daoFinances.update(finances);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Firestore UPDATE FAILED for Room ID: " + finances.getId() + ": " + e.getMessage(), e);
                        });
            } else {
                userFinancesRef.add(finances)
                        .addOnSuccessListener(documentReference -> {
                            String firestoreId = documentReference.getId();
                            databaseWriteExeutor.execute(() -> {
                                finances.setFirestoreId(firestoreId);
                                finances.setSynced(true);
                                daoFinances.update(finances);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Firestore ADD FAILED for Room ID: " + finances.getId() + ": " + e.getMessage(), e);
                        });
            }
        } else {
            Log.d(TAG, "Firestore sync SKIPPED for Room ID: " + finances.getId() + ". User not authenticated or Firestore ref null.");
        }
    }

    /**
     * Синхронизация несинхронизированных данных из Room в Firestore
     */
    public void syncUnsyncedDataToFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference userFinancesRef = getUserFinancesCollection();

        if (user == null || userFinancesRef == null) {
            Log.d(TAG, "syncUnsyncedDataToFirestore SKIPPED. User not authenticated or Firestore ref null.");
            return;
        }
        /*
          Синхронизация несинхронизированных данных из Room в Firestore
         */
        databaseWriteExeutor.execute(() -> {
            List<Finances> unsyncedFinances = daoFinances.getUnsyncedFinances();
            if (unsyncedFinances.isEmpty()) {
                Log.d(TAG, "No unsynced finances found.");
                return;
            }

            List<Task<Void>> syncTasks = new ArrayList<>();

            for (Finances finance : unsyncedFinances) {
                if (finance.getFirestoreId() != null && !finance.getFirestoreId().isEmpty()) {
                    syncTasks.add(userFinancesRef.document(finance.getFirestoreId()).set(finance)
                            .continueWith(task -> {
                                if (task.isSuccessful()) {
                                    databaseWriteExeutor.execute(() -> {
                                        finance.setSynced(true);
                                        daoFinances.update(finance);
                                    });
                                } else {
                                    Log.e(TAG, "syncUnsynced: UPDATE FAILED for Room ID: " + finance.getId() + ": " + task.getException());
                                }
                                return (Void) null;
                            }));
                } else {
                    syncTasks.add(userFinancesRef.add(finance)
                            .continueWith(task -> {
                                if (task.isSuccessful()) {
                                    String firestoreId = Objects.requireNonNull(task.getResult()).getId();
                                    databaseWriteExeutor.execute(() -> {
                                        finance.setFirestoreId(firestoreId);
                                        finance.setSynced(true);
                                        daoFinances.update(finance);
                                    });
                                } else {
                                    Log.e(TAG, "syncUnsynced: ADD FAILED for Room ID: " + finance.getId() + ": " + task.getException());
                                }
                                return (Void) null;
                            }));
                }
            }
            Tasks.whenAllComplete(syncTasks)
                    .addOnCompleteListener(allTasks -> {
                        System.out.println("Completed all unsynced data sync attempts.");
                    });
        });
    }

    /**
     * СИНХРОНИЗАЦИЯ ПО КНОПКЕ: Получает данные из Firestore и обновляет Room.
     *
     * @param callback
     */
    public void syncFromFirestore(SyncCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference userFinancesRef = getUserFinancesCollection();

        if (user == null || userFinancesRef == null) {
            if (callback != null) callback.onSyncComplete(false, "User not authenticated.");
            return;
        }

        userFinancesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    databaseWriteExeutor.execute(() -> {
                        daoFinances.deleteAll();

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Finances finance = document.toObject(Finances.class);
                            finance.setFirestoreId(document.getId());
                            finance.setSynced(true);
                            daoFinances.insert(finance);
                        }
                        if (callback != null)
                            callback.onSyncComplete(true, "Data synced successfully.");
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "syncFromFirestore: FAILED: " + e.getMessage());
                    if (callback != null)
                        callback.onSyncComplete(false, "Error syncing data: " + e.getMessage());
                });
    }

    /**
     * Интерфейс для коллбэка синхронизации
     */
    public interface SyncCallback {
        void onSyncComplete(boolean success, String message);
    }
}