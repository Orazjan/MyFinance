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

            DAOtotalAmount.insert(totalAmount);

            upsertTotalAmountToFirestore(totalAmount);
        });
    }

    /**
     * Обновление записи в Room и Firestore
     * @param totalAmount
     */
    public void update(TotalAmount totalAmount) {
        totalAmount.setId(1);
        totalAmount.setSynced(false);

        databaseWriteExecutor.execute(() -> {
            DAOtotalAmount.update(totalAmount);

            upsertTotalAmountToFirestore(totalAmount);
        });
    }

    /**
     * Handles pushing the TotalAmount to Firestore.
     * Always uses .set() as it's a single document.
     */
    private void upsertTotalAmountToFirestore(TotalAmount totalAmount) {
        DocumentReference totalAmountRef = getTotalAmountDocumentRef();
        if (totalAmountRef == null) {
            Log.d(TAG, "Cannot upsert TotalAmount to Firestore: User not authenticated.");
            return;
        }

        TotalAmount roomTotalAmount = DAOtotalAmount.getSingleTotalAmountById(1);
        if (roomTotalAmount != null) {
            String currentFirestoreId = roomTotalAmount.getFirestoreId();
            if (currentFirestoreId != null && !currentFirestoreId.isEmpty()) {
                totalAmount.setFirestoreId(currentFirestoreId);
            }
        } else {
            Log.e(TAG, "TotalAmount record with ID 1 not found in Room for upserting to Firestore. This should not happen.");

        }
        performFirestoreSetOperation(totalAmount, totalAmountRef);
    }

    /**
     * Performs the Firestore set operation for TotalAmount.
     *
     * @param totalAmount
     * @param totalAmountRef
     */
    private void performFirestoreSetOperation(TotalAmount totalAmount, DocumentReference totalAmountRef) {
        totalAmountRef.set(totalAmount)
                .addOnSuccessListener(aVoid -> {
                    databaseWriteExecutor.execute(() -> {
                        totalAmount.setFirestoreId(totalAmountRef.getId());
                        totalAmount.setSynced(true);
                        DAOtotalAmount.update(totalAmount);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка обновления TotalAmount в Firestore: " + e.getMessage(), e);
                });
    }

    /**
     * Syncs the single TotalAmount record from Firestore to Room.
     * This is triggered upon user login.
     */
    private void syncTotalAmountFromFirestore() {
        DocumentReference totalAmountRef = getTotalAmountDocumentRef();
        if (totalAmountRef == null) {
            Log.d(TAG, "Cannot sync TotalAmount from Firestore: User not authenticated.");
            return;
        }

        totalAmountRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    databaseWriteExecutor.execute(() -> {
                        TotalAmount localTotalAmount = DAOtotalAmount.getSingleTotalAmountById(1);
                        if (localTotalAmount == null) {
                            localTotalAmount = new TotalAmount();
                            localTotalAmount.setId(1);
                        }

                        if (documentSnapshot.exists()) {
                            TotalAmount firestoreTotalAmount = documentSnapshot.toObject(TotalAmount.class);
                            if (firestoreTotalAmount != null) {
                                firestoreTotalAmount.setId(1);
                                firestoreTotalAmount.setFirestoreId(documentSnapshot.getId());
                                firestoreTotalAmount.setSynced(true);

                                DAOtotalAmount.insert(firestoreTotalAmount);
                                Log.d(TAG, "TotalAmount синхронизирован из Firestore в Room. Суммы: " +
                                        firestoreTotalAmount.getAmount() + "/" + firestoreTotalAmount.getSumma());
                            }
                        } else {
                            Log.d(TAG, "Документ TotalAmount не найден в Firestore. Отправляем текущее локальное состояние, если оно несинхронизировано.");
                            if (localTotalAmount != null && !localTotalAmount.isSynced()) {
                                upsertTotalAmountToFirestore(localTotalAmount);
                            } else if (localTotalAmount.getAmount() == 0.0 && localTotalAmount.getSumma() == 0.0 && localTotalAmount.getFirestoreId() == null) {
                                localTotalAmount.setAmount(0.0);
                                localTotalAmount.setSumma(0.0);
                                insert(localTotalAmount);
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
