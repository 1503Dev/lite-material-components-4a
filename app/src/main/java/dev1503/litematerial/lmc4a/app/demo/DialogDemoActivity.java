package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.dialog.MaterialDialogBuilder;

public class DialogDemoActivity extends Activity {

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

        content.addView(createButton("Basic Dialog"), itemParams());
        content.addView(createSpacer(), spacerParams());

        content.addView(createButton("Confirm Dialog"), itemParams());
        content.addView(createSpacer(), spacerParams());

        content.addView(createButton("Three Buttons Dialog"), itemParams());
        content.addView(createSpacer(), spacerParams());

        content.addView(createButton("Custom View Dialog"), itemParams());

        setContentView(scrollView);
    }

    private MaterialButton createButton(String text) {
        MaterialButton btn = new MaterialButton(this);
        btn.setText(text);
        btn.setOnClickListener(clickListener);
        return btn;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = ((TextView) v).getText().toString();
            switch (text) {
                case "Basic Dialog":
                    showBasicDialog();
                    break;
                case "Confirm Dialog":
                    showConfirmDialog();
                    break;
                case "Three Buttons Dialog":
                    showThreeButtonsDialog();
                    break;
                case "Custom View Dialog":
                    showCustomViewDialog();
                    break;
            }
        }
    };

    private void showBasicDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Basic Dialog")
                .setMessage("This is a basic Material 3 dialog with spring animation.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showConfirmDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showThreeButtonsDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Choose Option")
                .setMessage("Please select one of the following options.")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Maybe", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showCustomViewDialog() {
        LinearLayout customView = new LinearLayout(this);
        customView.setOrientation(LinearLayout.VERTICAL);
        customView.setPadding(dp(24), dp(16), dp(24), dp(8));

        TextView label = new TextView(this);
        label.setText("Enter your name:");
        label.setTextSize(14);
        label.setTextColor(getResources().getColor(android.R.color.white));
        customView.addView(label, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        new MaterialDialogBuilder(this)
                .setTitle("Custom View")
                .setView(customView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private View createSpacer() {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));
        return spacer;
    }

    private LinearLayout.LayoutParams spacerParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
