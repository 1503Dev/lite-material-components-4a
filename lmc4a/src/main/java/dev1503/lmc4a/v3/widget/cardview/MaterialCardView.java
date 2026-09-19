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

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialCardView extends LinearLayout {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float DEFAULT_CORNER_RADIUS_DP = 12.0f;
    private static final float PRESSED_OVERLAY_ALPHA = 0.10f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private final GradientDrawable contentDrawable = new GradientDrawable();
    private final GradientDrawable maskDrawable = new GradientDrawable();

    private DynamicScheme colorScheme = publicColorScheme;
    private float cornerRadius;
    private int containerColor;

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
        updateShape();
    }

    public void setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadius = dp(cornerRadiusDp);
        updateShape();
    }

    public float getCornerRadiusDp() {
        return cornerRadius / getResources().getDisplayMetrics().density;
    }

    public void setCardBackgroundColor(int containerColor) {
        this.containerColor = containerColor;
        updateShape();
    }

    public int getCardBackgroundColor() {
        return containerColor;
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        containerColor = dynamicColors.surfaceContainerLow().getArgb(colorScheme);
        updateShape();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isClickable()) {
            contentDrawable.setCornerRadius(cornerRadius);
            contentDrawable.setColor(containerColor);
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
            background.setColor(containerColor);
            setBackground(background);
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