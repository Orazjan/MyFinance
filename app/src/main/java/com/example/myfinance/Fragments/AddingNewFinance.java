package com.example.myfinance.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.CategoryViewModel;
import com.example.myfinance.Models.FinanceViewModel;
import com.example.myfinance.MyApplication;
import com.example.myfinance.Prevalent.DateFormatter;
import com.example.myfinance.R;
import com.example.myfinance.data.Categories;
import com.example.myfinance.data.FinanceRepository;
import com.example.myfinance.data.Finances;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddingNewFinance extends Fragment {
    private final String TAG = "AddingNewFinance";

    private Spinner standart_variant;
    private Button btnAdd;
    private TextInputEditText sumEditText, reasonEditText, commentsEditText;
    private TextInputLayout sumInputLayout, reasonInputLayout, commentsInputLayout;
    private String selectedCategory;

    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;

    private ArrayAdapter<String> categorySpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adding_new_finance_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        standart_variant = view.findViewById(R.id.standart_variant);
        btnAdd = view.findViewById(R.id.btnAdd);
        sumEditText = view.findViewById(R.id.sumEditText);
        reasonEditText = view.findViewById(R.id.reasonEditText);
        sumInputLayout = view.findViewById(R.id.sumInputLayout);
        reasonInputLayout = view.findViewById(R.id.reasonInputLayout);
        commentsEditText = view.findViewById(R.id.commentsEditText);
        commentsInputLayout = view.findViewById(R.id.commentsInputLayout);

        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new CategoryViewModel.TaskViewModelFactory(requireActivity().getApplication())
        ).get(CategoryViewModel.class);

        FinanceRepository financeRepository = ((MyApplication) requireActivity().getApplication()).getFinanceRepository();
        FinanceViewModel.TaskViewModelFactory finViewModelTaskFactory = new FinanceViewModel.TaskViewModelFactory(financeRepository);
        financeViewModel = new ViewModelProvider(requireActivity(), finViewModelTaskFactory).get(FinanceViewModel.class);

        btnAdd.setEnabled(false);

        loadAndDisplay();
        setupTextWatchers();

        btnAdd.setOnClickListener(v -> checkFields());
    }

    /**
     * Загрузка и отображение всех категорий из базы данных.
     * Использует categoryViewModel (поле класса).
     */
    private void loadAndDisplay() {
        Log.d(TAG, "loadAndDisplay: Called. Observing all categories from ViewModel.");
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            List<String> categoryNames = new ArrayList<>();
            if (categories != null) {
                for (Categories category : categories) {
                    categoryNames.add(category.getCategoryName());
                }
            }

            Log.d(TAG, "loadAndDisplay: Categories received from ViewModel: " + categoryNames.size() + " items. Content: " + categoryNames.toString());

            if (categorySpinnerAdapter == null) {
                categorySpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                standart_variant.setAdapter(categorySpinnerAdapter);
                Log.d(TAG, "loadAndDisplay: New adapter set for spinner.");
            } else {
                categorySpinnerAdapter.clear();
                categorySpinnerAdapter.addAll(categoryNames);
                categorySpinnerAdapter.notifyDataSetChanged();
                Log.d(TAG, "loadAndDisplay: Existing adapter updated.");
            }

            if (!categoryNames.isEmpty()) {
                if (selectedCategory == null || !categoryNames.contains(selectedCategory)) {
                    selectedCategory = categoryNames.get(0);
                    standart_variant.setSelection(0); // Устанавливаем первый элемент по умолчанию
                    Log.d(TAG, "loadAndDisplay: Initial selected category set to: " + selectedCategory);
                } else {
                    int position = categoryNames.indexOf(selectedCategory);
                    if (position >= 0) {
                        standart_variant.setSelection(position);
                        Log.d(TAG, "loadAndDisplay: Retained selected category: " + selectedCategory + " at position: " + position);
                    }
                }
            } else {
                selectedCategory = null;
                Log.d(TAG, "loadAndDisplay: Category list is empty. Selected category set to null.");
            }
            validateFieldsForButton();
        });
    }

    /**
     * Проверка полей ввода и добавление данных в базу данных.
     */
    private void checkFields() {
        String sumStr = Objects.requireNonNull(sumEditText.getText()).toString().trim();
        String reason = Objects.requireNonNull(reasonEditText.getText()).toString().trim();
        String comments = Objects.requireNonNull(commentsEditText.getText()).toString().trim();
        boolean isValid = true;

        if (TextUtils.isEmpty(selectedCategory) || "Категория не выбрана".equals(selectedCategory)) {
            Toast.makeText(getContext(), "Пожалуйста, выберите категорию", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (sumStr.isEmpty()) {
            sumInputLayout.setError("Введите сумму");
            isValid = false;
        } else {
            try {
                Double.parseDouble(sumStr);
                sumInputLayout.setError(null);
            } catch (NumberFormatException e) {
                sumInputLayout.setError("Неверный формат суммы");
                isValid = false;
            }
        }

        if (reason.isEmpty()) {
            reasonInputLayout.setError("Введите причину");
            isValid = false;
        } else {
            reasonInputLayout.setError(null);
        }

        if (!isValid) return;

        double sum = Double.parseDouble(sumStr);
        addingToDb(reason, sum, comments);
        clearFields();
        popBackAndPassData(sum);
    }

    /**
     * Очистка полей ввода.
     */
    private void clearFields() {
        sumEditText.setText("");
        reasonEditText.setText("");
        commentsEditText.setText("");
    }

    /**
     * Настройка слушателей для полей ввода.
     */
    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                validateFieldsForButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        sumEditText.addTextChangedListener(watcher);
        reasonEditText.addTextChangedListener(watcher);

        standart_variant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString();
                setToReasonAndSum(selectedCategory);
                validateFieldsForButton();
                Log.d(TAG, "Spinner item selected: " + selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedCategory = null;
                sumEditText.setText("");
                reasonEditText.setText("");
                validateFieldsForButton();
                Log.d(TAG, "Spinner: Nothing selected.");
            }
        });
    }

    /**
     * Проверяет, заполнены ли все необходимые поля, и включает/отключает кнопку добавления.
     */
    private void validateFieldsForButton() {
        String sum = sumEditText.getText() != null ? sumEditText.getText().toString().trim() : "";
        String reason = reasonEditText.getText() != null ? reasonEditText.getText().toString().trim() : "";

        boolean isValid = !sum.isEmpty() &&
                !reason.isEmpty() &&
                selectedCategory != null &&
                !selectedCategory.equals("Категория не выбрана");

        btnAdd.setEnabled(isValid);
        Log.d(TAG, "validateFieldsForButton: isValid=" + isValid + ", current selectedCategory=" + selectedCategory);
    }

    /**
     * Возвращает данные в родительский фрагмент и закрывает текущий.
     *
     * @param sum Сумма, которую нужно передать.
     */
    private void popBackAndPassData(double sum) {
        Bundle result = new Bundle();
        result.putDouble("ValueSum", sum);

        getParentFragmentManager().setFragmentResult("ValueSum", result);
        getParentFragmentManager().popBackStack();
    }

    /**
     * Добавление данных о новой финансовой операции в базу данных.
     *
     * @param reason   Причина операции.
     * @param sum      Сумма операции.
     * @param comments Комментарии к операции.
     */
    private void addingToDb(String reason, double sum, String comments) {
        Finances newFinance = new Finances(reason, sum, comments, getCurrentDate());
        financeViewModel.insert(newFinance);
        Toast.makeText(requireContext(), "Данные добавлены!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Определение фокуса на поле ввода и открытие клавиатуры.
     *
     * @param textView Текстовое поле, на котором нужно сфокусироваться.
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
     * Устанавливает значение суммы и причины в текстовых полях на основе выбранной категории.
     *
     * @param categoryName Имя выбранной категории.
     */
    private void setToReasonAndSum(String categoryName) {
        if (categoryName != null) {
            // Используем getTotalSumByCategory, так как getSumForCategory был удален/консолидирован
            categoryViewModel.getSumForCategory(categoryName).observe(getViewLifecycleOwner(), sum -> {
                if (sum != null) {
                    reasonEditText.setText(categoryName);
                    if (sum == 0.0) {
                        sumEditText.setText("");
                        focusAndOpenKeyboard(sumEditText);
                    } else {
                        sumEditText.setText(String.valueOf(sum)); // Устанавливаем сумму, если она не 0
                    }
                    Log.d(TAG, "setToReasonAndSum: Reason and sum set for category: " + categoryName + ", sum: " + sum);
                } else {
                    sumEditText.setText("");
                    focusAndOpenKeyboard(sumEditText);
                    Log.d(TAG, "setToReasonAndSum: Sum is null for category: " + categoryName + ". Clearing sum field.");
                }
            });
        } else {
            reasonEditText.setText("");
            sumEditText.setText("");
            focusAndOpenKeyboard(reasonEditText);
            Log.d(TAG, "setToReasonAndSum: categoryName is null. Clearing fields.");
        }
    }

    /**
     * Возвращает текущую дату в формате "dd.MM.yyyy".
     *
     * @return Текущая дата в строковом формате.
     */
    public String getCurrentDate() {
        return DateFormatter.formatDate(new Date());
    }
}
