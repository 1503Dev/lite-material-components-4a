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
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicColor;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
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

        content.addView(caption("默认 — 圆角 12dp、容器色 surfaceContainerLow、elevation 0dp（默认无阴影）、"
                + "描边宽度 0dp（默认不描边，描边色默认 outline）；点击监听 → 涟漪"), captionParams());
        content.addView(createClickableCard(), cardParams());

        content.addView(caption("setCornerRadiusDp(24f)（默认 12dp）+ setContainerColor(secondaryContainer)"
                + "（替代旧 setCardBackgroundColor，默认 surfaceContainerLow）— 不可点击"), captionParams());
        content.addView(createCustomCard(), cardParams());

        content.addView(caption("仅 clickable=true（无监听）— 也显示涟漪"), captionParams());
        content.addView(createClickableOnlyCard(), cardParams());

        content.addView(caption("图片卡片 — setCornerRadiusDp(30f) + padding 0，内容按圆角裁剪"), captionParams());
        content.addView(createCustomCard2(), cardParams());

        content.addView(caption("阴影 — setElevationDp(8f)（默认 0dp = 无阴影；API 21 及以上通过 View.setElevation 生效，"
                + "21 以下只记录数值不绘制）"), captionParams());
        content.addView(createElevationCard(), cardParams());

        content.addView(caption("描边 — setStrokeWidthDp(2f)（默认 0dp）配默认描边色 outline；"
                + "setStrokeColor(error) 覆盖描边色（默认 outline）"), captionParams());
        content.addView(createStrokeCard(false), cardParams());
        content.addView(createStrokeCard(true), cardParams());

        content.addView(caption("读取与清除 — 容器色 tertiaryContainer、阴影 6dp、描边 2dp / error；"
                + "clearContainerColor() 回 surfaceContainerLow，clearElevationDp() 回 0dp，"
                + "clearStrokeWidthDp() 回 0dp，clearStrokeColor() 回 outline，"
                + "setColorScheme(null) 同时清除容器色与描边色覆盖（阴影与描边宽度保留）"), captionParams());
        final MaterialCardView overrideCard = createCardBase();
        overrideCard.setContainerColor(role(dynamicColors.tertiaryContainer()));
        overrideCard.setElevationDp(6f);
        overrideCard.setStrokeWidthDp(2f);
        overrideCard.setStrokeColor(role(dynamicColors.error()));
        addTitleAndBody(overrideCard, "Overrides", "容器色 tertiaryContainer、阴影 6dp、描边 2dp + error 色。");
        content.addView(overrideCard, cardParams());

        content.addView(action("读取 get / has", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("container=" + hex(overrideCard.getContainerColor())
                        + " hasContainerColor()=" + overrideCard.hasContainerColor()
                        + " | elevation=" + overrideCard.getElevationDp() + "dp hasElevationDp()=" + overrideCard.hasElevationDp()
                        + " | strokeWidth=" + overrideCard.getStrokeWidthDp() + "dp hasStrokeWidthDp()=" + overrideCard.hasStrokeWidthDp()
                        + " | strokeColor=" + hex(overrideCard.getStrokeColor())
                        + " hasStrokeColor()=" + overrideCard.hasStrokeColor());
            }
        }), buttonParams());
        content.addView(action("clearContainerColor() → surfaceContainerLow 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = overrideCard.getContainerColor();
                overrideCard.clearContainerColor();
                toast("clearContainerColor: " + hex(before) + " → " + hex(overrideCard.getContainerColor())
                        + "（surfaceContainerLow），hasContainerColor()=" + overrideCard.hasContainerColor());
            }
        }), buttonParams());
        content.addView(action("clearElevationDp() → 0dp（无阴影）", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float before = overrideCard.getElevationDp();
                overrideCard.clearElevationDp();
                toast("clearElevationDp: " + before + "dp → " + overrideCard.getElevationDp()
                        + "dp（默认 0dp），hasElevationDp()=" + overrideCard.hasElevationDp());
            }
        }), buttonParams());
        content.addView(action("clearStrokeWidthDp() → 0dp（不描边）", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float before = overrideCard.getStrokeWidthDp();
                overrideCard.clearStrokeWidthDp();
                toast("clearStrokeWidthDp: " + before + "dp → " + overrideCard.getStrokeWidthDp()
                        + "dp（默认 0dp），hasStrokeWidthDp()=" + overrideCard.hasStrokeWidthDp());
            }
        }), buttonParams());
        content.addView(action("clearStrokeColor() → outline 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = overrideCard.getStrokeColor();
                overrideCard.clearStrokeColor();
                toast("clearStrokeColor: " + hex(before) + " → " + hex(overrideCard.getStrokeColor())
                        + "（outline），hasStrokeColor()=" + overrideCard.hasStrokeColor());
            }
        }), buttonParams());
        content.addView(action("setColorScheme(null) → 清除容器色与描边色覆盖", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overrideCard.setColorScheme(null);
                toast("setColorScheme(null): container=" + hex(overrideCard.getContainerColor())
                        + "（surfaceContainerLow）strokeColor=" + hex(overrideCard.getStrokeColor())
                        + "（outline），elevation=" + overrideCard.getElevationDp() + "dp 与 strokeWidth="
                        + overrideCard.getStrokeWidthDp() + "dp 保持不变");
            }
        }), buttonParams());

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
        card.setContainerColor(dynamicColors.secondaryContainer().getArgb(MaterialCardView.publicColorScheme));
        addTitleAndBody(card, "Custom Card", "自定义圆角与容器色（setContainerColor / getContainerColor / clearContainerColor），"
                + "没有点击监听和 clickable，无涟漪。");
        return card;
    }

    private MaterialCardView createClickableOnlyCard() {
        MaterialCardView card = createCardBase();
        addTitleAndBody(card, "Clickable Only", "直接 setClickable(true)，无监听器，按下同样显示涟漪。");
        card.setClickable(true);
        return card;
    }

    private MaterialCardView createElevationCard() {
        MaterialCardView card = createCardBase();
        card.setElevationDp(8.0f);
        addTitleAndBody(card, "Elevated 8dp", "默认 elevation 0dp 无阴影；clearElevationDp() 后回到 0dp。");
        return card;
    }

    private MaterialCardView createStrokeCard(boolean customColor) {
        MaterialCardView card = createCardBase();
        card.setStrokeWidthDp(2.0f);
        if (customColor) {
            card.setStrokeColor(role(dynamicColors.error()));
            addTitleAndBody(card, "Stroke 2dp / error", "描边色覆盖为 error；clearStrokeColor() 后回到 outline 角色色。");
        } else {
            addTitleAndBody(card, "Stroke 2dp / outline", "描边宽度 2dp，颜色沿用默认 outline 角色色。");
        }
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

    private MaterialButton action(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setStyle(ButtonStyle.OUTLINED);
        button.setOnClickListener(listener);
        return button;
    }

    private int role(DynamicColor color) {
        return color.getArgb(MaterialCardView.publicColorScheme);
    }

    private String hex(int color) {
        return String.format("#%08X", color);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
