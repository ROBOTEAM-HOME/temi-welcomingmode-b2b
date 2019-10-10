package com.robotemi.welcomingbtob.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import timber.log.Timber;

public class AnimationUtils {

    private static final float ALPHA_30 = 0.3f;

    private static final float ALPHA_60 = 0.6f;

    private static final int ALPHA_FULL = 1;

    private static final int ALPHA_DURATION = 300;

    private static final int PULSE_DURATION = 300;

    private static final int START_DELAY = 500;

    public static Animator animate(View view, int animationRes, AnimatorListenerAdapter listenerAdapter) {
        Animator animator = loadItemSlideAnimation(view, animationRes,0);
        animator.addListener(listenerAdapter);
        animator.start();
        return animator;
    }

    public static void animateFadeOut(View view, AnimatorListenerAdapter listenerAdapter){
        view.animate().alpha(0).setListener(listenerAdapter);
    }

    public static void animateFadeIn(View view, AnimatorListenerAdapter listenerAdapter){
        view.animate().alpha(1).setListener(listenerAdapter);
    }

    public static void animateGraduallySlide(ViewGroup viewgroup, int animationRes, long interval, AnimatorListenerAdapter listenerAdapter) {
        final AnimatorSet animatorSet = new AnimatorSet();
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            animatorSet.play(loadItemSlideAnimation(viewgroup.getChildAt(i),  animationRes, (int) (i * interval)));
        }
        animatorSet.addListener(listenerAdapter);
        animatorSet.start();
    }

    public static void animateTextHighlight(AppCompatTextView text, int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(ALPHA_DURATION);
        colorAnimation.addUpdateListener(animator -> text.setTextColor((Integer)animator.getAnimatedValue()));
        colorAnimation.start();
    }

    public static void animateViewAlpha(View view, boolean highlighted) {
        float alphaValue;
        if (highlighted) {
            alphaValue = ALPHA_FULL;
        } else {
            alphaValue = ALPHA_60;
        }

        Timber.d("animate to: "+alphaValue);
        view.animate().alpha(alphaValue).setDuration(ALPHA_DURATION);
    }

    public static void animateViewAlphaEnable(View view, boolean enable) {
        view.animate().alpha(enable ? ALPHA_60 : ALPHA_30).setDuration(ALPHA_DURATION);
    }

    public static void animateViewHighlight(View view, boolean highlighted) {
        float alphaValue = highlighted ? ALPHA_FULL : ALPHA_60;
        view.animate().alpha(alphaValue).setDuration(ALPHA_DURATION);
    }

    public static void animateViewHighlight(boolean highlighted, View... views) {
        for (View view : views) {
            animateViewHighlight(view, highlighted);
        }
    }

    private static Animator loadItemSlideAnimation(View view, int animationRes, int delay) {
        final Animator animation = AnimatorInflater.loadAnimator(view.getContext(), animationRes);
        view.setLayerType(LinearLayout.LAYER_TYPE_HARDWARE, null);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                final AnimatorSet animationSet = (AnimatorSet) animation;
                final View itemView = (View) ((ObjectAnimator) animationSet.getChildAnimations().get(0)).getTarget();
                if (itemView != null) {
                    itemView.setLayerType(LinearLayout.LAYER_TYPE_NONE, null);
                }
            }
        });

        animation.setStartDelay(delay);
        animation.setTarget(view);
        return animation;
    }

    public static void setPulseAnimation(View view) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f));
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.15f),
                PropertyValuesHolder.ofFloat("scaleY", 1.15f));
        ObjectAnimator scaleUpAgain = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.15f),
                PropertyValuesHolder.ofFloat("scaleY", 1.15f));
        scaleDown.setDuration(500);
        scaleUp.setDuration(500);
        scaleDown.setInterpolator(new BounceInterpolator());
        AnimatorSet as = new AnimatorSet();
        as.playSequentially(scaleUp, scaleDown, scaleUpAgain);
        as.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                    as.setStartDelay(START_DELAY);
                    as.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        as.start();
        view.setTag(as);
    }
}
