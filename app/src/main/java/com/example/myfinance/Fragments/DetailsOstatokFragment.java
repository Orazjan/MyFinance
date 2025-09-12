package com.example.myfinance.Fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myfinance.Adapters.CategoryAdapter;
import com.example.myfinance.Adapters.CategorySummary;
import com.example.myfinance.R;
import com.example.myfinance.data.Finances;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DetailsOstatokFragment extends Fragment {
    private static final String ARG_SUM = "arg_sum";
    private static final String ARG_CURRENCY = "arg_currency";
    private static final String ARG_TRANSITION_NAME = "arg_transition_name";
    private static final String ARG_CATEGORIES = "arg_categories";
    private static final String ARG_FINANCES = "arg_finances";

    private ListView categoryListView;
    private TextView sumTextView, valutaTextView;
    private ArrayList<CategorySummary> categorySummaries;
    private ArrayList<Finances> allFinances; // Поле для хранения всех финансовых операций

    public DetailsOstatokFragment() {
    }

    public static DetailsOstatokFragment newInstance(double sum, String currency, String transitionName, ArrayList<CategorySummary> categories, ArrayList<Finances> allFinances) {
        DetailsOstatokFragment fragment = new DetailsOstatokFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_SUM, sum);
        args.putString(ARG_CURRENCY, currency);
        args.putString(ARG_TRANSITION_NAME, transitionName);
        args.putSerializable(ARG_CATEGORIES, categories);
        args.putSerializable(ARG_FINANCES, allFinances); // Передаем полный список финансов
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем анимацию для входа этого фрагмента
        setSharedElementEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_bounds_transition));
        setEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.explode));

        if (getArguments() != null) {
            String transitionName = getArguments().getString(ARG_TRANSITION_NAME);
            if (transitionName != null) {
                setSharedElementEnterTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_bounds_transition));
                setSharedElementReturnTransition(TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_bounds_transition));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container, false);
        sumTextView = view.findViewById(R.id.sumDetails);
        valutaTextView = view.findViewById(R.id.valutaDetails);
        categoryListView = view.findViewById(R.id.categoryListView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем данные из аргументов
        if (getArguments() != null) {
            double sum = getArguments().getDouble(ARG_SUM);
            String currency = getArguments().getString(ARG_CURRENCY);
            categorySummaries = (ArrayList<CategorySummary>) getArguments().getSerializable(ARG_CATEGORIES);
            allFinances = (ArrayList<Finances>) getArguments().getSerializable(ARG_FINANCES); // Получаем полный список

            sumTextView.setText(String.valueOf(sum));
            valutaTextView.setText(currency);

            if (categorySummaries != null) {
                CategoryAdapter adapter = new CategoryAdapter(requireContext(), categorySummaries);
                categoryListView.setAdapter(adapter);
            }
        }

        // Обработчик нажатий на элементы списка
        categoryListView.setOnItemClickListener((parent, v, position, id) -> {
            CategorySummary clickedCategory = (CategorySummary) parent.getItemAtPosition(position);

            // Фильтруем все операции, чтобы найти те, которые относятся к выбранной категории
            List<Finances> categoryFinances = new ArrayList<>();
            if (allFinances != null) {
                categoryFinances = allFinances.stream()
                        .filter(finance -> finance.getFinanceResult() != null && finance.getFinanceResult().equalsIgnoreCase(clickedCategory.getCategoryName()))
                        .collect(Collectors.toList());
            }

            // Создаем и отображаем Bottom Sheet
            TransactionDetailsBottomSheetFragment bottomSheetFragment = new TransactionDetailsBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transactions", (Serializable) categoryFinances); // Передаем список операций
            bottomSheetFragment.setArguments(bundle);
            bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
        });
    }
}
