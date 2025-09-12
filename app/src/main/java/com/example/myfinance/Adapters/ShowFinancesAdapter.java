package com.example.myfinance.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.R;

import java.util.ArrayList;
import java.util.List;

public class ShowFinancesAdapter extends BaseAdapter {

    private static final String TAG = "FinancesAdapter";
    private List<ShowFinances> finances;
    private LayoutInflater inflater;
    private Context context; // Добавлен контекст для ContextCompat

    public ShowFinancesAdapter(Context context, List<ShowFinances> initialFinances) {
        this.context = context; // Инициализируем контекст
        this.finances = new ArrayList<>(initialFinances);
        this.inflater = LayoutInflater.from(context);
        Log.d(TAG, "Adapter initialized with " + initialFinances.size() + " items.");
    }

    public void addItem(ShowFinances newItem) {
        finances.add(newItem);
        notifyDataSetChanged();
    }

    public void addAllItems(List<ShowFinances> newItems) {
        finances.clear();
        finances.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clearItems() {
        finances.clear();
        notifyDataSetChanged();
    }

    public void setItems(List<ShowFinances> newFinances) {
        this.finances.clear();
        this.finances.addAll(newFinances);
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.main_finance_item, parent, false); // ИСПОЛЬЗУЕМ item_finance
            holder = new ViewHolder();

            holder.textViewId = convertView.findViewById(R.id.id);
            holder.textViewSum = convertView.findViewById(R.id.summa);
            holder.textViewName = convertView.findViewById(R.id.name);
            holder.textOperationType = convertView.findViewById(R.id.operation);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShowFinances item = (ShowFinances) getItem(position);

        if (holder.textViewId != null) {
            holder.textViewId.setText(String.valueOf(item.getId()));
        } else {
            Log.e(TAG, "textViewId == null " + position);
        }

        if (holder.textViewSum != null) {
            holder.textViewSum.setText(String.valueOf(item.getSum()));
            if ("Доход".equals(item.getOperationType())) {

                holder.textViewSum.setTextColor(ContextCompat.getColor(context, R.color.accent_green));
            } else if ("Расход".equals(item.getOperationType())) {

                holder.textViewSum.setTextColor(ContextCompat.getColor(context, R.color.alert_red));
            } else {

                holder.textViewSum.setTextColor(ContextCompat.getColor(context, R.color.text_dark_primary));
            }
        } else {
            Log.e(TAG, "textViewSum == null " + position);
        }

        if (holder.textViewName != null) {
            holder.textViewName.setText(item.getName() != null ? item.getName() : "N/A");
        } else {
            Log.e(TAG, "textViewName == null " + position);
        }
        if (holder.textOperationType != null) {
            holder.textOperationType.setText(item.getOperationType() != null ? item.getOperationType() : "N/A");
        } else {
            Log.e(TAG, "textOperationType is null for position " + position);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textViewId;
        TextView textViewSum;
        TextView textViewName;
        TextView textOperationType;
    }
}
