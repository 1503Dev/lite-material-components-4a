package dev1503.lmc4a.v3.bottomsheet;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.anim.SpringSimulation;
import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.dynamiccolor.MaterialDynamicColors;

public class MaterialBottomSheet extends Dialog {

    public enum SheetState {
        COLLAPSED, DRAGGING, EXPANDED, HALF_EXPANDED
    }

    public interface OnStateChangedListener {
        void onStateChanged(SheetState newState);
    }

    public static DynamicScheme publicColorScheme = Lmc.publicColorScheme;

    private static final float DEFAULT_CORNER_RADIUS_DP = 28.0f;
    private static final float MAX_WIDTH_DP = 720.0f;
    private static final float HANDLE_WIDTH_DP = 32.0f;
    private static final float HANDLE_HEIGHT_DP = 4.0f;
    private static final float HANDLE_TOP_MARGIN_DP = 12.0f;
    private static final float CONTENT_LEFT_RIGHT_MARGIN_DP = 24.0f;
    private static final float CONTENT_TOP_MARGIN_DP = 12.0f;
    private static final float CONTENT_BOTTOM_MARGIN_DP = 24.0f;
    private static final float HANDLE_ALPHA = 0.4f;
    private static final float OUT_DURATION_MS = 200.0f;
    private static final float STATE_ANIM_DURATION_MS = 240.0f;
    private static final float SHEET_SPRING_STIFFNESS = 400f;
    private static final float SHEET_SPRING_DAMPING = 1.0f;
    private static final float SPRING_FRAME_DURATION_MS = 1000f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    private static final float MIN_PEEK_HEIGHT_DP = 96.0f;
    private static final float MAX_PEEK_AUTO_DP = 256.0f;
    private static final float FLING_VELOCITY_THRESHOLD = 1200f;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final FrameLayout container;
    private final SheetContainer sheetPanel;
    private View scrimView;
    private GradientDrawable backgroundDrawable;
    private View contentView;
    private boolean dismissed;
    private boolean fullscreenMode;
    private boolean dragEnabled = true;
    private boolean dragToCancelEnabled = true;
    private boolean cancelable = true;
    private boolean canceledOnTouchOutside = true;
    private SheetState sheetState = SheetState.COLLAPSED;
    private int touchSlop;
    private int peekHeight = PEEK_HEIGHT_AUTO;
    private float halfExpandedRatio = 0.5f;
    private int expandedOffset = 0;
    private int topInset = 0;
    private boolean fitToContents = false;
    private float lastVelocityY;
    private long lastEventTime;
    private OnStateChangedListener onStateChangedListener;
    private boolean animatedIn;
    private Integer containerColorOverride;
    private Integer scrimColorOverride;
    private Float cornerRadiusDpOverride;

    public MaterialBottomSheet(Context context) {
        this(context, null);
    }

