package dev1503.litematerial.lmc4a.app;

import android.app.Application;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.color.hct.Hct;
import dev1503.lmc4a.v3.color.scheme.SchemeTonalSpot;

public class DemoApplication extends Application {

    private static final int DEFAULT_SEED_COLOR = 0xFF6750A4;

    @Override
    public void onCreate() {
        super.onCreate();
        applySchemeFromUiMode();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applySchemeFromUiMode();
    }

    private void applySchemeFromUiMode() {
        int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDark = nightMode == Configuration.UI_MODE_NIGHT_YES;
        int seedColor = resolveSeedColor();
        Lmc.publicColorScheme = new SchemeTonalSpot(Hct.fromInt(seedColor), isDark, 0.0);
    }

    private int resolveSeedColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            return DEFAULT_SEED_COLOR;
        }
        try {
            Object wallpaperManager = getSystemService(Context.WALLPAPER_SERVICE);
            if (wallpaperManager == null) {
                return DEFAULT_SEED_COLOR;
            }
            Object colors = wallpaperManager.getClass().getMethod(
                    "getWallpaperColors", int.class)
                    .invoke(wallpaperManager, WallpaperManager.FLAG_SYSTEM);
            if (colors == null) {
                return DEFAULT_SEED_COLOR;
            }
            Object primaryColor = colors.getClass().getMethod("getPrimaryColor").invoke(colors);
            if (primaryColor == null) {
                return DEFAULT_SEED_COLOR;
            }
            Object argb = primaryColor.getClass().getMethod("toArgb").invoke(primaryColor);
            if (argb == null) {
                return DEFAULT_SEED_COLOR;
            }
            return (int) argb;
        } catch (Exception e) {
            return DEFAULT_SEED_COLOR;
        }
    }
}