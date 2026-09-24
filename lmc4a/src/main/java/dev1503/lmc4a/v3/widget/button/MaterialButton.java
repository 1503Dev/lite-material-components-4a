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
    private static final float DEFAULT_RIPPLE_ALPHA = 0.10f;
    private static final float ELEVATED_ELEVATION_DP = 1.0f;
    private static final float DEFAULT_STROKE_WIDTH_DP = 1.0f;
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
    protected boolean hasIconColor;
    protected int iconColor;
    protected boolean hasContainerColor;
    protected int containerColor;
    protected boolean hasContentColor;
    protected int contentColor;
    protected boolean hasStrokeColor;
    protected int strokeColor;
    protected boolean hasRippleColor;
    protected int rippleColor;
    protected float strokeWidthDp = DEFAULT_STROKE_WIDTH_DP;
    protected boolean hasElevationDp;
    protected float elevationDp;
    protected boolean hasIconSizeDp;
    protected float iconSizeDp;
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
        contentDrawable.setCornerRadius(cornerRadius);
        maskDrawable.setCornerRadius(cornerRadius);
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
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        clearColorOverrides();
        refreshColorScheme();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void clearColorOverrides() {
        hasContainerColor = false;
        hasContentColor = false;
        hasIconColor = false;
        hasStrokeColor = false;
        hasRippleColor = false;
    }

    public void setStyle(ButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle == null ? ButtonStyle.FILLED : buttonStyle;
        refreshColorScheme();
    }

    protected ButtonStyle resolveButtonStyle() {
        return buttonStyle;
    }

    protected float resolveVerticalInset() {
        return resolveButtonStyle() == ButtonStyle.ICON ? 0.0f : verticalInset;
    }

    public ButtonStyle getStyle() {
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

    public void setContainerColor(int color) {
        this.containerColor = color;
        this.hasContainerColor = true;
        refreshColorScheme();
    }

    public int getContainerColor() {
        return resolveContainerColor();
    }

    public boolean hasContainerColor() {
        return hasContainerColor;
    }

    public void clearContainerColor() {
        this.hasContainerColor = false;
        refreshColorScheme();
    }

    public void setContentColor(int color) {
        this.contentColor = color;
        this.hasContentColor = true;
        refreshColorScheme();
    }

    public int getContentColor() {
        return resolveContentColor();
    }

    public boolean hasContentColor() {
        return hasContentColor;
    }

    public void clearContentColor() {
        this.hasContentColor = false;
        refreshColorScheme();
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

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
        this.hasIconColor = true;
        invalidate();
    }

    public int getIconColor() {
        return resolveIconColor(isEnabled()
                ? resolveEnabledIconColor()
                : applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ICON_ALPHA));
    }

    public boolean hasIconColor() {
        return hasIconColor;
    }

    public void clearIconColor() {
        this.hasIconColor = false;
        invalidate();
    }

    public void setIconSizeDp(float iconSizeDp) {
        this.iconSizeDp = iconSizeDp;
        this.hasIconSizeDp = true;
        requestLayout();
        invalidate();
    }

    public float getIconSizeDp() {
        return resolveIconSizeDp();
    }

    public boolean hasIconSizeDp() {
        return hasIconSizeDp;
    }

    public void clearIconSizeDp() {
        this.hasIconSizeDp = false;
        requestLayout();
        invalidate();
    }

    protected float resolveIconSizeDp() {
        return hasIconSizeDp ? iconSizeDp : resolveDefaultIconSizeDp();
    }

    protected float resolveDefaultIconSizeDp() {
        return hasText() ? ICON_WITH_LABEL_SIZE_DP : ICON_ONLY_SIZE_DP;
    }

    protected boolean hasText() {
        return getText() != null && getText().length() > 0;
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

    public void clearCornerRadiusDp() {
        setCornerRadiusDp(DEFAULT_CORNER_RADIUS_DP);
    }

    public void setStrokeWidthDp(float strokeWidthDp) {
        this.strokeWidthDp = strokeWidthDp;
        refreshContainerDrawable();
        invalidate();
    }

    public float getStrokeWidthDp() {
        return strokeWidthDp;
    }

    public void clearStrokeWidthDp() {
        setStrokeWidthDp(DEFAULT_STROKE_WIDTH_DP);
    }

    public void setStrokeColor(int color) {
        this.strokeColor = color;
        this.hasStrokeColor = true;
        refreshContainerDrawable();
        invalidate();
    }

    public int getStrokeColor() {
        return resolveStrokeColor();
    }

    public boolean hasStrokeColor() {
        return hasStrokeColor;
    }

    public void clearStrokeColor() {
        this.hasStrokeColor = false;
        refreshContainerDrawable();
        invalidate();
    }

    public void setElevationDp(float elevationDp) {
        this.elevationDp = elevationDp;
        this.hasElevationDp = true;
        applyElevation();
        invalidate();
    }

    public float getElevationDp() {
        return resolveElevationDp();
    }

    public boolean hasElevationDp() {
        return hasElevationDp;
    }

    public void clearElevationDp() {
        this.hasElevationDp = false;
        applyElevation();
        invalidate();
    }

    protected float resolveElevationDp() {
        return hasElevationDp ? elevationDp : resolveDefaultElevationDp();
    }

    protected float resolveDefaultElevationDp() {
        return resolveButtonStyle() == ButtonStyle.ELEVATED ? ELEVATED_ELEVATION_DP : 0.0f;
    }

    protected void applyElevation() {
        float elevationDp = resolveElevationDp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(dp(elevationDp));
        } else {
            paint.setShadowLayer(
                    dp(elevationDp > 0.0f ? ELEVATED_SHADOW_RADIUS_DP : 0.0f),
                    0.0f,
                    dp(elevationDp > 0.0f ? ELEVATED_SHADOW_OFFSET_DP : 0.0f),
                    applyAlphaFraction(
                            dynamicColors.shadow().getArgb(colorScheme),
                            ELEVATED_SHADOW_ALPHA));
        }
    }

    public void setRippleColor(int color) {
        this.rippleColor = color;
        this.hasRippleColor = true;
        applyBackground();
        invalidate();
    }

    public int getRippleColor() {
        return resolveRippleColor();
    }

    public boolean hasRippleColor() {
        return hasRippleColor;
    }

    public void clearRippleColor() {
        this.hasRippleColor = false;
        applyBackground();
        invalidate();
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
            width += (int) (dp(resolveIconSizeDp()) + dp(ICON_LABEL_GAP_DP) + 0.5f);
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
        int iconFg = resolveIconColor(fg);

        if (!hasText) {
            float iconSize = dp(resolveIconSizeDp());
            drawIcon(canvas, cx - iconSize / 2f, cy, iconSize, iconFg);
            return;
        }

        setupIconTextPaint();
        String s = text.toString();
        float iconSize = dp(resolveIconSizeDp());
        float labelWidth = paint.measureText(s);
        float unitWidth = iconSize + dp(ICON_LABEL_GAP_DP) + labelWidth;
        float availLeft = getPaddingLeft();
        float availWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        float startX = availLeft + Math.max(0f, (availWidth - unitWidth) / 2f);

        drawIcon(canvas, startX, cy, iconSize, iconFg);
        paint.setColor(fg);
        float baseline = cy - (paint.ascent() + paint.descent()) / 2f;
        canvas.drawText(s, startX + iconSize + dp(ICON_LABEL_GAP_DP), baseline, paint);
    }

    protected int resolveIconColor(int fallbackColor) {
        if (!hasIconColor) {
            return fallbackColor;
        }
        return isEnabled() ? iconColor : applyAlphaFraction(iconColor, DISABLED_ICON_ALPHA);
    }

    protected int resolveEnabledIconColor() {
        return resolveEnabledTextColor();
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
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(resolveContainerColor());
        float elevationDp = resolveElevationDp();
        if (elevationDp > 0.0f) {
            float scale = Math.min(elevationDp, ELEVATED_ELEVATION_DP * 3.0f);
            paint.setShadowLayer(
                    dp(ELEVATED_SHADOW_RADIUS_DP * scale),
                    0.0f,
                    dp(ELEVATED_SHADOW_OFFSET_DP * scale),
                    applyAlphaFraction(
                            dynamicColors.shadow().getArgb(colorScheme),
                            ELEVATED_SHADOW_ALPHA));
        } else {
            paint.clearShadowLayer();
        }
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        paint.clearShadowLayer();
        if (isPressed()) {
            paint.setColor(applyAlphaFraction(resolveEnabledTextColor(), DEFAULT_RIPPLE_ALPHA));
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }
        if (resolveButtonStyle() == ButtonStyle.OUTLINED) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(strokeWidthDp));
            paint.setColor(resolveStrokeColor());
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (hasStrokeColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            applyBackground();
        }
        invalidate();
    }

    protected void refreshColorScheme() {
        if (colorScheme == null) {
            colorScheme = Imc.publicColorScheme;
        }
        setTextColor(resolveTextColors());
        syncTextPaint();
        applyElevation();
        applyBackground();
        setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        int horizontalPadding = (int) (dp(DEFAULT_HORIZONTAL_PADDING_DP) + 0.5f);
        setPadding(horizontalPadding, 0, horizontalPadding, 0);
        invalidate();
    }

    protected void applyBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            applyRippleBackground();
            invalidateOutline();
        } else {
            setBackground(null);
            setLayerType(resolveElevationDp() > 0.0f
                    ? View.LAYER_TYPE_SOFTWARE : View.LAYER_TYPE_NONE, null);
        }
    }

    private void refreshContainerDrawable() {
        contentDrawable.setCornerRadius(cornerRadius);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentDrawable.setColor(resolveContainerColors());
            if (resolveButtonStyle() == ButtonStyle.OUTLINED) {
                contentDrawable.setStroke((int) (dp(strokeWidthDp) + 0.5f), resolveStrokeColors());
            } else {
                contentDrawable.setStroke(0, Color.TRANSPARENT);
            }
        } else {
            contentDrawable.setColor(resolveContainerColor());
        }
    }

    protected void applyRippleBackground() {
        refreshContainerDrawable();
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
        if (hasRippleColor) {
            return rippleColor;
        }
        return applyAlphaFraction(resolveEnabledTextColor(), DEFAULT_RIPPLE_ALPHA);
    }

    protected int resolveContainerColor() {
        if (hasContainerColor) {
            if (!isEnabled()) {
                return applyAlphaFraction(containerColor, DISABLED_CONTAINER_ALPHA);
            }
            return containerColor;
        }
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

    protected int resolveContentColor() {
        return hasContentColor ? contentColor : resolveEnabledTextColor();
    }

    protected int resolveEnabledTextColor() {
        if (hasContentColor) {
            return contentColor;
        }
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
            if (hasStrokeColor) {
                return applyAlphaFraction(strokeColor, DISABLED_CONTAINER_ALPHA);
            }
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        return hasStrokeColor ? strokeColor : dynamicColors.outline().getArgb(colorScheme);
    }

    private ColorStateList resolveStrokeColors() {
        int enabled = hasStrokeColor
                ? strokeColor
                : dynamicColors.outline().getArgb(colorScheme);
        int disabled = hasStrokeColor
                ? applyAlphaFraction(strokeColor, DISABLED_CONTAINER_ALPHA)
                : applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        return new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{disabled, enabled});
    }

    protected ColorStateList resolveContainerColors() {
        int enabled = hasContainerColor ? containerColor : resolveEnabledContainerColor();
        int disabled = hasContainerColor
                ? applyAlphaFraction(containerColor, DISABLED_CONTAINER_ALPHA)
                : applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        return new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{disabled, enabled});
    }

    protected ColorStateList resolveTextColors() {
        int enabled = resolveContentColor();
        int disabled = hasContentColor
                ? applyAlphaFraction(contentColor, DISABLED_TEXT_ALPHA)
                : applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_TEXT_ALPHA);
        return new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{disabled, enabled});
    }

    protected void syncTextPaint() {
        paint.setTextSize(getTextSize());
        Typeface typeface = getTypeface();
        paint.setTypeface(typeface == null ? Typeface.DEFAULT : typeface);
        paint.setTextAlign(Paint.Align.LEFT);
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
