package dev1503.litematerial.lmc4a.app.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.dialog.MaterialDialogBuilder;

public class DialogDemoActivity extends DemoActivity {

    private static final float CUSTOM_CORNER_RADIUS_DP = 8.0f;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private final DialogInterface.OnClickListener dismissListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    private int colorPrimaryContainer;
    private int colorOnPrimaryContainer;
    private int colorTertiaryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);
        colorPrimaryContainer = dynamicColors.primaryContainer()
                .getArgb(MaterialDialogBuilder.publicColorScheme);
        colorOnPrimaryContainer = dynamicColors.onPrimaryContainer()
                .getArgb(MaterialDialogBuilder.publicColorScheme);
        colorTertiaryContainer = dynamicColors.tertiaryContainer()
                .getArgb(MaterialDialogBuilder.publicColorScheme);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("基础对话框：容器色为角色色 surfaceContainerHigh，标题 onSurface 24sp，"
                + "正文 onSurfaceVariant 14sp，按钮文字用 MaterialButton 自身的角色色，四角圆角默认 28dp。"));
        content.addView(createButton("Basic Dialog"), buttonParams());

        content.addView(caption("确认对话框：setPositiveButton / setNegativeButton 填入文字与监听器，"
                + "按钮文字色未覆盖时跟随按钮角色色（primary）。"));
        content.addView(createButton("Confirm Dialog"), buttonParams());

        content.addView(caption("三按钮对话框：中性 / 否定 / 肯定三个按钮按 neutral → negative → positive "
                + "从右到左排列，只传文字的按钮不会显示。"));
        content.addView(createButton("Three Buttons Dialog"), buttonParams());

        content.addView(caption("自定义 View 对话框：setView(View) 放在标题与正文之后、按钮行之前，"
                + "左右各留 24dp；标题为 null 时不会占位。"));
        content.addView(createButton("Custom View Dialog"), buttonParams());

        content.addView(caption("图标对话框（新增）：setIcon(Drawable) 设置标题旁的图标，getIcon() 读回（未设置时为 null），"
                + "clearIcon() 清除；图标位由 AlertDialog 提供，不支持图标的平台实现会被忽略。"));
        content.addView(createButton("Icon Dialog"), buttonParams());

        content.addView(caption("自定义颜色与圆角（新增）：setContainerColor（默认 surfaceContainerHigh）、"
                + "setTitleColor（默认 onSurface）、setMessageColor（默认 onSurfaceVariant）、"
                + "setButtonTextColor（默认 MaterialButton 自身角色色）、setCornerRadiusDp（默认 28dp），"
                + "全部 fluent 返回 builder；本例用 primaryContainer / onPrimaryContainer / tertiaryContainer 与 8dp 圆角。"));
        content.addView(createButton("Custom Color Dialog"), buttonParams());

        content.addView(caption("clearXxx 恢复角色色：先覆盖容器色 / 标题色 / 正文色 / 按钮文字色并改圆角，"
                + "再 clearContainerColor() / clearTitleColor() / clearMessageColor() / clearButtonTextColor() / "
                + "clearCornerRadiusDp()，各处回到 surfaceContainerHigh / onSurface / onSurfaceVariant / 按钮角色色 / 28dp。"));
        content.addView(createButton("clearXxx Dialog"), buttonParams());

        content.addView(caption("fluent setColorScheme（新增返回值）：setColorScheme(DynamicScheme) 现在返回 builder，"
                + "可继续链式调用，并会清除容器色 / 标题色 / 正文色 / 按钮文字色四处覆盖；getColorScheme() 读回当前方案。"));
        content.addView(createButton("Fluent setColorScheme Dialog"), buttonParams());

        content.addView(caption("setCancelable(false)：返回键与点击对话框外部都不再关闭，必须点按钮（默认 true，可取消）。"));
        content.addView(createButton("Non-cancelable Dialog"), buttonParams());

        content.addView(caption("静态 applyDismissAnimation(AlertDialog, Runnable)：先播放缩小淡出动画，动画结束后再真正 "
                + "dismiss，签名与行为未变（show() 内部的入场动画是自动的）。"));
        content.addView(createButton("Dismiss Animation Dialog"), buttonParams());

        content.addView(spacer(32.0f));

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
                case "Icon Dialog":
                    showIconDialog();
                    break;
                case "Custom Color Dialog":
                    showCustomColorDialog();
                    break;
                case "clearXxx Dialog":
                    showClearOverridesDialog();
                    break;
                case "Fluent setColorScheme Dialog":
                    showFluentColorSchemeDialog();
                    break;
                case "Non-cancelable Dialog":
                    showNonCancelableDialog();
                    break;
                case "Dismiss Animation Dialog":
                    showDismissAnimationDialog();
                    break;
            }
        }
    };

    private void showBasicDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Basic Dialog")
                .setMessage("This is a basic Material 3 dialog with spring animation.")
                .setPositiveButton("OK", dismissListener)
                .show();
    }

    private void showConfirmDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
                .setPositiveButton("Delete", dismissListener)
                .setNegativeButton("Cancel", dismissListener)
                .show();
    }

    private void showThreeButtonsDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Choose Option")
                .setMessage("Please select one of the following options.")
                .setPositiveButton("Accept", dismissListener)
                .setNegativeButton("Decline", dismissListener)
                .setNeutralButton("Maybe", dismissListener)
                .show();
    }

    private void showCustomViewDialog() {
        LinearLayout customView = new LinearLayout(this);
        customView.setOrientation(LinearLayout.VERTICAL);
        customView.setPadding(dp(24), dp(16), dp(24), dp(8));

        TextView label = new TextView(this);
        label.setText("Enter your name:");
        label.setTextSize(14);
        label.setTextColor(SchemeHelper.onBackgroundColor());
        customView.addView(label, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        new MaterialDialogBuilder(this)
                .setTitle("Custom View")
                .setView(customView)
                .setPositiveButton("Save", dismissListener)
                .setNegativeButton("Cancel", dismissListener)
                .show();
    }

    private void showIconDialog() {
        MaterialDialogBuilder builder = new MaterialDialogBuilder(this)
                .setTitle("Icon Dialog")
                .setMessage("setIcon(Drawable) 设置的图标显示在标题旁；clearIcon() 清除后 getIcon() 回到 null，"
                        + "AlertDialog 会隐藏图标位。")
                .setPositiveButton("OK", dismissListener)
                .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
        String afterSet = builder.getIcon() == null ? "null" : "已设置图标";
        builder.clearIcon();
        String afterClear = builder.getIcon() == null ? "null" : "已设置图标";
        builder.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info));
        toast("setIcon(...) → getIcon() = " + afterSet + "；clearIcon() → getIcon() = " + afterClear
                + "；再 setIcon(...) 后弹出带图标的对话框");
        builder.show();
    }

    private void showCustomColorDialog() {
        MaterialDialogBuilder builder = new MaterialDialogBuilder(this)
                .setTitle("Custom Color Dialog")
                .setMessage("容器色 primaryContainer、标题与正文 onPrimaryContainer、"
                        + "按钮文字 tertiaryContainer、圆角 8dp（默认 28dp）。")
                .setPositiveButton("OK", dismissListener)
                .setNegativeButton("Cancel", dismissListener)
                .setContainerColor(colorPrimaryContainer)
                .setTitleColor(colorOnPrimaryContainer)
                .setMessageColor(colorOnPrimaryContainer)
                .setButtonTextColor(colorTertiaryContainer)
                .setCornerRadiusDp(CUSTOM_CORNER_RADIUS_DP);
        toast("覆盖值：" + describe(builder));
        builder.show();
    }

    private void showClearOverridesDialog() {
        MaterialDialogBuilder builder = new MaterialDialogBuilder(this)
                .setTitle("clearXxx 恢复角色色")
                .setMessage("先覆盖容器色 / 标题色 / 正文色 / 按钮文字色并把圆角改成 8dp，"
                        + "再依次 clear 四处颜色覆盖与圆角覆盖。")
                .setPositiveButton("OK", dismissListener)
                .setContainerColor(colorPrimaryContainer)
                .setTitleColor(colorOnPrimaryContainer)
                .setMessageColor(colorOnPrimaryContainer)
                .setButtonTextColor(colorTertiaryContainer)
                .setCornerRadiusDp(CUSTOM_CORNER_RADIUS_DP);
        String overridden = describe(builder);
        builder.clearContainerColor()
                .clearTitleColor()
                .clearMessageColor()
                .clearButtonTextColor()
                .clearCornerRadiusDp();
        String restored = describe(builder);
        toast("覆盖时：" + overridden + "\nclear 后：" + restored);
        builder.show();
    }

    private void showFluentColorSchemeDialog() {
        MaterialDialogBuilder builder = new MaterialDialogBuilder(this)
                .setTitle("Fluent setColorScheme")
                .setMessage("setColorScheme(DynamicScheme) 返回 builder，可继续链式调用；"
                        + "它同时清除容器色 / 标题色 / 正文色 / 按钮文字色四处覆盖。")
                .setContainerColor(colorPrimaryContainer)
                .setTitleColor(colorOnPrimaryContainer)
                .setColorScheme(MaterialDialogBuilder.publicColorScheme)
                .setPositiveButton("OK", dismissListener);
        toast("链式 setColorScheme(publicColorScheme) 后：容器 "
                + hex(builder.getContainerColor()) + "(surfaceContainerHigh)、标题 "
                + hex(builder.getTitleColor()) + "(onSurface)、getColorScheme() = "
                + (builder.getColorScheme() == MaterialDialogBuilder.publicColorScheme
                ? "publicColorScheme" : "其它"));
        builder.show();
    }

    private void showNonCancelableDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("Non-cancelable Dialog")
                .setMessage("setCancelable(false)：返回键与点击对话框外部都不会关闭，只能点下面的按钮。")
                .setPositiveButton("Close", dismissListener)
                .setCancelable(false)
                .show();
    }

    private void showDismissAnimationDialog() {
        new MaterialDialogBuilder(this)
                .setTitle("applyDismissAnimation")
                .setMessage("点击按钮后先播放缩小淡出动画，动画结束的回调里再真正 dismiss()。")
                .setPositiveButton("Animate & Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MaterialDialogBuilder.applyDismissAnimation((AlertDialog) dialog, new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .show();
    }

    private String describe(MaterialDialogBuilder builder) {
        return "容器 " + hex(builder.getContainerColor())
                + " / 标题 " + hex(builder.getTitleColor())
                + " / 正文 " + hex(builder.getMessageColor())
                + " / 按钮 " + hex(builder.getButtonTextColor())
                + " / 圆角 " + builder.getCornerRadiusDp() + "dp";
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

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private static String hex(int color) {
        return String.format("#%08X", color);
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
