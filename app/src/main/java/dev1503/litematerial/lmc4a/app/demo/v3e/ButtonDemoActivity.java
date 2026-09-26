package dev1503.litematerial.lmc4a.app.demo.v3e;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3e.widget.button.MaterialButton;
import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.litematerial.lmc4a.app.demo.DemoActivity;

public class ButtonDemoActivity extends DemoActivity {

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

        pressSection(content);
        baseRadiusSection(content);

        setContentView(scrollView);
    }

    private void pressSection(LinearLayout content) {
        MaterialButton filledButton = createButton("Filled");
        content.addView(filledButton, itemParams());

        MaterialButton elevatedButton = createButton("Elevated");
        elevatedButton.setStyle(ButtonStyle.ELEVATED);
        content.addView(elevatedButton, itemParams());

        MaterialButton outlinedButton = createButton("Outlined");
        outlinedButton.setStyle(ButtonStyle.OUTLINED);
        content.addView(outlinedButton, itemParams());

        MaterialButton textButton = createButton("Text");
        textButton.setStyle(ButtonStyle.TEXT);
        content.addView(textButton, itemParams());

        MaterialButton iconButton = createButton("Save");
        iconButton.setIcon(new Icon(android.R.drawable.ic_menu_save));
        content.addView(iconButton, itemParams());

        MaterialButton iconOnlyButton = createButton("");
        iconOnlyButton.setStyle(ButtonStyle.ICON);
        iconOnlyButton.setIcon(new Icon(android.R.drawable.ic_menu_add));
        content.addView(iconOnlyButton, itemParams());
    }

    private void baseRadiusSection(LinearLayout content) {
        content.addView(caption("setPressedCornerRadiusDp(float)"),
                captionParams());

        final MaterialButton radiusButton = createButton("Press Me");
        content.addView(radiusButton, itemParams());
    }
    private MaterialButton createButton(String text) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        return button;
    }

    private TextView caption(String text) {
        TextView captionView = new TextView(this);
        captionView.setText(text);
        captionView.setTextSize(13.0f);
        captionView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        captionView.setTextColor(SchemeHelper.onBackgroundColor());
        return captionView;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(8.0f);
        params.bottomMargin = dp(8.0f);
        return params;
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
