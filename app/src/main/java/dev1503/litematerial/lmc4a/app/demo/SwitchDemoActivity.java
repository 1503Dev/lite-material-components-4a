package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.switches.MaterialSwitch;

public class SwitchDemoActivity extends Activity {

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

        content.addView(createSwitch("Unchecked"), itemParams());

        MaterialSwitch checkedSwitch = createSwitch("Checked");
        checkedSwitch.setChecked(true);
        content.addView(checkedSwitch, itemParams());

        MaterialSwitch disabledUncheckedSwitch = createSwitch("Disabled unchecked");
        disabledUncheckedSwitch.setEnabled(false);
        content.addView(disabledUncheckedSwitch, itemParams());

        MaterialSwitch disabledCheckedSwitch = createSwitch("Disabled checked");
        disabledCheckedSwitch.setChecked(true);
        disabledCheckedSwitch.setEnabled(false);
        content.addView(disabledCheckedSwitch, itemParams());

        MaterialSwitch noLabelSwitch = new MaterialSwitch(this);
        content.addView(noLabelSwitch, itemParams());

        setContentView(scrollView);
    }

    private MaterialSwitch createSwitch(String text) {
        MaterialSwitch materialSwitch = new MaterialSwitch(this);
        materialSwitch.setText(text);
        return materialSwitch;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
