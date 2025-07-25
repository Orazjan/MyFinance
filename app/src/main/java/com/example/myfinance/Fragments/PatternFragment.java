package com.example.myfinance.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinance.Adapters.CategoryViewAdapter;
import com.example.myfinance.Models.CategoryViewModel;
import com.example.myfinance.Prevalent.CategoryItem;
import com.example.myfinance.R;
import com.example.myfinance.data.Categories;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class PatternFragment extends Fragment {
    private final String TAG = "PatternFragment";

    private TextInputLayout reasonInputLayout, sumInputLayout, operationTypeInputLayout;
    private TextInputEditText reasonEditText, sumEditText;
    private MaterialButton btnAddPattern;
    private double sumInDouble;
    private String reason;
    private RecyclerView RecyclerForCategory;
    private AutoCompleteTextView spinnerForChooseActiveOrPassive;
    private String selectedOperationType;

    private CategoryViewModel categoryViewModel;
    private CategoryViewAdapter CategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patterns_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reasonInputLayout = view.findViewById(R.id.reasonInputLayout);
        sumInputLayout = view.findViewById(R.id.sumInputLayout);
        reasonEditText = view.findViewById(R.id.reasonEditText);
        sumEditText = view.findViewById(R.id.sumEditText);
        btnAddPattern = view.findViewById(R.id.btnaddPattern);
        RecyclerForCategory = view.findViewById(R.id.RecyclerForCategory);
        spinnerForChooseActiveOrPassive = view.findViewById(R.id.SpinnerForChooseActiveOrPassive);
        operationTypeInputLayout = view.findViewById(R.id.operationTypeInputLayout);

        categoryViewModel = new ViewModelProvider(requireActivity(), new CategoryViewModel.TaskViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);

        loadAndDisplay();
        setupTextWatchersAndClickListeners();
    }

    /**
     * Загружает и отображает все категории из базы данных Room.
     * Использует LiveData из CategoryViewModel для реактивного обновления UI.
     * Также настраивает AutoCompleteTextView для выбора типа операции и его слушатель.
     */
    private void loadAndDisplay() {

        List<String> ActiveAndPassive = new ArrayList<>();
        ActiveAndPassive.add("Доход");
        ActiveAndPassive.add("Расход");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, ActiveAndPassive);
        spinnerForChooseActiveOrPassive.setAdapter(adapter);

        if (!ActiveAndPassive.isEmpty()) {
            spinnerForChooseActiveOrPassive.setText(ActiveAndPassive.get(0), false);
            selectedOperationType = ActiveAndPassive.get(0);
        }

        spinnerForChooseActiveOrPassive.setOnClickListener(v -> spinnerForChooseActiveOrPassive.showDropDown());

        spinnerForChooseActiveOrPassive.setOnItemClickListener((parent, view, position, id) -> {
            selectedOperationType = parent.getItemAtPosition(position).toString();
            updateAddButtonState();
        });

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            List<CategoryItem> categoryItems = new ArrayList<>();
            if (categories != null) {
                for (Categories category : categories) {
                    categoryItems.add(new CategoryItem(category.getCategoryName(), category.getSum(), category.getOperationType()));
                }
            }

            if (CategoryAdapter == null) {
                RecyclerForCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
                CategoryAdapter = new CategoryViewAdapter(categoryItems);
                RecyclerForCategory.setAdapter(CategoryAdapter);
                onClickAdapter();
            } else {
                CategoryAdapter.clear();
                CategoryAdapter.setItems(categoryItems);
                CategoryAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Настраивает слушатель кликов для адаптера RecyclerView.
     * Теперь использует getCategoryByNameAsync для одноразового получения данных.
     */
    private void onClickAdapter() {
        if (CategoryAdapter != null) {
            CategoryAdapter.setOnItemClickListener(item -> {
                categoryViewModel.getCategoryByNameAsync(item.getCategoryName()).addOnSuccessListener(category -> {
                    if (category != null) {
                        showCategoryActionsDialog(category);
                    } else {
                        Toast.makeText(requireContext(), "Категория не найдена или была удалена.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "onClickAdapter: Failed to retrieve category asynchronously for " + item.getCategoryName() + ": " + e.getMessage(), e);
                    Toast.makeText(requireContext(), "Ошибка при загрузке категории.", Toast.LENGTH_SHORT).show();
                });
            });
        } else {
            Log.e(TAG, "onClickAdapter(): CategoryAdapter is null. This should not happen if called correctly.");
        }
    }

    /**
     * Объединяет настройку TextWatchers и OnClickListener для кнопок.
     */
    private void setupTextWatchersAndClickListeners() {
        reasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                reason = editable.toString().trim();
                if (reason.isEmpty()) {
                    reasonInputLayout.setError("Причина не может быть пустой");
                } else {
                    reasonInputLayout.setError(null);
                }
                updateAddButtonState();
            }
        });

        sumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String sumText = editable.toString().trim();
                if (sumText.isEmpty()) {
                    sumInputLayout.setError("Сумма не может быть пустой");
                    sumInDouble = 0;
                } else {
                    try {
                        sumInDouble = Double.parseDouble(sumText);
                        sumInputLayout.setError(null);
                    } catch (NumberFormatException e) {
                        sumInputLayout.setError("Неверный формат суммы");
                        sumInDouble = 0;
                    }
                }
                updateAddButtonState();
            }
        });

        btnAddPattern.setOnClickListener(v -> {
            Log.d(TAG, "btnAddPattern clicked.");
            if (reason == null || reason.isEmpty()) {
                reasonInputLayout.setError("Введите причину");
                Toast.makeText(requireContext(), "Причина не может быть пустой.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Add button clicked: Reason is empty.");
                return;
            }

            if (sumInDouble <= 0) {
                sumInputLayout.setError("Сумма должна быть больше нуля");
                Toast.makeText(requireContext(), "Сумма должна быть больше нуля.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Add button clicked: Sum is not positive.");
                return;
            }

            Categories newCategory = new Categories(reason, sumInDouble, selectedOperationType);
            Log.d(TAG, "btnAddPattern: Attempting to insert new category via ViewModel: " + newCategory.getCategoryName() + ", Sum: " + newCategory.getSum() + ", Operation Type: " + newCategory.getOperationType());
            categoryViewModel.insert(newCategory);

            Toast.makeText(requireContext(), "Шаблон добавлен!", Toast.LENGTH_SHORT).show();
            clearInputFields();
            hideKeyboard(v);
            Log.d(TAG, "btnAddPattern: Category added, fields cleared, keyboard hidden.");
        });

        updateAddButtonState();
    }

    /**
     * Очищает поля ввода.
     */
    private void clearInputFields() {
        reasonEditText.setText("");
        sumEditText.setText("");
        reasonInputLayout.setError(null);
        sumInputLayout.setError(null);
        if (spinnerForChooseActiveOrPassive.getAdapter() != null && spinnerForChooseActiveOrPassive.getAdapter().getCount() > 0) {
            spinnerForChooseActiveOrPassive.setText(spinnerForChooseActiveOrPassive.getAdapter().getItem(0).toString(), false);
            selectedOperationType = spinnerForChooseActiveOrPassive.getAdapter().getItem(0).toString();
        }
    }

    /**
     * Обновляет состояние кнопки "Добавить".
     * Если причина и сумма не пустые, кнопка становится активной.
     * Иначе - неактивной.
     */
    private void updateAddButtonState() {
        boolean isReasonValid = reason != null && !reason.isEmpty();
        boolean isSumValid = sumInDouble > 0;
        boolean isOperationTypeSelected = selectedOperationType != null;
        btnAddPattern.setEnabled(isReasonValid && isSumValid && isOperationTypeSelected);
    }

    /**
     * Показывает диалог для изменения или удаления категории.
     * Теперь принимает полный объект Categories.
     *
     * @param category Объект категории, которую нужно изменить или удалить.
     */
    private void showCategoryActionsDialog(Categories category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_change_or_delete, null);
        TextView instructionTextView = dialogView.findViewById(R.id.categoryNameTextView);
        TextInputEditText editTextSum = dialogView.findViewById(R.id.sum_edit_text); // ИЗМЕНЕНО: на TextInputEditText
        ImageView imgClose = dialogView.findViewById(R.id.imgClose);

        instructionTextView.setText("Изменить сумму для " + category.getCategoryName() + ":");
        editTextSum.setText(String.valueOf(category.getSum()));
        editTextSum.setSelection(editTextSum.getText().length());

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Изменить", (dialogInterface, i) -> {
            String sumText = editTextSum.getText().toString().trim();
            double newSum;

            if (sumText.isEmpty()) {
                newSum = 0.0;
            } else {
                try {
                    newSum = Double.parseDouble(sumText);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Введите корректное число для суммы", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (newSum == category.getSum()) {
                Toast.makeText(requireContext(), "Сумма не изменилась.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            Categories updatedCategory = new Categories(category.getCategoryName(), newSum, category.getOperationType(), category.getFirestoreId(), false);
            updatedCategory.setId(category.getId());
            categoryViewModel.update(updatedCategory);

            Toast.makeText(requireContext(), "Сумма для категории '" + category.getCategoryName() + "' изменена на " + newSum + ".", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Удалить", (dialogInterface, i) -> {
            categoryViewModel.delete(category);
            Toast.makeText(requireContext(), "Категория '" + category.getCategoryName() + "' удалена.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.setOnShowListener(dialogInterface -> focusAndOpenKeyboard(editTextSum));
        dialog.setCanceledOnTouchOutside(false);
        imgClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Устанавливает фокус на TextView и открывает клавиатуру.
     *
     * @param textView Текстовое поле, на которое нужно сфокусироваться.
     */
    private void focusAndOpenKeyboard(TextView textView) {
        textView.requestFocus();
        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    /**
     * Скрывает клавиатуру.
     *
     * @param view Любой View из текущего окна.
     */
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
