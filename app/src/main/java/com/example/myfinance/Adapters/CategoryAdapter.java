package com.example.myfinance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myfinance.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategorySummary> {

    public CategoryAdapter(Context context, ArrayList<CategorySummary> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Получаем объект CategorySummary для текущей позиции
        CategorySummary category = getItem(position);

        // Проверяем, существует ли переиспользуемый view, иначе создаем новый
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_category_summary, parent, false);
        }

        // Находим TextViews в файле разметки.
        // Используем идентификаторы из макета list_item_category_summary.xml.
        TextView categoryName = convertView.findViewById(R.id.categoryNameTextView);
        TextView categorySum = convertView.findViewById(R.id.totalSumTextView);

        // Заполняем TextViews данными из объекта CategorySummary
        if (category != null) {
            categoryName.setText(category.getCategoryName());
            // Форматируем сумму, чтобы она выглядела аккуратно
            String formattedSum = String.format("%.2f", category.getTotalSum());
            categorySum.setText(formattedSum);
        }

        // Возвращаем готовый view
        return convertView;
    }
}
