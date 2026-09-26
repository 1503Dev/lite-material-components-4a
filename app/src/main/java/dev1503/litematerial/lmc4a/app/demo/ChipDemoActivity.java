package dev1503.litematerial.lmc4a.app.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.chip.ChipStyle;
import dev1503.lmc4a.v3.widget.chip.MaterialChip;
import dev1503.litematerial.lmc4a.app.SchemeHelper;

public class ChipDemoActivity extends DemoActivity {

    private static final int[] ICON_RES_IDS = {
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_manage,
            android.R.drawable.ic_menu_compass,
    };

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private MaterialChip assistChip;
    private MaterialChip filterChip;
    private MaterialChip inputChip;
    private MaterialChip demoChip;

    private int colorPrimaryContainer;
    private int colorOnPrimaryContainer;
    private int colorTertiaryContainer;
    private int colorOnTertiaryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);
        colorPrimaryContainer = dynamicColors.primaryContainer()
                .getArgb(MaterialChip.publicColorScheme);
        colorOnPrimaryContainer = dynamicColors.onPrimaryContainer()
                .getArgb(MaterialChip.publicColorScheme);
        colorTertiaryContainer = dynamicColors.tertiaryContainer()
                .getArgb(MaterialChip.publicColorScheme);
        colorOnTertiaryContainer = dynamicColors.onTertiaryContainer()
                .getArgb(MaterialChip.publicColorScheme);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("MaterialChip 是单个 chip 组件，继承 CompoundButton：圆形 32dp 高、8dp 圆角。"
                + "setStyle(ChipStyle) 切换四种样式 —— ASSIST / SUGGESTION 不可选中、仅点击；"
                + "FILTER / INPUT 可选中并带选中勾。setText(CharSequence) 设置文字（继承 TextView 平台 API），"
                + "setIcon(Icon) 设置前导图标，统一使用 Icon 包装类型。"));

        content.addView(caption("ASSIST：纯展示 + 点击。未覆盖颜色时容器透明、描边 outline、文字 onSurface。"
                + "第一个带前导图标，第二个只有文字。"));
        assistChip = new MaterialChip(this);
        assistChip.setStyle(ChipStyle.ASSIST);
        assistChip.setText("Assist");
        assistChip.setIcon(new Icon(ICON_RES_IDS[0]));
        assistChip.setOnClickListener(clickToast("assistChip"));
        content.addView(chipRow(assistChip, textOnlyChip(ChipStyle.SUGGESTION, "只读标签")));

        content.addView(caption("SUGGESTION：与 ASSIST 外观一致，同样只响应点击、不可选中。"));
        MaterialChip suggestionChip = new MaterialChip(this);
        suggestionChip.setStyle(ChipStyle.SUGGESTION);
        suggestionChip.setText("Suggestion");
        suggestionChip.setIcon(new Icon(ICON_RES_IDS[1]));
        suggestionChip.setOnClickListener(clickToast("suggestionChip"));
        content.addView(chipRow(suggestionChip));

        content.addView(caption("FILTER：setChecked(true/false) 切换选中；选中时容器 secondaryContainer、"
                + "文字 onSecondaryContainer、并显示前导选中勾，未选中时描边 outline。"
                + "setOnCheckedChangeListener 监听选中变化。下面的第一个同时设了前导图标，选中时勾会替换图标。"));
        filterChip = new MaterialChip(this);
        filterChip.setStyle(ChipStyle.FILTER);
        filterChip.setText("Filter");
        filterChip.setIcon(new Icon(ICON_RES_IDS[2]));
        filterChip.setOnCheckedChangeListener(checkedToast("filterChip"));
        MaterialChip filterChip2 = new MaterialChip(this);
        filterChip2.setStyle(ChipStyle.FILTER);
        filterChip2.setText("无图标");
        content.addView(chipRow(filterChip, filterChip2));

        content.addView(caption("INPUT：默认显示尾部关闭图标（可移除），点击关闭图标不会触发选中；"
                + "setOnCloseClickListener 监听关闭，setCloseIconShown(false) 可隐藏关闭图标。"));
        inputChip = new MaterialChip(this);
        inputChip.setStyle(ChipStyle.INPUT);
        inputChip.setText("Input");
        inputChip.setIcon(new Icon(ICON_RES_IDS[3]));
        inputChip.setOnCloseClickListener(new MaterialChip.OnCloseClickListener() {
            @Override
            public void onCloseClick(MaterialChip chip) {
                toast("onCloseClick：" + chip.getText() + " 被移除");
            }
        });
        content.addView(chipRow(inputChip));

        content.addView(caption("下面是用于演示各项 API 的芯片：setStyle / setCheckable / setChecked / "
                + "setIcon / setCloseIconShown / setIconColor。"));
        demoChip = new MaterialChip(this);
        demoChip.setStyle(ChipStyle.FILTER);
        demoChip.setText("Demo");
        demoChip.setIcon(new Icon(ICON_RES_IDS[0]));
        demoChip.setOnCheckedChangeListener(checkedToast("demoChip"));
        content.addView(chipRow(demoChip));

        content.addView(caption("颜色覆盖三件套：setContainerColor / setContentColor / setIconColor / "
                + "setOutlineColor / setCloseIconColor 都有 getXxxColor()（返回当前生效色）与 hasXxxColor()"
                + "（是否显式覆盖）、clearXxxColor()（恢复角色色）；setColorScheme(DynamicScheme) 一次清除全部覆盖。"
                + "圆角与描边粗细：setCornerRadiusDp / getCornerRadiusDp / clearCornerRadiusDp、"
                + "setStrokeWidthDp / getStrokeWidthDp / clearStrokeWidthDp。"));
        content.addView(controlRow(createStyleControls()));
        content.addView(controlRow(createStateControls()));
        content.addView(controlRow(createColorControls()));
        content.addView(controlRow(createGeometryControls()));

        content.addView(caption("取值查询：getStyle() / isCheckable() / getContainerColor() / getContentColor() / "
                + "getIconColor() / getOutlineColor() / getCloseIconColor()，以及 hasXxx 系列判断是否已覆盖。"));
        content.addView(controlRow(createQueryControls()));

        content.addView(spacer(32.0f));
        setContentView(scrollView);
    }

    private LinearLayout createStyleControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        ChipStyle[] styles = ChipStyle.values();
        for (int i = 0; i < styles.length; i++) {
            final ChipStyle style = styles[i];
            MaterialButton button = controlButton(style.name());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    demoChip.setStyle(style);
                    toast("setStyle(" + style + ") → getStyle() = " + demoChip.getStyle()
                            + "，isCheckable() = " + demoChip.isCheckable()
                            + "，isCloseIconShown() = " + demoChip.isCloseIconShown());
                }
            });
            row.addView(button, controlParams());
        }
        return row;
    }

    private LinearLayout createStateControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton check = controlButton("setChecked(true)");
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setChecked(true);
                toast("setChecked(true) → isChecked() = " + demoChip.isChecked());
            }
        });
        row.addView(check, controlParams());

        MaterialButton uncheck = controlButton("setChecked(false)");
        uncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setChecked(false);
                toast("setChecked(false) → isChecked() = " + demoChip.isChecked());
            }
        });
        row.addView(uncheck, controlParams());

        MaterialButton toggleCheckable = controlButton("setCheckable(false)");
        toggleCheckable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setCheckable(false);
                toast("setCheckable(false) → isCheckable() = " + demoChip.isCheckable()
                        + "，isChecked() = " + demoChip.isChecked());
            }
        });
        row.addView(toggleCheckable, controlParams());

        MaterialButton clearCheckable = controlButton("clearCheckable");
        clearCheckable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.clearCheckable();
                toast("clearCheckable() → isCheckable() = " + demoChip.isCheckable()
                        + "（回到样式默认值）");
            }
        });
        row.addView(clearCheckable, controlParams());

        return row;
    }

    private LinearLayout createColorControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton setColors = controlButton("setColors");
        setColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setContainerColor(colorPrimaryContainer);
                demoChip.setContentColor(colorOnPrimaryContainer);
                demoChip.setIconColor(colorOnPrimaryContainer);
                demoChip.setOutlineColor(colorTertiaryContainer);
                demoChip.setCloseIconColor(colorOnTertiaryContainer);
                toast("覆盖后：容器 " + hex(demoChip.getContainerColor())
                        + "、内容 " + hex(demoChip.getContentColor())
                        + "、图标 " + hex(demoChip.getIconColor())
                        + "、描边 " + hex(demoChip.getOutlineColor())
                        + "、关闭 " + hex(demoChip.getCloseIconColor()));
            }
        });
        row.addView(setColors, controlParams());

        MaterialButton clearColors = controlButton("clearColors");
        clearColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.clearContainerColor();
                demoChip.clearContentColor();
                demoChip.clearIconColor();
                demoChip.clearOutlineColor();
                demoChip.clearCloseIconColor();
                toast("clear 后：容器 " + hex(demoChip.getContainerColor())
                        + "、内容 " + hex(demoChip.getContentColor())
                        + "、描边 " + hex(demoChip.getOutlineColor())
                        + "（回到角色色）");
            }
        });
        row.addView(clearColors, controlParams());

        MaterialButton scheme = controlButton("setColorScheme(public)");
        scheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setColorScheme(MaterialChip.publicColorScheme);
                toast("setColorScheme(publicColorScheme)：清除全部颜色覆盖，容器 "
                        + hex(demoChip.getContainerColor()) + "、内容 "
                        + hex(demoChip.getContentColor()));
            }
        });
        row.addView(scheme, controlParams());

        return row;
    }

    private LinearLayout createGeometryControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton radius = controlButton("圆角 4dp");
        radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setCornerRadiusDp(4.0f);
                toast("setCornerRadiusDp(4) → getCornerRadiusDp() = " + demoChip.getCornerRadiusDp());
            }
        });
        row.addView(radius, controlParams());

        MaterialButton clearRadius = controlButton("clearCornerRadiusDp");
        clearRadius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.clearCornerRadiusDp();
                toast("clearCornerRadiusDp() → getCornerRadiusDp() = " + demoChip.getCornerRadiusDp());
            }
        });
        row.addView(clearRadius, controlParams());

        MaterialButton close = controlButton("setCloseIconShown");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean shown = !demoChip.isCloseIconShown();
                demoChip.setCloseIconShown(shown);
                toast("setCloseIconShown(" + shown + ") → isCloseIconShown() = "
                        + demoChip.isCloseIconShown() + "，hasCloseIconShown() = "
                        + demoChip.hasCloseIconShown());
            }
        });
        row.addView(close, controlParams());

        MaterialButton stroke = controlButton("描边 2dp");
        stroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoChip.setStrokeWidthDp(2.0f);
                toast("setStrokeWidthDp(2) → getStrokeWidthDp() = " + demoChip.getStrokeWidthDp());
            }
        });
        row.addView(stroke, controlParams());

        return row;
    }

    private LinearLayout createQueryControls() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton query = controlButton("getState");
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("getStyle() = " + demoChip.getStyle()
                        + "、isCheckable() = " + demoChip.isCheckable()
                        + "、isChecked() = " + demoChip.isChecked()
                        + "、getIcon() = " + (demoChip.getIcon() != null ? "Icon" : "null"));
            }
        });
        row.addView(query, controlParams());

        MaterialButton overrides = controlButton("hasXxx");
        overrides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("hasContainerColor() = " + demoChip.hasContainerColor()
                        + "、hasContentColor() = " + demoChip.hasContentColor()
                        + "、hasIconColor() = " + demoChip.hasIconColor()
                        + "、hasOutlineColor() = " + demoChip.hasOutlineColor()
                        + "、hasCloseIconColor() = " + demoChip.hasCloseIconColor());
            }
        });
        row.addView(overrides, controlParams());

        return row;
    }

    private MaterialChip textOnlyChip(ChipStyle style, String text) {
        MaterialChip chip = new MaterialChip(this);
        chip.setStyle(style);
        chip.setText(text);
        return chip;
    }

    private View.OnClickListener clickToast(final String name) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast(name + " onClick：getStyle() = " + ((MaterialChip) v).getStyle());
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener checkedToast(final String name) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toast(name + " onCheckedChanged：isChecked() = " + isChecked);
            }
        };
    }

    private View chipRow(View... chips) {
        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        int padding = dp(16.0f);
        row.setPadding(padding, 0, padding, dp(8.0f));
        for (int i = 0; i < chips.length; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = dp(8.0f);
            row.addView(chips[i], params);
        }
        scrollView.addView(row, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return scrollView;
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
