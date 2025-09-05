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

/**
 * Адаптер для отображения списка категорий в RecyclerView.
 * Отображает название категории, сумму и тип операции.
 */
public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.ViewHolder> {
    // Список элементов категорий, которые будут отображаться
    private List<CategoryItem> categoryItems;
    // Слушатель для обработки кликов по элементам списка
    private OnItemClickListener listener;

    /**
     * Интерфейс для обработки событий клика по элементу списка.
     */
    public interface OnItemClickListener {
        /**
         * Вызывается при клике на элемент категории.
         *
         * @param item Объект CategoryItem, на который был совершен клик.
         */
        void onItemClick(CategoryItem item);
    }

    /**
     * Устанавливает слушатель для кликов по элементам списка.
     *
     * @param listener Реализация OnItemClickListener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Конструктор адаптера.
     *
     * @param categoryItems Исходный список элементов категорий.
     */
    public CategoryViewAdapter(List<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    /**
     * Создает новые ViewHolder'ы (вызывается LayoutManager'ом).
     *
     * @param parent   ViewGroup, в которую будет добавлен новый View.
     * @param viewType Тип View нового View.
     * @return Новый экземпляр ViewHolder.
     */
    @NonNull
    @Override
    public CategoryViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Надуваем" макет для каждого элемента списка
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        // Передаем только слушатель в ViewHolder
        return new ViewHolder(view, listener);
    }

    /**
     * Заменяет содержимое ViewHolder'а (вызывается LayoutManager'ом).
     *
     * @param holder   ViewHolder, который должен быть обновлен.
     * @param position Позиция элемента в списке данных.
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryViewAdapter.ViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItems.get(position);

        // Привязываем данные к View элементам ViewHolder'а
        holder.categoryName.setText(categoryItem.getCategoryName());
        holder.categorySum.setText(String.valueOf(categoryItem.getSum()));
        holder.categoryOperation.setText(categoryItem.getOperation());
    }

    /**
     * Возвращает общее количество элементов в наборе данных.
     *
     * @return Количество элементов в списке.
     */
    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    /**
     * Очищает список элементов и уведомляет адаптер об изменении.
     */
    public void clear() {
        int size = categoryItems.size();
        categoryItems.clear();
        // Уведомляем адаптер, что все элементы были удалены
        notifyItemRangeRemoved(0, size);
    }

    /**
     * Обновляет список элементов в адаптере.
     *
     * @param newItems Новый список элементов.
     */
    public void setItems(List<CategoryItem> newItems) {
        this.categoryItems = newItems;
        // Уведомляем адаптер, что набор данных изменился.
        // Для более эффективных обновлений можно использовать DiffUtil.
        notifyDataSetChanged();
    }

    /**
     * ViewHolder представляет собой один элемент списка в RecyclerView.
     * Содержит ссылки на View элементы макета item_category.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public TextView categorySum;
        public TextView categoryOperation;

        /**
         * Конструктор ViewHolder'а.
         *
         * @param itemView View для одного элемента списка.
         * @param listener Слушатель для обработки кликов по элементам.
         */
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categorySum = itemView.findViewById(R.id.categorySum);
            categoryOperation = itemView.findViewById(R.id.categoryOperation);

            // Устанавливаем слушатель кликов один раз в конструкторе ViewHolder
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // getAdapterPosition() возвращает текущую позицию элемента в адаптере
                        int position = getAdapterPosition();
                        // Проверяем, что позиция валидна и что список не пуст
                        if (position != RecyclerView.NO_POSITION && position < categoryItems.size()) {
                            // Обращаемся к главному списку адаптера, который всегда актуален
                            listener.onItemClick(categoryItems.get(position));
                        }
                    }
                }
            });
        }
    }
}
