package mod.adrenix.nostalgic.client.gui.widget.embed;

public interface EmbedEvent
{
    interface MouseClicked
    {
        /**
         * Handler method for when the mouse clicks on this embed.
         *
         * @param mouseX The current x-coordinate of the mouse.
         * @param mouseY The current y-coordinate of the mouse.
         * @param button The mouse button that was clicked.
         * @return Whether this method handled the mouse click event.
         */
        boolean accept(double mouseX, double mouseY, int button);
    }

    interface MouseDragged
    {
        /**
         * Handler method for when the mouse drags on the embed.
         *
         * @param mouseX The current x-coordinate of the mouse.
         * @param mouseY The current y-coordinate of the mouse.
         * @param button The mouse button that was clicked.
         * @param dragX  The x-distance of the drag.
         * @param dragY  The y-distance of the drag.
         * @return Whether this method handled the event.
         */
        boolean accept(double mouseX, double mouseY, int button, double dragX, double dragY);
    }

    interface MouseReleased
    {
        /**
         * Handler method for when the mouse is released.
         *
         * @param mouseX The current x-coordinate of the mouse.
         * @param mouseY The current y-coordinate of the mouse.
         * @param button The mouse button that was clicked.
         * @return Whether this method handled the event.
         */
        boolean accept(double mouseX, double mouseY, int button);
    }

    interface MouseScrolled
    {
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

    interface KeyPressed
    {
        /**
         * Handler method for when a key is pressed.
         *
         * @param keyCode   The key code that was pressed.
         * @param scanCode  A key scancode.
         * @param modifiers Any held modifiers.
         * @return Whether this method handled the event.
         */
        boolean accept(int keyCode, int scanCode, int modifiers);
    }

    interface KeyReleased
    {
        /**
         * Handler method for when a key is released after being pressed.
         *
         * @param keyCode   The key code that was pressed.
         * @param scanCode  A key scancode.
         * @param modifiers Any held modifiers.
         * @return Whether this method handled the event.
         */
        boolean accept(int keyCode, int scanCode, int modifiers);
    }

    interface CharTyped
    {
        /**
         * Handler method for when a char is typed.
         *
         * @param codePoint The char that was typed.
         * @param modifiers Any held modifiers.
         * @return Whether this method handled the event.
         */
        boolean accept(int codePoint, int modifiers);
    }
}
