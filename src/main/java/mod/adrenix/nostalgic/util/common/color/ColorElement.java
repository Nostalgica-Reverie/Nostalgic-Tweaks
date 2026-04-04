package mod.adrenix.nostalgic.util.common.color;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

public enum ColorElement {
    HUE(0.0D, 1.0D),
    SATURATION(0.0D, 1.0D),
    BRIGHTNESS(0.0D, 1.0D),
    RED(0.0D, 255.0D),
    GREEN(0.0D, 255.0D),
    BLUE(0.0D, 255.0D),
    ALPHA(0.0D, 1.0D);

    /* Fields */

    private final double min;
    private final double max;

    /* Constructor */

    ColorElement(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /* Methods */

    /**
     * @return The minimum value allowed by this color element.
     */
    @PublicAPI
    public double getMin() {
        return this.min;
    }

    /**
     * @return The maximum value allowed by this color element.
     */
    @PublicAPI
    public double getMax() {
        return this.max;
    }

    /**
     * @return Whether this element is HSB.
     */
    @PublicAPI
    public boolean isHSB() {
        return switch (this) {
            case HUE, SATURATION, BRIGHTNESS -> true;
            default -> false;
        };
    }

    /**
     * @return Whether this element is RGB.
     */
    @PublicAPI
    public boolean isRGB() {
        return switch (this) {
            case RED, GREEN, BLUE -> true;
            default -> false;
        };
    }

    /**
     * Get the component value from the given color.
     *
     * @param color A {@link Color} to get data from.
     * @return A HSB/RGB/alpha component value.
     */
    @PublicAPI
    public double getValue(Color color) {
        return switch (this) {
            case HUE -> color.getHue();
            case SATURATION -> color.getSaturation();
            case BRIGHTNESS -> color.getBrightness();
            case RED -> color.getRed();
            case GREEN -> color.getGreen();
            case BLUE -> color.getBlue();
            case ALPHA -> color.getFloatAlpha();
        };
    }

    /**
     * @return The interval to move a slider by.
     */
    @PublicAPI
    public double getInterval() {
        return switch (this) {
            case RED, GREEN, BLUE -> 1.0D;
            case HUE, SATURATION, BRIGHTNESS, ALPHA -> 0.01D;
        };
    }

    /**
     * Change the component value of the given color.
     *
     * @param number A {@link Number} value to apply.
     * @param color  The {@link Color} to change.
     */
    @PublicAPI
    public void apply(Number number, Color color) {
        float[] hsb = color.getHSBComponents();
        int[] rgb = color.getIntComponents();

        float floatAlpha = color.getFloatAlpha();
        int intAlpha = color.getAlpha();

        float floatValue = number.floatValue();
        int intValue = number.intValue();

        switch (this) {
            case HUE -> color.set(Color.getHSBColor(floatValue, hsb[1], hsb[2], floatAlpha));
            case SATURATION -> color.set(Color.getHSBColor(hsb[0], floatValue, hsb[2], floatAlpha));
            case BRIGHTNESS -> color.set(Color.getHSBColor(hsb[0], hsb[1], floatValue, floatAlpha));
            case RED -> color.set(new Color(intValue, rgb[1], rgb[2], intAlpha));
            case GREEN -> color.set(new Color(rgb[0], intValue, rgb[2], intAlpha));
            case BLUE -> color.set(new Color(rgb[0], rgb[1], intValue, intAlpha));
            case ALPHA -> color.setAlpha(floatValue);
        }
    }
}
