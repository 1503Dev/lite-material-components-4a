package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.R;
import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.cardview.MaterialCardView;

public class CardViewDemoActivity extends Activity {

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

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

        content.addView(caption("默认圆角 12dp — 点击监听 → 涟漪"), captionParams());
        content.addView(createClickableCard(), cardParams());

        content.addView(caption("自定义圆角 24dp / secondaryContainer 背景 — 不可点击"), captionParams());
        content.addView(createCustomCard(), cardParams());

        content.addView(caption("仅 clickable=true（无监听）— 也显示涟漪"), captionParams());
        content.addView(createClickableOnlyCard(), cardParams());

        content.addView(createCustomCard2(), cardParams());

        setContentView(scrollView);
    }

    private MaterialCardView createClickableCard() {
        MaterialCardView card = createCardBase();
        addTitleAndBody(card, "Clickable Card", "点击这张卡片观察涟漪效果（API<21 显示遮罩）。");
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CardViewDemoActivity.this, "Card clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return card;
    }

    private MaterialCardView createCustomCard2() {
        MaterialCardView card = createCardBase();
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        -1,-1
                )
        );
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.addView(imageView);
        card.setCornerRadiusDp(30);
        card.setPadding(0, 0, 0, 0);
        return card;
    }

    private MaterialCardView createCustomCard() {
        MaterialCardView card = createCardBase();
        card.setCornerRadiusDp(24.0f);
        card.setCardBackgroundColor(
                dynamicColors.secondaryContainer().getArgb(MaterialCardView.publicColorScheme));
        addTitleAndBody(card, "Custom Card", "自定义圆角与背景色，没有点击监听和 clickable，无涟漪。");
        return card;
    }

    private MaterialCardView createClickableOnlyCard() {
        MaterialCardView card = createCardBase();
        addTitleAndBody(card, "Clickable Only", "直接 setClickable(true)，无监听器，按下同样显示涟漪。");
        card.setClickable(true);
        return card;
    }

    private MaterialCardView createCardBase() {
        MaterialCardView card = new MaterialCardView(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int innerPadding = dp(16.0f);
        card.setPadding(innerPadding, dp(20.0f), innerPadding, dp(20.0f));
        return card;
    }

    private void addTitleAndBody(MaterialCardView card, String title, String body) {
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16.0f);
        titleView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleView.setTextColor(SchemeHelper.onBackgroundColor());
        card.addView(titleView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView bodyView = new TextView(this);
        bodyView.setText(body);
        bodyView.setTextSize(14.0f);
        bodyView.setTextColor(SchemeHelper.onBackgroundColor());
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bodyParams.topMargin = dp(8.0f);
        card.addView(bodyView, bodyParams);
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

    private LinearLayout.LayoutParams cardParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(24.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}