package dev1503.lmc4a.v3.widget.checkbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.CheckBox;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialCheckBox extends CheckBox {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float COMPONENT_MIN_SIZE_DP = 48.0f;
    private static final float BOX_SIZE_DP = 18.0f;
    private static final float BOX_LEFT_GAP_DP = 7.0f;
    private static final float BOX_CORNER_RADIUS_DP = 2.0f;
    private static final float OUTLINE_STROKE_WIDTH_DP = 2.0f;
    private static final float GLYPH_STROKE_WIDTH_DP = 2.0f;
    private static final float TEXT_GAP_DP = 8.0f;
    private static final float DISABLED_ALPHA = 0.38f;
    private static final float ANIM_DURATION_MS = 400.0f;
    private static final float SPRING_STIFFNESS = 300f;
    private static final float SPRING_DAMPING = 0.7f;
    private static final float GLYPH_MIN_SCALE = 0.4f;
    private static final float GLYPH_FADE_START = 0.3f;
    private static final float CHECK_SIZE_WIDTH_DP = 10.82f;
    private static final float CHECK_SIZE_HEIGHT_DP = 8.66f;

    private static final float CHECK_START_X = 0.0f;
    private static final float CHECK_START_Y = 0.541f;
    private static final float CHECK_MIDDLE_X = 0.367f;
    private static final float CHECK_MIDDLE_Y = 1.0f;
    private static final float CHECK_END_X = 1.0f;
    private static final float CHECK_END_Y = 0.0f;
    private static final float DASH_START_X = 0.25f;
    private static final float DASH_END_X = 0.75f;

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
    private final RectF boxRect = new RectF();
    private final RectF outlineRect = new RectF();
    private final Path glyphPath = new Path();

    private float checkProgress;
    private SpringSimulation checkSpring;
    private ValueAnimator checkAnimator;
    private long lastFrameTime;
    private boolean indeterminate;

    public MaterialCheckBox(Context context) {
        this(context, null);
    }

    public MaterialCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public MaterialCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        setButtonDrawable(EMPTY_BUTTON_DRAWABLE);
        setClickable(true);
        setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        setPadding((int) (dp(BOX_LEFT_GAP_DP) + dp(BOX_SIZE_DP) + dp(TEXT_GAP_DP) + 0.5f), 0, 0, 0);
        setTextColor(resolveTextColor());
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        setTextColor(resolveTextColor());
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setIndeterminate(boolean indeterminate) {
        if (this.indeterminate == indeterminate) {
            return;
        }
        this.indeterminate = indeterminate;
        animateToState();
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    @Override
    public void setChecked(boolean checked) {
        if (dynamicColors == null) {
            super.setChecked(checked);
            return;
        }
        boolean changed = checked != isChecked();
        super.setChecked(checked);
        if (changed || indeterminate) {
            indeterminate = false;
            animateToState();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean changed = enabled != isEnabled();
        super.setEnabled(enabled);
        if (changed && dynamicColors != null) {
            setTextColor(resolveTextColor());
            invalidate();
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

        float boxSize = dp(BOX_SIZE_DP);
        float left = dp(BOX_LEFT_GAP_DP);
        float centerY = getHeight() / 2.0f;
        float radius = dp(BOX_CORNER_RADIUS_DP);
        float outlineStroke = dp(OUTLINE_STROKE_WIDTH_DP);
        float progress = Math.max(0f, Math.min(1f, checkProgress));

        boxRect.set(left, centerY - boxSize / 2.0f, left + boxSize, centerY + boxSize / 2.0f);

        if (progress < 1.0f) {
            float inset = outlineStroke / 2.0f;
            outlineRect.set(boxRect.left + inset, boxRect.top + inset,
                    boxRect.right - inset, boxRect.bottom - inset);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(outlineStroke);
            paint.setColor(applyAlphaFraction(resolveOutlineColor(), 1.0f - progress));
            canvas.drawRoundRect(outlineRect, Math.max(0.0f, radius - inset),
                    Math.max(0.0f, radius - inset), paint);
        }

        if (progress > 0.0f) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(applyAlphaFraction(resolveContainerColor(), progress));
            canvas.drawRoundRect(boxRect, radius, radius, paint);

            float glyphAlpha = Math.max(0.0f,
                    Math.min(1.0f, (progress - GLYPH_FADE_START) / (1.0f - GLYPH_FADE_START)));
            if (glyphAlpha > 0.0f) {
                float scale = GLYPH_MIN_SCALE + (1.0f - GLYPH_MIN_SCALE) * glyphAlpha;
                canvas.save();
                canvas.scale(scale, scale, boxRect.centerX(), boxRect.centerY());
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(GLYPH_STROKE_WIDTH_DP));
                paint.setStrokeCap(Paint.Cap.BUTT);
                paint.setStrokeJoin(Paint.Join.MITER);
                paint.setColor(applyAlphaFraction(resolveGlyphColor(), glyphAlpha));
                if (indeterminate) {
                    canvas.drawLine(
                            boxRect.left + boxSize * DASH_START_X, centerY,
                            boxRect.left + boxSize * DASH_END_X, centerY, paint);
                } else {
                    float glyphWidth = dp(CHECK_SIZE_WIDTH_DP);
                    float glyphHeight = dp(CHECK_SIZE_HEIGHT_DP);
                    float glyphLeft = boxRect.centerX() - glyphWidth / 2.0f;
                    float glyphTop = boxRect.centerY() - glyphHeight / 2.0f;
                    glyphPath.reset();
                    glyphPath.moveTo(glyphLeft + glyphWidth * CHECK_START_X,
                            glyphTop + glyphHeight * CHECK_START_Y);
                    glyphPath.lineTo(glyphLeft + glyphWidth * CHECK_MIDDLE_X,
                            glyphTop + glyphHeight * CHECK_MIDDLE_Y);
                    glyphPath.lineTo(glyphLeft + glyphWidth * CHECK_END_X,
                            glyphTop + glyphHeight * CHECK_END_Y);
                    canvas.drawPath(glyphPath, paint);
                }
                canvas.restore();
            }
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
            ValueAnimator animator = checkAnimator;
            checkAnimator = null;
            animator.cancel();
        }
    }

    private void animateToState() {
        float target = isChecked() || indeterminate ? 1.0f : 0.0f;
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
        checkSpring = new SpringSimulation(SPRING_STIFFNESS, SPRING_DAMPING);
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

    private int resolveContainerColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        return dynamicColors.primary().getArgb(colorScheme);
    }

    private int resolveGlyphColor() {
        if (!isEnabled()) {
            return dynamicColors.surface().getArgb(colorScheme);
        }
        return dynamicColors.onPrimary().getArgb(colorScheme);
    }

    private int resolveOutlineColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveTextColor() {
        if (!isEnabled()) {
            return applyAlphaFraction(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_ALPHA);
        }
        return dynamicColors.onSurface().getArgb(colorScheme);
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
