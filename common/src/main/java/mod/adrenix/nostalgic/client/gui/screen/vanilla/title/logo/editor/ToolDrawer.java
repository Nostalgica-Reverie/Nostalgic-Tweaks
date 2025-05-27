package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import mod.adrenix.nostalgic.util.common.array.CycleIndex;

public enum ToolDrawer
{
    OPEN,
    HALF,
    CLOSED;

    /* Static */

    private static final CycleIndex CYCLE_INDEX = new CycleIndex(ToolDrawer.values(), false);

    /* Helpers */

    /**
     * Get the current tool drawer.
     *
     * @return The {@link ToolDrawer} currently in use.
     */
    public static ToolDrawer get()
    {
        return ToolDrawer.values()[CYCLE_INDEX.get()];
    }

    /**
     * Move the tool drawer upwards.
     */
    public static void up()
    {
        CYCLE_INDEX.forward();
    }

    /**
     * Move the tool drawer downwards.
     */
    public static void down()
    {
        CYCLE_INDEX.backward();
    }

    /**
     * @return Whether the drawer is fully open.
     */
    public static boolean isOpen()
    {
        return get().equals(OPEN);
    }

    /**
     * @return Whether the drawer is fully closed.
     */
    public static boolean isClosed()
    {
        return get().equals(CLOSED);
    }

    /**
     * Reset the tool drawer back to the default position.
     */
    public static void reset()
    {
        CYCLE_INDEX.setStartIndex(0);
        CYCLE_INDEX.restart();
    }
}
