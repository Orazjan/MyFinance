package com.example.myfinance.Prevalent;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.myfinance.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;

public class TutorialController {

    private final Context context;
    private final List<TutorialStep> tutorialSteps = new ArrayList<>();
    private int currentStep = 0;
    private boolean isTutorialActive = false;
    private final Handler handler = new Handler();

    public TutorialController(Context context) {
        this.context = context;
    }

    /**
     * Добавляет обычный шаг туториала.
     * @param targetView View, на которую указывает подсказка.
     * @param description Текст подсказки.
     * @param title Заголовок подсказки.
     * @param order Порядок шага.
     */
    public void addStep(View targetView, String description, String title, int order) {
        tutorialSteps.add(new TutorialStep(targetView, description, title, order, false));
    }

    /**
     * Добавляет специальный, неблокирующий шаг туториала.
     * @param targetView View, над которой отобразится подсказка (не будет блокировать).
     * @param description Текст подсказки.
     * @param title Заголовок подсказки.
     * @param order Порядок шага.
     */
    public void addNonBlockingHintStep(View targetView, String description, String title, int order) {
        tutorialSteps.add(new TutorialStep(targetView, description, title, order, true));
    }

    public void startTutorial() {
        if (isTutorialActive) {
            return;
        }
        isTutorialActive = true;
        currentStep = 0;
        showNextStep();
    }

    private void showNextStep() {
        if (currentStep < tutorialSteps.size()) {
            TutorialStep step = tutorialSteps.get(currentStep);
            if (step.targetView.isShown()) {
                if (step.isNonBlocking) {
                    showNonBlockingHint(step);
                } else {
                    showBlockingStep(step);
                }
            } else {
                // Если view не видна, пропускаем этот шаг и переходим к следующему
                currentStep++;
                showNextStep();
            }
        } else {
            // Туториал завершен
            isTutorialActive = false;
        }
    }

    /**
     * Показывает обычный (блокирующий) шаг туториала.
     */
    private void showBlockingStep(TutorialStep step) {
        if (step.targetView == null) {
            currentStep++;
            showNextStep();
            return;
        }

        MaterialTapTargetPrompt.Builder promptBuilder = new MaterialTapTargetPrompt.Builder((Activity) context).setTarget(step.targetView).setPrimaryText(step.title).setSecondaryText(step.description).setFocalColour(context.getResources().getColor(R.color.white)).setBackgroundColour(context.getResources().getColor(R.color.accent_green)).setPromptBackground(new RectanglePromptBackground()).setPromptStateChangeListener((prompt, state) -> {
            if (state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FINISHING) {
                currentStep++;
                showNextStep();
            }
        });

        promptBuilder.show();
    }

    /**
     * Показывает специальную, неблокирующую подсказку (для ListView).
     */
    private void showNonBlockingHint(TutorialStep step) {
        FrameLayout rootView = step.targetView.getRootView().findViewById(android.R.id.content);

        CardView hintCard = new CardView(context);
        hintCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        hintCard.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        hintCard.setRadius(10);
        hintCard.setCardElevation(10);
        hintCard.setAlpha(0); // Начнем с прозрачности для анимации

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(32, 32, 32, 32);

        TextView titleTextView = new TextView(context);
        titleTextView.setText(step.title);
        titleTextView.setTextColor(Color.WHITE);
        titleTextView.setTextSize(20);
        titleTextView.setGravity(Gravity.CENTER);

        TextView descriptionTextView = new TextView(context);
        descriptionTextView.setText(step.description);
        descriptionTextView.setTextColor(Color.WHITE);
        descriptionTextView.setTextSize(16);
        descriptionTextView.setGravity(Gravity.CENTER);

        layout.addView(titleTextView);
        layout.addView(descriptionTextView);
        hintCard.addView(layout);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.bottomMargin = (int) (40 * context.getResources().getDisplayMetrics().density); // 40dp от низа

        rootView.addView(hintCard, layoutParams);

        // Анимация появления
        hintCard.animate().alpha(1).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        // Автоматическое скрытие через 10 секунд
        handler.postDelayed(() -> {
            hintCard.animate().alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rootView.removeView(hintCard);
                    currentStep++;
                    showNextStep();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }, TimeUnit.SECONDS.toMillis(10));
    }

    private static class TutorialStep {
        final View targetView;
        final String description;
        final String title;
        final int order;
        final boolean isNonBlocking;

        TutorialStep(View targetView, String description, String title, int order, boolean isNonBlocking) {
            this.targetView = targetView;
            this.description = description;
            this.title = title;
            this.order = order;
            this.isNonBlocking = isNonBlocking;
        }
    }
}
