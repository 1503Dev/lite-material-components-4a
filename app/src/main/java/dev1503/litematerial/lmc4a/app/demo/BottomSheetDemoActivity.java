package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.bottomsheet.MaterialBottomSheet;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class BottomSheetDemoActivity extends Activity {

    private static final float CUSTOM_CORNER_RADIUS_DP = 16.0f;
    private static final float CUSTOM_SCRIM_ALPHA = 0.45f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

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
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("默认 sheet：容器色是角色色 surfaceContainerLow，遮罩走系统窗口调光"
                + "（角色色 scrim × backgroundDimAmount），顶部圆角 28dp，面板可拖拽（isDragEnabled()=true）、"
                + "可下滑隐藏（isHideable()=true），收起锚点用 PEEK_HEIGHT_AUTO = -1（自动，96dp~256dp 之间）。"));
        addOpenButton(content, "打开默认 sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                toast("默认：容器 " + hex(sheet.getContainerColor()) + "(surfaceContainerLow)、圆角 "
                        + sheet.getCornerRadiusDp() + "dp、拖拽 " + sheet.isDragEnabled()
                        + "、可隐藏 " + sheet.isHideable() + "、peek " + sheet.getPeekHeight()
                        + " (PEEK_HEIGHT_AUTO=" + MaterialBottomSheet.PEEK_HEIGHT_AUTO + ")");
            }
        });

        content.addView(caption("setFullscreenMode(true)：展开高度 = 屏幕高度 - expandedOffset - 状态栏高度；"
                + "只有全屏模式才允许「半展开」锚点（默认 false，非全屏固定为屏幕高度的 1/2）。"
                + "isFullscreenMode() 读回。"));
        addOpenButton(content, "打开全屏 sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                toast("setFullscreenMode(true)，isFullscreenMode() = " + sheet.isFullscreenMode());
            }
        });

        content.addView(caption("setFitToContents(true)：面板贴合内容高度，此时不再提供半展开锚点"
                + "（默认 false，面板高度为屏幕高度的 1/2）；isFitToContents() 读回。"));
        addOpenButton(content, "打开 fitToContents sheet（全屏）", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                sheet.setFitToContents(true);
                toast("setFitToContents(true)，isFitToContents() = " + sheet.isFitToContents());
            }
        });

        content.addView(caption("setPeekHeight(160dp)：收起锚点固定为 160dp（单位 px，这里用 dp() 换算）。"
                + "默认 PEEK_HEIGHT_AUTO(-1) 表示自动高度；getPeekHeight() 读回。"));
        addOpenButton(content, "打开 peek = 160dp 的 sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setPeekHeight(dp(160.0f));
                toast("setPeekHeight(160dp)，getPeekHeight() = " + sheet.getPeekHeight() + "px");
            }
        });

        content.addView(caption("setExpandedOffset(120dp)：全屏展开时顶部留出 120dp 空隙"
                + "（默认 0，只在全屏模式生效）；getExpandedOffset() 读回。"));
        addOpenButton(content, "打开 offset = 120dp 的 sheet（全屏）", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                sheet.setExpandedOffset(dp(120.0f));
                toast("setExpandedOffset(120dp)，getExpandedOffset() = " + sheet.getExpandedOffset() + "px");
            }
        });

        content.addView(caption("setHalfExpandedRatio(0.3)：半展开高度 = 屏幕高度 × 0.3"
                + "（默认 0.5，只在全屏模式生效）；getHalfExpandedRatio() 读回。"
                + "点手柄可在 收起 → 半展开 → 展开 之间循环。"));
        addOpenButton(content, "打开半展开比例 0.3 的 sheet（全屏）", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setFullscreenMode(true);
                sheet.setHalfExpandedRatio(0.3f);
                toast("setHalfExpandedRatio(0.3)，getHalfExpandedRatio() = "
                        + sheet.getHalfExpandedRatio());
            }
        });

        final int primaryContainer = dynamicColors.primaryContainer()
                .getArgb(MaterialBottomSheet.publicColorScheme);
        content.addView(caption("自定义容器色与圆角：setContainerColor(primaryContainer) 覆盖角色色 "
                + "surfaceContainerLow，setCornerRadiusDp(16dp) 覆盖默认 28dp 的顶部圆角；"
                + "clearContainerColor() / clearCornerRadiusDp() 可分别恢复角色色与 28dp。"));
        addOpenButton(content, "打开自定义容器色 + 16dp 圆角", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setContainerColor(primaryContainer);
                sheet.setCornerRadiusDp(CUSTOM_CORNER_RADIUS_DP);
                toast("setContainerColor(primaryContainer=" + hex(primaryContainer)
                        + ")、setCornerRadiusDp(16) → getCornerRadiusDp() = "
                        + sheet.getCornerRadiusDp() + "dp");
            }
        });

        final int scrim = applyAlpha(dynamicColors.scrim()
                .getArgb(MaterialBottomSheet.publicColorScheme), CUSTOM_SCRIM_ALPHA);
        content.addView(caption("自定义遮罩色：setScrimColor(半透明 scrim, alpha 0.45) 后由组件自绘遮罩层并关闭"
                + "系统窗口调光（FLAG_DIM_BEHIND）；不透明色会完全挡住身后的页面，所以示例用半透明。"
                + "默认未覆盖时按角色色 scrim × backgroundDimAmount 调光；clearScrimColor() 恢复系统调光。"));
        addOpenButton(content, "打开自定义遮罩色 sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setScrimColor(scrim);
                toast("setScrimColor(" + hex(scrim) + ")，getScrimColor() = " + hex(sheet.getScrimColor())
                        + "；clearScrimColor() 后回到系统调光");
            }
        });

        content.addView(caption("setDragEnabled(false)：触摸拖拽不再改变面板位置，手柄点击与遮罩点击仍然可用"
                + "（默认 true）；面板内的 hide() 按钮也照常关闭面板。isDragEnabled() 读回。"));
        addOpenButton(content, "打开不可拖拽的 sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setDragEnabled(false);
                toast("setDragEnabled(false)，isDragEnabled() = " + sheet.isDragEnabled());
            }
        });

        content.addView(caption("setHideable(false)：下滑只回到最近锚点，同时等价于 setCancelable(false)，"
                + "返回键与点击遮罩都不再关闭（默认 true）；clear 没有对应 API，恢复需重新 setHideable(true)。"));
        addOpenButton(content, "打开不可隐藏的 sheet", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setHideable(false);
                toast("setHideable(false)（= setCancelable(false)），isHideable() = " + sheet.isHideable()
                        + "；请用面板内的 hide() 按钮关闭");
            }
        });

        content.addView(caption("clearXxx 恢复角色色：先覆盖容器色 / 遮罩色 / 圆角三处，再用 "
                + "clearContainerColor() / clearScrimColor() / clearCornerRadiusDp() 清掉覆盖，"
                + "容器回到 surfaceContainerLow、遮罩回到系统调光、圆角回到 28dp，setColorScheme() 同样会一次清除这三处覆盖。"));
        addOpenButton(content, "演示 clearXxx 恢复角色色", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setContainerColor(primaryContainer);
                sheet.setScrimColor(scrim);
                sheet.setCornerRadiusDp(CUSTOM_CORNER_RADIUS_DP);
                String overridden = "覆盖时：容器 " + hex(sheet.getContainerColor())
                        + "、遮罩 " + hex(sheet.getScrimColor())
                        + "、圆角 " + sheet.getCornerRadiusDp() + "dp";
                sheet.clearContainerColor();
                sheet.clearScrimColor();
                sheet.clearCornerRadiusDp();
                String restored = "clear 后：容器 " + hex(sheet.getContainerColor())
                        + "(surfaceContainerLow)、圆角 " + sheet.getCornerRadiusDp() + "dp";
                toast(overridden + "\n" + restored);
            }
        });

        content.addView(caption("setColorScheme(DynamicScheme)：应用配色方案并一次性清除容器色、遮罩色、圆角三处覆盖；"
                + "getColorScheme() 读回当前方案。本例重新应用 publicColorScheme，效果与 clearXxx 三连一致。"));
        addOpenButton(content, "setColorScheme 清除全部覆盖", new SheetConfig() {
            @Override
            public void apply(MaterialBottomSheet sheet) {
                sheet.setContainerColor(primaryContainer);
                sheet.setScrimColor(scrim);
                sheet.setCornerRadiusDp(CUSTOM_CORNER_RADIUS_DP);
                sheet.setColorScheme(MaterialBottomSheet.publicColorScheme);
                toast("setColorScheme(publicColorScheme) → 容器 " + hex(sheet.getContainerColor())
                        + "、圆角 " + sheet.getCornerRadiusDp() + "dp、getColorScheme()="
                        + (sheet.getColorScheme() == MaterialBottomSheet.publicColorScheme
                        ? "publicColorScheme" : "其它"));
            }
        });

        content.addView(caption("当前面板状态（OnStateChangedListener / getSheetState()）：COLLAPSED → DRAGGING → "
                + "HALF_EXPANDED / EXPANDED，面板内的 setSheetState(...) 按钮可手动在两个锚点之间切换。"));
        stateText = new TextView(this);
        stateText.setText("—");
        stateText.setTextColor(SchemeHelper.onBackgroundColor());
        stateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        content.addView(stateText, itemParams());

        content.addView(spacer(32.0f));

        setContentView(scrollView);
    }

    private void addOpenButton(LinearLayout content, String text, final SheetConfig config) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setStyle(ButtonStyle.FILLED);
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
                sheet.setContent(createSheetContent(sheet));
                sheet.show();
                toast("show()：getSheetState() = " + sheet.getSheetState()
                        + "，getContent() " + (sheet.getContent() != null ? "已设置" : "为空"));
            }
        });
        content.addView(button, buttonParams());
    }

    private View createSheetContent(final MaterialBottomSheet sheet) {
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
        body.setText("这是 Material bottom sheet。可下滑、点遮罩或按返回键（isHideable() 为 true 时）关闭，"
                + "点手柄在锚点之间循环；hide() 关闭面板，clearXxx() 恢复角色色。");
        body.setTextColor(SchemeHelper.onBackgroundColor());
        body.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bodyParams.bottomMargin = dp(16.0f);
        sheetContent.addView(body, bodyParams);

        LinearLayout buttonRow = new LinearLayout(this);
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton hideButton = new MaterialButton(this);
        hideButton.setText("hide()");
        hideButton.setStyle(ButtonStyle.FILLED);
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheet.hide();
            }
        });
        buttonRow.addView(hideButton, sheetButtonParams());

        MaterialButton stateButton = new MaterialButton(this);
        stateButton.setText("setSheetState(...)");
        stateButton.setStyle(ButtonStyle.OUTLINED);
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = sheet.getSheetState() == MaterialBottomSheet.SheetState.EXPANDED;
                sheet.setSheetState(expanded
                        ? MaterialBottomSheet.SheetState.COLLAPSED
                        : MaterialBottomSheet.SheetState.EXPANDED);
                toast("setSheetState(...) → getSheetState() = " + sheet.getSheetState());
            }
        });
        buttonRow.addView(stateButton, sheetButtonParams());

        sheetContent.addView(buttonRow);
        return sheetContent;
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        caption.setTextColor(SchemeHelper.onBackgroundColor());
        int horizontal = dp(16.0f);
        caption.setPadding(horizontal, dp(16.0f), horizontal, dp(8.0f));
        caption.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return caption;
    }

    private View spacer(float heightDp) {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(heightDp)));
        return spacer;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(16.0f);
        params.rightMargin = dp(16.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams sheetButtonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams itemParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(16.0f);
        params.rightMargin = dp(16.0f);
        return params;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static String hex(int color) {
        return String.format("#%08X", color);
    }

    private static int applyAlpha(int argb, float alphaFraction) {
        int alpha = (int) (((argb >>> 24) & 0xFF) * alphaFraction);
        return (argb & 0x00FFFFFF) | (alpha << 24);
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
