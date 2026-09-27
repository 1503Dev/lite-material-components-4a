package dev1503.lmc4a.v3.widget.collapse;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import dev1503.lmc4a.v3.anim.SpringSimulation;

public class MaterialCollapse extends LinearLayout {

    private static final float SPRING_STIFFNESS = 380f;
    private static final float SPRING_DAMPING = 1.0f;
    private static final float SPRING_MAX_FRAME_DELTA_SEC = 0.05f;

    private final RevealWindow revealWindow;
    private final LinearLayout contentSlot;

    private CollapseAlign align = CollapseAlign.START;
    private boolean expanded = true;
    private boolean animate = true;

    private ValueAnimator springAnimator;
    private View boundView;
    private boolean boundViewClickable;
    private OnExpandChangeListener onExpandChangeListener;

    public MaterialCollapse(Context context) {
        this(context, null);
    }

    public MaterialCollapse(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCollapse(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        contentSlot = new LinearLayout(getContext());
        contentSlot.setOrientation(VERTICAL);
        revealWindow = new RevealWindow(getContext());
        revealWindow.addView(contentSlot, new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        super.addView(revealWindow, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        revealWindow.setRevealFraction(1.0f);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child == revealWindow || child == contentSlot) {
            super.addView(child, index, params);
            return;
        }
        contentSlot.addView(child, index, params);
    }

    public void setAlign(CollapseAlign align) {
        this.align = align == null ? CollapseAlign.START : align;
        revealWindow.invalidateReveal();
    }

    public CollapseAlign getAlign() {
        return align;
    }

    public void setExpanded(boolean expanded) {
        setExpanded(expanded, animate);
    }

    public void setExpanded(boolean expanded, boolean animate) {
        boolean changed = this.expanded != expanded;
        this.expanded = expanded;
        applyContentReveal(animate && changed, changed);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void toggle() {
        setExpanded(!expanded);
    }

    public void expand() {
        setExpanded(true);
    }

    public void collapse() {
        setExpanded(false);
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setOnExpandChangeListener(OnExpandChangeListener listener) {
        this.onExpandChangeListener = listener;
    }

    public void bindTo(View view) {
        unbind();
        if (view == null) {
            return;
        }
        boundView = view;
        boundViewClickable = view.isClickable();
        view.setClickable(true);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    public void unbind() {
        if (boundView != null) {
            boundView.setOnClickListener(null);
            boundView.setClickable(boundViewClickable);
        }
        boundView = null;
        boundViewClickable = false;
    }

    public boolean isBound() {
        return boundView != null;
    }

    public interface OnExpandChangeListener {
        void onExpandChange(boolean expanded);
    }

    public LinearLayout getContentSlotView() {
        return contentSlot;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbind();
        if (springAnimator != null) {
            springAnimator.cancel();
            springAnimator = null;
        }
    }

    private void applyContentReveal(boolean animate, boolean changed) {
        if (springAnimator != null) {
            springAnimator.cancel();
            springAnimator = null;
        }
        float targetFraction = expanded ? 1.0f : 0.0f;
        if (!animate || getWindowToken() == null) {
            revealWindow.setRevealFraction(targetFraction);
            return;
        }
        float startFraction = revealWindow.getRevealFraction();
        if (startFraction != targetFraction) {
            final SpringSimulation spring = new SpringSimulation(SPRING_STIFFNESS, SPRING_DAMPING);
            spring.setPosition(startFraction);
            spring.setTarget(targetFraction);
            final float[] lastFrameTime = {System.nanoTime()};
            springAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            springAnimator.setDuration((long) Float.MAX_VALUE);
            springAnimator.setRepeatCount(ValueAnimator.INFINITE);
            springAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    long now = System.nanoTime();
                    float delta = (now - lastFrameTime[0]) / 1_000_000_000f;
                    lastFrameTime[0] = now;
                    delta = Math.min(delta, SPRING_MAX_FRAME_DELTA_SEC);
                    float fraction = spring.update(delta);
                    revealWindow.setRevealFraction(fraction);
                    if (spring.isAtRest()) {
                        springAnimator.cancel();
                        springAnimator = null;
                        revealWindow.setRevealFraction(expanded ? 1.0f : 0.0f);
                    }
                }
            });
            springAnimator.start();
        }
        if (changed && onExpandChangeListener != null) {
            onExpandChangeListener.onExpandChange(expanded);
        }
    }

    private final class RevealWindow extends FrameLayout {

        private float revealFraction = 1.0f;

        RevealWindow(Context context) {
            super(context);
        }

        void setRevealFraction(float fraction) {
            float clamped = Math.max(0.0f, Math.min(1.0f, fraction));
            if (clamped != revealFraction) {
                revealFraction = clamped;
                requestLayout();
            }
        }

        float getRevealFraction() {
            return revealFraction;
        }

        void invalidateReveal() {
            requestLayout();
            invalidate();
        }

        private float revealedHeight() {
            MarginLayoutParams lp = (MarginLayoutParams) contentSlot.getLayoutParams();
            int naturalHeight = contentSlot.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            return naturalHeight * revealFraction;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            measureChildWithMargins(contentSlot, widthMeasureSpec, 0,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), 0);
            MarginLayoutParams lp = (MarginLayoutParams) contentSlot.getLayoutParams();
            int childWidth = contentSlot.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int naturalHeight = contentSlot.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            int desiredWidth = childWidth + getPaddingLeft() + getPaddingRight();
            int width = View.resolveSize(
                    Math.max(desiredWidth, getSuggestedMinimumWidth()), widthMeasureSpec);
            int height = (int) (naturalHeight * revealFraction + 0.5f)
                    + getPaddingTop() + getPaddingBottom();
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            if (heightMode == View.MeasureSpec.EXACTLY) {
                height = heightSize;
            } else if (heightMode == View.MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            MarginLayoutParams lp = (MarginLayoutParams) contentSlot.getLayoutParams();
            int windowHeight = bottom - top;
            int childWidth = contentSlot.getMeasuredWidth();
            int childHeight = contentSlot.getMeasuredHeight();
            int childLeft = getPaddingLeft() + lp.leftMargin;
            int childTop;
            if (align == CollapseAlign.END) {
                childTop = windowHeight - getPaddingBottom() - lp.bottomMargin - childHeight;
            } else {
                childTop = getPaddingTop() + lp.topMargin;
            }
            contentSlot.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            float revealed = revealedHeight();
            if (align == CollapseAlign.END) {
                float top = getHeight() - getPaddingBottom() - revealed;
                canvas.clipRect(0, top, getWidth(), getHeight());
            } else {
                canvas.clipRect(0, 0, getWidth(), getPaddingTop() + revealed);
            }
            super.dispatchDraw(canvas);
            canvas.restore();
        }
    }
}
