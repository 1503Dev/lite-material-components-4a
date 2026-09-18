package dev1503.lmc4a.v3.widget.navigationrail;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialNavigationRail extends ViewGroup {

    public interface OnItemSelectedListener {
        void onItemSelected(int position);

        void onItemReselected(int position);
    }

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float HORIZONTAL_ITEM_HEIGHT_DP = 72.0f;
    private static final float VERTICAL_ITEM_WIDTH_DP = 98.0f;
    private static final float VERTICAL_ITEM_HEIGHT_DP = 64.0f;
    private static final float DEFAULT_RAIL_WIDTH_DP = 360.0f;
    private static final float DEFAULT_RAIL_HEIGHT_DP = 72.0f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private NavigationRailOrientation orientation = NavigationRailOrientation.HORIZONTAL;
    private NavigationRailLabelVisibilityMode labelVisibilityMode = NavigationRailLabelVisibilityMode.AUTO;
    private DynamicScheme colorScheme = publicColorScheme;

    private ViewPager boundPager;
    private ViewPager.SimpleOnPageChangeListener pageListener;
    private boolean syncingFromPager;
    private int selectedIndex = -1;
    private final List<OnItemSelectedListener> itemSelectedListeners = new ArrayList<>();

    private final CompoundButton.OnCheckedChangeListener itemCheckListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        return;
                    }
                    int index = indexOfChild(buttonView);
                    for (int i = 0; i < getChildCount(); i++) {
                        MaterialNavigationRailItemView sibling = getItem(i);
                        if (sibling != null && sibling != buttonView && sibling.isChecked()) {
                            sibling.setChecked(false);
                            break;
                        }
                    }
                    if (index != selectedIndex) {
                        selectedIndex = index;
                        for (OnItemSelectedListener listener : itemSelectedListeners) {
                            listener.onItemSelected(index);
                        }
                    } else if (index >= 0) {
                        for (OnItemSelectedListener listener : itemSelectedListeners) {
                            listener.onItemReselected(index);
                        }
                    }
                    if (boundPager != null && !syncingFromPager && index >= 0) {
                        boundPager.setCurrentItem(index, true);
                    }
                }
            };

    public MaterialNavigationRail(Context context) {
        this(context, null);
    }

    public MaterialNavigationRail(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialNavigationRail(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOrientation(NavigationRailOrientation orientation) {
        if (orientation == null || this.orientation == orientation) {
            return;
        }
        this.orientation = orientation;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setLayoutParams(defaultParamsFor(orientation));
        }
        requestLayout();
    }

    public NavigationRailOrientation getOrientation() {
        return orientation;
    }

    public void setLabelVisibilityMode(NavigationRailLabelVisibilityMode labelVisibilityMode) {
        if (labelVisibilityMode == null) {
            labelVisibilityMode = NavigationRailLabelVisibilityMode.AUTO;
        }
        if (this.labelVisibilityMode != labelVisibilityMode) {
            this.labelVisibilityMode = labelVisibilityMode;
            for (int i = 0; i < getChildCount(); i++) {
                MaterialNavigationRailItemView item = getItem(i);
                if (item != null) {
                    item.setLabelVisibilityMode(labelVisibilityMode);
                }
            }
            requestLayout();
        }
    }

    public NavigationRailLabelVisibilityMode getLabelVisibilityMode() {
        return labelVisibilityMode;
    }

    public void setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof MaterialNavigationRailItemView) {
                ((MaterialNavigationRailItemView) child).setColorScheme(colorScheme);
            }
        }
        invalidate();
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public void addOnItemSelectedListener(OnItemSelectedListener listener) {
        if (listener != null && !itemSelectedListeners.contains(listener)) {
            itemSelectedListeners.add(listener);
        }
    }

    public void removeOnItemSelectedListener(OnItemSelectedListener listener) {
        itemSelectedListeners.remove(listener);
    }

    public void clearOnItemSelectedListeners() {
        itemSelectedListeners.clear();
    }

    public int getSelectedItemPosition() {
        return selectedIndex;
    }

    public MaterialNavigationRailItemView getSelectedItem() {
        return getItem(selectedIndex);
    }

    public void addItem(MaterialNavigationRailItem item) {
        addItem(item, -1);
    }

    public void addItem(MaterialNavigationRailItem item, int index) {
        if (item == null) {
            return;
        }
        MaterialNavigationRailItemView view = new MaterialNavigationRailItemView(getContext());
        view.setItemData(item);
        view.setIcon(item.getIcon());
        view.setText(item.getText());
        addItem(view, index);
    }

    public void addItem(MaterialNavigationRailItemView item) {
        addItem(item, -1);
    }

    public void addItem(MaterialNavigationRailItemView item, int index) {
        if (item == null) {
            return;
        }
        if (item.getParent() != null) {
            ((ViewGroup) item.getParent()).removeView(item);
        }
        int insertAt = index < 0 || index > getChildCount() ? getChildCount() : index;
        item.setColorScheme(colorScheme);
        item.setLabelVisibilityMode(labelVisibilityMode);
        item.setLayoutParams(defaultParamsFor(orientation));
        item.setOnCheckedChangeListener(itemCheckListener);
        addView(item, insertAt);
        if (insertAt <= selectedIndex) {
            selectedIndex++;
        }
        requestLayout();
    }

    public void removeItem(MaterialNavigationRailItem item) {
        if (item == null) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            MaterialNavigationRailItemView view = getItem(i);
            if (view != null && view.getItemData() == item) {
                removeItem(view);
                return;
            }
        }
    }

    public void removeItem(MaterialNavigationRailItemView item) {
        if (item == null || item.getParent() != this) {
            return;
        }
        int removedIndex = indexOfChild(item);
        item.setOnCheckedChangeListener(null);
        removeView(item);
        if (removedIndex >= 0) {
            if (removedIndex == selectedIndex) {
                selectedIndex = -1;
            } else if (removedIndex < selectedIndex) {
                selectedIndex--;
            }
        }
        requestLayout();
    }

    public void removeItem(int index) {
        MaterialNavigationRailItemView item = getItem(index);
        if (item != null) {
            removeItem(item);
        }
    }

    public int getItemCount() {
        return getChildCount();
    }

    public MaterialNavigationRailItemView getItem(int index) {
        if (index < 0 || index >= getChildCount()) {
            return null;
        }
        View child = getChildAt(index);
        return child instanceof MaterialNavigationRailItemView
                ? (MaterialNavigationRailItemView) child : null;
    }

    public void bindTo(ViewPager viewPager) {
        if (viewPager == null) {
            unbindViewPager();
            return;
        }
        if (boundPager == viewPager) {
            return;
        }
        unbindViewPager();

        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            int pageCount = adapter.getCount();
            if (getItemCount() != pageCount) {
                for (int i = getChildCount() - 1; i >= 0; i--) {
                    removeItem(i);
                }
                for (int i = 0; i < pageCount; i++) {
                    MaterialNavigationRailItemView item = new MaterialNavigationRailItemView(getContext());
                    CharSequence title = adapter.getPageTitle(i);
                    if (title != null) {
                        item.setText(title);
                    }
                    addItem(item);
                }
            }
        }

        boundPager = viewPager;
        pageListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MaterialNavigationRailItemView item = getItem(position);
                if (item != null && !item.isChecked()) {
                    syncingFromPager = true;
                    item.setChecked(true);
                    syncingFromPager = false;
                }
            }
        };
        viewPager.addOnPageChangeListener(pageListener);

        int current = viewPager.getCurrentItem();
        MaterialNavigationRailItemView currentItem = getItem(current);
        if (currentItem != null) {
            syncingFromPager = true;
            currentItem.setChecked(true);
            syncingFromPager = false;
        }
    }

    public void unbindViewPager() {
        if (boundPager != null && pageListener != null) {
            boundPager.removeOnPageChangeListener(pageListener);
        }
        boundPager = null;
        pageListener = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        if (childCount == 0) {
            int emptyWidth = View.resolveSize(
                    (int) (dp(orientation == NavigationRailOrientation.HORIZONTAL
                            ? DEFAULT_RAIL_WIDTH_DP : VERTICAL_ITEM_WIDTH_DP) + 0.5f),
                    widthMeasureSpec);
            int emptyHeight = View.resolveSize(
                    (int) (dp(orientation == NavigationRailOrientation.HORIZONTAL
                            ? DEFAULT_RAIL_HEIGHT_DP : VERTICAL_ITEM_HEIGHT_DP) + 0.5f),
                    heightMeasureSpec);
            setMeasuredDimension(emptyWidth, emptyHeight);
            return;
        }

        if (orientation == NavigationRailOrientation.HORIZONTAL) {
            int width = View.resolveSize((int) (dp(DEFAULT_RAIL_WIDTH_DP) + 0.5f), widthMeasureSpec);
            int height = View.resolveSize((int) (dp(HORIZONTAL_ITEM_HEIGHT_DP) + 0.5f), heightMeasureSpec);
            for (int i = 0; i < childCount; i++) {
                int itemWidth = (width * (i + 1)) / childCount - (width * i) / childCount;
                measureChild(getChildAt(i),
                        View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            }
            setMeasuredDimension(width, height);
        } else {
            int itemWidth = (int) (dp(VERTICAL_ITEM_WIDTH_DP) + 0.5f);
            int width = View.resolveSize(itemWidth, widthMeasureSpec);
            int totalHeight = 0;
            for (int i = 0; i < childCount; i++) {
                measureChild(getChildAt(i),
                        View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec((int) (dp(VERTICAL_ITEM_HEIGHT_DP) + 0.5f),
                                View.MeasureSpec.EXACTLY));
                totalHeight += getChildAt(i).getMeasuredHeight();
            }
            int height = View.resolveSize(totalHeight, heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int width = r - l;
        int height = b - t;
        if (orientation == NavigationRailOrientation.HORIZONTAL) {
            for (int i = 0; i < childCount; i++) {
                int childLeft = (width * i) / childCount;
                int childRight = (width * (i + 1)) / childCount;
                getChildAt(i).layout(childLeft, 0, childRight, height);
            }
        } else {
            int itemWidth = (int) (dp(VERTICAL_ITEM_WIDTH_DP) + 0.5f);
            int childLeft = (width - itemWidth) / 2;
            int childTop = 0;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int childBottom = childTop + child.getMeasuredHeight();
                child.layout(childLeft, childTop, childLeft + itemWidth, childBottom);
                childTop = childBottom;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(dynamicColors.surfaceContainer().getArgb(colorScheme));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbindViewPager();
    }

    private LinearLayout.LayoutParams defaultParamsFor(NavigationRailOrientation orientation) {
        if (orientation == NavigationRailOrientation.HORIZONTAL) {
            return new LinearLayout.LayoutParams(
                    0, (int) (dp(HORIZONTAL_ITEM_HEIGHT_DP) + 0.5f), 1.0f);
        }
        return new LinearLayout.LayoutParams(
                (int) (dp(VERTICAL_ITEM_WIDTH_DP) + 0.5f),
                (int) (dp(VERTICAL_ITEM_HEIGHT_DP) + 0.5f));
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}