package com.example.myfinance.Adapters;

import android.content.Context;
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

    private List<ShowFinances> finances;
    private LayoutInflater inflater;

    public ShowFinancesAdapter(Context context, List<ShowFinances> initialFinances) {
        this.finances = new ArrayList<>(initialFinances);
        this.inflater = LayoutInflater.from(context);
    }

    public void addItem(ShowFinances newItem) {
        finances.add(0, newItem);
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
            convertView = inflater.inflate(R.layout.main_finance_item, parent, false);
            holder = new ViewHolder();

            holder.textViewId = convertView.findViewById(R.id.id);
            holder.textViewSum = convertView.findViewById(R.id.summa);
            holder.textViewName = convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ShowFinances item = (ShowFinances) getItem(position);

        holder.textViewId.setText(String.valueOf(item.getId()));
        holder.textViewSum.setText(String.valueOf(item.getSum()));
        holder.textViewName.setText(item.getName());

        return convertView;
    }

    static class ViewHolder {
        TextView textViewId;
        TextView textViewSum;
        TextView textViewName;
    }
}