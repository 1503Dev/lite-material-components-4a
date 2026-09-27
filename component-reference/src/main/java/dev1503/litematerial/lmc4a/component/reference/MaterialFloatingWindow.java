package dev1503.litematerial.lmc4a.component.reference;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MaterialFloatingWindow extends Dialog {

    private static final float DEFAULT_BLUR_RADIUS_DP = 24.0f;
    private static final float DEFAULT_CORNER_RADIUS_DP = 28.0f;
    private static final float DEFAULT_MARGIN_DP = 24.0f;
    private static final float SCRIM_ALPHA_BLUR = 0.45f;
    private static final float SCRIM_ALPHA_FALLBACK = 0.82f;
    private static final int DEFAULT_SURFACE_COLOR = 0xFFFFFFFF;

    private final DragPanel panel;
    private final GradientDrawable backgroundDrawable = new GradientDrawable();

    private float blurRadiusDp = DEFAULT_BLUR_RADIUS_DP;
    private float cornerRadiusDp = DEFAULT_CORNER_RADIUS_DP;
    private int surfaceColor = DEFAULT_SURFACE_COLOR;
    private boolean blurEnabled = true;
    private boolean windowConfigured;

    public MaterialFloatingWindow(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        surfaceColor = resolveSurfaceColor();
        panel = new DragPanel(context);
        backgroundDrawable.setCornerRadius(dp(cornerRadiusDp));
        backgroundDrawable.setColor(resolveScrimColor());
        panel.setBackground(backgroundDrawable);
        panel.setElevation(dp(8.0f));
        panel.setClipToOutline(true);
        panel.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dp(cornerRadiusDp));
            }
        });
        setContentView(panel);
    }

    public MaterialFloatingWindow(Context context, AttributeSet attrs) {
        this(context);
    }

    public MaterialFloatingWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    public void setBlurRadiusDp(float blurRadiusDp) {
        this.blurRadiusDp = Math.max(0f, blurRadiusDp);
        refreshWindow();
    }

    public float getBlurRadiusDp() {
        return blurRadiusDp;
    }

    public void setBlurEnabled(boolean blurEnabled) {
        this.blurEnabled = blurEnabled;
        refreshWindow();
    }

    public boolean isBlurEnabled() {
        return blurEnabled;
    }

    public void setCornerRadiusDp(float cornerRadiusDp) {
        this.cornerRadiusDp = Math.max(0f, cornerRadiusDp);
        backgroundDrawable.setCornerRadius(dp(cornerRadiusDp));
        panel.setClipToOutline(cornerRadiusDp > 0f);
        panel.invalidateOutline();
        panel.invalidate();
    }

    public float getCornerRadiusDp() {
        return cornerRadiusDp;
    }

    public void setSurfaceColor(int color) {
        this.surfaceColor = color;
        backgroundDrawable.setColor(resolveScrimColor());
        panel.invalidate();
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        configureWindow();
        super.show();
    }

    public void hide() {
        if (isShowing()) {
            super.hide();
        }
    }

    public FrameLayout getContentView() {
        return panel;
    }

    private void configureWindow() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFormat(PixelFormat.TRANSLUCENT);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.TOP | Gravity.START;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = (int) (dp(DEFAULT_MARGIN_DP) + 0.5f);
        params.y = (int) (dp(DEFAULT_MARGIN_DP * 4.0f) + 0.5f);
        params.dimAmount = 0f;
        params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        windowConfigured = true;
        applyBlur(params);
        window.setAttributes(params);
    }

    private void applyBlur(WindowManager.LayoutParams params) {
        boolean blur = supportsBlur();
        if (blur) {
            params.flags |= WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            try {
                params.setBlurBehindRadius((int) (dp(blurRadiusDp) + 0.5f));
            } catch (NoSuchMethodError ignored) {
                params.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            }
        } else {
            params.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
            try {
                params.setBlurBehindRadius(0);
            } catch (NoSuchMethodError ignored) {
            }
        }
        backgroundDrawable.setColor(resolveScrimColor());
        panel.invalidate();
    }

    private void refreshWindow() {
        Window window = getWindow();
        if (window == null || !windowConfigured || !isShowing()) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        applyBlur(params);
        window.setAttributes(params);
    }

    private boolean supportsBlur() {
        return blurEnabled && blurRadiusDp > 0f
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    private int resolveScrimColor() {
        float scrimAlpha = supportsBlur() ? SCRIM_ALPHA_BLUR : SCRIM_ALPHA_FALLBACK;
        return applyAlpha(surfaceColor, scrimAlpha);
    }

    private int resolveSurfaceColor() {
        TypedValue value = new TypedValue();
        if (getContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorSurface, value, true)) {
            return value.data;
        }
        return DEFAULT_SURFACE_COLOR;
    }

    private static int applyAlpha(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * Math.max(0f, Math.min(1f, alphaFraction)));
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp,
                getContext().getResources().getDisplayMetrics());
    }

    public Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private final class DragPanel extends FrameLayout {

        private final float touchSlop;
        private float downRawX;
        private float downRawY;
        private int downWindowX;
        private int downWindowY;
        private boolean dragging;

        DragPanel(Context context) {
            super(context);
            touchSlop = android.view.ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downRawX = ev.getRawX();
                    downRawY = ev.getRawY();
                    Window window = getWindow();
                    if (window != null) {
                        downWindowX = window.getAttributes().x;
                        downWindowY = window.getAttributes().y;
                    }
                    dragging = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!dragging && isDragDistanceReached(ev)) {
                        dragging = true;
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return dragging;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    if (dragging) {
                        updateWindowPosition(
                                downWindowX + (int) (ev.getRawX() - downRawX),
                                downWindowY + (int) (ev.getRawY() - downRawY));
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    dragging = false;
                    return true;
                default:
                    return super.onTouchEvent(ev);
            }
        }

        private boolean isDragDistanceReached(MotionEvent ev) {
            float dx = ev.getRawX() - downRawX;
            float dy = ev.getRawY() - downRawY;
            return dx * dx + dy * dy > touchSlop * touchSlop;
        }

        private void updateWindowPosition(int x, int y) {
            Window window = getWindow();
            if (window == null) {
                return;
            }
            WindowManager.LayoutParams params = window.getAttributes();
            int maxX = Math.max(0, getResources().getDisplayMetrics().widthPixels - getWidth());
            int maxY = Math.max(0, getResources().getDisplayMetrics().heightPixels - getHeight());
            params.x = Math.max(0, Math.min(x, maxX));
            params.y = Math.max(0, Math.min(y, maxY));
            window.setAttributes(params);
        }
    }
}