    public MaterialBottomSheet(Context context, View contentView) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contentView = contentView;
        Window window = getWindow();
        if (window != null) {
            window.setFormat(PixelFormat.TRANSLUCENT);
        }
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        container = new FrameLayout(context);
        sheetPanel = new SheetContainer(context);
        sheetPanel.setOrientation(LinearLayout.VERTICAL);
        init();
    }

    private final class SheetContainer extends LinearLayout {
        private float downRawY;
        private boolean downOnHandle;
        private boolean dragging;
        private int startOffset;

        SheetContainer(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downRawY = ev.getRawY();
                    downOnHandle = ev.getY() <= dp(28.0f);
                    dragging = false;
                    lastVelocityY = 0f;
                    lastEventTime = ev.getEventTime();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateVelocity(ev);
                    if (dragEnabled && Math.abs(ev.getRawY() - downRawY) > touchSlop) {
                        beginDrag();
                        return true;
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    updateVelocity(ev);
                    if (dragEnabled && !dragging
                            && Math.abs(ev.getRawY() - downRawY) > touchSlop) {
                        beginDrag();
                    }
                    if (dragging) {
                        sheetPanel.setTranslationY(clampOffset(startOffset
                                + (int) (ev.getRawY() - downRawY)));
                        updateCornerRadiusForOffset(currentOffset());
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (dragging) {
                        dragging = false;
                        settleDrag(visibleHeight(), !downOnHandle);
                        return true;
                    }
                    if (!dragging && downOnHandle
                            && ev.getActionMasked() == MotionEvent.ACTION_UP) {
                        toggleExpandState();
                        return true;
                    }
                    break;
            }
            return false;
        }

        private void beginDrag() {
            dragging = true;
            setSheetStateInternal(SheetState.DRAGGING);
            startOffset = currentOffset();
        }

        private void updateVelocity(MotionEvent ev) {
            long eventTime = ev.getEventTime();
            float dyPx = ev.getRawY() - downRawY;
            float dtSec = (eventTime - lastEventTime) / 1000f;
            if (dtSec > 0f && Math.abs(dyPx) > touchSlop) {
                lastVelocityY = dyPx / dtSec;
            }
            lastEventTime = eventTime;
        }
    }

    private void toggleExpandState() {
        if (sheetState == SheetState.COLLAPSED) {
            setSheetState(SheetState.EXPANDED);
        } else if (isHalfExpandedAllowed() && sheetState == SheetState.EXPANDED) {
            setSheetState(SheetState.HALF_EXPANDED);
        } else {
            setSheetState(SheetState.COLLAPSED);
        }
    }

    private void init() {
        GradientDrawable bg = new GradientDrawable();
        backgroundDrawable = bg;
        sheetPanel.setBackground(bg);

        View dragHandle = new View(getContext());
        GradientDrawable handleBg = new GradientDrawable();
        handleBg.setColor(applyAlphaFraction(
                dynamicColors.onSurfaceVariant().getArgb(colorScheme), HANDLE_ALPHA));
        handleBg.setCornerRadius(dp(HANDLE_HEIGHT_DP / 2.0f));
        dragHandle.setBackground(handleBg);
        dragHandle.setClickable(true);
        dragHandle.setOnClickListener(v -> toggleExpandState());
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(
                dp(HANDLE_WIDTH_DP), dp(HANDLE_HEIGHT_DP));
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.topMargin = dp(HANDLE_TOP_MARGIN_DP);
        sheetPanel.addView(dragHandle, handleParams);

        scrimView = new View(getContext());
        scrimView.setBackgroundColor(Color.TRANSPARENT);
        scrimView.setOnClickListener(v -> {
            if (cancelable && canceledOnTouchOutside) {
                cancel();
            }
        });
        container.addView(scrimView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (contentView != null) {
            addContent(contentView);
        }

        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                Math.min(getContext().getResources().getDisplayMetrics().widthPixels,
                        dp(MAX_WIDTH_DP)),
                panelHeightPx());
        panelParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        container.addView(sheetPanel, panelParams);

        super.setContentView(container);
        refreshBackgroundColors();
    }

    private void updatePanelHeight() {
        ViewGroup.LayoutParams params = sheetPanel.getLayoutParams();
        if (params == null) {
            return;
        }
        int height = panelHeightPx();
        if (params.height != height) {
            params.height = height;
            sheetPanel.setLayoutParams(params);
        }
    }

    private int panelHeightPx() {
        if (fitToContents) {
            return ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        return Math.max(1, expandedHeight());
    }

    private void applyWindowLayout() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setFormat(PixelFormat.TRANSLUCENT);
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams params = window.getAttributes();
        if (scrimColorOverride == null) {
            params.dimAmount = resolveDimAmount();
            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        } else {
            params.dimAmount = 0f;
            params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        }
        window.setAttributes(params);
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

    private float resolveDimAmount() {
        TypedValue value = new TypedValue();
        if (getContext().getTheme().resolveAttribute(
                android.R.attr.backgroundDimAmount, value, true)) {
            return value.getFloat();
        }
        return 0.6f;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private final class WindowInsetsHandler implements View.OnApplyWindowInsetsListener {

        @Override
        public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
            int bottom = insets.getSystemWindowInsetBottom();
            if (sheetPanel.getPaddingBottom() != bottom) {
                sheetPanel.setPadding(0, 0, 0, bottom);
            }
            int top = insets.getSystemWindowInsetTop();
            if (topInset != top) {
                topInset = top;
                if (isShowing() && sheetState != SheetState.DRAGGING) {
                    animateOffsetTo(offsetForState(sheetState), (long) STATE_ANIM_DURATION_MS);
                }
            }
            return insets.consumeSystemWindowInsets();
        }
    }

    public DynamicScheme getColorScheme() {
        return colorScheme;
    }

    public MaterialBottomSheet setColorScheme(DynamicScheme colorScheme) {
        this.colorScheme = colorScheme;
        this.containerColorOverride = null;
        this.scrimColorOverride = null;
        this.cornerRadiusDpOverride = null;
        refreshBackgroundColors();
        applyWindowLayout();
        return this;
    }

    public MaterialBottomSheet clearContainerColor() {
        containerColorOverride = null;
        refreshBackgroundColors();
        return this;
    }

    @Override
    public void dismiss() {
        if (dismissed) {
            return;
        }
        dismissed = true;
        if (!isShowing()) {
            super.dismiss();
            return;
        }
        final float startOffset = sheetPanel.getTranslationY();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration((long) OUT_DURATION_MS);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = animation.getAnimatedFraction();
                float easedT = t * t;
                sheetPanel.setTranslationY(startOffset
                        + (sheetHeight() - startOffset) * easedT);
                sheetPanel.setAlpha(1f - easedT);
            }
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                MaterialBottomSheet.super.dismiss();
            }
        });
        animator.start();
    }

    public MaterialBottomSheet setContainerColor(int color) {
        containerColorOverride = color;
        refreshBackgroundColors();
        return this;
    }

    public int getContainerColor() {
        return containerColorOverride != null
                ? containerColorOverride
                : dynamicColors.surfaceContainerLow().getArgb(colorScheme);
    }

    public MaterialBottomSheet setScrimColor(int color) {
        scrimColorOverride = color;
        refreshBackgroundColors();
        applyWindowLayout();
        return this;
    }

    public int getScrimColor() {
        if (scrimColorOverride != null) {
            return scrimColorOverride;
        }
        return applyAlphaFraction(
                dynamicColors.scrim().getArgb(colorScheme), resolveDimAmount());
    }

    public MaterialBottomSheet clearScrimColor() {
        scrimColorOverride = null;
        refreshBackgroundColors();
        applyWindowLayout();
        return this;
    }

    public MaterialBottomSheet setCornerRadiusDp(float cornerRadiusDp) {
        cornerRadiusDpOverride = Math.max(0f, cornerRadiusDp);
        refreshBackgroundColors();
        return this;
    }

    public float getCornerRadiusDp() {
        return cornerRadiusDpOverride != null
                ? cornerRadiusDpOverride
                : DEFAULT_CORNER_RADIUS_DP;
    }

    public MaterialBottomSheet clearCornerRadiusDp() {
        cornerRadiusDpOverride = null;
        refreshBackgroundColors();
        return this;
    }

    public MaterialBottomSheet setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
        return this;
    }

    public boolean isDragEnabled() {
        return dragEnabled;
    }

    public MaterialBottomSheet setDragToCancelEnabled(boolean dragToCancelEnabled) {
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

    public boolean isFullscreenMode() {
        return fullscreenMode;
    }

    public MaterialBottomSheet setFullscreenMode(boolean fullscreenMode) {
        this.fullscreenMode = fullscreenMode;
        updatePanelHeight();
        if (!isHalfExpandedAllowed() && sheetState == SheetState.HALF_EXPANDED) {
            setSheetState(SheetState.EXPANDED);
        }
        if (sheetState != SheetState.DRAGGING && isShowing()) {
            animateOffsetTo(offsetForState(sheetState), (long) STATE_ANIM_DURATION_MS);
        }
        return this;
    }

    public MaterialBottomSheet setPeekHeight(int peekHeightPx) {
        this.peekHeight = peekHeightPx;
        if (isShowing() && sheetState != SheetState.DRAGGING) {
            animateOffsetTo(offsetForState(sheetState), (long) STATE_ANIM_DURATION_MS);
        }
        return this;
    }

    public int getPeekHeight() {
        return peekHeight;
    }

    public MaterialBottomSheet setHalfExpandedRatio(float ratio) {
        this.halfExpandedRatio = Math.max(0f, ratio);
        return this;
    }

    public float getHalfExpandedRatio() {
        return halfExpandedRatio;
    }

    public MaterialBottomSheet setExpandedOffset(int offsetPx) {
        this.expandedOffset = Math.max(0, offsetPx);
        updatePanelHeight();
        return this;
    }

    public int getExpandedOffset() {
        return expandedOffset;
    }

    public MaterialBottomSheet setFitToContents(boolean fitToContents) {
        this.fitToContents = fitToContents;
        updatePanelHeight();
        if (fitToContents && sheetState == SheetState.HALF_EXPANDED) {
            setSheetState(SheetState.EXPANDED);
        }
        return this;
    }

    public boolean isFitToContents() {
        return fitToContents;
    }

    public MaterialBottomSheet setOnStateChangedListener(OnStateChangedListener listener) {
        this.onStateChangedListener = listener;
        return this;
    }

    public SheetState getSheetState() {
        return sheetState;
    }

    public MaterialBottomSheet setSheetState(SheetState state) {
        if (state == null || state == SheetState.DRAGGING) {
            return this;
        }
        if (state == SheetState.HALF_EXPANDED && !isHalfExpandedAllowed()) {
            return this;
        }
        setSheetStateInternal(state);
        animateOffsetTo(offsetForState(state), (long) STATE_ANIM_DURATION_MS);
        return this;
    }

    private void setSheetStateInternal(SheetState state) {
        if (sheetState == state) {
            return;
        }
        sheetState = state;
        updateCornerRadiusForOffset(currentOffset());
        if (onStateChangedListener != null) {
            onStateChangedListener.onStateChanged(state);
        }
    }

    private int sheetHeight() {
        int height = sheetPanel.getHeight();
        if (height > 0) {
            return height;
        }
        return Math.max(1, expandedHeight());
    }

    private int targetHeightFor(SheetState state) {
        int panelHeight = sheetHeight();
        switch (state) {
            case EXPANDED:
                return Math.min(panelHeight, expandedHeight());
            case HALF_EXPANDED:
                return Math.min(panelHeight, halfExpandedHeight());
            case COLLAPSED:
            default:
                return Math.min(panelHeight, peekHeightPx());
        }
    }

    private int offsetForState(SheetState state) {
        return Math.max(0, sheetHeight() - targetHeightFor(state));
    }

    private int clampOffset(int offset) {
        return Math.max(0, Math.min(offset, sheetHeight()));
    }

    private int currentOffset() {
        return (int) (sheetHeight() - visibleHeight());
    }

    private int visibleHeight() {
        return (int) (sheetHeight() - sheetPanel.getTranslationY());
    }

    private void refreshBackgroundColors() {
        if (backgroundDrawable != null) {
            backgroundDrawable.setColor(resolveContainerColor());
            updateCornerRadiusForOffset(currentOffset());
        }
        if (scrimView != null) {
            scrimView.setBackgroundColor(scrimColorOverride != null
                    ? scrimColorOverride : Color.TRANSPARENT);
        }
    }

    private int resolveContainerColor() {
        if (containerColorOverride != null) {
            return containerColorOverride;
        }
        return dynamicColors.surfaceContainerLow().getArgb(colorScheme);
    }

    private void updateCornerRadiusForOffset(int offset) {
        if (backgroundDrawable == null) {
            return;
        }
        int full = sheetHeight();
        int visible = Math.max(0, Math.min(full, full - offset));
        float progress = 0f;
        if (fullscreenMode) {
            int peek = targetHeightFor(SheetState.COLLAPSED);
            int expanded = targetHeightFor(SheetState.EXPANDED);
            if (expanded > peek) {
                progress = (visible - peek) / (float) (expanded - peek);
            }
        }
        progress = Math.max(0f, Math.min(1f, progress));
        float top = dp(getCornerRadiusDp()) * (1f - progress);
        backgroundDrawable.setCornerRadii(new float[]{
                top, top,
                top, top,
                0, 0, 0, 0});
    }

    public View getContentView() {
        return contentView;
    }

    @Override
    public void setContentView(View view) {
        if (contentView != null) {
            sheetPanel.removeView(contentView);
        }
        contentView = view;
        if (view != null) {
            addContent(view);
        }
    }

    private void addContent(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dp(CONTENT_LEFT_RIGHT_MARGIN_DP);
        params.rightMargin = dp(CONTENT_LEFT_RIGHT_MARGIN_DP);
        params.topMargin = dp(CONTENT_TOP_MARGIN_DP);
        params.bottomMargin = dp(CONTENT_BOTTOM_MARGIN_DP);
        sheetPanel.addView(view, params);
    }

    @Override
    public void show() {
        dismissed = false;
        animatedIn = false;
        sheetState = SheetState.COLLAPSED;
        updatePanelHeight();
        Window window = getWindow();
        if (window != null) {
            window.getDecorView();
            window.setFormat(PixelFormat.TRANSLUCENT);
        }
        super.show();
        applyWindowLayout();
        animateIn();
    }

    private void settleDrag(int height, boolean allowDismiss) {
        int collapsedHeight = targetHeightFor(SheetState.COLLAPSED);
        if (allowDismiss && cancelable && dragToCancelEnabled && dragEnabled
                && height <= (int) (collapsedHeight * 0.55f)) {
            cancel();
            return;
        }
        SheetState target = nearestAnchorState(height, lastVelocityY);
        setSheetStateInternal(target);
        animateOffsetTo(offsetForState(target), (long) STATE_ANIM_DURATION_MS);
    }

    private SheetState nearestAnchorState(int height, float velocityY) {
        int peek = targetHeightFor(SheetState.COLLAPSED);
        int expanded = targetHeightFor(SheetState.EXPANDED);
        int half = targetHeightFor(SheetState.HALF_EXPANDED);

        if (isHalfExpandedAllowed()) {
            int halfBand = Math.max(1, (expanded - half) / 6);
            if (Math.abs(height - half) <= halfBand) {
                return SheetState.HALF_EXPANDED;
            }
        }

        int dPeek = Math.abs(height - peek);
        int dExp = Math.abs(height - expanded);
        int dHalf = Math.abs(height - half);

        SheetState nearest;
        if (isHalfExpandedAllowed() && dHalf <= dPeek && dHalf <= dExp) {
            nearest = SheetState.HALF_EXPANDED;
        } else if (dExp <= dPeek) {
            nearest = SheetState.EXPANDED;
        } else {
            nearest = SheetState.COLLAPSED;
        }

        if (Math.abs(velocityY) <= FLING_VELOCITY_THRESHOLD) {
            return nearest;
        }
        if (velocityY < 0f) {
            if (nearest == SheetState.COLLAPSED) {
                return isHalfExpandedAllowed() ? SheetState.HALF_EXPANDED : SheetState.EXPANDED;
            }
            if (nearest == SheetState.HALF_EXPANDED) {
                return SheetState.EXPANDED;
            }
            return nearest;
        }
        if (nearest == SheetState.EXPANDED) {
            return isHalfExpandedAllowed() ? SheetState.HALF_EXPANDED : SheetState.COLLAPSED;
        }
        if (nearest == SheetState.HALF_EXPANDED) {
            return SheetState.COLLAPSED;
        }
        return nearest;
    }

    private void animateOffsetTo(int targetOffset, long durationMs) {
        final int startOffset = currentOffset();
        if (startOffset == targetOffset) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(startOffset, targetOffset);
        animator.setDuration(durationMs);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int offset = (Integer) animation.getAnimatedValue();
            sheetPanel.setTranslationY(offset);
            updateCornerRadiusForOffset(offset);
        });
        animator.start();
    }

    private int parentHeight() {
        return realDisplayMetrics().heightPixels;
    }

    private int parentWidth() {
        return realDisplayMetrics().widthPixels;
    }

    private android.util.DisplayMetrics realDisplayMetrics() {
        android.util.DisplayMetrics dm = new android.util.DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager wm = (WindowManager) getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            if (wm != null) {
                wm.getDefaultDisplay().getRealMetrics(dm);
                return dm;
            }
        }
        return getContext().getResources().getDisplayMetrics();
    }

    private int expandedHeight() {
        if (fullscreenMode) {
            return parentHeight() - expandedOffset - topInset;
        }
        return (int) (parentHeight() * 0.5f);
    }

    private int halfExpandedHeight() {
        return (int) (parentHeight() * halfExpandedRatio);
    }

    private int peekHeightPx() {
        if (peekHeight == PEEK_HEIGHT_AUTO) {
            int keyline = parentHeight() - (int) (parentWidth() * 9f / 16f);
            int auto = Math.min(keyline, dp(MAX_PEEK_AUTO_DP));
            return Math.max(auto, dp(MIN_PEEK_HEIGHT_DP));
        }
        return Math.max(0, peekHeight);
    }

    private boolean isHalfExpandedAllowed() {
        return !fitToContents && fullscreenMode;
    }

    private void animateIn() {
        if (animatedIn) {
            return;
        }
        sheetPanel.setAlpha(0f);
        sheetPanel.setTranslationY(parentHeight());
        updateCornerRadiusForOffset(offsetForState(SheetState.COLLAPSED));

        ViewTreeObserver.OnGlobalLayoutListener layoutListener =
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (dismissed) {
                            return;
                        }
                        ViewTreeObserver observer = sheetPanel.getViewTreeObserver();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            observer.removeOnGlobalLayoutListener(this);
                        } else {
                            observer.removeGlobalOnLayoutListener(this);
                        }
                        final int startOffset = sheetHeight();
                        final int targetOffset = offsetForState(SheetState.COLLAPSED);
                        sheetPanel.setTranslationY(startOffset);
                        updateCornerRadiusForOffset(startOffset);

                        final SpringSimulation spring = new SpringSimulation(
                                SHEET_SPRING_STIFFNESS, SHEET_SPRING_DAMPING);
                        spring.setPosition(startOffset);
                        spring.setTarget(targetOffset);
                        final float travel = Math.max(1f, startOffset - targetOffset);
                        final long[] lastFrameTime = new long[]{System.nanoTime()};

                        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                        animator.setDuration((long) SPRING_FRAME_DURATION_MS);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                long now = System.nanoTime();
                                float delta = (now - lastFrameTime[0]) / 1_000_000_000f;
                                lastFrameTime[0] = now;
                                delta = Math.min(delta, 0.05f);

                                float offset = spring.update(delta);
                                sheetPanel.setTranslationY(offset);
                                updateCornerRadiusForOffset((int) offset);

                                float progress = (startOffset - offset) / travel;
                                sheetPanel.setAlpha(Math.max(0f, Math.min(1f, progress)));

                                if (spring.isAtRest()) {
                                    animation.cancel();
                                }
                            }
                        });
                        animator.addListener(new android.animation.AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                sheetPanel.setTranslationY(targetOffset);
                                updateCornerRadiusForOffset(targetOffset);
                                sheetPanel.setAlpha(1f);
                            }
                        });
                        animator.start();
                        animatedIn = true;
                    }
                };
        sheetPanel.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        sheetPanel.requestLayout();
    }

    private static int applyAlphaFraction(int argb, float alphaFraction) {
        int alpha = (int) (Color.alpha(argb) * alphaFraction);
        return (argb & 0x00ffffff) | (alpha << 24);
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
