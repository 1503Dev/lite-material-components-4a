package dev1503.lmc4a.v3.widget.slider;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.TypedValue;
import android.view.View;

import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

class SliderHelper {

    static final int MASK_0 = 0;
    static final int MASK_1 = 1;

    static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    static final float DEFAULT_HEIGHT_DP = 48.0f;
    static final float TRACK_HEIGHT_DP = 4.0f;
    static final float THUMB_SIZE_DP = 20.0f;
    private static final float THUMB_SHADOW_BLUR_DP = 2.0f;
    private static final float THUMB_SHADOW_OFFSET_DP = 1.0f;
    private static final int THUMB_SHADOW_COLOR = 0x3C000000;
    private static final int THUMB_SHADOW_COLOR_EDGE = 0x00000000;
    private static final float MASK_RADIUS_DP = 24.0f;
    private static final float IN_STIFFNESS = 400f;
    private static final float IN_DAMPING = 0.7f;

    DynamicScheme colorScheme;
    final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint thumbShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RadialGradient thumbShadowShader;
    private final Matrix thumbShadowMatrix = new Matrix();
    final RectF trackRect = new RectF();
    final RectF thumbRect = new RectF();

    float min = 0f;
    float max = 100f;
    float step = 0f;
    int progressColor;
    int trackColor;
    int thumbColor;
    int disabledTrackColor;

    private Integer progressColorOverride;
    private Integer trackColorOverride;
    private Integer thumbColorOverride;
    private Integer disabledTrackColorOverride;
    private Integer disabledThumbColorOverride;
    private Integer valueIndicatorColorOverride;
    private float thumbRadiusDp = THUMB_SIZE_DP / 2.0f;
    private float trackHeightDp = TRACK_HEIGHT_DP;
    boolean valueIndicatorEnabled = true;

    int trackTop;
    int trackBottom;
    int trackLeft;
    int trackRight;

    private final float[] animatedMaskRadius = new float[2];
    private final SpringSimulation[] spring = new SpringSimulation[2];
    private final ValueAnimator[] springAnimator = new ValueAnimator[2];
    private final long[] lastFrameTime = new long[2];
    private View hostingView;
    private int lastTrackWidthPx;

    SliderHelper() {
        this(publicColorScheme);
    }

    SliderHelper(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme != null ? colorScheme : publicColorScheme;
    }

    void resolveColors() {
        progressColor = dynamicColors.primary().getArgb(colorScheme);
        trackColor = dynamicColors.surfaceContainerHighest().getArgb(colorScheme);
        thumbColor = dynamicColors.primary().getArgb(colorScheme);
        int outlineVariant = dynamicColors.outlineVariant().getArgb(colorScheme);
        int outline = dynamicColors.outline().getArgb(colorScheme);
        disabledTrackColor = blendColors(outlineVariant, outline, 0.5f);
    }

    void clearColorOverrides() {
        progressColorOverride = null;
        trackColorOverride = null;
        thumbColorOverride = null;
        disabledTrackColorOverride = null;
        disabledThumbColorOverride = null;
        valueIndicatorColorOverride = null;
        valueIndicatorEnabled = true;
        resolveColors();
    }

    int getProgressColor() {
        return progressColorOverride != null ? progressColorOverride : progressColor;
    }

    void setProgressColor(int color) {
        progressColorOverride = color;
    }

    void clearProgressColor() {
        progressColorOverride = null;
    }

    int getTrackColor() {
        return trackColorOverride != null ? trackColorOverride : trackColor;
    }

    void setTrackColor(int color) {
        trackColorOverride = color;
    }

    void clearTrackColor() {
        trackColorOverride = null;
    }

    int getThumbColor() {
        return thumbColorOverride != null ? thumbColorOverride : thumbColor;
    }

    void setThumbColor(int color) {
        thumbColorOverride = color;
    }

    void clearThumbColor() {
        thumbColorOverride = null;
    }

    int getDisabledTrackColor() {
        return disabledTrackColorOverride != null ? disabledTrackColorOverride : disabledTrackColor;
    }

    void setDisabledTrackColor(int color) {
        disabledTrackColorOverride = color;
    }

    void clearDisabledTrackColor() {
        disabledTrackColorOverride = null;
    }

