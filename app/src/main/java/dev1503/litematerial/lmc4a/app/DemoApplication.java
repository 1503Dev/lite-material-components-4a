package dev1503.litematerial.lmc4a.app;

import android.app.Application;
import android.content.res.Configuration;

import dev1503.lmc4a.v3.Imc;
import dev1503.lmc4a.v3.color.hct.Hct;
import dev1503.lmc4a.v3.color.scheme.SchemeTonalSpot;

public class DemoApplication extends Application {

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
        Imc.publicColorScheme = new SchemeTonalSpot(Hct.fromInt(0xFF6750A4), isDark, 0.0);
    }
}
