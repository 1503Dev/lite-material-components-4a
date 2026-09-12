package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class ButtonDemoActivity extends Activity {

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

        content.addView(createButton("Filled"), itemParams());

        MaterialButton elevatedButton = createButton("Elevated");
        elevatedButton.setButtonStyle(ButtonStyle.ELEVATED);
        content.addView(elevatedButton, itemParams());

        MaterialButton outlinedButton = createButton("Outlined");
        outlinedButton.setButtonStyle(ButtonStyle.OUTLINED);
        content.addView(outlinedButton, itemParams());

        MaterialButton textButton = createButton("Text");
        textButton.setButtonStyle(ButtonStyle.TEXT);
        content.addView(textButton, itemParams());

        MaterialButton disabledButton = createButton("Filled disabled");
        disabledButton.setEnabled(false);
        content.addView(disabledButton, itemParams());

        MaterialButton corner8dpButton = createButton("Corner 8dp");
        corner8dpButton.setCornerRadiusDp(8.0f);
        content.addView(corner8dpButton, itemParams());

        MaterialButton corner2dpButton = createButton("Corner 2dp");
        corner2dpButton.setCornerRadiusDp(2.0f);
        content.addView(corner2dpButton, itemParams());

        MaterialButton squareCornerButton = createButton("Corner 0dp");
        squareCornerButton.setCornerRadiusDp(0.0f);
        content.addView(squareCornerButton, itemParams());

        MaterialButton pillButton = createButton("Corner 20dp full round");
        pillButton.setCornerRadiusDp(20.0f);
        content.addView(pillButton, itemParams());

        setContentView(scrollView);
    }

    private MaterialButton createButton(String text) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        return button;
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