    int getDisabledThumbColor() {
        if (disabledThumbColorOverride != null) {
            return disabledThumbColorOverride;
        }
        return getDisabledTrackColor();
    }

    void setDisabledThumbColor(int color) {
        disabledThumbColorOverride = color;
    }

    void clearDisabledThumbColor() {
        disabledThumbColorOverride = null;
    }

    int getValueIndicatorColor() {
        if (valueIndicatorColorOverride != null) {
            return valueIndicatorColorOverride;
        }
        return dynamicColors.primary().getArgb(colorScheme);
    }

    void setValueIndicatorColor(int color) {
        valueIndicatorColorOverride = color;
    }

    void clearValueIndicatorColor() {
        valueIndicatorColorOverride = null;
    }

    float getThumbRadiusDp() {
        return thumbRadiusDp;
    }

    void setThumbRadiusDp(float radiusDp) {
        thumbRadiusDp = Math.max(1.0f, radiusDp);
        thumbShadowShader = null;
    }

    void clearThumbRadiusDp() {
        thumbRadiusDp = THUMB_SIZE_DP / 2.0f;
        thumbShadowShader = null;
    }

    float getTrackHeightDp() {
        return trackHeightDp;
    }

    void setTrackHeightDp(float heightDp) {
        trackHeightDp = Math.max(1.0f, heightDp);
    }

    void clearTrackHeightDp() {
        trackHeightDp = TRACK_HEIGHT_DP;
    }

    float getTrackWidthDp(View view) {
        return lastTrackWidthPx / view.getResources().getDisplayMetrics().density;
    }

    void updateTrackBounds(View view, int w, int h) {
        float thumbRadius = dp(view, getThumbRadiusDp());
        trackTop = (int) ((h - dp(view, getTrackHeightDp())) / 2);
        trackBottom = trackTop + (int) dp(view, getTrackHeightDp());
        trackLeft = (int) (view.getPaddingLeft() + thumbRadius);
        trackRight = (int) (w - view.getPaddingRight() - thumbRadius);
        lastTrackWidthPx = Math.max(0, trackRight - trackLeft);
    }

