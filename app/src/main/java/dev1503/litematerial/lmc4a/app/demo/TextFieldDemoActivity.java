package dev1503.litematerial.lmc4a.app.demo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicColor;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.textfield.MaterialTextField;
import dev1503.lmc4a.v3.widget.textfield.TextFieldStyle;

public class TextFieldDemoActivity extends DemoActivity {

    private static final int CLEAR_CONTAINER = 0;
    private static final int CLEAR_CONTENT = 1;
    private static final int CLEAR_LABEL = 2;
    private static final int CLEAR_PLACEHOLDER = 3;
    private static final int CLEAR_SUPPORTING = 4;
    private static final int CLEAR_INDICATOR = 5;
    private static final int CLEAR_FOCUSED_INDICATOR = 6;
    private static final int CLEAR_LEADING_ICON = 7;
    private static final int CLEAR_TRAILING_ICON = 8;

    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

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

        content.addView(caption("FILLED（默认 setStyle(TextFieldStyle.FILLED)）— setLabel + setPlaceholder"
                + "（替代旧 setPlaceholderText，仅聚焦且为空时显示）+ setSupportingText；"
                + "默认容器色 surfaceContainerHighest、标签 onSurfaceVariant（聚焦 primary）、指示线 onSurfaceVariant"), captionParams());
        MaterialTextField filled = new MaterialTextField(this);
        filled.setLabel("姓名");
        filled.setPlaceholder("请输入姓名");
        filled.setSupportingText("用于在个人资料中显示");
        content.addView(filled, fieldParams());

