package com.example.myfinance.data;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.myfinance.DAO.DAOTotalAmount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AmountRepository {
    private final String TAG = "AmountRepository";

    private final DAOTotalAmount DAOtotalAmount;
    private LiveData<Double> lastAmount;
    private LiveData<Double> summa;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private static final String USERS_COLLECTION = "users";
    private static final String FIRESTORE_FINANCES_COLLECTION_NAME = "finances";
    private static final String FIRESTORE_TOTAL_AMOUNT_DOCUMENT_ID = "balance_summary";

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public AmountRepository(DAOTotalAmount daOtotalAmount) {
        this.DAOtotalAmount = daOtotalAmount;
        this.lastAmount = DAOtotalAmount.getLastAmount();
        this.summa = DAOtotalAmount.getSumma();

        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();

        // Добавлена логика для инициализации Room при запуске, независимо от аутентификации
        // Это гарантирует, что TotalAmount с ID 1 всегда существует в Room
        initializeRoomTotalAmount();

        auth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "Состояние аутентификации изменилось: Пользователь вошёл. Запуск синхронизации TotalAmount.");
                syncTotalAmountFromFirestore();
            } else {
                Log.d(TAG, "Состояние аутентификации изменилось: Пользователь вышел. Синхронизация TotalAmount пропущена.");
            }
        });
    }

    /**
     * Инициализирует запись TotalAmount в Room, если она не существует.
     * Это гарантирует, что всегда есть запись с ID=1 для Room.
     */
    private void initializeRoomTotalAmount() {
        databaseWriteExecutor.execute(() -> {
            TotalAmount existingTotalAmount = DAOtotalAmount.getSingleTotalAmountById(1);
            if (existingTotalAmount == null) {
                Log.d(TAG, "initializeRoomTotalAmount: TotalAmount record with ID 1 not found. Creating new one with 0.0.");
                TotalAmount newZeroAmount = new TotalAmount(0.0, 0.0);
                DAOtotalAmount.insert(newZeroAmount);
            } else {
                Log.d(TAG, "initializeRoomTotalAmount: TotalAmount record with ID 1 already exists. Amount=" + existingTotalAmount.getAmount() + ", Summa=" + existingTotalAmount.getSumma());
            }
        });
    }

    /**
     * Вспомогательный метод для получения ссылки на коллекцию пользователя
     *
     * @return
     */
    private DocumentReference getTotalAmountDocumentRef() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return db.collection(USERS_COLLECTION)
                    .document(Objects.requireNonNull(user.getEmail()))
                    .collection(FIRESTORE_FINANCES_COLLECTION_NAME)
                    .document(FIRESTORE_TOTAL_AMOUNT_DOCUMENT_ID);
        }
        return null;
    }

    /**
     * Вставка записи в Room и Firestore
     *
     * @param totalAmount
     */
    public void insert(TotalAmount totalAmount) {
        databaseWriteExecutor.execute(() -> {
            totalAmount.setId(1);
            totalAmount.setSynced(false);

            Log.d(TAG, "Inserting/Updating TotalAmount in Room: Amount=" + totalAmount.getAmount() + ", Summa=" + totalAmount.getSumma() + ", Synced=" + totalAmount.isSynced());
            DAOtotalAmount.insert(totalAmount);

            // Отправляем в Firestore только если пользователь аутентифицирован ---
            if (auth.getCurrentUser() != null) {
                upsertTotalAmountToFirestore(totalAmount);
            } else {
                Log.d(TAG, "Skipping Firestore upsert: User not authenticated.");
            }
        });
    }

    /**
     * Обновление записи в Room и Firestore
     * @param totalAmount
     */
    public void update(TotalAmount totalAmount) {
        totalAmount.setId(1);
        totalAmount.setSynced(false); // Сбрасываем флаг синхронизации перед обновлением

        databaseWriteExecutor.execute(() -> {
            Log.d(TAG, "Updating TotalAmount in Room: Amount=" + totalAmount.getAmount() + ", Summa=" + totalAmount.getSumma() + ", Synced=" + totalAmount.isSynced());
            DAOtotalAmount.update(totalAmount);

            // Отправляем в Firestore только если пользователь аутентифицирован ---
            if (auth.getCurrentUser() != null) {
                upsertTotalAmountToFirestore(totalAmount);
            } else {
                Log.d(TAG, "Skipping Firestore upsert: User not authenticated.");
            }
        });
    }

    /**
     * Обновление записи в Firestore и Room
     * @param totalAmount
     */
    private void upsertTotalAmountToFirestore(TotalAmount totalAmount) {
        DocumentReference totalAmountRef = getTotalAmountDocumentRef();
        if (totalAmountRef == null) {
            Log.d(TAG, "Cannot upsert TotalAmount to Firestore: User not authenticated (getTotalAmountDocumentRef returned null).");
            return;
        }

        // Получаем текущую локальную запись, чтобы сохранить firestoreId, если он уже есть
        // Это нужно делать здесь, так как totalAmount, переданный в метод, может быть новым объектом
        TotalAmount roomTotalAmount = DAOtotalAmount.getSingleTotalAmountById(1);
        if (roomTotalAmount != null && roomTotalAmount.getFirestoreId() != null && !roomTotalAmount.getFirestoreId().isEmpty()) {
            totalAmount.setFirestoreId(roomTotalAmount.getFirestoreId());
        } else {
            // Если firestoreId нет, он будет сгенерирован Firestore при первом set()
            // Для фиксированного документа мы устанавливаем его ID вручную
            totalAmount.setFirestoreId(FIRESTORE_TOTAL_AMOUNT_DOCUMENT_ID);
            Log.d(TAG, "upsertTotalAmountToFirestore: Setting fixed Firestore ID for new document: " + FIRESTORE_TOTAL_AMOUNT_DOCUMENT_ID);
        }

        Log.d(TAG, "Attempting to upsert TotalAmount to Firestore: Amount=" + totalAmount.getAmount() + ", Summa=" + totalAmount.getSumma() + ", FirestoreId=" + totalAmount.getFirestoreId());
        performFirestoreSetOperation(totalAmount, totalAmountRef);
    }

    /**
     * Обновление записи в Firestore и Room (вспомогательный метод)
     * @param totalAmount
     * @param totalAmountRef
     */
    private void performFirestoreSetOperation(TotalAmount totalAmount, DocumentReference totalAmountRef) {
        totalAmountRef.set(totalAmount)
                .addOnSuccessListener(aVoid -> {
                    databaseWriteExecutor.execute(() -> {
                        // После успешной записи в Firestore, обновляем локальную запись как синхронизированную
                        // totalAmountRef.getId() для фиксированного документа будет FIRESTORE_TOTAL_AMOUNT_DOCUMENT_ID
                        totalAmount.setFirestoreId(totalAmountRef.getId());
                        totalAmount.setSynced(true);
                        DAOtotalAmount.update(totalAmount); // Обновляем локальную запись
                        Log.d(TAG, "TotalAmount successfully upserted to Firestore. Local record updated as synced. Amount=" + totalAmount.getAmount() + ", Summa=" + totalAmount.getSumma());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка обновления TotalAmount в Firestore: " + e.getMessage(), e);
                    // Оставляем isSynced=false, чтобы повторить попытку позже
                });
    }

    /**
     * Синхронизация TotalAmount из Firestore в Room (вспомогательный метод)
     */
    private void syncTotalAmountFromFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.d(TAG, "Cannot sync TotalAmount from Firestore: User not authenticated.");
            return;
        }

        DocumentReference totalAmountRef = getTotalAmountDocumentRef();
        if (totalAmountRef == null) { // Дополнительная проверка, хотя выше уже есть
            Log.d(TAG, "Cannot sync TotalAmount from Firestore: Document reference is null.");
            return;
        }

        totalAmountRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    databaseWriteExecutor.execute(() -> {
                        TotalAmount localTotalAmount = DAOtotalAmount.getSingleTotalAmountById(1); // Получаем текущую локальную запись

                        if (documentSnapshot.exists()) {
                            TotalAmount firestoreTotalAmount = documentSnapshot.toObject(TotalAmount.class);
                            if (firestoreTotalAmount != null) {
                                firestoreTotalAmount.setId(1); // Убеждаемся, что ID для Room всегда 1
                                firestoreTotalAmount.setFirestoreId(documentSnapshot.getId());
                                firestoreTotalAmount.setSynced(true);

                                Log.d(TAG, "Firestore document exists. Syncing from Firestore to Room: Amount=" + firestoreTotalAmount.getAmount() + ", Summa=" + firestoreTotalAmount.getSumma());
                                DAOtotalAmount.insert(firestoreTotalAmount); // REPLACE с ID 1
                                Log.d(TAG, "TotalAmount синхронизирован из Firestore в Room. Суммы: " +
                                        firestoreTotalAmount.getAmount() + "/" + firestoreTotalAmount.getSumma());
                            }
                        } else {
                            Log.d(TAG, "Документ TotalAmount не найден в Firestore.");
                            if (localTotalAmount != null) {
                                // Если локальная запись не синхронизирована, пытаемся отправить ее в Firestore
                                if (!localTotalAmount.isSynced()) {
                                    upsertTotalAmountToFirestore(localTotalAmount);
                                } else {
                                    Log.d(TAG, "Local TotalAmount is synced and Firestore document is missing. This might indicate an issue. No action needed for missing Firestore document.");
                                }
                            } else {
                                // Если нет локальной записи и нет в Firestore, создаем новую с 0.0
                                Log.d(TAG, "No local TotalAmount record. Creating new one with 0.0 and pushing to Firestore.");
                                TotalAmount newZeroAmount = new TotalAmount(0.0, 0.0);
                                // Используем insert(), который уже содержит логику upsertTotalAmountToFirestore
                                insert(newZeroAmount);
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка синхронизации TotalAmount из Firestore: " + e.getMessage(), e);
                });
    }

    /**
     * Получение последнего суммы
     *
     * @return
     */
    public LiveData<Double> getLastAmount() {
        return lastAmount;
    }

    /**
     * Получение суммы
     *
     * @return
     */
    public LiveData<Double> getSumma() {
        return summa;
    }

    /**
     * Удаление всех записей из Room и Firestore
     */
    public void deleteAll() {
        databaseWriteExecutor.execute(() -> {
            DAOtotalAmount.deleteAll();
            DocumentReference totalAmountRef = getTotalAmountDocumentRef();
            if (totalAmountRef != null) {
                totalAmountRef.delete()
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Документ TotalAmount удален из Firestore."))
                        .addOnFailureListener(e -> Log.e(TAG, "Ошибка удаления документа TotalAmount из Firestore: " + e.getMessage(), e));
            }
        });
    }

    /**
     * Удаление записи из Room и Firestore
     *
     * @param totalAmount
     */
    public void delete(TotalAmount totalAmount) {
        databaseWriteExecutor.execute(() -> DAOtotalAmount.delete(totalAmount));
    }
}
