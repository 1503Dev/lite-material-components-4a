package dev1503.litematerial.lmc4a.app.demo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.progressindicator.MaterialLinearProgressIndicator;

public class ProgressIndicatorDemoActivity extends DemoActivity {

    private MaterialLinearProgressIndicator determinate;
    private TextView progressLabel;

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

        content.addView(createLabel("确定进度"), labelParams());

        determinate = createBar(false, 40);
        content.addView(determinate, barParams());

        progressLabel = createLabel("");
        content.addView(progressLabel, labelParams());
        updateProgressLabel();

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(actionButton("-10", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyProgress(determinate.getProgress() - 10);
            }
        }), actionParams());
        row.addView(actionButton("+10", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyProgress(determinate.getProgress() + 10);
            }
        }), actionParams());
        row.addView(actionButton("0", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyProgress(0);
            }
        }), actionParams());
        row.addView(actionButton("100", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyProgress(100);
            }
        }), actionParams());
        row.addView(actionButton("不确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determinate.setIndeterminate(!determinate.isIndeterminate());
            }
        }), actionParams());
        content.addView(row, barParams());

        content.addView(createLabel("不确定"), labelParams());
        content.addView(createBar(true, 0), barParams());

        content.addView(createLabel("自定义颜色与厚度"), labelParams());
        MaterialLinearProgressIndicator custom = createBar(false, 70);
        custom.setTrackThicknessDp(8.0f);
        custom.setActiveIndicatorColor(Lmc.publicColorScheme.getTertiary());
        custom.setTrackColor(Lmc.publicColorScheme.getTertiaryContainer());
        content.addView(custom, barParams());

        final MaterialLinearProgressIndicator plain = createBar(true, 0);
        plain.setTrackThicknessDp(6.0f);
        content.addView(plain, barParams());
        LinearLayout visibilityRow = new LinearLayout(this);
        visibilityRow.setOrientation(LinearLayout.HORIZONTAL);
        visibilityRow.addView(actionButton("show()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plain.show();
            }
        }), actionParams());
        visibilityRow.addView(actionButton("hide()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plain.hide();
            }
        }), actionParams());
        content.addView(visibilityRow, barParams());

        setContentView(scrollView);
    }

    private void applyProgress(int progress) {
        determinate.setIndeterminate(false);
        determinate.setProgress(Math.max(0, Math.min(100, progress)));
        updateProgressLabel();
    }

    private void updateProgressLabel() {
        progressLabel.setText("progress = " + determinate.getProgress() + " / "
                + determinate.getMax());
    }

    private MaterialLinearProgressIndicator createBar(boolean indeterminate, int progress) {
        MaterialLinearProgressIndicator bar = new MaterialLinearProgressIndicator(this);
        bar.setMax(100);
        bar.setProgress(progress);
        bar.setIndeterminate(indeterminate);
        return bar;
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

    private LinearLayout.LayoutParams barParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12.0f);
        return params;
    }

    private LinearLayout.LayoutParams actionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = dp(4.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
