package mod.adrenix.nostalgic.client.gui.overlay.types.item;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class acts as a placeholder for items in the heavily populated row list. Having squares with indexes that point
 * to an item in the item list (or found items list) is <i>a lot</i> faster than rebuilding the row list each time a
 * character is added or removed from the search query.
 */
class ItemSquare
{
    /* Fields */

    private final ItemPicker itemPicker;
    private final Consumer<ButtonBuilder> extraSteps;
    private final int index;

    /* Constructor */

    public ItemSquare(ItemPicker itemPicker, int index, Consumer<ButtonBuilder> extraSteps)
    {
        this.itemPicker = itemPicker;
        this.extraSteps = extraSteps;
        this.index = index;
    }

    /* Methods */

    /**
     * @return A shortcut for retrieve the parent {@link ItemPicker} super class.
     */
    private ItemPicker parent()
    {
        return itemPicker;
    }

    /**
     * @return Gets an {@link ItemStack} from either the {@code found} items list or the pre-populated {@code item}
     * items list.
     */
    private ItemStack getItemStack()
    {
        if (this.parent().found.size() > this.index)
            return this.parent().found.get(this.index);

        return this.parent().items.get(this.index);
    }

    /**
     * Instructions to perform when this square is pressed.
     */
    private void onPress()
    {
        this.parent().selected = this.getItemStack();
        this.parent().overlay.close();
    }

    /**
     * @return Gets a {@link TextureIcon} that matches the {@link ItemStack} this square is pointing to.
     */
    private TextureIcon getIcon()
    {
        return this.parent().icons.get(this.getItemStack().getItem());
    }

    /**
     * @return Provides a {@link List} of {@link Component}s from the {@link ItemStack} this square is pointing to.
     */
    private List<Component> getListTooltip()
    {
        try
        {
            return this.getItemStack().getTooltipLines(null, TooltipFlag.Default.NORMAL);
        }
        catch (Throwable throwable)
        {
            return List.of(Component.literal(ItemUtil.getResourceKey(this.getItemStack())));
        }
    }

    /**
     * Checks if the {@link ItemStack} this square is pointing to wasn't found in the {@code found} items list.
     *
     * @return Whether the {@link ButtonWidget} is invisible.
     */
    private boolean isInvisible()
    {
        return !this.parent().found.isEmpty() && this.parent().found.size() <= this.index;
    }

    /**
     * Renderer helper method for rendering the contents of this square using the {@link ButtonWidget}.
     */
    private void render(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Color color = Color.WHITE.fromAlpha(85);

        boolean isMouseOver = this.parent().rowList.isMouseOver(mouseX, mouseY) && button.isMouseOver(mouseX, mouseY);
        boolean isFocused = button.isFocused();
        boolean isHoveredOrFocused = isMouseOver || isFocused;

        if (isHoveredOrFocused)
            RenderUtil.fill(graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), color);

        int startY = button.getIconY() - (isHoveredOrFocused ? 1 : 0);

        button.getIconManager().pos(button.getIconX(), startY);
        button.getIconManager().render(graphics, mouseX, mouseY, partialTick);

        if (isHoveredOrFocused)
            button.getIconManager().get().setY(startY + 1);
    }

    /**
     * @return A {@link ButtonWidget} that will be used for alignment within a row and display the correct information
     * about the {@link ItemStack} this square is pointing to.
     */
    public ButtonWidget getButton()
    {
        ButtonBuilder builder = ButtonWidget.create()
            .darkenOnDisable(0.5F)
            .onPress(this::onPress)
            .icon(this::getIcon)
            .listTooltip(this::getListTooltip)
            .invisibleIf(this::isInvisible)
            .renderer(this::render);

        this.extraSteps.accept(builder);

        return builder.build();
    }
}
