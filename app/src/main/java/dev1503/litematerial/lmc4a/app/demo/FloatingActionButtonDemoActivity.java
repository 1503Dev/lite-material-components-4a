package dev1503.litematerial.lmc4a.app.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.widget.ColorVariant;
import dev1503.lmc4a.v3.widget.SizeVariant;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.floatingactionbutton.MaterialFloatingActionButton;

public class FloatingActionButtonDemoActivity extends DemoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        int padding = dp(24.0f);
        content.setPadding(padding, padding, padding, padding);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        showHideSection(content);
        bindSection(content, scrollView);
        sizeSection(content);
        iconSizeSection(content);
        elevationSection(content);
        colorSection(content);
        colorVariantSection(content);

        setContentView(scrollView);
    }

    private void showHideSection(LinearLayout content) {
        content.addView(caption("show() / hide()：弹簧缩放动画（stiffness 300、damping 0.7，约 400ms），"
                + "隐藏时缩放并淡出到 INVISIBLE。"), captionParams());

        final MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, false);
        MaterialButton hideButton = actionButton("Hide", v -> fab.hide());
        MaterialButton showButton = actionButton("Show", v -> fab.show());
        MaterialButton sizeButton = actionButton("getSize()", v -> toast("getSize()=" + fab.getSize()
                + "（默认 SizeVariant.SMALL）"));
        content.addView(row(fab, hideButton, showButton, sizeButton), rowParams());
    }

    private void bindSection(LinearLayout content, final ScrollView scrollView) {
        content.addView(caption("bindTo(View) / unbind() / isBound()：绑定任意可滚动容器后，向下滚动自动 hide()，"
                + "滚回顶部或向上滚动自动 show()；再次 bindTo 会先自动解除上一个绑定，"
                + "unbind() 无参数、解绑后 isBound() 返回 false。下面这枚已绑定本页 ScrollView，"
                + "向下滚动内容即可看到它隐藏。"), captionParams());

        final MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, false);
        fab.bindTo(scrollView);

        final TextView status = new TextView(this);
        status.setTextSize(12.0f);
        status.setTextColor(SchemeHelper.onBackgroundColor());
        status.setText("isBound() = " + fab.isBound());

        final MaterialButton toggleButton = actionButton("unbind()", null);
        toggleButton.setOnClickListener(v -> {
            if (fab.isBound()) {
                fab.unbind();
                toggleButton.setText("bindTo(scrollView)");
            } else {
                fab.bindTo(scrollView);
                toggleButton.setText("unbind()");
            }
            status.setText("isBound() = " + fab.isBound());
            toast("调用后 isBound()=" + fab.isBound());
        });
        content.addView(row(fab, toggleButton), rowParams());
        content.addView(status, captionParams());
    }

    private void sizeSection(LinearLayout content) {
        for (SizeVariant variant : SizeVariant.values()) {
            content.addView(caption(variant.name() + " — icon only（容器高度 " + heightDp(variant)
                    + "dp、圆角 " + cornerDp(variant) + "dp、图标默认 " + iconDp(variant)
                    + "dp、默认 elevation 6dp）"), captionParams());
            content.addView(createFab(variant, false), fabParams());
            content.addView(caption(variant.name() + " — icon + label（标签字号随档位变化）"), captionParams());
            content.addView(createFab(variant, true), fabParams());
        }
    }

    private void iconSizeSection(LinearLayout content) {
        content.addView(caption("图标尺寸 setIconSizeDp / getIconSizeDp / hasIconSizeDp / clearIconSizeDp："
                + "默认随 SizeVariant（SMALL 24dp / MEDIUM 28dp / LARGE 32dp）。"
                + "下面这枚 LARGE 覆盖为 40dp，clearIconSizeDp() 后回 32dp。"), captionParams());

        final MaterialFloatingActionButton fab = createFab(SizeVariant.LARGE, false);
        fab.setIconSizeDp(40.0f);
        content.addView(row(
                fab,
                actionButton("setIconSizeDp(40)", v -> {
                    fab.setIconSizeDp(40.0f);
                    toast("LARGE setIconSizeDp(40) → hasIconSizeDp()=" + fab.hasIconSizeDp()
                            + "，getIconSizeDp()=" + fab.getIconSizeDp() + "dp（默认 32dp）");
                }),
                actionButton("clearIconSizeDp()", v -> {
                    fab.clearIconSizeDp();
                    toast("clearIconSizeDp() → getIconSizeDp()=" + fab.getIconSizeDp()
                            + "dp（回 LARGE 默认 32dp）");
                })), rowParams());
    }

    private void elevationSection(LinearLayout content) {
        content.addView(caption("阴影 setElevationDp / getElevationDp / hasElevationDp / clearElevationDp："
                + "FAB 默认恒为 6dp（与 ButtonStyle 无关）；覆盖为 16dp 后阴影明显变大，"
                + "clearElevationDp() 后回到 6dp。"), captionParams());

        final MaterialFloatingActionButton fab = createFab(SizeVariant.MEDIUM, false);
        fab.setElevationDp(16.0f);
        content.addView(row(
                fab,
                actionButton("setElevationDp(16)", v -> {
                    fab.setElevationDp(16.0f);
                    toast("setElevationDp(16) → hasElevationDp()=" + fab.hasElevationDp()
                            + "，getElevationDp()=" + fab.getElevationDp() + "dp");
                }),
                actionButton("clearElevationDp()", v -> {
                    fab.clearElevationDp();
                    toast("clearElevationDp() → hasElevationDp()=" + fab.hasElevationDp()
                            + "，getElevationDp()=" + fab.getElevationDp() + "dp（回默认 6dp）");
                })), rowParams());
    }

    private void colorSection(LinearLayout content) {
        content.addView(caption("容器色覆盖（继承 MaterialButton）：默认容器色为角色色 primaryContainer，"
                + "图标与标签固定使用 onPrimaryContainer（不受 setIconColor 影响）。"
                + "这里覆盖容器为 tertiaryContainer，clearContainerColor() 恢复角色色，"
                + "clearColorOverrides() 一次清除全部颜色覆盖。"), captionParams());

        final MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, true);
        fab.setContainerColor(Imc.publicColorScheme.getTertiaryContainer());
        content.addView(row(
                fab,
                actionButton("clearContainerColor()", v -> {
                    fab.clearContainerColor();
                    toast("clearContainerColor() → hasContainerColor()=" + fab.hasContainerColor()
                            + "，容器色回到 primaryContainer " + hex(fab.getContainerColor()));
                }),
                actionButton("clearColorOverrides()", v -> {
                    fab.clearColorOverrides();
                    toast("clearColorOverrides() → hasContainerColor()=" + fab.hasContainerColor()
                            + "，容器色回到 primaryContainer " + hex(fab.getContainerColor()));
                }),
                actionButton("重新 setContainerColor", v -> {
                    fab.setContainerColor(Imc.publicColorScheme.getTertiaryContainer());
                    toast("setContainerColor(tertiaryContainer) → " + hex(fab.getContainerColor()));
                })), rowParams());
    }

    private void colorVariantSection(LinearLayout content) {
        content.addView(caption("ColorVariant：setColorVariant / getColorVariant，只影响 FILLED 容器色"
                + "（默认 PRIMARY_CONTAINER，FAB 构造时即设为此值）；FAB 的图标与标签固定用 onPrimaryContainer，"
                + "不随变体变化。下面每枚 SMALL icon only 一枚变体，点击可 Toast 回读 getColorVariant()。"), captionParams());
        for (ColorVariant colorVariant : ColorVariant.values()) {
            final MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, false);
            fab.setColorVariant(colorVariant);
            fab.setOnClickListener(v -> toast("getColorVariant()=" + fab.getColorVariant()));
            content.addView(fab, fabParams());
            content.addView(caption(colorVariant.name() + "（getColorVariant()=" + fab.getColorVariant() + "）"),
                    fabParams());
        }
    }

    private MaterialFloatingActionButton createFab(SizeVariant variant, boolean withLabel) {
        MaterialFloatingActionButton fab = new MaterialFloatingActionButton(this);
        fab.setSize(variant);
        fab.setIcon(new Icon(android.R.drawable.ic_menu_add));
        if (withLabel) {
            fab.setText("Add");
        }
        return fab;
    }

    private MaterialButton actionButton(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setStyle(ButtonStyle.TEXT);
        button.setText(text);
        button.setTextSize(13.0f);
        button.setOnClickListener(listener);
        return button;
    }

    private View row(View... children) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        for (View child : children) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = dp(8.0f);
            row.addView(child, params);
        }
        return row;
    }

    private String heightDp(SizeVariant variant) {
        switch (variant) {
            case MEDIUM:
                return "84";
            case LARGE:
                return "92";
            default:
                return "56";
        }
    }

    private String cornerDp(SizeVariant variant) {
        switch (variant) {
            case MEDIUM:
                return "16";
            case LARGE:
                return "24";
            default:
                return "12";
        }
    }

    private String iconDp(SizeVariant variant) {
        switch (variant) {
            case MEDIUM:
                return "28";
            case LARGE:
                return "32";
            default:
                return "24";
        }
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        caption.setTextColor(SchemeHelper.onBackgroundColor());
        return caption;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams rowParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(24.0f);
        return params;
    }

    private LinearLayout.LayoutParams fabParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String hex(int color) {
        return "#" + Integer.toHexString(color);
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
