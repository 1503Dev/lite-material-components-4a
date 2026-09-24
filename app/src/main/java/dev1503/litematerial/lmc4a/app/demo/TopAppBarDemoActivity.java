package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.topappbar.MaterialTopAppBar;
import dev1503.lmc4a.v3.widget.topappbar.TopAppBarVariant;

public class TopAppBarDemoActivity extends Activity {

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private MaterialTopAppBar interactiveBar;
    private ScrollView scrollView;
    private boolean customIconColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        interactiveBar = createInteractiveBar();
        root.addView(interactiveBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("顶部固定条已 bindTo(ScrollView)：向下滚动内容后容器色 "
                + "surface → surfaceContainer、elevation 0 → 3dp（Material 3 on-scroll 行为），"
                + "回到顶部自动还原；unbind() 解除绑定，isBound() 查询状态。"));

        content.addView(caption("小号 Small：容器 64dp，标题 title-large 22sp / onSurface；"
                + "左右图标为 onSurfaceVariant，48dp 触控区（图标 24dp）且为全圆角，"
                + "距容器边缘 4dp，标题距槽位 16dp。"));
        content.addView(createSmallBar(), barParams());

        content.addView(caption("小号 + 超长标题：标题始终单行，末尾省略，不会把操作图标挤出屏幕。"));
        content.addView(createLongTitleBar(), barParams());

        content.addView(caption("居中对齐 Center-aligned：容器 64dp，标题在左右槽位之间居中（title-large 22sp）。"));
        content.addView(createCenterAlignedBar(), barParams());

        content.addView(caption("中号 Medium：容器 112dp = 64dp 图标行 + 48dp 标题行，"
                + "标题 headline-small 24sp，位于第二行底部，距底部 16dp。"));
        content.addView(createMediumBar(), barParams());

        content.addView(caption("大号 Large：容器 152dp = 64dp 图标行 + 88dp 标题行，标题 headline-medium 28sp。"));
        content.addView(createLargeBar(), barParams());

        content.addView(caption("副标题：本库对 Material 3 的扩展（规范里顶栏没有副标题），"
                + "在标题下方追加一行 onSurfaceVariant 14sp，可用 setSubtitleTextSizeSp / "
                + "clearSubtitleTextSizeSp 调整。"));
        content.addView(createSubtitleBar(), barParams());

        content.addView(caption("自定义颜色与字型：setContainerColor / setTitleTextColor / "
                + "setSubtitleTextColor / setIconColor / setTitleTextSizeSp；"
                + "对应的 clearXxxColor / clearTitleTextSizeSp 可恢复配色方案角色色。"));
        content.addView(createCustomColorBar(), barParams());

        content.addView(caption("自定义 on-scroll 颜色：setScrolledUnderContainerColor + "
                + "setScrolledUnderElevationDp（本条目同样绑定了下方 ScrollView，滚动时即可看到变化）。"));
        content.addView(createScrolledUnderColorBar(), barParams());

        content.addView(caption("自定义图标按钮：setIconButtonSizeDp / setIconButtonCornerRadiusDp"
                + "（默认 48dp 全圆角，clearIconButtonCornerRadiusDp 恢复圆形）；"
                + "本例放大到 56dp / 16dp 圆角。"));
        content.addView(createCustomIconButtonBar(), barParams());

        content.addView(caption("自定义槽位内容：addStartView / addEndView 可放入任意 View"
                + "（非 MaterialButton 时保持 wrap_content，左右槽位各留 4dp 边缘内边距）。"));
        content.addView(createCustomSlotBar(), barParams());

        content.addView(caption("运行时切换变体（作用于顶部固定条）"));
        content.addView(controlRow(createVariantControls()));

        content.addView(caption("运行时增删槽位（作用于顶部固定条）：addStartView / addEndView 返回插入索引，"
                + "removeStartView / removeEndView 返回被移除的索引，越界或不存在时返回 -1。"));
        content.addView(controlRow(createSlotControls()));

        content.addView(caption("其它 API：setColorScheme(DynamicScheme) 应用配色方案并清除全部颜色覆盖；"
                + "setScrolledUnder(boolean) / isScrolledUnder() 手动控制 on-scroll 状态；"
                + "setElevationDp(float) 基础 elevation；getStartView(int) / getStartViewCount() 遍历槽位。"));
        content.addView(spacer(32.0f));

        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        setContentView(root);

