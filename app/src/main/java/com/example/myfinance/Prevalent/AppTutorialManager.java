package com.example.myfinance.Prevalent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.myfinance.MainActivity;
import com.example.myfinance.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

/**
 * Менеджер для инициализации и запуска всех туториалов в приложении.
 * Использует SharedPreferences для отслеживания, был ли туториал уже показан.
 */
public class AppTutorialManager {
    private static final String TAG = "AppTutorialManager";
    private static final String TUTORIAL_SHOWN_KEY = "main_fragment_tutorial_shown";

    private final Context context;
    private final TutorialController tutorialController;
    private boolean mainTutorialCompleted = false;

    public AppTutorialManager(Context context, TutorialController tutorialController) {
        this.context = context;
        this.tutorialController = tutorialController;
    }

    /**
     * Инициализирует и добавляет шаги туториала для MainActivity.
     * Этот метод должен быть вызван в onCreate() MainActivity.
     *
     * @param mainActivity Экземпляр MainActivity для доступа к View.
     */
    public void setupMainActivityTutorial(MainActivity mainActivity) {
        View tutorialButton = mainActivity.findViewById(R.id.button_tutorial);
        if (tutorialButton != null) {
            tutorialController.addStep(tutorialButton, "Эта кнопка запускает обучение по работе с приложением", "Обучение", -1);
        }

        TabLayout tabLayout = mainActivity.findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            if (tabLayout.getTabAt(1) != null) {
                View mainTab = Objects.requireNonNull(tabLayout.getTabAt(1)).view;
                tutorialController.addStep(mainTab, "Здесь вы можете наблюдать за остатками и историей операций", "Главная страница", 1);
            }
            FloatingActionButton btnAddNewCheck = mainActivity.findViewById(R.id.btnAddNewCheck);
            if (btnAddNewCheck != null) {
                tutorialController.addStep(btnAddNewCheck, "Нажмите на эту кнопку, чтобы добавить новую финансовую операцию — доход или расход.", "Добавление операции", 2);
            }
            if (tabLayout.getTabAt(0) != null) {
                View analizTab = Objects.requireNonNull(tabLayout.getTabAt(0)).view;
                tutorialController.addStep(analizTab, "В этом разделе вы можете анализировать свои финансы с помощью графиков и отчетов", "Анализ финансов", 3);
            }
            if (tabLayout.getTabAt(2) != null) {
                View profileTab = Objects.requireNonNull(tabLayout.getTabAt(2)).view;
                tutorialController.addStep(profileTab, "В профиле вы можете управлять своими настройками и учетной записью", "Управление профилем", 4);
            }
        }
    }

    /**
     * Инициализирует и добавляет шаги туториала для MainFragment.
     * Этот метод должен быть вызван в onViewCreated() MainFragment.
     *
     * @param fragment Корневой View фрагмента.
     */
    public void setupMainFragmentTutorial(View fragment) {
        // Шаги для MainFragment (карточка, спиннер, список)
        View cardViewOstatok = fragment.findViewById(R.id.cardViewOstatok);
        if (cardViewOstatok != null) {
            tutorialController.addStep(cardViewOstatok, "Здесь отображается ваш текущий баланс. Нажмите на карточку, чтобы увидеть сводку расходов по категориям.", "Текущий баланс", 5);
        }
        Spinner spinner = fragment.findViewById(R.id.spinnerForMonth);
        if (spinner != null) {
            tutorialController.addStep(spinner, "Используйте это меню, чтобы отфильтровать операции по месяцам. По умолчанию отображаются все операции.", "Фильтр по месяцам", 6);
        }
        ListView listView = fragment.findViewById(R.id.mainCheck);
        if (listView != null) {
            tutorialController.addStep(listView, "В этом списке вы можете видеть все ваши финансовые операции. Нажмите и удерживайте, чтобы изменить или удалить запись.", "Список операций", 7);
        }
    }

    /**
     * Запускает туториал. Проверяет, был ли туториал уже показан.
     * Предназначен для автоматического запуска (например, при первом входе).
     */
    public void startTutorial() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean tutorialShown = sharedPreferences.getBoolean(TUTORIAL_SHOWN_KEY, false);

        if (!tutorialShown) {
            Log.d(TAG, "Starting tutorial automatically.");
            tutorialController.startTutorial();
            sharedPreferences.edit().putBoolean(TUTORIAL_SHOWN_KEY, true).apply();
            mainTutorialCompleted = true; // Отмечаем, что главный туториал был показан
        } else {
            Log.d(TAG, "Tutorial has already been shown.");
            mainTutorialCompleted = true;
        }
    }

    /**
     * Запускает туториал для MainFragment, если он еще не был показан.
     * Вызывается из MainActivity при переключении на MainFragment.
     */
    public void triggerMainFragmentTutorial() {
        if (!mainTutorialCompleted) {
            Log.d(TAG, "Triggering MainFragment tutorial.");
            tutorialController.startTutorial();
            mainTutorialCompleted = true;
        } else {
            Log.d(TAG, "MainFragment tutorial has already been triggered.");
        }
    }

    /**
     * Принудительно запускает туториал, игнорируя флаг.
     * Предназначен для запуска по нажатию кнопки.
     */
    public void forceStartTutorial() {
        Log.d(TAG, "Forcing tutorial to start.");
        tutorialController.startTutorial();
    }
}
