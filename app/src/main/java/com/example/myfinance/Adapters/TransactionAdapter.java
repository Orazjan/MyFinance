package com.example.myfinance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myfinance.Prevalent.DateFormatter;
import com.example.myfinance.R;
import com.example.myfinance.data.Finances;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends ArrayAdapter<Finances> {

    private Context context;
    private List<Finances> transactionsList;

    public TransactionAdapter(Context context, List<Finances> transactionsList) {
        super(context, R.layout.list_item_transaction, transactionsList);
        this.context = context;
        this.transactionsList = transactionsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Получаем объект Finances для текущей позиции
        Finances transaction = getItem(position);

        // Проверяем, существует ли переиспользуемый view, иначе создаем новый
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_transaction, parent, false);
        }

        // Находим TextViews в файле разметки
        TextView transactionSum = convertView.findViewById(R.id.transactionAmountTextView);
        TextView transactionName = convertView.findViewById(R.id.transactionCategoryTextView);
        TextView transactionComment = convertView.findViewById(R.id.transaction_comment);
        TextView transactionDate = convertView.findViewById(R.id.transaction_date);

        // Заполняем TextViews данными из объекта Finances
        if (transaction != null) {
            // Форматируем сумму
            String formattedSum = String.format(Locale.getDefault(), "%.2f", transaction.getSumma());
            transactionSum.setText(formattedSum);

            // Устанавливаем название категории
            transactionName.setText(transaction.getFinanceResult());

            // Устанавливаем комментарий
            if (transaction.getComments() != null && !transaction.getComments().isEmpty()) {
                transactionComment.setText(transaction.getComments());
            } else {
                transactionComment.setText("Нет комментария");
            }

            String dateString = transaction.getDate();
            if (dateString != null && !dateString.isEmpty()) {
                // Преобразуем строку в объект Date с помощью вашего парсера
                Date parsedDate = DateFormatter.parseDate(dateString);

                // Проверяем, удалось ли распарсить дату
                if (parsedDate != null) {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String formattedDate = displayFormat.format(parsedDate);
                    transactionDate.setText(formattedDate);
                } else {
                    transactionDate.setText("Неверный формат даты");
                }
            } else {
                transactionDate.setText("Нет даты");
            }
        }

        return convertView;
    }
}
