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
    private String selectedOperationType;
    private Spinner SpinnerForChooseActiveOrPassive;

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
        SpinnerForChooseActiveOrPassive = view.findViewById(R.id.SpinnerForChooseActiveOrPassive);

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
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            List<String> categoryNames = new ArrayList<>();
            if (categories != null) {
                for (Categories category : categories) {
                    categoryNames.add(category.getCategoryName());
                }
            }

            List<String> operationType = new ArrayList<>();
            operationType.add("Доход");
            operationType.add("Расход");
            ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, operationType);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerForChooseActiveOrPassive.setAdapter(adapter);

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
        String operationType = selectedOperationType;
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
        addingToDb(reason, sum, operationType, comments);
        clearFields();
        popBackAndPassData(sum, operationType);
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

        SpinnerForChooseActiveOrPassive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOperationType = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "Operation type spinner selected: " + selectedOperationType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (adapterView.getAdapter() != null && adapterView.getAdapter().getCount() > 0) {
                    selectedOperationType = adapterView.getItemAtPosition(0).toString();
                } else {
                    selectedOperationType = null;
                }
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
                !selectedCategory.equals("Категория не выбрана") &&
                selectedOperationType != null;

        btnAdd.setEnabled(isValid);
    }

    /**
     * Возвращает данные в родительский фрагмент и закрывает текущий.
     *
     * @param sum Сумма, которую нужно передать.
     * @param operationType Тип операции ("Доход" или "Расход").
     */
    private void popBackAndPassData(double sum, String operationType) {
        Bundle result = new Bundle();
        result.putDouble("Sum", sum); // Используем ключ "Sum"
        result.putString("OperationType", operationType); // Добавляем тип операции с ключом "OperationType"

        getParentFragmentManager().setFragmentResult("ValueSumAndType", result); // Используем ключ "ValueSumAndType"
        getParentFragmentManager().popBackStack();
    }

    /**
     * Добавление данных о новой финансовой операции в базу данных.
     *
     * @param reason   Причина операции.
     * @param sum      Сумма операции.
     * @param comments Комментарии к операции.
     * @param operationType Тип операции (доход/расход).
     */
    private void addingToDb(String reason, double sum, String operationType, String comments) {
        Finances newFinance = new Finances(reason, sum, operationType, comments, getCurrentDate());
        financeViewModel.insert(newFinance);
        Toast.makeText(requireContext(), "Данные добавлены!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "addingToDb: New finance added: Reason=" + reason + ", Sum=" + sum + ", Type=" + operationType);
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
     * Устанавливает значение суммы, причины и типа операции в текстовых полях и Spinner на основе выбранной категории.
     *
     * @param categoryName Имя выбранной категории.
     */
    private void setToReasonAndSum(String categoryName) {
        if (categoryName != null) {
            // Используем getCategoryByNameAsync для получения всей информации о категории
            categoryViewModel.getCategoryByNameAsync(categoryName).addOnSuccessListener(category -> {
                if (category != null) {
                    reasonEditText.setText(category.getCategoryName());
                    if (category.getSum() == 0.0) {
                        sumEditText.setText("");
                        focusAndOpenKeyboard(sumEditText);
                    } else {
                        sumEditText.setText(String.valueOf(category.getSum()));
                    }

                    selectedOperationType = category.getOperationType(); // Сохраняем тип операции

                    ArrayAdapter<String> operationTypeAdapter = (ArrayAdapter<String>) SpinnerForChooseActiveOrPassive.getAdapter();
                    if (operationTypeAdapter != null) {
                        int position = operationTypeAdapter.getPosition(selectedOperationType);
                        if (position >= 0) {
                            SpinnerForChooseActiveOrPassive.setSelection(position);
                        } else {
                            Log.w(TAG, "setToReasonAndSum: Operation type '" + selectedOperationType + "' not found in spinner adapter.");
                        }
                    }
                } else {
                    sumEditText.setText("");
                    focusAndOpenKeyboard(sumEditText);
                    Log.d(TAG, "setToReasonAndSum: Category not found for: " + categoryName + ". Clearing sum field.");
                }
                validateFieldsForButton();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "setToReasonAndSum: Failed to retrieve category asynchronously for " + categoryName + ": " + e.getMessage(), e);
                Toast.makeText(requireContext(), "Ошибка при загрузке категории.", Toast.LENGTH_SHORT).show();
                sumEditText.setText("");
                reasonEditText.setText("");
                validateFieldsForButton();
            });
        } else {
            reasonEditText.setText("");
            sumEditText.setText("");
            if (SpinnerForChooseActiveOrPassive.getAdapter() != null && SpinnerForChooseActiveOrPassive.getAdapter().getCount() > 0) {
                SpinnerForChooseActiveOrPassive.setSelection(0);
                selectedOperationType = ((ArrayAdapter<String>) SpinnerForChooseActiveOrPassive.getAdapter()).getItem(0);
            } else {
                selectedOperationType = null;
            }

            focusAndOpenKeyboard(reasonEditText);
            Log.d(TAG, "setToReasonAndSum: categoryName is null. Clearing fields.");
            validateFieldsForButton();
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
