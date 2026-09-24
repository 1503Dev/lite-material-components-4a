package dev1503.lmc4a.v3.widget.slider;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import dev1503.lmc4a.v3.anim.SpringSimulation;

class SliderPopup {

    private final SliderPopupView popupView;
    private PopupWindow popupWindow;
    private int backgroundColor = Color.BLACK;
    private int textColor = Color.WHITE;
    private float thumbRadiusDp = 10.0f;
    private int anchorScreenX;
    private int anchorScreenY;
    private int thumbOffsetY;
    private ValueAnimator springAnimator;
    private long lastFrameTime;

    private static final float IN_STIFFNESS = 400f;
    private static final float IN_DAMPING = 0.7f;

    SliderPopup(android.content.Context context) {
        this.popupView = new SliderPopupView(context);
    }

    void setValueIndicatorColor(int color) {
        setBackgroundColor(color);
    }

    void setThumbRadiusDp(float radiusDp) {
        this.thumbRadiusDp = radiusDp;
    }

    void setBackgroundColor(int color) {
        this.backgroundColor = color;
        popupView.setBackgroundColor2(color);
    }

    void setTextColor(int color) {
        this.textColor = color;
        popupView.setTextColor2(color);
    }

    void show(View anchor, int thumbCenterX, int thumbCenterOffsetY, String text) {
        if (springAnimator != null && springAnimator.isRunning()) {
            springAnimator.cancel();
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            try {
                popupWindow.dismiss();
            } catch (Exception e) {
                // ignore
            }
            popupWindow = null;
        }

        popupView.setProgressText(text);
        popupView.setBackgroundColor2(backgroundColor);
        popupView.setTextColor2(textColor);
        this.thumbOffsetY = thumbCenterOffsetY;

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        anchorScreenX = location[0];
        anchorScreenY = location[1];

        if (popupWindow == null) {
            popupWindow = new PopupWindow(popupView,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, false);
            popupWindow.setClippingEnabled(true);
            popupWindow.setAnimationStyle(0);
        }

        popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int popupWidth = popupView.getMeasuredWidth();
        int popupHeight = popupView.getMeasuredHeight();

        int x = anchorScreenX + thumbCenterX - popupWidth / 2;
        int thumbRadius = (int) (anchor.getResources().getDisplayMetrics().density * thumbRadiusDp + 0.5f);
        int gap = (int) (anchor.getResources().getDisplayMetrics().density * 4 + 0.5f);
        int y = anchorScreenY + thumbOffsetY - thumbRadius - popupHeight - gap;

        try {
            popupWindow.showAtLocation(anchor, Gravity.TOP | Gravity.START, x, y);
        } catch (Exception e) {
            return;
        }

        animateIn(popupWidth, popupHeight);
    }

    void update(int thumbCenterX, String text) {
        if (popupWindow == null || !popupWindow.isShowing()) {
            return;
        }
        popupView.setProgressText(text);

        popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int popupWidth = popupView.getMeasuredWidth();
        int popupHeight = popupView.getMeasuredHeight();

        int x = anchorScreenX + thumbCenterX - popupWidth / 2;
        int thumbRadius = (int) (popupView.getResources().getDisplayMetrics().density * thumbRadiusDp + 0.5f);
        int gap = (int) (popupView.getResources().getDisplayMetrics().density * 4 + 0.5f);
        int y = anchorScreenY + thumbOffsetY - thumbRadius - popupHeight - gap;

        try {
            popupWindow.update(x, y, popupWidth, popupHeight);
        } catch (Exception e) {
            // ignore
        }
    }

    void dismiss() {
        if (popupWindow == null || !popupWindow.isShowing()) {
            return;
        }
        animateOut(new Runnable() {
            @Override
            public void run() {
                try {
                    popupWindow.dismiss();
                } catch (Exception e) {
                    // ignore
                }
            }
        });
    }

    boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }

    private void animateIn(final int width, final int height) {
        if (springAnimator != null) {
            springAnimator.cancel();
        }

        popupView.setPivotX(width / 2f);
        popupView.setPivotY((float) height);
        popupView.setScaleX(0f);
        popupView.setScaleY(0f);

        final SpringSimulation spring = new SpringSimulation(IN_STIFFNESS, IN_DAMPING);
        spring.setPosition(0f);
        spring.setTarget(1f);

        lastFrameTime = System.nanoTime();
        springAnimator = ValueAnimator.ofFloat(0f, 1f);
        springAnimator.setDuration(1000);
        springAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                delta = Math.min(delta, 0.05f);

                float value = spring.update(delta);
                popupView.setScaleX(value);
                popupView.setScaleY(value);

                if (spring.isAtRest()) {
                    animation.cancel();
                }
            }
        });
        springAnimator.start();
    }

    private void animateOut(final Runnable onEnd) {
        if (springAnimator != null) {
            springAnimator.cancel();
        }

        lastFrameTime = System.nanoTime();
        springAnimator = ValueAnimator.ofFloat(0f, 1f);
        springAnimator.setDuration(200);
        springAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = animation.getAnimatedFraction();
                float easedT = t * t;
                float value = 1f - easedT;
                popupView.setScaleX(value);
                popupView.setScaleY(value);
            }
        });
        springAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        });
        springAnimator.start();
    }
}
