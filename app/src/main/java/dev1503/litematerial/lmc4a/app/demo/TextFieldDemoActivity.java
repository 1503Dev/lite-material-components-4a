package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
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

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.textfield.MaterialTextField;
import dev1503.lmc4a.v3.widget.textfield.TextFieldStyle;

public class TextFieldDemoActivity extends Activity {

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

        content.addView(caption("FILLED — 标签 + 占位 + 辅助文字"), captionParams());
        MaterialTextField filled = new MaterialTextField(this);
        filled.setLabel("姓名");
        filled.setPlaceholderText("请输入姓名");
        filled.setSupportingText("用于在个人资料中显示");
        content.addView(filled, fieldParams());

        content.addView(caption("OUTLINED — 标签 + 字数统计"), captionParams());
        MaterialTextField outlined = new MaterialTextField(this);
        outlined.setTextFieldStyle(TextFieldStyle.OUTLINED);
        outlined.setLabel("简介");
        outlined.setCounterEnabled(true);
        outlined.setCounterMaxLength(20);
        outlined.setSingleLine(true);
        content.addView(outlined, fieldParams());

        content.addView(caption("错误状态 — 点按钮校验"), captionParams());
        final MaterialTextField errorField = new MaterialTextField(this);
        errorField.setTextFieldStyle(TextFieldStyle.OUTLINED);
        errorField.setLabel("邮箱");
        errorField.setSupportingText("需要包含 @");
        errorField.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        errorField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        content.addView(errorField, fieldParams());

        MaterialButton validate = new MaterialButton(this);
        validate.setText("校验");
        validate.setButtonStyle(ButtonStyle.OUTLINED);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text = errorField.getText();
                if (text == null || text.toString().indexOf('@') < 0) {
                    errorField.setError("邮箱格式不正确");
                } else {
                    errorField.setError(null);
                }
            }
        });
        content.addView(validate, fieldParams());

        content.addView(caption("图标 — 前置图标 + 后置图标（切换密码可见性）"), captionParams());
        final MaterialTextField password = new MaterialTextField(this);
        password.setLabel("密码");
        password.setInputType(android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setLeadingIcon(getResources().getDrawable(android.R.drawable.ic_lock_lock));
        password.setTrailingIcon(getResources().getDrawable(android.R.drawable.ic_menu_view));
        password.setOnTrailingIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hidden = password.getTransformationMethod() instanceof PasswordTransformationMethod;
                password.setTransformationMethod(hidden ? null : PasswordTransformationMethod.getInstance());
            }
        });
        content.addView(password, fieldParams());

        content.addView(caption("禁用"), captionParams());
        MaterialTextField disabled = new MaterialTextField(this);
        disabled.setLabel("只读");
        disabled.setText("不可编辑的内容");
        disabled.setSupportingText("禁用态使用 38% onSurface");
        disabled.setEnabled(false);
        content.addView(disabled, fieldParams());

        MaterialTextField disabledOutlined = new MaterialTextField(this);
        disabledOutlined.setTextFieldStyle(TextFieldStyle.OUTLINED);
        disabledOutlined.setLabel("只读（描边）");
        disabledOutlined.setText("不可编辑的内容");
        disabledOutlined.setEnabled(false);
        content.addView(disabledOutlined, fieldParams());

        content.addView(caption("多行"), captionParams());
        MaterialTextField multiline = new MaterialTextField(this);
        multiline.setLabel("备注");
        multiline.setSupportingText("容器会随高度拉伸");
        multiline.setSingleLine(false);
        multiline.setMinLines(3);
        multiline.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
        content.addView(multiline, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(scrollView);
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

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}
