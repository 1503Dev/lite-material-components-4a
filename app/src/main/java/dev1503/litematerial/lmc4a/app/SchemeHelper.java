package dev1503.litematerial.lmc4a.app;

import android.app.Activity;

import dev1503.lmc4a.v3.Imc;

public final class SchemeHelper {

    private SchemeHelper() {
    }

    public static int backgroundColor() {
        return Imc.publicColorScheme.getBackground();
    }

    public static int onBackgroundColor() {
        return Imc.publicColorScheme.getOnBackground();
    }

    public static void applyWindowBackground(Activity activity) {
        activity.getWindow().getDecorView().setBackgroundColor(backgroundColor());
    }
}
