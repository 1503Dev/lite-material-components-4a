package dev1503.litematerial.lmc4a.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        int padding = dp(24.0f);
        root.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Lite Material Components 4A");
        title.setTextSize(20.0f);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(SchemeHelper.onBackgroundColor());
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        MaterialButton v3Button = new MaterialButton(this);
        v3Button.setText("Material 3");
        v3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, V3Activity.class));
            }
        });
        LinearLayout.LayoutParams v3Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v3Params.topMargin = dp(24.0f);
        root.addView(v3Button, v3Params);

        MaterialButton v3eButton = new dev1503.lmc4a.v3e.widget.button.MaterialButton(this);
        v3eButton.setText("Material 3 Expressive");
        v3eButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, V3EActivity.class));
            }
        });
        LinearLayout.LayoutParams v3eParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v3eParams.topMargin = dp(12.0f);
        root.addView(v3eButton, v3eParams);

        setContentView(root);
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
