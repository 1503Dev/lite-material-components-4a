package dev1503.lmc4a.v3.widget.chip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.CompoundButton;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialChip extends CompoundButton {

    public interface OnCloseClickListener {
        void onCloseClick(MaterialChip chip);
    }

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float DEFAULT_HEIGHT_DP = 32.0f;
    private static final float DEFAULT_CORNER_RADIUS_DP = 8.0f;
    private static final float DEFAULT_STROKE_WIDTH_DP = 1.0f;
    private static final float DEFAULT_PADDING_DP = 16.0f;
    private static final float COMPACT_PADDING_DP = 8.0f;
    private static final float LEADING_GAP_DP = 8.0f;
    private static final float TRAILING_GAP_DP = 8.0f;
    private static final float ICON_SIZE_DP = 18.0f;
    private static final float CLOSE_ICON_SIZE_DP = 18.0f;
    private static final float CLOSE_TOUCH_SIZE_DP = 24.0f;
    private static final float DEFAULT_TEXT_SIZE_SP = 14.0f;
    private static final float DISABLED_CONTAINER_ALPHA = 0.12f;
    private static final float DISABLED_CONTENT_ALPHA = 0.38f;
    private static final float PRESSED_OVERLAY_ALPHA = 0.10f;
    private static final float ANIM_STIFFNESS = 300.0f;
    private static final float ANIM_DAMPING = 0.7f;
    private static final float ANIM_DURATION_MS = 500.0f;
    private static final float GLYPH_STROKE_WIDTH_DP = 2.0f;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private final Path glyphPath = new Path();
    private final GradientDrawable containerDrawable = new GradientDrawable();
    private final GradientDrawable maskDrawable = new GradientDrawable();

    private ChipStyle style = ChipStyle.ASSIST;
    private float cornerRadius;
    private float strokeWidthDp = DEFAULT_STROKE_WIDTH_DP;

    private boolean checkable;
    private boolean hasCheckableOverride;
    private boolean closeIconShown;
    private boolean hasCloseIconShownOverride;

    private Icon icon;
    private Drawable iconDrawable;
    private Icon closeIcon;
    private Drawable closeIconDrawable;

    private boolean hasContainerColor;
    private int containerColor;
    private boolean hasContentColor;
    private int contentColor;
    private boolean hasIconColor;
    private int iconColor;
    private boolean hasOutlineColor;
    private int outlineColor;
    private boolean hasCloseIconColor;
    private int closeIconColor;

    private float checkProgress;
    private SpringSimulation checkSpring;
    private ValueAnimator checkAnimator;
    private long lastFrameTime;

    private boolean closePressed;
    private float closeCenterX = -1.0f;
    private float closeCenterY = -1.0f;
    private float closeTouchRadius;

    private OnCloseClickListener closeClickListener;

    private boolean initialized;

    public MaterialChip(Context context) {
        this(context, null);
    }

    public MaterialChip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialChip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cornerRadius = dp(DEFAULT_CORNER_RADIUS_DP);
        setBackground(null);
        setButtonDrawable(null);
        setClickable(true);
        setGravity(Gravity.CENTER);
        setPadding(0, 0, 0, 0);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP);
        closeTouchRadius = dp(CLOSE_TOUCH_SIZE_DP) / 2.0f;
        initialized = true;
        refreshBackground();
    }

    public void setStyle(ChipStyle style) {
        ChipStyle resolved = style == null ? ChipStyle.ASSIST : style;
        if (this.style == resolved) {
            return;
        }
        this.style = resolved;
        if (!hasCheckableOverride) {
            checkable = resolved == ChipStyle.FILTER || resolved == ChipStyle.INPUT;
        }
        if (!hasCloseIconShownOverride) {
            closeIconShown = resolved == ChipStyle.INPUT;
        }
        if (!checkable && isChecked()) {
            setChecked(false);
        }
        requestLayout();
        refreshBackground();
        invalidate();
    }

    public ChipStyle getStyle() {
        return style;
    }

    public void setCheckable(boolean checkable) {
        if (this.checkable == checkable && hasCheckableOverride) {
            return;
        }
        this.checkable = checkable;
        this.hasCheckableOverride = true;
        if (!checkable && isChecked()) {
            setChecked(false);
        }
        invalidate();
    }

    public boolean isCheckable() {
        return checkable;
    }

    public boolean hasCheckable() {
        return hasCheckableOverride;
    }

    public void clearCheckable() {
        hasCheckableOverride = false;
        checkable = style == ChipStyle.FILTER || style == ChipStyle.INPUT;
        if (!checkable && isChecked()) {
            setChecked(false);
        }
        invalidate();
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        iconDrawable = icon == null ? null : icon.resolve(getContext());
        if (iconDrawable != null) {
            iconDrawable = iconDrawable.mutate();
        }
        requestLayout();
        invalidate();
    }

    public Icon getIcon() {
        return icon;
    }

    public void clearIcon() {
        setIcon(null);
    }

    public void setCloseIcon(Icon closeIcon) {
        this.closeIcon = closeIcon;
        closeIconDrawable = closeIcon == null ? null : closeIcon.resolve(getContext());
        if (closeIconDrawable != null) {
            closeIconDrawable = closeIconDrawable.mutate();
        }
        requestLayout();
        invalidate();
    }

    public Icon getCloseIcon() {
        return closeIcon;
    }

    public void clearCloseIcon() {
        setCloseIcon(null);
    }

    public void setCloseIconShown(boolean shown) {
        this.hasCloseIconShownOverride = true;
        if (this.closeIconShown == shown) {
            return;
        }
        this.closeIconShown = shown;
        requestLayout();
        invalidate();
    }

    public boolean isCloseIconShown() {
        return closeIconShown;
    }

    public boolean hasCloseIconShown() {
        return hasCloseIconShownOverride;
    }

    public void clearCloseIconShown() {
        hasCloseIconShownOverride = false;
        boolean shown = style == ChipStyle.INPUT;
        if (closeIconShown != shown) {
            closeIconShown = shown;
            requestLayout();
            invalidate();
        }
    }

    public void setOnCloseClickListener(OnCloseClickListener listener) {
        closeClickListener = listener;
    }

    public OnCloseClickListener getOnCloseClickListener() {
        return closeClickListener;
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        clearColorOverrides();
        refreshBackground();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void clearColorOverrides() {
        hasContainerColor = false;
        hasContentColor = false;
        hasIconColor = false;
        hasOutlineColor = false;
        hasCloseIconColor = false;
    }

    public void setContainerColor(int color) {
        this.containerColor = color;
        this.hasContainerColor = true;
        refreshBackground();
        invalidate();
    }

    public int getContainerColor() {
        return resolveContainerColor();
    }

    public boolean hasContainerColor() {
        return hasContainerColor;
    }

    public void clearContainerColor() {
        hasContainerColor = false;
        refreshBackground();
        invalidate();
    }

    public void setContentColor(int color) {
        this.contentColor = color;
        this.hasContentColor = true;
        refreshBackground();
        invalidate();
    }

    public int getContentColor() {
        return resolveContentColor();
    }

    public boolean hasContentColor() {
        return hasContentColor;
    }

    public void clearContentColor() {
        hasContentColor = false;
        refreshBackground();
        invalidate();
    }

    public void setIconColor(int color) {
        this.iconColor = color;
        this.hasIconColor = true;
        invalidate();
    }

    public int getIconColor() {
        return resolveIconColor();
    }

    public boolean hasIconColor() {
        return hasIconColor;
    }

    public void clearIconColor() {
        hasIconColor = false;
        invalidate();
    }

    public void setOutlineColor(int color) {
        this.outlineColor = color;
        this.hasOutlineColor = true;
        refreshBackground();
        invalidate();
    }

    public int getOutlineColor() {
        return resolveOutlineColor();
    }

    public boolean hasOutlineColor() {
        return hasOutlineColor;
    }

    public void clearOutlineColor() {
        hasOutlineColor = false;
        refreshBackground();
        invalidate();
    }

    public void setCloseIconColor(int color) {
        this.closeIconColor = color;
        this.hasCloseIconColor = true;
        invalidate();
    }

    public int getCloseIconColor() {
        return resolveCloseIconColor();
    }

    public boolean hasCloseIconColor() {
        return hasCloseIconColor;
    }

    public void clearCloseIconColor() {
        hasCloseIconColor = false;
        invalidate();
    }

    public void setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadius = dp(cornerRadiusDp);
        refreshBackground();
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
        refreshBackground();
        invalidate();
    }

    public float getStrokeWidthDp() {
        return strokeWidthDp;
    }

    public void clearStrokeWidthDp() {
        setStrokeWidthDp(DEFAULT_STROKE_WIDTH_DP);
    }

    @Override
    public void toggle() {
        if (!checkable) {
            return;
        }
        super.toggle();
    }

    @Override
    public void setChecked(boolean checked) {
        if (!initialized) {
            super.setChecked(checked);
            return;
        }
        if (checked && !checkable) {
            return;
        }
        boolean changed = checked != isChecked();
        super.setChecked(checked);
        if (changed) {
            requestLayout();
            refreshBackground();
            animateCheck(checked);
        } else {
            checkProgress = checked ? 1.0f : 0.0f;
            refreshBackground();
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean changed = enabled != isEnabled();
        super.setEnabled(enabled);
        if (initialized && changed) {
            refreshBackground();
            invalidate();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (initialized) {
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        if (initialized) {
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        if (initialized) {
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = resolveSize((int) (dp(DEFAULT_HEIGHT_DP) + 0.5f), heightMeasureSpec);
        int width = resolveSize((int) (computeContentWidth() + 0.5f), widthMeasureSpec);
        setMeasuredDimension(Math.max(width, 1), Math.max(height, 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawPreLollipopContainer(canvas);
        }
        drawContent(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (closeIconShown && isEnabled() && closeCenterX >= 0.0f) {
            float dx = event.getX() - closeCenterX;
            float dy = event.getY() - closeCenterY;
            boolean inside = Math.sqrt(dx * dx + dy * dy) <= closeTouchRadius;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (inside) {
                        closePressed = true;
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (closePressed && !inside) {
                        closePressed = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (closePressed && inside) {
                        closePressed = false;
                        performCloseClick();
                        return true;
                    }
                    closePressed = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    closePressed = false;
                    break;
                default:
                    break;
            }
            if (closePressed) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (checkAnimator != null) {
            ValueAnimator animator = checkAnimator;
            checkAnimator = null;
            animator.cancel();
        }
    }

    private void drawContent(Canvas canvas) {
        float padLeft = hasLeadingSlot() ? dp(COMPACT_PADDING_DP) : dp(DEFAULT_PADDING_DP);
        float padRight = closeIconShown ? dp(COMPACT_PADDING_DP) : dp(DEFAULT_PADDING_DP);

        paint.setTextSize(getTextSize());
        Typeface typeface = getTypeface();
        paint.setTypeface(typeface == null ? Typeface.DEFAULT : typeface);

        CharSequence text = getText();
        boolean hasText = text != null && text.length() > 0;
        String label = hasText ? text.toString() : "";
        float textWidth = hasText ? paint.measureText(label) : 0.0f;

        boolean hasLeading = hasLeadingSlot();
        float leadingSize = hasLeading ? dp(ICON_SIZE_DP) : 0.0f;
        float closeSize = closeIconShown ? dp(CLOSE_ICON_SIZE_DP) : 0.0f;

        float contentWidth = 0.0f;
        if (hasLeading) {
            contentWidth += leadingSize + (hasText ? dp(LEADING_GAP_DP) : 0.0f);
        }
        contentWidth += textWidth;
        if (closeIconShown) {
            if (hasLeading || hasText) {
                contentWidth += dp(TRAILING_GAP_DP);
            }
            contentWidth += closeSize;
        }

        float available = getWidth() - padLeft - padRight;
        float x = padLeft + Math.max(0.0f, (available - contentWidth) / 2.0f);
        float centerY = getHeight() / 2.0f;

        if (hasLeading) {
            float leadingCenterX = x + leadingSize / 2.0f;
            if (supportsSelection() && checkProgress > 0.0f) {
                drawCheckGlyph(canvas, leadingCenterX, centerY, leadingSize,
                        applyAlpha(resolveContentColor(), checkProgress));
            } else if (iconDrawable != null) {
                drawIcon(canvas, x, centerY, leadingSize, resolveIconColor());
            }
            x += leadingSize + dp(LEADING_GAP_DP);
        }

        if (hasText) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(resolveContentColor());
            float baseline = centerY - (paint.ascent() + paint.descent()) / 2.0f;
            canvas.drawText(label, x, baseline, paint);
            x += textWidth;
            if (closeIconShown) {
                x += dp(TRAILING_GAP_DP);
            }
        }

        if (closeIconShown) {
            closeCenterX = x + closeSize / 2.0f;
            closeCenterY = centerY;
            int closeColor = resolveCloseIconColor();
            if (closeIconDrawable != null) {
                drawIcon(canvas, x, centerY, closeSize, closeColor);
            } else {
                drawCloseGlyph(canvas, closeCenterX, centerY, closeSize, closeColor);
            }
        } else {
            closeCenterX = -1.0f;
        }
    }

    private void drawPreLollipopContainer(Canvas canvas) {
        rectF.set(0.0f, 0.0f, getWidth(), getHeight());
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(resolveContainerColor());
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        if (isOutlinedState()) {
            float inset = dp(strokeWidthDp) / 2.0f;
            rectF.set(inset, inset, getWidth() - inset, getHeight() - inset);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(strokeWidthDp));
            paint.setColor(resolveOutlineColor());
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }

        if (isEnabled() && isPressed()) {
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), PRESSED_OVERLAY_ALPHA));
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void refreshBackground() {
        if (!initialized) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setBackground(null);
            return;
        }
        containerDrawable.setCornerRadius(cornerRadius);
        containerDrawable.setColor(resolveContainerColor());
        if (isOutlinedState()) {
            containerDrawable.setStroke((int) (dp(strokeWidthDp) + 0.5f), resolveOutlineColor());
        } else {
            containerDrawable.setStroke(0, Color.TRANSPARENT);
        }
        maskDrawable.setCornerRadius(cornerRadius);
        maskDrawable.setColor(Color.WHITE);
        RippleDrawable ripple = new RippleDrawable(
                ColorStateList.valueOf(resolveRippleColor()), containerDrawable, maskDrawable);
        setBackground(new LayerDrawable(new Drawable[]{ripple}));
    }

    private float computeContentWidth() {
        paint.setTextSize(getTextSize());
        Typeface typeface = getTypeface();
        paint.setTypeface(typeface == null ? Typeface.DEFAULT : typeface);

        CharSequence text = getText();
        boolean hasText = text != null && text.length() > 0;
        float textWidth = hasText ? paint.measureText(text.toString()) : 0.0f;

        boolean hasLeading = hasLeadingSlot();
        float padLeft = hasLeading ? dp(COMPACT_PADDING_DP) : dp(DEFAULT_PADDING_DP);
        float padRight = closeIconShown ? dp(COMPACT_PADDING_DP) : dp(DEFAULT_PADDING_DP);

        float width = padLeft + padRight;
        if (hasLeading) {
            width += dp(ICON_SIZE_DP) + (hasText ? dp(LEADING_GAP_DP) : 0.0f);
        }
        width += textWidth;
        if (closeIconShown) {
            if (hasLeading || hasText) {
                width += dp(TRAILING_GAP_DP);
            }
            width += dp(CLOSE_ICON_SIZE_DP);
        }
        return width;
    }

    private boolean hasLeadingSlot() {
        return iconDrawable != null || (supportsSelection() && isChecked());
    }

    private boolean supportsSelection() {
        return style == ChipStyle.FILTER || style == ChipStyle.INPUT;
    }

    private boolean isOutlinedState() {
        return !(supportsSelection() && isChecked());
    }

    private int resolveContainerColor() {
        if (hasContainerColor) {
            return isEnabled() ? containerColor
                    : applyAlpha(containerColor, DISABLED_CONTAINER_ALPHA);
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        if (supportsSelection() && isChecked()) {
            return dynamicColors.secondaryContainer().getArgb(colorScheme);
        }
        return Color.TRANSPARENT;
    }

    private int resolveContentColor() {
        if (hasContentColor) {
            return isEnabled() ? contentColor
                    : applyAlpha(contentColor, DISABLED_CONTENT_ALPHA);
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTENT_ALPHA);
        }
        if (supportsSelection() && isChecked()) {
            return dynamicColors.onSecondaryContainer().getArgb(colorScheme);
        }
        return dynamicColors.onSurface().getArgb(colorScheme);
    }

    private int resolveIconColor() {
        if (hasIconColor) {
            return isEnabled() ? iconColor : applyAlpha(iconColor, DISABLED_CONTENT_ALPHA);
        }
        return resolveContentColor();
    }

    private int resolveCloseIconColor() {
        if (hasCloseIconColor) {
            return isEnabled() ? closeIconColor : applyAlpha(closeIconColor, DISABLED_CONTENT_ALPHA);
        }
        return resolveContentColor();
    }

    private int resolveOutlineColor() {
        if (hasOutlineColor) {
            return isEnabled() ? outlineColor : applyAlpha(outlineColor, DISABLED_CONTAINER_ALPHA);
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        return dynamicColors.outline().getArgb(colorScheme);
    }

    private int resolveRippleColor() {
        return applyAlpha(resolveContentColor(), PRESSED_OVERLAY_ALPHA);
    }

    private void drawIcon(Canvas canvas, float left, float centerY, float size, int color) {
        if (iconDrawable == null) {
            return;
        }
        int l = (int) left;
        int t = (int) (centerY - size / 2.0f);
        iconDrawable.setBounds(l, t, (int) (l + size + 0.5f), (int) (t + size + 0.5f));
        tintDrawable(iconDrawable, color);
        iconDrawable.draw(canvas);
    }

    private void drawCheckGlyph(Canvas canvas, float centerX, float centerY, float size, int color) {
        float width = size * 0.62f;
        float height = width * 0.8f;
        float left = centerX - width / 2.0f;
        float top = centerY - height / 2.0f;
        glyphPath.reset();
        glyphPath.moveTo(left, top + height * 0.54f);
        glyphPath.lineTo(left + width * 0.37f, top + height);
        glyphPath.lineTo(left + width, top);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(GLYPH_STROKE_WIDTH_DP));
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setColor(color);
        canvas.drawPath(glyphPath, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawCloseGlyph(Canvas canvas, float centerX, float centerY, float size, int color) {
        float arm = size * 0.28f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(GLYPH_STROKE_WIDTH_DP));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);
        canvas.drawLine(centerX - arm, centerY - arm, centerX + arm, centerY + arm, paint);
        canvas.drawLine(centerX + arm, centerY - arm, centerX - arm, centerY + arm, paint);
        paint.setStyle(Paint.Style.FILL);
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

    private void performCloseClick() {
        if (closeClickListener != null) {
            closeClickListener.onCloseClick(this);
        }
    }

    private void animateCheck(boolean checked) {
        float target = checked ? 1.0f : 0.0f;
        if (checkAnimator != null) {
            ValueAnimator animator = checkAnimator;
            checkAnimator = null;
            animator.cancel();
        }
        if (getWindowToken() == null || checkProgress == target) {
            checkProgress = target;
            invalidate();
            return;
        }
        checkSpring = new SpringSimulation(ANIM_STIFFNESS, ANIM_DAMPING);
        checkSpring.setPosition(checkProgress);
        checkSpring.setTarget(target);
        lastFrameTime = System.nanoTime();
        checkAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
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
                }
            }
        });
        checkAnimator.start();
    }

    private static int applyAlpha(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0.0f, Math.min(1.0f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
