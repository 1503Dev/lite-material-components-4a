package dev1503.lmc4a.v3.widget.topappbar;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class MaterialTopAppBar extends LinearLayout {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float ICON_ROW_HEIGHT_DP = 64.0f;
    private static final float ICON_BUTTON_SIZE_DP = 48.0f;
    private static final float SLOT_EDGE_INSET_DP = 4.0f;
    private static final float TITLE_HORIZONTAL_PADDING_DP = 16.0f;
    private static final float TITLE_BOTTOM_PADDING_DP = 16.0f;
    private static final float DEFAULT_SUBTITLE_TEXT_SIZE_SP = 14.0f;
    public static final float DEFAULT_SCROLLED_UNDER_ELEVATION_DP = 3.0f;
    private static final String TITLE_FONT_FAMILY = "sans-serif";

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private final LinearLayout topRow;
    private final LinearLayout startContainer;
    private final LinearLayout endContainer;
    private final LinearLayout titleContainer;
    private final LinearLayout expandedTitleRow;
    private final TextView titleView;
    private final TextView subtitleView;
    private final View topRowSpacer;

    private TopAppBarVariant variant = TopAppBarVariant.SMALL;
    private DynamicScheme colorScheme = publicColorScheme;

    private int containerColor;
    private int scrolledUnderContainerColor;
    private int titleColor;
    private int subtitleColor;
    private int iconColor;
    private float elevationDp;
    private float scrolledUnderElevationDp = DEFAULT_SCROLLED_UNDER_ELEVATION_DP;
    private float iconButtonSizeDp = ICON_BUTTON_SIZE_DP;
    private float iconButtonCornerRadiusDp = -1.0f;
    private boolean scrolledUnder;
    private boolean hasTitleTextSize;

    private View boundScrollable;
    private ViewTreeObserver.OnScrollChangedListener scrollListener;

    public MaterialTopAppBar(Context context) {
        this(context, null);
    }

    public MaterialTopAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);

        topRow = new LinearLayout(context);
        topRow.setOrientation(HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        startContainer = new LinearLayout(context);
        startContainer.setOrientation(HORIZONTAL);
        startContainer.setGravity(Gravity.CENTER_VERTICAL);
        setRelativePadding(startContainer, SLOT_EDGE_INSET_DP, 0.0f, 0.0f);

        endContainer = new LinearLayout(context);
        endContainer.setOrientation(HORIZONTAL);
        endContainer.setGravity(Gravity.CENTER_VERTICAL);
        setRelativePadding(endContainer, 0.0f, SLOT_EDGE_INSET_DP, 0.0f);

        titleContainer = new LinearLayout(context);
        titleContainer.setOrientation(VERTICAL);

        titleView = new TextView(context);
        titleView.setTypeface(Typeface.create(TITLE_FONT_FAMILY, Typeface.NORMAL));
        titleView.setSingleLine(true);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            titleView.setAccessibilityHeading(true);
        }

        subtitleView = new TextView(context);
        subtitleView.setTextSize(DEFAULT_SUBTITLE_TEXT_SIZE_SP);
        subtitleView.setSingleLine(true);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        subtitleView.setVisibility(GONE);

        titleContainer.addView(titleView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleContainer.addView(subtitleView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        topRowSpacer = new View(context);

        topRow.addView(startContainer, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        topRow.addView(endContainer, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        expandedTitleRow = new LinearLayout(context);
        expandedTitleRow.setOrientation(VERTICAL);

        addView(topRow, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        applyColorSchemeRoles();
        applyVariant();
        refreshColors();
    }

    public void setVariant(TopAppBarVariant variant) {
        if (variant == null) {
            variant = TopAppBarVariant.SMALL;
        }
        if (this.variant == variant) {
            return;
        }
        this.variant = variant;
        applyVariant();
        refreshColors();
    }

    public TopAppBarVariant getVariant() {
        return variant;
    }

    private void applyVariant() {
        removeFromParent(titleContainer);
        removeFromParent(topRowSpacer);
        removeFromParent(expandedTitleRow);

        LayoutParams topRowParams = (LayoutParams) topRow.getLayoutParams();
        topRowParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        topRowParams.height = variant.isTwoRow()
                ? (int) (dp(ICON_ROW_HEIGHT_DP) + 0.5f)
                : ViewGroup.LayoutParams.MATCH_PARENT;
        topRow.setLayoutParams(topRowParams);

        if (variant.isTwoRow()) {
            topRow.addView(topRowSpacer, 1, new LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            titleContainer.setGravity(Gravity.BOTTOM | Gravity.START);
            setRelativePadding(titleContainer, TITLE_HORIZONTAL_PADDING_DP,
                    TITLE_HORIZONTAL_PADDING_DP, TITLE_BOTTOM_PADDING_DP);
            expandedTitleRow.addView(titleContainer, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(expandedTitleRow, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        } else {
            topRow.addView(titleContainer, 1, new LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            titleContainer.setGravity(variant.isTitleCentered()
                    ? Gravity.CENTER
                    : (Gravity.CENTER_VERTICAL | Gravity.START));
            setRelativePadding(titleContainer, TITLE_HORIZONTAL_PADDING_DP,
                    TITLE_HORIZONTAL_PADDING_DP, 0.0f);
        }

        if (!hasTitleTextSize) {
            titleView.setTextSize(variant.getTitleTextSizeSp());
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = (int) (dp(variant.getContainerHeightDp()) + 0.5f)
                + getPaddingTop() + getPaddingBottom();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resolvedHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resolvedHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST && heightSize > 0) {
            resolvedHeight = Math.min(desiredHeight, heightSize);
        } else {
            resolvedHeight = desiredHeight;
        }
        super.onMeasure(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(Math.max(resolvedHeight, 1), MeasureSpec.EXACTLY));
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public CharSequence getTitle() {
        return titleView.getText();
    }

    public void setSubtitle(CharSequence subtitle) {
        subtitleView.setText(subtitle);
        subtitleView.setVisibility(
                subtitle == null || subtitle.length() == 0 ? GONE : VISIBLE);
    }

    public CharSequence getSubtitle() {
        return subtitleView.getText();
    }

    public void setTitleTextSizeSp(float titleTextSizeSp) {
        titleView.setTextSize(titleTextSizeSp);
        hasTitleTextSize = true;
    }

    public void clearTitleTextSizeSp() {
        hasTitleTextSize = false;
        titleView.setTextSize(variant.getTitleTextSizeSp());
    }

    public float getTitleTextSizeSp() {
        return titleView.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
    }

    public void setSubtitleTextSizeSp(float subtitleTextSizeSp) {
        subtitleView.setTextSize(subtitleTextSizeSp);
    }

    public void clearSubtitleTextSizeSp() {
        subtitleView.setTextSize(DEFAULT_SUBTITLE_TEXT_SIZE_SP);
    }

    public float getSubtitleTextSizeSp() {
        return subtitleView.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
    }

    public void setTitleTextColor(int color) {
        titleColor = color;
        titleView.setTextColor(color);
    }

    public int getTitleTextColor() {
        return titleColor;
    }

    public void clearTitleTextColor() {
        titleColor = dynamicColors.onSurface().getArgb(colorScheme);
        titleView.setTextColor(titleColor);
    }

    public void setSubtitleTextColor(int color) {
        subtitleColor = color;
        subtitleView.setTextColor(color);
    }

    public int getSubtitleTextColor() {
        return subtitleColor;
    }

    public void clearSubtitleTextColor() {
        subtitleColor = dynamicColors.onSurfaceVariant().getArgb(colorScheme);
        subtitleView.setTextColor(subtitleColor);
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme == null ? publicColorScheme : colorScheme;
        applyColorSchemeRoles();
        refreshColors();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void setContainerColor(int color) {
        containerColor = color;
        refreshColors();
    }

    public int getContainerColor() {
        return containerColor;
    }

    public void clearContainerColor() {
        containerColor = dynamicColors.surface().getArgb(colorScheme);
        refreshColors();
    }

    public void setScrolledUnderContainerColor(int color) {
        scrolledUnderContainerColor = color;
        refreshColors();
    }

    public int getScrolledUnderContainerColor() {
        return scrolledUnderContainerColor;
    }

    public void clearScrolledUnderContainerColor() {
        scrolledUnderContainerColor = dynamicColors.surfaceContainer().getArgb(colorScheme);
        refreshColors();
    }

    public void setIconColor(int color) {
        iconColor = color;
        applyIconColors();
    }

    public int getIconColor() {
        return iconColor;
    }

    public void clearIconColor() {
        iconColor = dynamicColors.onSurfaceVariant().getArgb(colorScheme);
        applyIconColors();
    }

    public void setScrolledUnder(boolean scrolledUnder) {
        if (this.scrolledUnder == scrolledUnder) {
            return;
        }
        this.scrolledUnder = scrolledUnder;
        refreshColors();
    }

    public boolean isScrolledUnder() {
        return scrolledUnder;
    }

    public void setScrolledUnderElevationDp(float elevationDp) {
        scrolledUnderElevationDp = elevationDp;
        applyElevation();
    }

    public float getScrolledUnderElevationDp() {
        return scrolledUnderElevationDp;
    }

    public void clearScrolledUnderElevationDp() {
        scrolledUnderElevationDp = DEFAULT_SCROLLED_UNDER_ELEVATION_DP;
        applyElevation();
    }

    public void bindTo(final View scrollable) {
        unbind();
        if (scrollable == null) {
            setScrolledUnder(false);
            return;
        }
        boundScrollable = scrollable;
        scrollListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (boundScrollable != null) {
                    setScrolledUnder(boundScrollable.getScrollY() > 0);
                }
            }
        };
        boundScrollable.getViewTreeObserver().addOnScrollChangedListener(scrollListener);
        setScrolledUnder(boundScrollable.getScrollY() > 0);
    }

    public void unbind() {
        if (boundScrollable != null && scrollListener != null) {
            ViewTreeObserver observer = boundScrollable.getViewTreeObserver();
            if (observer.isAlive()) {
                observer.removeOnScrollChangedListener(scrollListener);
            }
        }
        boundScrollable = null;
        scrollListener = null;
    }

    public boolean isBound() {
        return boundScrollable != null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbind();
    }

    public void setElevationDp(float elevationDp) {
        this.elevationDp = elevationDp;
        applyElevation();
    }

    public float getElevationDp() {
        return elevationDp;
    }

    public void clearElevationDp() {
        elevationDp = 0.0f;
        applyElevation();
    }

    public void setIconButtonSizeDp(float iconButtonSizeDp) {
        this.iconButtonSizeDp = iconButtonSizeDp;
        for (int i = 0; i < startContainer.getChildCount(); i++) {
            applySlotParams(startContainer.getChildAt(i));
        }
        for (int i = 0; i < endContainer.getChildCount(); i++) {
            applySlotParams(endContainer.getChildAt(i));
        }
    }

    public float getIconButtonSizeDp() {
        return iconButtonSizeDp;
    }

    public void clearIconButtonSizeDp() {
        setIconButtonSizeDp(ICON_BUTTON_SIZE_DP);
    }

    public void setIconButtonCornerRadiusDp(float cornerRadiusDp) {
        this.iconButtonCornerRadiusDp = cornerRadiusDp;
        applyIconColors();
    }

    public float getIconButtonCornerRadiusDp() {
        return iconButtonCornerRadiusDp >= 0.0f ? iconButtonCornerRadiusDp : iconButtonSizeDp / 2.0f;
    }

    public void clearIconButtonCornerRadiusDp() {
        iconButtonCornerRadiusDp = -1.0f;
        applyIconColors();
    }

    public int addStartView(View view) {
        return addStartView(view, -1);
    }

    public int addStartView(View view, int index) {
        return addViewTo(startContainer, view, index);
    }

    public int removeStartView(View view) {
        return removeViewFrom(startContainer, view);
    }

    public int removeStartView(int index) {
        return removeViewAt(startContainer, index);
    }

    public int getStartViewCount() {
        return startContainer.getChildCount();
    }

    public View getStartView(int index) {
        return startContainer.getChildAt(index);
    }

    public int addEndView(View view) {
        return addEndView(view, -1);
    }

    public int addEndView(View view, int index) {
        return addViewTo(endContainer, view, index);
    }

    public int removeEndView(View view) {
        return removeViewFrom(endContainer, view);
    }

    public int removeEndView(int index) {
        return removeViewAt(endContainer, index);
    }

    public int getEndViewCount() {
        return endContainer.getChildCount();
    }

    public View getEndView(int index) {
        return endContainer.getChildAt(index);
    }

    private int addViewTo(LinearLayout container, View view, int index) {
        int count = container.getChildCount();
        int target = index >= 0 ? Math.min(index, count) : count;
        container.addView(view, target, slotParams(view));
        applySlotParams(view);
        return target;
    }

    private int removeViewFrom(LinearLayout container, View view) {
        int index = container.indexOfChild(view);
        if (index >= 0) {
            container.removeView(view);
        }
        return index;
    }

    private int removeViewAt(LinearLayout container, int index) {
        if (index < 0 || index >= container.getChildCount()) {
            return -1;
        }
        container.removeViewAt(index);
        return index;
    }

    private LayoutParams slotParams(View view) {
        if (view instanceof MaterialButton) {
            int size = (int) (dp(iconButtonSizeDp) + 0.5f);
            return new LayoutParams(size, size);
        }
        return new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void applySlotParams(View view) {
        if (!(view instanceof MaterialButton)) {
            return;
        }
        MaterialButton button = (MaterialButton) view;
        button.setIconColor(iconColor);
        button.setCornerRadiusDp(getIconButtonCornerRadiusDp());
        LayoutParams params = (LayoutParams) button.getLayoutParams();
        int size = (int) (dp(iconButtonSizeDp) + 0.5f);
        if (params == null) {
            button.setLayoutParams(new LayoutParams(size, size));
        } else if (params.width != size || params.height != size) {
            params.width = size;
            params.height = size;
            button.setLayoutParams(params);
        }
    }

    private void applyColorSchemeRoles() {
        containerColor = dynamicColors.surface().getArgb(colorScheme);
        scrolledUnderContainerColor = dynamicColors.surfaceContainer().getArgb(colorScheme);
        titleColor = dynamicColors.onSurface().getArgb(colorScheme);
        subtitleColor = dynamicColors.onSurfaceVariant().getArgb(colorScheme);
        iconColor = dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    private void refreshColors() {
        setBackground(new ColorDrawable(
                scrolledUnder ? scrolledUnderContainerColor : containerColor));
        titleView.setTextColor(titleColor);
        subtitleView.setTextColor(subtitleColor);
        applyElevation();
        applyIconColors();
    }

    private void applyElevation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(dp(scrolledUnder ? scrolledUnderElevationDp : elevationDp));
        }
    }

    private void applyIconColors() {
        for (int i = 0; i < startContainer.getChildCount(); i++) {
            applySlotParams(startContainer.getChildAt(i));
        }
        for (int i = 0; i < endContainer.getChildCount(); i++) {
            applySlotParams(endContainer.getChildAt(i));
        }
    }

    private void removeFromParent(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    private void setRelativePadding(View view, float startDp, float endDp, float bottomDp) {
        int start = (int) (dp(startDp) + 0.5f);
        int end = (int) (dp(endDp) + 0.5f);
        int bottom = (int) (dp(bottomDp) + 0.5f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setPaddingRelative(start, 0, end, bottom);
        } else {
            view.setPadding(start, 0, end, bottom);
        }
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
