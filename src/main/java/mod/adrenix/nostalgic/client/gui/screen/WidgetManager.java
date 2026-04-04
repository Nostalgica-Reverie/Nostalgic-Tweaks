package mod.adrenix.nostalgic.client.gui.screen;

/**
 * Used by the {@link EnhancedScreen} abstraction to simplify the widget management of screens.
 */
public interface WidgetManager {
    /**
     * Initialize widgets.
     */
    void init();

    /**
     * Tick any widgets that need to be ticked.
     */
    default void tick() {
    }
}
