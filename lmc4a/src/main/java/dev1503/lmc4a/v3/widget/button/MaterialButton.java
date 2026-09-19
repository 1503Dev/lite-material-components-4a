package dev1503.lmc4a.v3.widget.button;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
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
import dev1503.lmc4a.v3.widget.ColorVariant;

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
    private static final float ICON_ONLY_SIZE_DP = 24.0f;
    private static final float ICON_BUTTON_SIZE_DP = 40.0f;
    private static final float ICON_WITH_LABEL_SIZE_DP = 18.0f;
    private static final float ICON_LABEL_GAP_DP = 8.0f;
    private static final float DISABLED_ICON_ALPHA = 0.38f;

    protected DynamicScheme colorScheme = publicColorScheme;
    protected ButtonStyle buttonStyle = ButtonStyle.FILLED;
    protected ColorVariant colorVariant = ColorVariant.PRIMARY;
    protected Drawable icon;
    protected final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final RectF rectF = new RectF();
    protected final GradientDrawable contentDrawable = new GradientDrawable();
    protected final GradientDrawable maskDrawable = new GradientDrawable();
    protected final float verticalInset;
    protected final float defaultTotalHeight;
    protected float cornerRadius;

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
                    int inset = (int) (resolveVerticalInset() + 0.5f);
                    outline.setRoundRect(
                            0, inset, view.getWidth(), view.getHeight() - inset, cornerRadius);
                    outline.setAlpha(resolveButtonStyle() == ButtonStyle.ELEVATED ? 1.0f : 0.0f);
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

    protected ButtonStyle resolveButtonStyle() {
        return buttonStyle;
    }

    protected float resolveVerticalInset() {
        return resolveButtonStyle() == ButtonStyle.ICON ? 0.0f : verticalInset;
    }

    public ButtonStyle getButtonStyle() {
        return resolveButtonStyle();
    }

    public void setColorVariant(ColorVariant colorVariant) {
        if (colorVariant == null) {
            colorVariant = ColorVariant.PRIMARY;
        }
        if (this.colorVariant != colorVariant) {
            this.colorVariant = colorVariant;
            refreshColorScheme();
        }
    }

    public ColorVariant getColorVariant() {
        return colorVariant;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
        if (icon != null) {
            icon.mutate();
        }
        requestLayout();
        invalidate();
    }

    public Drawable getIcon() {
        return icon;
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

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

        ButtonStyle style = resolveButtonStyle();
        if (style == ButtonStyle.ICON
                || getText() == null || getText().length() == 0) {
            int size = (int) ((style == ButtonStyle.ICON
                    ? dp(ICON_BUTTON_SIZE_DP) : defaultTotalHeight) + 0.5f);
            int maxReasonAbly = size * 2;
            if (widthMode == View.MeasureSpec.EXACTLY && widthSpecSize > 0
                    && widthSpecSize <= maxReasonAbly) {
                size = Math.max(size, widthSpecSize);
            }
            if (heightMode == View.MeasureSpec.EXACTLY && heightSpecSize > 0
                    && heightSpecSize <= maxReasonAbly) {
                size = Math.max(size, heightSpecSize);
            }
            if (widthMode != View.MeasureSpec.UNSPECIFIED && widthSpecSize > 0) {
                size = Math.min(size, widthSpecSize);
            }
            if (heightMode != View.MeasureSpec.UNSPECIFIED && heightSpecSize > 0) {
                size = Math.min(size, heightSpecSize);
            }
            size = Math.max(size, 1);
            setMeasuredDimension(size, size);
            return;
        }

        int width = resolveMeasuredWidth(widthMeasureSpec);
        if (width != getMeasuredWidth()) {
            super.onMeasure(
                    View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    heightMeasureSpec);
        }

        int height = getMeasuredHeight();
        if (heightMode != View.MeasureSpec.EXACTLY) {
            height = Math.max(height, (int) (defaultTotalHeight + 0.5f));
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
        if (icon != null) {
            width += (int) (dp(ICON_WITH_LABEL_SIZE_DP) + dp(ICON_LABEL_GAP_DP) + 0.5f);
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
            drawPreLollipopContainer(canvas);
        }
        if (icon != null) {
            drawIconAndText(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

    private void drawIconAndText(Canvas canvas) {
        CharSequence text = getText();
        boolean hasText = text != null && text.length() > 0;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        int fg = isEnabled()
                ? resolveEnabledTextColor()
                : applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ICON_ALPHA);

        if (!hasText) {
            drawIcon(canvas, cx - dp(ICON_ONLY_SIZE_DP) / 2f, cy, dp(ICON_ONLY_SIZE_DP), fg);
            return;
        }

        setupIconTextPaint();
        String s = text.toString();
        float iconSize = dp(ICON_WITH_LABEL_SIZE_DP);
        float labelWidth = paint.measureText(s);
        float unitWidth = iconSize + dp(ICON_LABEL_GAP_DP) + labelWidth;
        float availLeft = getPaddingLeft();
        float availWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float startX = availLeft + Math.max(0f, (availWidth - unitWidth) / 2f);

        drawIcon(canvas, startX, cy, iconSize, fg);
        paint.setColor(fg);
        float baseline = cy - (paint.ascent() + paint.descent()) / 2f;
        canvas.drawText(s, startX + iconSize + dp(ICON_LABEL_GAP_DP), baseline, paint);
    }

    private void drawIcon(Canvas canvas, float left, float centerY, float iconSize, int color) {
        if (icon == null) {
            return;
        }
        int l = (int) left;
        int t = (int) (centerY - iconSize / 2f);
        icon.setBounds(l, t, (int) (l + iconSize + 0.5f), (int) (t + iconSize + 0.5f));
        applyIconTint(icon, color);
        icon.draw(canvas);
    }

    private void setupIconTextPaint() {
        paint.setTextSize(getTextSize());
        Typeface typeface = getTypeface();
        paint.setTypeface(typeface == null ? Typeface.DEFAULT : typeface);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    protected void applyIconTint(Drawable drawable, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    protected void drawPreLollipopContainer(Canvas canvas) {
        float inset = resolveVerticalInset();
        rectF.set(0.0f, inset, getWidth(), getHeight() - inset);
        paint.setColor(resolveContainerColor());
        if (resolveButtonStyle() == ButtonStyle.ELEVATED) {
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
        if (resolveButtonStyle() == ButtonStyle.OUTLINED) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(OUTLINE_STROKE_DP));
            paint.setColor(resolveStrokeColor());
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    protected void refreshColorScheme() {
        if (colorScheme == null) {
            colorScheme = Imc.publicColorScheme;
        }
        setTextColor(resolveTextColors());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            applyRippleBackground();
            setElevation(resolveButtonStyle() == ButtonStyle.ELEVATED ? dp(ELEVATED_ELEVATION_DP) : 0.0f);
            invalidateOutline();
        } else {
            setBackground(null);
            setLayerType(resolveButtonStyle() == ButtonStyle.ELEVATED
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

    protected void applyRippleBackground() {
        contentDrawable.setCornerRadius(cornerRadius);
        contentDrawable.setColor(resolveContainerColors());
        if (resolveButtonStyle() == ButtonStyle.OUTLINED) {
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
        int inset = (int) (resolveVerticalInset() + 0.5f);
        layerDrawable.setLayerInset(0, 0, inset, 0, inset);
        setBackground(layerDrawable);
    }

    protected int resolveRippleColor() {
        return applyAlphaFraction(resolveEnabledTextColor(), PRESSED_RIPPLE_ALPHA);
    }

    protected int resolveContainerColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        return resolveEnabledContainerColor();
    }

    protected int resolveEnabledContainerColor() {
        if (resolveButtonStyle() == ButtonStyle.ELEVATED && colorVariant == ColorVariant.PRIMARY) {
            return dynamicColors.surfaceContainerLow().getArgb(colorScheme);
        }
        if (resolveButtonStyle() == ButtonStyle.OUTLINED
                || resolveButtonStyle() == ButtonStyle.TEXT
                || resolveButtonStyle() == ButtonStyle.ICON) {
            return Color.TRANSPARENT;
        }
        return resolveVariantColor();
    }

    protected int resolveEnabledTextColor() {
        if (colorVariant == ColorVariant.PRIMARY
                && (resolveButtonStyle() == ButtonStyle.ELEVATED
                || resolveButtonStyle() == ButtonStyle.OUTLINED
                || resolveButtonStyle() == ButtonStyle.TEXT
                || resolveButtonStyle() == ButtonStyle.ICON)) {
            return dynamicColors.primary().getArgb(colorScheme);
        }
        return resolveOnVariantColor();
    }

    protected int resolveVariantColor() {
        switch (colorVariant) {
            case PRIMARY_CONTAINER:
                return dynamicColors.primaryContainer().getArgb(colorScheme);
            case SECONDARY:
                return dynamicColors.secondary().getArgb(colorScheme);
            case SECONDARY_CONTAINER:
                return dynamicColors.secondaryContainer().getArgb(colorScheme);
            case TERTIARY:
                return dynamicColors.tertiary().getArgb(colorScheme);
            case TERTIARY_CONTAINER:
                return dynamicColors.tertiaryContainer().getArgb(colorScheme);
            default:
                return dynamicColors.primary().getArgb(colorScheme);
        }
    }

    protected int resolveOnVariantColor() {
        switch (colorVariant) {
            case PRIMARY_CONTAINER:
                return dynamicColors.onPrimaryContainer().getArgb(colorScheme);
            case SECONDARY:
                return dynamicColors.onSecondary().getArgb(colorScheme);
            case SECONDARY_CONTAINER:
                return dynamicColors.onSecondaryContainer().getArgb(colorScheme);
            case TERTIARY:
                return dynamicColors.onTertiary().getArgb(colorScheme);
            case TERTIARY_CONTAINER:
                return dynamicColors.onTertiaryContainer().getArgb(colorScheme);
            default:
                return dynamicColors.onPrimary().getArgb(colorScheme);
        }
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

    protected ColorStateList resolveContainerColors() {
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

    protected static int applyAlphaFraction(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * alphaFraction);
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    protected static int darken(int argb, float fraction) {
        int alpha = Color.alpha(argb);
        int r = (int) (Color.red(argb) * (1.0f - fraction));
        int g = (int) (Color.green(argb) * (1.0f - fraction));
        int b = (int) (Color.blue(argb) * (1.0f - fraction));
        return Color.argb(alpha, r, g, b);
    }

    protected float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
