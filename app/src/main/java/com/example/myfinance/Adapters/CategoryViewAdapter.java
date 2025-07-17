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
         * @param item Объект CategoryItem, на который был совершен клик.
         */
        void onItemClick(CategoryItem item);
    }

    /**
     * Устанавливает слушатель для кликов по элементам списка.
     * @param listener Реализация OnItemClickListener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Конструктор адаптера.
     * @param categoryItems Исходный список элементов категорий.
     */
    public CategoryViewAdapter(List<CategoryItem> categoryItems) {
        this.categoryItems = categoryItems;
    }

    /**
     * Создает новые ViewHolder'ы (вызывается LayoutManager'ом).
     * @param parent ViewGroup, в которую будет добавлен новый View.
     * @param viewType Тип View нового View.
     * @return Новый экземпляр ViewHolder.
     */
    @NonNull
    @Override
    public CategoryViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Надуваем" макет для каждого элемента списка
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        // Передаем слушатель и список categoryItems в ViewHolder
        return new ViewHolder(view, listener, categoryItems);
    }

    /**
     * Заменяет содержимое ViewHolder'а (вызывается LayoutManager'ом).
     * @param holder ViewHolder, который должен быть обновлен.
     * @param position Позиция элемента в списке данных.
     */
    @Override
    public void onBindViewHolder(@NonNull CategoryViewAdapter.ViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItems.get(position);

        // Привязываем данные к View элементам ViewHolder'а
        holder.categoryName.setText(categoryItem.getCategoryName());
        holder.categorySum.setText(String.valueOf(categoryItem.getSum()));
        // Assuming CategoryItem has a getOperation() method
        holder.categoryOperation.setText(categoryItem.getOperation());

        // ViewHolder уже имеет OnClickListener, который использует getAdapterPosition()
        // Здесь не нужно устанавливать новый OnClickListener для каждого элемента
    }

    /**
     * Возвращает общее количество элементов в наборе данных.
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public TextView categorySum;
        public TextView categoryOperation;

        // Добавлено поле для хранения списка элементов
        private final List<CategoryItem> localCategoryItems;

        /**
         * Конструктор ViewHolder'а.
         * @param itemView View для одного элемента списка.
         * @param listener Слушатель для обработки кликов по элементам.
         * @param categoryItems Список элементов категорий, необходимый для доступа из слушателя.
         */
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<CategoryItem> categoryItems) {
            super(itemView);
            this.localCategoryItems = categoryItems; // Инициализируем локальное поле
            categoryName = itemView.findViewById(R.id.categoryName);
            categorySum = itemView.findViewById(R.id.categorySum);
            categoryOperation = itemView.findViewById(R.id.categoryOperation);

            // --- IMPORTANT: Set the OnClickListener once in the ViewHolder constructor ---
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // getAdapterPosition() возвращает текущую позицию элемента в адаптере
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) { // Проверяем, что позиция валидна
                            // Используем локальное поле localCategoryItems
                            listener.onItemClick(localCategoryItems.get(position));
                        }
                    }
                }
            });
            // --- END OF IMPORTANT CHANGE ---
        }
    }
}
