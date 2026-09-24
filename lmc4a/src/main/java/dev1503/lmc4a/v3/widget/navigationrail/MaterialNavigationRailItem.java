package dev1503.lmc4a.v3.widget.navigationrail;

import android.graphics.drawable.Drawable;

public class MaterialNavigationRailItem {

    private Drawable icon;
    private CharSequence text;

    public MaterialNavigationRailItem() {
    }

    public MaterialNavigationRailItem(Drawable icon, CharSequence text) {
        this.icon = icon;
        this.text = text;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
