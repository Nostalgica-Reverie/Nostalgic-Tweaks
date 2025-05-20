package mod.adrenix.nostalgic.client.gui.screen;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class EnhancedScreen<T extends EnhancedScreen<T, W>, W extends WidgetManager> extends Screen
    implements DynamicScreen<T>
{
    /* Fields */

    @Nullable protected final Screen parentScreen;
    protected final Minecraft minecraft;
    private final Function<T, W> widgetManager;
    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;

    /* Constructor */

    public EnhancedScreen(Function<T, W> widgetManager, @Nullable Screen parentScreen, Component title)
    {
        super(title);

        this.minecraft = Minecraft.getInstance();
        this.widgets = new UniqueArrayList<>();
        this.widgetManager = widgetManager;
        this.parentScreen = parentScreen;
    }

    /* Abstraction */

    /**
     * @return The current {@link W WidgetManager} instance for this screen.
     */
    public abstract W getWidgetManager();

    /**
     * Setter for the widget manager field in an enhanced screen.
     *
     * @param widgetManager A new {@link W WidgetManager} instance.
     */
    public abstract void setWidgetManager(W widgetManager);

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Screen getParentScreen()
    {
        return this.parentScreen;
    }

    /**
     * Replaces the current widgets on the screen with fresh instances.
     */
    @Override
    protected void init()
    {
        this.clearWidgets();
        this.setWidgetManager(this.widgetManager.apply(this.self()));
        this.getWidgetManager().init();
    }

    /**
     * Ticks widgets that need ticked in the widget manager.
     */
    @Override
    public void tick()
    {
        this.getWidgetManager().tick();
        this.widgets.forEach(DynamicWidget::tick);
    }

    /**
     * Handler method for when the game window is resized.
     *
     * @param minecraft A singleton Minecraft instance.
     * @param width     The new game window width.
     * @param height    The new game window height.
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        super.resize(minecraft, width, height);
    }

    /**
     * Handler method for when a key is pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scan code.
     * @param modifiers Key code modifiers.
     * @return Whether this method handled the key that was pressed.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (KeyboardUtil.isEsc(keyCode))
        {
            if (this.getWidgets().stream().anyMatch(widget -> widget.keyPressed(keyCode, scanCode, modifiers)))
                return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Handler method for when a key is released after being pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * Handler method for when a char is typed.
     *
     * @param codePoint The char that was typed.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        return super.charTyped(codePoint, modifiers);
    }

    /**
     * Handler method for when the mouse is moved within the screen.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY the y-position of the mouse.
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);
    }

    /**
     * Handler method for when the mouse is clicked.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse being clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        boolean isMouseClicked = super.mouseClicked(mouseX, mouseY, button);

        if (this.getFocused() instanceof DynamicWidget<?, ?> dynamic && !dynamic.canFocusOnClick())
            dynamic.setFocused(false);

        return isMouseClicked;
    }

    /**
     * Handler method for when the mouse is released.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse scrolls.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param deltaY Vertical scroll amount.
     * @return Whether this method handled the mouse scroll.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaY)
    {
        return super.mouseScrolled(mouseX, mouseY, deltaY);
    }

    /**
     * Handler method for when the mouse drags on the screen.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX  The new dragged offset x-position from the mouse.
     * @param dragY  The new dragged offset y-position from the mouse.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * @return A {@link UniqueArrayList} of all {@link DynamicWidget} subscribed to this screen.
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets()
    {
        return this.widgets;
    }

    /**
     * Handler method for rendering elements to the screen.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Handler method for when this screen is closed.
     */
    @Override
    public void onClose()
    {
        this.minecraft.setScreen(this.parentScreen);
    }
}
