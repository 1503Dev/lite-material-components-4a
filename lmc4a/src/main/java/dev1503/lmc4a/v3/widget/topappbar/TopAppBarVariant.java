package dev1503.lmc4a.v3.widget.topappbar;

public enum TopAppBarVariant {

    SMALL(64.0f, 22.0f, false, false),
    CENTER_ALIGNED(64.0f, 22.0f, true, false),
    MEDIUM(112.0f, 24.0f, false, true),
    LARGE(152.0f, 28.0f, false, true);

    private final float containerHeightDp;
    private final float titleTextSizeSp;
    private final boolean titleCentered;
    private final boolean twoRow;

    TopAppBarVariant(
            float containerHeightDp, float titleTextSizeSp, boolean titleCentered, boolean twoRow) {
        this.containerHeightDp = containerHeightDp;
        this.titleTextSizeSp = titleTextSizeSp;
        this.titleCentered = titleCentered;
        this.twoRow = twoRow;
    }

    public float getContainerHeightDp() {
        return containerHeightDp;
    }

    public float getTitleTextSizeSp() {
        return titleTextSizeSp;
    }

    public boolean isTitleCentered() {
        return titleCentered;
    }

    public boolean isTwoRow() {
        return twoRow;
    }
}
