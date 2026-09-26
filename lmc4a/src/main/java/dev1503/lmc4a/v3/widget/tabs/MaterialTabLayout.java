package dev1503.lmc4a.v3.widget.tabs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialTabLayout extends HorizontalScrollView {

    public interface OnTabSelectedListener {
        void onTabSelected(MaterialTab tab);

        void onTabUnselected(MaterialTab tab);

        void onTabReselected(MaterialTab tab);
    }

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float TAB_HEIGHT_DP = 56.0f;
    private static final float TAB_MIN_WIDTH_DP = 72.0f;
    private static final float INDICATOR_HEIGHT_DP = 3.0f;
    private static final float DIVIDER_HEIGHT_DP = 1.0f;
    private static final float SPRING_STIFFNESS = 300f;
    private static final float SPRING_DAMPING = 0.7f;
    private static final float ANIM_DURATION_MS = 400.0f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final List<MaterialTab> tabs = new ArrayList<>();
    private final TabStrip strip;
    private final Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dividerPaint = new Paint();
    private final RectF indicatorRect = new RectF();

    private DynamicScheme colorScheme = publicColorScheme;
    private TabStyle tabStyle = TabStyle.PRIMARY;
    private TabMode tabMode = TabMode.FIXED;
    private OnTabSelectedListener onTabSelectedListener;
    private int selectedIndex = -1;

    private ViewPager boundPager;
    private ViewPager.SimpleOnPageChangeListener pageListener;
    private boolean syncingFromPager;

    private float indicatorLeft;
    private float indicatorRight;
    private float indicatorTargetLeft;
    private float indicatorTargetRight;
    private float indicatorThicknessDp = -1.0f;
    private boolean dividerEnabled = true;

    private int containerColor;
    private boolean hasContainerColor;
    private int activeTabColor;
    private boolean hasActiveTabColor;
    private int inactiveTabColor;
    private boolean hasInactiveTabColor;
    private int indicatorColor;
    private boolean hasIndicatorColor;
    private int dividerColor;
    private boolean hasDividerColor;

    private SpringSimulation leftSpring;
    private SpringSimulation rightSpring;
    private ValueAnimator indicatorAnimator;
    private long lastFrameTime;

    public MaterialTabLayout(Context context) {
        this(context, null);
    }

    public MaterialTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);

        strip = new TabStrip(context);
        strip.setOrientation(LinearLayout.HORIZONTAL);
        addView(strip, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        applyContainerColor();
    }

    public MaterialTab addTab(CharSequence text) {
        return addTab(null, text);
    }

    public MaterialTab addTab(Icon icon, CharSequence text) {
        MaterialTab tab = new MaterialTab(getContext());
        tab.setIcon(icon);
        tab.setText(text);
        addTab(tab);
        return tab;
    }

    public void addTab(MaterialTab tab) {
        addTab(tab, tabs.size());
    }

    public void addTab(MaterialTab tab, int index) {
        if (tab == null) {
            return;
        }
        int target = Math.max(0, Math.min(index, tabs.size()));
        tabs.add(target, tab);
        strip.addView(tab, target, createTabParams());
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = tabs.indexOf(view);
                if (position >= 0) {
                    setSelectedTab(position);
                }
            }
        });
        if (selectedIndex < 0) {
            setSelectedTabInternal(target, false);
        } else if (target <= selectedIndex) {
            selectedIndex++;
        }
        applyTabSizes();
        applyTabStates();
        updateIndicator(false);
        requestLayout();
    }

    public void removeTab(MaterialTab tab) {
        int index = tabs.indexOf(tab);
        if (index < 0) {
            return;
        }
        boolean wasSelected = index == selectedIndex;
        tabs.remove(index);
        strip.removeView(tab);
        if (tabs.isEmpty()) {
            selectedIndex = -1;
        } else if (wasSelected) {
            selectedIndex = -1;
            setSelectedTabInternal(Math.min(index, tabs.size() - 1), false);
        } else if (index < selectedIndex) {
            selectedIndex--;
        }
        applyTabSizes();
        applyTabStates();
        updateIndicator(false);
        requestLayout();
    }

    public void removeTabAt(int index) {
        if (index >= 0 && index < tabs.size()) {
            removeTab(tabs.get(index));
        }
    }

    public void clearTabs() {
        tabs.clear();
        strip.removeAllViews();
        selectedIndex = -1;
        updateIndicator(false);
        requestLayout();
    }

    public int getTabCount() {
        return tabs.size();
    }

    public MaterialTab getTabAt(int index) {
        return index >= 0 && index < tabs.size() ? tabs.get(index) : null;
    }

    public int getSelectedTabIndex() {
        return selectedIndex;
    }

    public MaterialTab getSelectedTab() {
        return getTabAt(selectedIndex);
    }

    public void setSelectedTab(int index) {
        setSelectedTab(index, true);
    }

    public void setSelectedTab(int index, boolean animate) {
        if (index < 0 || index >= tabs.size()) {
            return;
        }
        if (index == selectedIndex) {
            if (onTabSelectedListener != null) {
                onTabSelectedListener.onTabReselected(tabs.get(index));
            }
            return;
        }
        setSelectedTabInternal(index, animate);
        if (boundPager != null && !syncingFromPager) {
            boundPager.setCurrentItem(index, true);
        }
    }

    public void bindTo(ViewPager viewPager) {
        if (viewPager == null) {
            unbind();
            return;
        }
        if (boundPager == viewPager) {
            return;
        }
        unbind();

        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            int pageCount = adapter.getCount();
            if (getTabCount() != pageCount) {
                clearTabs();
                for (int i = 0; i < pageCount; i++) {
                    addTab(adapter.getPageTitle(i));
                }
            }
        }

        boundPager = viewPager;
        pageListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < getTabCount() && position != selectedIndex) {
                    syncingFromPager = true;
                    setSelectedTab(position, true);
                    syncingFromPager = false;
                }
            }
        };
        viewPager.addOnPageChangeListener(pageListener);

        int current = viewPager.getCurrentItem();
        if (current != selectedIndex && current >= 0 && current < getTabCount()) {
            syncingFromPager = true;
            setSelectedTab(current, false);
            syncingFromPager = false;
        }
    }

    public void unbind() {
        if (boundPager != null && pageListener != null) {
            boundPager.removeOnPageChangeListener(pageListener);
        }
        boundPager = null;
        pageListener = null;
    }

    public boolean isBound() {
        return boundPager != null;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.onTabSelectedListener = listener;
    }

    public OnTabSelectedListener getOnTabSelectedListener() {
        return onTabSelectedListener;
    }

    public void setStyle(TabStyle tabStyle) {
        if (tabStyle == null || this.tabStyle == tabStyle) {
            return;
        }
        this.tabStyle = tabStyle;
        updateIndicator(false);
        invalidate();
    }

    public TabStyle getStyle() {
        return tabStyle;
    }

    public void setMode(TabMode tabMode) {
        if (tabMode == null || this.tabMode == tabMode) {
            return;
        }
        this.tabMode = tabMode;
        applyTabSizes();
        updateIndicator(false);
        requestLayout();
    }

    public TabMode getMode() {
        return tabMode;
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        hasContainerColor = false;
        hasActiveTabColor = false;
        hasInactiveTabColor = false;
        hasIndicatorColor = false;
        hasDividerColor = false;
        applyContainerColor();
        applyTabStates();
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setContainerColor(int color) {
        containerColor = color;
        hasContainerColor = true;
        applyContainerColor();
    }

    public int getContainerColor() {
        return resolveContainerColor();
    }

    public boolean hasContainerColor() {
        return hasContainerColor;
    }

    public void clearContainerColor() {
        hasContainerColor = false;
        applyContainerColor();
    }

    public void setActiveTabColor(int color) {
        activeTabColor = color;
        hasActiveTabColor = true;
        applyTabStates();
    }

    public int getActiveTabColor() {
        return resolveActiveTabColor();
    }

    public boolean hasActiveTabColor() {
        return hasActiveTabColor;
    }

    public void clearActiveTabColor() {
        hasActiveTabColor = false;
        applyTabStates();
    }

    public void setInactiveTabColor(int color) {
        inactiveTabColor = color;
        hasInactiveTabColor = true;
        applyTabStates();
    }

    public int getInactiveTabColor() {
        return resolveInactiveTabColor();
    }

    public boolean hasInactiveTabColor() {
        return hasInactiveTabColor;
    }

    public void clearInactiveTabColor() {
        hasInactiveTabColor = false;
        applyTabStates();
    }

    public void setIndicatorColor(int color) {
        indicatorColor = color;
        hasIndicatorColor = true;
        strip.invalidate();
    }

    public int getIndicatorColor() {
        return resolveIndicatorColor();
    }

    public boolean hasIndicatorColor() {
        return hasIndicatorColor;
    }

    public void clearIndicatorColor() {
        hasIndicatorColor = false;
        strip.invalidate();
    }

    public void setDividerColor(int color) {
        dividerColor = color;
        hasDividerColor = true;
        strip.invalidate();
    }

    public int getDividerColor() {
        return resolveDividerColor();
    }

    public boolean hasDividerColor() {
        return hasDividerColor;
    }

    public void clearDividerColor() {
        hasDividerColor = false;
        strip.invalidate();
    }

    public void setIndicatorThicknessDp(float indicatorThicknessDp) {
        this.indicatorThicknessDp = Math.max(0.0f, indicatorThicknessDp);
        strip.invalidate();
    }

    public float getIndicatorThicknessDp() {
        return resolveIndicatorThicknessDp();
    }

    public void clearIndicatorThicknessDp() {
        indicatorThicknessDp = -1.0f;
        strip.invalidate();
    }

    public void setDividerEnabled(boolean dividerEnabled) {
        if (this.dividerEnabled != dividerEnabled) {
            this.dividerEnabled = dividerEnabled;
            strip.invalidate();
        }
    }

    public boolean isDividerEnabled() {
        return dividerEnabled;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (indicatorAnimator == null) {
            updateIndicator(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelIndicatorAnimation();
    }

    private void setSelectedTabInternal(int index, boolean animate) {
        MaterialTab previous = getSelectedTab();
        selectedIndex = index;
        applyTabStates();
        updateIndicator(animate);
        scrollTabIntoView(tabs.get(index));
        if (onTabSelectedListener != null) {
            if (previous != null && previous != tabs.get(index)) {
                onTabSelectedListener.onTabUnselected(previous);
            }
            onTabSelectedListener.onTabSelected(tabs.get(index));
        }
    }

    private void applyTabStates() {
        int active = resolveActiveTabColor();
        int inactive = resolveInactiveTabColor();
        for (int i = 0; i < tabs.size(); i++) {
            MaterialTab tab = tabs.get(i);
            tab.applyTabAppearance(colorScheme, active, inactive);
            tab.setTabSelected(i == selectedIndex);
        }
    }

    private void applyContainerColor() {
        setBackground(new ColorDrawable(resolveContainerColor()));
    }

    private void applyTabSizes() {
        boolean fixed = tabMode == TabMode.FIXED;
        setFillViewport(true);
        int heightPx = (int) (dp(resolveTabHeightDp()) + 0.5f);
        int minWidthPx = (int) (dp(TAB_MIN_WIDTH_DP) + 0.5f);
        for (MaterialTab tab : tabs) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(0, heightPx);
            }
            params.width = fixed ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT;
            params.weight = fixed ? 1.0f : 0.0f;
            params.height = heightPx;
            tab.setLayoutParams(params);
            tab.setMinimumWidth(fixed ? 0 : minWidthPx);
        }
        ViewGroup.LayoutParams stripParams = strip.getLayoutParams();
        stripParams.height = heightPx;
        strip.setLayoutParams(stripParams);
    }

    private LinearLayout.LayoutParams createTabParams() {
        return new LinearLayout.LayoutParams(0, (int) (dp(TAB_HEIGHT_DP) + 0.5f));
    }

    private float resolveTabHeightDp() {
        return TAB_HEIGHT_DP;
    }

    private void updateIndicator(boolean animate) {
        if (selectedIndex < 0 || selectedIndex >= tabs.size()) {
            indicatorLeft = 0.0f;
            indicatorRight = 0.0f;
            strip.invalidate();
            return;
        }
        MaterialTab tab = tabs.get(selectedIndex);
        float left = tab.getLeft();
        float right = tab.getRight();
        if (tabStyle == TabStyle.PRIMARY) {
            float center = (left + right) / 2.0f;
            float labelWidth = Math.min(tab.getWidth(), tab.getLabelWidth());
            left = center - labelWidth / 2.0f;
            right = center + labelWidth / 2.0f;
        }
        if (!animate || strip.getWidth() == 0 || indicatorRight <= indicatorLeft) {
            cancelIndicatorAnimation();
            indicatorLeft = left;
            indicatorRight = right;
            strip.invalidate();
            return;
        }
        animateIndicatorTo(left, right);
    }

    private void animateIndicatorTo(float targetLeft, float targetRight) {
        cancelIndicatorAnimation();
        indicatorTargetLeft = targetLeft;
        indicatorTargetRight = targetRight;
        leftSpring = new SpringSimulation(SPRING_STIFFNESS, SPRING_DAMPING);
        leftSpring.setPosition(indicatorLeft);
        leftSpring.setTarget(targetLeft);
        rightSpring = new SpringSimulation(SPRING_STIFFNESS, SPRING_DAMPING);
        rightSpring.setPosition(indicatorRight);
        rightSpring.setTarget(targetRight);
        lastFrameTime = System.nanoTime();
        indicatorAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        indicatorAnimator.setDuration((long) ANIM_DURATION_MS);
        indicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                delta = Math.min(delta, 0.05f);

                indicatorLeft = leftSpring.update(delta);
                indicatorRight = rightSpring.update(delta);
                strip.invalidate();

                if (leftSpring.isAtRest() && rightSpring.isAtRest()) {
                    animation.cancel();
                }
            }
        });
        indicatorAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (indicatorAnimator == animation) {
                    indicatorAnimator = null;
                    indicatorLeft = indicatorTargetLeft;
                    indicatorRight = indicatorTargetRight;
                    strip.invalidate();
                }
            }
        });
        indicatorAnimator.start();
    }

    private void cancelIndicatorAnimation() {
        if (indicatorAnimator != null) {
            ValueAnimator animator = indicatorAnimator;
            indicatorAnimator = null;
            animator.cancel();
        }
    }

    private void scrollTabIntoView(MaterialTab tab) {
        if (tabMode != TabMode.SCROLLABLE || tab.getWidth() == 0) {
            return;
        }
        int scrollX = getScrollX();
        int viewportWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int tabLeft = tab.getLeft();
        int tabRight = tab.getRight();
        if (tabLeft < scrollX) {
            smoothScrollTo(Math.max(0, tabLeft - (viewportWidth - tab.getWidth()) / 2), 0);
        } else if (tabRight > scrollX + viewportWidth) {
            smoothScrollTo(Math.max(0, tabRight - viewportWidth
                    + (viewportWidth - tab.getWidth()) / 2), 0);
        }
    }

    private int resolveContainerColor() {
        return hasContainerColor ? containerColor : dynamicColors.surface().getArgb(colorScheme);
    }

    private int resolveActiveTabColor() {
        return hasActiveTabColor ? activeTabColor : dynamicColors.primary().getArgb(colorScheme);
    }

    private int resolveInactiveTabColor() {
        return hasInactiveTabColor
                ? inactiveTabColor
                : dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private int resolveIndicatorColor() {
        return hasIndicatorColor ? indicatorColor : dynamicColors.primary().getArgb(colorScheme);
    }

    private int resolveDividerColor() {
        return hasDividerColor
                ? dividerColor
                : dynamicColors.outlineVariant().getArgb(colorScheme);
    }

    private float resolveIndicatorThicknessDp() {
        if (indicatorThicknessDp >= 0.0f) {
            return indicatorThicknessDp;
        }
        return INDICATOR_HEIGHT_DP;
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }

    private final class TabStrip extends LinearLayout {

        TabStrip(Context context) {
            super(context);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (dividerEnabled) {
                dividerPaint.setColor(resolveDividerColor());
                float dividerHeight = Math.max(1.0f, dp(DIVIDER_HEIGHT_DP));
                canvas.drawRect(0, getHeight() - dividerHeight, getWidth(), getHeight(), dividerPaint);
            }
            super.dispatchDraw(canvas);
            if (indicatorRight <= indicatorLeft) {
                return;
            }
            float thickness = dp(resolveIndicatorThicknessDp());
            if (thickness <= 0.0f) {
                return;
            }
            float bottom = getHeight();
            indicatorRect.set(indicatorLeft, bottom - thickness, indicatorRight, bottom);
            indicatorPaint.setColor(resolveIndicatorColor());
            float radius = thickness / 2.0f;
            canvas.drawRoundRect(indicatorRect, radius, radius, indicatorPaint);
        }
    }
}
