package mod.adrenix.nostalgic.client.gui.overlay.types.item;

import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemizeMaker
{
    /* Fields */

    @Nullable Supplier<Component> title;
    @Nullable Gradient backgroundGradient;
    @Nullable Color backgroundColor;
    @Nullable Color separatorColor;
    @Nullable Color borderColor;

    final Consumer<ItemStack> onItemAdd;
    final Runnable onEmptyAdd;
    final HashSet<ItemRule> rules;

    /* Constructor */

    protected ItemizeMaker(Consumer<ItemStack> onItemAdd, Runnable onEmptyAdd, HashSet<ItemRule> rules)
    {
        this.onItemAdd = onItemAdd;
        this.onEmptyAdd = onEmptyAdd;
        this.rules = rules;
    }

    /* Methods */

    /**
     * Set a custom title for the item picker.
     *
     * @param title A {@link Supplier} that provides a {@link Component}.
     * @see #title(Translation)
     * @see #title(Component)
     */
    @PublicAPI
    public ItemizeMaker title(Supplier<Component> title)
    {
        this.title = title;

        return this;
    }

    /**
     * Set a custom title for the item picker.
     *
     * @param title A {@link Translation} instance.
     * @see #title(Supplier)
     * @see #title(Component)
     */
    @PublicAPI
    public ItemizeMaker title(Translation title)
    {
        this.title = title::get;

        return this;
    }

    /**
     * Set a custom title for the item picker.
     *
     * @param title A {@link Component} instance.
     * @see #title(Supplier)
     * @see #title(Translation)
     */
    @PublicAPI
    public ItemizeMaker title(Component title)
    {
        this.title = () -> title;

        return this;
    }

    /**
     * Set a custom background gradient for this item picker.
     *
     * @param gradient A {@link Gradient} instance.
     * @see #backgroundColor(Color)
     */
    @PublicAPI
    public ItemizeMaker gradientBackground(Gradient gradient)
    {
        this.backgroundGradient = gradient;

        return this;
    }

    /**
     * Set a custom color background for this item picker.
     *
     * @param color A {@link Color} instance.
     * @see #gradientBackground(Gradient)
     */
    @PublicAPI
    public ItemizeMaker backgroundColor(Color color)
    {
        this.backgroundColor = color;

        return this;
    }

    /**
     * Set a custom border color for this item picker.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public ItemizeMaker borderColor(Color color)
    {
        this.borderColor = color;

        return this;
    }

    /**
     * Set a custom separator color used inside the item picker.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public ItemizeMaker separatorColor(Color color)
    {
        this.separatorColor = color;

        return this;
    }

    /**
     * Finalize the building process and make a new {@link ItemPicker}.
     *
     * @return A new {@link ItemPicker} instance.
     */
    @PublicAPI
    public ItemPicker build()
    {
        return new ItemPicker(this);
    }

    /**
     * If you do not a {@link ItemPicker} reference, and just want to open a picker overlay, then use this.
     */
    @PublicAPI
    public void open()
    {
        new ItemPicker(this).open();
    }
}
