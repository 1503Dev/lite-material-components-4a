package dev1503.lmc4a.v3.widget.progressindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class BaseProgressIndicator extends ProgressBar {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float DEFAULT_TRACK_THICKNESS_DP = 4.0f;
    private static final float DISABLED_ALPHA = 0.38f;
    private static final long INDETERMINATE_PERIOD_MS = 1800L;
    private static final long VISIBILITY_DURATION_MS = 200L;

    protected final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    protected DynamicScheme colorScheme = publicColorScheme;

    private int activeIndicatorColor;
    private boolean hasActiveIndicatorColor;
    private int trackColor;
    private boolean hasTrackColor;
    private int stopIndicatorColor;
    private boolean hasStopIndicatorColor;
    private float trackThicknessDp = DEFAULT_TRACK_THICKNESS_DP;
    private boolean stopIndicatorEnabled = true;
    private float indicatorPhase;
    private ValueAnimator phaseAnimator;
    private ValueAnimator visibilityAnimator;

    public BaseProgressIndicator(Context context) {
        this(context, null);
    }

    public BaseProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyleHorizontal);
    }

    public BaseProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        setProgressDrawable(createEmptyDrawable());
        setIndeterminateDrawable(createEmptyDrawable());
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        hasActiveIndicatorColor = false;
        hasTrackColor = false;
        hasStopIndicatorColor = false;
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setActiveIndicatorColor(int color) {
        activeIndicatorColor = color;
        hasActiveIndicatorColor = true;
        invalidate();
    }

    public int getActiveIndicatorColor() {
        return resolveActiveIndicatorColor();
    }

    public boolean hasActiveIndicatorColor() {
        return hasActiveIndicatorColor;
    }

    public void clearActiveIndicatorColor() {
        hasActiveIndicatorColor = false;
        invalidate();
    }

    public void setTrackColor(int color) {
        trackColor = color;
        hasTrackColor = true;
        invalidate();
    }

    public int getTrackColor() {
        return resolveTrackColor();
    }

    public boolean hasTrackColor() {
        return hasTrackColor;
    }

    public void clearTrackColor() {
        hasTrackColor = false;
        invalidate();
    }

    public void setStopIndicatorColor(int color) {
        stopIndicatorColor = color;
        hasStopIndicatorColor = true;
        invalidate();
    }

    public int getStopIndicatorColor() {
        return resolveStopIndicatorColor();
    }

    public boolean hasStopIndicatorColor() {
        return hasStopIndicatorColor;
    }

    public void clearStopIndicatorColor() {
        hasStopIndicatorColor = false;
        invalidate();
    }

    public void setTrackThicknessDp(float trackThicknessDp) {
        this.trackThicknessDp = Math.max(0.0f, trackThicknessDp);
        requestLayout();
        invalidate();
    }

    public float getTrackThicknessDp() {
        return trackThicknessDp;
    }

    public void clearTrackThicknessDp() {
        setTrackThicknessDp(DEFAULT_TRACK_THICKNESS_DP);
    }

    public void setStopIndicatorEnabled(boolean stopIndicatorEnabled) {
        if (this.stopIndicatorEnabled != stopIndicatorEnabled) {
            this.stopIndicatorEnabled = stopIndicatorEnabled;
            invalidate();
        }
    }

    public boolean isStopIndicatorEnabled() {
        return stopIndicatorEnabled;
    }

    public void show() {
        if (getVisibility() != VISIBLE) {
            setAlpha(0.0f);
            setVisibility(VISIBLE);
        }
        animateAlpha(1.0f);
        updatePhaseAnimation();
    }

    public void hide() {
        animateAlpha(0.0f);
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(indeterminate);
        updatePhaseAnimation();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredHeight = (int) (getIndicatorHeightPx() + 0.5f);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resolvedHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resolvedHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST && heightSize > 0) {
            resolvedHeight = Math.min(desiredHeight, heightSize);
        } else {
            resolvedHeight = desiredHeight;
        }
        setMeasuredDimension(getMeasuredWidth(), Math.max(resolvedHeight, 1));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updatePhaseAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPhaseAnimation();
        if (visibilityAnimator != null) {
            ValueAnimator animator = visibilityAnimator;
            visibilityAnimator = null;
            animator.cancel();
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        updatePhaseAnimation();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        updatePhaseAnimation();
    }

    protected float getIndicatorHeightPx() {
        return getTrackThicknessPx();
    }

    protected float getTrackThicknessPx() {
        return dp(trackThicknessDp);
    }

    protected float getProgressFraction() {
        int max = getMax();
        if (max <= 0) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, (float) getProgress() / max));
    }

    protected float getIndicatorPhase() {
        return indicatorPhase;
    }

    protected boolean isIndicatorAnimating() {
        return phaseAnimator != null;
    }

    protected int resolveActiveIndicatorColor() {
        if (hasActiveIndicatorColor) {
            return activeIndicatorColor;
        }
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        return dynamicColors.primary().getArgb(colorScheme);
    }

    protected int resolveTrackColor() {
        if (hasTrackColor) {
            return trackColor;
        }
        return dynamicColors.surfaceContainerHighest().getArgb(colorScheme);
    }

    protected int resolveStopIndicatorColor() {
        if (hasStopIndicatorColor) {
            return stopIndicatorColor;
        }
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        return dynamicColors.primary().getArgb(colorScheme);
    }

    protected static int applyAlphaFraction(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0f, Math.min(1f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    protected float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }

    private void animateAlpha(final float target) {
        if (visibilityAnimator != null) {
            ValueAnimator animator = visibilityAnimator;
            visibilityAnimator = null;
            animator.cancel();
        }
        float current = getAlpha();
        if (current == target || getWindowToken() == null) {
            setAlpha(target);
            applyTargetVisibility(target);
            return;
        }
        visibilityAnimator = ValueAnimator.ofFloat(current, target);
        visibilityAnimator.setDuration(VISIBILITY_DURATION_MS);
        visibilityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setAlpha((Float) animation.getAnimatedValue());
            }
        });
        visibilityAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (visibilityAnimator == animation) {
                    visibilityAnimator = null;
                    applyTargetVisibility(target);
                }
            }
        });
        visibilityAnimator.start();
    }

    private void applyTargetVisibility(float target) {
        setAlpha(target);
        if (target <= 0.0f) {
            stopPhaseAnimation();
            setVisibility(INVISIBLE);
        } else {
            setVisibility(VISIBLE);
            updatePhaseAnimation();
        }
    }

    private void updatePhaseAnimation() {
        boolean shouldAnimate = isIndeterminate()
                && getWindowToken() != null
                && getVisibility() == VISIBLE
                && getWindowVisibility() == VISIBLE
                && getAlpha() > 0.0f;
        if (shouldAnimate) {
            startPhaseAnimation();
        } else {
            stopPhaseAnimation();
        }
    }

    private void startPhaseAnimation() {
        if (phaseAnimator != null) {
            return;
        }
        indicatorPhase = 0.0f;
        phaseAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        phaseAnimator.setDuration(INDETERMINATE_PERIOD_MS);
        phaseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        phaseAnimator.setInterpolator(new LinearInterpolator());
        phaseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indicatorPhase = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        phaseAnimator.start();
    }

    private void stopPhaseAnimation() {
        if (phaseAnimator != null) {
            ValueAnimator animator = phaseAnimator;
            phaseAnimator = null;
            animator.cancel();
        }
    }

    private static Drawable createEmptyDrawable() {
        return new Drawable() {
            @Override
            public void draw(Canvas canvas) {
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }

            @Override
            public int getIntrinsicWidth() {
                return 0;
            }

            @Override
            public int getIntrinsicHeight() {
                return 0;
            }
        };
    }
}
