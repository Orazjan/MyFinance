package com.example.myfinance.Adapters;

import android.content.Context;
import android.util.Log; // Импорт для логирования
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.R;

import java.util.ArrayList;
import java.util.List;

public class ShowFinancesAdapter extends BaseAdapter {

    private static final String TAG = "FinancesAdapter"; // Тэг для логов
    private List<ShowFinances> finances;
    private LayoutInflater inflater;

    public ShowFinancesAdapter(Context context, List<ShowFinances> initialFinances) {
        this.finances = new ArrayList<>(initialFinances);
        this.inflater = LayoutInflater.from(context);
        Log.d(TAG, "Adapter initialized with " + initialFinances.size() + " items.");
    }

    public void addItem(ShowFinances newItem) {
        finances.add(newItem);
        notifyDataSetChanged();
        Log.d(TAG, "Item added: " + newItem.getName() + " - " + newItem.getSum() + ". Total items: " + finances.size());
    }

    public void addAllItems(List<ShowFinances> newItems) {
        finances.clear();
        finances.addAll(newItems);
        notifyDataSetChanged();
        Log.d(TAG, "All items added. Total items: " + finances.size());
    }

    public void clearItems() {
        finances.clear();
        notifyDataSetChanged();
        Log.d(TAG, "All items cleared.");
    }

    public void setItems(List<ShowFinances> newFinances) {
        this.finances.clear();
        this.finances.addAll(newFinances);
        notifyDataSetChanged();
        Log.d(TAG, "Items set. Total items: " + finances.size());
    }

    @Override
    public int getCount() {
        return finances.size();
    }

    @Override
    public Object getItem(int position) {
        return finances.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_finance_item, parent, false);
            holder = new ViewHolder();

            holder.textViewId = convertView.findViewById(R.id.id);
            holder.textViewSum = convertView.findViewById(R.id.summa);
            holder.textViewName = convertView.findViewById(R.id.name);

            convertView.setTag(holder);
            Log.d(TAG, "Created new view for position: " + position);
        } else {
            holder = (ViewHolder) convertView.getTag();
            Log.d(TAG, "Reusing view for position: " + position);
        }

        ShowFinances item = (ShowFinances) getItem(position);

        // Логирование данных перед установкой в TextView
        Log.d(TAG, "Item at position " + position + ": ID=" + item.getId() + ", Sum=" + item.getSum() + ", Name=" + item.getName());

        // Установка текста. Добавлена проверка на null, чтобы избежать NullPointerException
        if (holder.textViewId != null) {
            holder.textViewId.setText(String.valueOf(item.getId()));
        } else {
            Log.e(TAG, "textViewId is null for position " + position);
        }

        if (holder.textViewSum != null) {
            holder.textViewSum.setText(String.valueOf(item.getSum()));
        } else {
            Log.e(TAG, "textViewSum is null for position " + position);
        }

        if (holder.textViewName != null) {
            holder.textViewName.setText(item.getName() != null ? item.getName() : "N/A"); // Обработка null для имени
        } else {
            Log.e(TAG, "textViewName is null for position " + position);
        }


        return convertView;
    }

    static class ViewHolder {
        TextView textViewId;
        TextView textViewSum;
        TextView textViewName;
    }
}
