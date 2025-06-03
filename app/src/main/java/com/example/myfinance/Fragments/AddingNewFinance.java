package com.example.myfinance.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfinance.Models.AmountViewModel;
import com.example.myfinance.Models.CategoryViewModel;
import com.example.myfinance.R;
import com.example.myfinance.data.AmountDatabase;
import com.example.myfinance.data.AmountRepository;
import com.example.myfinance.data.Categories;
import com.example.myfinance.data.CategoryDataBase;
import com.example.myfinance.data.CategoryRepository;
import com.example.myfinance.data.FinanceDatabase;
import com.example.myfinance.data.FinanceRepository;
import com.example.myfinance.data.Finances;
import com.example.myfinance.data.TotalAmount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddingNewFinance extends Fragment {
    private Spinner standart_variant;
    private Button btnAdd;
    private TextInputEditText sumEditText, reasonEditText;
    private TextInputLayout sumInputLayout, reasonInputLayout;
    private String selectedCategory;
    private CategoryViewModel categoryViewModel;
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

        loadAndDisplay();

        standart_variant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = adapterView.getItemAtPosition(i).toString();
                switch (selectedCategory) {
                    case "Оплата за транспорт":
                        sumEditText.setText("20.0");
                        reasonEditText.setText(selectedCategory);
                        break;
                    case "Оплата оператора":
                        sumEditText.setText("200.0");
                        reasonEditText.setText(selectedCategory);
                        break;
                    case "Оплата за кофе":
                        sumEditText.setText("");
                        reasonEditText.setText(selectedCategory);
                        focusAndOpenKeyboard(sumEditText);
                        break;
                    case "Оплата за еду":
                        sumEditText.setText("");
                        reasonEditText.setText(selectedCategory);
                        focusAndOpenKeyboard(sumEditText);
                        break;
                    case "Другое":
                        sumEditText.setText("");
                        reasonEditText.setText("");
                        focusAndOpenKeyboard(sumEditText);
                        break;
                    default:
                        sumEditText.setText("");
                        reasonEditText.setText(selectedCategory);
                        focusAndOpenKeyboard(sumEditText);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sumEditText.setText("");
                reasonEditText.setText("Категория не выбрана");
                selectedCategory = null;
            }
        });

        btnAdd.setOnClickListener(v -> {
            checkFields();
        });
    }

    private void loadAndDisplay() {
        CategoryDataBase database = CategoryDataBase.getDatabase(requireActivity().getApplication());
        CategoryRepository repository = new CategoryRepository(database.daoCategories());
//        repository.deleteAll();   На всякий случай (удаляет всё)
        CategoryViewModel.TaskViewModelFactory viewModelFactory = new CategoryViewModel.TaskViewModelFactory(repository);

        categoryViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(CategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                Log.d("AddingNewFinance", "All categories from DB: " + categories);
                List<String> categoryNames = new ArrayList<>();
                for (Categories category : categories) {
                    categoryNames.add(category.getCategoryName());
                }
                if (categorySpinnerAdapter == null) {
                    categorySpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                    categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    standart_variant.setAdapter(categorySpinnerAdapter);
                } else {
                    categorySpinnerAdapter.clear();
                    categorySpinnerAdapter.addAll(categoryNames);
                    categorySpinnerAdapter.notifyDataSetChanged();
                }

                if (!categoryNames.isEmpty() && selectedCategory == null) {
                    selectedCategory = categoryNames.get(0);
                }
            }
        });
    }

    private void checkFields() {
        String sumStr = Objects.requireNonNull(sumEditText.getText()).toString().trim();
        String reason = Objects.requireNonNull(reasonEditText.getText()).toString().trim();
//        String data = getCurrentDate() + " " + getCurrentTime();

        if (sumStr.isEmpty()) {
            sumInputLayout.setError("Введите сумму");
            return;
        } else {
            sumInputLayout.setError(null);
        }

        if (reason.isEmpty()) {
            reasonInputLayout.setError("Введите причину");
            return;
        } else {
            reasonInputLayout.setError(null);
        }

        if (selectedCategory == null || selectedCategory.isEmpty() || selectedCategory.equals("Категория не выбрана")) {
            Toast.makeText(getContext(), "Пожалуйста, выберите категорию", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double sum = Double.parseDouble(sumStr);
            Toast.makeText(getContext(), "Данные добавлены!", Toast.LENGTH_SHORT).show();

            sumEditText.setText("");
            reasonEditText.setText("");
            addingToDb(reason, sum);

            popBackAndPassData(sum);
        } catch (NumberFormatException e) {
            sumInputLayout.setError("Неверный формат суммы");
            Log.e("AddingNewFinance", "NumberFormatException: " + e.getMessage());
        }
    }

    private void popBackAndPassData(double sum) {
        Bundle result = new Bundle();
        result.putDouble("ValueSum", sum);

        getParentFragmentManager().setFragmentResult("ValueSum", result);

        getParentFragmentManager().popBackStack();
    }

    private void addingToDb(String reason, double sum) {
        FinanceDatabase database = FinanceDatabase.getDatabase(requireActivity().getApplication());
        FinanceRepository repository = new FinanceRepository(database.daoFinances());
        AmountDatabase amdb = AmountDatabase.getDatabase(requireActivity().getApplication());
        AmountRepository amrepo = new AmountRepository(amdb.daoTotalAmount());
        AmountViewModel.TaskViewModelFactory amViewModelTaskFactory = new AmountViewModel.TaskViewModelFactory(amrepo);
        AmountViewModel amountViewModel = new ViewModelProvider(requireActivity(), amViewModelTaskFactory).get(AmountViewModel.class);

        amountViewModel.update(new TotalAmount(sum));

        repository.insert(new Finances(reason, sum));
        Toast.makeText(requireContext(), "Данные добавлены!", Toast.LENGTH_SHORT).show();
        Log.d("Adding to ROOM", "Sum and reason " + reason + " " + sum);
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

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm.ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
        return sdf.format(new Date());
    }
}