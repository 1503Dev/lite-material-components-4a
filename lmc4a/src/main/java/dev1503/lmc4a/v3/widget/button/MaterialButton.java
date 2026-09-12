package dev1503.lmc4a.v3.widget.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialButton extends Button {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float DEFAULT_VISUAL_HEIGHT_DP = 40.0f;
    private static final float DEFAULT_VERTICAL_INSET_DP = 4.0f;
    private static final float DEFAULT_CORNER_RADIUS_DP = 20.0f;
    private static final float DEFAULT_HORIZONTAL_PADDING_DP = 16.0f;
    private static final float DEFAULT_MIN_WIDTH_DP = 90.0f;
    private static final float DISABLED_CONTAINER_ALPHA = 0.12f;
    private static final float DISABLED_TEXT_ALPHA = 0.38f;
    private static final float PRESSED_RIPPLE_ALPHA = 0.10f;
    private static final float ELEVATED_ELEVATION_DP = 1.0f;
    private static final float OUTLINE_STROKE_DP = 1.0f;
    private static final float ELEVATED_SHADOW_RADIUS_DP = 2.0f;
    private static final float ELEVATED_SHADOW_OFFSET_DP = 1.0f;
    private static final float ELEVATED_SHADOW_ALPHA = 0.30f;

    private DynamicScheme colorScheme = publicColorScheme;
    private ButtonStyle buttonStyle = ButtonStyle.FILLED;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private final GradientDrawable contentDrawable = new GradientDrawable();
    private final GradientDrawable maskDrawable = new GradientDrawable();
    private final float verticalInset;
    private final float defaultTotalHeight;
    private float cornerRadius;

    public MaterialButton(Context context) {
        this(context, null);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        verticalInset = dp(DEFAULT_VERTICAL_INSET_DP);
        defaultTotalHeight = dp(DEFAULT_VISUAL_HEIGHT_DP) + verticalInset * 2.0f;
        cornerRadius = dp(DEFAULT_CORNER_RADIUS_DP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(0.0f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStateListAnimator(null);
            }
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int inset = (int) (verticalInset + 0.5f);
                    outline.setRoundRect(
                            0, inset, view.getWidth(), view.getHeight() - inset, cornerRadius);
                    outline.setAlpha(buttonStyle == ButtonStyle.ELEVATED ? 1.0f : 0.0f);
                }
            });
            setClipToOutline(true);
        }
        refreshColorScheme();
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        refreshColorScheme();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setButtonStyle(ButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle;
        refreshColorScheme();
    }

    public ButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    public void setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadius = dp(cornerRadiusDp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentDrawable.setCornerRadius(cornerRadius);
            maskDrawable.setCornerRadius(cornerRadius);
            invalidateOutline();
        }
        invalidate();
    }

    public float getCornerRadiusDp() {
        return cornerRadius / getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveMeasuredWidth(widthMeasureSpec);
        if (width != getMeasuredWidth()) {
            // TextView 只在测量阶段按测量宽度重建内部 Layout。若仅用 setMeasuredDimension
            // 放大控件宽度，Layout 的宽度仍是文本自身的宽度，TEXT_ALIGNMENT_CENTER 便没有
            // 可居中的余量，短文本会贴着左侧内边距绘制。这里用最终宽度重新测量一次，
            // 让 Layout 与控件宽度保持一致，文本才会真正水平居中。
            super.onMeasure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    heightMeasureSpec);
        }

        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int height = getMeasuredHeight();
        if (heightMode != View.MeasureSpec.EXACTLY) {
            height = Math.max(height, (int) (defaultTotalHeight + 0.5f));
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (heightMode == View.MeasureSpec.AT_MOST && heightSpecSize > 0) {
                height = Math.min(height, heightSpecSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    private int resolveMeasuredWidth(int widthMeasureSpec) {
        int width = getMeasuredWidth();
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == View.MeasureSpec.EXACTLY) {
            return width;
        }
        width = Math.max(width, (int) (dp(DEFAULT_MIN_WIDTH_DP) + 0.5f));
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == View.MeasureSpec.AT_MOST && widthSpecSize > 0) {
            width = Math.min(width, widthSpecSize);
        }
        return width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            rectF.set(0.0f, verticalInset, getWidth(), getHeight() - verticalInset);
            paint.setColor(resolveContainerColor());
            if (buttonStyle == ButtonStyle.ELEVATED) {
                paint.setShadowLayer(
                        dp(ELEVATED_SHADOW_RADIUS_DP),
                        0.0f,
                        dp(ELEVATED_SHADOW_OFFSET_DP),
                        applyAlphaFraction(
                                dynamicColors.shadow().getArgb(colorScheme),
                                ELEVATED_SHADOW_ALPHA));
            }
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
            paint.clearShadowLayer();
            if (isPressed()) {
                paint.setColor(applyAlphaFraction(resolveEnabledTextColor(), PRESSED_RIPPLE_ALPHA));
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
            }
            if (buttonStyle == ButtonStyle.OUTLINED) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(OUTLINE_STROKE_DP));
                paint.setColor(resolveStrokeColor());
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
                paint.setStyle(Paint.Style.FILL);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private void refreshColorScheme() {
        if (colorScheme == null) {
            colorScheme = Imc.publicColorScheme;
        }
        setTextColor(resolveTextColors());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            applyRippleBackground();
            setElevation(buttonStyle == ButtonStyle.ELEVATED ? dp(ELEVATED_ELEVATION_DP) : 0.0f);
            invalidateOutline();
        } else {
            setBackground(null);
            setLayerType(buttonStyle == ButtonStyle.ELEVATED
                    ? View.LAYER_TYPE_SOFTWARE : View.LAYER_TYPE_NONE, null);
        }
        setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        int horizontalPadding = (int) (dp(DEFAULT_HORIZONTAL_PADDING_DP) + 0.5f);
        setPadding(horizontalPadding, 0, horizontalPadding, 0);
        invalidate();
    }

    private void applyRippleBackground() {
        contentDrawable.setCornerRadius(cornerRadius);
        contentDrawable.setColor(resolveContainerColors());
        if (buttonStyle == ButtonStyle.OUTLINED) {
            int strokeWidth = (int) (dp(OUTLINE_STROKE_DP) + 0.5f);
            contentDrawable.setStroke(strokeWidth, resolveStrokeColors());
        } else {
            contentDrawable.setStroke(0, Color.TRANSPARENT);
        }
        maskDrawable.setCornerRadius(cornerRadius);
        maskDrawable.setColor(Color.WHITE);
        RippleDrawable rippleDrawable = new RippleDrawable(
                ColorStateList.valueOf(resolveRippleColor()),
                contentDrawable,
                maskDrawable);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{rippleDrawable});
        int inset = (int) (verticalInset + 0.5f);
        layerDrawable.setLayerInset(0, 0, inset, 0, inset);
        setBackground(layerDrawable);
    }

    private int resolveRippleColor() {
        return applyAlphaFraction(resolveEnabledTextColor(), PRESSED_RIPPLE_ALPHA);
    }

    private int resolveContainerColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        return resolveEnabledContainerColor();
    }

    private int resolveEnabledContainerColor() {
        if (buttonStyle == ButtonStyle.ELEVATED) {
            return dynamicColors.surfaceContainerLow().getArgb(colorScheme);
        }
        if (buttonStyle == ButtonStyle.FILLED) {
            return dynamicColors.primary().getArgb(colorScheme);
        }
        return Color.TRANSPARENT;
    }

    private int resolveEnabledTextColor() {
        if (buttonStyle == ButtonStyle.FILLED) {
            return dynamicColors.onPrimary().getArgb(colorScheme);
        }
        return dynamicColors.primary().getArgb(colorScheme);
    }

    private int resolveStrokeColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        return dynamicColors.outline().getArgb(colorScheme);
    }

    private ColorStateList resolveStrokeColors() {
        int enabled = dynamicColors.outline().getArgb(colorScheme);
        int disabled = applyAlphaFraction(
                dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        return new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{disabled, enabled});
    }

    private ColorStateList resolveContainerColors() {
        int enabled = resolveEnabledContainerColor();
        int disabled = applyAlphaFraction(
                dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        return new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{disabled, enabled});
    }

    private ColorStateList resolveTextColors() {
        int enabled = resolveEnabledTextColor();
        int disabled = applyAlphaFraction(
                dynamicColors.onSurface().getArgb(colorScheme), DISABLED_TEXT_ALPHA);
        return new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{disabled, enabled});
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
