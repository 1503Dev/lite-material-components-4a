package dev1503.lmc4a.v3.widget.bottomsheet;

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

import dev1503.lmc4a.v3.Imc;
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

    public static DynamicScheme publicColorScheme = Imc.publicColorScheme;

    private static final float CORNER_RADIUS_DP = 28.0f;
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
    private static final float SHEET_SPRING_DAMPING = 0.7f;
    private static final float SPRING_FRAME_DURATION_MS = 1000f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    private static final float MIN_PEEK_HEIGHT_DP = 96.0f;
    private static final float MAX_PEEK_AUTO_DP = 256.0f;
    private static final float FLING_VELOCITY_THRESHOLD = 1200f;

    private DynamicScheme colorScheme = publicColorScheme;
    private final MaterialDynamicColors dynamicColors = new MaterialDynamicColors();
    private final FrameLayout container;
    private final SheetContainer sheetPanel;
    private GradientDrawable backgroundDrawable;
    private View contentView;
    private boolean dismissed;
    private boolean fullscreenMode;
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

    public MaterialBottomSheet(Context context) {
        this(context, null);
    }

    public MaterialBottomSheet(Context context, View contentView) {
        // 使用全屏、无标题、透明背景的窗口主题：DeviceDefault 之类的应用主题会让
        // Dialog 变成带标题栏的浮动窗口，既撑不满屏幕也会给面板加上额外的窗口内边距。
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.contentView = contentView;
        Window window = getWindow();
        if (window != null) {
            // 窗口面的格式要在 decor 创建之前确定：API 16 上仅靠主题的 windowIsTranslucent
            // 并不会让图层带上 alpha，遮罩 alpha 被忽略而变成纯黑，身后的 Activity 也会被
            // 系统当作被完全遮挡而停止绘制。
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
                    if (Math.abs(ev.getRawY() - downRawY) > touchSlop) {
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
                    if (!dragging && Math.abs(ev.getRawY() - downRawY) > touchSlop) {
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
        int bgColor = dynamicColors.surfaceContainerHigh().getArgb(colorScheme);
        int onSurfaceVariantColor = dynamicColors.onSurfaceVariant().getArgb(colorScheme);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        backgroundDrawable = bg;
        bg.setCornerRadii(new float[]{
                dp(CORNER_RADIUS_DP), dp(CORNER_RADIUS_DP),
                dp(CORNER_RADIUS_DP), dp(CORNER_RADIUS_DP),
                0, 0, 0, 0});
        sheetPanel.setBackground(bg);

        View dragHandle = new View(getContext());
        GradientDrawable handleBg = new GradientDrawable();
        handleBg.setColor(applyAlphaFraction(onSurfaceVariantColor, HANDLE_ALPHA));
        handleBg.setCornerRadius(dp(HANDLE_HEIGHT_DP / 2.0f));
        dragHandle.setBackground(handleBg);
        dragHandle.setClickable(true);
        dragHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpandState();
            }
        });
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(
                dp(HANDLE_WIDTH_DP), dp(HANDLE_HEIGHT_DP));
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.topMargin = dp(HANDLE_TOP_MARGIN_DP);
        sheetPanel.addView(dragHandle, handleParams);

        if (contentView != null) {
            addContent(contentView);
        }

        // 遮罩改由窗口自身的调光实现（FLAG_DIM_BEHIND），容器只负责把面板之外的
        // 点击视为“点击遮罩”并关闭
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                Math.min(getContext().getResources().getDisplayMetrics().widthPixels,
                        dp(MAX_WIDTH_DP)),
                panelHeightPx());
        panelParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        container.addView(sheetPanel, panelParams);

        setContentView(container);
    }

    /**
     * 面板高度：非 fitToContents 时占满整个 sheet（默认半屏、全屏模式为屏幕高度减去顶部
     * 偏移），这样 收起/半展开/展开 之间才有真实位移，拖动也才有可拖动的余量；
     * fitToContents 时面板贴合内容高度。
     */
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
        // 这些属性必须等 decor 创建（show）之后再设置，否则会被主题中的窗口默认值覆盖：
        // DeviceDefault 之类的主题会给对话框套上不透明背景与阴影内边距，窗口也会退化成
        // 浮动尺寸，遮罩便无法铺满屏幕。
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // API 16 上仅靠主题的 windowIsTranslucent 不足以让窗口面带上 alpha 通道，
        // 遮罩会变成不透光的纯黑并把身后的 Activity 一起挡掉，这里显式指定格式。
        window.setFormat(PixelFormat.TRANSLUCENT);
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams params = window.getAttributes();
        // 遮罩直接用系统 Dialog 的默认黑色调光：FLAG_DIM_BEHIND + 主题里的
        // backgroundDimAmount，不再自绘遮罩视图，也不再自定义调光值
        params.dimAmount = resolveDimAmount();
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
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
            // 面板贴屏幕底部，但内容不能被导航栏/手势条盖住；全屏展开时也不能顶到
            // 状态栏下面（否则顶部手柄会被状态栏窗口抢走点击）
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

    public boolean isFullscreenMode() {
        return fullscreenMode;
    }

    public void setFullscreenMode(boolean fullscreenMode) {
        this.fullscreenMode = fullscreenMode;
        updatePanelHeight();
        if (!isHalfExpandedAllowed() && sheetState == SheetState.HALF_EXPANDED) {
            setSheetState(SheetState.EXPANDED);
        }
        if (sheetState != SheetState.DRAGGING && isShowing()) {
            animateOffsetTo(offsetForState(sheetState), (long) STATE_ANIM_DURATION_MS);
        }
    }

    public void setPeekHeight(int peekHeightPx) {
        this.peekHeight = peekHeightPx;
        if (isShowing() && sheetState != SheetState.DRAGGING) {
            animateOffsetTo(offsetForState(sheetState), (long) STATE_ANIM_DURATION_MS);
        }
    }

    public int getPeekHeight() {
        return peekHeight;
    }

    public void setHalfExpandedRatio(float ratio) {
        this.halfExpandedRatio = Math.max(0f, ratio);
    }

    public float getHalfExpandedRatio() {
        return halfExpandedRatio;
    }

    public void setExpandedOffset(int offsetPx) {
        this.expandedOffset = Math.max(0, offsetPx);
        updatePanelHeight();
    }

    public int getExpandedOffset() {
        return expandedOffset;
    }

    public void setFitToContents(boolean fitToContents) {
        this.fitToContents = fitToContents;
        updatePanelHeight();
        if (fitToContents && sheetState == SheetState.HALF_EXPANDED) {
            setSheetState(SheetState.EXPANDED);
        }
    }

    public boolean isFitToContents() {
        return fitToContents;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.onStateChangedListener = listener;
    }

    public SheetState getSheetState() {
        return sheetState;
    }

    public void setSheetState(SheetState state) {
        if (state == null || state == SheetState.DRAGGING) {
            return;
        }
        if (state == SheetState.HALF_EXPANDED && !isHalfExpandedAllowed()) {
            return;
        }
        setSheetStateInternal(state);
        animateOffsetTo(offsetForState(state), (long) STATE_ANIM_DURATION_MS);
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

    /**
     * 面板真实高度。面板在容器中底部对齐，因此所有位移都必须以它为基准：
     * 原先用屏幕高度推算位移，内容比半屏短的面板会被整体平移到屏幕下方而看不见。
     */
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

    private void updateCornerRadiusForOffset(int offset) {
        if (backgroundDrawable == null) {
            return;
        }
        // 圆角随展开收起成直角只在全屏模式下发生：那时 sheet 顶边会贴到窗口顶部。
        // 非全屏模式（半屏 sheet）以及贴合内容的小 sheet 无论 收起/半展开/展开 都保持圆角。
        int full = sheetHeight();
        int visible = Math.max(0, Math.min(full, full - offset));
        float progress = 0f;
        if (fullscreenMode) {
            // 用 收起(peek)/展开 两个锚点的可见高度插值，避免位移还在 sheet 高度附近时
            // 被误判成“已完全展开”而把刚打开时的圆角算成直角
            int peek = targetHeightFor(SheetState.COLLAPSED);
            int expanded = targetHeightFor(SheetState.EXPANDED);
            if (expanded > peek) {
                progress = (visible - peek) / (float) (expanded - peek);
            }
        }
        progress = Math.max(0f, Math.min(1f, progress));
        float top = dp(CORNER_RADIUS_DP) * (1f - progress);
        backgroundDrawable.setCornerRadii(new float[]{
                top, top,
                top, top,
                0, 0, 0, 0});
    }

    public View getContent() {
        return contentView;
    }

    public void setContent(View view) {
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
            // 先安装 decor（主题里的窗口默认值会在这一步写入 attrs），再覆盖窗口面格式，
            // 这样 super.show() 创建窗口面时才会带上 alpha 通道。API 16 上这一步不能省：
            // 否则图层被标记为不透明，调光遮罩会渲染成纯黑，身后的 Activity 也会被
            // 系统当作完全遮挡而停止绘制。
            window.getDecorView();
            window.setFormat(PixelFormat.TRANSLUCENT);
        }
        super.show();
        applyWindowLayout();
        animateIn();
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

    private void settleDrag(int height, boolean allowDismiss) {
        int collapsedHeight = targetHeightFor(SheetState.COLLAPSED);
        if (allowDismiss && height <= (int) (collapsedHeight * 0.55f)) {
            dismiss();
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
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (Integer) animation.getAnimatedValue();
                sheetPanel.setTranslationY(offset);
                updateCornerRadiusForOffset(offset);
            }
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
            // 全屏展开时让出状态栏高度，避免顶部手柄落在状态栏窗口下面
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
        // 先把收起锚点的圆角摆好，弹出过程中顶角就不会出现直角
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
                        // 面板此时已完成测量，收起位移按它的真实高度计算
                        final int startOffset = sheetHeight();
                        final int targetOffset = offsetForState(SheetState.COLLAPSED);
                        sheetPanel.setTranslationY(startOffset);
                        updateCornerRadiusForOffset(startOffset);

                        // 弹出动画用弹簧模拟驱动位移，透明度按弹簧进度插值
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