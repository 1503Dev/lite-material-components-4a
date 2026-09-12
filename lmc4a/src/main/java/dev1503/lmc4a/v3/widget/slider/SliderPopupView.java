package dev1503.lmc4a.v3.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

class SliderPopupView extends LinearLayout {

    private static final float CORNER_RADIUS_DP = 8.0f;
    private static final float ARROW_WIDTH_DP = 12.0f;
    private static final float ARROW_HEIGHT_DP = 7.0f;
    private static final float PADDING_H_DP = 8.0f;
    private static final float PADDING_V_DP = 4.0f;
    private static final float TEXT_SIZE_SP = 13.0f;
    private static final float MIN_WIDTH_DP = 28.0f;

    private final TextView progressText;
    private final ArrowView arrowView;
    private int backgroundColor = Color.BLACK;
    private int textColor = Color.WHITE;

    SliderPopupView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        progressText = new TextView(context);
        progressText.setGravity(Gravity.CENTER);
        progressText.setSingleLine(true);
        progressText.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        int padH = (int) (dp(PADDING_H_DP) + 0.5f);
        int padV = (int) (dp(PADDING_V_DP) + 0.5f);
        progressText.setPadding(padH, padV, padH, padV);
        progressText.setMinimumWidth((int) (dp(MIN_WIDTH_DP) + 0.5f));
        progressText.setTextColor(textColor);
        addView(progressText, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        arrowView = new ArrowView(context);
        arrowView.setColor(backgroundColor);
        LayoutParams arrowParams = new LayoutParams(
                (int) (dp(ARROW_WIDTH_DP) + 0.5f),
                (int) (dp(ARROW_HEIGHT_DP) + 0.5f));
        arrowParams.gravity = Gravity.CENTER_HORIZONTAL;
        addView(arrowView, arrowParams);
    }

    void setProgressText(String text) {
        progressText.setText(text);
    }

    void setBackgroundColor2(int color) {
        this.backgroundColor = color;
        updateBackground();
        arrowView.setColor(color);
    }

    void setTextColor2(int color) {
        this.textColor = color;
        progressText.setTextColor(color);
    }

    private void updateBackground() {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(backgroundColor);
        bg.setCornerRadius(dp(CORNER_RADIUS_DP));
        progressText.setBackground(bg);
    }

    private float dp(float valueDp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }

    static class ArrowView extends View {
        private final int mWidth;
        private final int mHeight;
        private final Path mPath;
        private final Paint mPaint;

        ArrowView(Context context) {
            super(context);
            mWidth = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ARROW_WIDTH_DP,
                    context.getResources().getDisplayMetrics()) + 0.5f);
            mHeight = (int) (TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, ARROW_HEIGHT_DP,
                    context.getResources().getDisplayMetrics()) + 0.5f);
            mPath = new Path();
            mPath.moveTo(0, 0);
            mPath.lineTo(mWidth, 0);
            mPath.lineTo(mWidth / 2f, mHeight);
            mPath.close();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
        }

        void setColor(int color) {
            mPaint.setColor(color);
            invalidate();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(mWidth, mHeight);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(mPath, mPaint);
        }
    }
}
