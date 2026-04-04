package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.client.gui.overlay.types.color.ColorPicker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

//import mod.adrenix.nostalgic.client.gui.overlay.types.item.ItemPicker;
//import mod.adrenix.nostalgic.tweak.listing.ItemRule;

public abstract class ButtonTemplate {
    /**
     * This button provides a template for opening a color picker overlay.
     *
     * @param color    A {@link Supplier} that provides a {@link Color} the overlay will manage.
     * @param onClose  A {@link Consumer} that accepts the {@link ColorPicker} when the overlay closes.
     * @param isOpaque Whether the given {@link Color} is opaque.
     * @return A {@link ButtonBuilder} instance.
     */
    @PublicAPI
    public static ButtonBuilder colorPicker(Supplier<Color> color, Consumer<ColorPicker> onClose, boolean isOpaque) {
        return ButtonWidget.create()
                .icon(Icons.COLOR_PICKER)
                .tooltip(Lang.Colorize.OPEN, 30, 500L, TimeUnit.MILLISECONDS)
                .onPress(() -> ColorPicker.create(color.get(), onClose).opaque(isOpaque).open())
                .postRenderer((button, graphics, mouseX, mouseY, partialTick) -> {
                    int iconX = button.getIconManager().get().getX();
                    int iconY = button.getIconManager().get().getY();
                    Color fill = color.get();

                    graphics.pose().pushMatrix();
                    graphics.pose().translate(iconX, iconY);

                    graphics.fill(1, 10, 2, 11, fill.get());
                    graphics.fill(2, 8, 4, 10, fill.get());
                    graphics.fill(3, 7, 6, 8, fill.get());
                    graphics.fill(4, 8, 5, 9, fill.get());
                    graphics.fill(4, 6, 7, 7, fill.get());

                    graphics.pose().popMatrix();
                });
    }

    /**
     * This button provides a template for opening a color picker overlay.
     *
     * @param color    A {@link Color} the overlay will manage.
     * @param isOpaque Whether the given {@link Color} is opaque.
     * @return A {@link ButtonBuilder} instance.
     */
    @PublicAPI
    public static ButtonBuilder colorPicker(Color color, boolean isOpaque) {
        return colorPicker(() -> color, picker -> {
        }, isOpaque);
    }

//    /**
//     * This button provides a template for opening an item picker overlay.
//     *
//     * @param onItemAdd  A {@link Consumer} that accepts an {@link ItemStack} if one was chosen.
//     * @param onEmptyAdd A {@link Runnable} that will run if no {@link ItemStack} is chosen.
//     * @param rules      A varargs list of {@link ItemRule}.
//     * @return A {@link ButtonBuilder} instance.
//     * @see #itemPicker(Consumer, ItemRule...)
//     */
//    @PublicAPI
//    public static ButtonBuilder itemPicker(Consumer<ItemStack> onItemAdd, Runnable onEmptyAdd, ItemRule... rules) {
//        return ButtonWidget.create()
//                .tooltip(Lang.Itemize.OPEN, 30, 500L, TimeUnit.MILLISECONDS)
//                .onPress(() -> ItemPicker.create(onItemAdd, onEmptyAdd, rules).open());
//    }

//    /**
//     * This button provides a template for opening an item picker overlay.
//     *
//     * @param onItemAdd A {@link Consumer} that accepts an {@link ItemStack} if one was chosen.
//     * @param rules     A varargs list of {@link ItemRule}.
//     * @return A {@link ButtonBuilder} instance.
//     * @see #itemPicker(Consumer, Runnable, ItemRule...)
//     */
//    @PublicAPI
//    public static ButtonBuilder itemPicker(Consumer<ItemStack> onItemAdd, ItemRule... rules) {
//        return ButtonWidget.create()
//                .tooltip(Lang.Itemize.OPEN, 30, 500L, TimeUnit.MILLISECONDS)
//                .onPress(() -> ItemPicker.create(onItemAdd, rules).open());
//    }

    /**
     * This button provides a template for a checkbox button.
     *
     * @param title    The {@link Component} title of the button.
     * @param supplier A {@link BooleanSupplier} that yields whether the checkbox is selected.
     * @return A new {@link ButtonBuilder} instance.
     * @see #checkbox(Translation, BooleanSupplier)
     */
    @PublicAPI
    public static ButtonBuilder checkbox(Component title, BooleanSupplier supplier) {
        return ButtonWidget.create(title)
                .icon(() -> supplier.getAsBoolean() ? Icons.CHECKBOX_SELECTED : Icons.CHECKBOX)
                .width(Icons.CHECKBOX.getWidth() + GuiUtil.font().width(title))
                .height(Icons.CHECKBOX.getHeight())
                .backgroundRenderer(ButtonRenderer.EMPTY)
                .iconTextPadding(6)
                .padding(0)
                .alignLeft()
                .useTextWidth();
    }

    /**
     * This button provides a template for a checkbox button.
     *
     * @param lang     A {@link Translation} title of the button.
     * @param supplier A {@link BooleanSupplier} that yields whether the checkbox is selected.
     * @return A new {@link ButtonBuilder} instance.
     * @see #checkbox(Component, BooleanSupplier)
     */
    @PublicAPI
    public static ButtonBuilder checkbox(Translation lang, BooleanSupplier supplier) {
        return checkbox(lang.get(), supplier);
    }

    /**
     * This button provides a template for a toggle switch button.
     *
     * @param getter The {@link BooleanSupplier} that determines the switch state.
     * @param setter The {@link BooleanConsumer} that changes the switch state.
     * @return A new {@link ButtonBuilder} instance.
     */
    @PublicAPI
    public static ButtonBuilder toggle(BooleanSupplier getter, BooleanConsumer setter) {
        return ButtonWidget.create()
                .onPress(() -> setter.accept(!getter.getAsBoolean()))
                .icon(() -> getter.getAsBoolean() ? Icons.TOGGLE_ON : Icons.TOGGLE_OFF)
                .hoverIcon(() -> getter.getAsBoolean() ? Icons.TOGGLE_ON_HOVER : Icons.TOGGLE_OFF_HOVER)
                .disabledIcon(() -> getter.getAsBoolean() ? Icons.TOGGLE_ON_DISABLED : Icons.TOGGLE_OFF_DISABLED)
                .backgroundRenderer(ButtonRenderer.EMPTY)
                .iconCenterOffset(4)
                .height(8)
                .width(12);
    }

    /**
     * This button provides a template for opening a folder on the user's operating system.
     *
     * @param path A {@link Path} to open.
     * @return A button factory so that the caller can define more properties.
     */
    @PublicAPI
    public static ButtonBuilder openFolder(Path path) {
        return ButtonWidget.create(Lang.Button.OPEN_FOLDER)
                .icon(Icons.FOLDER)
                .onPress(() -> Util.getPlatform().openFile(path.toFile()));
    }
}
