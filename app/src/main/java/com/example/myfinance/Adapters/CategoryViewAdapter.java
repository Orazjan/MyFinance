package com.example.myfinance.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinance.Prevalent.CategoryItem;
import com.example.myfinance.R;

import java.util.List;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.ViewHolder> {
    private List<CategoryItem> categoryItems;
    private OnItemClickListener listener;

    public void clear() {
        categoryItems.clear();
    }

    public interface OnItemClickListener {
        void onItemClick(CategoryItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryViewAdapter(List<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override

    public CategoryViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewAdapter.ViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItems.get(position);
        holder.categoryName.setText(categoryItem.getCategoryName());
        holder.categorySum.setText(String.valueOf(categoryItem.getSum()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(categoryItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public TextView categorySum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categorySum = itemView.findViewById(R.id.categorySum);

        }
    }

    public void setItems(List<CategoryItem> newItems) {
        this.categoryItems = newItems;
        notifyDataSetChanged();
    }
}
