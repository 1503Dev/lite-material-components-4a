package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.radiobutton.MaterialRadioButton;

public class RadioButtonDemoActivity extends Activity {

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

        content.addView(createLabel("Grouped (RadioGroup)"), labelParams());

        RadioGroup group = new RadioGroup(this);
        MaterialRadioButton radio1 = new MaterialRadioButton(this);
        radio1.setText("Option A");
        radio1.setId(1);
        MaterialRadioButton radio2 = new MaterialRadioButton(this);
        radio2.setText("Option B");
        radio2.setId(2);
        MaterialRadioButton radio3 = new MaterialRadioButton(this);
        radio3.setText("Option C");
        radio3.setId(3);
        radio1.setChecked(true);
        group.addView(radio1, itemParams());
        group.addView(radio2, itemParams());
        group.addView(radio3, itemParams());
        content.addView(group, itemParams());

        content.addView(createLabel("Independent"), labelParams());

        final TextView status = new TextView(this);
        status.setTextColor(SchemeHelper.onBackgroundColor());
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        content.addView(status, labelParams());

        final MaterialRadioButton independent1 = new MaterialRadioButton(this);
        independent1.setText("Independent 1");
        independent1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                status.setText("Independent 1: " + isChecked);
            }
        });
        content.addView(independent1, itemParams());

        MaterialRadioButton independent2 = new MaterialRadioButton(this);
        independent2.setText("Independent 2");
        independent2.setChecked(true);
        content.addView(independent2, itemParams());

        content.addView(createLabel("Disabled"), labelParams());

        MaterialRadioButton disabledUnchecked = new MaterialRadioButton(this);
        disabledUnchecked.setText("Disabled unchecked");
        disabledUnchecked.setEnabled(false);
        content.addView(disabledUnchecked, itemParams());

        MaterialRadioButton disabledChecked = new MaterialRadioButton(this);
        disabledChecked.setText("Disabled checked");
        disabledChecked.setChecked(true);
        disabledChecked.setEnabled(false);
        content.addView(disabledChecked, itemParams());

        setContentView(scrollView);
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