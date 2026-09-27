package dev1503.litematerial.lmc4a.app.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.cardview.MaterialCardView;
import dev1503.lmc4a.v3.widget.collapse.CollapseAlign;
import dev1503.lmc4a.v3.widget.collapse.MaterialCollapse;

public class CollapseDemoActivity extends DemoActivity {

    private MaterialCollapse overridePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        int padding = dp(24.0f);
        content.setPadding(padding, padding, padding, padding);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("默认（align=START）"), captionParams());
        content.addView(card(createPanel(true)), cardParams());

        content.addView(caption("setAlign(CollapseAlign.END)"), captionParams());
        content.addView(card(createPanel(false)), cardParams());

        content.addView(caption("代码控制"), captionParams());
        final MaterialCollapse controlPanel = createPanel(true);
        content.addView(card(controlPanel), cardParams());
        content.addView(action("toggle()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlPanel.toggle();
                toast("expanded=" + controlPanel.isExpanded());
            }
        }), buttonParams());
        content.addView(action("expand() / collapse()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controlPanel.isExpanded()) {
                    controlPanel.collapse();
                } else {
                    controlPanel.expand();
                }
                toast("expanded=" + controlPanel.isExpanded());
            }
        }), buttonParams());
        content.addView(action("setAnimate(false)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlPanel.setAnimate(!controlPanel.isAnimate());
                toast("animate=" + controlPanel.isAnimate());
            }
        }), buttonParams());

        content.addView(caption("读取与监听"), captionParams());
        overridePanel = createPanel(true);
        overridePanel.setOnExpandChangeListener(new MaterialCollapse.OnExpandChangeListener() {
            @Override
            public void onExpandChange(boolean expanded) {
                toast("onExpandChange: " + expanded);
            }
        });
        content.addView(card(overridePanel), cardParams());
        content.addView(action("get / has", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("align=" + overridePanel.getAlign()
                        + " expanded=" + overridePanel.isExpanded()
                        + " animate=" + overridePanel.isAnimate());
            }
        }), buttonParams());
        content.addView(action("setAlign(START) ↔ setAlign(END)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollapseAlign next = overridePanel.getAlign() == CollapseAlign.END
                        ? CollapseAlign.START : CollapseAlign.END;
                overridePanel.setAlign(next);
                toast("align=" + overridePanel.getAlign());
            }
        }), buttonParams());

        setContentView(scrollView);
    }

    private MaterialCardView card(MaterialCollapse panel) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        TextView trigger = new TextView(this);
        trigger.setText("点此展开 / 收起");
        trigger.setTextSize(16.0f);
        trigger.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        trigger.setTextColor(SchemeHelper.onBackgroundColor());
        trigger.setPadding(dp(16.0f), dp(16.0f), dp(16.0f), dp(8.0f));
        panel.bindTo(trigger);
        wrap.addView(trigger, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int inner = dp(16.0f);
        panel.setPadding(inner, 0, inner, dp(8.0f));
        wrap.addView(panel, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        MaterialCardView card = new MaterialCardView(this);
        card.addView(wrap, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private MaterialCollapse createPanel(boolean startAlign) {
        MaterialCollapse panel = new MaterialCollapse(this);
        panel.setAlign(startAlign ? CollapseAlign.START : CollapseAlign.END);
        panel.addView(body("这是面板内容"), bodyParams());
        panel.addView(body("由外部触发展开 / 收起"), bodyParams());
        return panel;
    }

    private TextView body(String text) {
        TextView bodyView = new TextView(this);
        bodyView.setText(text);
        bodyView.setTextSize(14.0f);
        bodyView.setTextColor(SchemeHelper.onBackgroundColor());
        return bodyView;
    }

    private MaterialButton action(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setStyle(ButtonStyle.OUTLINED);
        button.setOnClickListener(listener);
        return button;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        caption.setTextColor(SchemeHelper.onBackgroundColor());
        return caption;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams cardParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(24.0f);
        return params;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams bodyParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(4.0f), 0, dp(4.0f));
        return params;
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
