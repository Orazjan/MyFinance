package com.example.myfinance.Prevalent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Класс, который управляет пошаговым обучением (туториалом) для пользователя.
 * <p>
 * Использует библиотеку TapTargetView для создания визуальных подсказок и подсветки элементов
 * пользовательского интерфейса.
 */
public class TutorialController {

    private final String TAG = "TutorialController";
    private final Activity activity;
    private final Queue<TutorialStep> tutorialSteps;
    private TapTargetSequence tutorialSequence;

    /**
     * Конструктор контроллера обучения.
     *
     * @param activity Текущая активность, в которой будет отображаться обучение.
     */
    public TutorialController(Activity activity) {
        this.activity = activity;
        this.tutorialSteps = new LinkedList<>();
    }

    /**
     * Добавляет новый шаг в последовательность обучения.
     *
     * @param targetView    Целевой элемент, который нужно подсветить.
     * @param description   Описание шага.
     * @param title         Заголовок шага.
     * @param action        Действие, которое нужно выполнить после завершения шага.
     * @param highlightOnly Если true, отображает только подсветку без текста подсказки.
     */
    public void addStep(View targetView, String description, String title, Runnable action, boolean highlightOnly) {
        tutorialSteps.add(new TutorialStep(targetView, description, title, action, highlightOnly));
        Log.d(TAG, "Added tutorial step for: " + targetView.getId());
    }

    /**
     * Запускает последовательность обучения.
     * <p>
     * Если очередь шагов пуста, обучение не запускается.
     */
    @SuppressLint("ResourceType")
    public void startTutorial() {
        if (tutorialSteps.isEmpty()) {
            Log.w(TAG, "Tutorial steps queue is empty. Cannot start tutorial.");
            return;
        }

        tutorialSequence = new TapTargetSequence(activity)
                .continueOnCancel(true);

        // Создаем последовательность TapTarget на основе добавленных шагов
        for (TutorialStep step : tutorialSteps) {
            // Создаем TapTarget, передавая заголовок и описание.
            TapTarget tapTarget = TapTarget.forView(step.targetView, step.title, step.description);

            // Настраиваем подсветку и дополнительные параметры
            tapTarget.targetRadius(70) // Увеличиваем радиус подсветки
                    .outerCircleAlpha(0.80f) // Делаем затемнение более темным
                    .drawShadow(true) // Добавляем тень для лучшей видимости
                    .cancelable(false) // Делаем шаг обязательным для выполнения
                    .titleTextColor(Color.WHITE) // Явно устанавливаем белый цвет для заголовка
                    .descriptionTextColor(Color.WHITE); // Явно устанавливаем белый цвет для описания

            tutorialSequence.target(tapTarget);
        }

        // Запускаем последовательность
        tutorialSequence.start();
    }

    /**
     * Внутренний класс для представления одного шага обучения.
     */
    private static class TutorialStep {
        final View targetView;
        final String description;
        final String title;
        final Runnable action;
        final boolean highlightOnly;

        TutorialStep(View targetView, String description, String title, Runnable action, boolean highlightOnly) {
            this.targetView = targetView;
            this.description = description;
            this.title = title;
            this.action = action;
            this.highlightOnly = highlightOnly;
        }
    }
}
