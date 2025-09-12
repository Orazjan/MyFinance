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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

    private AutoCompleteTextView standart_variant;
    private Button btnAdd;
    private TextInputEditText sumEditText, reasonEditText, commentsEditText;
    private TextInputLayout sumInputLayout, reasonInputLayout, commentsInputLayout;
    private String selectedCategory;
    private String selectedOperationType;
    private AutoCompleteTextView SpinnerForChooseActiveOrPassive;

    private CategoryViewModel categoryViewModel;
    private FinanceViewModel financeViewModel;

    private ArrayAdapter<String> categorySpinnerAdapter;
    private ArrayAdapter<String> operationTypeAdapter;

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

            List<String> operationTypesList = new ArrayList<>();
            operationTypesList.add("Доход");
            operationTypesList.add("Расход");

            // Инициализация адаптера для типа операции
            if (operationTypeAdapter == null) {
                operationTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, operationTypesList);
                SpinnerForChooseActiveOrPassive.setAdapter(operationTypeAdapter);
            } else {
                operationTypeAdapter.clear();
                operationTypeAdapter.addAll(operationTypesList);
                operationTypeAdapter.notifyDataSetChanged();
            }

            // Инициализация адаптера для категорий
            if (categorySpinnerAdapter == null) {
                categorySpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                standart_variant.setAdapter(categorySpinnerAdapter);
            } else {
                categorySpinnerAdapter.clear();
                categorySpinnerAdapter.addAll(categoryNames);
                categorySpinnerAdapter.notifyDataSetChanged();
            }

            // Установка начального выбора для категории
            if (!categoryNames.isEmpty()) {
                if (selectedCategory == null || !categoryNames.contains(selectedCategory)) {
                    selectedCategory = categoryNames.get(0);
                    standart_variant.setText(selectedCategory, false); // Устанавливаем текст и не фильтруем
                } else {
                    standart_variant.setText(selectedCategory, false);
                }
            } else {
                selectedCategory = null;
                standart_variant.setText("", false); // Очищаем текст, если категорий нет
            }

            // Установка начального выбора для типа операции
            if (!operationTypesList.isEmpty()) {
                if (selectedOperationType == null || !operationTypesList.contains(selectedOperationType)) {
                    selectedOperationType = operationTypesList.get(0); // По умолчанию "Доход"
                    SpinnerForChooseActiveOrPassive.setText(selectedOperationType, false);
                } else {
                    SpinnerForChooseActiveOrPassive.setText(selectedOperationType, false);
                }
            } else {
                selectedOperationType = null;
                SpinnerForChooseActiveOrPassive.setText("", false);
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
        String operationType = selectedOperationType; // Получаем выбранный тип операции
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
                double parsedSum = Double.parseDouble(sumStr);
                if (parsedSum <= 0) { // Добавлена проверка на положительную сумму
                    sumInputLayout.setError("Сумма должна быть больше нуля");
                    isValid = false;
                } else {
                    sumInputLayout.setError(null);
                }
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
        if (categorySpinnerAdapter != null && categorySpinnerAdapter.getCount() > 0) {
            standart_variant.setText(categorySpinnerAdapter.getItem(0), false);
            selectedCategory = categorySpinnerAdapter.getItem(0);
        } else {
            standart_variant.setText("", false);
            selectedCategory = null;
        }
        if (operationTypeAdapter != null && operationTypeAdapter.getCount() > 0) {
            SpinnerForChooseActiveOrPassive.setText(operationTypeAdapter.getItem(0), false);
            selectedOperationType = operationTypeAdapter.getItem(0);
        } else {
            SpinnerForChooseActiveOrPassive.setText("", false);
            selectedOperationType = null;
        }
        validateFieldsForButton(); // Повторная валидация после очистки
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
        commentsEditText.addTextChangedListener(watcher); // Добавлен TextWatcher для комментариев

        // Слушатель для AutoCompleteTextView категории
        standart_variant.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = (String) parent.getItemAtPosition(position);
            setToReasonAndSum(selectedCategory);
            validateFieldsForButton();
        });

        // Слушатель для AutoCompleteTextView типа операции
        SpinnerForChooseActiveOrPassive.setOnItemClickListener((parent, view, position, id) -> {
            selectedOperationType = (String) parent.getItemAtPosition(position);
            validateFieldsForButton();
        });
    }

    /**
     * Проверяет, заполнены ли все необходимые поля, и включает/отключает кнопку добавления.
     */
    private void validateFieldsForButton() {
        String sum = sumEditText.getText() != null ? sumEditText.getText().toString().trim() : "";
        String reason = reasonEditText.getText() != null ? reasonEditText.getText().toString().trim() : "";

        boolean isSumValid = !sum.isEmpty() && Double.parseDouble(sum) > 0; // Проверка на > 0
        boolean isReasonValid = !reason.isEmpty();
        boolean isCategorySelected = selectedCategory != null && !selectedCategory.isEmpty(); // Проверка на пустую строку
        boolean isOperationTypeSelected = selectedOperationType != null && !selectedOperationType.isEmpty(); // Проверка на пустую строку

        btnAdd.setEnabled(isSumValid && isReasonValid && isCategorySelected && isOperationTypeSelected);
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
            categoryViewModel.getCategoryByNameAsync(categoryName).addOnSuccessListener(category -> {
                if (category != null) {
                    reasonEditText.setText(category.getCategoryName());
                    if (category.getSum() == 0.0) {
                        sumEditText.setText("");
                        focusAndOpenKeyboard(sumEditText);
                    } else {
                        sumEditText.setText(String.valueOf(category.getSum()));
                    }

                    selectedOperationType = category.getOperationType();

                    if (operationTypeAdapter != null) {
                        // Устанавливаем текст AutoCompleteTextView, а не selection для Spinner
                        SpinnerForChooseActiveOrPassive.setText(selectedOperationType, false);
                    } else {
                        Log.w(TAG, "setToReasonAndSum: Operation type adapter is null.");
                    }
                } else {
                    sumEditText.setText("");
                    focusAndOpenKeyboard(sumEditText);
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
            // Если категория не выбрана, сбрасываем тип операции на первый элемент (Доход)
            if (operationTypeAdapter != null && operationTypeAdapter.getCount() > 0) {
                SpinnerForChooseActiveOrPassive.setText(operationTypeAdapter.getItem(0), false);
                selectedOperationType = operationTypeAdapter.getItem(0);
            } else {
                SpinnerForChooseActiveOrPassive.setText("", false);
                selectedOperationType = null;
            }

            focusAndOpenKeyboard(reasonEditText);
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
