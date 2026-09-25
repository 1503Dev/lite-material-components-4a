package dev1503.litematerial.lmc4a.app.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.navigationrail.MaterialNavigationRail;
import dev1503.lmc4a.v3.widget.navigationrail.MaterialNavigationRailItem;
import dev1503.lmc4a.v3.widget.navigationrail.MaterialNavigationRailItemView;
import dev1503.lmc4a.v3.widget.navigationrail.NavigationRailLabelVisibilityMode;
import dev1503.lmc4a.v3.widget.navigationrail.NavigationRailOrientation;
import dev1503.lmc4a.v3.widget.radiobutton.MaterialRadioButton;

public class NavigationRailDemoActivity extends DemoActivity {

    private static final int[] ICON_RES_IDS = {
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_manage,
            android.R.drawable.ic_menu_compass,
    };

    private static final String[] ITEM_LABELS = {"Home", "Gallery", "Manage", "Compass"};

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private MaterialNavigationRail horizontalRail;
    private MaterialNavigationRail itemRail;
    private MaterialNavigationRail verticalRail;
    private ViewPager viewPager;

    private int colorPrimaryContainer;
    private int colorOnPrimaryContainer;
    private int colorTertiaryContainer;
    private int colorOnTertiaryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);
        colorPrimaryContainer = dynamicColors.primaryContainer()
                .getArgb(MaterialNavigationRail.publicColorScheme);
        colorOnPrimaryContainer = dynamicColors.onPrimaryContainer()
                .getArgb(MaterialNavigationRail.publicColorScheme);
        colorTertiaryContainer = dynamicColors.tertiaryContainer()
                .getArgb(MaterialNavigationRail.publicColorScheme);
        colorOnTertiaryContainer = dynamicColors.onTertiaryContainer()
                .getArgb(MaterialNavigationRail.publicColorScheme);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("水平 rail（默认方向 NavigationRailOrientation.HORIZONTAL）：高 72dp，"
                + "4 个 item 平分宽度，由 MaterialNavigationRailItem 数据对象构建 —— "
                + "setIcon(Icon)（这里用 new Icon(resId)）与 setText(CharSequence)，"
                + "统一使用 Icon 包装类型。未覆盖任何颜色时：容器色为角色色 surfaceContainer、"
                + "选中项指示器 secondaryContainer、选中图标 / 文字 onSurface、未选中 onSurfaceVariant。"));
        horizontalRail = createDataRail(NavigationRailOrientation.HORIZONTAL);
        content.addView(horizontalRail, matchWrap());

        content.addView(caption("bindTo(ViewPager)：导航项与页码一一对应，切项翻页、翻页也会同步选中；"
                + "unbind() 解除绑定（旧 unbindViewPager() 已删除），isBound() 查询是否已绑定。"));
        viewPager = createViewPager();
        content.addView(viewPager, pagerParams());
        content.addView(controlRow(createBindControls()));

        content.addView(caption("setLabelVisibilityMode / getLabelVisibilityMode：AUTO（默认，项数 <5 时始终显示标签，"
                + "≥5 时只显示选中项）、LABELED（始终显示）、UNLABELED（始终隐藏）、SELECTED（只显示选中项）。"));
        content.addView(createModeGroup(horizontalRail), matchWrap());

        content.addView(caption("rail 级颜色覆盖（新增）：setContainerColor / setIndicatorColor / setIconColor / setTextColor "
                + "作用于所有未单独设置的 item，item 自身设置优先；getXxxColor() 返回当前生效色"
                + "（未覆盖时为角色色），hasXxxColor() 判断是否显式覆盖；"
                + "clearXxxColor() 分别恢复 surfaceContainer / secondaryContainer / onSurface / onSurfaceVariant；"
                + "setColorScheme(DynamicScheme) 会一次清除 rail 与所有 item 上的颜色覆盖。"));
        content.addView(controlRow(createRailColorControls()));

        content.addView(caption("item 级覆盖（MaterialNavigationRailItemView）：setContainerColor / setIndicatorColor / "
                + "setIconColor / setTextColor（每个都有 get / clear / has）、setLabelTextSizeSp（默认 12sp，"
                + "hasLabelTextSizeSp() 判断是否覆盖）、setIconSizeDp（默认 24dp）、setIndicatorWidthDp / "
                + "setIndicatorHeightDp（默认 56dp × 32dp，超出指示器区域时会被限制）。"
                + "第 1 项覆盖四种颜色、第 2 项放大字号与图标、第 3 项缩小指示器，第 4 项保持默认。"));
        itemRail = createItemViewRail();
        content.addView(itemRail, matchWrap());
        content.addView(controlRow(createItemColorControls()));
        content.addView(controlRow(createItemSizeControls()));

        content.addView(caption("垂直 rail（NavigationRailOrientation.VERTICAL）：item 宽 98dp / 高 64dp，"
                + "rail 收窄为单列；本例不绑定 ViewPager。addItem / removeItem / getItem(int) / getItemCount() "
                + "与 getSelectedItemPosition() / getSelectedItem() 都是保留 API。"));
        FrameLayout frame = new FrameLayout(this);
        frame.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(300.0f)));
        verticalRail = createDataRail(NavigationRailOrientation.VERTICAL);
        FrameLayout.LayoutParams railParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        railParams.gravity = Gravity.START;
        frame.addView(verticalRail, railParams);
        content.addView(frame);
        content.addView(controlRow(createItemListControls()));

        content.addView(caption("选中监听器：旧 addOnItemSelectedListener / removeOnItemSelectedListener / "
                + "clearOnItemSelectedListeners 已全部删除，统一为 setOnItemSelectedListener(...)（重复设置会替换，"
                + "传 null 即清除）与 getOnItemSelectedListener()；回调包含 onItemSelected（切到新项）与 "
                + "onItemReselected（重复点击当前项）。"));
        content.addView(controlRow(createListenerControls()));

        content.addView(spacer(32.0f));

        setContentView(scrollView);

        horizontalRail.bindTo(viewPager);
    }

    private LinearLayout createBindControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton bind = controlButton("bindTo(viewPager)");
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.bindTo(viewPager);
                toast("bindTo(viewPager) → isBound() = " + horizontalRail.isBound()
                        + "，getSelectedItemPosition() = " + horizontalRail.getSelectedItemPosition());
            }
        });
        row.addView(bind, controlParams());

        MaterialButton unbind = controlButton("unbind()");
        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.unbind();
                toast("unbind()（旧 unbindViewPager()）→ isBound() = " + horizontalRail.isBound());
            }
        });
        row.addView(unbind, controlParams());

        MaterialButton isBound = controlButton("isBound()");
        isBound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("isBound() = " + horizontalRail.isBound()
                        + "，getOrientation() = " + horizontalRail.getOrientation()
                        + "，getItemCount() = " + horizontalRail.getItemCount());
            }
        });
        row.addView(isBound, controlParams());

        MaterialButton selected = controlButton("getSelectedItem()");
        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = horizontalRail.getSelectedItem();
                toast("getSelectedItemPosition() = " + horizontalRail.getSelectedItemPosition()
                        + "，getSelectedItem() = "
                        + (item == null || item.getItemData() == null
                        ? "null（尚未选中）" : item.getItemData().getText()));
            }
        });
        row.addView(selected, controlParams());

        return row;
    }

    private LinearLayout createRailColorControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton setColors = controlButton("setRailColors");
        setColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.setContainerColor(colorPrimaryContainer);
                horizontalRail.setIndicatorColor(colorTertiaryContainer);
                horizontalRail.setIconColor(colorOnPrimaryContainer);
                horizontalRail.setTextColor(colorOnPrimaryContainer);
                toast("rail 级：容器 " + hex(horizontalRail.getContainerColor())
                        + "(primaryContainer)、指示器 " + hex(horizontalRail.getIndicatorColor())
                        + "(tertiaryContainer)、图标 / 文字 " + hex(horizontalRail.getIconColor())
                        + "；作用于所有未单独设置的 item");
            }
        });
        row.addView(setColors, controlParams());

        MaterialButton clearColors = controlButton("clearRailColors");
        clearColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.clearContainerColor();
                horizontalRail.clearIndicatorColor();
                horizontalRail.clearIconColor();
                horizontalRail.clearTextColor();
                toast("clear 后 getContainerColor() = " + horizontalRail.getContainerColor()
                        + " → surfaceContainer；指示器 "
                        + horizontalRail.getIndicatorColor() + " → secondaryContainer；"
                        + "图标 / 文字 " + horizontalRail.getIconColor() + " → onSurface / onSurfaceVariant");
            }
        });
        row.addView(clearColors, controlParams());

        MaterialButton scheme = controlButton("setColorScheme(public)");
        scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.setColorScheme(MaterialNavigationRail.publicColorScheme);
                toast("setColorScheme(publicColorScheme)：清除 rail 与所有 item 的颜色覆盖，"
                        + "getContainerColor() = " + horizontalRail.getContainerColor()
                        + "，getColorScheme() = "
                        + (horizontalRail.getColorScheme() == MaterialNavigationRail.publicColorScheme
                        ? "publicColorScheme" : "其它"));
            }
        });
        row.addView(scheme, controlParams());

        return row;
    }

    private LinearLayout createItemColorControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton setColors = controlButton("item1 setColors");
        setColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = itemRail.getItem(0);
                if (item == null) {
                    toast("itemRail.getItem(0) == null");
                    return;
                }
                item.setContainerColor(colorPrimaryContainer);
                item.setIndicatorColor(colorTertiaryContainer);
                item.setIconColor(colorOnPrimaryContainer);
                item.setTextColor(colorOnPrimaryContainer);
                toast("item 级覆盖：hasContainerColor() = " + item.hasContainerColor()
                        + "、hasIndicatorColor() = " + item.hasIndicatorColor()
                        + "、hasIconColor() = " + item.hasIconColor()
                        + "、hasTextColor() = " + item.hasTextColor()
                        + "（item 自身设置优先于 rail 级）");
            }
        });
        row.addView(setColors, controlParams());

        MaterialButton clearColors = controlButton("item1 clearColors");
        clearColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = itemRail.getItem(0);
                if (item == null) {
                    toast("itemRail.getItem(0) == null");
                    return;
                }
                item.clearContainerColor();
                item.clearIndicatorColor();
                item.clearIconColor();
                item.clearTextColor();
                toast("clear 后 getContainerColor() = " + item.getContainerColor()
                        + "、getIndicatorColor() = " + item.getIndicatorColor()
                        + "、getIconColor() = " + item.getIconColor()
                        + "、getTextColor() = " + item.getTextColor()
                        + "（未覆盖，跟随 rail 级 / 角色色），has 标记 "
                        + item.hasContainerColor() + "/" + item.hasIndicatorColor()
                        + "/" + item.hasIconColor() + "/" + item.hasTextColor());
            }
        });
        row.addView(clearColors, controlParams());

        MaterialButton railOverride = controlButton("rail 覆盖对比");
        railOverride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemRail.setContainerColor(colorTertiaryContainer);
                itemRail.setTextColor(colorOnTertiaryContainer);
                toast("itemRail.setContainerColor/setTextColor：未单独设置的 item 跟随 rail 级，"
                        + "第 1 项仍用自己的覆盖值 " + (itemRail.getItem(0) == null
                        ? "（getItem(0) == null）" : hex(itemRail.getItem(0).getTextColor())));
            }
        });
        row.addView(railOverride, controlParams());

        return row;
    }

    private LinearLayout createItemSizeControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton fontSize = controlButton("toggle 16sp 标签");
        fontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = itemRail.getItem(1);
                if (item == null) {
                    toast("itemRail.getItem(1) == null");
                    return;
                }
                if (item.hasLabelTextSizeSp()) {
                    item.clearLabelTextSizeSp();
                    toast("clearLabelTextSizeSp() → getLabelTextSizeSp() = "
                            + item.getLabelTextSizeSp() + "sp（默认 12sp）");
                } else {
                    item.setLabelTextSizeSp(16.0f);
                    toast("setLabelTextSizeSp(16) → getLabelTextSizeSp() = "
                            + item.getLabelTextSizeSp() + "sp、hasLabelTextSizeSp() = "
                            + item.hasLabelTextSizeSp());
                }
            }
        });
        row.addView(fontSize, controlParams());

        MaterialButton iconSize = controlButton("toggle 32dp 图标");
        iconSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = itemRail.getItem(1);
                if (item == null) {
                    toast("itemRail.getItem(1) == null");
                    return;
                }
                if (item.hasIconSizeDp()) {
                    item.clearIconSizeDp();
                    toast("clearIconSizeDp() → getIconSizeDp() = " + item.getIconSizeDp()
                            + "dp（默认 24dp）");
                } else {
                    item.setIconSizeDp(32.0f);
                    toast("setIconSizeDp(32) → getIconSizeDp() = " + item.getIconSizeDp()
                            + "dp、hasIconSizeDp() = " + item.hasIconSizeDp());
                }
            }
        });
        row.addView(iconSize, controlParams());

        MaterialButton indicatorSize = controlButton("toggle 指示器 40×24");
        indicatorSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = itemRail.getItem(2);
                if (item == null) {
                    toast("itemRail.getItem(2) == null");
                    return;
                }
                if (item.hasIndicatorWidthDp() || item.hasIndicatorHeightDp()) {
                    item.clearIndicatorWidthDp();
                    item.clearIndicatorHeightDp();
                    toast("clearIndicatorWidthDp() / clearIndicatorHeightDp() → "
                            + item.getIndicatorWidthDp() + " / " + item.getIndicatorHeightDp()
                            + "dp（默认 56dp × 32dp）");
                } else {
                    item.setIndicatorWidthDp(40.0f);
                    item.setIndicatorHeightDp(24.0f);
                    toast("setIndicatorWidthDp(40) / setIndicatorHeightDp(24) → "
                            + item.getIndicatorWidthDp() + " / " + item.getIndicatorHeightDp()
                            + "dp、hasIndicatorWidthDp() = " + item.hasIndicatorWidthDp()
                            + "、hasIndicatorHeightDp() = " + item.hasIndicatorHeightDp());
                }
            }
        });
        row.addView(indicatorSize, controlParams());

        return row;
    }

    private LinearLayout createItemListControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton add = controlButton("addItem(new Tab)");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItem data = new MaterialNavigationRailItem();
                data.setIcon(new Icon(android.R.drawable.ic_menu_add));
                data.setText("Tab " + (verticalRail.getItemCount() + 1));
                verticalRail.addItem(data);
                toast("addItem(...) → getItemCount() = " + verticalRail.getItemCount()
                        + "，getItem(last).getIcon() = "
                        + (verticalRail.getItem(verticalRail.getItemCount() - 1).getIcon() != null
                        ? "Icon 已设置" : "null"));
            }
        });
        row.addView(add, controlParams());

        MaterialButton remove = controlButton("removeItem(last)");
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = verticalRail.getItemCount();
                if (count == 0) {
                    toast("已经没有可移除的 item");
                    return;
                }
                verticalRail.removeItem(count - 1);
                toast("removeItem(" + (count - 1) + ") → getItemCount() = "
                        + verticalRail.getItemCount());
            }
        });
        row.addView(remove, controlParams());

        MaterialButton query = controlButton("getItem(0)");
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialNavigationRailItemView item = verticalRail.getItem(0);
                toast(item == null || item.getItemData() == null
                        ? "getItem(0) == null"
                        : "getItem(0)：文字 " + item.getItemData().getText()
                        + "、图标 " + (item.getItemData().getIcon() != null ? "Icon" : "null")
                        + "、selected = " + (verticalRail.getSelectedItem() == item));
            }
        });
        row.addView(query, controlParams());

        return row;
    }

    private LinearLayout createListenerControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton setListener = controlButton("setOnItemSelectedListener");
        setListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.setOnItemSelectedListener(
                        new MaterialNavigationRail.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(int position) {
                                toast("onItemSelected(" + position + ")");
                            }

                            @Override
                            public void onItemReselected(int position) {
                                toast("onItemReselected(" + position + ")");
                            }
                        });
                toast("已设置监听器，getOnItemSelectedListener() "
                        + (horizontalRail.getOnItemSelectedListener() != null ? "!= null" : "== null"));
            }
        });
        row.addView(setListener, controlParams());

        MaterialButton getListener = controlButton("getOnItemSelectedListener()");
        getListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("getOnItemSelectedListener() = "
                        + (horizontalRail.getOnItemSelectedListener() != null
                        ? "已设置（点导航项会弹出回调）" : "null（未设置）"));
            }
        });
        row.addView(getListener, controlParams());

        MaterialButton clearListener = controlButton("setOnItemSelectedListener(null)");
        clearListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                horizontalRail.setOnItemSelectedListener(null);
                toast("setOnItemSelectedListener(null) → getOnItemSelectedListener() = "
                        + horizontalRail.getOnItemSelectedListener());
            }
        });
        row.addView(clearListener, controlParams());

        return row;
    }

    private RadioGroup createModeGroup(final MaterialNavigationRail rail) {
        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.HORIZONTAL);
        NavigationRailLabelVisibilityMode[] modes = NavigationRailLabelVisibilityMode.values();
        for (int i = 0; i < modes.length; i++) {
            final NavigationRailLabelVisibilityMode mode = modes[i];
            MaterialRadioButton button = new MaterialRadioButton(this);
            button.setId(i);
            button.setText(mode.name());
            group.addView(button, new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            if (i == 0) {
                button.setChecked(true);
            }
            button.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        rail.setLabelVisibilityMode(mode);
                        toast("setLabelVisibilityMode(" + mode + ") → getLabelVisibilityMode() = "
                                + rail.getLabelVisibilityMode());
                    }
                }
            });
        }
        return group;
    }

    private MaterialNavigationRail createDataRail(NavigationRailOrientation orientation) {
        MaterialNavigationRail rail = new MaterialNavigationRail(this);
        rail.setOrientation(orientation);
        for (int i = 0; i < ITEM_LABELS.length; i++) {
            MaterialNavigationRailItem item = new MaterialNavigationRailItem();
            item.setIcon(new Icon(ICON_RES_IDS[i % ICON_RES_IDS.length]));
            item.setText(ITEM_LABELS[i % ITEM_LABELS.length]);
            rail.addItem(item);
        }
        return rail;
    }

    private MaterialNavigationRail createItemViewRail() {
        MaterialNavigationRail rail = new MaterialNavigationRail(this);
        rail.setOrientation(NavigationRailOrientation.HORIZONTAL);
        for (int i = 0; i < ITEM_LABELS.length; i++) {
            MaterialNavigationRailItemView item = new MaterialNavigationRailItemView(this);
            item.setIcon(new Icon(ICON_RES_IDS[i % ICON_RES_IDS.length]));
            item.setText(ITEM_LABELS[i % ITEM_LABELS.length]);
            if (i == 0) {
                item.setContainerColor(colorPrimaryContainer);
                item.setIndicatorColor(colorTertiaryContainer);
                item.setIconColor(colorOnPrimaryContainer);
                item.setTextColor(colorOnPrimaryContainer);
            } else if (i == 1) {
                item.setLabelTextSizeSp(16.0f);
                item.setIconSizeDp(32.0f);
            } else if (i == 2) {
                item.setIndicatorWidthDp(40.0f);
                item.setIndicatorHeightDp(24.0f);
            }
            rail.addItem(item);
        }
        return rail;
    }

    private ViewPager createViewPager() {
        ViewPager pager = new ViewPager(this);
        pager.setAdapter(new DemoPagerAdapter());
        return pager;
    }

    private static class DemoPagerAdapter extends PagerAdapter {

        private static final String[] TITLES = {"Home", "Gallery", "Manage", "Compass"};

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView page = new TextView(container.getContext());
            page.setText(TITLES[position]);
            page.setGravity(Gravity.CENTER);
            page.setTextColor(SchemeHelper.onBackgroundColor());
            page.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            container.addView(page, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private MaterialButton controlButton(String text) {
        MaterialButton button = new MaterialButton(this);
        button.setStyle(ButtonStyle.TEXT);
        button.setText(text);
        button.setTextSize(13.0f);
        return button;
    }

    private View controlRow(View child) {
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        int padding = dp(16.0f);
        row.setPadding(padding, 0, padding, dp(8.0f));
        row.addView(child, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.addView(row, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scrollView;
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        caption.setTextColor(SchemeHelper.onBackgroundColor());
        int horizontal = dp(16.0f);
        caption.setPadding(horizontal, dp(16.0f), horizontal, dp(8.0f));
        caption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return caption;
    }

    private View spacer(float heightDp) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(heightDp)));
        return spacer;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams pagerParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(200.0f));
        params.leftMargin = dp(16.0f);
        params.rightMargin = dp(16.0f);
        return params;
    }

    private LinearLayout.LayoutParams controlParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = dp(8.0f);
        return params;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static String hex(int color) {
        return String.format("#%08X", color);
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
