package dev1503.litematerial.lmc4a.component.reference;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FloatingWindowDemoActivity extends AppCompatActivity {

    private MaterialFloatingWindow floatingWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_window_demo);

        findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFloatingWindow();
            }
        });
        findViewById(R.id.btn_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingWindow != null) {
                    floatingWindow.hide();
                }
            }
        });
        findViewById(R.id.btn_toggle_blur).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingWindow == null) {
                    Toast.makeText(FloatingWindowDemoActivity.this,
                            R.string.floating_window_not_shown, Toast.LENGTH_SHORT).show();
                    return;
                }
                floatingWindow.setBlurEnabled(!floatingWindow.isBlurEnabled());
                Toast.makeText(FloatingWindowDemoActivity.this,
                        floatingWindow.isBlurEnabled()
                                ? R.string.floating_window_blur_on
                                : R.string.floating_window_blur_off,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFloatingWindow() {
        if (floatingWindow != null && floatingWindow.isShowing()) {
            return;
        }
        floatingWindow = new MaterialFloatingWindow(this);
        floatingWindow.setBlurRadiusDp(32.0f);
        floatingWindow.setCornerRadiusDp(28.0f);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(20.0f);
        content.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText(R.string.floating_window_title);
        title.setTextSize(16.0f);
        content.addView(title);

        TextView body = new TextView(this);
        body.setText(R.string.floating_window_body);
        body.setTextSize(14.0f);
        content.addView(body);

        Button dragHint = new Button(this);
        dragHint.setText(R.string.floating_window_drag_hint);
        dragHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingWindowDemoActivity.this,
                        R.string.floating_window_clicked, Toast.LENGTH_SHORT).show();
            }
        });
        content.addView(dragHint);

        floatingWindow.getContentView().addView(content, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        floatingWindow.show();
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
