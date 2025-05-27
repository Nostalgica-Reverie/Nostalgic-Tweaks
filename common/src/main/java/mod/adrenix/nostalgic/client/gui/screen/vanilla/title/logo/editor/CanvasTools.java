package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import mod.adrenix.nostalgic.util.common.array.CycleIndex;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.client.gui.screens.Screen;

public enum CanvasTools
{
    DRAW(Icons.PAINTBRUSH, Lang.Logo.DRAW_TOOL_UNDERLINE, Lang.Logo.DRAW_TOOL, Lang.Logo.DRAW_TOOL_TOOLTIP, true),
    PICK(Icons.FILLED_COLOR_PICKER, Lang.Logo.PICK_TOOL_UNDERLINE, Lang.Logo.PICK_TOOL, Lang.Logo.PICK_TOOL_TOOLTIP, false),
    SELECT(Icons.SELECTOR, Lang.Logo.SELECT_TOOL_UNDERLINE, Lang.Logo.SELECT_TOOL, Lang.Logo.SELECT_TOOL_TOOLTIP, true),
    MOVEIT(Icons.MOVE, Lang.Logo.MOVEIT_TOOL_UNDERLINE, Lang.Logo.MOVEIT_TOOL, Lang.Logo.MOVEIT_TOOL_TOOLTIP, true),
    ERASER(Icons.ERASER, Lang.Logo.ERASER_TOOL_UNDERLINE, Lang.Logo.ERASER_TOOL, Lang.Logo.ERASER_TOOL_TOOLTIP, true);

    /* Static */

    private static final CycleIndex CYCLE_INDEX = new CycleIndex(CanvasTools.values(), true);

    /* Fields */

    private final TextureIcon icon;
    private final Translation underline;
    private final Translation header;
    private final Translation tooltip;
    private final boolean draggable;

    /* Constructor */

    CanvasTools(TextureIcon icon, Translation underline, Translation header, Translation tooltip, boolean draggable)
    {
        this.icon = icon;
        this.underline = underline;
        this.header = header;
        this.tooltip = tooltip;
        this.draggable = draggable;
    }

    /* Methods */

    /**
     * @return The {@link TextureIcon} instance for this canvas tool.
     */
    public TextureIcon icon()
    {
        return this.icon;
    }

    /**
     * @return The keyboard shortcut underline {@link Translation} instance for this canvas tool.
     */
    public Translation underline()
    {
        return this.underline;
    }

    /**
     * @return The {@link Translation} header tooltip name instance for this canvas tool.
     */
    public Translation header()
    {
        return this.header;
    }

    /**
     * @return The {@link Translation} info tooltip for this canvas tool.
     */
    public Translation tooltip()
    {
        return this.tooltip;
    }

    /**
     * @return Whether this canvas tool allows mouse dragging on the canvas.
     */
    public boolean isDraggable()
    {
        return this.draggable;
    }

    /* Helpers */

    /**
     * Check if the current canvas tool is move or select.
     *
     * @param mode The {@link CanvasTools} to check against.
     * @return Whether the given canvas tool manages a selection.
     */
    public static boolean isMoveOrSelect(CanvasTools mode)
    {
        return CanvasTools.SELECT == mode || CanvasTools.MOVEIT == mode;
    }

    /**
     * Manually change the canvas tool.
     *
     * @param index The new cycle starting index.
     */
    public static void set(int index)
    {
        CYCLE_INDEX.setStartIndex(index);
        CYCLE_INDEX.restart();
    }

    /**
     * Manually change the canvas tool.
     *
     * @param mode The {@link CanvasTools} value to change to.
     */
    public static void set(CanvasTools mode)
    {
        for (int i = 0; i < CanvasTools.values().length; i++)
        {
            if (mode == CanvasTools.values()[i])
            {
                set(i);
                return;
            }
        }
    }

    /**
     * Get the current canvas tool.
     *
     * @return The {@link CanvasTools} currently in use.
     */
    public static CanvasTools get()
    {
        return CanvasTools.values()[CYCLE_INDEX.get()];
    }

    /**
     * Cycle to the next canvas tool.
     *
     * @return The next {@link CanvasTools} instance.
     */
    public static CanvasTools cycle()
    {
        if (Screen.hasShiftDown())
            CYCLE_INDEX.backward();
        else
            CYCLE_INDEX.forward();

        return CanvasTools.values()[CYCLE_INDEX.get()];
    }

    /**
     * Reset the canvas tool back first value.
     */
    public static void reset()
    {
        CYCLE_INDEX.setStartIndex(0);
        CYCLE_INDEX.restart();
    }
}