        interactiveBar.bindTo(scrollView);
    }

    private MaterialTopAppBar createInteractiveBar() {
        MaterialTopAppBar appBar = new MaterialTopAppBar(this);
        appBar.setVariant(TopAppBarVariant.SMALL);
        appBar.setTitle("交互示例（固定顶部）");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_sort_by_size, "打开导航"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_search, "搜索"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createSmallBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.SMALL, "小号 Small");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_sort_by_size, "打开导航"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_search, "搜索"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createLongTitleBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.SMALL,
                "这是一个非常长的标题，用于演示单行省略号，并且不会挤压两侧的图标按钮");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_revert, "返回"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_share, "分享"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createCenterAlignedBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.CENTER_ALIGNED, "居中对齐");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_revert, "返回"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_edit, "编辑"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createMediumBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.MEDIUM, "中号 Medium");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_agenda, "打开导航"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_save, "保存"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createLargeBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.LARGE, "大号 Large");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_gallery, "打开导航"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_camera, "拍照"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createSubtitleBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.SMALL, "标题 + 副标题");
        appBar.setSubtitle("副标题使用 onSurfaceVariant 14sp");
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_revert, "返回"));
        return appBar;
    }

    private MaterialTopAppBar createCustomColorBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.SMALL, "自定义颜色与字型");
        int primaryContainer = dynamicColors.primaryContainer()
                .getArgb(MaterialTopAppBar.publicColorScheme);
        int onPrimaryContainer = dynamicColors.onPrimaryContainer()
                .getArgb(MaterialTopAppBar.publicColorScheme);
        appBar.setContainerColor(primaryContainer);
        appBar.setTitleTextColor(onPrimaryContainer);
        appBar.setIconColor(onPrimaryContainer);
        appBar.setTitleTextSizeSp(20.0f);
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_edit, "编辑"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_sort_by_size, "排序"));
        return appBar;
    }

    private MaterialTopAppBar createScrolledUnderColorBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.MEDIUM, "自定义 on-scroll 颜色");
        appBar.setSubtitle("滚动后容器变为 tertiaryContainer，elevation 6dp");
        appBar.setScrolledUnderContainerColor(dynamicColors.tertiaryContainer()
                .getArgb(MaterialTopAppBar.publicColorScheme));
        appBar.setScrolledUnderElevationDp(6.0f);
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_compass, "定位"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        appBar.bindTo(scrollView);
        return appBar;
    }

    private MaterialTopAppBar createCustomIconButtonBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.SMALL, "自定义图标按钮尺寸与圆角");
        appBar.setIconButtonSizeDp(56.0f);
        appBar.setIconButtonCornerRadiusDp(16.0f);
        appBar.addStartView(iconButton(android.R.drawable.ic_menu_revert, "返回"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_search, "搜索"));
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private MaterialTopAppBar createCustomSlotBar() {
        MaterialTopAppBar appBar = baseBar(TopAppBarVariant.SMALL, "任意 View 槽位");
        TextView badge = new TextView(this);
        badge.setText("自定义");
        badge.setTextSize(14.0f);
        badge.setTextColor(SchemeHelper.onBackgroundColor());
        appBar.addStartView(badge);
        appBar.addEndView(iconButton(android.R.drawable.ic_menu_more, "更多"));
        return appBar;
    }

    private LinearLayout createVariantControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        final TopAppBarVariant[] variants = {
                TopAppBarVariant.SMALL,
                TopAppBarVariant.CENTER_ALIGNED,
                TopAppBarVariant.MEDIUM,
                TopAppBarVariant.LARGE,
        };
        final String[] labels = {"SMALL", "CENTER_ALIGNED", "MEDIUM", "LARGE"};
        for (int i = 0; i < variants.length; i++) {
            final TopAppBarVariant variant = variants[i];
            MaterialButton button = controlButton(labels[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interactiveBar.setVariant(variant);
                    toast("setVariant(" + variant + ")：容器 "
                            + (int) variant.getContainerHeightDp() + "dp，标题 "
                            + (int) variant.getTitleTextSizeSp() + "sp");
                }
            });
            row.addView(button, controlParams());
        }
        return row;
    }

    private LinearLayout createSlotControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton addStart = controlButton("addStartView");
        addStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = interactiveBar.addStartView(
                        iconButton(android.R.drawable.ic_menu_add, "新增起始图标"));
                toast("addStartView -> 索引 " + index
                        + "，起始槽位共 " + interactiveBar.getStartViewCount() + " 个");
            }
        });
        row.addView(addStart, controlParams());

        MaterialButton addEnd = controlButton("addEndView");
        addEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = interactiveBar.addEndView(
                        iconButton(android.R.drawable.ic_menu_view, "新增结束图标"));
                toast("addEndView -> 索引 " + index
                        + "，结束槽位共 " + interactiveBar.getEndViewCount() + " 个");
            }
        });
        row.addView(addEnd, controlParams());

        MaterialButton removeStart = controlButton("removeStartView(0)");
        removeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("removeStartView(0) -> 索引 "
                        + interactiveBar.removeStartView(0));
            }
        });
        row.addView(removeStart, controlParams());

        MaterialButton removeEnd = controlButton("removeEndView(last)");
        removeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("removeEndView(最后一个) -> 索引 "
                        + interactiveBar.removeEndView(
                                interactiveBar.getEndViewCount() - 1));
            }
        });
        row.addView(removeEnd, controlParams());

        MaterialButton toggleIconColor = controlButton("toggle iconColor");
        toggleIconColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customIconColor = !customIconColor;
                if (customIconColor) {
                    interactiveBar.setIconColor(dynamicColors.primary()
                            .getArgb(MaterialTopAppBar.publicColorScheme));
                } else {
                    interactiveBar.clearIconColor();
                }
                toast(customIconColor ? "setIconColor(primary)" : "clearIconColor()");
            }
        });
        row.addView(toggleIconColor, controlParams());

        MaterialButton toggleScrolled = controlButton("toggle scrolledUnder");
        toggleScrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean scrolledUnder = !interactiveBar.isScrolledUnder();
                interactiveBar.setScrolledUnder(scrolledUnder);
                toast("setScrolledUnder(" + scrolledUnder + ")（滚动页面会按实际滚动位置覆盖）");
            }
        });
        row.addView(toggleScrolled, controlParams());

        MaterialButton toggleSubtitle = controlButton("toggle subtitle");
        toggleSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasSubtitle = interactiveBar.getSubtitle() != null
                        && interactiveBar.getSubtitle().length() > 0;
                interactiveBar.setSubtitle(hasSubtitle ? null : "副标题（onSurfaceVariant 14sp）");
                toast("setSubtitle(" + (hasSubtitle ? "null" : "文本") + ")");
            }
        });
        row.addView(toggleSubtitle, controlParams());

        MaterialButton toggleRound = controlButton("toggle 圆角");
        toggleRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean round = interactiveBar.getIconButtonCornerRadiusDp()
                        >= interactiveBar.getIconButtonSizeDp() / 2.0f;
                if (round) {
                    interactiveBar.setIconButtonCornerRadiusDp(8.0f);
                } else {
                    interactiveBar.clearIconButtonCornerRadiusDp();
                }
                toast("图标按钮圆角 = " + interactiveBar.getIconButtonCornerRadiusDp() + "dp");
            }
        });
        row.addView(toggleRound, controlParams());

        return row;
    }

    private MaterialTopAppBar baseBar(TopAppBarVariant variant, CharSequence title) {
        MaterialTopAppBar appBar = new MaterialTopAppBar(this);
        appBar.setVariant(variant);
        appBar.setTitle(title);
        return appBar;
    }

    private MaterialButton iconButton(int iconRes, CharSequence contentDescription) {
        MaterialButton button = new MaterialButton(this);
        button.setStyle(ButtonStyle.ICON);
        button.setIcon(getResources().getDrawable(iconRes));
        button.setContentDescription(contentDescription);
        return button;
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

    private View spacer(float heightDp) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(heightDp)));
        return spacer;
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

    private LinearLayout.LayoutParams barParams() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
