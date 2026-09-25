package dev1503.litematerial.lmc4a.app.demo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.checkbox.MaterialCheckBox;

public class CheckBoxDemoActivity extends DemoActivity {

    private boolean syncing;

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

        content.addView(createLabel("默认"), labelParams());
        content.addView(createCheckBox("未选中", false, true), itemParams());
        content.addView(createCheckBox("已选中", true, true), itemParams());
        content.addView(createCheckBox("禁用（未选中）", false, false), itemParams());
        content.addView(createCheckBox("禁用（已选中）", true, false), itemParams());

        content.addView(createLabel("不确定态"), labelParams());
        final MaterialCheckBox indeterminate = createCheckBox("部分选中", false, true);
        indeterminate.setIndeterminate(true);
        content.addView(indeterminate, itemParams());
        LinearLayout indeterminateRow = new LinearLayout(this);
        indeterminateRow.setOrientation(LinearLayout.HORIZONTAL);
        indeterminateRow.addView(actionButton("setIndeterminate(true)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indeterminate.setIndeterminate(true);
            }
        }), actionParams());
        indeterminateRow.addView(actionButton("setChecked(true)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indeterminate.setChecked(true);
            }
        }), actionParams());
        indeterminateRow.addView(actionButton("setChecked(false)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indeterminate.setChecked(false);
            }
        }), actionParams());
        content.addView(indeterminateRow, itemParams());

        content.addView(createLabel("全选"), labelParams());
        final MaterialCheckBox optionA = createCheckBox("选项 A", true, true);
        final MaterialCheckBox optionB = createCheckBox("选项 B", false, true);
        final MaterialCheckBox optionC = createCheckBox("选项 C", true, true);
        final MaterialCheckBox selectAll = createCheckBox("全选", false, true);
        final TextView summary = new TextView(this);
        summary.setTextColor(SchemeHelper.onBackgroundColor());
        summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        CompoundButton.OnCheckedChangeListener optionListener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (syncing) {
                            return;
                        }
                        updateSummary(summary, optionA, optionB, optionC, selectAll);
                    }
                };
        optionA.setOnCheckedChangeListener(optionListener);
        optionB.setOnCheckedChangeListener(optionListener);
        optionC.setOnCheckedChangeListener(optionListener);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (syncing) {
                    return;
                }
                syncing = true;
                optionA.setChecked(isChecked);
                optionB.setChecked(isChecked);
                optionC.setChecked(isChecked);
                syncing = false;
                updateSummary(summary, optionA, optionB, optionC, selectAll);
            }
        });

        content.addView(optionA, itemParams());
        content.addView(optionB, itemParams());
        content.addView(optionC, itemParams());
        content.addView(selectAll, itemParams());
        content.addView(summary, itemParams());
        updateSummary(summary, optionA, optionB, optionC, selectAll);

        setContentView(scrollView);
    }

    private void updateSummary(TextView summary, MaterialCheckBox optionA,
            MaterialCheckBox optionB, MaterialCheckBox optionC, MaterialCheckBox selectAll) {
        int count = 0;
        if (optionA.isChecked()) {
            count++;
        }
        if (optionB.isChecked()) {
            count++;
        }
        if (optionC.isChecked()) {
            count++;
        }
        summary.setText("已选 " + count + " / 3");

        syncing = true;
        if (count == 3) {
            selectAll.setIndeterminate(false);
            selectAll.setChecked(true);
        } else if (count == 0) {
            selectAll.setIndeterminate(false);
            selectAll.setChecked(false);
        } else {
            selectAll.setChecked(false);
            selectAll.setIndeterminate(true);
        }
        syncing = false;
    }

    private MaterialCheckBox createCheckBox(String text, boolean checked, boolean enabled) {
        MaterialCheckBox checkBox = new MaterialCheckBox(this);
        checkBox.setText(text);
        checkBox.setChecked(checked);
        checkBox.setEnabled(enabled);
        return checkBox;
    }

    private MaterialButton actionButton(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setStyle(ButtonStyle.TEXT);
        button.setText(text);
        button.setTextSize(13.0f);
        button.setOnClickListener(listener);
        return button;
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
        params.topMargin = dp(12.0f);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private LinearLayout.LayoutParams actionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = dp(8.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
