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
import dev1503.lmc4a.v3.widget.slider.MaterialRangeSlider;

public class RangeSliderDemoActivity extends Activity {

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

        content.addView(caption("默认 — setMinValue 默认 0、setMaxValue 默认 100，两个滑块初始 0 ~ 100"
                + "（setLowValue 设下限 / setHighValue 设上限，旧 setLowProgress/setHighProgress 已删除）；"
                + "横向拖动超过 touch slop 后会接管手势（requestDisallowInterceptTouchEvent），"
                + "在 ScrollView 中拖动不会再被抢走，竖直滑动仍可正常滚动页面。"), captionParams());
        final TextView defaultLabel = createLabel("默认 (0-100): 0 ~ 100");
        MaterialRangeSlider defaultRange = new MaterialRangeSlider(this);
        defaultRange.setOnRangeChangeListener(labelListener(defaultLabel));
        content.addView(defaultLabel, labelParams());
        content.addView(defaultRange, itemParams());

        content.addView(caption("setMinValue(0f) / setMaxValue(10f) / setStep(0.5f) / setLowValue(1.0f) / "
                + "setHighValue(8.0f) — 浮点上下限请用 setMinValue/setMaxValue（旧 setMin/setMax(float) 已删除）"), captionParams());
        final TextView stepLabel = createLabel("步长 0.5 (0-10): 1.0 ~ 8.0");
        MaterialRangeSlider stepRange = range(0f, 10f, 0.5f, 1f, 8f);
        stepRange.setOnRangeChangeListener(labelListener(stepLabel));
        content.addView(stepLabel, labelParams());
        content.addView(stepRange, itemParams());

        content.addView(caption("setMinValue(1f) / setMaxValue(100f)（连续取值）/ setLowValue(25f) / setHighValue(75f)"), captionParams());
        final TextView customLabel = createLabel("连续 (1-100): 25 ~ 75");
        MaterialRangeSlider customRange = range(1f, 100f, 0f, 25f, 75f);
        customRange.setOnRangeChangeListener(labelListener(customLabel));
        content.addView(customLabel, labelParams());
        content.addView(customRange, itemParams());

