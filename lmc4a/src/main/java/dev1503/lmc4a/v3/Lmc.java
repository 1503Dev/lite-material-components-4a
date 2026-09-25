package dev1503.lmc4a.v3;

import dev1503.lmc4a.v3.color.dynamiccolor.DynamicScheme;
import dev1503.lmc4a.v3.color.hct.Hct;
import dev1503.lmc4a.v3.color.scheme.SchemeTonalSpot;

public class Lmc {
    public static DynamicScheme publicColorScheme = new SchemeTonalSpot(Hct.fromInt(0xFF6750A4), false, 0.0);
}
