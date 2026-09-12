package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.slider.MaterialSlider;

public class SliderDemoActivity extends Activity {

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

        label = createLabel("Default (0-100): 0");
        MaterialSlider defaultSlider = new MaterialSlider(this);
        defaultSlider.setOnSeekBarChangeListener(createListener(label, defaultSlider));
        content.addView(label, labelParams());
        content.addView(defaultSlider, itemParams());

        label = createLabel("Custom range (0-10, step 0.5): 5.0");
        MaterialSlider stepSlider = new MaterialSlider(this);
        stepSlider.setMin(0f);
        stepSlider.setMax(10);
        stepSlider.setStep(0.5f);
        stepSlider.setProgressValue(5);
        stepSlider.setOnSeekBarChangeListener(createListener(label, stepSlider));
        content.addView(label, labelParams());
        content.addView(stepSlider, itemParams());

        label = createLabel("Custom range (1-100, step 1): 25");
        MaterialSlider rangeSlider = new MaterialSlider(this);
        rangeSlider.setMin(1f);
        rangeSlider.setMax(100);
        rangeSlider.setStep(1);
        rangeSlider.setProgressValue(25);
        rangeSlider.setOnSeekBarChangeListener(createListener(label, rangeSlider));
        content.addView(label, labelParams());
        content.addView(rangeSlider, itemParams());

        label = createLabel("Step 0.1 (0-1): 0.3");
        MaterialSlider preciseSlider = new MaterialSlider(this);
        preciseSlider.setMin(0f);
        preciseSlider.setMax(1);
        preciseSlider.setStep(0.1f);
        preciseSlider.setProgressValue(0.3f);
        preciseSlider.setOnSeekBarChangeListener(createListener(label, preciseSlider));
        content.addView(label, labelParams());
        content.addView(preciseSlider, itemParams());

        label = createLabel("Disabled: 50");
        MaterialSlider disabledSlider = new MaterialSlider(this);
        disabledSlider.setEnabled(false);
        disabledSlider.setProgressValue(50);
        content.addView(label, labelParams());
        content.addView(disabledSlider, itemParams());

        label = createLabel("Disabled with step: 2.50");
        MaterialSlider disabledStepSlider = new MaterialSlider(this);
        disabledStepSlider.setStep(0.25f);
        disabledStepSlider.setMax(5);
        disabledStepSlider.setProgressValue(2.5f);
        disabledStepSlider.setEnabled(false);
        content.addView(label, labelParams());
        content.addView(disabledStepSlider, itemParams());

        setContentView(scrollView);
    }

    private SeekBar.OnSeekBarChangeListener createListener(final TextView label, final MaterialSlider slider) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String name = label.getText().toString().split(":")[0].trim();
                label.setText(name + ": " + slider.formatProgress(slider.getProgressValue()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
