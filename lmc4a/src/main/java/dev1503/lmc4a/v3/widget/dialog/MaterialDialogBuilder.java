package dev1503.lmc4a.v3.widget.dialog;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;

public class MaterialDialogBuilder extends AlertDialog.Builder {

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float DEFAULT_CORNER_RADIUS_DP = 28.0f;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private CharSequence titleText;
    private CharSequence messageText;
    private CharSequence positiveText;
    private CharSequence negativeText;
    private CharSequence neutralText;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;
    private DialogInterface.OnClickListener neutralListener;
    private View customView;
    private boolean cancelable = true;

    private Icon icon;
    private Integer containerColorOverride;
    private Integer titleColorOverride;
    private Integer messageColorOverride;
    private Integer buttonTextColorOverride;
    private Float cornerRadiusDpOverride;

    private static final float IN_STIFFNESS = 600f;
    private static final float IN_DAMPING = 0.85f;
    private static final float OUT_STIFFNESS = 700f;
    private static final float OUT_DAMPING = 0.85f;

    public MaterialDialogBuilder(Context context) {
        super(context);
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public MaterialDialogBuilder setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        this.containerColorOverride = null;
        this.titleColorOverride = null;
        this.messageColorOverride = null;
        this.buttonTextColorOverride = null;
        return this;
    }

