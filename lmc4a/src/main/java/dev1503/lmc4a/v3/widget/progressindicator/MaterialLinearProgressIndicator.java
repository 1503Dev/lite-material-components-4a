package dev1503.lmc4a.v3.widget.progressindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class MaterialLinearProgressIndicator extends BaseProgressIndicator {

    private static final float FIRST_LINE_START = 0.0f;
    private static final float FIRST_LINE_END = 0.6f;
    private static final float SECOND_LINE_START = 0.45f;
    private static final float SECOND_LINE_END = 1.0f;
    private static final float MAX_FILL_PORTION = 2.0f / 3.0f;
    private static final float SECOND_FILL_PORTION = 0.5f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    public MaterialLinearProgressIndicator(Context context) {
        this(context, null);
    }

    public MaterialLinearProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyleHorizontal);
    }

    public MaterialLinearProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float thickness = getTrackThicknessPx();
        float halfThickness = thickness / 2.0f;
        float centerY = getHeight() / 2.0f;
        float trackRight = getWidth();

        if (thickness <= 0.0f || trackRight <= 0.0f) {
            return;
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(resolveTrackColor());
        rectF.set(0.0f, centerY - halfThickness, trackRight, centerY + halfThickness);
        canvas.drawRect(rectF, paint);

        paint.setColor(resolveActiveIndicatorColor());
        if (isIndeterminate()) {
            float phase = getIndicatorPhase();
            drawLine(canvas, phase, FIRST_LINE_START, FIRST_LINE_END, MAX_FILL_PORTION, trackRight,
                    halfThickness, centerY);
            drawLine(canvas, phase, SECOND_LINE_START, SECOND_LINE_END, SECOND_FILL_PORTION,
                    trackRight, halfThickness, centerY);
        } else {
            float fraction = getProgressFraction();
            if (fraction > 0.0f) {
                rectF.set(0.0f, centerY - halfThickness, trackRight * fraction,
                        centerY + halfThickness);
                canvas.drawRect(rectF, paint);
            }
        }
    }

    private void drawLine(Canvas canvas, float phase, float windowStart, float windowEnd,
            float fillPortion, float trackRight, float halfThickness, float centerY) {
        float local = (phase - windowStart) / (windowEnd - windowStart);
        if (local <= 0.0f || local >= 1.0f) {
            return;
        }
        float fillLength = trackRight * fillPortion;
        float head = ease(local) * (trackRight + fillLength);
        float left = Math.max(0.0f, head - fillLength);
        float right = Math.min(trackRight, head);
        if (right <= left) {
            return;
        }
        rectF.set(left, centerY - halfThickness, right, centerY + halfThickness);
        canvas.drawRect(rectF, paint);
    }

    private static float ease(float fraction) {
        return fraction * fraction * (3.0f - 2.0f * fraction);
    }
}
