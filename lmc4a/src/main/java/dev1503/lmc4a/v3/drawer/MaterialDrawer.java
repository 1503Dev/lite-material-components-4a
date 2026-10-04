package dev1503.lmc4a.v3.drawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialDrawer extends Dialog {

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float DEFAULT_MAX_WIDTH_DP = 360.0f;
    private static final float DEFAULT_WIDTH_SCREEN_FRACTION = 0.8f;
    private static final float ELEVATION_DP = 8.0f;
    private static final float SCRIM_ALPHA = 0.32f;
    private static final long ANIM_DURATION_MS = 246L;
    private static final float FLING_VELOCITY_THRESHOLD = 1200f;
    private static final float DRAG_CLOSE_FRACTION = 0.5f;

    private final Activity activity;
    private final FrameLayout rootView;
    private final DrawerPanel panelView;
    private final View scrimView;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();

    private View contentView;
    private DrawerOrientation orientation = DrawerOrientation.LEFT;
    private boolean opened;
    private boolean dragEnabled = true;
    private boolean dragToCancelEnabled = true;
    private boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;
    private boolean dismissed;
    private int touchSlop;
    private ValueAnimator animator;

    private DynamicScheme colorScheme = publicColorScheme;
    private Integer containerColorOverride;
    private Integer scrimColorOverride;
    private Float drawerWidthDpOverride;

    public MaterialDrawer(Activity activity) {
        this(activity, null);
    }

    public MaterialDrawer(Activity activity, View contentView) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.activity = activity;
        touchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();

        Window window = getWindow();
        if (window != null) {
            window.setFormat(PixelFormat.TRANSLUCENT);
        }

        rootView = new FrameLayout(activity);

        scrimView = new View(activity);
        scrimView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelable && canceledOnTouchOutside) {
                    cancel();
                }
            }
        });
        rootView.addView(scrimView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        panelView = new DrawerPanel(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            panelView.setElevation(dp(ELEVATION_DP));
        }
        rootView.addView(panelView, new FrameLayout.LayoutParams(
                panelWidthPx(), ViewGroup.LayoutParams.MATCH_PARENT, panelGravity()));

        super.setContentView(rootView);
        refreshColors();
        setContentView(contentView);
    }

    @Override
    public void show() {
        open(orientation);
    }

    @Override
    public void setContentView(View view) {
        if (contentView == view) {
            return;
        }
        if (contentView != null) {
            panelView.removeView(contentView);
        }
        contentView = view;
        if (view != null) {
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            panelView.addView(view, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public View getContentView() {
        return contentView;
    }

    public MaterialDrawer open(DrawerOrientation orientation) {
        if (orientation == null) {
            return this;
        }
        boolean switched = opened && this.orientation != orientation;
        this.orientation = orientation;
        dismissed = false;
        updatePanelLayout();

        if (!isShowing()) {
            scrimView.setAlpha(0f);
            panelView.setTranslationX(hiddenOffset());
            Window window = getWindow();
            if (window != null) {
                window.getDecorView();
                window.setFormat(PixelFormat.TRANSLUCENT);
            }
            try {
                super.show();
            } catch (RuntimeException ignored) {
                return this;
            }
            applyWindowLayout();
            requestInsets();
        } else if (switched) {
            scrimView.setAlpha(0f);
            panelView.setTranslationX(hiddenOffset());
        }

        opened = true;
        animateTo(true);
        return this;
    }

    @Override
    public void dismiss() {
        if (dismissed) {
            return;
        }
        dismissed = true;
        opened = false;
        if (!isShowing()) {
            super.dismiss();
            return;
        }
        animateTo(false);
    }

    public boolean isOpen() {
        return opened;
    }

    public DrawerOrientation getOrientation() {
        return orientation;
    }

    public MaterialDrawer setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
        return this;
    }

    public boolean isDragEnabled() {
        return dragEnabled;
    }

    public MaterialDrawer setDragToCancelEnabled(boolean dragToCancelEnabled) {
        this.dragToCancelEnabled = dragToCancelEnabled;
        return this;
    }

    public boolean isDragToCancelEnabled() {
        return dragToCancelEnabled;
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        this.cancelable = flag;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        if (cancel && !cancelable) {
            setCancelable(true);
        }
        super.setCanceledOnTouchOutside(cancel);
        this.canceledOnTouchOutside = cancel;
    }

    public boolean isCanceledOnTouchOutside() {
        return canceledOnTouchOutside;
    }

    public MaterialDrawer setDrawerWidthDp(float drawerWidthDp) {
        drawerWidthDpOverride = drawerWidthDp > 0f ? drawerWidthDp : null;
        applyPanelSize();
        return this;
    }

    public float getDrawerWidthDp() {
        if (drawerWidthDpOverride != null) {
            return drawerWidthDpOverride;
        }
        float density = activity.getResources().getDisplayMetrics().density;
        float screenWidthDp = activity.getResources().getDisplayMetrics().widthPixels / density;
        return Math.min(screenWidthDp * DEFAULT_WIDTH_SCREEN_FRACTION, DEFAULT_MAX_WIDTH_DP);
    }

    public MaterialDrawer clearDrawerWidthDp() {
        drawerWidthDpOverride = null;
        applyPanelSize();
        return this;
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public MaterialDrawer setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme != null ? colorScheme : publicColorScheme;
        this.containerColorOverride = null;
        this.scrimColorOverride = null;
        refreshColors();
        return this;
    }

    public MaterialDrawer setContainerColor(int color) {
        containerColorOverride = color;
        refreshColors();
        return this;
    }

    public int getContainerColor() {
        return containerColorOverride != null
                ? containerColorOverride
                : dynamicColors.surfaceContainer().getArgb(colorScheme);
    }

    public MaterialDrawer clearContainerColor() {
        containerColorOverride = null;
        refreshColors();
        return this;
    }

    public MaterialDrawer setScrimColor(int color) {
        scrimColorOverride = color;
        refreshColors();
        return this;
    }

    public int getScrimColor() {
        if (scrimColorOverride != null) {
            return scrimColorOverride;
        }
        return applyAlphaFraction(dynamicColors.scrim().getArgb(colorScheme), SCRIM_ALPHA);
    }

    public MaterialDrawer clearScrimColor() {
        scrimColorOverride = null;
        refreshColors();
        return this;
    }

    private void animateTo(final boolean open) {
        cancelAnimator();

        final float target = open ? 0f : hiddenOffset();
        final float start = panelView.getTranslationX();
        final float targetAlpha = open ? 1f : 0f;
        final float startAlpha = scrimView.getAlpha();
        if (start == target) {
            finishAnimation(open);
            return;
        }

        final float travel = target - start;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(ANIM_DURATION_MS);
        valueAnimator.setInterpolator(resolveInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = animation.getAnimatedFraction();
                panelView.setTranslationX(start + travel * t);
                scrimView.setAlpha(startAlpha + (targetAlpha - startAlpha) * t);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animator != valueAnimator) {
                    return;
                }
                animator = null;
                finishAnimation(open);
            }
        });
        animator = valueAnimator;
        valueAnimator.start();
    }

    private void cancelAnimator() {
        if (animator != null) {
            ValueAnimator running = animator;
            animator = null;
            running.cancel();
        }
    }

    private void finishAnimation(boolean open) {
        if (open) {
            panelView.setTranslationX(0f);
            scrimView.setAlpha(1f);
            return;
        }
        if (isShowing()) {
            try {
                super.dismiss();
            } catch (RuntimeException ignored) {
            }
        }
        scrimView.setAlpha(0f);
        panelView.setTranslationX(hiddenOffset());
    }

    private void applyWindowLayout() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFormat(PixelFormat.TRANSLUCENT);
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        applyEdgeToEdge(window);
    }

    private void applyEdgeToEdge(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        } else {
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams
                    .LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            window.getDecorView().setOnApplyWindowInsetsListener(new WindowInsetsHandler());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private final class WindowInsetsHandler implements View.OnApplyWindowInsetsListener {

        @Override
        public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
            applyInsets(insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetBottom());
            return insets.consumeSystemWindowInsets();
        }
    }

    private void updatePanelLayout() {
        ViewGroup.LayoutParams current = panelView.getLayoutParams();
        if (!(current instanceof FrameLayout.LayoutParams)) {
            return;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) current;
        params.width = panelWidthPx();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = panelGravity();
        panelView.setLayoutParams(params);
    }

    private void applyPanelSize() {
        updatePanelLayout();
        if (isShowing()) {
            panelView.requestLayout();
        }
    }

    private int panelWidthPx() {
        return dp(getDrawerWidthDp());
    }

    private int panelGravity() {
        return (panelAtRight() ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP;
    }

    private float hiddenOffset() {
        int width = panelWidthPx();
        if (panelView != null && panelView.getWidth() > 0) {
            width = panelView.getWidth();
        }
        return panelAtRight() ? width : -width;
    }

    private boolean panelAtRight() {
        if (orientation != DrawerOrientation.RIGHT) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.getResources().getConfiguration().getLayoutDirection()
                    == View.LAYOUT_DIRECTION_LTR;
        }
        return true;
    }

    private void requestInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Window window = getWindow();
            if (window != null) {
                window.getDecorView().requestApplyInsets();
            }
            return;
        }
        ViewTreeObserver observer = rootView.getViewTreeObserver();
        if (!observer.isAlive()) {
            return;
        }
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver current = rootView.getViewTreeObserver();
                if (!current.isAlive()) {
                    return;
                }
                current.removeOnGlobalLayoutListener(this);
                Rect visible = new Rect();
                rootView.getWindowVisibleDisplayFrame(visible);
                int[] location = new int[2];
                rootView.getLocationOnScreen(location);
                int top = Math.max(0, visible.top - location[1]);
                int bottom = Math.max(0, location[1] + rootView.getHeight() - visible.bottom);
                applyInsets(top, bottom);
            }
        });
    }

    private void applyInsets(int topPx, int bottomPx) {
        int top = Math.max(0, topPx);
        int bottom = Math.max(0, bottomPx);
        if (panelView.getPaddingTop() == top && panelView.getPaddingBottom() == bottom) {
            return;
        }
        panelView.setPadding(panelView.getPaddingLeft(), top,
                panelView.getPaddingRight(), bottom);
    }

    private static Interpolator resolveInterpolator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
        }
        return new DecelerateInterpolator();
    }

    private static float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    private final class DrawerPanel extends FrameLayout {

        private float downRawX;
        private float downRawY;
        private float startTranslationX;
        private boolean dragging;
        private float velocityX;
        private long lastEventTime;

        DrawerPanel(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    trackDown(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateVelocity(ev);
                    if (isDragStart(ev)) {
                        beginDrag();
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
                case MotionEvent.ACTION_DOWN:
                    trackDown(ev);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    updateVelocity(ev);
                    if (!dragging && isDragStart(ev)) {
                        beginDrag();
                    }
                    if (dragging) {
                        setTranslationX(clampOffset(
                                startTranslationX + (ev.getRawX() - downRawX)));
                        updateScrimForOffset();
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (dragging) {
                        dragging = false;
                        settleDrag();
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return super.onTouchEvent(ev);
        }

        private void trackDown(MotionEvent ev) {
            downRawX = ev.getRawX();
            downRawY = ev.getRawY();
            startTranslationX = getTranslationX();
            dragging = false;
            velocityX = 0f;
            lastEventTime = ev.getEventTime();
        }

        private boolean isDragStart(MotionEvent ev) {
            if (!dragEnabled) {
                return false;
            }
            float dx = ev.getRawX() - downRawX;
            float dy = ev.getRawY() - downRawY;
            return Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy);
        }

        private void beginDrag() {
            dragging = true;
            cancelAnimator();
            startTranslationX = getTranslationX();
        }

        private void updateVelocity(MotionEvent ev) {
            long eventTime = ev.getEventTime();
            float dx = ev.getRawX() - downRawX;
            float dtSec = (eventTime - lastEventTime) / 1000f;
            if (dtSec > 0f && Math.abs(dx) > touchSlop) {
                velocityX = dx / dtSec;
            }
            lastEventTime = eventTime;
        }

        private float clampOffset(float offset) {
            float hidden = hiddenOffset();
            if (hidden < 0f) {
                return Math.max(hidden, Math.min(0f, offset));
            }
            return Math.min(hidden, Math.max(0f, offset));
        }

        private void updateScrimForOffset() {
            float hidden = Math.abs(hiddenOffset());
            if (hidden <= 0f) {
                return;
            }
            scrimView.setAlpha(clamp01(1f - Math.abs(getTranslationX()) / hidden));
        }

        private void settleDrag() {
            float hidden = Math.abs(hiddenOffset());
            if (hidden <= 0f) {
                return;
            }
            float fraction = clamp01(Math.abs(getTranslationX()) / hidden);
            float closingVelocity = velocityX * (panelAtRight() ? 1f : -1f);
            boolean close;
            if (Math.abs(closingVelocity) > FLING_VELOCITY_THRESHOLD) {
                close = closingVelocity > 0f;
            } else {
                close = fraction > DRAG_CLOSE_FRACTION;
            }
            if (close && cancelable && dragToCancelEnabled && dragEnabled) {
                dismiss();
            } else {
                opened = true;
                animateTo(true);
            }
        }
    }

    private void refreshColors() {
        if (panelView != null) {
            panelView.setBackgroundColor(getContainerColor());
        }
        if (scrimView != null) {
            scrimView.setBackgroundColor(getScrimColor());
        }
    }

    private static int applyAlphaFraction(int argb, float alphaFraction) {
        float fraction = Math.max(0f, Math.min(1f, alphaFraction));
        int alpha = (int) (Color.alpha(argb) * fraction);
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private int dp(float valueDp) {
        return (int) (valueDp * activity.getResources().getDisplayMetrics().density + 0.5f);
    }
}
