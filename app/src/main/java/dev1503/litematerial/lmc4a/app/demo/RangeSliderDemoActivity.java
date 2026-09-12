package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.slider.MaterialRangeSlider;

public class RangeSliderDemoActivity extends Activity {

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

        TextView label;

        label = createLabel("Default range (0-100): 0 ~ 100");
        MaterialRangeSlider defaultRange = new MaterialRangeSlider(this);
        defaultRange.setOnRangeChangeListener(createListener(label, defaultRange));
        content.addView(label, labelParams());
        content.addView(defaultRange, itemParams());

        label = createLabel("Custom range (0-10, step 0.5): 1.0 ~ 8.0");
        MaterialRangeSlider stepRange = new MaterialRangeSlider(this);
        stepRange.setMin(0f);
        stepRange.setMax(10f);
        stepRange.setStep(0.5f);
        stepRange.setLowValue(1.0f);
        stepRange.setHighValue(8.0f);
        stepRange.setOnRangeChangeListener(createListener(label, stepRange));
        content.addView(label, labelParams());
        content.addView(stepRange, itemParams());

        label = createLabel("Custom range (1-100): 25 ~ 75");
        MaterialRangeSlider customRange = new MaterialRangeSlider(this);
        customRange.setMin(1f);
        customRange.setMax(100f);
        customRange.setLowValue(25f);
        customRange.setHighValue(75f);
        customRange.setOnRangeChangeListener(createListener(label, customRange));
        content.addView(label, labelParams());
        content.addView(customRange, itemParams());

        label = createLabel("Disabled: 20 ~ 80");
        MaterialRangeSlider disabledRange = new MaterialRangeSlider(this);
        disabledRange.setEnabled(false);
        disabledRange.setLowValue(20f);
        disabledRange.setHighValue(80f);
        content.addView(label, labelParams());
        content.addView(disabledRange, itemParams());

        label = createLabel("Step 0.1 (0-1): 0.3 ~ 0.7");
        MaterialRangeSlider preciseRange = new MaterialRangeSlider(this);
        preciseRange.setMin(0f);
        preciseRange.setMax(1f);
        preciseRange.setStep(0.1f);
        preciseRange.setLowValue(0.3f);
        preciseRange.setHighValue(0.7f);
        preciseRange.setOnRangeChangeListener(createListener(label, preciseRange));
        content.addView(label, labelParams());
        content.addView(preciseRange, itemParams());

        setContentView(scrollView);
    }

    private MaterialRangeSlider.OnRangeChangeListener createListener(final TextView label, final MaterialRangeSlider slider) {
        return new MaterialRangeSlider.OnRangeChangeListener() {
            @Override
            public void onRangeChanged(MaterialRangeSlider sliderView, int lowProgress, int highProgress) {
                String name = label.getText().toString().split(":")[0].trim();
                label.setText(name + ": " + slider.formatProgress(slider.getLowValue())
                        + " ~ " + slider.formatProgress(slider.getHighValue()));
            }
        };
    }

    private TextView createLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(SchemeHelper.onBackgroundColor());
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return label;
    }

    private LinearLayout.LayoutParams labelParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
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
