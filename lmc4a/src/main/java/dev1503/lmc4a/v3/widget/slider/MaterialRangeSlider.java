package dev1503.lmc4a.v3.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialRangeSlider extends View {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float DEFAULT_HEIGHT_DP = 48.0f;
    private static final float TRACK_HEIGHT_DP = 4.0f;
    private static final float THUMB_SIZE_DP = 20.0f;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF trackRect = new RectF();
    private final RectF thumbRect = new RectF();

    private float min = 0f;
    private float max = 100f;
    private float step = 0f;
    private int lowProgress = 0;
    private int highProgress = 100;
    private int progressColor;
    private int trackColor;
    private int thumbColor;
    private int disabledTrackColor;
    private int disabledThumbColor;

    private SliderPopup lowPopup;
    private SliderPopup highPopup;
    private boolean isTouching = false;
    private int activeThumb = -1;
    private int trackTop;
    private int trackBottom;
    private int trackLeft;
    private int trackRight;
    private RippleDrawable lowRippleDrawable;
    private RippleDrawable highRippleDrawable;
    private boolean lowPressed = false;
    private boolean highPressed = false;

    public interface OnRangeChangeListener {
        void onRangeChanged(MaterialRangeSlider slider, int lowProgress, int highProgress);
    }

    private OnRangeChangeListener onRangeChangeListener;

    public void setOnRangeChangeListener(OnRangeChangeListener listener) {
        this.onRangeChangeListener = listener;
    }

    private static final int THUMB_LOW = 0;
    private static final int THUMB_HIGH = 1;
    private static final int THUMB_SIZE_THRESHOLD_DP = 48;

    public MaterialRangeSlider(Context context) {
        this(context, null);
    }

    public MaterialRangeSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialRangeSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMin(0f);
        setMax(100f);
        setStep(0f);
        resolveColors();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            OvalShape lowMask = new OvalShape();
            ShapeDrawable lowMaskDrawable = new ShapeDrawable(lowMask);
            lowRippleDrawable = new RippleDrawable(
                    android.content.res.ColorStateList.valueOf(Color.WHITE),
                    null, lowMaskDrawable);

            OvalShape highMask = new OvalShape();
            ShapeDrawable highMaskDrawable = new ShapeDrawable(highMask);
            highRippleDrawable = new RippleDrawable(
                    android.content.res.ColorStateList.valueOf(Color.WHITE),
                    null, highMaskDrawable);
        }
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        resolveColors();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setMin(float min) {
        this.min = min;
        invalidate();
    }

    public float getMinValue() {
        return min;
    }

    public void setMax(float max) {
        this.max = max;
        invalidate();
    }

    public float getMaxValue() {
        return max;
    }

    public void setStep(float step) {
        this.step = step;
        invalidate();
    }

    public float getStep() {
        return step;
    }

    public void setLowProgress(int progress) {
        this.lowProgress = Math.max(0, Math.min(highProgress, progress));
        invalidate();
        notifyRangeChanged();
    }

    public int getLowProgress() {
        return lowProgress;
    }

    public void setHighProgress(int progress) {
        this.highProgress = Math.max(lowProgress, Math.min(getInternalMax(), progress));
        invalidate();
        notifyRangeChanged();
    }

    public int getHighProgress() {
        return highProgress;
    }

    private void notifyRangeChanged() {
        if (onRangeChangeListener != null) {
            onRangeChangeListener.onRangeChanged(this, lowProgress, highProgress);
        }
    }

    public float getLowValue() {
        if (step > 0) {
            return min + lowProgress * step;
        }
        float fraction = getInternalMax() > 0 ? (float) lowProgress / getInternalMax() : 0f;
        return min + fraction * (max - min);
    }

    public float getHighValue() {
        if (step > 0) {
            return min + highProgress * step;
        }
        float fraction = getInternalMax() > 0 ? (float) highProgress / getInternalMax() : 0f;
        return min + fraction * (max - min);
    }

    public void setLowValue(float value) {
        float clamped = Math.max(min, Math.min(max, value));
        int progress;
        if (step > 0) {
            progress = (int) ((clamped - min) / step);
        } else {
            progress = (int) ((clamped - min) / (max - min) * getInternalMax());
        }
        setLowProgress(progress);
    }

    public void setHighValue(float value) {
        float clamped = Math.max(min, Math.min(max, value));
        int progress;
        if (step > 0) {
            progress = (int) ((clamped - min) / step);
        } else {
            progress = (int) ((clamped - min) / (max - min) * getInternalMax());
        }
        setHighProgress(progress);
    }

    private int getInternalMax() {
        return (int) ((max - min) / (step > 0 ? step : 1));
    }

    private void resolveColors() {
        progressColor = dynamicColors.primary().getArgb(colorScheme);
        trackColor = dynamicColors.surfaceContainerHighest().getArgb(colorScheme);
        thumbColor = dynamicColors.primary().getArgb(colorScheme);
        int outlineVariant = dynamicColors.outlineVariant().getArgb(colorScheme);
        int outline = dynamicColors.outline().getArgb(colorScheme);
        disabledTrackColor = blendColors(outlineVariant, outline, 0.5f);
        disabledThumbColor = disabledTrackColor;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        float inverseRatio = 1f - ratio;
        int a = (int) ((color1 >> 24 & 0xff) * inverseRatio + (color2 >> 24 & 0xff) * ratio);
        int r = (int) ((color1 >> 16 & 0xff) * inverseRatio + (color2 >> 16 & 0xff) * ratio);
        int g = (int) ((color1 >> 8 & 0xff) * inverseRatio + (color2 >> 8 & 0xff) * ratio);
        int b = (int) ((color1 & 0xff) * inverseRatio + (color2 & 0xff) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (dp(DEFAULT_HEIGHT_DP) + 0.5f);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float thumbSize = dp(THUMB_SIZE_DP);
        float halfThumb = thumbSize / 2.0f;
        trackTop = (int) ((h - dp(TRACK_HEIGHT_DP)) / 2);
        trackBottom = trackTop + (int) dp(TRACK_HEIGHT_DP);
        trackLeft = (int) (getPaddingLeft() + halfThumb);
        trackRight = (int) (w - getPaddingRight() - halfThumb);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean enabled = isEnabled();
        int internalMax = getInternalMax();
        float lowFraction = internalMax > 0 ? (float) lowProgress / internalMax : 0f;
        float highFraction = internalMax > 0 ? (float) highProgress / internalMax : 0f;
        int lowCenterX = (int) (trackLeft + lowFraction * (trackRight - trackLeft));
        int highCenterX = (int) (trackLeft + highFraction * (trackRight - trackLeft));
        float centerY = trackTop + dp(TRACK_HEIGHT_DP) / 2.0f;

        drawTrack(canvas, enabled, lowCenterX, highCenterX);
        drawThumb(canvas, enabled, lowCenterX, centerY, lowPressed, lowRippleDrawable);
        drawThumb(canvas, enabled, highCenterX, centerY, highPressed, highRippleDrawable);
    }

    private void drawTrack(Canvas canvas, boolean enabled, int lowCenterX, int highCenterX) {
        float trackHeight = dp(TRACK_HEIGHT_DP);
        float trackRadius = trackHeight / 2.0f;

        trackRect.set(trackLeft, trackTop, trackRight, trackBottom);
        trackPaint.setColor(enabled ? trackColor : trackColor);
        canvas.drawRoundRect(trackRect, trackRadius, trackRadius, trackPaint);

        if (highCenterX > lowCenterX) {
            trackRect.set(lowCenterX, trackTop, highCenterX, trackBottom);
            trackPaint.setColor(enabled ? progressColor : disabledTrackColor);
            canvas.drawRoundRect(trackRect, trackRadius, trackRadius, trackPaint);
        }
    }

    private void drawThumb(Canvas canvas, boolean enabled, float centerX, float centerY,
                           boolean pressed, RippleDrawable ripple) {
        float thumbSize = dp(THUMB_SIZE_DP);
        float thumbRadius = thumbSize / 2.0f;

        thumbShadowPaint.setColor(Color.argb(60, 0, 0, 0));
        thumbShadowPaint.setShadowLayer(dp(2), 0, dp(1), Color.argb(60, 0, 0, 0));
        thumbRect.set(centerX - thumbRadius, centerY - thumbRadius,
                centerX + thumbRadius, centerY + thumbRadius);
        canvas.drawRoundRect(thumbRect, thumbRadius, thumbRadius, thumbShadowPaint);

        thumbPaint.setColor(enabled ? thumbColor : disabledThumbColor);
        thumbRect.set(centerX - thumbRadius, centerY - thumbRadius,
                centerX + thumbRadius, centerY + thumbRadius);
        canvas.drawRoundRect(thumbRect, thumbRadius, thumbRadius, thumbPaint);

        if (enabled && pressed) {
            float rippleRadius = dp(24);
            thumbPaint.setColor(applyAlphaFraction(thumbColor, 0.12f));
            canvas.drawCircle(centerX, centerY, rippleRadius, thumbPaint);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && enabled && pressed && ripple != null) {
            float rippleRadius = dp(24);
            ripple.setBounds((int) (centerX - rippleRadius), (int) (centerY - rippleRadius),
                    (int) (centerX + rippleRadius), (int) (centerY + rippleRadius));
            ripple.setHotspot(centerX, centerY);
            ripple.setState(getDrawableState());
            ripple.draw(canvas);
        }
    }

    private int getLowCenterX() {
        int internalMax = getInternalMax();
        float fraction = internalMax > 0 ? (float) lowProgress / internalMax : 0f;
        return (int) (trackLeft + fraction * (trackRight - trackLeft));
    }

    private int getHighCenterX() {
        int internalMax = getInternalMax();
        float fraction = internalMax > 0 ? (float) highProgress / internalMax : 0f;
        return (int) (trackLeft + fraction * (trackRight - trackLeft));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }

        float touchX = event.getX();
        float touchY = event.getY();
        float centerY = trackTop + dp(TRACK_HEIGHT_DP) / 2.0f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeThumb = pickThumb(touchX, touchY, centerY);
                if (activeThumb == -1) {
                    return false;
                }
                isTouching = true;
                if (activeThumb == THUMB_LOW) {
                    lowPressed = true;
                    setProgressFromTouch(THUMB_LOW, touchX);
                    showPopup(THUMB_LOW);
                } else {
                    highPressed = true;
                    setProgressFromTouch(THUMB_HIGH, touchX);
                    showPopup(THUMB_HIGH);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (activeThumb != -1) {
                    setProgressFromTouch(activeThumb, touchX);
                    updatePopup(activeThumb);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (activeThumb == THUMB_LOW) {
                    lowPressed = false;
                    hidePopup(THUMB_LOW);
                } else if (activeThumb == THUMB_HIGH) {
                    highPressed = false;
                    hidePopup(THUMB_HIGH);
                }
                activeThumb = -1;
                isTouching = false;
                invalidate();
                break;
        }
        return true;
    }

    private int pickThumb(float touchX, float touchY, float centerY) {
        int lowX = getLowCenterX();
        int highX = getHighCenterX();
        float threshold = dp(THUMB_SIZE_THRESHOLD_DP) / 2.0f;

        boolean touchLow = Math.abs(touchX - lowX) <= threshold
                && Math.abs(touchY - centerY) <= threshold;
        boolean touchHigh = Math.abs(touchX - highX) <= threshold
                && Math.abs(touchY - centerY) <= threshold;

        if (touchLow && touchHigh) {
            return Math.abs(touchX - lowX) <= Math.abs(touchX - highX) ? THUMB_LOW : THUMB_HIGH;
        }
        if (touchLow) return THUMB_LOW;
        if (touchHigh) return THUMB_HIGH;

        float distLow = Math.abs(touchX - lowX);
        float distHigh = Math.abs(touchX - highX);
        return distLow <= distHigh ? THUMB_LOW : THUMB_HIGH;
    }

    private void setProgressFromTouch(int thumb, float touchX) {
        float fraction = (touchX - trackLeft) / (float) (trackRight - trackLeft);
        fraction = Math.max(0f, Math.min(1f, fraction));
        int progress = (int) (fraction * getInternalMax() + 0.5f);
        if (thumb == THUMB_LOW) {
            lowProgress = Math.max(0, Math.min(highProgress, progress));
        } else {
            highProgress = Math.max(lowProgress, Math.min(getInternalMax(), progress));
        }
        notifyRangeChanged();
    }

    public String formatProgress(float value) {
        if (step <= 0) {
            return String.valueOf((int) value);
        }
        int decimals = countDecimals(step);
        if (decimals == 0) {
            return String.valueOf((int) value);
        }
        String format = "%." + decimals + "f";
        return String.format(format, value);
    }

    private int countDecimals(float value) {
        String text = String.valueOf(value);
        int dotIndex = text.indexOf('.');
        if (dotIndex < 0) return 0;
        int decimals = 0;
        for (int i = text.length() - 1; i > dotIndex; i--) {
            if (text.charAt(i) != '0') {
                decimals = i - dotIndex;
                break;
            }
        }
        return decimals;
    }

    private void showPopup(int thumb) {
        SliderPopup popup = thumb == THUMB_LOW ? getOrCreateLowPopup() : getOrCreateHighPopup();
        float value = thumb == THUMB_LOW ? getLowValue() : getHighValue();
        int cx = thumb == THUMB_LOW ? getLowCenterX() : getHighCenterX();
        int cy = trackTop + (int) (dp(TRACK_HEIGHT_DP) / 2f);
        popup.show(this, cx, cy, formatProgress(value));
    }

    private void updatePopup(int thumb) {
        SliderPopup popup = thumb == THUMB_LOW ? lowPopup : highPopup;
        if (popup != null && popup.isShowing()) {
            float value = thumb == THUMB_LOW ? getLowValue() : getHighValue();
            int cx = thumb == THUMB_LOW ? getLowCenterX() : getHighCenterX();
            popup.update(cx, formatProgress(value));
        }
    }

    private void hidePopup(int thumb) {
        SliderPopup popup = thumb == THUMB_LOW ? lowPopup : highPopup;
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        }
    }

    private SliderPopup getOrCreateLowPopup() {
        if (lowPopup == null) {
            lowPopup = new SliderPopup(getContext());
            lowPopup.setBackgroundColor(dynamicColors.primary().getArgb(colorScheme));
            lowPopup.setTextColor(dynamicColors.onPrimary().getArgb(colorScheme));
        }
        return lowPopup;
    }

    private SliderPopup getOrCreateHighPopup() {
        if (highPopup == null) {
            highPopup = new SliderPopup(getContext());
            highPopup.setBackgroundColor(dynamicColors.primary().getArgb(colorScheme));
            highPopup.setTextColor(dynamicColors.onPrimary().getArgb(colorScheme));
        }
        return highPopup;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (lowPopup != null && lowPopup.isShowing()) {
            lowPopup.dismiss();
        }
        if (highPopup != null && highPopup.isShowing()) {
            highPopup.dismiss();
        }
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
