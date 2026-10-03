package dev1503.litematerial.lmc4a.app.demo.v3e;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3e.widget.slider.MaterialSlider;
import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.litematerial.lmc4a.app.demo.DemoActivity;

public class SliderDemoActivity extends DemoActivity {

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

        defaultSection(content);
        sizeSection(content);
        colorSection(content);
        disabledSection(content);
        listenerSection(content);

        setContentView(scrollView);
    }

    private void defaultSection(LinearLayout content) {
        content.addView(caption("默认"), captionParams());

        final TextView label = createLabel("0-100: 0");
        MaterialSlider slider = slider(0f, 100f, 0f, 0f);
        slider.setOnValueChangeListener(labelListener(label, slider));
        content.addView(label, labelParams());
        content.addView(slider, itemParams());

        MaterialSlider steppedSlider = slider(0f, 10f, 0.5f, 5f);
        content.addView(steppedSlider, itemParams());
    }

    private void sizeSection(LinearLayout content) {
        content.addView(caption("setHandleWidthDp(float) / setHandleHeightDp(float) / "
                + "setTrackHeightDp(float) / setTrackGapDp(float)"), captionParams());

        final MaterialSlider slider = slider(0f, 100f, 0f, 40f);
        slider.setHandleWidthDp(8f);
        slider.setHandleHeightDp(32f);
        slider.setTrackHeightDp(24f);
        slider.setTrackGapDp(12f);
        content.addView(slider, itemParams());
        content.addView(action("clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.clearHandleWidthDp();
                slider.clearHandleHeightDp();
                slider.clearTrackHeightDp();
                slider.clearTrackGapDp();
            }
        }), itemParams());
    }

    private void colorSection(LinearLayout content) {
        content.addView(caption("setThumbColor(int) / setActiveTrackColor(int) / "
                + "setValueIndicatorColor(int)"), captionParams());

        final MaterialSlider slider = slider(0f, 100f, 0f, 60f);
        slider.setThumbColor(android.graphics.Color.rgb(128, 0, 128));
        slider.setActiveTrackColor(android.graphics.Color.RED);
        slider.setValueIndicatorColor(android.graphics.Color.rgb(0, 128, 128));
        content.addView(slider, itemParams());
        content.addView(action("clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.clearThumbColor();
                slider.clearActiveTrackColor();
                slider.clearValueIndicatorColor();
            }
        }), itemParams());
    }

    private void disabledSection(LinearLayout content) {
        content.addView(caption("setDisabledTrackColor(int) / setDisabledThumbColor(int)"),
                captionParams());

        MaterialSlider slider = slider(0f, 100f, 0f, 40f);
        slider.setDisabledTrackColor(android.graphics.Color.rgb(255, 180, 200));
        slider.setDisabledThumbColor(android.graphics.Color.RED);
        slider.setEnabled(false);
        content.addView(slider, itemParams());

        MaterialSlider plainDisabled = slider(0f, 100f, 0f, 70f);
        plainDisabled.setEnabled(false);
        content.addView(plainDisabled, itemParams());
    }

    private void listenerSection(LinearLayout content) {
        content.addView(caption("setOnValueChangeListener(OnValueChangeListener)"),
                captionParams());

        final TextView label = createLabel("0-10: 0.0");
        final MaterialSlider slider = slider(0f, 10f, 0.5f, 0f);
        slider.setOnValueChangeListener(new MaterialSlider.OnValueChangeListener() {
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
        });
        content.addView(label, labelParams());
        content.addView(slider, itemParams());
    }

    private MaterialSlider slider(float min, float max, float step, float value) {
        MaterialSlider slider = new MaterialSlider(this);
        slider.setMinValue(min);
        slider.setMaxValue(max);
        slider.setStep(step);
        slider.setValue(value);
        return slider;
    }

    private MaterialSlider.OnValueChangeListener labelListener(final TextView label,
                                                              final MaterialSlider slider) {
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
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
