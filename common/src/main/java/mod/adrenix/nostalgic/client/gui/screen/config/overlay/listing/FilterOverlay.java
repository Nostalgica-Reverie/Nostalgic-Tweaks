package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchOverlay;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.world.ItemFilter;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FilterOverlay
{
    /* Fields */

    private final Runnable onPress;
    private final SwitchOverlay overlay;
    private final Holder<Boolean> tools = Holder.create(false);
    private final Holder<Boolean> items = Holder.create(false);
    private final Holder<Boolean> blocks = Holder.create(false);
    private final Holder<Boolean> edibles = Holder.create(false);
    private final Holder<Boolean> invincible = Holder.create(false);
    private final List<Holder<Boolean>> holders = List.of(tools, items, blocks, edibles);

    /* Constructor */

    /**
     * Create a new filter overlay instance.
     *
     * @param aboveOrBelow A {@link LayoutElement} to set the filter overlay above/below.
     * @param onPress      A {@link Runnable} to run when a toggle is switched.
     */
    public FilterOverlay(LayoutElement aboveOrBelow, Runnable onPress)
    {
        this.onPress = onPress;
        this.overlay = SwitchOverlay.create(aboveOrBelow);

        WidgetHolder parent = this.overlay.get();
        SwitchGroup tools = SwitchGroup.create(parent, ItemRule.NO_TOOLS.getName(), ItemRule.NO_TOOLS.getInfo(), this.tools::get, this.set(this.tools));
        SwitchGroup items = SwitchGroup.create(parent, ItemRule.NO_ITEMS.getName(), ItemRule.NO_ITEMS.getInfo(), this.items::get, this.set(this.items));
        SwitchGroup blocks = SwitchGroup.create(parent, ItemRule.NO_BLOCKS.getName(), ItemRule.NO_BLOCKS.getInfo(), this.blocks::get, this.set(this.blocks));
        SwitchGroup edibles = SwitchGroup.create(parent, ItemRule.NO_EDIBLES.getName(), ItemRule.NO_EDIBLES.getInfo(), this.edibles::get, this.set(this.edibles));
        SwitchGroup invincible = SwitchGroup.create(parent, ItemRule.INVINCIBLE.getName(), ItemRule.INVINCIBLE.getInfo(), this.invincible::get, this.set(this.invincible));

        this.overlay.setGroups(List.of(tools, items, blocks, edibles, invincible));
        this.overlay.get()
            .getBuilder()
            .outlineColor(Color.WHITE)
            .gradientBackground(Gradient.vertical(Color.ASPHALT_GRAY.fromAlpha(220), Color.DARK_BLUE.fromAlpha(220)));
    }

    /* Methods */

    /**
     * Open the filter overlay.
     */
    public void open()
    {
        this.overlay.open();
    }

    /**
     * Creates a {@link BooleanConsumer} and runs the {@link Runnable} assigned to this utility for when a toggle is
     * changed.
     *
     * @param holder A {@link Holder} instance.
     * @return A {@link BooleanConsumer} instance.
     */
    private BooleanConsumer set(Holder<Boolean> holder)
    {
        return state -> {
            holder.set(state);
            this.onPress.run();
        };
    }

    /**
     * Check if the given item is filtered based on the rules defined by the filtering overlay.
     *
     * @param item An {@link Item} instance.
     * @return Whether the item is filtered.
     */
    public boolean isFiltered(Item item)
    {
        if (this.holders.stream().noneMatch(Holder::get))
            return false;

        boolean isToolFiltered = ItemFilter.isToolLike(item) && this.tools.get();
        boolean isItemFiltered = ItemFilter.isItemLike(item) && this.items.get();
        boolean isBlockFiltered = ItemFilter.isBlockLike(item) && this.blocks.get();
        boolean isEdibleFiltered = item.isEdible() && this.edibles.get();
        boolean isInvincibleFiltered = item.canBeDepleted() && this.invincible.get();

        return isToolFiltered || isItemFiltered || isBlockFiltered || isEdibleFiltered || isInvincibleFiltered;
    }

    /**
     * Overload method for {@link #isFiltered(Item)}.
     *
     * @param itemStack An {@link ItemStack} instance.
     * @return Whether the item stack is filtered.
     */
    public boolean isFiltered(ItemStack itemStack)
    {
        return this.isFiltered(itemStack.getItem());
    }
}
