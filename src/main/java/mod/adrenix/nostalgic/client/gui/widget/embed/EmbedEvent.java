package mod.adrenix.nostalgic.client.gui.widget.embed;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public interface EmbedEvent {
    interface MouseClicked {
        /**
         * Handler method for when the mouse clicks on this embed.
         * <p>
         *
         * @param event       The {@link MouseButtonEvent} of the mouse click.
         * @param doubleClick Whether the mouse is being double-clicked.
         * @return Whether this method handled the mouse click event.
         */
        boolean accept(MouseButtonEvent event, boolean doubleClick);
    }

    interface MouseDragged {
        /**
         * Handler method for when the mouse drags on the embed.
         * <p>
         *
         * @param event The {@link MouseButtonEvent} of the mouse click.
         * @param dragX The x-distance of the drag.
         * @param dragY The y-distance of the drag.
         * @return Whether this method handled the event.
         */
        boolean accept(MouseButtonEvent event, double dragX, double dragY);
    }

    interface MouseReleased {
        /**
         * Handler method for when the mouse is released.
         * <p>
         *
         * @param event The {@link MouseButtonEvent} of the mouse click.
         * @return Whether this method handled the event.
         */
        boolean accept(MouseButtonEvent event);
    }

    interface MouseScrolled {
        /**
         * Handler method for when the mouse scrolls in this embed.
         *
         * @param mouseX The current x-coordinate of the mouse.
         * @param mouseY The current y-coordinate of the mouse.
         * @param deltaY The change in scroll in the y-direction. A delta of -1.0D (scroll down) moves rows up while a
         *               delta of 1.0D (scroll up) moves rows back down.
         * @return Whether this method handled the event.
         */
        boolean accept(double mouseX, double mouseY, double deltaY);
    }

    interface KeyPressed {
        /**
         * Handler method for when a key is pressed.
         * <p>
         *
         * @param event The {@link KeyEvent} for this keypress.
         * @return Whether this method handled the event.
         */
        boolean accept(KeyEvent event);
    }

    interface KeyReleased {
        /**
         * Handler method for when a key is released after being pressed.
         * <p>
         *
         * @param event The {@link KeyEvent} for this keypress.
         * @return Whether this method handled the event.
         */
        boolean accept(KeyEvent event);
    }

    interface CharTyped {
        /**
         * Handler method for when a char is typed.
         * <p>
         *
         * @param event The {@link CharacterEvent} for this typed char.
         * @return Whether this method handled the event.
         */
        boolean accept(CharacterEvent event);
    }
}
