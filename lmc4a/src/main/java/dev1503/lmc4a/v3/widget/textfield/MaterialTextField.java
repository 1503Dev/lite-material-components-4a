package dev1503.lmc4a.v3.widget.textfield;

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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialTextField extends EditText {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float BOX_HEIGHT_DP = 56.0f;
    private static final float OUTLINED_TOP_INSET_DP = 8.0f;
    private static final float EDGE_PADDING_DP = 16.0f;
    private static final float ICON_EDGE_PADDING_DP = 12.0f;
    private static final float ICON_SIZE_DP = 24.0f;
    private static final float ICON_GAP_DP = 16.0f;
    private static final float ICON_STATE_LAYER_DP = 32.0f;
    private static final float CORNER_RADIUS_DP = 4.0f;
    private static final float LABEL_FLOATED_SIZE_SP = 12.0f;
    private static final float FILLED_LABEL_FLOATED_TOP_DP = 8.0f;
    private static final float FILLED_TEXT_OFFSET_DP = 4.0f;
    private static final float LABEL_NOTCH_GAP_DP = 4.0f;
    private static final float SUPPORTING_TEXT_SIZE_SP = 12.0f;
    private static final float SUPPORTING_ROW_HEIGHT_DP = 20.0f;
    private static final float SUPPORTING_TEXT_TOP_GAP_DP = 4.0f;
    private static final float INDICATOR_HEIGHT_DP = 1.0f;
    private static final float INDICATOR_FOCUSED_HEIGHT_DP = 2.0f;
    private static final float OUTLINE_STROKE_DP = 1.0f;
    private static final float OUTLINE_FOCUSED_STROKE_DP = 2.0f;
    private static final float DISABLED_TEXT_ALPHA = 0.38f;
    private static final float DISABLED_CONTAINER_ALPHA = 0.04f;
    private static final float DISABLED_OUTLINE_ALPHA = 0.12f;
    private static final float SELECTION_ALPHA = 0.24f;
    private static final float ICON_STATE_LAYER_ALPHA = 0.10f;
    private static final long LABEL_ANIMATION_DURATION_MS = 150L;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF boxRect = new RectF();
    private final RectF arcRect = new RectF();
    private final Path outlinePath = new Path();
    private final Paint.FontMetricsInt fontMetrics = new Paint.FontMetricsInt();
    private final Paint metricsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private DynamicScheme colorScheme = publicColorScheme;
    private TextFieldStyle textFieldStyle = TextFieldStyle.FILLED;

    private boolean initialized;

    private CharSequence label;
    private CharSequence placeholder;
    private CharSequence supportingText;
    private CharSequence errorText;
    private boolean counterEnabled;
    private int counterMaxLength;

    private Icon leadingIcon;
    private Drawable leadingIconDrawable;
    private Icon trailingIcon;
    private Drawable trailingIconDrawable;
    private OnClickListener leadingIconClickListener;
    private OnClickListener trailingIconClickListener;
    private boolean leadingIconPressed;
    private boolean trailingIconPressed;

    private float labelTextSizeSp = LABEL_FLOATED_SIZE_SP;
    private boolean hasLabelTextSizeSp;
    private float supportingTextSizeSp = SUPPORTING_TEXT_SIZE_SP;
    private boolean hasSupportingTextSizeSp;

    private int containerColor;
    private boolean hasContainerColor;
    private int contentColor;
    private boolean hasContentColor;
    private int labelColor;
    private boolean hasLabelColor;
    private int placeholderColor;
    private boolean hasPlaceholderColor;
    private int supportingTextColor;
    private boolean hasSupportingTextColor;
    private int errorColor;
    private boolean hasErrorColor;
    private int indicatorColor;
    private boolean hasIndicatorColor;
    private int focusedIndicatorColor;
    private boolean hasFocusedIndicatorColor;
    private int leadingIconColor;
    private boolean hasLeadingIconColor;
    private int trailingIconColor;
    private boolean hasTrailingIconColor;

    private float labelProgress;
    private ValueAnimator labelAnimator;
    private float cornerRadius;

    public MaterialTextField(Context context) {
        this(context, null);
    }

    public MaterialTextField(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MaterialTextField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(null);
        setGravity(Gravity.TOP | Gravity.START);
        setIncludeFontPadding(true);
        cornerRadius = dp(CORNER_RADIUS_DP);
        initialized = true;
        refreshColors();
        updatePaddings();
        updateLabel(false);
    }

    // ------------------------------------------------------------------ 样式与配色

    public void setStyle(TextFieldStyle textFieldStyle) {
        if (textFieldStyle == null) {
            textFieldStyle = TextFieldStyle.FILLED;
        }
        if (this.textFieldStyle != textFieldStyle) {
            this.textFieldStyle = textFieldStyle;
            updatePaddings();
            requestLayout();
            invalidate();
        }
    }

    public TextFieldStyle getStyle() {
        return textFieldStyle;
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        clearAllColorOverrides();
        refreshColors();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadius = dp(cornerRadiusDp);
        invalidate();
    }

    public float getCornerRadiusDp() {
        return cornerRadius / getResources().getDisplayMetrics().density;
    }

    public void clearCornerRadiusDp() {
        setCornerRadiusDp(CORNER_RADIUS_DP);
    }

    public void setContainerColor(int containerColor) {
        this.containerColor = containerColor;
        this.hasContainerColor = true;
        invalidate();
    }

    public int getContainerColor() {
        return resolveContainerColor();
    }

    public boolean hasContainerColor() {
        return hasContainerColor;
    }

    public void clearContainerColor() {
        this.hasContainerColor = false;
        invalidate();
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
        this.hasContentColor = true;
        refreshColors();
    }

    public int getContentColor() {
        return resolveContentColor();
    }

    public boolean hasContentColor() {
        return hasContentColor;
    }

    public void clearContentColor() {
        this.hasContentColor = false;
        refreshColors();
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        this.hasLabelColor = true;
        invalidate();
    }

    public int getLabelColor() {
        return resolveLabelColor();
    }

    public boolean hasLabelColor() {
        return hasLabelColor;
    }

    public void clearLabelColor() {
        this.hasLabelColor = false;
        invalidate();
    }

    public void setPlaceholderColor(int placeholderColor) {
        this.placeholderColor = placeholderColor;
        this.hasPlaceholderColor = true;
        invalidate();
    }

    public int getPlaceholderColor() {
        return resolvePlaceholderColor();
    }

    public boolean hasPlaceholderColor() {
        return hasPlaceholderColor;
    }

    public void clearPlaceholderColor() {
        this.hasPlaceholderColor = false;
        invalidate();
    }

    public void setSupportingTextColor(int supportingTextColor) {
        this.supportingTextColor = supportingTextColor;
        this.hasSupportingTextColor = true;
        invalidate();
    }

    public int getSupportingTextColor() {
        return resolveSupportingTextColor();
    }

    public boolean hasSupportingTextColor() {
        return hasSupportingTextColor;
    }

    public void clearSupportingTextColor() {
        this.hasSupportingTextColor = false;
        invalidate();
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
        this.hasErrorColor = true;
        invalidate();
    }

    public int getErrorColor() {
        return resolveErrorColor();
    }

    public boolean hasErrorColor() {
        return hasErrorColor;
    }

    public void clearErrorColor() {
        this.hasErrorColor = false;
        invalidate();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        this.hasIndicatorColor = true;
        invalidate();
    }

    public int getIndicatorColor() {
        return hasIndicatorColor
                ? indicatorColor
                : (textFieldStyle == TextFieldStyle.OUTLINED
                        ? dynamicColors.outline().getArgb(colorScheme)
                        : dynamicColors.onSurfaceVariant().getArgb(colorScheme));
    }

    public boolean hasIndicatorColor() {
        return hasIndicatorColor;
    }

    public void clearIndicatorColor() {
        this.hasIndicatorColor = false;
        invalidate();
    }

    public void setFocusedIndicatorColor(int focusedIndicatorColor) {
        this.focusedIndicatorColor = focusedIndicatorColor;
        this.hasFocusedIndicatorColor = true;
        invalidate();
    }

    public int getFocusedIndicatorColor() {
        return resolveFocusedIndicatorColor();
    }

    public boolean hasFocusedIndicatorColor() {
        return hasFocusedIndicatorColor;
    }

    public void clearFocusedIndicatorColor() {
        this.hasFocusedIndicatorColor = false;
        invalidate();
    }

    public void setLeadingIconColor(int leadingIconColor) {
        this.leadingIconColor = leadingIconColor;
        this.hasLeadingIconColor = true;
        invalidate();
    }

    public int getLeadingIconColor() {
        return resolveLeadingIconColor();
    }

    public boolean hasLeadingIconColor() {
        return hasLeadingIconColor;
    }

    public void clearLeadingIconColor() {
        this.hasLeadingIconColor = false;
        invalidate();
    }

    public void setTrailingIconColor(int trailingIconColor) {
        this.trailingIconColor = trailingIconColor;
        this.hasTrailingIconColor = true;
        invalidate();
    }

    public int getTrailingIconColor() {
        return resolveTrailingIconColor();
    }

    public boolean hasTrailingIconColor() {
        return hasTrailingIconColor;
    }

    public void clearTrailingIconColor() {
        this.hasTrailingIconColor = false;
        invalidate();
    }

    private void clearAllColorOverrides() {
        hasContainerColor = false;
        hasContentColor = false;
        hasLabelColor = false;
        hasPlaceholderColor = false;
        hasSupportingTextColor = false;
        hasErrorColor = false;
        hasIndicatorColor = false;
        hasFocusedIndicatorColor = false;
        hasLeadingIconColor = false;
        hasTrailingIconColor = false;
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        if (initialized) {
            updatePaddings();
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        if (initialized) {
            updatePaddings();
            requestLayout();
            invalidate();
        }
    }

    public void setLabelTextSizeSp(float labelTextSizeSp) {
        this.labelTextSizeSp = Math.max(0f, labelTextSizeSp);
        this.hasLabelTextSizeSp = true;
        invalidate();
    }

    public float getLabelTextSizeSp() {
        return hasLabelTextSizeSp ? labelTextSizeSp : LABEL_FLOATED_SIZE_SP;
    }

    public boolean hasLabelTextSizeSp() {
        return hasLabelTextSizeSp;
    }

    public void clearLabelTextSizeSp() {
        this.hasLabelTextSizeSp = false;
        this.labelTextSizeSp = LABEL_FLOATED_SIZE_SP;
        invalidate();
    }

    public void setSupportingTextSizeSp(float supportingTextSizeSp) {
        this.supportingTextSizeSp = Math.max(0f, supportingTextSizeSp);
        this.hasSupportingTextSizeSp = true;
        updateSupportingRow();
    }

    public float getSupportingTextSizeSp() {
        return hasSupportingTextSizeSp ? supportingTextSizeSp : SUPPORTING_TEXT_SIZE_SP;
    }

    public boolean hasSupportingTextSizeSp() {
        return hasSupportingTextSizeSp;
    }

    public void clearSupportingTextSizeSp() {
        this.hasSupportingTextSizeSp = false;
        this.supportingTextSizeSp = SUPPORTING_TEXT_SIZE_SP;
        updateSupportingRow();
    }

    // ------------------------------------------------------------------ 标签 / 占位文字

    public void setLabel(CharSequence label) {
        this.label = label;
        updatePaddings();
        requestLayout();
        invalidate();
    }

    public CharSequence getLabel() {
        return label;
    }

    private CharSequence resolveLabel() {
        if (label != null && label.length() > 0) {
            return label;
        }
        CharSequence hint = getHint();
        return hint != null && hint.length() > 0 ? hint : null;
    }

    public void setPlaceholder(CharSequence placeholder) {
        this.placeholder = placeholder;
        invalidate();
    }

    public CharSequence getPlaceholder() {
        return placeholder;
    }

    // ------------------------------------------------------------------ 辅助文字行

    public void setSupportingText(CharSequence supportingText) {
        this.supportingText = supportingText;
        updateSupportingRow();
    }

    public CharSequence getSupportingText() {
        return supportingText;
    }

    @Override
    public void setError(CharSequence error) {
        this.errorText = error;
        updateSupportingRow();
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setError(error);
    }

    @Override
    public CharSequence getError() {
        return errorText;
    }

    public boolean isErrorEnabled() {
        return errorText != null && errorText.length() > 0;
    }

    public void setCounterEnabled(boolean counterEnabled) {
        if (this.counterEnabled != counterEnabled) {
            this.counterEnabled = counterEnabled;
            updateSupportingRow();
        }
    }

    public boolean isCounterEnabled() {
        return counterEnabled;
    }

    public void setCounterMaxLength(int counterMaxLength) {
        if (this.counterMaxLength != counterMaxLength) {
            this.counterMaxLength = counterMaxLength;
            invalidate();
        }
    }

    public int getCounterMaxLength() {
        return counterMaxLength;
    }

    // ------------------------------------------------------------------ 图标

    public void setLeadingIcon(Icon leadingIcon) {
        this.leadingIcon = leadingIcon;
        leadingIconDrawable = leadingIcon == null ? null : leadingIcon.resolve(getContext());
        if (leadingIconDrawable != null) {
            leadingIconDrawable = leadingIconDrawable.mutate();
        }
        updatePaddings();
        requestLayout();
        invalidate();
    }

    public Icon getLeadingIcon() {
        return leadingIcon;
    }

    public void setTrailingIcon(Icon trailingIcon) {
        this.trailingIcon = trailingIcon;
        trailingIconDrawable = trailingIcon == null ? null : trailingIcon.resolve(getContext());
        if (trailingIconDrawable != null) {
            trailingIconDrawable = trailingIconDrawable.mutate();
        }
        updatePaddings();
        requestLayout();
        invalidate();
    }

    public Icon getTrailingIcon() {
        return trailingIcon;
    }

    public void setOnTrailingIconClickListener(OnClickListener listener) {
        this.trailingIconClickListener = listener;
    }

    public void setOnLeadingIconClickListener(OnClickListener listener) {
        this.leadingIconClickListener = listener;
    }

    // ------------------------------------------------------------------ EditText 回调

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!initialized) {
            return;
        }
        updateLabel(true);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, android.graphics.Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (!initialized) {
            return;
        }
        updateLabel(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (initialized) {
            refreshColors();
            updatePaddings();
            updateLabel(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (labelAnimator != null) {
            labelAnimator.cancel();
            labelAnimator = null;
        }
        labelProgress = shouldFloatLabel() ? 1f : 0f;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (initialized) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updatePaddings();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!initialized) {
            return;
        }
        int desired = (int) (resolveBoxTop() + dp(BOX_HEIGHT_DP) + resolveSupportingRowHeight() + 0.5f);
        int height = Math.max(getMeasuredHeight(), desired);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == View.MeasureSpec.AT_MOST) {
            height = Math.min(height, heightSize);
        } else if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        setMeasuredDimension(getMeasuredWidth(), Math.max(height, 1));
    }

    // ------------------------------------------------------------------ 绘制

    @Override
    protected void onDraw(Canvas canvas) {
        if (!initialized) {
            super.onDraw(canvas);
            return;
        }
        updateBoxRect();
        drawBox(canvas);
        super.onDraw(canvas);
        drawPlaceholder(canvas);
        drawLabel(canvas);
        drawIcons(canvas);
        drawSupportingRow(canvas);
    }

    private void drawBox(Canvas canvas) {
        if (textFieldStyle == TextFieldStyle.OUTLINED) {
            drawOutlinedBox(canvas);
            return;
        }
        boxPaint.setStyle(Paint.Style.FILL);
        boxPaint.setColor(resolveContainerColor());
        float radius = cornerRadius;
        canvas.drawRoundRect(boxRect, radius, radius, boxPaint);
        if (boxRect.height() > radius) {
            canvas.drawRect(boxRect.left, boxRect.bottom - radius, boxRect.right, boxRect.bottom, boxPaint);
        }
        int indicatorColor = resolveIndicatorColor();
        if (Color.alpha(indicatorColor) > 0) {
            float indicatorHeight = isEnabled() && isFocused()
                    ? dp(INDICATOR_FOCUSED_HEIGHT_DP) : dp(INDICATOR_HEIGHT_DP);
            boxPaint.setColor(indicatorColor);
            canvas.drawRect(boxRect.left, boxRect.bottom - indicatorHeight, boxRect.right, boxRect.bottom, boxPaint);
        }
    }

    private void drawOutlinedBox(Canvas canvas) {
        float stroke = isEnabled() && isFocused()
                ? dp(OUTLINE_FOCUSED_STROKE_DP) : dp(OUTLINE_STROKE_DP);
        float radius = cornerRadius;
        float inset = stroke / 2f;
        float left = boxRect.left + inset;
        float top = boxRect.top + inset;
        float right = boxRect.right - inset;
        float bottom = boxRect.bottom - inset;
        if (right - left < radius * 2f || bottom - top < radius * 2f) {
            return;
        }

        CharSequence currentLabel = resolveLabel();
        float notchStart = left + radius;
        float notchEnd = notchStart;
        if (currentLabel != null && labelProgress > 0.01f) {
            metricsPaint.setTypeface(getTypeface());
            metricsPaint.setTextSize(currentLabelTextSize());
            float labelWidth = metricsPaint.measureText(currentLabel, 0, currentLabel.length());
            float gap = dp(LABEL_NOTCH_GAP_DP);
            float labelLeft = isRtl() ? contentStart() - labelWidth : contentStart();
            float min = left + radius;
            float max = right - radius;
            float from = clamp(labelLeft - gap, min, max);
            float to = clamp(labelLeft + labelWidth + gap, min, max);
            notchStart = Math.min(from, to);
            notchEnd = Math.max(from, to);
        }

        outlinePath.reset();
        outlinePath.moveTo(notchEnd, top);
        outlinePath.lineTo(right - radius, top);
        arcRect.set(right - radius * 2f, top, right, top + radius * 2f);
        outlinePath.arcTo(arcRect, -90f, 90f);
        outlinePath.lineTo(right, bottom - radius);
        arcRect.set(right - radius * 2f, bottom - radius * 2f, right, bottom);
        outlinePath.arcTo(arcRect, 0f, 90f);
        outlinePath.lineTo(left + radius, bottom);
        arcRect.set(left, bottom - radius * 2f, left + radius * 2f, bottom);
        outlinePath.arcTo(arcRect, 90f, 90f);
        outlinePath.lineTo(left, top + radius);
        arcRect.set(left, top, left + radius * 2f, top + radius * 2f);
        outlinePath.arcTo(arcRect, 180f, 90f);
        outlinePath.lineTo(notchStart, top);

        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(stroke);
        boxPaint.setColor(resolveOutlineColor());
        canvas.drawPath(outlinePath, boxPaint);
        boxPaint.setStyle(Paint.Style.FILL);
    }

    private void drawLabel(Canvas canvas) {
        CharSequence currentLabel = resolveLabel();
        if (currentLabel == null) {
            return;
        }
        textPaint.setTypeface(getTypeface());
        textPaint.setTextSize(currentLabelTextSize());
        textPaint.setColor(resolveLabelColor());
        boolean rtl = isRtl();
        textPaint.setTextAlign(rtl ? Paint.Align.RIGHT : Paint.Align.LEFT);
        canvas.drawText(currentLabel, 0, currentLabel.length(), contentStart(), currentLabelBaseline(), textPaint);
    }

    private void drawPlaceholder(Canvas canvas) {
        if (placeholder == null || placeholder.length() == 0) {
            return;
        }
        if (!isFocused() || length() > 0) {
            return;
        }
        textPaint.setTypeface(getTypeface());
        textPaint.setTextSize(getTextSize());
        textPaint.setColor(resolvePlaceholderColor());
        boolean rtl = isRtl();
        textPaint.setTextAlign(rtl ? Paint.Align.RIGHT : Paint.Align.LEFT);
        canvas.drawText(placeholder, 0, placeholder.length(),
                contentStart(), inputBaseline(), textPaint);
    }

    private void drawIcons(Canvas canvas) {
        float centerY = boxRect.centerY();
        float iconSize = dp(ICON_SIZE_DP);
        boolean rtl = isRtl();

        if (leadingIconDrawable != null) {
            float left = rtl ? getWidth() - dp(ICON_EDGE_PADDING_DP) - iconSize : dp(ICON_EDGE_PADDING_DP);
            if (leadingIconPressed) {
                drawIconStateLayer(canvas, left, centerY, iconSize);
            }
            drawIcon(canvas, leadingIconDrawable, left, centerY, iconSize, resolveLeadingIconColor());
        }
        if (trailingIconDrawable != null) {
            float left = rtl ? dp(ICON_EDGE_PADDING_DP) : getWidth() - dp(ICON_EDGE_PADDING_DP) - iconSize;
            if (trailingIconPressed) {
                drawIconStateLayer(canvas, left, centerY, iconSize);
            }
            drawIcon(canvas, trailingIconDrawable, left, centerY, iconSize, resolveTrailingIconColor());
        }
    }

    private void drawIconStateLayer(Canvas canvas, float iconLeft, float centerY, float iconSize) {
        iconPaint.setColor(applyAlpha(
                dynamicColors.onSurface().getArgb(colorScheme), ICON_STATE_LAYER_ALPHA));
        float radius = dp(ICON_STATE_LAYER_DP) / 2f;
        canvas.drawCircle(iconLeft + iconSize / 2f, centerY, radius, iconPaint);
    }

    private void drawIcon(Canvas canvas, Drawable drawable, float left, float centerY, float size, int color) {
        int l = (int) (left + 0.5f);
        int t = (int) (centerY - size / 2f + 0.5f);
        drawable.setBounds(l, t, (int) (l + size + 0.5f), (int) (t + size + 0.5f));
        applyIconTint(drawable, color);
        drawable.draw(canvas);
    }

    private void drawSupportingRow(Canvas canvas) {
        float rowHeight = resolveSupportingRowHeight();
        if (rowHeight <= 0f) {
            return;
        }
        float rowTop = getHeight() - rowHeight;
        boolean rtl = isRtl();
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(sp(supportingTextSizeSp));
        float baseline = rowTop + dp(SUPPORTING_TEXT_TOP_GAP_DP) - textPaint.ascent();

        CharSequence text = isErrorEnabled() ? errorText : supportingText;
        if (text != null && text.length() > 0) {
            textPaint.setColor(resolveSupportingTextColor());
            textPaint.setTextAlign(rtl ? Paint.Align.RIGHT : Paint.Align.LEFT);
            float x = rtl ? getWidth() - getPaddingRight() : getPaddingLeft();
            canvas.drawText(text, 0, text.length(), x, baseline, textPaint);
        }
        if (counterEnabled) {
            boolean exceeded = counterMaxLength > 0 && length() > counterMaxLength;
            textPaint.setColor(exceeded
                    ? resolveErrorColor()
                    : resolveSupportingTextColor());
            textPaint.setTextAlign(rtl ? Paint.Align.LEFT : Paint.Align.RIGHT);
            float x = rtl ? getPaddingLeft() : getWidth() - getPaddingRight();
            canvas.drawText(counterText(), x, baseline, textPaint);
        }
    }

    // ------------------------------------------------------------------ 几何 / 内边距

    private void updateBoxRect() {
        float top = resolveBoxTop();
        float bottom = Math.max(top + 1f, getHeight() - resolveSupportingRowHeight());
        boxRect.set(0f, top, getWidth(), bottom);
    }

    private float resolveBoxTop() {
        return textFieldStyle == TextFieldStyle.OUTLINED ? dp(OUTLINED_TOP_INSET_DP) : 0f;
    }

    private float inputLineHeight() {
        return measureLineHeight(getTextSize());
    }

    private float measureLineHeight(float textSizePx) {
        measureFontMetrics(textSizePx);
        return fontMetrics.bottom - fontMetrics.top;
    }

    private void measureFontMetrics(float textSizePx) {
        metricsPaint.setTypeface(getTypeface());
        metricsPaint.setTextSize(textSizePx);
        metricsPaint.getFontMetricsInt(fontMetrics);
    }

    private float resolveSupportingRowHeight() {
        if (!isSupportingRowVisible()) {
            return 0f;
        }
        return Math.max(dp(SUPPORTING_ROW_HEIGHT_DP),
                dp(SUPPORTING_TEXT_TOP_GAP_DP) + measureLineHeight(sp(supportingTextSizeSp)));
    }

    private boolean isSupportingRowVisible() {
        return isErrorEnabled() || (supportingText != null && supportingText.length() > 0) || counterEnabled;
    }

    private void updateSupportingRow() {
        if (!initialized) {
            return;
        }
        updatePaddings();
        requestLayout();
        invalidate();
    }

    private boolean updatePaddings() {
        if (!initialized) {
            return false;
        }
        float startPadding = leadingIconDrawable != null
                ? dp(ICON_EDGE_PADDING_DP) + dp(ICON_SIZE_DP) + dp(ICON_GAP_DP)
                : dp(EDGE_PADDING_DP);
        float endPadding = trailingIconDrawable != null
                ? dp(ICON_EDGE_PADDING_DP) + dp(ICON_SIZE_DP) + dp(ICON_GAP_DP)
                : dp(EDGE_PADDING_DP);
        boolean rtl = isRtl();
        int leftPadding = (int) ((rtl ? endPadding : startPadding) + 0.5f);
        int rightPadding = (int) ((rtl ? startPadding : endPadding) + 0.5f);

        float slack = Math.max(0f, (dp(BOX_HEIGHT_DP) - inputLineHeight()) / 2f);
        float topPadding = slack;
        float bottomPadding = slack;
        if (textFieldStyle != TextFieldStyle.OUTLINED) {
            float offset = dp(FILLED_TEXT_OFFSET_DP);
            topPadding = slack + offset;
            bottomPadding = Math.max(0f, slack - offset);
        }
        int top = (int) (resolveBoxTop() + topPadding + 0.5f);
        int bottom = (int) (bottomPadding + resolveSupportingRowHeight() + 0.5f);
        if (getPaddingLeft() != leftPadding || getPaddingTop() != top
                || getPaddingRight() != rightPadding || getPaddingBottom() != bottom) {
            setPadding(leftPadding, top, rightPadding, bottom);
            return true;
        }
        return false;
    }

    private float contentStart() {
        return isRtl() ? getWidth() - getPaddingRight() : getPaddingLeft();
    }

    private boolean isRtl() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private float restingLabelBaseline() {
        float slack = Math.max(0f, (dp(BOX_HEIGHT_DP) - inputLineHeight()) / 2f);
        measureFontMetrics(getTextSize());
        return resolveBoxTop() + slack - fontMetrics.top;
    }

    private float inputBaseline() {
        int baseline = getBaseline();
        if (baseline > 0) {
            return baseline;
        }
        measureFontMetrics(getTextSize());
        return getPaddingTop() - fontMetrics.top;
    }

    private float currentLabelTextSize() {
        return getTextSize() + (sp(labelTextSizeSp) - getTextSize()) * labelProgress;
    }

    private float currentLabelBaseline() {
        measureFontMetrics(sp(labelTextSizeSp));
        float floated;
        if (textFieldStyle == TextFieldStyle.OUTLINED) {
            floated = resolveBoxTop() - (fontMetrics.ascent + fontMetrics.descent) / 2f;
        } else {
            floated = resolveBoxTop() + dp(FILLED_LABEL_FLOATED_TOP_DP) - fontMetrics.top;
        }
        float resting = restingLabelBaseline();
        return resting + (floated - resting) * labelProgress;
    }

    private String counterText() {
        int current = length();
        if (counterMaxLength > 0) {
            return current + "/" + counterMaxLength;
        }
        return String.valueOf(current);
    }

    // ------------------------------------------------------------------ 状态

    private boolean shouldFloatLabel() {
        return isFocused() || length() > 0;
    }

    private void updateLabel(boolean animate) {
        float target = shouldFloatLabel() ? 1f : 0f;
        if (labelAnimator != null) {
            labelAnimator.cancel();
            labelAnimator = null;
        }
        if (!animate || getWindowToken() == null || labelProgress == target) {
            labelProgress = target;
            invalidate();
            return;
        }
        labelAnimator = ValueAnimator.ofFloat(labelProgress, target);
        labelAnimator.setDuration(LABEL_ANIMATION_DURATION_MS);
        labelAnimator.setInterpolator(new DecelerateInterpolator());
        labelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                labelProgress = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        labelAnimator.start();
    }

    private void refreshColors() {
        int content = resolveContentColor();
        setTextColor(new ColorStateList(
                new int[][]{{-android.R.attr.state_enabled}, new int[0]},
                new int[]{applyAlpha(content, DISABLED_TEXT_ALPHA), content}));
        setHintTextColor(Color.TRANSPARENT);
        setHighlightColor(applyAlpha(dynamicColors.primary().getArgb(colorScheme), SELECTION_ALPHA));
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && hasIconClickListener()) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    boolean leadingDown = leadingIconDrawable != null && leadingIconClickListener != null
                            && isInsideIconTouchArea(x, y, true);
                    boolean trailingDown = !leadingDown && trailingIconDrawable != null
                            && trailingIconClickListener != null
                            && isInsideIconTouchArea(x, y, false);
                    if (leadingDown || trailingDown) {
                        leadingIconPressed = leadingDown;
                        trailingIconPressed = trailingDown;
                        invalidate();
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (leadingIconPressed || trailingIconPressed) {
                        boolean wasLeading = leadingIconPressed;
                        boolean wasTrailing = trailingIconPressed;
                        leadingIconPressed = false;
                        trailingIconPressed = false;
                        invalidate();
                        if (wasLeading && leadingIconClickListener != null
                                && isInsideIconTouchArea(x, y, true)) {
                            leadingIconClickListener.onClick(this);
                        } else if (wasTrailing && trailingIconClickListener != null
                                && isInsideIconTouchArea(x, y, false)) {
                            trailingIconClickListener.onClick(this);
                        }
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (leadingIconPressed || trailingIconPressed) {
                        leadingIconPressed = false;
                        trailingIconPressed = false;
                        invalidate();
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean hasIconClickListener() {
        return (leadingIconDrawable != null && leadingIconClickListener != null)
                || (trailingIconDrawable != null && trailingIconClickListener != null);
    }

    private boolean isInsideIconTouchArea(float x, float y, boolean leading) {
        Drawable icon = leading ? leadingIconDrawable : trailingIconDrawable;
        if (icon == null) {
            return false;
        }
        updateBoxRect();
        float iconSize = dp(ICON_SIZE_DP);
        boolean rtl = isRtl();
        float centerX;
        if (leading) {
            centerX = rtl
                    ? getWidth() - dp(ICON_EDGE_PADDING_DP) - iconSize / 2f
                    : dp(ICON_EDGE_PADDING_DP) + iconSize / 2f;
        } else {
            centerX = rtl
                    ? dp(ICON_EDGE_PADDING_DP) + iconSize / 2f
                    : getWidth() - dp(ICON_EDGE_PADDING_DP) - iconSize / 2f;
        }
        float centerY = boxRect.centerY();
        float half = Math.max(iconSize, dp(48.0f)) / 2f;
        return x >= centerX - half && x <= centerX + half
                && y >= centerY - half && y <= centerY + half;
    }

    // ------------------------------------------------------------------ 杂项

    private void applyIconTint(Drawable drawable, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    private int resolveContainerColor() {
        if (hasContainerColor) {
            return containerColor;
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_CONTAINER_ALPHA);
        }
        return dynamicColors.surfaceContainerHighest().getArgb(colorScheme);
    }

    private int resolveContentColor() {
        if (hasContentColor) {
            return contentColor;
        }
        return dynamicColors.onSurface().getArgb(colorScheme);
    }

    private int resolveErrorColor() {
        if (hasErrorColor) {
            return errorColor;
        }
        return dynamicColors.error().getArgb(colorScheme);
    }

    private int resolveIndicatorColor() {
        if (!isEnabled()) {
            return Color.TRANSPARENT;
        }
        if (isErrorEnabled()) {
            return resolveErrorColor();
        }
        if (isFocused()) {
            return resolveFocusedIndicatorColor();
        }
        if (hasIndicatorColor) {
            return indicatorColor;
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveFocusedIndicatorColor() {
        if (hasFocusedIndicatorColor) {
            return focusedIndicatorColor;
        }
        if (hasIndicatorColor) {
            return indicatorColor;
        }
        return dynamicColors.primary().getArgb(colorScheme);
    }

    private int resolveOutlineColor() {
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_OUTLINE_ALPHA);
        }
        if (isErrorEnabled()) {
            return resolveErrorColor();
        }
        if (isFocused()) {
            return resolveFocusedIndicatorColor();
        }
        if (hasIndicatorColor) {
            return indicatorColor;
        }
        return dynamicColors.outline().getArgb(colorScheme);
    }

    private int resolveLabelColor() {
        if (hasLabelColor) {
            return labelColor;
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_TEXT_ALPHA);
        }
        if (isErrorEnabled()) {
            return resolveErrorColor();
        }
        if (isFocused()) {
            return dynamicColors.primary().getArgb(colorScheme);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolvePlaceholderColor() {
        if (hasPlaceholderColor) {
            return placeholderColor;
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_TEXT_ALPHA);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveSupportingTextColor() {
        if (isErrorEnabled()) {
            return resolveErrorColor();
        }
        if (hasSupportingTextColor) {
            return supportingTextColor;
        }
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_TEXT_ALPHA);
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveLeadingIconColor() {
        if (hasLeadingIconColor) {
            return leadingIconColor;
        }
        return resolveDefaultIconColor();
    }

    private int resolveTrailingIconColor() {
        if (hasTrailingIconColor) {
            return trailingIconColor;
        }
        return resolveDefaultIconColor();
    }

    private int resolveDefaultIconColor() {
        if (!isEnabled()) {
            return applyAlpha(dynamicColors.onSurface().getArgb(colorScheme), DISABLED_TEXT_ALPHA);
        }
        if (isErrorEnabled()) {
            return resolveErrorColor();
        }
        return dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private static int applyAlpha(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0f, Math.min(1f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
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
