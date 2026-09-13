package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.bottomsheet.MaterialBottomSheet;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class BottomSheetDemoActivity extends Activity {

    private TextView stateText;

    private interface SheetConfig {
        void apply(MaterialBottomSheet sheet);
    }

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

        content.addView(createLabel("Modal bottom sheet (defaults)"), labelParams());
        addOpenButton(content, "Open default sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
            }
        });

        content.addView(createLabel("Fullscreen mode (half expand available)"), labelParams());
        addOpenButton(content, "Open fullscreen sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
            }
        });

        content.addView(createLabel("fitToContents (half disabled)"), labelParams());
        addOpenButton(content, "Open fitToContents sheet (fullscreen)", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                sheet.setFitToContents(true);
            }
        });

        content.addView(createLabel("Custom peek height (160dp)"), labelParams());
        addOpenButton(content, "Open sheet with peek 160dp", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setPeekHeight(dp(160.0f));
            }
        });

        content.addView(createLabel("Custom expanded offset (120dp)"), labelParams());
        addOpenButton(content, "Open sheet with offset 120dp (fullscreen)", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                sheet.setExpandedOffset(dp(120.0f));
            }
        });

        content.addView(createLabel("Custom half ratio (0.3)"), labelParams());
        addOpenButton(content, "Open sheet with half ratio 0.3 (fullscreen)", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                sheet.setHalfExpandedRatio(0.3f);
            }
        });

        content.addView(createLabel("Current sheet state"), labelParams());
        stateText = new TextView(this);
        stateText.setText("—");
        stateText.setTextColor(SchemeHelper.onBackgroundColor());
        stateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        content.addView(stateText, itemParams());

        setContentView(scrollView);
    }

    private void addOpenButton(LinearLayout content, String text, final SheetConfig config) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setButtonStyle(ButtonStyle.FILLED);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialBottomSheet sheet = new MaterialBottomSheet(BottomSheetDemoActivity.this);
                sheet.setOnStateChangedListener(new MaterialBottomSheet.OnStateChangedListener() {
                    @Override
                    public void onStateChanged(MaterialBottomSheet.SheetState newState) {
                        stateText.setText(newState.name());
                    }
                });
                config.apply(sheet);
                sheet.setContent(createSheetContent(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.dismiss();
                    }
                }));
                sheet.show();
            }
        });
        content.addView(button, itemParams());
    }

    private View createSheetContent(final View.OnClickListener dismissListener) {
        LinearLayout sheetContent = new LinearLayout(this);
        sheetContent.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("Bottom sheet title");
        title.setTextColor(SchemeHelper.onBackgroundColor());
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = dp(8.0f);
        sheetContent.addView(title, titleParams);

        TextView body = new TextView(this);
        body.setText("This is a Material bottom sheet. Swipe down, tap outside or press " +
                "back to dismiss. Tap the handle to cycle states.");
        body.setTextColor(SchemeHelper.onBackgroundColor());
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bodyParams.bottomMargin = dp(16.0f);
        sheetContent.addView(body, bodyParams);

        MaterialButton actionButton = new MaterialButton(this);
        actionButton.setText("Dismiss");
        actionButton.setButtonStyle(ButtonStyle.FILLED);
        if (dismissListener != null) {
            actionButton.setOnClickListener(dismissListener);
        }
        sheetContent.addView(actionButton, actionParams());
        return sheetContent;
    }

    private LinearLayout.LayoutParams actionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(16.0f);
        return params;
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
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}