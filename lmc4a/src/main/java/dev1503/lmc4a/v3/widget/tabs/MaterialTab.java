package dev1503.lmc4a.v3.widget.tabs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialTab extends LinearLayout {

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float ICON_SIZE_DP = 24.0f;
    private static final float ICON_LABEL_GAP_DP = 2.0f;
    private static final float HORIZONTAL_PADDING_DP = 16.0f;
    private static final float LABEL_TEXT_SIZE_SP = 14.0f;
    private static final float ICON_LABEL_TEXT_SIZE_SP = 12.0f;
    private static final float DISABLED_ALPHA = 0.38f;
    private static final float PRESSED_OVERLAY_ALPHA = 0.10f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final ImageView iconView;
    private final TextView labelView;
    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DynamicScheme colorScheme = publicColorScheme;
    private Icon icon;
    private int activeColor;
    private int inactiveColor;
    private boolean tabSelected;

    public MaterialTab(Context context) {
        this(context, null);
    }

    public MaterialTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setClickable(true);
        setFocusable(true);
        setMinimumWidth((int) (dp(HORIZONTAL_PADDING_DP * 2.0f) + 0.5f));

        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iconView.setVisibility(GONE);

        labelView = new TextView(context);
        labelView.setSingleLine(true);
        labelView.setEllipsize(TextUtils.TruncateAt.END);
        labelView.setGravity(Gravity.CENTER);
        labelView.setTextSize(LABEL_TEXT_SIZE_SP);
        labelView.setVisibility(GONE);

        int iconSize = (int) (dp(ICON_SIZE_DP) + 0.5f);
        addView(iconView, new LayoutParams(iconSize, iconSize));
        addView(labelView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        activeColor = resolveActiveColor();
        inactiveColor = resolveInactiveColor();
        applyContent();
        applyRipple();
        applyColors();
    }

    public void setText(CharSequence text) {
        labelView.setText(text);
        applyContent();
        applyColors();
    }

    public CharSequence getText() {
        return labelView.getText();
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        iconView.setImageDrawable(icon == null ? null : icon.resolve(getContext()));
        applyContent();
        applyColors();
    }

    public Icon getIcon() {
        return icon;
    }

    public boolean isTabSelected() {
        return tabSelected;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        applyColors();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && isEnabled() && isPressed()) {
            overlayPaint.setColor(applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA));
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
        }
    }

    void setTabSelected(boolean tabSelected) {
        if (this.tabSelected != tabSelected) {
            this.tabSelected = tabSelected;
            applyColors();
        }
    }

    void applyTabAppearance(DynamicScheme colorScheme, int activeColor, int inactiveColor) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        this.activeColor = activeColor;
        this.inactiveColor = inactiveColor;
        applyRipple();
        applyColors();
    }

    int getLabelWidth() {
        return labelView.getMeasuredWidth();
    }

    private void applyContent() {
        boolean hasIcon = icon != null;
        boolean hasLabel = getText() != null && getText().length() > 0;
        iconView.setVisibility(hasIcon ? VISIBLE : GONE);
        labelView.setVisibility(hasLabel ? VISIBLE : GONE);
        labelView.setTextSize(hasIcon ? ICON_LABEL_TEXT_SIZE_SP : LABEL_TEXT_SIZE_SP);

        LayoutParams iconParams = (LayoutParams) iconView.getLayoutParams();
        int iconSize = (int) (dp(ICON_SIZE_DP) + 0.5f);
        iconParams.width = iconSize;
        iconParams.height = iconSize;
        iconParams.bottomMargin = hasIcon && hasLabel ? (int) (dp(ICON_LABEL_GAP_DP) + 0.5f) : 0;
        iconView.setLayoutParams(iconParams);
        requestLayout();
    }

    private void applyRipple() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorDrawable mask = new ColorDrawable(Color.WHITE);
            setBackground(new RippleDrawable(
                    ColorStateList.valueOf(resolveRippleColor()), null, mask));
        } else {
            setBackground(null);
        }
    }

    private void applyColors() {
        int color = resolveContentColor();
        labelView.setTextColor(color);
        Drawable drawable = iconView.getDrawable();
        if (drawable != null) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private int resolveContentColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        return tabSelected ? activeColor : inactiveColor;
    }

    private int resolveActiveColor() {
        return dynamicColors.primary().getArgb(colorScheme);
    }

    private int resolveInactiveColor() {
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveRippleColor() {
        return applyAlphaFraction(
                dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA);
    }

    private static int applyAlphaFraction(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0f, Math.min(1f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