    public MaterialDialogBuilder setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }

    public Icon getIcon() {
        return icon;
    }

    public MaterialDialogBuilder clearIcon() {
        this.icon = null;
        return this;
    }

    public MaterialDialogBuilder setContainerColor(int color) {
        this.containerColorOverride = color;
        return this;
    }

    public int getContainerColor() {
        return containerColorOverride != null
                ? containerColorOverride
                : dynamicColors.surfaceContainerHigh().getArgb(colorScheme);
    }

    public MaterialDialogBuilder clearContainerColor() {
        this.containerColorOverride = null;
        return this;
    }

    public MaterialDialogBuilder setTitleColor(int color) {
        this.titleColorOverride = color;
        return this;
    }

    public int getTitleColor() {
        return titleColorOverride != null
                ? titleColorOverride
                : dynamicColors.onSurface().getArgb(colorScheme);
    }

    public MaterialDialogBuilder clearTitleColor() {
        this.titleColorOverride = null;
        return this;
    }

    public MaterialDialogBuilder setMessageColor(int color) {
        this.messageColorOverride = color;
        return this;
    }

    public int getMessageColor() {
        return messageColorOverride != null
                ? messageColorOverride
                : dynamicColors.onSurfaceVariant().getArgb(colorScheme);
    }

    public MaterialDialogBuilder clearMessageColor() {
        this.messageColorOverride = null;
        return this;
    }

    public MaterialDialogBuilder setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadiusDpOverride = Math.max(0f, cornerRadiusDp);
        return this;
    }

    public float getCornerRadiusDp() {
        return cornerRadiusDpOverride != null
                ? cornerRadiusDpOverride
                : DEFAULT_CORNER_RADIUS_DP;
    }

    public MaterialDialogBuilder clearCornerRadiusDp() {
        this.cornerRadiusDpOverride = null;
        return this;
    }

    public MaterialDialogBuilder setButtonTextColor(int color) {
        this.buttonTextColorOverride = color;
        return this;
    }

    public int getButtonTextColor() {
        return buttonTextColorOverride != null
                ? buttonTextColorOverride
                : dynamicColors.primary().getArgb(colorScheme);
    }

    public MaterialDialogBuilder clearButtonTextColor() {
        this.buttonTextColorOverride = null;
        return this;
    }

    @Override
    public MaterialDialogBuilder setTitle(CharSequence title) {
        this.titleText = title;
        return this;
    }

    @Override
    public MaterialDialogBuilder setTitle(int titleId) {
        this.titleText = getContext().getString(titleId);
        return this;
    }

    @Override
    public MaterialDialogBuilder setMessage(CharSequence message) {
        this.messageText = message;
        return this;
    }

    @Override
    public MaterialDialogBuilder setMessage(int messageResId) {
        this.messageText = getContext().getString(messageResId);
        return this;
    }

    @Override
    public MaterialDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveText = text;
        this.positiveListener = listener;
        return this;
    }

    @Override
    public MaterialDialogBuilder setPositiveButton(int textResId, DialogInterface.OnClickListener listener) {
        this.positiveText = getContext().getString(textResId);
        this.positiveListener = listener;
        return this;
    }

    @Override
    public MaterialDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeText = text;
        this.negativeListener = listener;
        return this;
    }

    @Override
    public MaterialDialogBuilder setNegativeButton(int textResId, DialogInterface.OnClickListener listener) {
        this.negativeText = getContext().getString(textResId);
        this.negativeListener = listener;
        return this;
    }

    @Override
    public MaterialDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.neutralText = text;
        this.neutralListener = listener;
        return this;
    }

    @Override
    public MaterialDialogBuilder setNeutralButton(int textResId, DialogInterface.OnClickListener listener) {
        this.neutralText = getContext().getString(textResId);
        this.neutralListener = listener;
        return this;
    }

    @Override
    public MaterialDialogBuilder setView(View view) {
        this.customView = view;
        return this;
    }

    @Override
    public MaterialDialogBuilder setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = create();
        dialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            applyShowAnimation(dialog);
        }
        return dialog;
    }

    @Override
    public AlertDialog create() {
        int bgColor = getContainerColor();
        int titleColor = getTitleColor();
        int messageColor = getMessageColor();

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bg.setCornerRadius(dp(getCornerRadiusDp()));
        }
        container.setBackground(bg);

        if (titleText != null) {
            TextView titleView = new TextView(getContext());
            titleView.setText(titleText);
            titleView.setTextColor(titleColor);
            titleView.setTextSize(24);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            titleParams.leftMargin = dp(24);
            titleParams.rightMargin = dp(24);
            titleParams.topMargin = dp(24);
            titleParams.bottomMargin = dp(16);
            container.addView(titleView, titleParams);
        }

        if (messageText != null) {
            TextView messageView = new TextView(getContext());
            messageView.setText(messageText);
            messageView.setTextColor(messageColor);
            messageView.setTextSize(14);
            LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            msgParams.leftMargin = dp(24);
            msgParams.rightMargin = dp(24);
            msgParams.bottomMargin = dp(24);
            container.addView(messageView, msgParams);
        }

        if (customView != null) {
            LinearLayout.LayoutParams customParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            customParams.leftMargin = dp(24);
            customParams.rightMargin = dp(24);
            container.addView(customView, customParams);
        }

        LinearLayout buttonRow = new LinearLayout(getContext());
        buttonRow.setOrientation(LinearLayout.HORIZONTAL);
        buttonRow.setGravity(android.view.Gravity.END);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.rightMargin = dp(24);
        rowParams.bottomMargin = dp(14);

        MaterialButton neutralBtn = neutralText != null ? createButton(neutralText) : null;
        MaterialButton negativeBtn = negativeText != null ? createButton(negativeText) : null;
        MaterialButton positiveBtn = positiveText != null ? createButton(positiveText) : null;

        if (neutralBtn != null) buttonRow.addView(neutralBtn, buttonParams());
        if (negativeBtn != null) buttonRow.addView(negativeBtn, buttonParams());
        if (positiveBtn != null) buttonRow.addView(positiveBtn, buttonParams());

        if (positiveBtn != null || negativeBtn != null || neutralBtn != null) {
            container.addView(buttonRow, rowParams);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        FrameLayout wrapper = new FrameLayout(getContext());
        FrameLayout.LayoutParams wrapperParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrapperParams.leftMargin = dp(24);
            wrapperParams.rightMargin = dp(24);
        }
        wrapper.addView(container, wrapperParams);

        builder.setView(wrapper);

        if (icon != null) {
            Drawable iconDrawable = icon.resolve(getContext());
            if (iconDrawable != null) {
                builder.setIcon(iconDrawable.mutate());
            }
        }

        if (!cancelable) {
            builder.setCancelable(false);
        }

        final AlertDialog dialog = builder.create();

        if (neutralBtn != null) {
            neutralBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (neutralListener != null) neutralListener.onClick(dialog, 0);
                    dialog.dismiss();
                }
            });
        }
        if (negativeBtn != null) {
            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeListener != null) negativeListener.onClick(dialog, 0);
                    dialog.dismiss();
                }
            });
        }
        if (positiveBtn != null) {
            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveListener != null) positiveListener.onClick(dialog, 0);
                    dialog.dismiss();
                }
            });
        }

        if (dialog.getWindow() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x01000000));
                dialog.getWindow().setElevation(dp(16));
            } else {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.setCancelable(cancelable);

        return dialog;
    }

    private MaterialButton createButton(CharSequence text) {
        MaterialButton btn = new MaterialButton(getContext());
        btn.setText(text);
        btn.setTextSize(14);
        btn.setStyle(ButtonStyle.TEXT);
        if (buttonTextColorOverride != null) {
            btn.setTextColor(buttonTextColorOverride);
        }
        return btn;
    }

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(8);
        return params;
    }

    private void applyShowAnimation(final AlertDialog dialog) {
        final Window window = dialog.getWindow();
        if (window == null) return;

        final View decorView = window.getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(
                new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        decorView.setAlpha(0f);
                        decorView.setScaleX(0.8f);
                        decorView.setScaleY(0.8f);

                        final SpringSimulation springX = new SpringSimulation(IN_STIFFNESS, IN_DAMPING);
                        final SpringSimulation springY = new SpringSimulation(IN_STIFFNESS, IN_DAMPING);
                        final SpringSimulation springAlpha = new SpringSimulation(IN_STIFFNESS, IN_DAMPING);
                        springX.setPosition(0.8f);
                        springX.setTarget(1f);
                        springY.setPosition(0.8f);
                        springY.setTarget(1f);
                        springAlpha.setPosition(0f);
                        springAlpha.setTarget(1f);

                        final long[] lastFrameTime = {0L};
                        final android.view.Choreographer choreographer = android.view.Choreographer.getInstance();

                        final android.view.Choreographer.FrameCallback frameCallback = new android.view.Choreographer.FrameCallback() {
                            @Override
                            public void doFrame(long frameTimeNanos) {
                                long now = System.nanoTime();
                                if (lastFrameTime[0] == 0L) {
                                    lastFrameTime[0] = now;
                                    choreographer.postFrameCallback(this);
                                    return;
                                }
                                float delta = (now - lastFrameTime[0]) / 1_000_000_000f;
                                lastFrameTime[0] = now;
                                delta = Math.min(delta, 0.05f);

                                float scaleX = springX.update(delta);
                                float scaleY = springY.update(delta);
                                float alpha = springAlpha.update(delta);

                                decorView.setScaleX(Math.max(0f, Math.min(1f, scaleX)));
                                decorView.setScaleY(Math.max(0f, Math.min(1f, scaleY)));
                                decorView.setAlpha(Math.max(0f, Math.min(1f, alpha)));

                                if (!springX.isAtRest() || !springAlpha.isAtRest()) {
                                    choreographer.postFrameCallback(this);
                                }
                            }
                        };

                        choreographer.postFrameCallback(frameCallback);
                    }
                });
    }

    public static void applyDismissAnimation(final AlertDialog dialog, final Runnable onEnd) {
        final Window window = dialog.getWindow();
        if (window == null) {
            if (onEnd != null) onEnd.run();
            return;
        }

        final View decorView = window.getDecorView();

        final long[] lastFrameTime = {System.nanoTime()};

        final ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = animation.getAnimatedFraction();
                float easedT = t * t;
                float scale = 1f - easedT * 0.2f;
                float alpha = 1f - easedT;

                decorView.setScaleX(scale);
                decorView.setScaleY(scale);
                decorView.setAlpha(alpha);
            }
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        });
        animator.start();
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
