package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicColor;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.slider.MaterialSlider;

public class SliderDemoActivity extends Activity {

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

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

        content.addView(caption("默认 — setMinValue 默认 0、setMaxValue 默认 100、setStep 默认 0（连续），"
                + "setValue 设初值 0，气泡默认开启（isValueIndicatorEnabled() == true）"), captionParams());
        final TextView defaultLabel = createLabel("默认 (0-100): 0");
        MaterialSlider defaultSlider = new MaterialSlider(this);
        defaultSlider.setOnValueChangeListener(labelListener(defaultLabel, defaultSlider));
        content.addView(defaultLabel, labelParams());
        content.addView(defaultSlider, itemParams());

        content.addView(caption("setMinValue(0) / setMaxValue(10) / setStep(0.5) / setValue(5.0) — "
                + "浮点上下限请用 setMinValue(0f) / setMaxValue(10f)，旧 setMin/setMax(float) 已删除（lossy conversion）"), captionParams());
        final TextView stepLabel = createLabel("步长 0.5 (0-10): 5.0");
        MaterialSlider stepSlider = slider(0f, 10f, 0.5f, 5f);
        stepSlider.setOnValueChangeListener(labelListener(stepLabel, stepSlider));
        content.addView(stepLabel, labelParams());
        content.addView(stepSlider, itemParams());

        content.addView(caption("setMaxValue 接受浮点 — 0-1 步长 0.1，setValue(0.3f)；"
                + "getValue() 返回真实值，formatProgress() 按步长决定小数位"), captionParams());
        final TextView preciseLabel = createLabel("步长 0.1 (0-1): 0.3");
        MaterialSlider preciseSlider = slider(0f, 1f, 0.1f, 0.3f);
        preciseSlider.setOnValueChangeListener(labelListener(preciseLabel, preciseSlider));
        content.addView(preciseLabel, labelParams());
        content.addView(preciseSlider, itemParams());

        content.addView(caption("setMinValue(1) / setMaxValue(100) / setStep(1) / setValue(25)"), captionParams());
        final TextView rangeLabel = createLabel("整数步长 (1-100): 25");
        MaterialSlider rangeSlider = slider(1f, 100f, 1f, 25f);
        rangeSlider.setOnValueChangeListener(labelListener(rangeLabel, rangeSlider));
        content.addView(rangeLabel, labelParams());
        content.addView(rangeSlider, itemParams());

        content.addView(caption("禁用态 — 激活轨道改用 setDisabledTrackColor 的角色色"
                + "（默认 outlineVariant 与 outline 的 50% 混合），拇指默认跟随禁用轨道色"), captionParams());
        final TextView disabledLabel = createLabel("禁用 (0-100): 50");
        MaterialSlider disabledSlider = slider(0f, 100f, 0f, 50f);
        disabledSlider.setEnabled(false);
        content.addView(disabledLabel, labelParams());
        content.addView(disabledSlider, itemParams());

        final TextView disabledStepLabel = createLabel("禁用 + 步长 0.25 (0-5): 2.50");
        MaterialSlider disabledStepSlider = slider(0f, 5f, 0.25f, 2.5f);
        disabledStepSlider.setEnabled(false);
        content.addView(disabledStepLabel, labelParams());
        content.addView(disabledStepSlider, itemParams());

