package dev1503.lmc4a.v3.widget.radiobutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RadioButton;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialRadioButton extends RadioButton {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float COMPONENT_MIN_SIZE_DP = 48.0f;
    private static final float BUTTON_SIZE_DP = 20.0f;
    private static final float BUTTON_LEFT_GAP_DP = 6.0f;
    private static final float TEXT_GAP_DP = 8.0f;
    private static final float INNER_DOT_DP = 10.0f;
    private static final float STROKE_WIDTH_DP = 2.0f;
    private static final float DISABLED_OUTLINE_ALPHA = 0.38f;
    private static final float DISABLED_INNER_ALPHA = 0.38f;
    private static final float ANIM_DURATION_MS = 500.0f;
    private static final float SPRING_STIFFNESS = 130f;
    private static final float SPRING_DAMPING = 0.7f;
    private static final float SCALE_STIFFNESS = 400f;
    private static final float SCALE_DAMPING = 0.7f;
    private static final float SCALE_MIN = 0.9f;

    private static final Drawable EMPTY_BUTTON_DRAWABLE = new Drawable() {
        @Override
        public void draw(Canvas canvas) {
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return 0;
        }

        @Override
        public int getIntrinsicHeight() {
            return 0;
        }
    };

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float checkProgress;
    private int currentOutlineColor;
    private int currentInnerColor;
    private SpringSimulation checkSpring;
    private SpringSimulation colorSpring;
    private ValueAnimator checkAnimator;
    private ValueAnimator colorAnimator;
    private long lastCheckFrameTime;
    private long lastColorFrameTime;

    private float currentScale = 1.0f;
    private float scaleTarget;
    private SpringSimulation scaleSpring;
    private ValueAnimator scaleAnimator;
    private long lastScaleFrameTime;

    public MaterialRadioButton(Context context) {
        this(context, null);
    }

    public MaterialRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.radioButtonStyle);
    }

    public MaterialRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        setButtonDrawable(EMPTY_BUTTON_DRAWABLE);
        setClickable(true);
        setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        setPadding((int) (dp(BUTTON_LEFT_GAP_DP) + dp(BUTTON_SIZE_DP) + dp(TEXT_GAP_DP) + 0.5f), 0, 0, 0);
        setTextColor(resolveTextColor());
        currentOutlineColor = resolveOutlineColor();
        currentInnerColor = resolveInnerColor();
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        setTextColor(resolveTextColor());
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    @Override
    public void setChecked(boolean checked) {
        if (dynamicColors == null) {
            super.setChecked(checked);
            return;
        }
        boolean changed = checked != isChecked();
        int oldOutline = currentOutlineColor;
        int oldInner = currentInnerColor;
        super.setChecked(checked);
        if (changed) {
            animateCheckTo(checked, oldOutline, oldInner);
        } else {
            checkProgress = checked ? 1.0f : 0.0f;
            currentOutlineColor = resolveOutlineColor();
            currentInnerColor = resolveInnerColor();
            invalidate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean changed = enabled != isEnabled();
        super.setEnabled(enabled);
        if (changed && dynamicColors != null) {
            animateColorTransition(currentOutlineColor, currentInnerColor);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int minSize = (int) (dp(COMPONENT_MIN_SIZE_DP) + 0.5f);
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) {
            width = Math.max(width, minSize);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            height = Math.max(height, minSize);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = dp(BUTTON_LEFT_GAP_DP + BUTTON_SIZE_DP / 2.0f);
        float cy = getHeight() / 2.0f;
        float outerRadius = dp(BUTTON_SIZE_DP) / 2.0f;
        float innerRadius = dp(INNER_DOT_DP) / 2.0f;
        float strokeWidth = dp(STROKE_WIDTH_DP);

        canvas.save();
        canvas.scale(currentScale, currentScale, cx, cy);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(currentOutlineColor);
        canvas.drawCircle(cx, cy, outerRadius - strokeWidth / 2.0f, paint);

        if (checkProgress > 0f) {
            if (checkProgress <= 0.5f) {
                float t = checkProgress / 0.5f;
                t = 1f - (1f - t) * (1f - t);
                float ringWidth = t * outerRadius;
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(ringWidth);
                paint.setColor(currentInnerColor);
                canvas.drawCircle(cx, cy, outerRadius - ringWidth / 2.0f, paint);
            } else {
                float t = (checkProgress - 0.5f) / 0.5f;
                float fillRadius = outerRadius - t * (outerRadius - innerRadius);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(currentInnerColor);
                canvas.drawCircle(cx, cy, fillRadius, paint);
            }
        }

        canvas.restore();
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
        }
        if (colorAnimator != null) {
            colorAnimator.cancel();
        }
        if (scaleAnimator != null) {
            scaleAnimator.cancel();
        }
    }

    private void animateCheckTo(boolean checked, int oldOutline, int oldInner) {
        float target = checked ? 1.0f : 0.0f;
        if (checkAnimator != null) {
            checkAnimator.cancel();
            checkAnimator = null;
        }
        if (getWindowToken() == null || checkProgress == target) {
            checkProgress = target;
            currentOutlineColor = resolveOutlineColor();
            currentInnerColor = resolveInnerColor();
            invalidate();
            return;
        }
        checkSpring = new SpringSimulation(SPRING_STIFFNESS, SPRING_DAMPING);
        checkSpring.setPosition(checkProgress);
        checkSpring.setTarget(target);
        lastCheckFrameTime = System.nanoTime();
        checkAnimator = ValueAnimator.ofFloat(0f, 1f);
        checkAnimator.setDuration((long) ANIM_DURATION_MS);
        checkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastCheckFrameTime) / 1_000_000_000f;
                lastCheckFrameTime = now;
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
        animateColorTransition(oldOutline, oldInner);
        startScaleAnimation();
    }

    private void startScaleAnimation() {
        if (scaleAnimator != null) {
            scaleAnimator.cancel();
            scaleAnimator = null;
        }
        if (getWindowToken() == null) {
            currentScale = 1.0f;
            return;
        }
        scaleSpring = new SpringSimulation(SCALE_STIFFNESS, SCALE_DAMPING);
        scaleSpring.setPosition(currentScale);
        scaleTarget = SCALE_MIN;
        scaleSpring.setTarget(scaleTarget);
        lastScaleFrameTime = System.nanoTime();
        scaleAnimator = ValueAnimator.ofFloat(0f, 1f);
        scaleAnimator.setDuration(1000);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastScaleFrameTime) / 1_000_000_000f;
                lastScaleFrameTime = now;
                delta = Math.min(delta, 0.05f);

                currentScale = scaleSpring.update(delta);
                invalidate();

                if (scaleTarget != 1.0f) {
                    if (currentScale <= scaleTarget + 0.02f) {
                        scaleTarget = 1.0f;
                        scaleSpring.setTarget(scaleTarget);
                    }
                } else if (scaleSpring.isAtRest()) {
                    currentScale = 1.0f;
                    animation.cancel();
                }
            }
        });
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (scaleAnimator == animation) {
                    currentScale = 1.0f;
                    invalidate();
                }
            }
        });
        scaleAnimator.start();
    }

    private void animateColorTransition(int startOutline, int startInner) {
        if (colorAnimator != null) {
            colorAnimator.cancel();
            colorAnimator = null;
        }
        final int endOutline = resolveOutlineColor();
        final int endInner = resolveInnerColor();
        currentOutlineColor = startOutline;
        currentInnerColor = startInner;
        if (startOutline == endOutline && startInner == endInner) {
            currentOutlineColor = endOutline;
            currentInnerColor = endInner;
            return;
        }
        colorSpring = new SpringSimulation(SPRING_STIFFNESS, SPRING_DAMPING);
        colorSpring.setPosition(0f);
        colorSpring.setTarget(1f);
        lastColorFrameTime = System.nanoTime();
        colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration((long) ANIM_DURATION_MS);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastColorFrameTime) / 1_000_000_000f;
                lastColorFrameTime = now;
                delta = Math.min(delta, 0.05f);

                float fraction = colorSpring.update(delta);
                fraction = Math.max(0f, Math.min(1f, fraction));
                currentOutlineColor = (int) new ArgbEvaluator().evaluate(fraction, startOutline, endOutline);
                currentInnerColor = (int) new ArgbEvaluator().evaluate(fraction, startInner, endInner);
                invalidate();

                if (colorSpring.isAtRest()) {
                    animation.cancel();
                }
            }
        });
        colorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (colorAnimator == animation) {
                    currentOutlineColor = endOutline;
                    currentInnerColor = endInner;
                    invalidate();
                }
            }
        });
        colorAnimator.start();
    }

    private int resolveOutlineColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), DISABLED_OUTLINE_ALPHA);
        }
        if (isChecked()) {
            return dynamicColors.primary().getArgb(colorScheme);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveInnerColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), DISABLED_OUTLINE_ALPHA);
        }
        if (isChecked()) {
            return dynamicColors.primary().getArgb(colorScheme);
        }
        return Color.TRANSPARENT;
    }

    private int resolveTextColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(
                    dynamicColors.onSurface().getArgb(colorScheme), DISABLED_OUTLINE_ALPHA);
        }
        return dynamicColors.onSurface().getArgb(colorScheme);
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