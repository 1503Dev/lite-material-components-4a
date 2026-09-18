package dev1503.lmc4a;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class Icon {
    public static final int TYPE_BITMAP = 0;
    public static final int TYPE_DRAWABLE = 1;
    public static final int TYPE_RES_ID = 2;
    public static final int DEFAULT_SIZE_DP = 24;
    private int type;

    private Bitmap bitmap;
    private Drawable drawable;
    private int resId;

    public Icon(Drawable drawable) {
        setDrawable(drawable);
    }

    public Icon(Bitmap bitmap) {
        setBitmap(bitmap);
    }

    public Icon(int resId) {
        setResId(resId);
    }

    public Icon setBitmap(Bitmap bitmap) {
        this.type = TYPE_BITMAP;
        this.bitmap = bitmap;
        this.drawable = null;
        this.resId = 0;
        return this;
    }

    public Icon setDrawable(Drawable drawable) {
        this.type = TYPE_DRAWABLE;
        this.drawable = drawable;
        this.bitmap = null;
        this.resId = 0;
        return this;
    }

    public Icon setResId(int resId) {
        this.type = TYPE_RES_ID;
        this.resId = resId;
        this.bitmap = null;
        this.drawable = null;
        return this;
    }

    public boolean isBitmap() {
        return type == TYPE_BITMAP && bitmap != null;
    }

    public boolean isDrawable() {
        return type == TYPE_DRAWABLE && drawable != null;
    }

    public boolean isResId() {
        return type == TYPE_RES_ID && resId != 0;
    }

    public int getType() {
        return type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public int getResId() {
        return resId;
    }

    public Drawable resolve(Context context) {
        switch (type) {
            case TYPE_RES_ID:
                if (Build.VERSION.SDK_INT <= 25) {
                    return context.getResources().getDrawable(resId);
                } else {
                    return context.getDrawable(resId);
                }
            case TYPE_DRAWABLE:
                return drawable;
            case TYPE_BITMAP:
                float density = context.getResources().getDisplayMetrics().density;
                int size = Math.round(DEFAULT_SIZE_DP * density);
                return new BitmapDrawable(context.getResources(), scaleTo(bitmap, size));
        }
        return null;
    }

    private static Bitmap scaleTo(Bitmap src, int size) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (width == size && height == size) {
            return src;
        }
        float scale = Math.min((float) size / width, (float) size / height);
        int newWidth = Math.max(1, Math.round(width * scale));
        int newHeight = Math.max(1, Math.round(height * scale));
        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
    }
}