        content.addView(caption("OUTLINED — setStyle(TextFieldStyle.OUTLINED)（替代旧 setTextFieldStyle，默认 FILLED）"
                + "+ 字数统计；默认边框色 outline（聚焦 primary）"), captionParams());
        final MaterialTextField outlined = new MaterialTextField(this);
        outlined.setStyle(TextFieldStyle.OUTLINED);
        outlined.setLabel("简介");
        outlined.setCounterEnabled(true);
        outlined.setCounterMaxLength(20);
        outlined.setSingleLine(true);
        content.addView(outlined, fieldParams());
        content.addView(action("读取 getStyle() / isCounterEnabled() / getCounterMaxLength()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("getStyle()=" + outlined.getStyle() + "，isCounterEnabled()=" + outlined.isCounterEnabled()
                        + "，getCounterMaxLength()=" + outlined.getCounterMaxLength());
            }
        }), buttonParams());

        content.addView(caption("圆角 — setCornerRadiusDp(16f)（默认 4dp），getCornerRadiusDp() 读取；"
                + "填充与轮廓两种样式的圆角都生效"), captionParams());
        final MaterialTextField rounded = new MaterialTextField(this);
        rounded.setStyle(TextFieldStyle.OUTLINED);
        rounded.setLabel("圆角 16dp");
        rounded.setCornerRadiusDp(16f);
        rounded.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_menu_search));
        content.addView(rounded, fieldParams());
        content.addView(action("读取 getCornerRadiusDp()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("getCornerRadiusDp()=" + rounded.getCornerRadiusDp() + "dp（默认 4dp，无 clear，重新 set 即可改回 4f）");
            }
        }), buttonParams());

        content.addView(caption("错误状态 — 点按钮校验；setError(null) 清除错误。错误文字 / 标签 / 图标 / 指示线默认用 error 角色色"), captionParams());
        final MaterialTextField errorField = new MaterialTextField(this);
        errorField.setStyle(TextFieldStyle.OUTLINED);
        errorField.setLabel("邮箱");
        errorField.setSupportingText("需要包含 @");
        errorField.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        errorField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        content.addView(errorField, fieldParams());

        MaterialButton validate = new MaterialButton(this);
        validate.setText("校验");
        validate.setStyle(ButtonStyle.OUTLINED);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = errorField.getText();
                if (text == null || text.toString().indexOf('@') < 0) {
                    errorField.setError("邮箱格式不正确");
                } else {
                    errorField.setError(null);
                }
                toast("setError 后 isErrorEnabled()=" + errorField.isErrorEnabled());
            }
        });
        content.addView(validate, buttonParams());

        content.addView(caption("setErrorColor(tertiary) 覆盖第 10 组颜色 —— 错误文字、错误态标签、错误态图标与"
                + "错误态指示线 / 轮廓线同时换色（默认 error 角色色）；clearErrorColor() 后回到 error"), captionParams());
        final MaterialTextField errorColorField = new MaterialTextField(this);
        errorColorField.setStyle(TextFieldStyle.OUTLINED);
        errorColorField.setLabel("错误色覆盖");
        errorColorField.setText("abc");
        errorColorField.setError("邮箱格式不正确");
        errorColorField.setErrorColor(role(dynamicColors.tertiary()));
        content.addView(errorColorField, fieldParams());
        content.addView(action("读取 getErrorColor() / hasErrorColor()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("getErrorColor()=" + hex(errorColorField.getErrorColor()) + "，hasErrorColor()="
                        + errorColorField.hasErrorColor());
            }
        }), buttonParams());
        content.addView(action("clearErrorColor() → 回到 error 角色色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int before = errorColorField.getErrorColor();
                errorColorField.clearErrorColor();
                toast("clearErrorColor: " + hex(before) + " → " + hex(errorColorField.getErrorColor())
                        + "（error 角色色），hasErrorColor()=" + errorColorField.hasErrorColor());
            }
        }), buttonParams());

        content.addView(caption("图标 — setLeadingIcon + setOnLeadingIconClickListener（新增，点击 Toast）"
                + "与 setTrailingIcon + setOnTrailingIconClickListener（切换密码可见性）；"
                + "默认没有监听器时图标不拦截触摸"), captionParams());
        final MaterialTextField password = new MaterialTextField(this);
        password.setLabel("密码");
        password.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_lock_lock));
        password.setTrailingIcon(getResources().getDrawable(android.R.drawable.ic_menu_view));
        password.setOnLeadingIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("onLeadingIconClick — 前置图标被点击（getLeadingIcon() "
                        + (password.getLeadingIcon() == null ? "== null" : "!= null") + "）");
            }
        });
        password.setOnTrailingIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hidden = password.getTransformationMethod() instanceof PasswordTransformationMethod;
                password.setTransformationMethod(hidden ? null : PasswordTransformationMethod.getInstance());
                toast("onTrailingIconClick — 密码" + (hidden ? "已显示" : "已隐藏"));
            }
        });
        content.addView(password, fieldParams());

        content.addView(caption("禁用 — 填充样式容器为 4% onSurface、文字 38% onSurface；轮廓样式边框 12% onSurface"), captionParams());
        MaterialTextField disabled = new MaterialTextField(this);
        disabled.setLabel("只读");
        disabled.setText("不可编辑的内容");
        disabled.setSupportingText("禁用态使用 38% onSurface");
        disabled.setEnabled(false);
        content.addView(disabled, fieldParams());

        MaterialTextField disabledOutlined = new MaterialTextField(this);
        disabledOutlined.setStyle(TextFieldStyle.OUTLINED);
        disabledOutlined.setLabel("只读（描边）");
        disabledOutlined.setText("不可编辑的内容");
        disabledOutlined.setEnabled(false);
        content.addView(disabledOutlined, fieldParams());

        content.addView(caption("多行 — setSingleLine(false) + setMinLines(3)"), captionParams());
        MaterialTextField multiline = new MaterialTextField(this);
        multiline.setLabel("备注");
        multiline.setSupportingText("容器会随高度拉伸");
        multiline.setSingleLine(false);
        multiline.setMinLines(3);
        multiline.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
        content.addView(multiline, fieldParams());

        content.addView(caption("字号 — setTextSize(20f) 设置输入文字 20sp（平台 API，单位 sp，无 clear）、"
                + "setLabelTextSizeSp(16f) 浮动标签（默认 12sp）、setSupportingTextSizeSp(16f) 辅助文字（默认 12sp）"), captionParams());
        final MaterialTextField sizeField = new MaterialTextField(this);
        sizeField.setStyle(TextFieldStyle.FILLED);
        sizeField.setLabel("字号");
        sizeField.setText("输入文字 20sp");
        sizeField.setSupportingText("标签 16sp / 辅助文字 16sp");
        sizeField.setLabelTextSizeSp(16f);
        sizeField.setSupportingTextSizeSp(16f);
        sizeField.setTextSize(20f);
        content.addView(sizeField, fieldParams());
        content.addView(action("读取字号 getTextSize()/getLabelTextSizeSp()/hasLabelTextSizeSp() 等", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float textSp = sizeField.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                toast("text=" + textSp + "sp（getTextSize() 返回 px），label=" + sizeField.getLabelTextSizeSp()
                        + "sp hasLabelTextSizeSp()=" + sizeField.hasLabelTextSizeSp()
                        + "，supporting=" + sizeField.getSupportingTextSizeSp()
                        + "sp hasSupportingTextSizeSp()=" + sizeField.hasSupportingTextSizeSp());
            }
        }), buttonParams());
        content.addView(action("clearLabelTextSizeSp() → 回到 12sp", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float before = sizeField.getLabelTextSizeSp();
                sizeField.clearLabelTextSizeSp();
                toast("clearLabelTextSizeSp: " + before + "sp → " + sizeField.getLabelTextSizeSp()
                        + "sp（默认 12sp），hasLabelTextSizeSp()=" + sizeField.hasLabelTextSizeSp());
            }
        }), buttonParams());
        content.addView(action("clearSupportingTextSizeSp() → 回到 12sp", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float before = sizeField.getSupportingTextSizeSp();
                sizeField.clearSupportingTextSizeSp();
                toast("clearSupportingTextSizeSp: " + before + "sp → " + sizeField.getSupportingTextSizeSp()
                        + "sp（默认 12sp），hasSupportingTextSizeSp()=" + sizeField.hasSupportingTextSizeSp());
            }
        }), buttonParams());

        content.addView(caption("颜色覆盖 — ContainerColor(secondaryContainer)、ContentColor(onSecondaryContainer)、"
                + "LabelColor(tertiary)、PlaceholderColor(tertiary)、SupportingTextColor(tertiary)、"
                + "IndicatorColor(outlineVariant)、FocusedIndicatorColor(tertiary)、Leading/TrailingIconColor(tertiary)；"
                + "默认依次为 surfaceContainerHighest / onSurface / onSurfaceVariant（聚焦 primary）/ onSurfaceVariant / "
                + "onSurfaceVariant / outline（填充为 onSurfaceVariant）/ primary / onSurfaceVariant"), captionParams());
        final MaterialTextField colorField = new MaterialTextField(this);
        colorField.setStyle(TextFieldStyle.OUTLINED);
        colorField.setLabel("颜色覆盖");
        colorField.setPlaceholder("聚焦后显示占位色");
        colorField.setSupportingText("辅助文字色已覆盖");
        colorField.setText("内容文字色已覆盖");
        colorField.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_menu_search));
        colorField.setTrailingIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
        colorField.setContainerColor(role(dynamicColors.secondaryContainer()));
        colorField.setContentColor(role(dynamicColors.onSecondaryContainer()));
        colorField.setLabelColor(role(dynamicColors.tertiary()));
        colorField.setPlaceholderColor(role(dynamicColors.tertiary()));
        colorField.setSupportingTextColor(role(dynamicColors.tertiary()));
        colorField.setIndicatorColor(role(dynamicColors.outlineVariant()));
        colorField.setFocusedIndicatorColor(role(dynamicColors.tertiary()));
        colorField.setLeadingIconColor(role(dynamicColors.tertiary()));
        colorField.setTrailingIconColor(role(dynamicColors.tertiary()));
        content.addView(colorField, fieldParams());
        content.addView(action("读取全部 get / has", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("container=" + hex(colorField.getContainerColor()) + " has=" + colorField.hasContainerColor()
                        + " | content=" + hex(colorField.getContentColor()) + " has=" + colorField.hasContentColor()
                        + " | label=" + hex(colorField.getLabelColor()) + " has=" + colorField.hasLabelColor()
                        + " | placeholder=" + hex(colorField.getPlaceholderColor()) + " has=" + colorField.hasPlaceholderColor()
                        + " | supporting=" + hex(colorField.getSupportingTextColor()) + " has=" + colorField.hasSupportingTextColor()
                        + " | indicator=" + hex(colorField.getIndicatorColor()) + " has=" + colorField.hasIndicatorColor()
                        + " | focusedIndicator=" + hex(colorField.getFocusedIndicatorColor()) + " has=" + colorField.hasFocusedIndicatorColor()
                        + " | leadingIcon=" + hex(colorField.getLeadingIconColor()) + " has=" + colorField.hasLeadingIconColor()
                        + " | trailingIcon=" + hex(colorField.getTrailingIconColor()) + " has=" + colorField.hasTrailingIconColor());
            }
        }), buttonParams());
        content.addView(clearButton("clearContainerColor() → surfaceContainerHighest", colorField, CLEAR_CONTAINER), buttonParams());
        content.addView(clearButton("clearContentColor() → onSurface", colorField, CLEAR_CONTENT), buttonParams());
        content.addView(clearButton("clearLabelColor() → onSurfaceVariant / 聚焦 primary", colorField, CLEAR_LABEL), buttonParams());
        content.addView(clearButton("clearPlaceholderColor() → onSurfaceVariant", colorField, CLEAR_PLACEHOLDER), buttonParams());
        content.addView(clearButton("clearSupportingTextColor() → onSurfaceVariant（错误态仍用 error）", colorField, CLEAR_SUPPORTING), buttonParams());
        content.addView(clearButton("clearIndicatorColor() → 轮廓 outline / 填充 onSurfaceVariant", colorField, CLEAR_INDICATOR), buttonParams());
        content.addView(clearButton("clearFocusedIndicatorColor() → primary（未设时沿用 indicatorColor）", colorField, CLEAR_FOCUSED_INDICATOR), buttonParams());
        content.addView(clearButton("clearLeadingIconColor() → onSurfaceVariant", colorField, CLEAR_LEADING_ICON), buttonParams());
        content.addView(clearButton("clearTrailingIconColor() → onSurfaceVariant", colorField, CLEAR_TRAILING_ICON), buttonParams());
        content.addView(action("setColorScheme(null) → 一次性清除全部 10 组颜色覆盖", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorField.setColorScheme(null);
                toast("setColorScheme(null): hasContainerColor()=" + colorField.hasContainerColor()
                        + "，container=" + hex(colorField.getContainerColor())
                        + "（surfaceContainerHighest），label=" + hex(colorField.getLabelColor())
                        + "（未聚焦 onSurfaceVariant）");
            }
        }), buttonParams());

        setContentView(scrollView);
    }

    private MaterialButton clearButton(String text, final MaterialTextField field, final int target) {
        return action(text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                switch (target) {
                    case CLEAR_CONTAINER:
                        message = "clearContainerColor: " + hex(field.getContainerColor()) + " has=" + field.hasContainerColor();
                        field.clearContainerColor();
                        message += " → " + hex(field.getContainerColor()) + " has=" + field.hasContainerColor()
                                + "（默认 surfaceContainerHighest）";
                        break;
                    case CLEAR_CONTENT:
                        message = "clearContentColor: " + hex(field.getContentColor()) + " has=" + field.hasContentColor();
                        field.clearContentColor();
                        message += " → " + hex(field.getContentColor()) + " has=" + field.hasContentColor()
                                + "（默认 onSurface，禁用态叠加 38% 透明度）";
                        break;
                    case CLEAR_LABEL:
                        message = "clearLabelColor: " + hex(field.getLabelColor()) + " has=" + field.hasLabelColor();
                        field.clearLabelColor();
                        message += " → " + hex(field.getLabelColor()) + " has=" + field.hasLabelColor()
                                + "（默认未聚焦 onSurfaceVariant、聚焦 primary、错误态 error）";
                        break;
                    case CLEAR_PLACEHOLDER:
                        message = "clearPlaceholderColor: " + hex(field.getPlaceholderColor()) + " has=" + field.hasPlaceholderColor();
                        field.clearPlaceholderColor();
                        message += " → " + hex(field.getPlaceholderColor()) + " has=" + field.hasPlaceholderColor()
                                + "（默认 onSurfaceVariant）";
                        break;
                    case CLEAR_SUPPORTING:
                        message = "clearSupportingTextColor: " + hex(field.getSupportingTextColor()) + " has=" + field.hasSupportingTextColor();
                        field.clearSupportingTextColor();
                        message += " → " + hex(field.getSupportingTextColor()) + " has=" + field.hasSupportingTextColor()
                                + "（默认 onSurfaceVariant，错误态优先 error）";
                        break;
                    case CLEAR_INDICATOR:
                        message = "clearIndicatorColor: " + hex(field.getIndicatorColor()) + " has=" + field.hasIndicatorColor();
                        field.clearIndicatorColor();
                        message += " → " + hex(field.getIndicatorColor()) + " has=" + field.hasIndicatorColor()
                                + "（默认轮廓样式 outline、填充样式 onSurfaceVariant）";
                        break;
                    case CLEAR_FOCUSED_INDICATOR:
                        message = "clearFocusedIndicatorColor: " + hex(field.getFocusedIndicatorColor())
                                + " has=" + field.hasFocusedIndicatorColor();
                        field.clearFocusedIndicatorColor();
                        message += " → " + hex(field.getFocusedIndicatorColor())
                                + " has=" + field.hasFocusedIndicatorColor() + "（默认 primary）";
                        break;
                    case CLEAR_LEADING_ICON:
                        message = "clearLeadingIconColor: " + hex(field.getLeadingIconColor()) + " has=" + field.hasLeadingIconColor();
                        field.clearLeadingIconColor();
                        message += " → " + hex(field.getLeadingIconColor()) + " has=" + field.hasLeadingIconColor()
                                + "（默认 onSurfaceVariant）";
                        break;
                    case CLEAR_TRAILING_ICON:
                        message = "clearTrailingIconColor: " + hex(field.getTrailingIconColor()) + " has=" + field.hasTrailingIconColor();
                        field.clearTrailingIconColor();
                        message += " → " + hex(field.getTrailingIconColor()) + " has=" + field.hasTrailingIconColor()
                                + "（默认 onSurfaceVariant）";
                        break;
                    default:
                        message = "未知目标";
                        break;
                }
                toast(message);
            }
        });
    }

    private MaterialButton action(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setStyle(ButtonStyle.OUTLINED);
        button.setOnClickListener(listener);
        return button;
    }

    private int role(DynamicColor color) {
        return color.getArgb(MaterialTextField.publicColorScheme);
    }

    private String hex(int color) {
        return String.format("#%08X", color);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams fieldParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12.0f);
        return params;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
