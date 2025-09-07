package com.example.myfinance.Models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfinance.MyApplication;
import com.example.myfinance.Prevalent.DateFormatter;
import com.example.myfinance.data.DateSum;
import com.example.myfinance.data.FinanceRepository;
import com.example.myfinance.data.Finances;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FinanceChartViewModel extends AndroidViewModel {
    // Единый источник данных. ViewModel получает его от репозитория и
    // не изменяет, так как это LiveData.
    private final LiveData<List<Finances>> allFinances;
    private final FinanceRepository repository;

    // Этот LiveData хранит текущий выбранный месяц.
    // Изменение его значения будет автоматически запускать трансформацию.
    private final MutableLiveData<String> selectedMonth = new MutableLiveData<>("Все");

    public FinanceChartViewModel(@NonNull Application application) {
        super(application);
        this.repository = ((MyApplication) application).getFinanceRepository();
        // Получаем все данные из репозитория один раз.
        this.allFinances = repository.getAllFinances();
    }

    /**
     * Запускает синхронизацию данных из Firestore.
     * Этот метод просто делегирует вызов репозиторию.
     */
    public void syncDataFromFirestore(FinanceRepository.SyncCallback callback) {
        repository.syncFromFirestore(callback);
    }

    /**
     * Устанавливает выбранный месяц для фильтрации данных.
     * @param monthEn Название месяца на английском (или "Все").
     */
    public void filterByMonth(String monthEn) {
        selectedMonth.setValue(monthEn);
    }

    /**
     * Сбрасывает фильтр, чтобы отобразить все данные.
     * Мы просто устанавливаем значение фильтра на "Все".
     */
    public void showAllData() {
        filterByMonth("Все");
    }

    // --- LiveData для PieChart (Круговая диаграмма) ---

    // Эта LiveData будет автоматически обновляться каждый раз, когда меняется
    // `selectedMonth` или `allFinances`.
    public LiveData<List<PieEntry>> getPieChartDataForExpenses() {
        return Transformations.switchMap(selectedMonth, month ->
                Transformations.map(allFinances, finances -> {
                    List<Finances> filteredFinances = filterFinances(finances, month, "Расход");
                    return processForPieChart(filteredFinances);
                })
        );
    }

    public LiveData<List<PieEntry>> getPieChartDataForIncomes() {
        return Transformations.switchMap(selectedMonth, month ->
                Transformations.map(allFinances, finances -> {
                    List<Finances> filteredFinances = filterFinances(finances, month, "Доход");
                    return processForPieChart(filteredFinances);
                })
        );
    }

    public LiveData<List<PieEntry>> getPieChartDataForAll() {
        return Transformations.switchMap(selectedMonth, month ->
                Transformations.map(allFinances, finances -> {
                    List<Finances> filteredFinances = filterFinances(finances, month, null);
                    return processForPieChart(filteredFinances);
                })
        );
    }

    // --- LiveData для LineChart (Линейный график) ---

    public LiveData<List<DateSum>> getExpensesLineChartDateSums() {
        return Transformations.switchMap(selectedMonth, month ->
                Transformations.map(allFinances, finances -> {
                    List<Finances> filteredFinances = filterFinances(finances, month, "Расход");
                    return processForLineChart(filteredFinances);
                })
        );
    }

    public LiveData<List<DateSum>> getIncomesLineChartDateSums() {
        return Transformations.switchMap(selectedMonth, month ->
                Transformations.map(allFinances, finances -> {
                    List<Finances> filteredFinances = filterFinances(finances, month, "Доход");
                    return processForLineChart(filteredFinances);
                })
        );
    }

    public LiveData<Double> getTotalIncomesSum() {
        return Transformations.switchMap(selectedMonth, month ->
                Transformations.map(allFinances, finances -> {
                    List<Finances> filteredFinances = filterFinances(finances, month, "Доход");
                    return filteredFinances.stream().mapToDouble(Finances::getSumma).sum();
                })
        );
    }

    // --- Новые методы для получения полных списков ---

    /**
     * Возвращает полный список доходов (без фильтра по месяцу).
     */
    public LiveData<List<Finances>> getAllIncomes() {
        return Transformations.map(allFinances, finances ->
                filterFinances(finances, "Все", "Доход")
        );
    }

    /**
     * Возвращает полный список расходов (без фильтра по месяцу).
     */
    public LiveData<List<Finances>> getAllExpenses() {
        return Transformations.map(allFinances, finances ->
                filterFinances(finances, "Все", "Расход")
        );
    }

    /**
     * Вспомогательный метод для фильтрации финансовых операций.
     */
    private List<Finances> filterFinances(List<Finances> finances, String month, String type) {
        if (finances == null) {
            return new ArrayList<>();
        }
        return finances.stream()
                .filter(finance -> {
                    if (type != null && !type.equals(finance.getOperationType())) {
                        return false;
                    }
                    if (month.equals("Все")) {
                        return true;
                    }
                    String financeMonth = DateFormatter.getMonthName(finance.getDate());
                    return financeMonth != null && financeMonth.equalsIgnoreCase(month);
                })
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод для обработки операций для кругового графика.
     */
    private List<PieEntry> processForPieChart(List<Finances> finances) {
        // Мы группируем данные по категории (financeResult), а не по комментариям (comments)
        return finances.stream()
                .filter(f -> f.getFinanceResult() != null) // Убедимся, что категория не null
                .collect(Collectors.groupingBy(Finances::getFinanceResult, Collectors.summingDouble(Finances::getSumma)))
                .entrySet().stream()
                .map(entry -> new PieEntry((float) entry.getValue().doubleValue(), entry.getKey()))
                .sorted(Comparator.comparing(PieEntry::getLabel))
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод для обработки операций для линейного графика.
     */
    private List<DateSum> processForLineChart(List<Finances> finances) {
        return finances.stream()
                .filter(f -> f.getDate() != null) // Добавили фильтрацию по полю date
                .collect(Collectors.groupingBy(Finances::getDate, Collectors.summingDouble(Finances::getSumma)))
                .entrySet().stream()
                .map(entry -> new DateSum(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DateSum::getDate))
                .collect(Collectors.toList());
    }
}
