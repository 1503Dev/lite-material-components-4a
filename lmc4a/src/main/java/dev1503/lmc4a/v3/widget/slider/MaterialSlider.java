package dev1503.lmc4a.v3.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.SeekBar;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialSlider extends SeekBar {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float DEFAULT_HEIGHT_DP = 48.0f;
    private static final float TRACK_HEIGHT_DP = 4.0f;
    private static final float THUMB_SIZE_DP = 20.0f;
    private static final float THUMB_ELEVATION_DP = 2.0f;
    private static final float POPUP_GAP_DP = 4.0f;

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
    private int progressColor;
    private int trackColor;
    private int thumbColor;
    private int disabledTrackColor;
    private int disabledThumbColor;

    private SliderPopup popup;
    private boolean isTouching = false;
    private boolean isPressed2 = false;
    private int thumbCenterX;
    private int trackTop;
    private int trackBottom;
    private int trackLeft;
    private int trackRight;
    private RippleDrawable rippleDrawable;

    public MaterialSlider(Context context) {
        this(context, null);
    }

    public MaterialSlider(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public MaterialSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            OvalShape maskShape = new OvalShape();
            ShapeDrawable maskDrawable = new ShapeDrawable(maskShape);
            rippleDrawable = new RippleDrawable(
                    android.content.res.ColorStateList.valueOf(Color.WHITE),
                    null, maskDrawable);
        }
        setMin(0f);
        setMax(100f);
        setStep(0f);
        resolveColors();
        setThumb(null);
        setProgressDrawable(null);
        setMinimumHeight((int) (dp(DEFAULT_HEIGHT_DP) + 0.5f));
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
        setProgress(getProgress());
    }

    public float getMinValue() {
        return min;
    }

    @Override
    public void setMax(int max) {
        this.max = max;
        super.setMax((int) ((max - min) / (step > 0 ? step : 1)));
    }

    public void setMax(float max) {
        this.max = max;
        super.setMax((int) ((max - min) / (step > 0 ? step : 1)));
    }

    public float getMaxValue() {
        return max;
    }

    public void setStep(float step) {
        this.step = step;
        super.setMax((int) ((max - min) / (step > 0 ? step : 1)));
    }

    public float getStep() {
        return step;
    }

    public float getProgressValue() {
        if (step > 0) {
            return min + getProgress() * step;
        }
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        return min + fraction * (max - min);
    }

    public void setProgressValue(float value) {
        float clamped = Math.max(min, Math.min(max, value));
        int progress;
        if (step > 0) {
            progress = (int) ((clamped - min) / step);
        } else {
            progress = (int) ((clamped - min) / (max - min) * getMax());
        }
        setProgress(progress);
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        if (popup != null && popup.isShowing()) {
            popup.update(getThumbCenterX(), formatProgress(getProgressValue()));
        }
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
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        thumbCenterX = (int) (trackLeft + fraction * (trackRight - trackLeft));

        drawTrack(canvas, enabled, fraction);
        drawThumb(canvas, enabled);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && enabled && isPressed2 && rippleDrawable != null) {
            float centerX = thumbCenterX;
            float centerY = trackTop + dp(TRACK_HEIGHT_DP) / 2.0f;
            float rippleRadius = dp(24);
            rippleDrawable.setBounds((int) (centerX - rippleRadius), (int) (centerY - rippleRadius),
                    (int) (centerX + rippleRadius), (int) (centerY + rippleRadius));
            rippleDrawable.setHotspot(centerX, centerY);
            rippleDrawable.setState(getDrawableState());
            rippleDrawable.draw(canvas);
        }
    }

    private void drawTrack(Canvas canvas, boolean enabled, float fraction) {
        float trackHeight = dp(TRACK_HEIGHT_DP);
        float trackRadius = trackHeight / 2.0f;

        trackRect.set(trackLeft, trackTop, trackRight, trackBottom);
        trackPaint.setColor(enabled ? trackColor : trackColor);
        canvas.drawRoundRect(trackRect, trackRadius, trackRadius, trackPaint);

        if (fraction > 0) {
            trackRect.set(trackLeft, trackTop, thumbCenterX, trackBottom);
            trackPaint.setColor(enabled ? progressColor : disabledTrackColor);
            canvas.drawRoundRect(trackRect, trackRadius, trackRadius, trackPaint);
        }
    }

    private void drawThumb(Canvas canvas, boolean enabled) {
        float thumbSize = dp(THUMB_SIZE_DP);
        float thumbRadius = thumbSize / 2.0f;
        float centerX = thumbCenterX;
        float centerY = trackTop + dp(TRACK_HEIGHT_DP) / 2.0f;

        thumbShadowPaint.setColor(Color.argb(60, 0, 0, 0));
        thumbShadowPaint.setShadowLayer(dp(2), 0, dp(1), Color.argb(60, 0, 0, 0));
        thumbRect.set(centerX - thumbRadius, centerY - thumbRadius,
                centerX + thumbRadius, centerY + thumbRadius);
        canvas.drawRoundRect(thumbRect, thumbRadius, thumbRadius, thumbShadowPaint);

        thumbPaint.setColor(enabled ? thumbColor : disabledThumbColor);
        thumbRect.set(centerX - thumbRadius, centerY - thumbRadius,
                centerX + thumbRadius, centerY + thumbRadius);
        canvas.drawRoundRect(thumbRect, thumbRadius, thumbRadius, thumbPaint);

        if (enabled && isPressed2) {
            float rippleRadius = dp(24);
            thumbPaint.setColor(applyAlphaFraction(thumbColor, 0.12f));
            canvas.drawCircle(centerX, centerY, rippleRadius, thumbPaint);
        }
    }

    private int getThumbCenterX() {
        return thumbCenterX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && rippleDrawable != null) {
            float centerX = thumbCenterX;
            float centerY = trackTop + dp(TRACK_HEIGHT_DP) / 2.0f;
            rippleDrawable.setHotspot(event.getAction() == MotionEvent.ACTION_DOWN ? event.getX() : centerX, centerY);
        }
        boolean result = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                isPressed2 = true;
                setProgressFromTouch(event.getX());
                showPopup();
                break;
            case MotionEvent.ACTION_MOVE:
                if (popup != null && popup.isShowing()) {
                    int cx = getThumbXFromProgress();
                    popup.update(cx, formatProgress(getProgressValue()));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                isPressed2 = false;
                hidePopup();
                break;
        }
        return result;
    }

    private void setProgressFromTouch(float touchX) {
        float fraction = (touchX - trackLeft) / (float) (trackRight - trackLeft);
        fraction = Math.max(0f, Math.min(1f, fraction));
        int progress = (int) (fraction * getMax() + 0.5f);
        setProgress(progress);
        invalidate();
    }

    private void showPopup() {
        if (popup == null) {
            popup = new SliderPopup(getContext());
            popup.setBackgroundColor(
                    dynamicColors.primary().getArgb(colorScheme));
            popup.setTextColor(
                    dynamicColors.onPrimary().getArgb(colorScheme));
        }
        int cx = getThumbXFromProgress();
        int cy = trackTop + (int) (dp(TRACK_HEIGHT_DP) / 2f);
        popup.show(this, cx, cy, formatProgress(getProgressValue()));
    }

    private int getThumbXFromProgress() {
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        return (int) (trackLeft + fraction * (trackRight - trackLeft));
    }

    private void hidePopup() {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hidePopup();
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
