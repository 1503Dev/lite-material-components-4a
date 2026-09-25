package dev1503.lmc4a.v3.widget.progressindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class MaterialCircularProgressIndicator extends BaseProgressIndicator {

    private static final float DEFAULT_INDICATOR_SIZE_DP = 42.0f;
    private static final float MIN_SWEEP_DEGREES = 40.0f;
    private static final float MAX_SWEEP_DEGREES = 280.0f;
    private static final float ROTATIONS_PER_CYCLE = 2.0f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private float indicatorSizeDp = DEFAULT_INDICATOR_SIZE_DP;
    private Paint.Cap strokeCap = Paint.Cap.BUTT;

    public MaterialCircularProgressIndicator(Context context) {
        this(context, null);
    }

    public MaterialCircularProgressIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyleHorizontal);
    }

    public MaterialCircularProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIndicatorSizeDp(float indicatorSizeDp) {
        this.indicatorSizeDp = Math.max(0.0f, indicatorSizeDp);
        requestLayout();
        invalidate();
    }

    public float getIndicatorSizeDp() {
        return indicatorSizeDp;
    }

    public void clearIndicatorSizeDp() {
        setIndicatorSizeDp(DEFAULT_INDICATOR_SIZE_DP);
    }

    public void setStrokeCap(Paint.Cap strokeCap) {
        this.strokeCap = strokeCap == null ? Paint.Cap.BUTT : strokeCap;
        invalidate();
    }

    public Paint.Cap getStrokeCap() {
        return strokeCap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) Math.ceil(dp(indicatorSizeDp));
        setMeasuredDimension(
                resolveSize(size, widthMeasureSpec), resolveSize(size, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float stroke = getTrackThicknessPx();
        float diameter = Math.min(getWidth(), getHeight());
        float radius = (diameter - stroke) / 2.0f;
        if (stroke <= 0.0f || radius <= 0.0f) {
            return;
        }

        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setStrokeCap(strokeCap);

        paint.setColor(resolveActiveIndicatorColor());
        if (isIndeterminate()) {
            float phase = getIndicatorPhase();
            float wave = (1.0f - (float) Math.cos(phase * 2.0f * Math.PI)) / 2.0f;
            float sweep = MIN_SWEEP_DEGREES + (MAX_SWEEP_DEGREES - MIN_SWEEP_DEGREES) * wave;
            canvas.save();
            canvas.rotate(phase * 360.0f * ROTATIONS_PER_CYCLE, centerX, centerY);
            canvas.drawArc(rectF, -90.0f, sweep, false, paint);
            canvas.restore();
        } else {
            float fraction = getProgressFraction();
            if (fraction > 0.0f) {
                canvas.drawArc(rectF, -90.0f, 360.0f * fraction, false, paint);
            }
        }
    }
}
