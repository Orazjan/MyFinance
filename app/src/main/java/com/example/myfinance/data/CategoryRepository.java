package com.example.myfinance.data;

import android.util.Log;

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
import java.util.Objects;
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
                        if (!pushTask.isSuccessful()) {
                            Log.e(TAG, "Failed to push unsynced local categories to Firestore: " + (pushTask.getException() != null ? pushTask.getException().getMessage() : "Unknown error"));
                        }
                        syncCategoriesFromFirestore().addOnCompleteListener(pullTask -> {
                            isAuthSyncInProgress.set(false);
                            if (!pullTask.isSuccessful()) {
                                Log.e(TAG, "Failed to pull categories from Firestore: " + (pullTask.getException() != null ? pullTask.getException().getMessage() : "Unknown error"));
                            }
                        });
                    });
                } else {
                    Log.d(TAG, "Auth state changed: User is logged in, but sync already in progress. Skipping additional trigger.");
                }
            } else {
                isAuthSyncInProgress.set(false);
            }
        };
        auth.addAuthStateListener(mAuthStateListener);
        initializeDefaultCategories();
    }

    private CollectionReference getUserCategoriesCollection() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            return db.collection(USERS_COLLECTION)
                    .document(user.getEmail())
                    .collection(FIRESTORE_CATEGORIES_COLLECTION_NAME);
        }
        return null;
    }

    public void insert(Categories category) {
        category.setSynced(false);
        category.setFirestoreId(null);

        databaseWriteExecutor.execute(() -> {
            try {
                long roomId = daoCategories.insert(category);
                category.setId((int) roomId);

                if (auth.getCurrentUser() != null) {
                    syncUnsyncedCategoriesToFirestore();
                }
            } catch (Exception e) {
                Log.e(TAG, "Insert: Failed to insert category locally: " + category.getCategoryName(), e);
            }
        });
    }

    public void update(Categories category) {
        category.setSynced(false);

        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.update(category);
                if (auth.getCurrentUser() != null) {
                    syncUnsyncedCategoriesToFirestore();
                }
            } catch (Exception e) {
                Log.e(TAG, "Update: Failed to update category locally: " + category.getCategoryName(), e);
            }
        });
    }

    public void delete(Categories category) {
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.delete(category);

                CollectionReference categoriesRef = getUserCategoriesCollection();
                if (categoriesRef != null && category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
                    categoriesRef.document(category.getFirestoreId()).delete()
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Delete: Category successfully deleted from Firestore: " + category.getFirestoreId()))
                            .addOnFailureListener(e -> Log.e(TAG, "Delete: Error deleting category from Firestore: " + category.getFirestoreId(), e));
                }
            } catch (Exception e) {
                Log.e(TAG, "Delete: Failed to delete category locally: " + category.getCategoryName(), e);
            }
        });
    }

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
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "DeleteAll: Failed to delete all categories locally.", e);
            }
        });
    }

    public void updateCategorySumByName(String name, double newSum) {
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.updateCategorySum(name, newSum);
                Categories updatedCategory = daoCategories.getSingleCategoryByNameBlocking(name);
                if (updatedCategory != null) {
                    if (auth.getCurrentUser() != null) {
                        syncUnsyncedCategoriesToFirestore();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "UpdateCategorySumByName: Failed to update category sum locally for: " + name, e);
            }
        });
    }

    public LiveData<List<Categories>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Categories> getCategoryById(int id) {
        return daoCategories.getCategoryById(id);
    }

    public LiveData<Categories> getCategoryByName(String categoryName) {
        return daoCategories.getCategoryByName(categoryName);
    }

    public LiveData<Double> getTotalSumByCategory(String categoryName) {
        return daoCategories.getTotalSumByCategory(categoryName);
    }

    public LiveData<Categories> getCategoryBySum(double sum) {
        return daoCategories.getCategoryBySum(sum);
    }

    public Task<Categories> getCategoryByNameAsync(String categoryName) {
        return Tasks.call(databaseWriteExecutor, () -> {
            return daoCategories.getSingleCategoryByNameBlocking(categoryName);
        });
    }

    public Task<Void> syncUnsyncedCategoriesToFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference categoriesRef = getUserCategoriesCollection();

        if (user == null || categoriesRef == null) {
            return Tasks.forResult(null);
        }

        return Tasks.call(databaseWriteExecutor, daoCategories::getUnsyncedCategoriesBlocking)
                .continueWithTask(roomTask -> {
                    if (!roomTask.isSuccessful()) {
                        return Tasks.forException(roomTask.getException());
                    }

                    List<Categories> unsyncedCategories = roomTask.getResult();

                    if (unsyncedCategories == null || unsyncedCategories.isEmpty()) {
                        return Tasks.forResult(null);
                    }

                    WriteBatch batch = db.batch();
                    List<Task<Void>> individualFirestoreReadWriteTasks = new ArrayList<>();

                    for (Categories category : unsyncedCategories) {
                        if (category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
                            DocumentReference docRef = categoriesRef.document(category.getFirestoreId());
                            batch.set(docRef, category);
                        } else {
                            individualFirestoreReadWriteTasks.add(categoriesRef.whereEqualTo("categoryName", category.getCategoryName())
                                    .get()
                                    .continueWithTask(queryTask -> {
                                        if (!queryTask.isSuccessful()) {
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
                                                return null;
                                            });

                                        } else {
                                            DocumentReference newDocRef = categoriesRef.document();
                                            category.setFirestoreId(newDocRef.getId());
                                            category.setSynced(true);
                                            return Tasks.call(databaseWriteExecutor, (Callable<Void>) () -> {
                                                daoCategories.update(category);
                                                batch.set(newDocRef, category);
                                                return null;
                                            });
                                        }
                                    }));
                        }
                    }

                    return Tasks.whenAll(individualFirestoreReadWriteTasks).continueWithTask(allOpsCompletedTask -> {
                        if (!allOpsCompletedTask.isSuccessful()) {
                            return Tasks.forException(allOpsCompletedTask.getException());
                        }
                        Task<Void> commitResultTask = batch.commit();

                        commitResultTask.addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "SyncUnsynced: Firestore batch commit for unsynced categories successful.");
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "SyncUnsynced: Firestore batch commit failed: " + e.getMessage(), e);
                        });

                        return commitResultTask;
                    });
                });
    }

    public Task<Void> syncCategoriesFromFirestore() {
        FirebaseUser user = auth.getCurrentUser();
        CollectionReference categoriesRef = getUserCategoriesCollection();

        if (user == null || categoriesRef == null) {
            return Tasks.forResult(null);
        }

        return categoriesRef.get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
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
                            if (cat.getFirestoreId() != null && !cat.getFirestoreId().isEmpty()) {
                                localSyncedCategoriesForDeletion.add(cat);
                            }
                        }

                        for (Categories firestoreCategory : firestoreCategories) {
                            Categories existingLocalCategory = null;

                            if (firestoreCategory.getFirestoreId() != null && !firestoreCategory.getFirestoreId().isEmpty()) {
                                existingLocalCategory = daoCategories.getCategoryByFirestoreId(firestoreCategory.getFirestoreId());
                            }

                            if (existingLocalCategory != null) {
                                firestoreCategory.setId(existingLocalCategory.getId());
                                daoCategories.update(firestoreCategory);
                            } else {
                                Categories existingLocalCategoryByName = daoCategories.getSingleCategoryByNameBlocking(firestoreCategory.getCategoryName());

                                if (existingLocalCategoryByName != null) {
                                    firestoreCategory.setId(existingLocalCategoryByName.getId());
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
                                Log.d(TAG, "initializeDefaultCategories: Room empty but Firestore not empty. Relying on auth sync to pull (already happened or will happen).");
                            }
                        }).addOnFailureListener(e -> {
                            addDefaultCategories();
                        });
                    } else {
                        addDefaultCategories();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "initializeDefaultCategories: Error during initialization check.", e);
            }
        });
    }

    private void addDefaultCategories() {
        databaseWriteExecutor.execute(() -> {
            try {
                List<Categories> defaultCategoriesToAdd = new ArrayList<>();
                if (daoCategories.getSingleCategoryByNameBlocking("Другое") == null) {
                    defaultCategoriesToAdd.add(new Categories("Другое", 0, "Расход"));
                }

                for (Categories category : defaultCategoriesToAdd) {
                    insert(category);
                }
                if (auth.getCurrentUser() != null && !defaultCategoriesToAdd.isEmpty()) {
                    syncUnsyncedCategoriesToFirestore();
                }
            } catch (Exception e) {
                Log.e(TAG, "addDefaultCategories: Error during adding default categories.", e);
            }
        });
    }
}
