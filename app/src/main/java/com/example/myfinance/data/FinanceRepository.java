package com.example.myfinance.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfinance.DAO.DAOFinances;
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
                syncUnsyncedDataToFirestore().addOnCompleteListener(pushTask -> {
                    if (pushTask.isSuccessful()) {
                        syncFromFirestore(new SyncCallback() {
                            @Override
                            public void onSyncComplete(boolean success, String message) {
                                if (success) {
                                    Log.d(TAG, "Full sync from Firestore completed successfully: " + message);
                                } else {
                                    Log.e(TAG, "Full sync from Firestore failed: " + message);
                                }
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to push unsynced local data to Firestore: " + pushTask.getException().getMessage());
                    }
                });

            } else {
                System.out.println("Auth state changed: User is logged out.");
            }
        });
    }

    /**
     * Получение комментариев
     *
     * @return
     */
    public LiveData<List<String>> getComments() {
        return daoFinances.getComments();
    }

    /**
     * Получение всех записей
     *
     * @return
     */
    public LiveData<List<Finances>> getAllFinances() {
        return allFinances;
    }

    /**
     * Получение записи по ID
     *
     * @param id
     * @return
     */
    public LiveData<Finances> getFinancesById(int id) {
        return daoFinances.getFinancesById(id);
    }

    /**
     * Получение количества записей
     *
     * @return
     */
    public int getFinanceCount() {
        return daoFinances.getFinanceCount();
    }

    /**
     * Получение даты по ID
     *
     * @param id
     * @return
     */
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

    /**
     * Вставка записи в Room и Firestore
     * @param finances
     */
    public void insert(Finances finances) {
        finances.setSynced(false);
        finances.setFirestoreId(null);

        Log.d(TAG, "Insert (pre-Room): RoomID=" + finances.getId() + ", FirestoreID=" + finances.getFirestoreId() + ", isSynced=" + finances.isSynced());

        databaseWriteExeutor.execute(() -> {
            long roomId = daoFinances.insert(finances);
            finances.setId((int) roomId);

            Log.d(TAG, "Insert (post-Room): RoomID=" + finances.getId() + ", FirestoreID=" + finances.getFirestoreId() + ", isSynced=" + finances.isSynced());

            attemptFirestoreSync(finances);
        });
    }

    /**
     * Обновление записи в Room и Firestore
     * @param finances
     */
    public void update(Finances finances) {
        finances.setSynced(false);

        Log.d(TAG, "Update (pre-Room): RoomID=" + finances.getId() + ", FirestoreID=" + finances.getFirestoreId() + ", isSynced=" + finances.isSynced());

        databaseWriteExeutor.execute(() -> {
            daoFinances.update(finances);
            Log.d(TAG, "Update (post-Room): RoomID=" + finances.getId() + ", FirestoreID=" + finances.getFirestoreId() + ", isSynced=" + finances.isSynced());
            attemptFirestoreSync(finances);
        });
    }

    /**
     * Удаление записи из Room и Firestore
     * @param finances
     */
    public void delete(Finances finances) {
        databaseWriteExeutor.execute(() -> {
            daoFinances.delete(finances);

            CollectionReference userFinancesRef = getUserFinancesCollection();
            if (userFinancesRef != null && finances.getFirestoreId() != null) {
                userFinancesRef.document(finances.getFirestoreId()).delete().addOnSuccessListener(aVoid -> System.out.println("DocumentSnapshot successfully deleted!")).addOnFailureListener(e -> System.err.println("Error deleting document: " + e.getMessage()));
            } else {
                Log.e(TAG, "User not authenticated or Firestore ID is missing for delete.");
            }
        });
    }

    /**
     * Удаление всех записей из Room и Firestore
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
     * Вспомогательный метод для преобразования List<CategorySum> в List<PieEntry>.
     *
     * @param categorySums Список CategorySum, где category - это категория, а total - сумма.
     * @return Список PieEntry для кругового графика.
     */
    private List<PieEntry> mapCategorySumsToPieEntries(List<CategorySum> categorySums) {
        List<PieEntry> entries = new ArrayList<>();
        if (categorySums != null) {
            for (CategorySum item : categorySums) {
                if (item.getTotal() > 0) { // Исключаем нулевые или отрицательные значения
                    entries.add(new PieEntry(
                            (float) item.getTotal(),
                            item.getCategory()
                    ));
                }
            }
        }
        return entries;
    }

    /**
     * Для кругового графика (расходы по категориям).
     *
     * @return LiveData с данными для PieChart.
     */
    public LiveData<List<PieEntry>> getExpensesForPieChart() {
        return Transformations.map(daoFinances.getExpensesByCategory(), this::mapCategorySumsToPieEntries);
    }

    /**
     * Для кругового графика (доходы по категориям).
     *
     * @return LiveData с данными для PieChart.
     */
    public LiveData<List<PieEntry>> getIncomesForPieChart() {
        return Transformations.map(daoFinances.getIncomesByCategory(), this::mapCategorySumsToPieEntries);
    }

    /**
     * Для кругового графика (все транзакции по категориям).
     *
     * @return LiveData с данными для PieChart.
     */
    public LiveData<List<PieEntry>> getAllTransactionsForPieChart() {
        return Transformations.map(daoFinances.getAllTransactionsByCategory(), this::mapCategorySumsToPieEntries);
    }

    /**
     * Для линейного графика (расходы по датам).
     *
     * @return LiveData с данными для LineChart.
     */
    public LiveData<List<DateSum>> getExpensesDateSums() {
        return daoFinances.getExpensesByDate();
    }

    /**
     * Для линейного графика (доходы по датам).
     *
     * @return LiveData с данными для LineChart.
     */
    public LiveData<List<DateSum>> getIncomesDateSums() {
        return daoFinances.getIncomesByDate();
    }

    /**
     * Для линейного графика (все транзакции по датам).
     *
     * @return LiveData с данными для LineChart.
     */
    public LiveData<List<DateSum>> getAllTransactionsDateSums() {
        return daoFinances.getAllTransactionsByDate();
    }


    /**
     * Получает общую сумму всех доходов.
     * @return LiveData с общей суммой доходов.
     */
    public LiveData<Double> getTotalIncomesSum() {
        return daoFinances.getTotalIncomesSum();
    }

    /**
     * Синхронизация данных с Firestore
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
                            System.err.println("Error updating finances to Firestore: " + e.getMessage());
                            Log.e(TAG, "Error updating finances to Firestore for Room ID: " + finances.getId(), e);
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
                            System.err.println("Error adding finances to Firestore: " + e.getMessage());
                            Log.e(TAG, "Error adding finances to Firestore for Room ID: " + finances.getId(), e);
                        });
            }
        } else {
            Log.d(TAG, "Firestore sync SKIPPED for Room ID: " + finances.getId() + ". User not authenticated or Firestore ref null.");
        }
    }

    /**
     * Синхронизация несинхронизированных данных из Room в Firestore.
     * Возвращает Task, чтобы можно было отслеживать завершение.
     */
    public Task<Void> syncUnsyncedDataToFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference userFinancesRef = getUserFinancesCollection();

        Log.d(TAG, "syncUnsyncedDataToFirestore called. Current Firebase User: " + (user != null ? user.getEmail() : "null"));

        if (user == null || userFinancesRef == null) {
            Log.d(TAG, "syncUnsyncedDataToFirestore SKIPPED. User not authenticated or Firestore ref null.");
            return Tasks.forResult(null);
        }

        return Tasks.call(databaseWriteExeutor, () -> {
            List<Finances> unsyncedFinances = daoFinances.getUnsyncedFinances();

            if (unsyncedFinances.isEmpty()) {
                Log.d(TAG, "No unsynced finances found.");
                return null;
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
                                return null;
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
                                return null;
                            }));
                }
            }
            return Tasks.whenAllComplete(syncTasks);
        }).continueWith(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error during syncUnsyncedDataToFirestore batch operation: " + task.getException().getMessage());
                throw task.getException();
            }
            return null;
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
                        Log.d(TAG, "Full sync from Firestore completed. " + queryDocumentSnapshots.size() + " documents loaded.");
                        if (callback != null)
                            callback.onSyncComplete(true, "Data synced successfully.");
                    });
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error syncing data from Firestore: " + e.getMessage());
                    Log.e(TAG, "Full sync from Firestore FAILED: " + e.getMessage(), e);
                    if (callback != null)
                        callback.onSyncComplete(false, "Error syncing data: " + e.getMessage());
                });
    }

    /**
     * Интерфейс для обратного вызова после завершения синхронизации.
     */
    public interface SyncCallback {
        void onSyncComplete(boolean success, String message);
    }
}
