package mod.adrenix.nostalgic.client.gui;

public abstract class MouseManager {
    /* Position */

    private static int mouseX = -1;
    private static int mouseY = -1;

    /* Methods */

    /**
     * Set the mouse position during a render pass.
     *
     * @param mouseX The x-coordinate of the mouse cursor.
     * @param mouseY The y-coordinate of the mouse cursor.
     */
    public static void setPosition(int mouseX, int mouseY) {
        MouseManager.mouseX = mouseX;
        MouseManager.mouseY = mouseY;
    }

    /**
     * @return The x-coordinate of the mouse set stored by this manager.
     */
    public static int getX() {
        return MouseManager.mouseX;
    }

    /**
     * @return The y-coordinate of the mouse set stored by this manager.
     */
    public static int getY() {
        return MouseManager.mouseY;
    }
}
