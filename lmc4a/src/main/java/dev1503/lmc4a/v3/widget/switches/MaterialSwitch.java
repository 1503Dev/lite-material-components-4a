package dev1503.lmc4a.v3.widget.switches;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialSwitch extends CompoundButton {

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float TRACK_WIDTH_DP = 54.0f;
    private static final float TRACK_HEIGHT_DP = 32.0f;
    private static final float TRACK_OUTLINE_DP = 2.0f;
    private static final float HANDLE_CHECKED_DP = 24.0f;
    private static final float HANDLE_UNCHECKED_DP = 16.0f;
    private static final float HANDLE_UNCHECKED_CENTER_DP = 16.0f;
    private static final float HANDLE_CHECKED_CENTER_DP = 16f;
    private static final float HANDLE_TRAVEL_DURATION_MS = 100.0f;
    private static final float PRESS_EXTRA_DP = 4.0f;
    private static final float PRESS_DURATION_MS = 66.67f;
    private static final float DISABLED_TRACK_ALPHA = 0.12f;
    private static final float DISABLED_HANDLE_ALPHA = 0.38f;
    private static final float ICON_SIZE_DP = 16.0f;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private float trackWidth;
    private float trackHeight;
    private float trackRadius;
    private float minViewWidth;
    private float minViewHeight;
    private float handleProgress;
    private float pressProgress;
    private int currentTrackColor;
    private int currentHandleColor;
    private ValueAnimator handleAnimator;
    private ValueAnimator pressAnimator;
    private ValueAnimator colorAnimator;

    private boolean hasThumbColor;
    private boolean hasUnselectedThumbColor;
    private boolean hasTrackColor;
    private boolean hasUnselectedTrackColor;
    private boolean hasTrackOutlineColor;
    private boolean hasIconColor;
    private boolean hasTrackWidthDp;
    private boolean hasTrackHeightDp;
    private boolean hasThumbRadiusDp;
    private int thumbColor;
    private int unselectedThumbColor;
    private int trackColor;
    private int unselectedTrackColor;
    private int trackOutlineColor;
    private int iconColor;
    private float trackWidthDp;
    private float trackHeightDp;
    private float thumbRadiusDp;
    private Icon icon;
    private Drawable iconDrawable;

    public MaterialSwitch(Context context) {
        this(context, null);
    }

    public MaterialSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.switchStyle);
    }

    public MaterialSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        trackWidth = dp(TRACK_WIDTH_DP);
        trackHeight = dp(TRACK_HEIGHT_DP);
        trackRadius = trackHeight / 2.0f;
        minViewWidth = dp(TRACK_WIDTH_DP + 8.0f);
        minViewHeight = trackHeight;
        setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        setPadding((int) (minViewWidth + 0.5f), 0, 0, 0);
        setBackground(null);
        setButtonDrawable(null);
        setClickable(true);
        setTextColor(resolveTextColor());
        currentTrackColor = resolveTrackColor();
        currentHandleColor = resolveHandleColor();
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        hasThumbColor = false;
        hasUnselectedThumbColor = false;
        hasTrackColor = false;
        hasUnselectedTrackColor = false;
        hasTrackOutlineColor = false;
        hasIconColor = false;
        hasTrackWidthDp = false;
        hasTrackHeightDp = false;
        hasThumbRadiusDp = false;
        trackWidth = dp(TRACK_WIDTH_DP);
        trackHeight = dp(TRACK_HEIGHT_DP);
        updateTrackMetrics();
        setTextColor(resolveTextColor());
        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }
        currentTrackColor = resolveTrackColor();
        currentHandleColor = resolveHandleColor();
        requestLayout();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
        this.hasThumbColor = true;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public int getThumbColor() {
        return hasThumbColor ? thumbColor : dynamicColors.onPrimary().getArgb(colorScheme);
    }

    public boolean hasThumbColor() {
        return hasThumbColor;
    }

    public void clearThumbColor() {
        hasThumbColor = false;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public void setUnselectedThumbColor(int unselectedThumbColor) {
        this.unselectedThumbColor = unselectedThumbColor;
        this.hasUnselectedThumbColor = true;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public int getUnselectedThumbColor() {
        return hasUnselectedThumbColor
                ? unselectedThumbColor
                : dynamicColors.outline().getArgb(colorScheme);
    }

    public boolean hasUnselectedThumbColor() {
        return hasUnselectedThumbColor;
    }

    public void clearUnselectedThumbColor() {
        hasUnselectedThumbColor = false;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
        this.hasTrackColor = true;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public int getTrackColor() {
        return hasTrackColor ? trackColor : dynamicColors.primary().getArgb(colorScheme);
    }

    public boolean hasTrackColor() {
        return hasTrackColor;
    }

    public void clearTrackColor() {
        hasTrackColor = false;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public void setUnselectedTrackColor(int unselectedTrackColor) {
        this.unselectedTrackColor = unselectedTrackColor;
        this.hasUnselectedTrackColor = true;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public int getUnselectedTrackColor() {
        return hasUnselectedTrackColor
                ? unselectedTrackColor
                : dynamicColors.surfaceContainerHighest().getArgb(colorScheme);
    }

    public boolean hasUnselectedTrackColor() {
        return hasUnselectedTrackColor;
    }

    public void clearUnselectedTrackColor() {
        hasUnselectedTrackColor = false;
        animateColorTransition(currentTrackColor, currentHandleColor);
    }

    public void setTrackOutlineColor(int trackOutlineColor) {
        this.trackOutlineColor = trackOutlineColor;
        this.hasTrackOutlineColor = true;
        invalidate();
    }

    public int getTrackOutlineColor() {
        return hasTrackOutlineColor
                ? trackOutlineColor
                : dynamicColors.outline().getArgb(colorScheme);
    }

    public boolean hasTrackOutlineColor() {
        return hasTrackOutlineColor;
    }

    public void clearTrackOutlineColor() {
        hasTrackOutlineColor = false;
        invalidate();
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        iconDrawable = icon == null ? null : icon.resolve(getContext());
        if (iconDrawable != null) {
            iconDrawable = iconDrawable.mutate();
        }
        invalidate();
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean hasIcon() {
        return icon != null;
    }

    public void clearIcon() {
        this.icon = null;
        iconDrawable = null;
        invalidate();
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
        this.hasIconColor = true;
        invalidate();
    }

    public int getIconColor() {
        return hasIconColor
                ? iconColor
                : dynamicColors.onPrimaryContainer().getArgb(colorScheme);
    }

    public boolean hasIconColor() {
        return hasIconColor;
    }

    public void clearIconColor() {
        hasIconColor = false;
        invalidate();
    }

    public void setTrackWidthDp(float trackWidthDp) {
        this.trackWidthDp = trackWidthDp;
        this.hasTrackWidthDp = true;
        trackWidth = dp(trackWidthDp);
        updateTrackMetrics();
        requestLayout();
        invalidate();
    }

    public float getTrackWidthDp() {
        return trackWidth / getResources().getDisplayMetrics().density;
    }

    public boolean hasTrackWidthDp() {
        return hasTrackWidthDp;
    }

    public void clearTrackWidthDp() {
        hasTrackWidthDp = false;
        trackWidth = dp(TRACK_WIDTH_DP);
        updateTrackMetrics();
        requestLayout();
        invalidate();
    }

    public void setTrackHeightDp(float trackHeightDp) {
        this.trackHeightDp = trackHeightDp;
        this.hasTrackHeightDp = true;
        trackHeight = dp(trackHeightDp);
        updateTrackMetrics();
        requestLayout();
        invalidate();
    }

    public float getTrackHeightDp() {
        return trackHeight / getResources().getDisplayMetrics().density;
    }

    public boolean hasTrackHeightDp() {
        return hasTrackHeightDp;
    }

    public void clearTrackHeightDp() {
        hasTrackHeightDp = false;
        trackHeight = dp(TRACK_HEIGHT_DP);
        updateTrackMetrics();
        requestLayout();
        invalidate();
    }

    public void setThumbRadiusDp(float thumbRadiusDp) {
        this.thumbRadiusDp = thumbRadiusDp;
        this.hasThumbRadiusDp = true;
        invalidate();
    }

    public float getThumbRadiusDp() {
        if (hasThumbRadiusDp) {
            return thumbRadiusDp;
        }
        return resolveBaseRadius() / getResources().getDisplayMetrics().density;
    }

    public boolean hasThumbRadiusDp() {
        return hasThumbRadiusDp;
    }

    public void clearThumbRadiusDp() {
        hasThumbRadiusDp = false;
        invalidate();
    }

    @Override
    public void setChecked(boolean checked) {
        if (dynamicColors == null) {
            super.setChecked(checked);
            return;
        }
        boolean changed = checked != isChecked();
        int oldTrackColor = currentTrackColor;
        int oldHandleColor = currentHandleColor;
        super.setChecked(checked);
        if (changed) {
            animateHandleTo(checked, oldTrackColor, oldHandleColor);
        } else {
            handleProgress = checked ? 1.0f : 0.0f;
            currentTrackColor = resolveTrackColor();
            currentHandleColor = resolveHandleColor();
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean changed = enabled != isEnabled();
        super.setEnabled(enabled);
        if (changed && dynamicColors != null) {
            animateColorTransition(currentTrackColor, currentHandleColor);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY) {
            width = Math.max(width, (int) (minViewWidth + 0.5f));
        }
        if (View.MeasureSpec.getMode(heightMeasureSpec) != View.MeasureSpec.EXACTLY) {
            height = Math.max(height, (int) (minViewHeight + 0.5f));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float trackTop = (getHeight() - trackHeight) / 2.0f;
        float handleCenterX = lerp(dp(HANDLE_UNCHECKED_CENTER_DP),
                trackWidth - dp(HANDLE_CHECKED_CENTER_DP), handleProgress);
        float handleRadius = resolveHandleRadius();
        float centerY = trackTop + trackRadius;

        rectF.set(0.0f, trackTop, trackWidth, trackTop + trackHeight);
        paint.setColor(currentTrackColor);
        canvas.drawRoundRect(rectF, trackRadius, trackRadius, paint);
        if (!isChecked()) {
            float strokeWidth = dp(TRACK_OUTLINE_DP);
            float strokeHalf = strokeWidth / 2.0f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            if (isEnabled()) {
                paint.setColor(resolveTrackOutlineColor());
            } else {
                paint.setColor(dynamicColors.outlineVariant().getArgb(colorScheme));
            }
            rectF.set(strokeHalf, trackTop + strokeHalf,
                    trackWidth - strokeHalf, trackTop + trackHeight - strokeHalf);
            canvas.drawRoundRect(rectF, trackRadius - strokeHalf, trackRadius - strokeHalf, paint);
            paint.setStyle(Paint.Style.FILL);
        }
        rectF.set(handleCenterX - handleRadius, centerY - handleRadius,
                handleCenterX + handleRadius, centerY + handleRadius);
        paint.setColor(currentHandleColor);
        canvas.drawRoundRect(rectF, handleRadius, handleRadius, paint);
        drawIcon(canvas, handleCenterX, centerY);
        super.onDraw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        animatePressTo(isPressed() && isEnabled() ? 1.0f : 0.0f);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handleAnimator != null) {
            handleAnimator.cancel();
        }
        if (pressAnimator != null) {
            pressAnimator.cancel();
        }
        if (colorAnimator != null) {
            colorAnimator.cancel();
        }
    }

    private void animatePressTo(float target) {
        if (pressAnimator != null) {
            pressAnimator.cancel();
            pressAnimator = null;
        }
        if (getWindowToken() == null || pressProgress == target) {
            pressProgress = target;
            invalidate();
            return;
        }
        pressAnimator = ValueAnimator.ofFloat(pressProgress, target);
        pressAnimator.setDuration((long) PRESS_DURATION_MS);
        pressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pressProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        pressAnimator.start();
    }

    private void animateHandleTo(boolean checked, int oldTrackColor, int oldHandleColor) {
        float target = checked ? 1.0f : 0.0f;
        if (handleAnimator != null) {
            handleAnimator.cancel();
            handleAnimator = null;
        }
        if (getWindowToken() == null || handleProgress == target) {
            handleProgress = target;
            currentTrackColor = resolveTrackColor();
            currentHandleColor = resolveHandleColor();
            invalidate();
            return;
        }
        handleAnimator = ValueAnimator.ofFloat(handleProgress, target);
        handleAnimator.setDuration((long) HANDLE_TRAVEL_DURATION_MS);
        handleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                handleProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        handleAnimator.start();
        animateColorTransition(oldTrackColor, oldHandleColor);
    }

    private void animateColorTransition(int startTrack, int startHandle) {
        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }
        final int endTrack = resolveTrackColor();
        final int endHandle = resolveHandleColor();
        currentTrackColor = startTrack;
        currentHandleColor = startHandle;
        if (startTrack == endTrack && startHandle == endHandle) {
            currentTrackColor = endTrack;
            currentHandleColor = endHandle;
            return;
        }
        colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration((long) HANDLE_TRAVEL_DURATION_MS);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                currentTrackColor = (int) new ArgbEvaluator().evaluate(fraction, startTrack, endTrack);
                currentHandleColor = (int) new ArgbEvaluator().evaluate(fraction, startHandle, endHandle);
                invalidate();
            }
        });
        colorAnimator.start();
    }

    private void drawIcon(Canvas canvas, float centerX, float centerY) {
        if (iconDrawable == null || !isChecked()) {
            return;
        }
        float size = dp(ICON_SIZE_DP);
        float half = size / 2.0f;
        iconDrawable.setBounds((int) (centerX - half + 0.5f), (int) (centerY - half + 0.5f),
                (int) (centerX + half + 0.5f), (int) (centerY + half + 0.5f));
        iconDrawable.setColorFilter(resolveIconColor(), PorterDuff.Mode.SRC_IN);
        iconDrawable.draw(canvas);
    }

    private void updateTrackMetrics() {
        trackRadius = trackHeight / 2.0f;
        minViewWidth = trackWidth + dp(8.0f);
        minViewHeight = trackHeight;
        setPadding((int) (minViewWidth + 0.5f), 0, 0, 0);
    }

    private float resolveBaseRadius() {
        if (hasThumbRadiusDp) {
            return Math.max(dp(thumbRadiusDp), dp(HANDLE_UNCHECKED_DP) / 2.0f);
        }
        float baseDiameterDp = lerp(HANDLE_UNCHECKED_DP, HANDLE_CHECKED_DP, handleProgress);
        return dp(baseDiameterDp) / 2.0f;
    }

    private float resolveHandleRadius() {
        float baseDiameter = resolveBaseRadius() * 2.0f;
        float pressedDiameter = lerp(baseDiameter, dp(HANDLE_CHECKED_DP + PRESS_EXTRA_DP), pressProgress);
        return Math.max(baseDiameter, pressedDiameter) / 2.0f;
    }

    private int resolveTrackColor() {
        if (!isEnabled()) {
            if (isChecked()) {
                return applyAlphaFraction(getTrackColor(), DISABLED_TRACK_ALPHA);
            }
            return applyAlphaFraction(
                    dynamicColors.surfaceContainerHighest().getArgb(colorScheme), DISABLED_TRACK_ALPHA);
        }
        return isChecked() ? getTrackColor() : getUnselectedTrackColor();
    }

    private int resolveHandleColor() {
        if (!isEnabled()) {
            if (isChecked()) {
                return applyAlphaFraction(getThumbColor(), DISABLED_HANDLE_ALPHA);
            }
            return applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), DISABLED_HANDLE_ALPHA);
        }
        return isChecked() ? getThumbColor() : getUnselectedThumbColor();
    }

    private int resolveTrackOutlineColor() {
        return hasTrackOutlineColor
                ? trackOutlineColor
                : dynamicColors.outline().getArgb(colorScheme);
    }

    private int resolveIconColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(getIconColor(), DISABLED_HANDLE_ALPHA);
        }
        return getIconColor();
    }

    private int resolveTextColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), DISABLED_HANDLE_ALPHA);
        }
        return dynamicColors.onSurface().getArgb(colorScheme);
    }

    private static float lerp(float start, float end, float fraction) {
        return start + (end - start) * fraction;
    }

    private static int applyAlphaFraction(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * alphaFraction);
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
