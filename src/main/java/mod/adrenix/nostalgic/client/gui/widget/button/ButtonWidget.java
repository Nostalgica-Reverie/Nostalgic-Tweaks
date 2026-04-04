package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ButtonWidget extends AbstractButton<ButtonBuilder, ButtonWidget> {
    /* Builders */

    /**
     * Create a new button widget instance.
     *
     * @param builder A {@link ButtonBuilder} instance.
     * @param onPress A {@link Consumer} that accepts this {@link ButtonWidget} instance.
     */
    protected ButtonWidget(ButtonBuilder builder, Consumer<ButtonWidget> onPress) {
        super(builder, onPress);
    }

    /**
     * Create a new {@link ButtonWidget} instance.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component} to render.
     * @return A new {@link ButtonBuilder} instance.
     */
    public static ButtonBuilder create(Supplier<Component> supplier) {
        return new ButtonBuilder(Component.empty()).title(supplier);
    }

    /**
     * Create a new {@link ButtonWidget} instance.
     *
     * @param title A component that represents the button title.
     * @return A new {@link ButtonBuilder} instance.
     */
    public static ButtonBuilder create(Component title) {
        return new ButtonBuilder(title);
    }

    /**
     * Create a new {@link ButtonWidget} instance.
     *
     * @param langKey A {@link Translation} instance.
     * @return A new {@link ButtonBuilder} instance.
     */
    public static ButtonBuilder create(Translation langKey) {
        return new ButtonBuilder(langKey.get());
    }

    /**
     * Create a new {@link ButtonWidget} instance.
     *
     * @param title A string that represents the button title.
     * @return A new {@link ButtonBuilder} instance.
     */
    public static ButtonBuilder create(String title) {
        return new ButtonBuilder(Component.literal(title));
    }

    /* Constructor */

    /**
     * Create a new {@link ButtonWidget} instance. This builder will have its title set to {@link Component#empty()}. A
     * dynamic title can instead be defined using {@link ButtonBuilder#title(Supplier)}.
     *
     * @return A new {@link ButtonBuilder} instance.
     */
    public static ButtonBuilder create() {
        return create(Component.empty());
    }
}
