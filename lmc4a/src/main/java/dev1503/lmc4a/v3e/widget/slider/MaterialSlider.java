package dev1503.lmc4a.v3e.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialSlider extends dev1503.lmc4a.v3.widget.slider.MaterialSlider {

    public static final float DEFAULT_TRACK_HEIGHT_DP = 16.0f;
    public static final float DEFAULT_HANDLE_WIDTH_DP = 4.0f;
    public static final float DEFAULT_HANDLE_HEIGHT_DP = 44.0f;
    public static final float DEFAULT_PRESSED_HANDLE_WIDTH_DP = 2.0f;
    public static final float DEFAULT_TRACK_GAP_DP = 6.0f;
    public static final float DEFAULT_INSIDE_CORNER_DP = 2.0f;
    public static final float DEFAULT_STOP_INDICATOR_DP = 4.0f;
    public static final float DEFAULT_STOP_INDICATOR_INSET_DP = 4.0f;

    private static final float TRACK_BAND_HEIGHT_DP = 48.0f;
    private static final float DISABLED_ACTIVE_ALPHA = 0.38f;
    private static final float DISABLED_INACTIVE_ALPHA = 0.12f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path trackPath = new Path();
    private final RectF trackRect = new RectF();
    private final RectF handleRect = new RectF();
    private final float[] activeTrackRadii = new float[8];
    private final float[] inactiveTrackRadii = new float[8];

    public MaterialSlider(Context context) {
        this(context, null);
    }

    public MaterialSlider(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public MaterialSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTrackHeightDp(DEFAULT_TRACK_HEIGHT_DP);
        setPadding(0, getPaddingTop(), 0, getPaddingBottom());
        setMinimumHeight((int) (dp(TRACK_BAND_HEIGHT_DP) + 0.5f));
        setHandleWidthDp(DEFAULT_HANDLE_WIDTH_DP);
        setPressedHandleWidthDp(DEFAULT_PRESSED_HANDLE_WIDTH_DP);
        setHandleHeightDp(DEFAULT_HANDLE_HEIGHT_DP);
        setTrackGapDp(DEFAULT_TRACK_GAP_DP);
    }

    @Override
    protected float trackEdgeInset(float travelInsetPx) {
        return 0f;
    }

    @Override
    public void clearHandleWidthDp() {
        setHandleWidthDp(DEFAULT_HANDLE_WIDTH_DP);
    }

    @Override
    public void clearPressedHandleWidthDp() {
        setPressedHandleWidthDp(DEFAULT_PRESSED_HANDLE_WIDTH_DP);
    }

    @Override
    public void clearHandleHeightDp() {
        setHandleHeightDp(DEFAULT_HANDLE_HEIGHT_DP);
    }

    @Override
    public void clearTrackGapDp() {
        setTrackGapDp(DEFAULT_TRACK_GAP_DP);
    }

    @Override
    public void setThumbRadiusDp(float radiusDp) {
        setHandleWidthDp(radiusDp * 2.0f);
    }

    @Override
    public float getThumbRadiusDp() {
        return getHandleWidthDp() / 2.0f;
    }

    @Override
    public void clearThumbRadiusDp() {
        clearHandleWidthDp();
    }

    @Override
    protected int defaultInactiveTrackColor() {
        return dynamicColors.secondaryContainer().getArgb(scheme());
    }

    @Override
    protected int defaultDisabledTrackColor() {
        return applyAlpha(dynamicColors.onSurface().getArgb(scheme()), DISABLED_ACTIVE_ALPHA);
    }

    @Override
    protected int defaultDisabledThumbColor() {
        return applyAlpha(dynamicColors.onSurface().getArgb(scheme()), DISABLED_ACTIVE_ALPHA);
    }

    private DynamicScheme scheme() {
        DynamicScheme scheme = getColorScheme();
        return scheme != null ? scheme : publicColorScheme;
    }

    private int inactiveTrackColor(boolean enabled) {
        if (!enabled) {
            if (hasDisabledTrackColor()) {
                return getDisabledTrackColor();
            }
            return applyAlpha(dynamicColors.onSurface().getArgb(scheme()), DISABLED_INACTIVE_ALPHA);
        }
        return getInactiveTrackColor();
    }

    private int activeTrackColor(boolean enabled) {
        return enabled ? getActiveTrackColor() : getDisabledTrackColor();
    }

    private int handleColor(boolean enabled) {
        return enabled ? getThumbColor() : getDisabledThumbColor();
    }

    private int stopIndicatorColor(boolean enabled, boolean active) {
        if (!enabled) {
            if (hasDisabledTrackColor()) {
                return getDisabledTrackColor();
            }
            return applyAlpha(dynamicColors.onSurface().getArgb(scheme()),
                    active ? DISABLED_ACTIVE_ALPHA : DISABLED_INACTIVE_ALPHA);
        }
        if (active) {
            return dynamicColors.onPrimary().getArgb(scheme());
        }
        return dynamicColors.onSecondaryContainer().getArgb(scheme());
    }

    @Override
    protected float popupThumbRadiusDp() {
        return getHandleHeightDp() / 2.0f;
    }

    @Override
    protected int getTrackCenterY() {
        return (int) (getHeight() - dp(TRACK_BAND_HEIGHT_DP) / 2.0f);
    }

    @Override
    protected void onInteractionStart() {
        animateHandlePress(1.0f);
    }

    @Override
    protected void onInteractionEnd() {
        animateHandlePress(0.0f);
    }

    @Override
    protected boolean delegatesDragToSystem() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int desired = (int) (dp(TRACK_BAND_HEIGHT_DP) + 0.5f);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = desired;
        if (mode == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else if (mode == MeasureSpec.AT_MOST) {
            height = Math.min(desired, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, top, 0, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int trackLeft = getTrackLeft();
        int trackRight = getTrackRight();
        if (trackRight <= trackLeft) {
            return;
        }
        boolean enabled = isEnabled();
        float fraction = getMax() > 0 ? (float) getProgress() / getMax() : 0.0f;
        float centerX = handleCenterX(fraction);
        float centerY = getTrackCenterY();
        float trackHeight = dp(getTrackHeightDp());
        float handleWidth = currentHandleWidthPx();
        float handleHeight = dp(getHandleHeightDp());
        float gap = dp(getTrackGapDp());
        float outerRadius = trackHeight / 2.0f;
        float inside = Math.min(dp(DEFAULT_INSIDE_CORNER_DP), outerRadius);
        float handleHalf = handleWidth / 2.0f;
        float trackTop = centerY - trackHeight / 2.0f;
        float trackBottom = centerY + trackHeight / 2.0f;

        float activeEnd = centerX - handleHalf - gap;
        if (activeEnd > trackLeft) {
            setRadii(activeTrackRadii, outerRadius, inside);
            trackRect.set(trackLeft, trackTop, activeEnd, trackBottom);
            trackPath.reset();
            trackPath.addRoundRect(trackRect, activeTrackRadii, Path.Direction.CW);
            trackPaint.setColor(activeTrackColor(enabled));
            canvas.drawPath(trackPath, trackPaint);
        }

        float inactiveStart = centerX + handleHalf + gap;
        if (trackRight > inactiveStart) {
            setRadii(inactiveTrackRadii, inside, outerRadius);
            trackRect.set(inactiveStart, trackTop, trackRight, trackBottom);
            trackPath.reset();
            trackPath.addRoundRect(trackRect, inactiveTrackRadii, Path.Direction.CW);
            trackPaint.setColor(inactiveTrackColor(enabled));
            canvas.drawPath(trackPath, trackPaint);
        }

        drawStopIndicators(canvas, enabled, activeEnd, centerY);

        handleRect.set(centerX - handleWidth / 2.0f, centerY - handleHeight / 2.0f,
                centerX + handleWidth / 2.0f, centerY + handleHeight / 2.0f);
        handlePaint.setColor(handleColor(enabled));
        canvas.drawRoundRect(handleRect, handleWidth / 2.0f, handleWidth / 2.0f, handlePaint);
    }

    private void setRadii(float[] radii, float outerRadius, float inside) {
        radii[0] = outerRadius;
        radii[1] = outerRadius;
        radii[2] = inside;
        radii[3] = inside;
        radii[4] = inside;
        radii[5] = inside;
        radii[6] = outerRadius;
        radii[7] = outerRadius;
    }

    private void drawStopIndicators(Canvas canvas, boolean enabled, float activeEnd, float centerY) {
        float radius = dp(DEFAULT_STOP_INDICATOR_DP) / 2.0f;
        float inset = dp(DEFAULT_STOP_INDICATOR_INSET_DP);
        float leftX = handleCenterX(0.0f) + inset;
        float rightX = handleCenterX(1.0f) - inset;
        float clearance = currentHandleWidthPx() / 2.0f + dp(getTrackGapDp()) + radius;
        float centerX = handleCenterX(
                getMax() > 0 ? (float) getProgress() / getMax() : 0.0f);
        if (Math.abs(centerX - leftX) > clearance) {
            indicatorPaint.setColor(stopIndicatorColor(enabled, activeEnd > leftX));
            canvas.drawCircle(leftX, centerY, radius, indicatorPaint);
        }
        if (Math.abs(centerX - rightX) > clearance) {
            indicatorPaint.setColor(stopIndicatorColor(enabled, activeEnd > rightX));
            canvas.drawCircle(rightX, centerY, radius, indicatorPaint);
        }
    }
}
