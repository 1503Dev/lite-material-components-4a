package dev1503.lmc4a.v3.widget.navigationrail;

import dev1503.lmc4a.Icon;

public class NavigationRailItem {

    private Icon icon;
    private CharSequence text;

    public NavigationRailItem() {
    }

    public NavigationRailItem(Icon icon, CharSequence text) {
        this.icon = icon;
        this.text = text;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
