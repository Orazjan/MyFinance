package com.example.myfinance.data;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.myfinance.DAO.DAOcategories;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class CategoryRepository {
    private final String TAG = "CategoryRepository";

    private final DAOcategories daoCategories;
    private final LiveData<List<Categories>> allCategories;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private AuthStateListener mAuthStateListener;
    private final AtomicBoolean isAuthSyncInProgress = new AtomicBoolean(false);

    private static final String USERS_COLLECTION = "users";
    private static final String FIRESTORE_CATEGORIES_COLLECTION_NAME = "categories";

    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    /**
     * Конструктор для CategoryRepository.
     *
     * @param daoCategories
     */
    public CategoryRepository(DAOcategories daoCategories) {

        this.daoCategories = daoCategories;
        this.allCategories = daoCategories.getAllCategories();

        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (isAuthSyncInProgress.compareAndSet(false, true)) {
                    syncUnsyncedCategoriesToFirestore().addOnCompleteListener(pushTask -> {
                        if (pushTask.isSuccessful()) {
                            Log.d(TAG, "All unsynced local categories pushed to Firestore successfully. Now pulling categories from Firestore.");
                        } else {
                            Log.e(TAG, "Failed to push unsynced local categories to Firestore: " + (pushTask.getException() != null ? pushTask.getException().getMessage() : "Unknown error"));
                        }
                        syncCategoriesFromFirestore().addOnCompleteListener(pullTask -> {
                            isAuthSyncInProgress.set(false);
                            if (!pullTask.isSuccessful()) {
                                Log.e(TAG, "Failed to pull categories from Firestore: " + (pullTask.getException() != null ? pullTask.getException().getMessage() : "Unknown error"));
                            }
                            initializeDefaultCategories();
                        });
                    });
                } else {
                    Log.d(TAG, "Auth state changed: User is logged in, but sync already in progress. Skipping additional trigger.");
                }
            } else {
                Log.d(TAG, "Auth state changed: User is logged out. Category sync skipped.");
                isAuthSyncInProgress.set(false);
            }
        };
        auth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Получение ссылки на коллекцию пользователя
     *
     * @return
     */
    private CollectionReference getUserCategoriesCollection() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            return db.collection(USERS_COLLECTION)
                    .document(user.getEmail())
                    .collection(FIRESTORE_CATEGORIES_COLLECTION_NAME);
        }
        return null;
    }

    /**
     * Вставка записи в Room и Firestore
     *
     * @param category
     */
    public void insert(Categories category) {
        category.setSynced(false);
        category.setFirestoreId(null);

        databaseWriteExecutor.execute(() -> {
            try {
                long roomId = daoCategories.insert(category);
                category.setId((int) roomId);

                if (auth.getCurrentUser() != null) {
                    attemptFirestoreCategorySync(category);
                } else {
                    Log.d(TAG, "Insert: User not logged in. " + category.getCategoryName() + " will be synced on next login.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Insert: Failed to insert category locally: " + category.getCategoryName(), e);
            }
        });
    }

    /**
     * Обновление записи в Room и Firestore
     *
     * @param category
     */
    public void update(Categories category) {
        category.setSynced(false);

        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.update(category);
                if (auth.getCurrentUser() != null) {
                    attemptFirestoreCategorySync(category);
                } else {
                    Log.d(TAG, "Update: User not logged in. " + category.getCategoryName() + " will be synced on next login.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Update: Failed to update category locally: " + category.getCategoryName(), e);
            }
        });
    }

    /**
     * Удаление записи из Room и Firestore
     *
     * @param category
     */
    public void delete(Categories category) {
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.delete(category);

                CollectionReference categoriesRef = getUserCategoriesCollection();
                if (categoriesRef != null && category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
                    categoriesRef.document(category.getFirestoreId()).delete()
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Delete: Category successfully deleted from Firestore: " + category.getFirestoreId()))
                            .addOnFailureListener(e -> Log.e(TAG, "Delete: Error deleting category from Firestore: " + category.getFirestoreId(), e));
                } else {
                    Log.d(TAG, "Delete: User not authenticated or Firestore ID is missing for category delete. Local delete only for: " + category.getCategoryName());
                }
            } catch (Exception e) {
                Log.e(TAG, "Delete: Failed to delete category locally: " + category.getCategoryName(), e);
            }
        });
    }

    /**
     * Удаление всех записей из Room и Firestore
     * <p>
     * если вдруг нужно удалить все категории
     */
    public void deleteAll() {
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.deleteAll();

                CollectionReference categoriesRef = getUserCategoriesCollection();
                if (categoriesRef != null) {
                    categoriesRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            databaseWriteExecutor.execute(() -> {
                                WriteBatch batch = db.batch();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    batch.delete(document.getReference());
                                }
                                batch.commit()
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DeleteAll: All documents deleted from Firestore."))
                                        .addOnFailureListener(e -> Log.e(TAG, "DeleteAll: Error deleting all documents from Firestore: ", e));
                            });
                        } else {
                            Log.e(TAG, "DeleteAll: Error getting documents from Firestore for deleteAll: ", task.getException());
                        }
                    });
                } else {
                    Log.d(TAG, "DeleteAll: User not authenticated for Firestore deleteAll. Local delete only.");
                }
            } catch (Exception e) {
                Log.e(TAG, "DeleteAll: Failed to delete all categories locally.", e);
            }
        });
    }

    /**
     * Обновление суммы категории в Room и Firestore
     *
     * @param name
     * @param newSum
     */
    public void updateCategorySumByName(String name, double newSum) {
        databaseWriteExecutor.execute(() -> {
            try {
                Categories categoryToUpdate = daoCategories.getSingleCategoryByNameBlocking(name);
                if (categoryToUpdate != null) {
                    categoryToUpdate.setSum(newSum);
                    categoryToUpdate.setSynced(false);
                    daoCategories.update(categoryToUpdate);
                    attemptFirestoreCategorySync(categoryToUpdate);
                } else {
                    Log.e(TAG, "UpdateCategorySumByName: Category not found for sum update: " + name);
                }
            } catch (Exception e) {
                Log.e(TAG, "UpdateCategorySumByName: Failed to update category sum locally for: " + name, e);
            }
        });
    }

    /**
     * Получение всех категорий
     *
     * @return
     */
    public LiveData<List<Categories>> getAllCategories() {
        return allCategories;
    }

    /**
     * Получение категории по имени категории
     *
     * @param categoryName
     * @return
     */
    public LiveData<Categories> getCategoryByName(String categoryName) {
        return daoCategories.getCategoryByName(categoryName);
    }

    /**
     * Получение категории по имени категории
     *
     * @param categoryName
     * @return
     */
    public Task<Categories> getCategoryByNameAsync(String categoryName) {
        return Tasks.call(databaseWriteExecutor, () -> {
            return daoCategories.getSingleCategoryByNameBlocking(categoryName);
        });
    }

    /**
     * Получение суммы категории по имени категории
     *
     * @param categoryName
     * @return
     */
    public LiveData<Double> getTotalSumByCategory(String categoryName) {
        return daoCategories.getTotalSumByCategory(categoryName);
    }

    /**
     * Попытка синхронизации категории с Firestore через .set()
     *
     * @param category
     */
    private void attemptFirestoreCategorySync(Categories category) {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference categoriesRef = getUserCategoriesCollection();

        if (user == null || categoriesRef == null) {
            Log.d(TAG, "attemptFirestoreCategorySync SKIPPED for " + category.getCategoryName() + ". User not authenticated or Firestore ref null.");
            Toast.makeText(null, "attemptFirestoreCategorySync SKIPPED for " + category.getCategoryName() + ". User not authenticated or Firestore ref null.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
            categoriesRef.document(category.getFirestoreId()).set(category)
                    .addOnSuccessListener(aVoid -> {
                        databaseWriteExecutor.execute(() -> {
                            category.setSynced(true);
                            daoCategories.update(category);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "attemptFirestoreCategorySync: Firestore Category UPDATE FAILED for Room ID: " + category.getId() + ": " + e.getMessage(), e);
                    });
        } else {
            categoriesRef.add(category)
                    .addOnSuccessListener(documentReference -> {
                        String firestoreId = documentReference.getId();
                        databaseWriteExecutor.execute(() -> {
                            category.setFirestoreId(firestoreId);
                            category.setSynced(true);
                            daoCategories.update(category);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "attemptFirestoreCategorySync: Firestore Category ADD FAILED for Room ID: " + category.getId() + ": " + e.getMessage(), e);
                    });
        }
    }

    /**
     * Получение всех несинхронизированных категорий из Room
     *
     * @return
     */
    public Task<Void> syncUnsyncedCategoriesToFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference categoriesRef = getUserCategoriesCollection();

        if (user == null || categoriesRef == null) {
            Log.d(TAG, "syncUnsyncedCategoriesToFirestore SKIPPED. User not authenticated or Firestore ref null.");
            Toast.makeText(null, "syncUnsyncedCategoriesToFirestore SKIPPED. User not authenticated or Firestore ref null.", Toast.LENGTH_SHORT).show();
            return Tasks.forResult(null);
        }

        return Tasks.call(databaseWriteExecutor, (Callable<List<Categories>>) () -> daoCategories.getUnsyncedCategoriesBlocking())
                .continueWithTask(roomTask -> {
                    if (!roomTask.isSuccessful()) {
                        return Tasks.forException(roomTask.getException());
                    }

                    List<Categories> unsyncedCategories = roomTask.getResult();

                    if (unsyncedCategories == null || unsyncedCategories.isEmpty()) {
                        Log.d(TAG, "syncUnsyncedCategoriesToFirestore: No unsynced categories found in Room to push.");
                        return Tasks.forResult(null);
                    }

                    WriteBatch batch = db.batch();
                    List<Task<Void>> individualFirestoreReadWriteTasks = new ArrayList<>();

                    for (Categories category : unsyncedCategories) {
                        if (category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
                            batch.set(categoriesRef.document(category.getFirestoreId()), category);
                            individualFirestoreReadWriteTasks.add(Tasks.forResult(null));
                        } else {
                            Log.d(TAG, "syncUnsyncedCategoriesToFirestore: Checking for existing category by name for Room ID: " + category.getId());
                            individualFirestoreReadWriteTasks.add(categoriesRef.whereEqualTo("categoryName", category.getCategoryName())
                                    .get()
                                    .continueWithTask(queryTask -> {
                                        if (!queryTask.isSuccessful()) {
                                            Log.e(TAG, "syncUnsyncedCategoriesToFirestore: Failed to check existing category by name: " + category.getCategoryName(), queryTask.getException());
                                            return Tasks.forException(queryTask.getException());
                                        }

                                        if (!queryTask.getResult().isEmpty()) {
                                            QueryDocumentSnapshot existingDoc = (QueryDocumentSnapshot) queryTask.getResult().getDocuments().get(0);
                                            DocumentReference docRef = categoriesRef.document(existingDoc.getId());

                                            category.setFirestoreId(existingDoc.getId());
                                            category.setSynced(true);
                                            return Tasks.call(databaseWriteExecutor, (Callable<Void>) () -> {
                                                daoCategories.update(category);
                                                batch.set(docRef, category);
                                                Log.d(TAG, "syncUnsyncedCategoriesToFirestore: MERGED existing Firestore category: " + category.getCategoryName() + ", Room ID: " + category.getId() + ", Firestore ID: " + existingDoc.getId());
                                                return null;
                                            });

                                        } else {
                                            DocumentReference newDocRef = categoriesRef.document();
                                            category.setFirestoreId(newDocRef.getId());
                                            category.setSynced(true);
                                            return Tasks.call(databaseWriteExecutor, (Callable<Void>) () -> {
                                                daoCategories.update(category);
                                                batch.set(newDocRef, category);
                                                Log.d(TAG, "syncUnsyncedCategoriesToFirestore: ADDING new Firestore category: " + category.getCategoryName() + ", Room ID: " + category.getId() + ", Firestore ID: " + newDocRef.getId());
                                                return null;
                                            });
                                        }
                                    }));
                        }
                    }

                    return Tasks.whenAll(individualFirestoreReadWriteTasks).continueWithTask(allOpsCompletedTask -> {
                        if (!allOpsCompletedTask.isSuccessful()) {
                            Log.e(TAG, "syncUnsyncedCategoriesToFirestore: One or more individual push tasks failed before batch commit: " + allOpsCompletedTask.getException().getMessage());
                            return Tasks.forException(allOpsCompletedTask.getException());
                        }
                        Task<Void> commitResultTask = batch.commit();

                        commitResultTask.addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "syncUnsyncedCategoriesToFirestore: Firestore batch commit for unsynced categories successful.");
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "syncUnsyncedCategoriesToFirestore: Firestore batch commit failed: " + e.getMessage(), e);
                        });

                        return commitResultTask;
                    });
                });
    }

    /**
     * Получение всех категорий из Firestore через .get()
     *
     * @return
     */
    public Task<Void> syncCategoriesFromFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference categoriesRef = getUserCategoriesCollection();

        if (user == null || categoriesRef == null) {
            Log.d(TAG, "SyncFromFirestore: Cannot sync Categories from Firestore: User not authenticated.");
            return Tasks.forResult(null);
        }

        return categoriesRef.get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "SyncFromFirestore: Failed to fetch categories from Firestore: " + task.getException().getMessage());
                        return Tasks.forException(task.getException());
                    }

                    return Tasks.call(databaseWriteExecutor, (Callable<Void>) () -> {
                        List<Categories> firestoreCategories = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Categories firestoreCategory = document.toObject(Categories.class);
                            firestoreCategory.setFirestoreId(document.getId());
                            firestoreCategory.setSynced(true);
                            firestoreCategories.add(firestoreCategory);
                        }

                        List<Categories> localAllCategories = daoCategories.getAllCategoriesBlocking();
                        if (localAllCategories == null) {
                            localAllCategories = new ArrayList<>();
                        }

                        List<Categories> localSyncedCategoriesForDeletion = new ArrayList<>();
                        for (Categories cat : localAllCategories) {
                            if (cat.isSynced() && cat.getFirestoreId() != null) {
                                localSyncedCategoriesForDeletion.add(cat);
                            }
                        }

                        for (Categories firestoreCategory : firestoreCategories) {
                            Categories existingLocalCategory = null;

                            if (firestoreCategory.getFirestoreId() != null) {
                                existingLocalCategory = daoCategories.getCategoryByFirestoreId(firestoreCategory.getFirestoreId());
                            }

                            if (existingLocalCategory != null) {
                                firestoreCategory.setId(existingLocalCategory.getId());
                                daoCategories.update(firestoreCategory);
                            } else {
                                Categories existingLocalCategoryByName = daoCategories.getSingleCategoryByNameBlocking(firestoreCategory.getCategoryName());

                                if (existingLocalCategoryByName != null) {
                                    firestoreCategory.setId(existingLocalCategoryByName.getId());
                                    firestoreCategory.setSynced(true);
                                    daoCategories.update(firestoreCategory);
                                } else {
                                    firestoreCategory.setId(0);
                                    daoCategories.insert(firestoreCategory);
                                }
                            }
                        }

                        for (Categories localCategory : localSyncedCategoriesForDeletion) {
                            boolean foundInFirestore = false;
                            for (Categories firestoreCategory : firestoreCategories) {
                                if (localCategory.getFirestoreId().equals(firestoreCategory.getFirestoreId())) {
                                    foundInFirestore = true;
                                    break;
                                }
                            }
                            if (!foundInFirestore) {
                                daoCategories.delete(localCategory);
                            }
                        }

                        return null;
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "SyncFromFirestore: Full categories sync from Firestore FAILED: " + e.getMessage(), e);
                });
    }

    /**
     * Инициализация дефолтных категорий в Room
     */
    public void initializeDefaultCategories() {
        databaseWriteExecutor.execute(() -> {
            try {
                int roomCount = daoCategories.getCategoryCount();
                if (roomCount == 0) {
                    CollectionReference categoriesRef = getUserCategoriesCollection();
                    if (categoriesRef != null) {
                        categoriesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.isEmpty()) {
                                addDefaultCategories();
                            } else {
                                Log.d(TAG, "initializeDefaultCategories: Room empty but Firestore not empty. Relying on AuthStateListener to pull categories.");
                            }
                        }).addOnFailureListener(e -> {
                            addDefaultCategories();
                        });
                    } else {
                        Log.d(TAG, "initializeDefaultCategories: User not authenticated, adding default categories locally.");
                        addDefaultCategories();
                    }
                } else {
                    Log.d(TAG, "initializeDefaultCategories: Room is not empty, skipping default category addition.");
                }
            } catch (Exception e) {
                Log.e(TAG, "initializeDefaultCategories: Error during initialization check.", e);
            }
        });
    }

    /**
     * Добавление дефолтных категорий в Room и Firestore
     */
    private void addDefaultCategories() {
        databaseWriteExecutor.execute(() -> {
            try {
                if (daoCategories.getSingleCategoryByNameBlocking("Другое") == null) {
                    Categories defaultCategory = new Categories("Другое", 0.0);
                    insert(defaultCategory);
                }
            } catch (Exception e) {
                Log.e(TAG, "addDefaultCategories: Error during adding default categories.", e);
            }
        });
    }
}
