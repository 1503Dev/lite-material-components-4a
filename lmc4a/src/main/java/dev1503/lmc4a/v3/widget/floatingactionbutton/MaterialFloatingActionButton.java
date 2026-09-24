package dev1503.lmc4a.v3.widget.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;

import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.widget.ColorVariant;
import dev1503.lmc4a.v3.widget.SizeVariant;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class MaterialFloatingActionButton extends MaterialButton {

    private static final float SMALL_HEIGHT_DP = 56.0f;
    private static final float MEDIUM_HEIGHT_DP = 84.0f;
    private static final float LARGE_HEIGHT_DP = 92.0f;
    private static final float SMALL_RADIUS_DP = 12.0f;
    private static final float MEDIUM_RADIUS_DP = 16.0f;
    private static final float LARGE_RADIUS_DP = 24.0f;
    private static final float SMALL_ICON_SIZE_DP = 24.0f;
    private static final float MEDIUM_ICON_SIZE_DP = 28.0f;
    private static final float LARGE_ICON_SIZE_DP = 32.0f;
    private static final float SMALL_LABEL_GAP_DP = 12.0f;
    private static final float MEDIUM_LABEL_GAP_DP = 14.0f;
    private static final float LARGE_LABEL_GAP_DP = 16.0f;
    private static final float SMALL_HORIZONTAL_PADDING_DP = 16.0f;
    private static final float MEDIUM_HORIZONTAL_PADDING_DP = 20.0f;
    private static final float LARGE_HORIZONTAL_PADDING_DP = 24.0f;
    private static final float SMALL_LABEL_TEXT_SIZE_SP = 14.0f;
    private static final float MEDIUM_LABEL_TEXT_SIZE_SP = 16.0f;
    private static final float LARGE_LABEL_TEXT_SIZE_SP = 18.0f;
    private static final float ELEVATION_DP = 6.0f;
    private static final float DISABLED_ICON_ALPHA = 0.38f;
    private static final float LABEL_DARKEN_FRACTION = 0.2f;
    private static final float ANIM_STIFFNESS = 300f;
    private static final float ANIM_DAMPING = 0.7f;
    private static final long ANIM_DURATION_MS = 400L;

    private SizeVariant size = SizeVariant.SMALL;
    private SpringSimulation scaleSpring;
    private ValueAnimator scaleAnimator;
    private long lastFrameTime;
    private float scale = 1f;
    private float scaleTarget = 1f;
    private View boundScrollable;
    private ViewTreeObserver.OnScrollChangedListener scrollChangedListener;
    private int lastScrollY;

    public MaterialFloatingActionButton(Context context) {
        this(context, null);
    }

    public MaterialFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public MaterialFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setColorVariant(ColorVariant.PRIMARY_CONTAINER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int radius = (int) (dp(resolveCornerRadiusDp()) + 0.5f);
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                    outline.setAlpha(1.0f);
                }
            });
            setClipToOutline(true);
        }
    }

    public void setSize(SizeVariant size) {
        if (size == null) {
            size = SizeVariant.SMALL;
        }
        if (this.size != size) {
            this.size = size;
            requestLayout();
            updateShape();
        }
    }

    public SizeVariant getSize() {
        return size;
    }

    public void show() {
        animateScale(1f);
    }

    public void hide() {
        animateScale(0f);
    }

    public void bindTo(View scrollable) {
        if (scrollable == null) {
            return;
        }
        unbind();
        boundScrollable = scrollable;
        lastScrollY = 0;
        scrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (boundScrollable != null) {
                    handleScroll(boundScrollable.getScrollY());
                }
            }
        };
        boundScrollable.getViewTreeObserver().addOnScrollChangedListener(scrollChangedListener);
        handleScroll(boundScrollable.getScrollY());
    }

    public void unbind() {
        if (boundScrollable != null) {
            ViewTreeObserver observer = boundScrollable.getViewTreeObserver();
            if (observer.isAlive() && scrollChangedListener != null) {
                observer.removeOnScrollChangedListener(scrollChangedListener);
            }
        }
        boundScrollable = null;
        scrollChangedListener = null;
        lastScrollY = 0;
    }

    public boolean isBound() {
        return boundScrollable != null;
    }

    private void handleScroll(int scrollY) {
        int delta = scrollY - lastScrollY;
        lastScrollY = scrollY;
        if (scrollY <= 0) {
            show();
        } else if (delta > 0) {
            hide();
        } else if (delta < 0) {
            show();
        }
    }

    private void animateScale(float target) {
        if (getWindowToken() == null || scale == target) {
            scale = target;
            scaleTarget = target;
            applyScale(scale);
            return;
        }
        if (scaleAnimator != null && scaleTarget == target) {
            return;
        }
        if (scaleAnimator != null) {
            scaleAnimator.cancel();
            scaleAnimator = null;
        }
        scaleTarget = target;
        scaleSpring = new SpringSimulation(ANIM_STIFFNESS, ANIM_DAMPING);
        scaleSpring.setPosition(scale);
        scaleSpring.setTarget(target);
        lastFrameTime = System.nanoTime();
        scaleAnimator = ValueAnimator.ofFloat(0f, 1f);
        scaleAnimator.setDuration(ANIM_DURATION_MS);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                delta = Math.min(delta, 0.05f);

                scale = scaleSpring.update(delta);
                applyScale(scale);

                if (scaleSpring.isAtRest()) {
                    animation.cancel();
                }
            }
        });
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (scaleAnimator == animation) {
                    scale = target;
                    applyScale(scale);
                }
            }
        });
        scaleAnimator.start();
    }

    private void applyScale(float value) {
        setPivotX(getWidth() / 2f);
        setPivotY(getHeight() / 2f);
        setScaleX(value);
        setScaleY(value);
        setAlpha(value);
        boolean shouldBeVisible = value > 0f;
        if (shouldBeVisible != (getVisibility() == View.VISIBLE)) {
            setVisibility(shouldBeVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int variantHeight = (int) (dp(resolveVariantHeightDp()) + 0.5f);
        CharSequence label = getText();
        int desiredWidth = variantHeight;
        if (label != null && label.length() > 0) {
            setupLabelPaint();
            float labelWidth = paint.measureText(label.toString());
            desiredWidth = (int) (dp(resolveIconSizeDp()) + dp(resolveLabelGapDp()) + labelWidth
                    + dp(resolveHorizontalPaddingDp()) * 2f + 0.5f);
        }
        int width = resolveDesiredDimension(desiredWidth, widthMeasureSpec);
        int height = resolveDesiredDimension(variantHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int resolveDesiredDimension(int desired, int measureSpec) {
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case View.MeasureSpec.EXACTLY:
                return specSize;
            case View.MeasureSpec.AT_MOST:
                return specSize > 0 ? Math.min(desired, specSize) : desired;
            default:
                return desired;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateShape();
    }

    private void updateShape() {
        cornerRadius = dp(resolveCornerRadiusDp());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            applyRippleBackground();
            invalidateOutline();
        }
        invalidate();
    }

    private float resolveVariantHeightDp() {
        switch (size) {
            case MEDIUM:
                return MEDIUM_HEIGHT_DP;
            case LARGE:
                return LARGE_HEIGHT_DP;
            default:
                return SMALL_HEIGHT_DP;
        }
    }

    private float resolveCornerRadiusDp() {
        switch (size) {
            case MEDIUM:
                return MEDIUM_RADIUS_DP;
            case LARGE:
                return LARGE_RADIUS_DP;
            default:
                return SMALL_RADIUS_DP;
        }
    }

    @Override
    protected float resolveDefaultIconSizeDp() {
        switch (size) {
            case MEDIUM:
                return MEDIUM_ICON_SIZE_DP;
            case LARGE:
                return LARGE_ICON_SIZE_DP;
            default:
                return SMALL_ICON_SIZE_DP;
        }
    }

    private float resolveLabelGapDp() {
        switch (size) {
            case MEDIUM:
                return MEDIUM_LABEL_GAP_DP;
            case LARGE:
                return LARGE_LABEL_GAP_DP;
            default:
                return SMALL_LABEL_GAP_DP;
        }
    }

    private float resolveHorizontalPaddingDp() {
        switch (size) {
            case MEDIUM:
                return MEDIUM_HORIZONTAL_PADDING_DP;
            case LARGE:
                return LARGE_HORIZONTAL_PADDING_DP;
            default:
                return SMALL_HORIZONTAL_PADDING_DP;
        }
    }

    private float resolveLabelTextSizeSp() {
        switch (size) {
            case MEDIUM:
                return MEDIUM_LABEL_TEXT_SIZE_SP;
            case LARGE:
                return LARGE_LABEL_TEXT_SIZE_SP;
            default:
                return SMALL_LABEL_TEXT_SIZE_SP;
        }
    }

    @Override
    protected float resolveVerticalInset() {
        // FAB 永远是正方形（见 onMeasure），形状不需要为阴影预留上下内缩空间。
        return 0.0f;
    }

    @Override
    protected float resolveDefaultElevationDp() {
        return ELEVATION_DP;
    }

    @Override
    protected void applyRippleBackground() {
        contentDrawable.setCornerRadius(cornerRadius);
        contentDrawable.setColor(resolveContainerColors());
        contentDrawable.setStroke(0, Color.TRANSPARENT);
        maskDrawable.setCornerRadius(cornerRadius);
        maskDrawable.setColor(Color.WHITE);
        RippleDrawable rippleDrawable = new RippleDrawable(
                ColorStateList.valueOf(resolveRippleColor()),
                contentDrawable,
                maskDrawable);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{rippleDrawable});
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        setBackground(layerDrawable);
    }

    @Override
    protected int resolveEnabledTextColor() {
        return dynamicColors.onPrimaryContainer().getArgb(colorScheme);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawPreLollipopContainer(canvas);
        }
        drawIconAndLabel(canvas);
    }

    private void drawIconAndLabel(Canvas canvas) {
        CharSequence label = getText();
        boolean hasLabel = label != null && label.length() > 0;
        float iconSize = dp(resolveIconSizeDp());
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        int color = isEnabled()
                ? resolveEnabledTextColor()
                : applyAlphaFraction(resolveEnabledTextColor(), DISABLED_ICON_ALPHA);

        if (hasLabel) {
            setupLabelPaint();
            String s = label.toString();
            float labelWidth = paint.measureText(s);
            float startX = cx - (iconSize + dp(resolveLabelGapDp()) + labelWidth) / 2f;
            drawIcon(canvas, startX, cy, iconSize, color);
            paint.setColor(darken(resolveEnabledTextColor(), LABEL_DARKEN_FRACTION));
            if (!isEnabled()) {
                paint.setColor(applyAlphaFraction(paint.getColor(), DISABLED_ICON_ALPHA));
            }
            float baseline = cy - (paint.ascent() + paint.descent()) / 2f;
            canvas.drawText(s, startX + iconSize + dp(resolveLabelGapDp()), baseline, paint);
        } else {
            drawIcon(canvas, cx - iconSize / 2f, cy, iconSize, color);
        }
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

    private void setupLabelPaint() {
        paint.setTextSize(sp(resolveLabelTextSizeSp()));
        paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private float sp(float valueSp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, valueSp, getResources().getDisplayMetrics());
    }
}
