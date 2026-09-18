package dev1503.lmc4a.v3.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MaterialRangeSlider extends MaterialSlider {

    private int lowProgress = 0;
    private int highProgress = 100;

    private SliderPopup lowPopup;
    private SliderPopup highPopup;
    private int activeThumb = -1;

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
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public MaterialRangeSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        if (h.step > 0) {
            return h.min + lowProgress * h.step;
        }
        float fraction = getInternalMax() > 0 ? (float) lowProgress / getInternalMax() : 0f;
        return h.min + fraction * (h.max - h.min);
    }

    public float getHighValue() {
        if (h.step > 0) {
            return h.min + highProgress * h.step;
        }
        float fraction = getInternalMax() > 0 ? (float) highProgress / getInternalMax() : 0f;
        return h.min + fraction * (h.max - h.min);
    }

    public void setLowValue(float value) {
        float clamped = Math.max(h.min, Math.min(h.max, value));
        int progress;
        if (h.step > 0) {
            progress = (int) ((clamped - h.min) / h.step);
        } else {
            progress = (int) ((clamped - h.min) / (h.max - h.min) * getInternalMax());
        }
        setLowProgress(progress);
    }

    public void setHighValue(float value) {
        float clamped = Math.max(h.min, Math.min(h.max, value));
        int progress;
        if (h.step > 0) {
            progress = (int) ((clamped - h.min) / h.step);
        } else {
            progress = (int) ((clamped - h.min) / (h.max - h.min) * getInternalMax());
        }
        setHighProgress(progress);
    }

    public float getValue() {
        return (getLowValue() + getHighValue()) / 2f;
    }

    public float getValueRadius() {
        return (getMaxValue() - getMinValue()) / 2f;
    }

    private int getInternalMax() {
        return (int) ((h.max - h.min) / (h.step > 0 ? h.step : 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean enabled = isEnabled();
        int internalMax = getInternalMax();
        float lowFraction = internalMax > 0 ? (float) lowProgress / internalMax : 0f;
        float highFraction = internalMax > 0 ? (float) highProgress / internalMax : 0f;
        int lowCenterX = h.centerXFromFraction(lowFraction);
        int highCenterX = h.centerXFromFraction(highFraction);
        float centerY = h.trackTop + SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP) / 2.0f;

        h.drawCircularMask(canvas, this, enabled, lowCenterX, centerY, SliderHelper.MASK_0);
        h.drawCircularMask(canvas, this, enabled, highCenterX, centerY, SliderHelper.MASK_1);
        drawTrack(canvas, enabled, lowCenterX, highCenterX);
        h.drawThumb(canvas, this, enabled, lowCenterX, centerY);
        h.drawThumb(canvas, this, enabled, highCenterX, centerY);
    }

    private void drawTrack(Canvas canvas, boolean enabled, int lowCenterX, int highCenterX) {
        float trackHeight = SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP);
        float trackRadius = trackHeight / 2.0f;

        h.trackRect.set(h.trackLeft, h.trackTop, h.trackRight, h.trackBottom);
        h.trackPaint.setColor(h.trackColor);
        canvas.drawRoundRect(h.trackRect, trackRadius, trackRadius, h.trackPaint);

        if (highCenterX > lowCenterX) {
            h.trackRect.set(lowCenterX, h.trackTop, highCenterX, h.trackBottom);
            h.trackPaint.setColor(enabled ? h.progressColor : h.disabledTrackColor);
            canvas.drawRoundRect(h.trackRect, trackRadius, trackRadius, h.trackPaint);
        }
    }

    private int getLowCenterX() {
        int internalMax = getInternalMax();
        float fraction = internalMax > 0 ? (float) lowProgress / internalMax : 0f;
        return h.centerXFromFraction(fraction);
    }

    private int getHighCenterX() {
        int internalMax = getInternalMax();
        float fraction = internalMax > 0 ? (float) highProgress / internalMax : 0f;
        return h.centerXFromFraction(fraction);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }

        float touchX = event.getX();
        float touchY = event.getY();
        float centerY = h.trackTop + SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP) / 2.0f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeThumb = pickThumb(touchX, touchY, centerY);
                if (activeThumb == -1) {
                    return false;
                }
                h.startMaskAnimation(activeThumb == THUMB_LOW ? SliderHelper.MASK_0 : SliderHelper.MASK_1, this);
                if (activeThumb == THUMB_LOW) {
                    setProgressFromTouch(THUMB_LOW, touchX);
                    showPopup(THUMB_LOW);
                } else {
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
                h.stopMaskAnimation(activeThumb == THUMB_LOW ? SliderHelper.MASK_0 : SliderHelper.MASK_1, this);
                if (activeThumb == THUMB_LOW) {
                    hidePopup(THUMB_LOW);
                } else if (activeThumb == THUMB_HIGH) {
                    hidePopup(THUMB_HIGH);
                }
                activeThumb = -1;
                invalidate();
                break;
        }
        return true;
    }

    private int pickThumb(float touchX, float touchY, float centerY) {
        int lowX = getLowCenterX();
        int highX = getHighCenterX();
        float threshold = SliderHelper.dp(this, THUMB_SIZE_THRESHOLD_DP) / 2.0f;

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
        int progress = h.progressFromTouch(touchX, getInternalMax());
        if (thumb == THUMB_LOW) {
            lowProgress = Math.max(0, Math.min(highProgress, progress));
        } else {
            highProgress = Math.max(lowProgress, Math.min(getInternalMax(), progress));
        }
        notifyRangeChanged();
    }

    private void showPopup(int thumb) {
        SliderPopup popup = thumb == THUMB_LOW ? getOrCreateLowPopup() : getOrCreateHighPopup();
        float value = thumb == THUMB_LOW ? getLowValue() : getHighValue();
        int cx = thumb == THUMB_LOW ? getLowCenterX() : getHighCenterX();
        int cy = h.trackTop + (int) (SliderHelper.dp(this, SliderHelper.TRACK_HEIGHT_DP) / 2f);
        popup.show(this, cx, cy, h.formatProgress(value));
    }

    private void updatePopup(int thumb) {
        SliderPopup popup = thumb == THUMB_LOW ? lowPopup : highPopup;
        if (popup != null && popup.isShowing()) {
            float value = thumb == THUMB_LOW ? getLowValue() : getHighValue();
            int cx = thumb == THUMB_LOW ? getLowCenterX() : getHighCenterX();
            popup.update(cx, h.formatProgress(value));
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
            lowPopup = h.createPopup(this);
        }
        return lowPopup;
    }

    private SliderPopup getOrCreateHighPopup() {
        if (highPopup == null) {
            highPopup = h.createPopup(this);
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
}