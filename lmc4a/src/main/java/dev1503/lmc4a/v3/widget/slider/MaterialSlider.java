package dev1503.lmc4a.v3.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;

public class MaterialSlider extends SeekBar {

    protected final SliderHelper h = new SliderHelper();
    private SliderPopup popup;
    private boolean isTouching = false;
    private int thumbCenterX;

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
        h.colorScheme = colorScheme;
        h.resolveColors();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return h.colorScheme;
    }

    public void setMin(float min) {
        h.min = min;
        setProgress(getProgress());
    }

    public float getMinValue() {
        return h.min;
    }

    @Override
    public void setMax(int max) {
        if (h == null) return;
        h.max = max;
        super.setMax((int) ((h.max - h.min) / (h.step > 0 ? h.step : 1)));
    }

    public void setMax(float max) {
        if (h == null) return;
        h.max = max;
        super.setMax((int) ((h.max - h.min) / (h.step > 0 ? h.step : 1)));
    }

    public float getMaxValue() {
        return h.max;
    }

    public void setStep(float step) {
        if (h == null) return;
        h.step = step;
        super.setMax((int) ((h.max - h.min) / (h.step > 0 ? h.step : 1)));
    }

    public float getStep() {
        return h.step;
    }

    public float getProgressValue() {
        if (h.step > 0) {
            return h.min + getProgress() * h.step;
        }
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        return h.min + fraction * (h.max - h.min);
    }

    public void setProgressValue(float value) {
        float clamped = Math.max(h.min, Math.min(h.max, value));
        int progress;
        if (h.step > 0) {
            progress = (int) ((clamped - h.min) / h.step);
        } else {
            progress = (int) ((clamped - h.min) / (h.max - h.min) * getMax());
        }
        setProgress(progress);
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        if (h != null && popup != null && popup.isShowing()) {
            popup.update(thumbCenterX, h.formatProgress(getProgressValue()));
        }
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
        float centerY = h.trackTop + SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP) / 2.0f;

        h.drawCircularMask(canvas, this, enabled, thumbCenterX, centerY, SliderHelper.MASK_0);
        drawTrack(canvas, enabled, fraction);
        h.drawThumb(canvas, this, enabled, thumbCenterX, centerY);
    }

    private void drawTrack(Canvas canvas, boolean enabled, float fraction) {
        float trackHeight = SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP);
        float trackRadius = trackHeight / 2.0f;

        h.trackRect.set(h.trackLeft, h.trackTop, h.trackRight, h.trackBottom);
        h.trackPaint.setColor(h.trackColor);
        canvas.drawRoundRect(h.trackRect, trackRadius, trackRadius, h.trackPaint);

        if (fraction > 0) {
            h.trackRect.set(h.trackLeft, h.trackTop, thumbCenterX, h.trackBottom);
            h.trackPaint.setColor(enabled ? h.progressColor : h.disabledTrackColor);
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
                showPopup();
                break;
            case MotionEvent.ACTION_MOVE:
                if (popup != null && popup.isShowing()) {
                    int cx = getThumbXFromProgress();
                    popup.update(cx, h.formatProgress(getProgressValue()));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouching = false;
                h.stopMaskAnimation(SliderHelper.MASK_0, this);
                hidePopup();
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
        if (popup == null) {
            popup = h.createPopup(this);
        }
        int cx = getThumbXFromProgress();
        int cy = h.trackTop + (int) (SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP) / 2f);
        popup.show(this, cx, cy, h.formatProgress(getProgressValue()));
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
        h.cancelAllMaskAnimations();
        hidePopup();
    }
}
