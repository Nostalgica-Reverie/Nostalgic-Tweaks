package mod.adrenix.nostalgic.client.gui.overlay;

import net.minecraft.client.input.KeyEvent;

/**
 * This is a functional interface that provides custom key pressing instructions that will be performed when keyboard
 * input is accepted by an overlay.
 */
public interface OverlayKeyboard {
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param overlay The {@link Overlay} instance.
     * @param event   The {@link KeyEvent} for this keypress.
     * @return Whether this handled the key press event.
     */
    boolean test(Overlay overlay, KeyEvent event);
}
