package dev1503.lmc4a.v3.widget.cardview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;

import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialCardView extends LinearLayout {

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float DEFAULT_CORNER_RADIUS_DP = 12.0f;
    private static final float DEFAULT_ELEVATION_DP = 0.0f;
    private static final float DEFAULT_STROKE_WIDTH_DP = 0.0f;
    private static final float PRESSED_OVERLAY_ALPHA = 0.10f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private final GradientDrawable contentDrawable = new GradientDrawable();
    private final GradientDrawable maskDrawable = new GradientDrawable();

    private DynamicScheme colorScheme = publicColorScheme;
    private float cornerRadius;
    private int containerColor;
    private boolean hasContainerColor;
    private int strokeColor;
    private boolean hasStrokeColor;
    private float strokeWidthDp;
    private boolean hasStrokeWidthDp;
    private float elevationDp;
    private boolean hasElevationDp;

    public MaterialCardView(Context context) {
        this(context, null);
    }

    public MaterialCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cornerRadius = dp(DEFAULT_CORNER_RADIUS_DP);
        containerColor = dynamicColors.surfaceContainerLow().getArgb(colorScheme);
        strokeColor = dynamicColors.outline().getArgb(colorScheme);
        strokeWidthDp = DEFAULT_STROKE_WIDTH_DP;
        elevationDp = DEFAULT_ELEVATION_DP;
        updateShape();
    }

    public void setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadius = dp(cornerRadiusDp);
        updateShape();
    }

    public float getCornerRadiusDp() {
        return cornerRadius / getResources().getDisplayMetrics().density;
    }

    public void clearCornerRadiusDp() {
        setCornerRadiusDp(DEFAULT_CORNER_RADIUS_DP);
    }

    public void setContainerColor(int containerColor) {
        this.containerColor = containerColor;
        this.hasContainerColor = true;
        updateShape();
    }

    public int getContainerColor() {
        return resolveContainerColor();
    }

    public boolean hasContainerColor() {
        return hasContainerColor;
    }

    public void clearContainerColor() {
        this.hasContainerColor = false;
        this.containerColor = dynamicColors.surfaceContainerLow().getArgb(colorScheme);
        updateShape();
    }

    public void setElevationDp(float elevationDp) {
        this.elevationDp = Math.max(0f, elevationDp);
        this.hasElevationDp = true;
        applyElevation();
    }

    public float getElevationDp() {
        return hasElevationDp ? elevationDp : DEFAULT_ELEVATION_DP;
    }

    public boolean hasElevationDp() {
        return hasElevationDp;
    }

    public void clearElevationDp() {
        this.hasElevationDp = false;
        this.elevationDp = DEFAULT_ELEVATION_DP;
        applyElevation();
    }

    public void setStrokeWidthDp(float strokeWidthDp) {
        this.strokeWidthDp = Math.max(0f, strokeWidthDp);
        this.hasStrokeWidthDp = true;
        updateShape();
    }

    public float getStrokeWidthDp() {
        return hasStrokeWidthDp ? strokeWidthDp : DEFAULT_STROKE_WIDTH_DP;
    }

    public boolean hasStrokeWidthDp() {
        return hasStrokeWidthDp;
    }

    public void clearStrokeWidthDp() {
        this.hasStrokeWidthDp = false;
        this.strokeWidthDp = DEFAULT_STROKE_WIDTH_DP;
        updateShape();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        this.hasStrokeColor = true;
        updateShape();
    }

    public int getStrokeColor() {
        return resolveStrokeColor();
    }

    public boolean hasStrokeColor() {
        return hasStrokeColor;
    }

    public void clearStrokeColor() {
        this.hasStrokeColor = false;
        this.strokeColor = dynamicColors.outline().getArgb(colorScheme);
        updateShape();
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        this.hasContainerColor = false;
        this.containerColor = dynamicColors.surfaceContainerLow().getArgb(this.colorScheme);
        this.hasStrokeColor = false;
        this.strokeColor = dynamicColors.outline().getArgb(this.colorScheme);
        updateShape();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    private int resolveContainerColor() {
        if (hasContainerColor) {
            return containerColor;
        }
        return dynamicColors.surfaceContainerLow().getArgb(colorScheme);
    }

    private int resolveStrokeColor() {
        if (hasStrokeColor) {
            return strokeColor;
        }
        return dynamicColors.outline().getArgb(colorScheme);
    }

    private void applyElevation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(dp(elevationDp));
        }
        invalidate();
    }

    private void updateShape() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
                    outline.setAlpha(1.0f);
                }
            });
            setClipToOutline(true);
        }
        refreshBackground();
        invalidate();
    }

    private void refreshBackground() {
        int resolvedContainerColor = resolveContainerColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isClickable()) {
            contentDrawable.setCornerRadius(cornerRadius);
            contentDrawable.setColor(resolvedContainerColor);
            applyStroke(contentDrawable);
            maskDrawable.setCornerRadius(cornerRadius);
            maskDrawable.setColor(Color.WHITE);
            setBackground(new RippleDrawable(
                    ColorStateList.valueOf(
                            applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA)),
                    contentDrawable,
                    maskDrawable));
        } else {
            GradientDrawable background = new GradientDrawable();
            background.setCornerRadius(cornerRadius);
            background.setColor(resolvedContainerColor);
            applyStroke(background);
            setBackground(background);
        }
    }

    private void applyStroke(GradientDrawable drawable) {
        if (strokeWidthDp > 0f) {
            drawable.setStroke((int) (dp(strokeWidthDp) + 0.5f), resolveStrokeColor());
        } else {
            drawable.setStroke(0, Color.TRANSPARENT);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(listener);
        refreshBackground();
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        refreshBackground();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                && isEnabled() && isClickable() && isPressed()) {
            paint.setColor(applyAlpha(
                    dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA));
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    private static int applyAlpha(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0f, Math.min(1f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
