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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinance.Adapters.CategoryViewAdapter;
import com.example.myfinance.Models.CategoryViewModel;
import com.example.myfinance.Prevalent.CategoryItem;
import com.example.myfinance.R;
import com.example.myfinance.data.Categories;
import com.example.myfinance.data.CategoryDataBase;
import com.example.myfinance.data.CategoryRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatternFragment extends Fragment {
    private TextInputLayout reasonInputLayout, sumInputLayout;
    private TextInputEditText reasonEditText, sumEditText;
    private Button btnAddPattern;
    private double sumInDouble;
    private String reason;
    private RecyclerView RecyclerForCategory;
    private CategoryRepository categoryRepository;
    private CategoryViewModel.TaskViewModelFactory viewModelFactory;
    private CategoryDataBase database;
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

        database = CategoryDataBase.getDatabase(requireActivity().getApplication());
        categoryRepository = new CategoryRepository(database.daoCategories());
        viewModelFactory = new CategoryViewModel.TaskViewModelFactory(categoryRepository);
        categoryViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(CategoryViewModel.class);
        loadAndDisplay();
        checkFields();
        onClickAdapter();
    }

    private void loadAndDisplay() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                Log.d("PatternFragment", "All categories from DB: " + categories);
                List<CategoryItem> categoryNames = new ArrayList<>();
                for (Categories category : categories) {
                    categoryNames.add(new CategoryItem(category.getCategoryName(), category.getSum()));
                }
                if (CategoryAdapter == null) {
                    RecyclerForCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
                    CategoryAdapter = new CategoryViewAdapter(categoryNames);
                    RecyclerForCategory.setAdapter(CategoryAdapter);
                    onClickAdapter();
                } else {
                    CategoryAdapter.clear();
                    CategoryAdapter.setItems(categoryNames);
                    CategoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void onClickAdapter() {
        if (CategoryAdapter != null) {
            CategoryAdapter.setOnItemClickListener(item -> {
                showCategoryActionsDialog(item.getCategoryName(), item.getSum());
                CategoryAdapter.notifyDataSetChanged();
            });
        } else {
            Log.e("PatternFragment", "CategoryAdapter is null in onClickAdapter(). This should not happen if called correctly.");
        }
    }

    private void checkFields() {
        reasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (reasonEditText.getText().toString().trim().isEmpty()) {
                    reasonInputLayout.setError("Причина не может быть пустой");
                    reason = "";
                } else {
                    reasonInputLayout.setError(null);
                    reason = Objects.requireNonNull(reasonEditText.getText()).toString().trim();
                }
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
                String sumText = sumEditText.getText().toString().trim();
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
            }
        });

        btnAddPattern.setOnClickListener(v -> {
            if (reason.isEmpty() || sumInDouble <= 0) {
                Toast.makeText(requireContext(), "Пожалуйста, введите корректные данные для шаблона.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(requireContext(), "Шаблон добавлен!", Toast.LENGTH_SHORT).show();
            categoryRepository.insert(new Categories(reason, sumInDouble));
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void showCategoryActionsDialog(String categoryName, double currentSum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_change_or_delete, null);
        TextView instructionTextView = dialogView.findViewById(R.id.categoryNameTextView); // Убедитесь, что ID корректен
        EditText editTextSum = dialogView.findViewById(R.id.sum_edit_text);

        instructionTextView.setText("Изменить сумму для " + categoryName + ":");
        editTextSum.setText(String.valueOf(currentSum));
        editTextSum.setSelection(editTextSum.getText().length());

        builder.setView(dialogView);
        builder.setTitle("Действия с категорией");

        builder.setPositiveButton("Изменить", (dialogInterface, i) -> {
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
            categoryViewModel.updateCategorySumByName(categoryName, newSum);
            dialogInterface.dismiss(); // Dismiss the dialog only after successful update
        });

        builder.setNegativeButton("Удалить", (dialogInterface, i) -> {
            categoryViewModel.delete(new Categories(categoryName, currentSum));
            Toast.makeText(requireContext(), "Категория '" + categoryName + "' удалена.", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            focusAndOpenKeyboard(editTextSum);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void focusAndOpenKeyboard(TextView textView) {
        textView.requestFocus();
        new Handler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }
}