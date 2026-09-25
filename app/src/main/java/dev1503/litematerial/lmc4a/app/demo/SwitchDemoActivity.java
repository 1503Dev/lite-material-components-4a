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
import dev1503.lmc4a.v3.widget.switches.MaterialSwitch;

public class SwitchDemoActivity extends DemoActivity {

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

        basicSection(content);
        thumbColorSection(content);
        unselectedThumbColorSection(content);
        trackColorSection(content);
        unselectedTrackColorSection(content);
        trackOutlineColorSection(content);
        iconSection(content);
        trackSizeSection(content);
        thumbRadiusSection(content);
        colorSchemeSection(content);

        setContentView(scrollView);
    }

    private void basicSection(LinearLayout content) {
        content.addView(caption("默认尺寸：轨道 54 × 32dp（全圆角），拇指选中态直径 24dp、未选中态 16dp；"
                + "未选中时轨道填充 surfaceContainerHighest 并描 2dp outline，选中时轨道 primary、拇指 onPrimary。"),
                captionParams());
        content.addView(createSwitch("Unchecked（默认未选中）"), itemParams());

        MaterialSwitch checkedSwitch = createSwitch("Checked（setChecked(true)）");
        checkedSwitch.setChecked(true);
        content.addView(checkedSwitch, itemParams());

        MaterialSwitch disabledUncheckedSwitch = createSwitch("Disabled unchecked（容器 12% / 拇指 38% 不透明度）");
        disabledUncheckedSwitch.setEnabled(false);
        content.addView(disabledUncheckedSwitch, itemParams());

        MaterialSwitch disabledCheckedSwitch = createSwitch("Disabled checked");
        disabledCheckedSwitch.setChecked(true);
        disabledCheckedSwitch.setEnabled(false);
        content.addView(disabledCheckedSwitch, itemParams());

        MaterialSwitch noLabelSwitch = new MaterialSwitch(this);
        content.addView(row(noLabelSwitch, label("无标签开关（图标与轨道靠左绘制，文字留白 62dp）")), rowParams());
    }

    private void thumbColorSection(LinearLayout content) {
        content.addView(caption("setThumbColor / getThumbColor / clearThumbColor（hasThumbColor() 查询）："
                + "选中态拇指色，默认角色色 onPrimary。下面覆盖为 tertiary，"
                + "clearThumbColor() 后回到 onPrimary。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Thumb color = tertiary");
        materialSwitch.setChecked(true);
        materialSwitch.setThumbColor(Imc.publicColorScheme.getTertiary());
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setThumbColor(tertiary)", v -> {
                    materialSwitch.setThumbColor(Imc.publicColorScheme.getTertiary());
                    toast("setThumbColor(tertiary) → hasThumbColor()=" + materialSwitch.hasThumbColor()
                            + "，getThumbColor()=" + hex(materialSwitch.getThumbColor()));
                }),
                actionButton("clearThumbColor()", v -> {
                    materialSwitch.clearThumbColor();
                    toast("clearThumbColor() → hasThumbColor()=" + materialSwitch.hasThumbColor()
                            + "，拇指色回到角色色 onPrimary " + hex(materialSwitch.getThumbColor()));
                })), rowParams());
    }

    private void unselectedThumbColorSection(LinearLayout content) {
        content.addView(caption("setUnselectedThumbColor / getUnselectedThumbColor / clearUnselectedThumbColor："
                + "未选中态拇指色，默认角色色 outline。下面覆盖为 tertiary（未选中态即可看到紫红拇指），"
                + "clear 后回到 outline。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Unselected thumb = tertiary");
        materialSwitch.setUnselectedThumbColor(Imc.publicColorScheme.getTertiary());
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setUnselectedThumbColor(tertiary)", v -> {
                    materialSwitch.setUnselectedThumbColor(Imc.publicColorScheme.getTertiary());
                    toast("setUnselectedThumbColor(tertiary) → hasUnselectedThumbColor()="
                            + materialSwitch.hasUnselectedThumbColor() + "，getUnselectedThumbColor()="
                            + hex(materialSwitch.getUnselectedThumbColor()));
                }),
                actionButton("clearUnselectedThumbColor()", v -> {
                    materialSwitch.clearUnselectedThumbColor();
                    toast("clearUnselectedThumbColor() → hasUnselectedThumbColor()="
                            + materialSwitch.hasUnselectedThumbColor() + "，拇指色回到角色色 outline "
                            + hex(materialSwitch.getUnselectedThumbColor()));
                })), rowParams());
    }

    private void trackColorSection(LinearLayout content) {
        content.addView(caption("setTrackColor / getTrackColor / clearTrackColor（hasTrackColor() 查询）："
                + "选中态轨道色，默认角色色 primary。下面覆盖为 tertiaryContainer，"
                + "clearTrackColor() 后回到 primary；未选中时轨道色由 setUnselectedTrackColor 决定。"),
                captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Track color = tertiaryContainer");
        materialSwitch.setChecked(true);
        materialSwitch.setTrackColor(Imc.publicColorScheme.getTertiaryContainer());
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setTrackColor(tertiaryContainer)", v -> {
                    materialSwitch.setTrackColor(Imc.publicColorScheme.getTertiaryContainer());
                    toast("setTrackColor(tertiaryContainer) → hasTrackColor()=" + materialSwitch.hasTrackColor()
                            + "，getTrackColor()=" + hex(materialSwitch.getTrackColor()));
                }),
                actionButton("clearTrackColor()", v -> {
                    materialSwitch.clearTrackColor();
                    toast("clearTrackColor() → hasTrackColor()=" + materialSwitch.hasTrackColor()
                            + "，轨道色回到角色色 primary " + hex(materialSwitch.getTrackColor()));
                })), rowParams());
    }

    private void unselectedTrackColorSection(LinearLayout content) {
        content.addView(caption("setUnselectedTrackColor / getUnselectedTrackColor / clearUnselectedTrackColor："
                + "未选中态轨道填充色，默认角色色 surfaceContainerHighest。下面覆盖为 tertiaryContainer，"
                + "clear 后回到 surfaceContainerHighest。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Unselected track = tertiaryContainer");
        materialSwitch.setUnselectedTrackColor(Imc.publicColorScheme.getTertiaryContainer());
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setUnselectedTrackColor(tertiaryContainer)", v -> {
                    materialSwitch.setUnselectedTrackColor(Imc.publicColorScheme.getTertiaryContainer());
                    toast("setUnselectedTrackColor(tertiaryContainer) → hasUnselectedTrackColor()="
                            + materialSwitch.hasUnselectedTrackColor() + "，getUnselectedTrackColor()="
                            + hex(materialSwitch.getUnselectedTrackColor()));
                }),
                actionButton("clearUnselectedTrackColor()", v -> {
                    materialSwitch.clearUnselectedTrackColor();
                    toast("clearUnselectedTrackColor() → hasUnselectedTrackColor()="
                            + materialSwitch.hasUnselectedTrackColor() + "，轨道色回到角色色 surfaceContainerHighest "
                            + hex(materialSwitch.getUnselectedTrackColor()));
                })), rowParams());
    }

    private void trackOutlineColorSection(LinearLayout content) {
        content.addView(caption("setTrackOutlineColor / getTrackOutlineColor / clearTrackOutlineColor："
                + "未选中态的 2dp 轨道描边色，默认角色色 outline。下面覆盖为 tertiary，"
                + "clear 后回到 outline（禁用态描边固定用 outlineVariant，不受此覆盖影响）。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Track outline = tertiary");
        materialSwitch.setTrackOutlineColor(Imc.publicColorScheme.getTertiary());
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setTrackOutlineColor(tertiary)", v -> {
                    materialSwitch.setTrackOutlineColor(Imc.publicColorScheme.getTertiary());
                    toast("setTrackOutlineColor(tertiary) → hasTrackOutlineColor()="
                            + materialSwitch.hasTrackOutlineColor() + "，getTrackOutlineColor()="
                            + hex(materialSwitch.getTrackOutlineColor()));
                }),
                actionButton("clearTrackOutlineColor()", v -> {
                    materialSwitch.clearTrackOutlineColor();
                    toast("clearTrackOutlineColor() → hasTrackOutlineColor()="
                            + materialSwitch.hasTrackOutlineColor() + "，描边色回到角色色 outline "
                            + hex(materialSwitch.getTrackOutlineColor()));
                })), rowParams());
    }

    private void iconSection(LinearLayout content) {
        content.addView(caption("setIcon(Icon) / getIcon() / hasIcon() / clearIcon()：设置选中态拇指图标，"
                + "图标按 16dp 居中绘制在拇指上，未选中态不绘制。"
                + "setIconColor / getIconColor / clearIconColor：图标色默认角色色 onPrimaryContainer，"
                + "下面覆盖为 error，clear 后回 onPrimaryContainer。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Icon + icon color = error");
        materialSwitch.setChecked(true);
        materialSwitch.setIcon(new Icon(android.R.drawable.ic_menu_add));
        materialSwitch.setIconColor(Imc.publicColorScheme.getError());
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setIcon(add)", v -> {
                    materialSwitch.setIcon(new Icon(android.R.drawable.ic_menu_add));
                    toast("setIcon(ic_menu_add) → hasIcon()=" + materialSwitch.hasIcon()
                            + "，getIcon()=" + (materialSwitch.getIcon() == null ? "null" : "Icon"));
                }),
                actionButton("clearIcon()", v -> {
                    materialSwitch.clearIcon();
                    toast("clearIcon() → hasIcon()=" + materialSwitch.hasIcon()
                            + "，getIcon()=" + (materialSwitch.getIcon() == null ? "null" : "Icon")
                            + "（拇指上不再绘制图标）");
                }),
                actionButton("setIconColor(error)", v -> {
                    materialSwitch.setIconColor(Imc.publicColorScheme.getError());
                    toast("setIconColor(error) → hasIconColor()=" + materialSwitch.hasIconColor()
                            + "，getIconColor()=" + hex(materialSwitch.getIconColor()));
                }),
                actionButton("clearIconColor()", v -> {
                    materialSwitch.clearIconColor();
                    toast("clearIconColor() → hasIconColor()=" + materialSwitch.hasIconColor()
                            + "，图标色回到角色色 onPrimaryContainer " + hex(materialSwitch.getIconColor()));
                })), rowParams());
    }

    private void trackSizeSection(LinearLayout content) {
        content.addView(caption("setTrackWidthDp / getTrackWidthDp / hasTrackWidthDp / clearTrackWidthDp："
                + "轨道宽度默认 54dp（控件最小宽度 = 轨道宽 + 8dp 文字留白）；"
                + "setTrackHeightDp / … / clearTrackHeightDp：轨道高度默认 32dp，圆角半径始终取高度的一半。"
                + "下面这枚覆盖为 80 × 40dp，两枚 clear 按钮分别恢复 54dp 与 32dp。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Track 80 × 40dp");
        materialSwitch.setTrackWidthDp(80.0f);
        materialSwitch.setTrackHeightDp(40.0f);
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setTrackWidthDp(80)", v -> {
                    materialSwitch.setTrackWidthDp(80.0f);
                    toast("setTrackWidthDp(80) → hasTrackWidthDp()=" + materialSwitch.hasTrackWidthDp()
                            + "，getTrackWidthDp()=" + materialSwitch.getTrackWidthDp() + "dp（默认 54dp）");
                }),
                actionButton("clearTrackWidthDp()", v -> {
                    materialSwitch.clearTrackWidthDp();
                    toast("clearTrackWidthDp() → hasTrackWidthDp()=" + materialSwitch.hasTrackWidthDp()
                            + "，getTrackWidthDp()=" + materialSwitch.getTrackWidthDp() + "dp（回默认 54dp）");
                }),
                actionButton("setTrackHeightDp(40)", v -> {
                    materialSwitch.setTrackHeightDp(40.0f);
                    toast("setTrackHeightDp(40) → hasTrackHeightDp()=" + materialSwitch.hasTrackHeightDp()
                            + "，getTrackHeightDp()=" + materialSwitch.getTrackHeightDp() + "dp（默认 32dp）");
                }),
                actionButton("clearTrackHeightDp()", v -> {
                    materialSwitch.clearTrackHeightDp();
                    toast("clearTrackHeightDp() → hasTrackHeightDp()=" + materialSwitch.hasTrackHeightDp()
                            + "，getTrackHeightDp()=" + materialSwitch.getTrackHeightDp() + "dp（回默认 32dp）");
                })), rowParams());
    }

    private void thumbRadiusSection(LinearLayout content) {
        content.addView(caption("setThumbRadiusDp / getThumbRadiusDp / hasThumbRadiusDp / clearThumbRadiusDp："
                + "拇指半径默认随选中态变化（选中 12dp、未选中 8dp）；按住时直径按 16/24dp → 28dp 插值放大"
                + "（与上一版一致），设置了固定半径后按压不会小于该半径。"
                + "下面这枚固定为 16dp，clear 后回默认的选中 12dp / 未选中 8dp。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("Thumb radius = 16dp");
        materialSwitch.setChecked(true);
        materialSwitch.setThumbRadiusDp(16.0f);
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setThumbRadiusDp(16)", v -> {
                    materialSwitch.setThumbRadiusDp(16.0f);
                    toast("setThumbRadiusDp(16) → hasThumbRadiusDp()=" + materialSwitch.hasThumbRadiusDp()
                            + "，getThumbRadiusDp()=" + materialSwitch.getThumbRadiusDp() + "dp");
                }),
                actionButton("clearThumbRadiusDp()", v -> {
                    materialSwitch.clearThumbRadiusDp();
                    toast("clearThumbRadiusDp() → hasThumbRadiusDp()=" + materialSwitch.hasThumbRadiusDp()
                            + "，getThumbRadiusDp()=" + materialSwitch.getThumbRadiusDp()
                            + "dp（选中态默认 12dp）");
                })), rowParams());
    }

    private void colorSchemeSection(LinearLayout content) {
        content.addView(caption("setColorScheme(DynamicScheme) 会一次性清除本组件的全部颜色与尺寸覆盖"
                + "（拇指色 / 未选中拇指色 / 轨道色 / 未选中轨道色 / 描边色 / 图标色，"
                + "以及轨道宽高与拇指半径），全部回到配色方案角色色与默认尺寸。"), captionParams());

        final MaterialSwitch materialSwitch = createSwitch("全部覆盖已开启");
        materialSwitch.setChecked(true);
        applyAllOverrides(materialSwitch);
        content.addView(materialSwitch, itemParams());
        content.addView(row(
                actionButton("setColorScheme(scheme)", v -> {
                    materialSwitch.setColorScheme(Imc.publicColorScheme);
                    toast("setColorScheme() → hasThumbColor()=" + materialSwitch.hasThumbColor()
                            + "，hasTrackColor()=" + materialSwitch.hasTrackColor()
                            + "，hasTrackWidthDp()=" + materialSwitch.hasTrackWidthDp()
                            + "，hasThumbRadiusDp()=" + materialSwitch.hasThumbRadiusDp()
                            + "，getTrackWidthDp()=" + materialSwitch.getTrackWidthDp() + "dp");
                }),
                actionButton("再次应用全部覆盖", v -> {
                    applyAllOverrides(materialSwitch);
                    toast("已重新覆盖颜色与尺寸，可再次对比 setColorScheme() 的清除效果");
                })), rowParams());
    }

    private void applyAllOverrides(MaterialSwitch materialSwitch) {
        materialSwitch.setThumbColor(Imc.publicColorScheme.getTertiary());
        materialSwitch.setUnselectedThumbColor(Imc.publicColorScheme.getTertiary());
        materialSwitch.setTrackColor(Imc.publicColorScheme.getTertiaryContainer());
        materialSwitch.setUnselectedTrackColor(Imc.publicColorScheme.getTertiaryContainer());
        materialSwitch.setTrackOutlineColor(Imc.publicColorScheme.getTertiary());
        materialSwitch.setIcon(new Icon(android.R.drawable.ic_menu_add));
        materialSwitch.setIconColor(Imc.publicColorScheme.getError());
        materialSwitch.setTrackWidthDp(72.0f);
        materialSwitch.setTrackHeightDp(36.0f);
        materialSwitch.setThumbRadiusDp(14.0f);
    }

    private MaterialSwitch createSwitch(String text) {
        MaterialSwitch materialSwitch = new MaterialSwitch(this);
        materialSwitch.setText(text);
        return materialSwitch;
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
            params.rightMargin = dp(4.0f);
            row.addView(child, params);
        }
        return row;
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        caption.setTextColor(SchemeHelper.onBackgroundColor());
        return caption;
    }

    private TextView label(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(12.0f);
        label.setTextColor(SchemeHelper.onBackgroundColor());
        return label;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
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
