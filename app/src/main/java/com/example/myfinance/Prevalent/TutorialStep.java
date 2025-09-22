package com.example.myfinance.Prevalent;

import android.view.View;

/**
 * Класс, представляющий один шаг обучения.
 * Содержит информацию о целевом элементе, тексте и позиции вкладки.
 */
public class TutorialStep {
    public final View targetView;
    public final String description;
    public final String title;
    public final int tabPosition;

    /**
     * Конструктор для шага, который не требует переключения вкладки.
     *
     * @param targetView  Целевой элемент.
     * @param description Описание шага.
     * @param title       Заголовок шага.
     */
    public TutorialStep(View targetView, String description, String title) {
        this.targetView = targetView;
        this.description = description;
        this.title = title;
        this.tabPosition = -1;
    }

    /**
     * Конструктор для шага, который требует переключения вкладки.
     *
     * @param targetView  Целевой элемент.
     * @param description Описание шага.
     * @param title       Заголовок шага.
     * @param tabPosition Позиция вкладки, на которую нужно переключиться.
     */
    public TutorialStep(View targetView, String description, String title, int tabPosition) {
        this.targetView = targetView;
        this.description = description;
        this.title = title;
        this.tabPosition = tabPosition;
    }
}