    int[] measureView(int widthMeasureSpec, int heightMeasureSpec, View view) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (dp(view, DEFAULT_HEIGHT_DP) + 0.5f);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = View.MeasureSpec.getSize(heightMeasureSpec);
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            height = Math.min(height, View.MeasureSpec.getSize(heightMeasureSpec));
        }
        return new int[]{width, height};
    }

    void startMaskAnimation(int id, View view) {
        hostingView = view;
        if (springAnimator[id] != null) {
            springAnimator[id].cancel();
        }
        spring[id] = new SpringSimulation(IN_STIFFNESS, IN_DAMPING);
        spring[id].setPosition(animatedMaskRadius[id]);
        spring[id].setTarget(1f);

        lastFrameTime[id] = System.nanoTime();
        final int maskId = id;
        springAnimator[id] = ValueAnimator.ofFloat(0f, 1f);
        springAnimator[id].setDuration(1000);
        springAnimator[id].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime[maskId]) / 1_000_000_000f;
                lastFrameTime[maskId] = now;
                delta = Math.min(delta, 0.05f);

                animatedMaskRadius[maskId] = spring[maskId].update(delta);
                hostingView.invalidate();

                if (spring[maskId].isAtRest()) {
                    animation.cancel();
                }
            }
        });
        springAnimator[id].start();
    }

    void stopMaskAnimation(int id, View view) {
        hostingView = view;
        if (springAnimator[id] != null) {
            springAnimator[id].cancel();
        }
        spring[id] = new SpringSimulation(IN_STIFFNESS, IN_DAMPING);
        spring[id].setPosition(animatedMaskRadius[id]);
        spring[id].setTarget(0f);

        lastFrameTime[id] = System.nanoTime();
        final int maskId = id;
        springAnimator[id] = ValueAnimator.ofFloat(0f, 1f);
        springAnimator[id].setDuration(300);
        springAnimator[id].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime[maskId]) / 1_000_000_000f;
                lastFrameTime[maskId] = now;
                delta = Math.min(delta, 0.05f);

                animatedMaskRadius[maskId] = spring[maskId].update(delta);
                hostingView.invalidate();

                if (spring[maskId].isAtRest()) {
                    animation.cancel();
                }
            }
        });
        springAnimator[id].start();
    }

    void cancelMaskAnimation(int id) {
        if (springAnimator[id] != null) {
            springAnimator[id].cancel();
        }
        animatedMaskRadius[id] = 0f;
    }

    void cancelAllMaskAnimations() {
        cancelMaskAnimation(MASK_0);
        cancelMaskAnimation(MASK_1);
    }

    void drawCircularMask(Canvas canvas, View view, boolean enabled, float centerX,
                           float centerY, int id) {
        if (!enabled || animatedMaskRadius[id] <= 0f) return;
        float targetRadius = dp(view, MASK_RADIUS_DP);
        float radius = animatedMaskRadius[id] * targetRadius;
        thumbPaint.setColor(applyAlphaFraction(getThumbColor(), 0.12f));
        canvas.drawCircle(centerX, centerY, radius, thumbPaint);
    }

    void drawThumb(Canvas canvas, View view, boolean enabled, float centerX, float centerY) {
        float thumbRadius = dp(view, getThumbRadiusDp());
        float shadowRadius = thumbRadius + dp(view, THUMB_SHADOW_BLUR_DP);
        float shadowCenterY = centerY + dp(view, THUMB_SHADOW_OFFSET_DP);

        // 阴影用径向渐变自绘：setShadowLayer 在 API 28 之前只对文字生效，
        // 硬件加速画布上图形不会画阴影，Android 8.1 上 thumb 因此完全没有阴影。
        // 这里用单位圆渐变配合 localMatrix 平移缩放，避免每帧重建 Shader。
        if (thumbShadowShader == null) {
            thumbShadowShader = new RadialGradient(0f, 0f, 1f,
                    new int[]{THUMB_SHADOW_COLOR, THUMB_SHADOW_COLOR_EDGE},
                    new float[]{thumbRadius / shadowRadius, 1f},
                    Shader.TileMode.CLAMP);
        }
        thumbShadowMatrix.setScale(shadowRadius, shadowRadius);
        thumbShadowMatrix.postTranslate(centerX, shadowCenterY);
        thumbShadowShader.setLocalMatrix(thumbShadowMatrix);
        thumbShadowPaint.setShader(thumbShadowShader);
        canvas.drawCircle(centerX, shadowCenterY, shadowRadius, thumbShadowPaint);
        thumbShadowPaint.setShader(null);

        thumbPaint.setColor(enabled ? getThumbColor() : getDisabledThumbColor());
        thumbRect.set(centerX - thumbRadius, centerY - thumbRadius,
                centerX + thumbRadius, centerY + thumbRadius);
        canvas.drawRoundRect(thumbRect, thumbRadius, thumbRadius, thumbPaint);
    }

    int centerXFromFraction(float fraction) {
        return (int) (trackLeft + fraction * (trackRight - trackLeft));
    }

    int progressFromTouch(float touchX, int internalMax) {
        if (trackRight == trackLeft) {
            return 0;
        }
        float fraction = (touchX - trackLeft) / (float) (trackRight - trackLeft);
        fraction = Math.max(0f, Math.min(1f, fraction));
        return (int) (fraction * internalMax + 0.5f);
    }

    String formatProgress(float value) {
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

    SliderPopup createPopup(View view) {
        SliderPopup popup = new SliderPopup(view.getContext());
        popup.setValueIndicatorColor(getValueIndicatorColor());
        popup.setTextColor(dynamicColors.onPrimary().getArgb(colorScheme));
        popup.setThumbRadiusDp(getThumbRadiusDp());
        return popup;
    }

    private static int blendColors(int color1, int color2, float ratio) {
        float inverseRatio = 1f - ratio;
        int a = (int) ((color1 >> 24 & 0xff) * inverseRatio + (color2 >> 24 & 0xff) * ratio);
        int r = (int) ((color1 >> 16 & 0xff) * inverseRatio + (color2 >> 16 & 0xff) * ratio);
        int g = (int) ((color1 >> 8 & 0xff) * inverseRatio + (color2 >> 8 & 0xff) * ratio);
        int b = (int) ((color1 & 0xff) * inverseRatio + (color2 & 0xff) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int countDecimals(float value) {
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

    static int applyAlphaFraction(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * alphaFraction);
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    static float dp(View view, float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, view.getResources().getDisplayMetrics());
    }
}
