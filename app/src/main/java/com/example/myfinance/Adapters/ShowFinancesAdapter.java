package com.example.myfinance.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myfinance.Models.ShowFinances;
import com.example.myfinance.R;

import java.util.List;

public class ShowFinancesAdapter extends BaseAdapter {
    private Context context;
    private List<ShowFinances> finances;
    private LayoutInflater inflater;

    public ShowFinancesAdapter(Context context, List<ShowFinances> finances) {
        this.context = context;
        this.finances = finances;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return finances.size();
    }

    @Override
    public Object getItem(int i) {
        return finances.get(i);
    }

    @Override
    public long getItemId(int i) {
        return finances.size();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.main_finance_item, viewGroup, false);
            holder = new ViewHolder();
            holder.id = view.findViewById(R.id.id);
            holder.sum = view.findViewById(R.id.summa);
            holder.name = view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ShowFinances currentItem = (ShowFinances) getItem(position);
        if (currentItem != null) {
            holder.id.setText(String.valueOf(currentItem.getId()));
            holder.sum.setText(String.format("%.2f", currentItem.getSum()));
            holder.name.setText(currentItem.getName());
        }

        return view;
    }

    private static class ViewHolder {
        TextView id;
        TextView sum;
        TextView name;
    }

    public void addItem(ShowFinances item) {
        finances.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        finances.remove(position);
        notifyDataSetChanged();
    }
}
