package dev1503.litematerial.lmc4a.app.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
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
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class ButtonDemoActivity extends DemoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(16.0f);
        content.setPadding(padding, padding, padding, padding);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        styleSection(content);
        cornerSection(content);
        iconSection(content);
        containerColorSection(content);
        contentColorSection(content);
        iconColorSection(content);
        iconSizeSection(content);
        strokeSection(content);
        elevationSection(content);
        rippleSection(content);
        clearOverridesSection(content);

        setContentView(scrollView);
    }

    private void styleSection(LinearLayout content) {
        content.addView(caption("按钮样式：setStyle(ButtonStyle) / getStyle()，默认 FILLED。"
                + "视觉高度 40dp（上下各留 4dp 内缩给阴影）、最小宽度 90dp、默认圆角 20dp。"));
        final MaterialButton filledButton = createButton("Filled（默认）");
        content.addView(filledButton, itemParams());

        final MaterialButton elevatedButton = createButton("Elevated");
        elevatedButton.setStyle(ButtonStyle.ELEVATED);
        content.addView(elevatedButton, itemParams());
        content.addView(caption("setStyle(ELEVATED)：容器色 surfaceContainerLow、文字 primary，默认 elevation 1dp。"),
                captionParams());

        final MaterialButton outlinedButton = createButton("Outlined");
        outlinedButton.setStyle(ButtonStyle.OUTLINED);
        content.addView(outlinedButton, itemParams());
        content.addView(caption("setStyle(OUTLINED)：容器透明 + 1dp outline 描边，文字 primary。"), captionParams());

        final MaterialButton textButton = createButton("Text");
        textButton.setStyle(ButtonStyle.TEXT);
        content.addView(textButton, itemParams());
        content.addView(caption("setStyle(TEXT)：无容器（透明），文字 primary。"), captionParams());

        final MaterialButton iconStyleButton = createButton("");
        iconStyleButton.setStyle(ButtonStyle.ICON);
        iconStyleButton.setIcon(new Icon(android.R.drawable.ic_menu_add));
        content.addView(iconStyleButton, itemParams());
        content.addView(caption("setStyle(ICON)：40dp 方形 + 透明容器（同 TEXT）。"), captionParams());

        MaterialButton disabledButton = createButton("Filled disabled");
        disabledButton.setEnabled(false);
        content.addView(disabledButton, itemParams());
        content.addView(caption("禁用态：容器色按 12%、文字按 38% 不透明度叠加；覆盖色同样会被叠加。"),
                captionParams());

        content.addView(row(actionButton("Toast 各按钮 getStyle()", v -> toast(
                "Filled=" + filledButton.getStyle()
                        + "；Elevated=" + elevatedButton.getStyle()
                        + "；Outlined=" + outlinedButton.getStyle()
                        + "；Text=" + textButton.getStyle()
                        + "；Icon=" + iconStyleButton.getStyle()))), rowParams());
    }

    private void cornerSection(LinearLayout content) {
        content.addView(caption("圆角：setCornerRadiusDp(float) / getCornerRadiusDp()，默认 20dp（全圆角）。"),
                captionParams());

        MaterialButton corner8dpButton = createButton("Corner 8dp");
        corner8dpButton.setCornerRadiusDp(8.0f);
        content.addView(corner8dpButton, itemParams());

        MaterialButton corner2dpButton = createButton("Corner 2dp");
        corner2dpButton.setCornerRadiusDp(2.0f);
        content.addView(corner2dpButton, itemParams());

        MaterialButton squareCornerButton = createButton("Corner 0dp");
        squareCornerButton.setCornerRadiusDp(0.0f);
        content.addView(squareCornerButton, itemParams());

        MaterialButton pillButton = createButton("Corner 20dp full round");
        pillButton.setCornerRadiusDp(20.0f);
        content.addView(pillButton, itemParams());
    }

    private void iconSection(LinearLayout content) {
        content.addView(caption("图标：setIcon(Icon) / getIcon()；仅图标按钮默认 24dp、"
                + "图标 + 文字默认 18dp（可用 setIconSizeDp 覆盖）。"), captionParams());

        MaterialButton iconFilled = createButton("");
        iconFilled.setIcon(new Icon(android.R.drawable.ic_menu_add));
        content.addView(iconFilled, itemParams());

        MaterialButton iconElevated = createButton("");
        iconElevated.setStyle(ButtonStyle.ELEVATED);
        iconElevated.setIcon(new Icon(android.R.drawable.ic_menu_edit));
        content.addView(iconElevated, itemParams());

        MaterialButton iconOutlined = createButton("");
        iconOutlined.setStyle(ButtonStyle.OUTLINED);
        iconOutlined.setIcon(new Icon(android.R.drawable.ic_menu_close_clear_cancel));
        content.addView(iconOutlined, itemParams());

        MaterialButton iconTextFilled = createButton("Save");
        iconTextFilled.setIcon(new Icon(android.R.drawable.ic_menu_save));
        content.addView(iconTextFilled, itemParams());

        MaterialButton iconTextElevated = createButton("Edit");
        iconTextElevated.setStyle(ButtonStyle.ELEVATED);
        iconTextElevated.setIcon(new Icon(android.R.drawable.ic_menu_edit));
        content.addView(iconTextElevated, itemParams());

        MaterialButton iconTextOutlined = createButton("Delete");
        iconTextOutlined.setStyle(ButtonStyle.OUTLINED);
        iconTextOutlined.setIcon(new Icon(android.R.drawable.ic_menu_delete));
        content.addView(iconTextOutlined, itemParams());
    }

    private void containerColorSection(LinearLayout content) {
        content.addView(caption("容器色 setContainerColor / getContainerColor / clearContainerColor（hasContainerColor() 查询是否已覆盖）："
                + "Filled 默认容器色是角色色 primary；覆盖值为 tertiary 时容器变紫红，"
                + "clearContainerColor() 后回到 primary。"), captionParams());

        final MaterialButton containerButton = createButton("Container color");
        content.addView(containerButton, itemParams());
        content.addView(row(
                actionButton("setContainerColor(tertiary)", v -> {
                    containerButton.setContainerColor(Imc.publicColorScheme.getTertiary());
                    toast("setContainerColor(tertiary) → hasContainerColor()=" + containerButton.hasContainerColor()
                            + "，getContainerColor()=" + hex(containerButton.getContainerColor()));
                }),
                actionButton("clearContainerColor()", v -> {
                    containerButton.clearContainerColor();
                    toast("clearContainerColor() → hasContainerColor()=" + containerButton.hasContainerColor()
                            + "，容器色回到角色色 primary " + hex(Imc.publicColorScheme.getPrimary()));
                })), rowParams());
    }

    private void contentColorSection(LinearLayout content) {
        content.addView(caption("内容（文字）色 setContentColor / getContentColor / clearContentColor："
                + "默认文字色由角色决定（Filled 为 onPrimary）；覆盖为 tertiary 后文字变紫红，"
                + "clearContentColor() 回 onPrimary。内容色覆盖不会被 setStyle() 清掉。"), captionParams());

        final MaterialButton contentColorButton = createButton("Content color");
        content.addView(contentColorButton, itemParams());
        content.addView(row(
                actionButton("setContentColor(tertiary)", v -> {
                    contentColorButton.setContentColor(Imc.publicColorScheme.getTertiary());
                    toast("setContentColor(tertiary) → hasContentColor()=" + contentColorButton.hasContentColor()
                            + "，getContentColor()=" + hex(contentColorButton.getContentColor()));
                }),
                actionButton("clearContentColor()", v -> {
                    contentColorButton.clearContentColor();
                    toast("clearContentColor() → hasContentColor()=" + contentColorButton.hasContentColor()
                            + "，文字色回到角色色 onPrimary " + hex(Imc.publicColorScheme.getOnPrimary()));
                })), rowParams());
    }

    private void iconColorSection(LinearLayout content) {
        content.addView(caption("图标色 setIconColor / getIconColor / clearIconColor（hasIconColor() 查询）："
                + "图标默认与文字同色；覆盖为 tertiary 后图标变紫红而文字不变，"
                + "clearIconColor() 后图标重新跟随文字色。"), captionParams());

        final MaterialButton iconColorButton = createButton("Save");
        iconColorButton.setIcon(new Icon(android.R.drawable.ic_menu_save));
        content.addView(iconColorButton, itemParams());
        content.addView(row(
                actionButton("setIconColor(tertiary)", v -> {
                    iconColorButton.setIconColor(Imc.publicColorScheme.getTertiary());
                    toast("setIconColor(tertiary) → hasIconColor()=" + iconColorButton.hasIconColor()
                            + "，getIconColor()=" + hex(iconColorButton.getIconColor()));
                }),
                actionButton("clearIconColor()", v -> {
                    iconColorButton.clearIconColor();
                    toast("clearIconColor() → hasIconColor()=" + iconColorButton.hasIconColor()
                            + "，图标色回到文字色 " + hex(iconColorButton.getIconColor()));
                })), rowParams());
    }

    private void iconSizeSection(LinearLayout content) {
        content.addView(caption("图标尺寸 setIconSizeDp(float) / getIconSizeDp() / hasIconSizeDp() / clearIconSizeDp()："
                + "仅图标按钮默认 24dp。下面这枚覆盖为 36dp，clear 后回 24dp。"), captionParams());

        final MaterialButton iconOnlySizeButton = createButton("");
        iconOnlySizeButton.setIcon(new Icon(android.R.drawable.ic_menu_add));
        iconOnlySizeButton.setIconSizeDp(36.0f);
        content.addView(iconOnlySizeButton, itemParams());
        content.addView(row(
                actionButton("setIconSizeDp(36)", v -> {
                    iconOnlySizeButton.setIconSizeDp(36.0f);
                    toast("仅图标 setIconSizeDp(36) → hasIconSizeDp()=" + iconOnlySizeButton.hasIconSizeDp()
                            + "，getIconSizeDp()=" + iconOnlySizeButton.getIconSizeDp() + "dp（默认 24dp）");
                }),
                actionButton("clearIconSizeDp()", v -> {
                    iconOnlySizeButton.clearIconSizeDp();
                    toast("clearIconSizeDp() → hasIconSizeDp()=" + iconOnlySizeButton.hasIconSizeDp()
                            + "，getIconSizeDp()=" + iconOnlySizeButton.getIconSizeDp() + "dp（回默认 24dp）");
                })), rowParams());

        content.addView(caption("图标 + 文字按钮的默认图标尺寸是 18dp。下面这枚覆盖为 28dp，clear 后回 18dp。"),
                captionParams());

        final MaterialButton iconTextSizeButton = createButton("Save");
        iconTextSizeButton.setIcon(new Icon(android.R.drawable.ic_menu_save));
        iconTextSizeButton.setIconSizeDp(28.0f);
        content.addView(iconTextSizeButton, itemParams());
        content.addView(row(
                actionButton("setIconSizeDp(28)", v -> {
                    iconTextSizeButton.setIconSizeDp(28.0f);
                    toast("图标 + 文字 setIconSizeDp(28) → getIconSizeDp()=" + iconTextSizeButton.getIconSizeDp()
                            + "dp（默认 18dp）");
                }),
                actionButton("clearIconSizeDp()", v -> {
                    iconTextSizeButton.clearIconSizeDp();
                    toast("clearIconSizeDp() → getIconSizeDp()=" + iconTextSizeButton.getIconSizeDp()
                            + "dp（回默认 18dp）");
                })), rowParams());
    }

    private void strokeSection(LinearLayout content) {
        content.addView(caption("描边：setStrokeWidthDp(float) / getStrokeWidthDp() 与 setStrokeColor / getStrokeColor / "
                + "clearStrokeColor（hasStrokeColor() 查询）。仅 OUTLINED 会绘制描边；"
                + "默认宽度 1dp、颜色为角色色 outline；覆盖为 3dp + tertiary，clearStrokeColor() 后回 outline。"
                + "宽度没有 clear，只能再 setStrokeWidthDp(1) 恢复。"), captionParams());

        final MaterialButton strokeButton = createButton("Outlined");
        strokeButton.setStyle(ButtonStyle.OUTLINED);
        strokeButton.setStrokeWidthDp(3.0f);
        strokeButton.setStrokeColor(Imc.publicColorScheme.getTertiary());
        content.addView(strokeButton, itemParams());
        content.addView(row(
                actionButton("setStrokeColor(tertiary)", v -> {
                    strokeButton.setStrokeColor(Imc.publicColorScheme.getTertiary());
                    toast("setStrokeColor(tertiary) → hasStrokeColor()=" + strokeButton.hasStrokeColor()
                            + "，getStrokeColor()=" + hex(strokeButton.getStrokeColor()));
                }),
                actionButton("clearStrokeColor()", v -> {
                    strokeButton.clearStrokeColor();
                    toast("clearStrokeColor() → hasStrokeColor()=" + strokeButton.hasStrokeColor()
                            + "，描边色回到角色色 outline " + hex(strokeButton.getStrokeColor()));
                }),
                actionButton("setStrokeWidthDp(1)", v -> {
                    strokeButton.setStrokeWidthDp(1.0f);
                    toast("setStrokeWidthDp(1) → getStrokeWidthDp()=" + strokeButton.getStrokeWidthDp() + "dp（默认值）");
                })), rowParams());
    }

    private void elevationSection(LinearLayout content) {
        content.addView(caption("阴影：setElevationDp(float) / getElevationDp() / hasElevationDp() / clearElevationDp()。"
                + "ELEVATED 默认 1dp、其它样式默认 0dp；下面这枚 ELEVATED 覆盖为 8dp，"
                + "clearElevationDp() 后回 1dp。"), captionParams());

        final MaterialButton elevationButton = createButton("Elevated 8dp");
        elevationButton.setStyle(ButtonStyle.ELEVATED);
        elevationButton.setElevationDp(8.0f);
        content.addView(elevationButton, itemParams());
        content.addView(row(
                actionButton("setElevationDp(8)", v -> {
                    elevationButton.setElevationDp(8.0f);
                    toast("setElevationDp(8) → hasElevationDp()=" + elevationButton.hasElevationDp()
                            + "，getElevationDp()=" + elevationButton.getElevationDp() + "dp");
                }),
                actionButton("clearElevationDp()", v -> {
                    elevationButton.clearElevationDp();
                    toast("clearElevationDp() → hasElevationDp()=" + elevationButton.hasElevationDp()
                            + "，getElevationDp()=" + elevationButton.getElevationDp() + "dp（ELEVATED 默认 1dp）");
                })), rowParams());
    }

    private void rippleSection(LinearLayout content) {
        content.addView(caption("涟漪色：setRippleColor / getRippleColor / clearRippleColor（hasRippleColor() 查询）。"
                + "默认取内容色按 10% 不透明度计算；这里覆盖为不透明的 tertiary，按住按钮即可看到明显的紫红涟漪，"
                + "clearRippleColor() 后回到默认的 10% 内容色。"), captionParams());

        final MaterialButton rippleButton = createButton("按住看涟漪");
        rippleButton.setRippleColor(Imc.publicColorScheme.getTertiary());
        content.addView(rippleButton, itemParams());
        content.addView(row(
                actionButton("setRippleColor(tertiary)", v -> {
                    rippleButton.setRippleColor(Imc.publicColorScheme.getTertiary());
                    toast("setRippleColor(tertiary) → hasRippleColor()=" + rippleButton.hasRippleColor()
                            + "，getRippleColor()=" + hex(rippleButton.getRippleColor()));
                }),
                actionButton("clearRippleColor()", v -> {
                    rippleButton.clearRippleColor();
                    toast("clearRippleColor() → hasRippleColor()=" + rippleButton.hasRippleColor()
                            + "，涟漪色回到默认（内容色 10%）：" + hex(rippleButton.getRippleColor()));
                })), rowParams());
    }

    private void clearOverridesSection(LinearLayout content) {
        content.addView(caption("一次性清除：clearColorOverrides() 清除容器 / 内容 / 图标 / 描边 / 涟漪五类颜色覆盖，"
                + "全部回到配色方案角色色（图标尺寸与 elevation 不属于颜色，需各自 clear）；"
                + "setColorScheme(DynamicScheme) 在应用配色方案的同时也会清除全部颜色覆盖。"), captionParams());

        final MaterialButton allOverridesButton = createButton("All overrides");
        allOverridesButton.setIcon(new Icon(android.R.drawable.ic_menu_add));
        allOverridesButton.setContainerColor(Imc.publicColorScheme.getTertiaryContainer());
        allOverridesButton.setContentColor(Imc.publicColorScheme.getOnTertiaryContainer());
        allOverridesButton.setIconColor(Imc.publicColorScheme.getError());
        allOverridesButton.setRippleColor(Imc.publicColorScheme.getTertiary());
        allOverridesButton.setIconSizeDp(28.0f);
        allOverridesButton.setElevationDp(6.0f);
        content.addView(allOverridesButton, itemParams());
        content.addView(caption("上面这枚同时覆盖了容器色 tertiaryContainer、内容色 onTertiaryContainer、"
                + "图标色 error、涟漪色 tertiary，以及图标 28dp 与 elevation 6dp。"), captionParams());

        content.addView(row(
                actionButton("clearColorOverrides()", v -> {
                    allOverridesButton.clearColorOverrides();
                    toast("clearColorOverrides() → 5 类颜色覆盖全部清除：hasContainerColor()="
                            + allOverridesButton.hasContainerColor() + "，hasContentColor()="
                            + allOverridesButton.hasContentColor() + "，hasIconColor()="
                            + allOverridesButton.hasIconColor() + "，hasRippleColor()="
                            + allOverridesButton.hasRippleColor()
                            + "；图标尺寸与 elevation 覆盖保留（getIconSizeDp()="
                            + allOverridesButton.getIconSizeDp() + "dp，getElevationDp()="
                            + allOverridesButton.getElevationDp() + "dp）");
                }),
                actionButton("setColorScheme(scheme)", v -> {
                    allOverridesButton.setColorScheme(Imc.publicColorScheme);
                    toast("setColorScheme(Imc.publicColorScheme) → 同样清除全部颜色覆盖，颜色回到角色色："
                            + "hasContainerColor()=" + allOverridesButton.hasContainerColor()
                            + "，getContainerColor()=" + hex(allOverridesButton.getContainerColor()));
                }),
                actionButton("重新覆盖颜色", v -> {
                    allOverridesButton.setContainerColor(Imc.publicColorScheme.getTertiaryContainer());
                    allOverridesButton.setContentColor(Imc.publicColorScheme.getOnTertiaryContainer());
                    allOverridesButton.setIconColor(Imc.publicColorScheme.getError());
                    allOverridesButton.setRippleColor(Imc.publicColorScheme.getTertiary());
                    toast("重新 set 四类颜色覆盖，可再次对比 clear 的效果");
                })), rowParams());
    }

    private MaterialButton createButton(String text) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        return button;
    }

    private MaterialButton actionButton(String text, View.OnClickListener listener) {
        MaterialButton button = createButton(text);
        button.setStyle(ButtonStyle.TEXT);
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
            params.rightMargin = dp(4.0f);
            row.addView(child, params);
        }
        return row;
    }

    private TextView caption(String text) {
        TextView captionView = new TextView(this);
        captionView.setText(text);
        captionView.setTextSize(13.0f);
        captionView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        captionView.setTextColor(SchemeHelper.onBackgroundColor());
        return captionView;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(8.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12.0f);
        return params;
    }

    private LinearLayout.LayoutParams rowParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(16.0f);
        return params;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String hex(int color) {
        return "#" + Integer.toHexString(color);
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
