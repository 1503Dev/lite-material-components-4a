package dev1503.lmc4a.v3.widget.navigationrail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialNavigationRailItemView extends CompoundButton {

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float ICON_AREA_WIDTH_DP = 56.0f;
    private static final float ICON_AREA_HEIGHT_DP = 32.0f;
    private static final float ICON_SIZE_DP = 24.0f;
    private static final float LABEL_GAP_DP = 4.0f;
    private static final float LABEL_TEXT_SIZE_SP = 12.0f;
    private static final float DEFAULT_WIDTH_DP = 98.0f;
    private static final float DEFAULT_HEIGHT_DP = 64.0f;
    private static final float PRESSED_OVERLAY_ALPHA = 0.10f;
    private static final float DISABLED_ALPHA = 0.38f;
    private static final float DISABLED_INDICATOR_ALPHA = 0.12f;
    private static final float ANIM_STIFFNESS = 300f;
    private static final float ANIM_DAMPING = 0.7f;
    private static final float ANIM_DURATION_MS = 500f;

    private static final int EMPTY_COLOR = -1;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint containerPaint = new Paint();

    private NavigationRailLabelVisibilityMode labelVisibilityMode = NavigationRailLabelVisibilityMode.AUTO;
    private Icon icon;
    private Drawable iconDrawable;
    private float iconSizeDp = ICON_SIZE_DP;
    private boolean hasIconSizeDp;
    private float labelTextSizeSp = LABEL_TEXT_SIZE_SP;
    private boolean hasLabelTextSizeSp;
    private NavigationRailItem data;
    private float checkProgress;
    private SpringSimulation checkSpring;
    private ValueAnimator checkAnimator;
    private long lastFrameTime;
    private RippleDrawable rippleDrawable;
    private Drawable rippleMask;
    private ColorDrawable containerDrawable;
    private final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF lastRippleRect = new RectF();

    private int containerColor;
    private boolean hasContainerColor;
    private int indicatorColor;
    private boolean hasIndicatorColor;
    private int iconColor;
    private boolean hasIconColor;
    private int textColor;
    private boolean hasTextColor;
    private float indicatorWidthDp;
    private boolean hasIndicatorWidthDp;
    private float indicatorHeightDp;
    private boolean hasIndicatorHeightDp;

    private boolean initialized;

    public MaterialNavigationRailItemView(Context context) {
        this(context, null);
    }

    public MaterialNavigationRailItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialNavigationRailItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        setButtonDrawable(null);
        setClickable(true);
        setGravity(Gravity.CENTER);
        setPadding(0, 0, 0, 0);
        initialized = true;
        refreshRippleMask();
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        hasContainerColor = false;
        hasIndicatorColor = false;
        hasIconColor = false;
        hasTextColor = false;
        updateRippleColor();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setContainerColor(int containerColor) {
        this.containerColor = containerColor;
        this.hasContainerColor = true;
        invalidate();
    }

    public int getContainerColor() {
        return resolveContainerColor();
    }

    public void clearContainerColor() {
        hasContainerColor = false;
        invalidate();
    }

    public boolean hasContainerColor() {
        return hasContainerColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        this.hasIndicatorColor = true;
        invalidate();
    }

    public int getIndicatorColor() {
        return resolveIndicatorColor();
    }

    public void clearIndicatorColor() {
        hasIndicatorColor = false;
        invalidate();
    }

    public boolean hasIndicatorColor() {
        return hasIndicatorColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
        this.hasIconColor = true;
        invalidate();
    }

    public int getIconColor() {
        return hasIconColor ? iconColor : resolveForegroundColor();
    }

    public void clearIconColor() {
        hasIconColor = false;
        invalidate();
    }

    public boolean hasIconColor() {
        return hasIconColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        this.hasTextColor = true;
        invalidate();
    }

    public int getTextColor() {
        return hasTextColor ? textColor : resolveForegroundColor();
    }

    public void clearTextColor() {
        hasTextColor = false;
        invalidate();
    }

    public boolean hasTextColor() {
        return hasTextColor;
    }

    public void setLabelTextSizeSp(float labelTextSizeSp) {
        this.labelTextSizeSp = labelTextSizeSp;
        this.hasLabelTextSizeSp = true;
        invalidate();
        refreshRippleMask();
    }

    public float getLabelTextSizeSp() {
        return hasLabelTextSizeSp ? labelTextSizeSp : LABEL_TEXT_SIZE_SP;
    }

    public boolean hasLabelTextSizeSp() {
        return hasLabelTextSizeSp;
    }

    public void clearLabelTextSizeSp() {
        hasLabelTextSizeSp = false;
        invalidate();
        refreshRippleMask();
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        hasLabelTextSizeSp = false;
        refreshRippleMask();
    }

    public void setIconSizeDp(float iconSizeDp) {
        this.iconSizeDp = iconSizeDp;
        this.hasIconSizeDp = true;
        invalidate();
    }

    public float getIconSizeDp() {
        return hasIconSizeDp ? iconSizeDp : ICON_SIZE_DP;
    }

    public boolean hasIconSizeDp() {
        return hasIconSizeDp;
    }

    public void clearIconSizeDp() {
        hasIconSizeDp = false;
        invalidate();
    }

    public void setIndicatorWidthDp(float indicatorWidthDp) {
        this.indicatorWidthDp = indicatorWidthDp;
        this.hasIndicatorWidthDp = true;
        invalidate();
    }

    public float getIndicatorWidthDp() {
        return hasIndicatorWidthDp ? indicatorWidthDp : ICON_AREA_WIDTH_DP;
    }

    public boolean hasIndicatorWidthDp() {
        return hasIndicatorWidthDp;
    }

    public void clearIndicatorWidthDp() {
        hasIndicatorWidthDp = false;
        invalidate();
    }

    public void setIndicatorHeightDp(float indicatorHeightDp) {
        this.indicatorHeightDp = indicatorHeightDp;
        this.hasIndicatorHeightDp = true;
        invalidate();
    }

    public float getIndicatorHeightDp() {
        return hasIndicatorHeightDp ? indicatorHeightDp : ICON_AREA_HEIGHT_DP;
    }

    public boolean hasIndicatorHeightDp() {
        return hasIndicatorHeightDp;
    }

    public void clearIndicatorHeightDp() {
        hasIndicatorHeightDp = false;
        invalidate();
    }

    public void setLabelVisibilityMode(NavigationRailLabelVisibilityMode labelVisibilityMode) {
        if (labelVisibilityMode == null) {
            labelVisibilityMode = NavigationRailLabelVisibilityMode.AUTO;
        }
        if (this.labelVisibilityMode != labelVisibilityMode) {
            this.labelVisibilityMode = labelVisibilityMode;
            invalidate();
            refreshRippleMask();
        }
    }

    public NavigationRailLabelVisibilityMode getLabelVisibilityMode() {
        return labelVisibilityMode;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshRippleMask();
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        iconDrawable = icon == null ? null : icon.resolve(getContext());
        if (iconDrawable != null) {
            iconDrawable = iconDrawable.mutate();
        }
        invalidate();
    }

    public Icon getIcon() {
        return icon;
    }

    public void setItemData(NavigationRailItem data) {
        this.data = data;
    }

    public NavigationRailItem getItemData() {
        return data;
    }

    void applyColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        updateRippleColor();
        invalidate();
    }

    @Override
    public void toggle() {
        if (isChecked()) {
            return;
        }
        super.toggle();
    }

    @Override
    public void setChecked(boolean checked) {
        boolean changed = checked != isChecked();
        super.setChecked(checked);
        if (changed) {
            animateCheck(checked);
        } else {
            checkProgress = checked ? 1f : 0f;
            invalidate();
            refreshRippleMask();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveDefault((int) (dp(DEFAULT_WIDTH_DP) + 0.5f), widthMeasureSpec);
        int height = resolveDefault((int) (dp(DEFAULT_HEIGHT_DP) + 0.5f), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        refreshRippleMask();

        int containerColor = resolveContainerColor();
        int indicatorColor = resolveIndicatorColor();
        int foregroundColor = resolveForegroundColor();

        RectF areaRect = computeIconAreaRect();
        float cornerRadius = areaRect.height() / 2f;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                && Color.alpha(containerColor) > 0) {
            containerPaint.setColor(containerColor);
            canvas.drawRect(0f, 0f, getWidth(), getHeight(), containerPaint);
        }

        float scale = Math.max(0f, checkProgress);
        if (scale > 0f && Color.alpha(indicatorColor) > 0) {
            float maxWidth = areaRect.width();
            float indicatorWidth = Math.min(computeIndicatorWidth(), maxWidth) * scale;
            float indicatorHeight = Math.min(computeIndicatorHeight(), areaRect.height());
            float radius = indicatorHeight / 2f;
            RectF indicator = new RectF(
                    areaRect.centerX() - indicatorWidth / 2f, areaRect.centerY() - indicatorHeight / 2f,
                    areaRect.centerX() + indicatorWidth / 2f, areaRect.centerY() + indicatorHeight / 2f);
            paint.setColor(applyAlpha(indicatorColor, checkProgress));
            canvas.drawRoundRect(indicator, radius, radius, paint);
        }

        if (iconDrawable != null) {
            float iconSize = dp(computeIconSizeDp());
            int left = (int) (areaRect.centerX() - iconSize / 2f);
            int top = (int) (areaRect.centerY() - iconSize / 2f);
            iconDrawable.setBounds(left, top, (int) (left + iconSize + 0.5f), (int) (top + iconSize + 0.5f));
            tintDrawable(iconDrawable, foregroundColor);
            iconDrawable.draw(canvas);
        }

        CharSequence text = getText();
        float labelAmount = computeLabelAmount();
        if (labelAmount > 0f && text != null && text.length() > 0) {
            paint.setColor(applyAlpha(foregroundColor, labelAmount));
            paint.setTextAlign(Paint.Align.CENTER);
            float baseline = areaRect.bottom + dp(LABEL_GAP_DP) - paint.ascent();
            canvas.drawText(text.toString(), areaRect.centerX(), baseline, paint);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                && isEnabled() && isPressed() && Color.alpha(indicatorColor) == 0) {
            paint.setColor(applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA));
            canvas.drawRoundRect(areaRect, cornerRadius, cornerRadius, paint);
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
        if (checkAnimator != null) {
            checkAnimator.cancel();
            checkAnimator = null;
        }
    }

    private void refreshRippleMask() {
        if (!initialized || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        RectF rect = computeIconAreaRect();
        if (rippleDrawable != null && rect.equals(lastRippleRect)) {
            updateContainerColor();
            return;
        }
        lastRippleRect.set(rect);
        if (rippleDrawable == null) {
            rippleMask = new PillMask();
            rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(resolveRippleColor()), null, rippleMask);
            containerDrawable = new ColorDrawable(resolveContainerColor());
            LayerDrawable layerDrawable = new LayerDrawable(
                    new Drawable[]{containerDrawable, rippleDrawable});
            setBackground(layerDrawable);
        } else {
            rippleDrawable.setDrawableByLayerId(android.R.id.mask, rippleMask);
        }
        updateContainerColor();
        updateRippleColor();
    }

    private void updateContainerColor() {
        if (containerDrawable != null) {
            containerDrawable.setColor(resolveContainerColor());
        }
    }

    private int resolveRippleColor() {
        return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA);
    }

    private void updateRippleColor() {
        if (rippleDrawable != null) {
            rippleDrawable.setColor(ColorStateList.valueOf(resolveRippleColor()));
        }
    }

    private RectF computeIconAreaRect() {
        float width = getWidth();
        float height = getHeight();

        float areaWidth = dp(ICON_AREA_WIDTH_DP);
        float areaHeight = dp(ICON_AREA_HEIGHT_DP);

        paint.setTextSize(sp(computeLabelTextSizeSp()));
        paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        float labelHeight = paint.descent() - paint.ascent();

        float offsetHidden = Math.max(0f, (height - areaHeight) / 2f);
        float offsetVisible = Math.max(0f,
                (height - (areaHeight + dp(LABEL_GAP_DP) + labelHeight)) / 2f);
        float topOffset = offsetHidden + (offsetVisible - offsetHidden) * computeLabelAmount();
        float cx = width / 2f;
        float areaCenterY = topOffset + areaHeight / 2f;

        return new RectF(cx - areaWidth / 2f, areaCenterY - areaHeight / 2f,
                cx + areaWidth / 2f, areaCenterY + areaHeight / 2f);
    }

    private float computeLabelAmount() {
        switch (labelVisibilityMode) {
            case LABELED:
                return 1f;
            case UNLABELED:
                return 0f;
            case SELECTED:
                return checkProgress;
            case AUTO:
            default: {
                View parent = (View) getParent();
                if (parent instanceof MaterialNavigationRail) {
                    return ((MaterialNavigationRail) parent).getItemCount() < 5 ? 1f : checkProgress;
                }
                return 1f;
            }
        }
    }

    private float computeIconSizeDp() {
        return hasIconSizeDp ? iconSizeDp : ICON_SIZE_DP;
    }

    private float computeLabelTextSizeSp() {
        return hasLabelTextSizeSp ? labelTextSizeSp : LABEL_TEXT_SIZE_SP;
    }

    private float computeIndicatorWidth() {
        return hasIndicatorWidthDp ? dp(indicatorWidthDp) : dp(ICON_AREA_WIDTH_DP);
    }

    private float computeIndicatorHeight() {
        return hasIndicatorHeightDp ? dp(indicatorHeightDp) : dp(ICON_AREA_HEIGHT_DP);
    }

    private int resolveContainerColor() {
        if (hasContainerColor) {
            return containerColor;
        }
        MaterialNavigationRail rail = getParentRail();
        if (rail != null && rail.hasContainerColorOverride()) {
            return rail.peekContainerColor();
        }
        return dynamicColors.surfaceContainer().getArgb(colorScheme);
    }

    private int resolveIndicatorColor() {
        if (hasIndicatorColor) {
            return indicatorColor;
        }
        MaterialNavigationRail rail = getParentRail();
        if (rail != null && rail.hasIndicatorColorOverride()) {
            return rail.peekIndicatorColor();
        }
        if (isChecked() && isEnabled()) {
            return dynamicColors.secondaryContainer().getArgb(colorScheme);
        }
        if (isChecked()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_INDICATOR_ALPHA);
        }
        return Color.TRANSPARENT;
    }

    private int resolveForegroundColor() {
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        MaterialNavigationRail rail = getParentRail();
        int iconColor = hasIconColor ? this.iconColor
                : (rail != null && rail.hasIconColorOverride() ? rail.peekIconColor() : EMPTY_COLOR);
        int textColor = hasTextColor ? this.textColor
                : (rail != null && rail.hasTextColorOverride() ? rail.peekTextColor() : EMPTY_COLOR);
        if (isChecked()) {
            if (iconColor != EMPTY_COLOR) {
                return iconColor;
            }
            if (textColor != EMPTY_COLOR) {
                return textColor;
            }
        } else {
            if (textColor != EMPTY_COLOR) {
                return textColor;
            }
            if (iconColor != EMPTY_COLOR) {
                return iconColor;
            }
        }
        if (isChecked()) {
            return dynamicColors.onSurface().getArgb(colorScheme);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private MaterialNavigationRail getParentRail() {
        View parent = (View) getParent();
        return parent instanceof MaterialNavigationRail ? (MaterialNavigationRail) parent : null;
    }

    private void tintDrawable(Drawable drawable, int color) {
        if (drawable instanceof BitmapDrawable) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private int resolveDefault(int defaultSize, int measureSpec) {
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case View.MeasureSpec.EXACTLY:
                return specSize;
            case View.MeasureSpec.AT_MOST:
                return Math.min(defaultSize, specSize);
            default:
                return defaultSize;
        }
    }

    private static int applyAlpha(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0f, Math.min(1f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }

    private float sp(float valueSp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, valueSp, getResources().getDisplayMetrics());
    }

    private final class PillMask extends Drawable {

        @Override
        public void draw(Canvas canvas) {
            RectF rect = computeIconAreaRect();
            if (rect.width() <= 0f || rect.height() <= 0f) {
                return;
            }
            maskPaint.setColor(Color.WHITE);
            maskPaint.setColorFilter(null);
            float radius = rect.height() / 2f;
            canvas.drawRoundRect(rect, radius, radius, maskPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            maskPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            maskPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private void animateCheck(boolean checked) {
        float target = checked ? 1f : 0f;
        if (checkAnimator != null) {
            checkAnimator.cancel();
            checkAnimator = null;
        }
        if (getWindowToken() == null || checkProgress == target) {
            checkProgress = target;
            invalidate();
            refreshRippleMask();
            return;
        }
        checkSpring = new SpringSimulation(ANIM_STIFFNESS, ANIM_DAMPING);
        checkSpring.setPosition(checkProgress);
        checkSpring.setTarget(target);
        lastFrameTime = System.nanoTime();
        checkAnimator = ValueAnimator.ofFloat(0f, 1f);
        checkAnimator.setDuration((long) ANIM_DURATION_MS);
        checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                delta = Math.min(delta, 0.05f);

                checkProgress = checkSpring.update(delta);
                invalidate();
                refreshRippleMask();

                if (checkSpring.isAtRest()) {
                    animation.cancel();
                }
            }
        });
        checkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (checkAnimator == animation) {
                    checkProgress = target;
                    invalidate();
                    refreshRippleMask();
                }
            }
        });
        checkAnimator.start();
    }
}