        content.addView(caption("颜色覆盖 — setThumbColor(tertiary)、setActiveTrackColor(error)、"
                + "setInactiveTrackColor(secondaryContainer)、setValueIndicatorColor(onTertiaryContainer)；"
                + "默认分别是 primary / primary / surfaceContainerHighest / primary"), captionParams());
        final MaterialSlider colorSlider = slider(0f, 100f, 0f, 60f);
        colorSlider.setThumbColor(role(dynamicColors.tertiary()));
        colorSlider.setActiveTrackColor(role(dynamicColors.error()));
        colorSlider.setInactiveTrackColor(role(dynamicColors.secondaryContainer()));
        colorSlider.setValueIndicatorColor(role(dynamicColors.onTertiaryContainer()));
        content.addView(colorSlider, itemParams());
        content.addView(action("读取颜色 getThumbColor()/getActiveTrackColor()/getInactiveTrackColor()/getValueIndicatorColor()",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("thumb=" + hex(colorSlider.getThumbColor())
                                + " active=" + hex(colorSlider.getActiveTrackColor())
                                + " inactive=" + hex(colorSlider.getInactiveTrackColor())
                                + " indicator=" + hex(colorSlider.getValueIndicatorColor()));
                    }
                }), itemParams());
        content.addView(action("clearThumbColor() → 回到 primary 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = colorSlider.getThumbColor();
                colorSlider.clearThumbColor();
                toast("clearThumbColor: " + hex(before) + " → " + hex(colorSlider.getThumbColor()) + "（primary 角色色）");
            }
        }), itemParams());
        content.addView(action("clearActiveTrackColor() → 回到 primary 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = colorSlider.getActiveTrackColor();
                colorSlider.clearActiveTrackColor();
                toast("clearActiveTrackColor: " + hex(before) + " → "
                        + hex(colorSlider.getActiveTrackColor()) + "（primary 角色色）");
            }
        }), itemParams());
        content.addView(action("clearInactiveTrackColor() → 回到 surfaceContainerHighest 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = colorSlider.getInactiveTrackColor();
                colorSlider.clearInactiveTrackColor();
                toast("clearInactiveTrackColor: " + hex(before) + " → "
                        + hex(colorSlider.getInactiveTrackColor()) + "（surfaceContainerHighest 角色色）");
            }
        }), itemParams());
        content.addView(action("clearValueIndicatorColor() → 气泡回到 primary 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = colorSlider.getValueIndicatorColor();
                colorSlider.clearValueIndicatorColor();
                toast("clearValueIndicatorColor: " + hex(before) + " → "
                        + hex(colorSlider.getValueIndicatorColor()) + "（primary 角色色）");
            }
        }), itemParams());

        content.addView(caption("禁用态颜色覆盖 — setDisabledTrackColor(tertiaryContainer)、"
                + "setDisabledThumbColor(error)；默认禁用轨道是 outlineVariant+outline 混合、拇指跟随禁用轨道色"), captionParams());
        final MaterialSlider disabledColorSlider = slider(0f, 100f, 0f, 40f);
        disabledColorSlider.setDisabledTrackColor(role(dynamicColors.tertiaryContainer()));
        disabledColorSlider.setDisabledThumbColor(role(dynamicColors.error()));
        disabledColorSlider.setEnabled(false);
        content.addView(disabledColorSlider, itemParams());
        content.addView(action("clearDisabledTrackColor() + clearDisabledThumbColor() → 回到混合色 / 跟随轨道", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int beforeTrack = disabledColorSlider.getDisabledTrackColor();
                int beforeThumb = disabledColorSlider.getDisabledThumbColor();
                disabledColorSlider.clearDisabledTrackColor();
                disabledColorSlider.clearDisabledThumbColor();
                toast("disabledTrack: " + hex(beforeTrack) + " → " + hex(disabledColorSlider.getDisabledTrackColor())
                        + "（outlineVariant+outline 混合），disabledThumb: " + hex(beforeThumb)
                        + " → " + hex(disabledColorSlider.getDisabledThumbColor()) + "（跟随禁用轨道色）");
            }
        }), itemParams());

        content.addView(action("setColorScheme(null) → 一次性清除全部颜色覆盖，回到角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSlider.setColorScheme(null);
                disabledColorSlider.setColorScheme(null);
                toast("setColorScheme(null): 全部颜色覆盖已清除，thumb=" + hex(colorSlider.getThumbColor())
                        + " active=" + hex(colorSlider.getActiveTrackColor()));
            }
        }), itemParams());

        content.addView(caption("尺寸 — setThumbRadiusDp(16f)（默认 10dp，即拇指直径 20dp）、"
                + "setTrackHeightDp(12f)（默认 4dp）；getTrackWidthDp() 只读（可用区域宽度，无 setter / clear）"), captionParams());
        final MaterialSlider sizeSlider = slider(0f, 100f, 0f, 40f);
        sizeSlider.setThumbRadiusDp(16f);
        sizeSlider.setTrackHeightDp(12f);
        content.addView(sizeSlider, itemParams());
        content.addView(action("读取尺寸 getThumbRadiusDp()/getTrackHeightDp()/getTrackWidthDp()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("thumbRadius=" + sizeSlider.getThumbRadiusDp() + "dp trackHeight="
                        + sizeSlider.getTrackHeightDp() + "dp trackWidth=" + sizeSlider.getTrackWidthDp() + "dp（只读）");
            }
        }), itemParams());
        content.addView(action("clearThumbRadiusDp() + clearTrackHeightDp() → 回到 10dp / 4dp", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float beforeRadius = sizeSlider.getThumbRadiusDp();
                float beforeHeight = sizeSlider.getTrackHeightDp();
                sizeSlider.clearThumbRadiusDp();
                sizeSlider.clearTrackHeightDp();
                toast("thumbRadius: " + beforeRadius + "dp → " + sizeSlider.getThumbRadiusDp()
                        + "dp（默认 10dp），trackHeight: " + beforeHeight + "dp → "
                        + sizeSlider.getTrackHeightDp() + "dp（默认 4dp）");
            }
        }), itemParams());

        content.addView(caption("气泡开关 — setValueIndicatorEnabled(false) 后按下与拖动都不再弹出气泡（默认 true）"), captionParams());
        final MaterialSlider noIndicatorSlider = slider(0f, 100f, 0f, 70f);
        noIndicatorSlider.setValueIndicatorEnabled(false);
        content.addView(noIndicatorSlider, itemParams());
        content.addView(action("切换 isValueIndicatorEnabled() / 恢复气泡", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enable = !noIndicatorSlider.isValueIndicatorEnabled();
                noIndicatorSlider.setValueIndicatorEnabled(enable);
                toast("setValueIndicatorEnabled(" + enable + ") → isValueIndicatorEnabled()="
                        + noIndicatorSlider.isValueIndicatorEnabled());
            }
        }), itemParams());

        content.addView(caption("setOnValueChangeListener — onValueChangeStart（按下）/ onValueChange（数值变化）/ "
                + "onValueChangeFinished（抬起或取消），三个回调传入的都是真实值；按下与抬起用 Toast 反馈"), captionParams());
        final TextView listenerLabel = createLabel("监听器 (0-100): 0");
        MaterialSlider listenerSlider = new MaterialSlider(this);
        listenerSlider.setOnValueChangeListener(new MaterialSlider.OnValueChangeListener() {
            @Override
            public void onValueChangeStart(float value) {
                toast("onValueChangeStart(" + listenerSlider.formatProgress(value) + ")");
            }

            @Override
            public void onValueChange(float value) {
                updateLabel(listenerLabel, listenerSlider, value);
            }

            @Override
            public void onValueChangeFinished(float value) {
                updateLabel(listenerLabel, listenerSlider, value);
                toast("onValueChangeFinished(" + listenerSlider.formatProgress(value) + ")");
            }
        });
        content.addView(listenerLabel, labelParams());
        content.addView(listenerSlider, itemParams());

        setContentView(scrollView);
    }

    private MaterialSlider slider(float min, float max, float step, float value) {
        MaterialSlider slider = new MaterialSlider(this);
        slider.setMinValue(min);
        slider.setMaxValue(max);
        slider.setStep(step);
        slider.setValue(value);
        return slider;
    }

    private MaterialSlider.OnValueChangeListener labelListener(final TextView label, final MaterialSlider slider) {
        return new MaterialSlider.OnValueChangeListener() {
            @Override
            public void onValueChangeStart(float value) {
            }

            @Override
            public void onValueChange(float value) {
                updateLabel(label, slider, value);
            }

            @Override
            public void onValueChangeFinished(float value) {
                updateLabel(label, slider, value);
            }
        };
    }

    private void updateLabel(TextView label, MaterialSlider slider, float value) {
        String name = label.getText().toString().split(":")[0].trim();
        label.setText(name + ": " + slider.formatProgress(value));
    }

    private TextView createLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(SchemeHelper.onBackgroundColor());
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return label;
    }

    private TextView caption(String text) {
        TextView captionView = new TextView(this);
        captionView.setText(text);
        captionView.setTextSize(13.0f);
        captionView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        captionView.setTextColor(SchemeHelper.onBackgroundColor());
        return captionView;
    }

    private MaterialButton action(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setStyle(ButtonStyle.OUTLINED);
        button.setOnClickListener(listener);
        return button;
    }

    private int role(DynamicColor color) {
        return color.getArgb(MaterialSlider.publicColorScheme);
    }

    private String hex(int color) {
        return String.format("#%08X", color);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private LinearLayout.LayoutParams labelParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(20.0f);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
