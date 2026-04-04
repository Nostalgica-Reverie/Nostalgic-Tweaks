package mod.adrenix.nostalgic.client.gui.screen;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class EnhancedScreen<T extends EnhancedScreen<T, W>, W extends WidgetManager> extends Screen
        implements DynamicScreen<T> {
    /* Fields */

    @Nullable
    protected final Screen parentScreen;
    protected final Minecraft minecraft;
    private final Function<T, W> widgetManager;
    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;

    /* Constructor */

    public EnhancedScreen(Function<T, W> widgetManager, @Nullable Screen parentScreen, Component title) {
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
    public @Nullable Screen getParentScreen() {
        return this.parentScreen;
    }

    /**
     * Replaces the current widgets on the screen with fresh instances.
     */
    @Override
    protected void init() {
        this.clearWidgets();
        this.setWidgetManager(this.widgetManager.apply(this.self()));
        this.getWidgetManager().init();
    }

    /**
     * Ticks widgets that need ticked in the widget manager.
     */
    @Override
    public void tick() {
        this.getWidgetManager().tick();
        this.widgets.forEach(DynamicWidget::tick);
    }

    /**
     * Handler method for when the game window is resized.
     *
     * @param width  The new game window width.
     * @param height The new game window height.
     */
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    /**
     * Handler method for when a key is pressed.
     * <p>
     *
     * @param event The {@link KeyEvent} for this keypress.
     * @return Whether this method handled the key that was pressed.
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (KeyboardUtil.isEsc(event.key())) {
            if (this.getWidgets().stream().anyMatch(widget -> widget.keyPressed(event)))
                return true;
        }

        return super.keyPressed(event);
    }

    /**
     * Handler method for when a key is released after being pressed.
     * <p>
     *
     * @param event The {@link KeyEvent} for this keypress.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyReleased(KeyEvent event) {
        return super.keyReleased(event);
    }

    /**
     * Handler method for when a char is typed.
     * <p>
     *
     * @param event The {@link CharacterEvent} for this typed char.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        return super.charTyped(event);
    }

    /**
     * Handler method for when the mouse is moved within the screen.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY the y-position of the mouse.
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    /**
     * Handler method for when the mouse is clicked.
     * <p>
     *
     * @param event       The {@link MouseButtonEvent} of the mouse click.
     * @param doubleClick Whether the mouse is being double-clicked.
     * @return Whether this method handled the mouse being clicked.
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean isMouseClicked = super.mouseClicked(event, doubleClick);

        if (this.getFocused() instanceof DynamicWidget<?, ?> dynamic && !dynamic.canFocusOnClick())
            dynamic.setFocused(false);

        return isMouseClicked;
    }

    /**
     * Handler method for when the mouse is released.
     *
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return super.mouseReleased(event);
    }

    /**
     * Handler method for when the mouse scrolls.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @param deltaX Vertical scroll amount.
     * @param deltaY Horizontal scroll amount.
     * @return Whether this method handled the mouse scroll.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    /**
     * Handler method for when the mouse drags on the screen.
     * <p>
     *
     * @param event The {@link MouseButtonEvent} of the mouse click.
     * @param dragX The new dragged offset x-position from the mouse.
     * @param dragY The new dragged offset y-position from the mouse.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return super.mouseDragged(event, dragX, dragY);
    }

    /**
     * @return A {@link UniqueArrayList} of all {@link DynamicWidget} subscribed to this screen.
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets() {
        return this.widgets;
    }

    /**
     * Handler method for rendering elements to the screen.
     *
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Handler method for when this screen is closed.
     */
    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }
}
