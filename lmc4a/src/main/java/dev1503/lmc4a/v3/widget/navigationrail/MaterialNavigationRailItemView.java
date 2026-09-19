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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialNavigationRailItemView extends CompoundButton {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

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

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private NavigationRailLabelVisibilityMode labelVisibilityMode = NavigationRailLabelVisibilityMode.AUTO;
    private Icon icon;
    private Drawable iconDrawable;
    private MaterialNavigationRailItem data;
    private float checkProgress;
    private SpringSimulation checkSpring;
    private ValueAnimator checkAnimator;
    private long lastFrameTime;
    private RippleDrawable rippleDrawable;
    private Drawable rippleMask;
    private final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF lastRippleRect = new RectF();

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
        this.colorScheme = colorScheme;
        updateRippleColor();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
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

    public void setItemData(MaterialNavigationRailItem data) {
        this.data = data;
    }

    public MaterialNavigationRailItem getItemData() {
        return data;
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

        int indicatorColor = resolveIndicatorColor();
        int foregroundColor = resolveForegroundColor();

        RectF areaRect = computeIconAreaRect();
        float cornerRadius = areaRect.height() / 2f;

        float scale = Math.max(0f, checkProgress);
        if (scale > 0f && Color.alpha(indicatorColor) > 0) {
            float w = areaRect.width() * scale;
            RectF indicator = new RectF(
                    areaRect.centerX() - w / 2f, areaRect.top,
                    areaRect.centerX() + w / 2f, areaRect.bottom);
            paint.setColor(applyAlpha(indicatorColor, checkProgress));
            canvas.drawRoundRect(indicator, cornerRadius * scale, cornerRadius * scale, paint);
        }

        if (iconDrawable != null) {
            float iconSize = dp(ICON_SIZE_DP);
            int left = (int) (areaRect.centerX() - iconSize / 2f);
            int top = (int) (areaRect.centerY() - iconSize / 2f);
            iconDrawable.setBounds(left, top, (int) (left + iconSize + 0.5f), (int) (top + iconSize + 0.5f));
            applyIconTint(iconDrawable, foregroundColor);
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
        if (rect.equals(lastRippleRect)) {
            return;
        }
        lastRippleRect.set(rect);
        if (rippleDrawable == null) {
            rippleMask = new PillMask();
            rippleDrawable = new RippleDrawable(
                    ColorStateList.valueOf(resolveRippleColor()), null, rippleMask);
            setBackground(rippleDrawable);
        } else {
            rippleDrawable.setDrawableByLayerId(android.R.id.mask, rippleMask);
        }
        updateRippleColor();
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

        paint.setTextSize(sp(LABEL_TEXT_SIZE_SP));
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

    private int resolveIndicatorColor() {
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
        if (isChecked()) {
            return dynamicColors.onSurface().getArgb(colorScheme);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private void applyIconTint(Drawable drawable, int color) {
        if (icon == null) {
            return;
        }
        int type = icon.getType();
        if (type == Icon.TYPE_RES_ID || type == Icon.TYPE_DRAWABLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable.setTint(color);
            } else {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
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
}