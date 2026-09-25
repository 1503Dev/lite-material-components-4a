package dev1503.lmc4a.v3.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;

public class MaterialSlider extends SeekBar {

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    protected final SliderHelper h = new SliderHelper(publicColorScheme);
    private SliderPopup popup;
    private boolean isTouching = false;
    private int thumbCenterX;
    private float lastNotifiedValue;
    private DynamicScheme colorScheme = publicColorScheme;

    private OnValueChangeListener onValueChangeListener;

    public interface OnValueChangeListener {
        void onValueChangeStart(float value);

        void onValueChange(float value);

        void onValueChangeFinished(float value);
    }

    public MaterialSlider(Context context) {
        this(context, null);
    }

    public MaterialSlider(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public MaterialSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        h.min = 0f;
        h.max = 100f;
        h.step = 0f;
        h.resolveColors();
        setThumb(null);
        setProgressDrawable(null);
        setMinimumHeight((int) (SliderHelper.dp(this, SliderHelper.DEFAULT_HEIGHT_DP) + 0.5f));
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        h.colorScheme = this.colorScheme;
        h.clearColorOverrides();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setValueIndicatorEnabled(boolean enabled) {
        h.valueIndicatorEnabled = enabled;
        if (!enabled) {
            hidePopup();
        }
        invalidate();
    }

    public boolean isValueIndicatorEnabled() {
        return h.valueIndicatorEnabled;
    }

    public void setValueIndicatorColor(int color) {
        h.setValueIndicatorColor(color);
        if (popup != null) {
            popup.setValueIndicatorColor(h.getValueIndicatorColor());
        }
        invalidate();
    }

    public int getValueIndicatorColor() {
        return h.getValueIndicatorColor();
    }

    public void clearValueIndicatorColor() {
        h.clearValueIndicatorColor();
        if (popup != null) {
            popup.setValueIndicatorColor(h.getValueIndicatorColor());
        }
        invalidate();
    }

    public void setThumbColor(int color) {
        h.setThumbColor(color);
        invalidate();
    }

    public int getThumbColor() {
        return h.getThumbColor();
    }

    public void clearThumbColor() {
        h.clearThumbColor();
        invalidate();
    }

    public void setActiveTrackColor(int color) {
        h.setProgressColor(color);
        invalidate();
    }

    public int getActiveTrackColor() {
        return h.getProgressColor();
    }

    public void clearActiveTrackColor() {
        h.clearProgressColor();
        invalidate();
    }

    public void setInactiveTrackColor(int color) {
        h.setTrackColor(color);
        invalidate();
    }

    public int getInactiveTrackColor() {
        return h.getTrackColor();
    }

    public void clearInactiveTrackColor() {
        h.clearTrackColor();
        invalidate();
    }

    public void setDisabledTrackColor(int color) {
        h.setDisabledTrackColor(color);
        invalidate();
    }

    public int getDisabledTrackColor() {
        return h.getDisabledTrackColor();
    }

    public void clearDisabledTrackColor() {
        h.clearDisabledTrackColor();
        invalidate();
    }

    public void setDisabledThumbColor(int color) {
        h.setDisabledThumbColor(color);
        invalidate();
    }

    public int getDisabledThumbColor() {
        return h.getDisabledThumbColor();
    }

    public void clearDisabledThumbColor() {
        h.clearDisabledThumbColor();
        invalidate();
    }

    public void setThumbRadiusDp(float radiusDp) {
        h.setThumbRadiusDp(radiusDp);
        if (popup != null) {
            popup.setThumbRadiusDp(h.getThumbRadiusDp());
        }
        h.updateTrackBounds(this, getWidth(), getHeight());
        requestLayout();
        invalidate();
    }

    public float getThumbRadiusDp() {
        return h.getThumbRadiusDp();
    }

    public void clearThumbRadiusDp() {
        h.clearThumbRadiusDp();
        if (popup != null) {
            popup.setThumbRadiusDp(h.getThumbRadiusDp());
        }
        h.updateTrackBounds(this, getWidth(), getHeight());
        requestLayout();
        invalidate();
    }

    public void setTrackHeightDp(float heightDp) {
        h.setTrackHeightDp(heightDp);
        h.updateTrackBounds(this, getWidth(), getHeight());
        invalidate();
    }

    public float getTrackHeightDp() {
        return h.getTrackHeightDp();
    }

    public void clearTrackHeightDp() {
        h.clearTrackHeightDp();
        h.updateTrackBounds(this, getWidth(), getHeight());
        invalidate();
    }

    public float getTrackWidthDp() {
        return h.getTrackWidthDp(this);
    }

    public void setMinValue(float min) {
        h.min = min;
        setProgress(getProgress());
        updatePopupText();
    }

    public float getMinValue() {
        return h.min;
    }

    public void setMaxValue(float max) {
        if (h == null) return;
        h.max = max;
        super.setMax((int) ((h.max - h.min) / (h.step > 0 ? h.step : 1)));
        updatePopupText();
    }

    public float getMaxValue() {
        return h.max;
    }

    public void setStep(float step) {
        if (h == null) return;
        h.step = step;
        super.setMax((int) ((h.max - h.min) / (h.step > 0 ? h.step : 1)));
        updatePopupText();
    }

    public float getStep() {
        return h.step;
    }

    public float getValue() {
        if (h.step > 0) {
            return h.min + getProgress() * h.step;
        }
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        return h.min + fraction * (h.max - h.min);
    }

    public void setValue(float value) {
        float clamped = Math.max(h.min, Math.min(h.max, value));
        int progress;
        if (h.step > 0) {
            progress = (int) ((clamped - h.min) / h.step);
        } else {
            progress = (int) ((clamped - h.min) / (h.max - h.min) * getMax());
        }
        setProgress(progress);
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        if (h != null && popup != null && popup.isShowing()) {
            popup.update(thumbCenterX, h.formatProgress(getValue()));
        }
    }

    @Override
    public void setMax(int max) {
        if (h == null) return;
        h.max = max;
        super.setMax((int) ((h.max - h.min) / (h.step > 0 ? h.step : 1)));
        updatePopupText();
    }

    @Override
    public int getProgress() {
        return super.getProgress();
    }

    public String formatProgress(float value) {
        return h.formatProgress(value);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] size = h.measureView(widthMeasureSpec, heightMeasureSpec, this);
        setMeasuredDimension(size[0], size[1]);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.h.updateTrackBounds(this, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean enabled = isEnabled();
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        thumbCenterX = h.centerXFromFraction(fraction);
        float centerY = h.trackTop + SliderHelper.dp(this, h.getTrackHeightDp()) / 2.0f;

        h.drawCircularMask(canvas, this, enabled, thumbCenterX, centerY, SliderHelper.MASK_0);
        drawTrack(canvas, enabled, fraction);
        h.drawThumb(canvas, this, enabled, thumbCenterX, centerY);
    }

    private void drawTrack(Canvas canvas, boolean enabled, float fraction) {
        float trackHeight = SliderHelper.dp(this, h.getTrackHeightDp());
        float trackRadius = trackHeight / 2.0f;

        h.trackRect.set(h.trackLeft, h.trackTop, h.trackRight, h.trackBottom);
        h.trackPaint.setColor(h.getTrackColor());
        canvas.drawRoundRect(h.trackRect, trackRadius, trackRadius, h.trackPaint);

        if (fraction > 0) {
            h.trackRect.set(h.trackLeft, h.trackTop, thumbCenterX, h.trackBottom);
            h.trackPaint.setColor(enabled ? h.getProgressColor() : h.getDisabledTrackColor());
            canvas.drawRoundRect(h.trackRect, trackRadius, trackRadius, h.trackPaint);
        }
    }

    private int getThumbXFromProgress() {
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        return h.centerXFromFraction(fraction);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        boolean result = super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                h.startMaskAnimation(SliderHelper.MASK_0, this);
                setProgressFromTouch(event.getX());
                lastNotifiedValue = getValue();
                notifyValueChangeStart();
                showPopup();
                break;
            case MotionEvent.ACTION_MOVE:
                if (popup != null && popup.isShowing()) {
                    int cx = getThumbXFromProgress();
                    popup.update(cx, h.formatProgress(getValue()));
                }
                notifyValueChanged();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                h.stopMaskAnimation(SliderHelper.MASK_0, this);
                hidePopup();
                notifyValueChangeFinished();
                break;
        }
        return result;
    }

