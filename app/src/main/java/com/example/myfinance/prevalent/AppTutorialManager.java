//package com.example.myfinance.prevalent;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.view.View;
//
//import com.example.myfinance.MainActivity;
//import com.example.myfinance.R;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.tabs.TabLayout;
//
//import java.util.Objects;
//
///**
// * Менеджер для инициализации и запуска всех туториалов в приложении.
// */
//public class AppTutorialManager {
//    private static final String TAG = "AppTutorialManager";
//    private static final String TUTORIAL_SHOWN_KEY = "main_fragment_tutorial_shown";
//
//    // Константы позиций вкладок (должны совпадать с порядком в ViewPager MainActivity)
//    private static final int TAB_ANALIZ = 0;
//    private static final int TAB_MAIN = 1;
//    private static final int TAB_PROFILE = 2;
//
//    private final Context context;
//    private final TutorialController tutorialController;
//    private boolean mainTutorialCompleted = false;
//
//    public AppTutorialManager(Context context, TutorialController tutorialController) {
//        this.context = context;
//        this.tutorialController = tutorialController;
//    }
//
//    /**
//     * Инициализирует и добавляет шаги туториала для элементов MainActivity (Табы и FAB).
//     */
//    public void setupMainActivityTutorial(MainActivity mainActivity) {
//        TabLayout tabLayout = mainActivity.findViewById(R.id.tab_layout);
//        if (tabLayout != null) {
//
//            // ШАГ 1: Таб "Главное"
//            if (tabLayout.getTabAt(1) != null) {
//                View mainTab = Objects.requireNonNull(tabLayout.getTabAt(1)).view;
//                // ДЕЙСТВИЕ: Переключиться на вкладку "Главное" перед показом
//                tutorialController.addStep(mainTab, "Здесь вы можете наблюдать за остатками и историей операций", "Главная страница", 1, () -> mainActivity.selectTab(TAB_MAIN));
//            }
//
//            // ШАГ 2: Кнопка добавления (FAB)
//            FloatingActionButton btnAddNewCheck = mainActivity.findViewById(R.id.btnAddNewCheck);
//            if (btnAddNewCheck != null) {
//                // ДЕЙСТВИЕ: Убедиться, что мы на вкладке "Главное" (FAB виден только там)
//                tutorialController.addStep(btnAddNewCheck, "Нажмите на эту кнопку, чтобы добавить новую финансовую операцию.", "Добавление операции", 2, () -> mainActivity.selectTab(TAB_MAIN));
//            }
//
//            // ШАГ 3: Таб "Анализ"
//            if (tabLayout.getTabAt(0) != null) {
//                View analizTab = Objects.requireNonNull(tabLayout.getTabAt(0)).view;
//                // ДЕЙСТВИЕ: Переключиться на вкладку "Анализ" перед показом
//                tutorialController.addStep(analizTab, "В этом разделе вы можете анализировать свои финансы с помощью графиков.", "Анализ финансов", 3, () -> mainActivity.selectTab(TAB_ANALIZ));
//            }
//
//            // ШАГ 4: Таб "Профиль"
//            if (tabLayout.getTabAt(2) != null) {
//                View profileTab = Objects.requireNonNull(tabLayout.getTabAt(2)).view;
//                // ДЕЙСТВИЕ: Переключиться на вкладку "Профиль" перед показом
//                tutorialController.addStep(profileTab, "В профиле вы можете управлять своими настройками.", "Управление профилем", 4, () -> mainActivity.selectTab(TAB_PROFILE));
//            }
//        }
//    }
//
//    /**
//     * Инициализирует и добавляет шаги туториала для MainFragment.
//     */
//    public void setupMainFragmentTutorial(View fragment) {
//        // Здесь мы передаем null вместо действия, так как внутри фрагмента переключения не нужны,
//        // либо используем перегруженный метод addStep без последнего аргумента.
//
//        // Шаг 0: Кнопка справки
//        View ivHelp = fragment.findViewById(R.id.ivHelp);
//        if (ivHelp != null) {
//            tutorialController.addStep(ivHelp, "Нажмите сюда в любой момент, чтобы повторить это обучение.", "Справка", 99, null);
//        }
//
//        // Шаг 5: Карточка баланса
//        View cardViewOstatok = fragment.findViewById(R.id.cardViewOstatok);
//        if (cardViewOstatok != null) {
//            tutorialController.addStep(cardViewOstatok, "Здесь отображается ваш текущий баланс.", "Текущий баланс", 5, null);
//        }
//
//        // Шаг 6: Фильтр
//        View spinner = fragment.findViewById(R.id.spinnerForMonth);
//        if (spinner != null) {
//            tutorialController.addStep(spinner, "Фильтр операций по месяцам.", "Фильтр по месяцам", 6, null);
//        }
//
//        // Шаг 7: Список
//        View listView = fragment.findViewById(R.id.mainCheck);
//        if (listView != null) {
//            tutorialController.addStep(listView, "Список ваших операций. Удерживайте для редактирования.", "Список операций", 7, null);
//        }
//    }
//
//    /**
//     * Автоматический запуск (только один раз).
//     */
//    public void triggerMainFragmentTutorial() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        boolean tutorialShown = sharedPreferences.getBoolean(TUTORIAL_SHOWN_KEY, false);
//
//        if (!tutorialShown && !mainTutorialCompleted) {
//            Log.d(TAG, "Triggering tutorial automatically.");
//
//            // Если мы запускаем туториал, убедимся, что мы на главном экране
//            if (context instanceof MainActivity) {
//                ((MainActivity) context).selectTab(TAB_MAIN);
//            }
//
//            tutorialController.startTutorial();
//
//            sharedPreferences.edit().putBoolean(TUTORIAL_SHOWN_KEY, true).apply();
//            mainTutorialCompleted = true;
//        }
//    }
//
//    /**
//     * Принудительный запуск (по кнопке).
//     */
//    public void forceStartTutorial() {
//        Log.d(TAG, "Forcing tutorial to start.");
//
//        // Обязательно переключаем на главный экран перед стартом,
//        // иначе туториал может попытаться показать элементы MainFragment, которых нет на экране
//        if (context instanceof MainActivity) {
//            ((MainActivity) context).selectTab(TAB_MAIN);
//        }
//
//        tutorialController.startTutorial();
//    }
//}