package dev1503.lmc4a.v3e.widget.button;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import dev1503.lmc4a.v3e.anim.SpringSimulation;

public class MaterialButton extends dev1503.lmc4a.v3.widget.button.MaterialButton {

    private static final float PRESSED_CORNER_RADIUS_DP = 8.0f;
    private static final float CORNER_STIFFNESS = 500.0f;
    private static final float CORNER_DAMPING = 0.9f;
    private static final long CORNER_DURATION_MS = 400L;

    private float restingCornerRadius;
    private boolean hasPressedCornerRadiusDp;
    private float pressedCornerRadiusDp = PRESSED_CORNER_RADIUS_DP;
    private float cornerTarget;
    private SpringSimulation cornerSpring;
    private ValueAnimator cornerAnimator;
    private long lastFrameTime;
    private boolean initialized;

    public MaterialButton(Context context) {
        this(context, null);
    }

    public MaterialButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public MaterialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        restingCornerRadius = cornerRadius;
        initialized = true;
    }

    @Override
    public void setCornerRadiusDp(float cornerRadiusDp) {
        super.setCornerRadiusDp(cornerRadiusDp);
        restingCornerRadius = cornerRadius;
        if (initialized && isPressed()) {
            applyCornerRadius(resolvePressedCornerRadius());
        }
    }

    public void setPressedCornerRadiusDp(float pressedCornerRadiusDp) {
        this.pressedCornerRadiusDp = pressedCornerRadiusDp;
        this.hasPressedCornerRadiusDp = true;
        if (initialized && isPressed()) {
            animateCornerRadius(resolvePressedCornerRadius());
        }
    }

    public float getPressedCornerRadiusDp() {
        return hasPressedCornerRadiusDp ? pressedCornerRadiusDp : PRESSED_CORNER_RADIUS_DP;
    }

    public boolean hasPressedCornerRadiusDp() {
        return hasPressedCornerRadiusDp;
    }

    public void clearPressedCornerRadiusDp() {
        this.hasPressedCornerRadiusDp = false;
        if (initialized && isPressed()) {
            animateCornerRadius(resolvePressedCornerRadius());
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (!initialized) {
            return;
        }
        animateCornerRadius(isPressed() ? resolvePressedCornerRadius() : restingCornerRadius);
    }

    private float resolvePressedCornerRadius() {
        float pressed = hasPressedCornerRadiusDp
                ? dp(pressedCornerRadiusDp)
                : dp(PRESSED_CORNER_RADIUS_DP);
        return Math.min(restingCornerRadius, pressed);
    }

    private void animateCornerRadius(float target) {
        if (cornerAnimator != null && cornerAnimator.isRunning() && cornerTarget == target) {
            return;
        }
        if (getWindowToken() == null) {
            if (cornerAnimator != null) {
                cornerAnimator.cancel();
                cornerAnimator = null;
            }
            cornerSpring = null;
            cornerTarget = target;
            applyCornerRadius(target);
            return;
        }
        if (cornerAnimator != null) {
            cornerAnimator.cancel();
            cornerAnimator = null;
        }
        cornerTarget = target;
        cornerSpring = new SpringSimulation(CORNER_STIFFNESS, CORNER_DAMPING);
        cornerSpring.setPosition(cornerRadius);
        cornerSpring.setTarget(target);
        lastFrameTime = System.nanoTime();
        cornerAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        cornerAnimator.setDuration(CORNER_DURATION_MS);
        cornerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long now = System.nanoTime();
                float delta = (now - lastFrameTime) / 1_000_000_000f;
                lastFrameTime = now;
                delta = Math.min(delta, 0.05f);

                applyCornerRadius(cornerSpring.update(delta));

                if (cornerSpring.isAtRest()) {
                    animation.cancel();
                }
            }
        });
        cornerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (cornerAnimator == animation) {
                    applyCornerRadius(target);
                    cornerAnimator = null;
                    cornerSpring = null;
                }
            }
        });
        cornerAnimator.start();
    }

    private void applyCornerRadius(float radius) {
        cornerRadius = radius;
        contentDrawable.setCornerRadius(radius);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            maskDrawable.setCornerRadius(radius);
            invalidateOutline();
        }
        invalidate();
    }
}