    private void setProgressFromTouch(float touchX) {
        int progress = h.progressFromTouch(touchX, getMax());
        setProgress(progress);
        invalidate();
    }

    private void showPopup() {
        if (!h.valueIndicatorEnabled) {
            return;
        }
        if (popup == null) {
            popup = h.createPopup(this);
        }
        int cx = getThumbXFromProgress();
        int cy = h.trackTop + (int) (SliderHelper.dp(this, h.getTrackHeightDp()) / 2f);
        popup.show(this, cx, cy, h.formatProgress(getValue()));
    }

    private void hidePopup() {
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        }
    }

    private void updatePopupText() {
        if (popup != null && popup.isShowing()) {
            popup.update(thumbCenterX, h.formatProgress(getValue()));
        }
    }

    private void notifyValueChangeStart() {
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChangeStart(getValue());
        }
    }

    private void notifyValueChanged() {
        if (onValueChangeListener == null) {
            return;
        }
        float value = getValue();
        if (value != lastNotifiedValue) {
            lastNotifiedValue = value;
            onValueChangeListener.onValueChange(value);
        }
    }

    private void notifyValueChangeFinished() {
        if (onValueChangeListener != null) {
            lastNotifiedValue = getValue();
            onValueChangeListener.onValueChangeFinished(getValue());
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
        h.cancelAllMaskAnimations();
        hidePopup();
    }
}