        content.addView(caption("getValue() / setValue(float) — 区间中点；setValue(50f) 会把上下限同时收到 50，"
                + "getValue() 返回 (getLowValue() + getHighValue()) / 2"), captionParams());
        final TextView midLabel = createLabel("中点 (0-100): 50.0");
        final MaterialRangeSlider midRange = range(0f, 100f, 0f, 20f, 80f);
        midRange.setOnRangeChangeListener(labelListener(midLabel));
        content.addView(midLabel, labelParams());
        content.addView(midRange, itemParams());
        content.addView(action("setValue(50f) → 上下限同时收到 50，读取 getValue()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midRange.setValue(50f);
                updateLabel(midLabel, midRange);
                toast("getValue()=" + midRange.formatProgress(midRange.getValue()) + "（中点），low="
                        + midRange.formatProgress(midRange.getLowValue()) + " high="
                        + midRange.formatProgress(midRange.getHighValue()));
            }
        }), itemParams());
        content.addView(action("setValue(80f) → 上下限同时到 80 后再 setLowValue(30f)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midRange.setValue(80f);
                midRange.setLowValue(30f);
                updateLabel(midLabel, midRange);
                toast("setValue(80f) 后 setLowValue(30f)：low=" + midRange.formatProgress(midRange.getLowValue())
                        + " high=" + midRange.formatProgress(midRange.getHighValue())
                        + "，中点 getValue()=" + midRange.formatProgress(midRange.getValue()));
            }
        }), itemParams());

        content.addView(caption("setOnRangeChangeListener — onRangeChanged(slider, lowProgress, highProgress) 的两个数字参数"
                + "是内部刻度（不是真实值），真实值用 getLowValue() / getHighValue() 读取"), captionParams());
        final TextView listenerLabel = createLabel("监听器 (0-100): 10 ~ 90");
        final MaterialRangeSlider listenerRange = range(0f, 100f, 0f, 10f, 90f);
        listenerRange.setOnRangeChangeListener(new MaterialRangeSlider.OnRangeChangeListener() {
            @Override
            public void onRangeChanged(MaterialRangeSlider slider, int lowProgress, int highProgress) {
                updateLabel(listenerLabel, slider);
                toast("onRangeChanged(lowProgress=" + lowProgress + ", highProgress=" + highProgress
                        + ") → 真实值 " + slider.formatProgress(slider.getLowValue())
                        + " ~ " + slider.formatProgress(slider.getHighValue()));
            }
        });
        content.addView(listenerLabel, labelParams());
        content.addView(listenerRange, itemParams());
        content.addView(action("getOnRangeChangeListener() — 读取当前监听器", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object listener = listenerRange.getOnRangeChangeListener();
                toast("getOnRangeChangeListener() " + (listener == null ? "== null（未设置）" : "!= null（已设置）"));
            }
        }), itemParams());

        content.addView(caption("禁用态 — 区间轨道用角色色 outlineVariant 与 outline 的 50% 混合色，"
                + "拇指默认跟随禁用轨道色（可用 setDisabledTrackColor / setDisabledThumbColor 覆盖）"), captionParams());
        final TextView disabledLabel = createLabel("禁用 (0-100): 20 ~ 80");
        MaterialRangeSlider disabledRange = range(0f, 100f, 0f, 20f, 80f);
        disabledRange.setEnabled(false);
        content.addView(disabledLabel, labelParams());
        content.addView(disabledRange, itemParams());

        content.addView(caption("颜色覆盖（继承 MaterialSlider）— setThumbColor(tertiary)、setActiveTrackColor(error)、"
                + "setInactiveTrackColor(secondaryContainer)、setValueIndicatorColor(onTertiaryContainer)；"
                + "默认分别是 primary / primary / surfaceContainerHighest / primary"), captionParams());
        final MaterialRangeSlider colorRange = range(0f, 100f, 0f, 25f, 75f);
        colorRange.setThumbColor(role(dynamicColors.tertiary()));
        colorRange.setActiveTrackColor(role(dynamicColors.error()));
        colorRange.setInactiveTrackColor(role(dynamicColors.secondaryContainer()));
        colorRange.setValueIndicatorColor(role(dynamicColors.onTertiaryContainer()));
        content.addView(colorRange, itemParams());
        content.addView(action("读取颜色 getThumbColor()/getActiveTrackColor()/getInactiveTrackColor()/getValueIndicatorColor()",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("thumb=" + hex(colorRange.getThumbColor())
                                + " active=" + hex(colorRange.getActiveTrackColor())
                                + " inactive=" + hex(colorRange.getInactiveTrackColor())
                                + " indicator=" + hex(colorRange.getValueIndicatorColor()));
                    }
                }), itemParams());
        content.addView(action("clearThumbColor() + clearActiveTrackColor() → 回到 primary 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int beforeThumb = colorRange.getThumbColor();
                int beforeActive = colorRange.getActiveTrackColor();
                colorRange.clearThumbColor();
                colorRange.clearActiveTrackColor();
                toast("thumb: " + hex(beforeThumb) + " → " + hex(colorRange.getThumbColor())
                        + "（primary），active: " + hex(beforeActive) + " → "
                        + hex(colorRange.getActiveTrackColor()) + "（primary）");
            }
        }), itemParams());
        content.addView(action("clearInactiveTrackColor() + clearValueIndicatorColor() → 回到角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int beforeInactive = colorRange.getInactiveTrackColor();
                int beforeIndicator = colorRange.getValueIndicatorColor();
                colorRange.clearInactiveTrackColor();
                colorRange.clearValueIndicatorColor();
                toast("inactive: " + hex(beforeInactive) + " → " + hex(colorRange.getInactiveTrackColor())
                        + "（surfaceContainerHighest），indicator: " + hex(beforeIndicator) + " → "
                        + hex(colorRange.getValueIndicatorColor()) + "（primary）");
            }
        }), itemParams());
        content.addView(caption("禁用态颜色覆盖 — setDisabledTrackColor(tertiaryContainer) / setDisabledThumbColor(error)"), captionParams());
        final MaterialRangeSlider disabledColorRange = range(0f, 100f, 0f, 30f, 70f);
        disabledColorRange.setDisabledTrackColor(role(dynamicColors.tertiaryContainer()));
        disabledColorRange.setDisabledThumbColor(role(dynamicColors.error()));
        disabledColorRange.setEnabled(false);
        content.addView(disabledColorRange, itemParams());
        content.addView(action("clearDisabledTrackColor() + clearDisabledThumbColor() → 回到混合色 / 跟随轨道", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int beforeTrack = disabledColorRange.getDisabledTrackColor();
                int beforeThumb = disabledColorRange.getDisabledThumbColor();
                disabledColorRange.clearDisabledTrackColor();
                disabledColorRange.clearDisabledThumbColor();
                toast("disabledTrack: " + hex(beforeTrack) + " → " + hex(disabledColorRange.getDisabledTrackColor())
                        + "（outlineVariant+outline 混合），disabledThumb: " + hex(beforeThumb) + " → "
                        + hex(disabledColorRange.getDisabledThumbColor()) + "（跟随禁用轨道色）");
            }
        }), itemParams());
        content.addView(action("setColorScheme(null) → 一次性清除全部颜色覆盖", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorRange.setColorScheme(null);
                disabledColorRange.setColorScheme(null);
                toast("setColorScheme(null): 全部颜色覆盖已清除，thumb=" + hex(colorRange.getThumbColor())
                        + " active=" + hex(colorRange.getActiveTrackColor()));
            }
        }), itemParams());

        content.addView(caption("尺寸（继承 MaterialSlider）— setThumbRadiusDp(16f)（默认 10dp）、"
                + "setTrackHeightDp(12f)（默认 4dp）；getTrackWidthDp() 只读，无 setter / clear"), captionParams());
        final MaterialRangeSlider sizeRange = range(0f, 100f, 0f, 30f, 70f);
        sizeRange.setThumbRadiusDp(16f);
        sizeRange.setTrackHeightDp(12f);
        content.addView(sizeRange, itemParams());
        content.addView(action("读取尺寸 getThumbRadiusDp()/getTrackHeightDp()/getTrackWidthDp()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("thumbRadius=" + sizeRange.getThumbRadiusDp() + "dp trackHeight="
                        + sizeRange.getTrackHeightDp() + "dp trackWidth=" + sizeRange.getTrackWidthDp() + "dp（只读）");
            }
        }), itemParams());
        content.addView(action("clearThumbRadiusDp() + clearTrackHeightDp() → 回到 10dp / 4dp", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float beforeRadius = sizeRange.getThumbRadiusDp();
                float beforeHeight = sizeRange.getTrackHeightDp();
                sizeRange.clearThumbRadiusDp();
                sizeRange.clearTrackHeightDp();
                toast("thumbRadius: " + beforeRadius + "dp → " + sizeRange.getThumbRadiusDp()
                        + "dp（默认 10dp），trackHeight: " + beforeHeight + "dp → "
                        + sizeRange.getTrackHeightDp() + "dp（默认 4dp）");
            }
        }), itemParams());

        content.addView(caption("气泡开关 — setValueIndicatorEnabled(false) 后两个滑块按下与拖动都不再弹出气泡（默认 true）"), captionParams());
        final MaterialRangeSlider noIndicatorRange = range(0f, 100f, 0f, 40f, 60f);
        noIndicatorRange.setValueIndicatorEnabled(false);
        content.addView(noIndicatorRange, itemParams());
        content.addView(action("切换 isValueIndicatorEnabled() / 恢复气泡", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enable = !noIndicatorRange.isValueIndicatorEnabled();
                noIndicatorRange.setValueIndicatorEnabled(enable);
                toast("setValueIndicatorEnabled(" + enable + ") → isValueIndicatorEnabled()="
                        + noIndicatorRange.isValueIndicatorEnabled());
            }
        }), itemParams());

        setContentView(scrollView);
    }

    private MaterialRangeSlider range(float min, float max, float step, float low, float high) {
        MaterialRangeSlider range = new MaterialRangeSlider(this);
        range.setMinValue(min);
        range.setMaxValue(max);
        range.setStep(step);
        range.setLowValue(low);
        range.setHighValue(high);
        return range;
    }

    private MaterialRangeSlider.OnRangeChangeListener labelListener(final TextView label) {
        return new MaterialRangeSlider.OnRangeChangeListener() {
            @Override
            public void onRangeChanged(MaterialRangeSlider slider, int lowProgress, int highProgress) {
                updateLabel(label, slider);
            }
        };
    }

    private void updateLabel(TextView label, MaterialRangeSlider slider) {
        String name = label.getText().toString().split(":")[0].trim();
        label.setText(name + ": " + slider.formatProgress(slider.getLowValue())
                + " ~ " + slider.formatProgress(slider.getHighValue()));
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
        return color.getArgb(MaterialRangeSlider.publicColorScheme);
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
