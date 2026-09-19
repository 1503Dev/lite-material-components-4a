package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.ColorVariant;
import dev1503.lmc4a.v3.widget.SizeVariant;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.floatingactionbutton.MaterialFloatingActionButton;

public class FloatingActionButtonDemoActivity extends Activity {

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

        content.addView(caption("show() / hide()"), captionParams());
        content.addView(createShowHideRow(), rowParams());

        content.addView(caption("bindTo(ScrollView) / unbindTo(ScrollView)"), captionParams());
        content.addView(createBindRow(scrollView), rowParams());

        for (SizeVariant variant : SizeVariant.values()) {
            content.addView(caption(variant.name() + " — icon only"), captionParams());
            content.addView(createFab(variant, false), fabParams());
            content.addView(caption(variant.name() + " — icon + label"), captionParams());
            content.addView(createFab(variant, true), fabParams());
        }

        content.addView(caption("ColorVariant — SMALL icon only (default PRIMARY_CONTAINER)"), captionParams());
        for (ColorVariant colorVariant : ColorVariant.values()) {
            content.addView(createFabWithVariant(colorVariant), fabParams());
        }

        setContentView(scrollView);
    }

    private LinearLayout createShowHideRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        final MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, false);
        row.addView(fab, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        MaterialButton hideButton = new MaterialButton(this);
        hideButton.setText("Hide");
        hideButton.setOnClickListener(v -> fab.hide());
        row.addView(hideButton, buttonParams());

        MaterialButton showButton = new MaterialButton(this);
        showButton.setText("Show");
        showButton.setOnClickListener(v -> fab.show());
        row.addView(showButton, buttonParams());

        return row;
    }

    private LinearLayout createBindRow(final ScrollView scrollView) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        final MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, false);
        fab.bindTo(scrollView);
        row.addView(fab, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final MaterialButton toggleButton = new MaterialButton(this);
        toggleButton.setText("Unbind");
        toggleButton.setOnClickListener(v -> {
            if (fab.isBound()) {
                fab.unbindTo();
                toggleButton.setText("Bind");
            } else {
                fab.bindTo(scrollView);
                toggleButton.setText("Unbind");
            }
        });
        row.addView(toggleButton, buttonParams());

        return row;
    }

    private MaterialFloatingActionButton createFab(SizeVariant variant, boolean withLabel) {
        MaterialFloatingActionButton fab = new MaterialFloatingActionButton(this);
        fab.setSize(variant);
        fab.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_add));
        if (withLabel) {
            fab.setText("Add");
        }
        return fab;
    }

    private MaterialFloatingActionButton createFabWithVariant(ColorVariant colorVariant) {
        MaterialFloatingActionButton fab = createFab(SizeVariant.SMALL, false);
        fab.setColorVariant(colorVariant);
        return fab;
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
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

    private LinearLayout.LayoutParams rowParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(24.0f);
        return params;
    }

    private LinearLayout.LayoutParams fabParams() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(12.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}