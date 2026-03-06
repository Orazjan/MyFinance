//package com.example.myfinance.prevalent;
//
//import android.animation.Animator;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Handler;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.cardview.widget.CardView;
//import androidx.core.content.ContextCompat;
//
//import com.example.myfinance.R;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
//import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
//
//public class TutorialController {
//
//    private final Context context;
//    private final List<TutorialStep> tutorialSteps = new ArrayList<>();
//    private int currentStep = 0;
//    private boolean isTutorialActive = false;
//    private final Handler handler = new Handler();
//
//    public TutorialController(Context context) {
//        this.context = context;
//    }
//
//    /**
//     * Добавляет обычный шаг туториала с предварительным действием.
//     * @param targetView View, на которую указывает подсказка.
//     * @param description Текст подсказки.
//     * @param title Заголовок подсказки.
//     * @param order Порядок шага (для сортировки, если нужно).
//     * @param preAction Действие, которое нужно выполнить ПЕРЕД показом (например, переключить вкладку).
//     */
//    public void addStep(View targetView, String description, String title, int order, Runnable preAction) {
//        tutorialSteps.add(new TutorialStep(targetView, description, title, order, false, preAction));
//    }
//
//    /**
//     * Перегрузка для совместимости (без действия).
//     */
//    public void addStep(View targetView, String description, String title, int order) {
//        addStep(targetView, description, title, order, null);
//    }
//
//    /**
//     * Добавляет специальный, неблокирующий шаг туториала.
//     */
//    public void addNonBlockingHintStep(View targetView, String description, String title, int order) {
//        // Для неблокирующих шагов action обычно не нужен, передаем null
//        tutorialSteps.add(new TutorialStep(targetView, description, title, order, true, null));
//    }
//
//    public void startTutorial() {
//        if (isTutorialActive) {
//            return;
//        }
//        isTutorialActive = true;
//        currentStep = 0;
//        showNextStep();
//    }
//
//    private void showNextStep() {
//        if (currentStep < tutorialSteps.size()) {
//            TutorialStep step = tutorialSteps.get(currentStep);
//
//            if (step.preAction != null) {
//                step.preAction.run();
//            }
//
//            // Даем системе крошечную паузу (50мс), чтобы UI успел обновиться после действия (смена таба)
//            // перед тем, как искать View и рисовать подсказку.
//            handler.postDelayed(() -> {
//                if (step.targetView != null && step.targetView.isShown()) {
//                    if (step.isNonBlocking) {
//                        showNonBlockingHint(step);
//                    } else {
//                        showBlockingStep(step);
//                    }
//                } else {
//                    // Если view все равно не видна (или была удалена), пропускаем шаг
//                    // Но логируем это для отладки
//                    currentStep++;
//                    showNextStep();
//                }
//            }, 50); // Задержка 50мс
//
//        } else {
//            // Туториал завершен
//            isTutorialActive = false;
//        }
//    }
//
//    /**
//     * Показывает обычный (блокирующий) шаг туториала.
//     */
//    private void showBlockingStep(TutorialStep step) {
//        if (step.targetView == null) {
//            currentStep++;
//            showNextStep();
//            return;
//        }
//
//        new MaterialTapTargetPrompt.Builder((Activity) context).setTarget(step.targetView).setPrimaryText(step.title).setSecondaryText(step.description).setFocalColour(Color.TRANSPARENT) // Прозрачный фокус, чтобы видеть кнопку
//                // Используем цвета из ресурсов
//                .setBackgroundColour(ContextCompat.getColor(context, R.color.app_blue)).setPromptBackground(new RectanglePromptBackground()).setPromptStateChangeListener((prompt, state) -> {
//                    if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING) {
//                        currentStep++;
//                        showNextStep();
//                    }
//                }).show();
//    }
//
//    /**
//     * Показывает специальную, неблокирующую подсказку (кастомная карточка).
//     */
//    private void showNonBlockingHint(TutorialStep step) {
//        // Находим корневой Layout, чтобы добавить карточку поверх всего
//        ViewGroup rootView = (ViewGroup) ((Activity) context).findViewById(android.R.id.content);
//
//        CardView hintCard = new CardView(context);
//        hintCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        hintCard.setCardBackgroundColor(Color.WHITE); // Белый фон
//        hintCard.setRadius(24);
//        hintCard.setCardElevation(16);
//        hintCard.setAlpha(0);
//
//        LinearLayout layout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.setGravity(Gravity.CENTER_HORIZONTAL);
//        layout.setPadding(40, 40, 40, 40);
//
//        TextView titleTextView = new TextView(context);
//        titleTextView.setText(step.title);
//        titleTextView.setTextColor(Color.BLACK); // Черный текст
//        titleTextView.setTextSize(18);
//        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
//        titleTextView.setGravity(Gravity.CENTER);
//
//        TextView descriptionTextView = new TextView(context);
//        descriptionTextView.setText(step.description);
//        descriptionTextView.setTextColor(Color.GRAY); // Серый текст описания
//        descriptionTextView.setTextSize(14);
//        descriptionTextView.setGravity(Gravity.CENTER);
//        descriptionTextView.setPadding(0, 8, 0, 0);
//
//        layout.addView(titleTextView);
//        layout.addView(descriptionTextView);
//        hintCard.addView(layout);
//
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.BOTTOM;
//        layoutParams.setMargins(40, 0, 40, 100);
//
//        rootView.addView(hintCard, layoutParams);
//
//        // Анимация появления
//        hintCard.animate().alpha(1).setDuration(500).start();
//
//        // Автоматическое скрытие через 6 секунд
//        handler.postDelayed(() -> {
//            hintCard.animate().alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    rootView.removeView(hintCard);
//                    currentStep++;
//                    showNextStep();
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                }
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                }
//            });
//        }, TimeUnit.SECONDS.toMillis(6));
//    }
//
//    /**
//     * Внутренний класс для хранения данных шага.
//     */
//    private static class TutorialStep {
//        final View targetView;
//        final String description;
//        final String title;
//        final int order;
//        final boolean isNonBlocking;
//        final Runnable preAction;
//
//        TutorialStep(View targetView, String description, String title, int order, boolean isNonBlocking, Runnable preAction) {
//            this.targetView = targetView;
//            this.description = description;
//            this.title = title;
//            this.order = order;
//            this.isNonBlocking = isNonBlocking;
//            this.preAction = preAction;
//        }
//    }
//}