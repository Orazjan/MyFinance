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
        Log.d(TAG, "CategoryRepository constructor called. Instance created.");

        this.daoCategories = daoCategories;
        this.allCategories = daoCategories.getAllCategories();

        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                if (isAuthSyncInProgress.compareAndSet(false, true)) {
                    Log.d(TAG, "Auth state changed: User is logged in. Starting Category sync (push then pull). User Email: " + user.getEmail());

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
        initializeDefaultCategories();
    }

    private CollectionReference getUserCategoriesCollection() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            return db.collection(USERS_COLLECTION)
                    .document(user.getEmail())
                    .collection(FIRESTORE_CATEGORIES_COLLECTION_NAME);
        }
        Log.w(TAG, "getUserCategoriesCollection: User is null or Email is empty. Cannot get Firestore collection reference.");
        return null;
    }

    public void insert(Categories category) {
        category.setSynced(false);
        category.setFirestoreId(null);

        Log.d(TAG, "Insert: Attempting to insert category locally: " + category.getCategoryName() + " (Room ID: " + category.getId() + ", isSynced: " + category.isSynced() + ")");

        databaseWriteExecutor.execute(() -> {
            try {
                long roomId = daoCategories.insert(category);
                category.setId((int) roomId);

                Log.d(TAG, "Insert: Category inserted locally: " + category.getCategoryName() + " with Room ID: " + category.getId());

                if (auth.getCurrentUser() != null) {
                    Log.d(TAG, "Insert: User logged in, attempting immediate sync for " + category.getCategoryName());
                    syncUnsyncedCategoriesToFirestore();
                } else {
                    Log.d(TAG, "Insert: User not logged in. " + category.getCategoryName() + " will be synced on next login.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Insert: Failed to insert category locally: " + category.getCategoryName(), e);
            }
        });
    }

    public void update(Categories category) {
        category.setSynced(false);

        Log.d(TAG, "Update: Attempting to update category locally: " + category.getCategoryName() + " (Room ID: " + category.getId() + ", isSynced: " + category.isSynced() + ")");
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.update(category);
                Log.d(TAG, "Update: Category updated locally: " + category.getCategoryName() + " (Room ID: " + category.getId() + ")");
                if (auth.getCurrentUser() != null) {
                    Log.d(TAG, "Update: User logged in, attempting immediate sync for " + category.getCategoryName());
                    syncUnsyncedCategoriesToFirestore();
                } else {
                    Log.d(TAG, "Update: User not logged in. " + category.getCategoryName() + " will be synced on next login.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Update: Failed to update category locally: " + category.getCategoryName(), e);
            }
        });
    }

    public void delete(Categories category) {
        Log.d(TAG, "Delete: Attempting to delete category locally: " + category.getCategoryName() + " (Room ID: " + category.getId() + ")");
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.delete(category);
                Log.d(TAG, "Delete: Category deleted from Room: " + category.getCategoryName());

                CollectionReference categoriesRef = getUserCategoriesCollection();
                if (categoriesRef != null && category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
                    Log.d(TAG, "Delete: Attempting to delete from Firestore: " + category.getCategoryName() + " (Firestore ID: " + category.getFirestoreId() + ")");
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

    public void deleteAll() {
        Log.d(TAG, "DeleteAll: Attempting to delete all categories locally.");
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.deleteAll();
                Log.d(TAG, "DeleteAll: All categories deleted from Room.");

                CollectionReference categoriesRef = getUserCategoriesCollection();
                if (categoriesRef != null) {
                    Log.d(TAG, "DeleteAll: Attempting to delete all categories from Firestore.");
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

    public void updateCategorySumByName(String name, double newSum) {
        Log.d(TAG, "UpdateCategorySumByName: Attempting to update sum for: " + name + " to " + newSum);
        databaseWriteExecutor.execute(() -> {
            try {
                daoCategories.updateCategorySum(name, newSum);
                Categories updatedCategory = daoCategories.getSingleCategoryByNameBlocking(name);
                if (updatedCategory != null) {
                    Log.d(TAG, "UpdateCategorySumByName: Category sum updated locally and marked unsynced: " + name + " (Room ID: " + updatedCategory.getId() + ")");
                    if (auth.getCurrentUser() != null) {
                        Log.d(TAG, "UpdateCategorySumByName: User logged in, attempting immediate sync for " + name);
                        syncUnsyncedCategoriesToFirestore();
                    } else {
                        Log.d(TAG, "UpdateCategorySumByName: User not logged in. " + name + " sum update will be synced on next login.");
                    }
                } else {
                    Log.e(TAG, "UpdateCategorySumByName: Category not found for sum update by name: " + name);
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
            Log.d(TAG, "SyncUnsynced: Cannot sync unsynced Categories to Firestore: User not authenticated or Firestore ref null.");
            return Tasks.forResult(null);
        }

        Log.d(TAG, "SyncUnsynced: Starting PUSH operation.");
        return Tasks.call(databaseWriteExecutor, daoCategories::getUnsyncedCategoriesBlocking)
                .continueWithTask(roomTask -> {
                    if (!roomTask.isSuccessful()) {
                        Log.e(TAG, "SyncUnsynced: Failed to get unsynced categories from Room: " + roomTask.getException().getMessage());
                        return Tasks.forException(roomTask.getException());
                    }

                    List<Categories> unsyncedCategories = roomTask.getResult();

                    if (unsyncedCategories == null || unsyncedCategories.isEmpty()) {
                        Log.d(TAG, "SyncUnsynced: No unsynced categories found in Room to push.");
                        return Tasks.forResult(null);
                    }

                    WriteBatch batch = db.batch();
                    List<Task<Void>> individualFirestoreReadWriteTasks = new ArrayList<>();

                    Log.d(TAG, "SyncUnsynced: Processing " + unsyncedCategories.size() + " categories for push.");
                    for (Categories category : unsyncedCategories) {
                        if (category.getFirestoreId() != null && !category.getFirestoreId().isEmpty()) {
                            DocumentReference docRef = categoriesRef.document(category.getFirestoreId());
                            batch.set(docRef, category);
                            Log.d(TAG, "SyncUnsynced: BATCH SET for existing category: " + category.getCategoryName() + ", ID: " + category.getFirestoreId());
                        } else {
                            individualFirestoreReadWriteTasks.add(categoriesRef.whereEqualTo("categoryName", category.getCategoryName())
                                    .get()
                                    .continueWithTask(queryTask -> {
                                        if (!queryTask.isSuccessful()) {
                                            Log.e(TAG, "SyncUnsynced: Failed to check existing category by name: " + category.getCategoryName(), queryTask.getException());
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
                                                Log.d(TAG, "SyncUnsynced: MERGED existing Firestore category: " + category.getCategoryName() + ", New Room ID: " + category.getId() + ", Firestore ID: " + existingDoc.getId());
                                                return null;
                                            });

                                        } else {
                                            DocumentReference newDocRef = categoriesRef.document();
                                            category.setFirestoreId(newDocRef.getId());
                                            category.setSynced(true);
                                            return Tasks.call(databaseWriteExecutor, (Callable<Void>) () -> {
                                                daoCategories.update(category);
                                                batch.set(newDocRef, category);
                                                Log.d(TAG, "SyncUnsynced: ADDING new Firestore category: " + category.getCategoryName() + ", New Room ID: " + category.getId() + ", Firestore ID: " + newDocRef.getId());
                                                return null;
                                            });
                                        }
                                    }));
                        }
                    }

                    return Tasks.whenAll(individualFirestoreReadWriteTasks).continueWithTask(allOpsCompletedTask -> {
                        if (!allOpsCompletedTask.isSuccessful()) {
                            Log.e(TAG, "SyncUnsynced: One or more individual push tasks failed before batch commit: " + allOpsCompletedTask.getException().getMessage());
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
            Log.d(TAG, "SyncFromFirestore: Cannot sync Categories from Firestore: User not authenticated.");
            return Tasks.forResult(null);
        }

        Log.d(TAG, "SyncFromFirestore: Starting PULL operation.");
        return categoriesRef.get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "SyncFromFirestore: Failed to fetch categories from Firestore: " + task.getException().getMessage());
                        throw task.getException();
                    }

                    return Tasks.call(databaseWriteExecutor, (Callable<Void>) () -> {
                        List<Categories> firestoreCategories = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Categories firestoreCategory = document.toObject(Categories.class);
                            firestoreCategory.setFirestoreId(document.getId());
                            firestoreCategory.setSynced(true);
                            firestoreCategories.add(firestoreCategory);
                        }
                        Log.d(TAG, "SyncFromFirestore: Fetched " + firestoreCategories.size() + " categories from Firestore.");

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
                        Log.d(TAG, "SyncFromFirestore: Found " + localSyncedCategoriesForDeletion.size() + " synced local categories for potential deletion check.");

                        for (Categories firestoreCategory : firestoreCategories) {
                            Categories existingLocalCategory = null;

                            if (firestoreCategory.getFirestoreId() != null && !firestoreCategory.getFirestoreId().isEmpty()) {
                                existingLocalCategory = daoCategories.getCategoryByFirestoreId(firestoreCategory.getFirestoreId());
                            }

                            if (existingLocalCategory != null) {
                                firestoreCategory.setId(existingLocalCategory.getId());
                                daoCategories.update(firestoreCategory);
                                Log.d(TAG, "SyncFromFirestore: MERGE - Updated local category by firestoreId: " + firestoreCategory.getCategoryName() + ", Room ID: " + firestoreCategory.getId());
                            } else {
                                Categories existingLocalCategoryByName = daoCategories.getSingleCategoryByNameBlocking(firestoreCategory.getCategoryName());

                                if (existingLocalCategoryByName != null) {
                                    firestoreCategory.setId(existingLocalCategoryByName.getId());
                                    daoCategories.update(firestoreCategory);
                                    Log.d(TAG, "SyncFromFirestore: MERGE - Merged local category by name, assigned firestoreId: " + firestoreCategory.getCategoryName() + ", Room ID: " + firestoreCategory.getId());
                                } else {
                                    firestoreCategory.setId(0);
                                    daoCategories.insert(firestoreCategory);
                                    Log.d(TAG, "SyncFromFirestore: ADD - New category added from Firestore to Room: " + firestoreCategory.getCategoryName() + ", Firestore ID: " + firestoreCategory.getFirestoreId());
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
                                Log.d(TAG, "SyncFromFirestore: DELETE - Category deleted from Room (removed from Firestore): " + localCategory.getCategoryName() + ", Room ID: " + localCategory.getId());
                            }
                        }

                        Log.d(TAG, "SyncFromFirestore: Full categories sync from Firestore completed.");
                        return null;
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "SyncFromFirestore: Full categories sync from Firestore FAILED: " + e.getMessage(), e);
                });
    }

    public void initializeDefaultCategories() {
        Log.d(TAG, "initializeDefaultCategories: Starting default category check.");
        databaseWriteExecutor.execute(() -> {
            try {
                int roomCount = daoCategories.getCategoryCount();
                Log.d(TAG, "initializeDefaultCategories: Room category count = " + roomCount);

                if (roomCount == 0) {
                    CollectionReference categoriesRef = getUserCategoriesCollection();
                    if (categoriesRef != null) {
                        Log.d(TAG, "initializeDefaultCategories: Room is empty, checking Firestore.");
                        categoriesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            Log.d(TAG, "initializeDefaultCategories: Firestore category count = " + queryDocumentSnapshots.size());
                            if (queryDocumentSnapshots.isEmpty()) {
                                Log.d(TAG, "initializeDefaultCategories: Room and Firestore are empty. Adding default categories.");
                                addDefaultCategories();
                            } else {
                                Log.d(TAG, "initializeDefaultCategories: Room empty but Firestore not empty. Relying on auth sync to pull (already happened or will happen).");
                            }
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Error checking Firestore for categories during initialization: " + e.getMessage());
                            Log.d(TAG, "initializeDefaultCategories: Failed to check Firestore. Adding default categories locally as fallback.");
                            addDefaultCategories();
                        });
                    } else {
                        Log.d(TAG, "initializeDefaultCategories: User not authenticated, adding default categories locally as initial state.");
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

    private void addDefaultCategories() {
        Log.d(TAG, "addDefaultCategories: Attempting to add default categories locally.");
        databaseWriteExecutor.execute(() -> {
            try {
                List<Categories> defaultCategoriesToAdd = new ArrayList<>();
                if (daoCategories.getSingleCategoryByNameBlocking("Другое") == null) {
                    defaultCategoriesToAdd.add(new Categories("Другое", 0));
                }
                if (daoCategories.getSingleCategoryByNameBlocking("Доход") == null) {
                    defaultCategoriesToAdd.add(new Categories("Доход", 0));
                }
                if (daoCategories.getSingleCategoryByNameBlocking("Расход") == null) {
                    defaultCategoriesToAdd.add(new Categories("Расход", 0));
                }

                for (Categories category : defaultCategoriesToAdd) {
                    insert(category);
                    Log.d(TAG, "addDefaultCategories: Default category '" + category.getCategoryName() + "' added locally.");
                }
                if (auth.getCurrentUser() != null && !defaultCategoriesToAdd.isEmpty()) {
                    Log.d(TAG, "addDefaultCategories: Default categories added, triggering unsynced push.");
                    syncUnsyncedCategoriesToFirestore();
                } else {
                    Log.d(TAG, "addDefaultCategories: No new default categories to push or user not logged in.");
                }
                Log.d(TAG, "addDefaultCategories: Finished processing default categories.");
            } catch (Exception e) {
                Log.e(TAG, "addDefaultCategories: Error during adding default categories.", e);
            }
        });
    }
}
