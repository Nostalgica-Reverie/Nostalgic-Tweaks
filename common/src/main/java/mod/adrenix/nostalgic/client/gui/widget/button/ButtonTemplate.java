package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.client.gui.overlay.types.color.ColorPicker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanConsumer;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ButtonTemplate
{
    /**
     * This button provides a template for opening a color picker overlay.
     *
     * @param color    A {@link Supplier} that provides a {@link Color} the overlay will manage.
     * @param onClose  A {@link Consumer} that accepts the {@link ColorPicker} when the overlay closes.
     * @param isOpaque Whether the given {@link Color} is opaque.
     * @return A {@link ButtonBuilder} instance.
     */
    @PublicAPI
    public static ButtonBuilder colorPicker(Supplier<Color> color, Consumer<ColorPicker> onClose, boolean isOpaque)
    {
        return ButtonWidget.create()
            .icon(Icons.COLOR_PICKER)
            .tooltip(Lang.Picker.OPEN, 30, 500L, TimeUnit.MILLISECONDS)
            .onPress(() -> ColorPicker.create(color.get(), onClose).opaque(isOpaque).open())
            .postRenderer((button, graphics, mouseX, mouseY, partialTick) -> {
                int iconX = button.getIconManager().get().getX();
                int iconY = button.getIconManager().get().getY();
                Color fill = color.get();

                RenderUtil.beginBatching();
                graphics.pose().pushPose();
                graphics.pose().translate(iconX, iconY, 1.0D);

                RenderUtil.fill(graphics, 1.0F, 10.0F, 2.0F, 11.0F, fill);
                RenderUtil.fill(graphics, 2.0F, 8.0F, 4.0F, 10.0F, fill);
                RenderUtil.fill(graphics, 3.0F, 7.0F, 6.0F, 8.0F, fill);
                RenderUtil.fill(graphics, 4.0F, 8.0F, 5.0F, 9.0F, fill);
                RenderUtil.fill(graphics, 4.0F, 6.0F, 7.0F, 7.0F, fill);

                graphics.pose().popPose();
                RenderUtil.endBatching();
            });
    }

    /**
     * This button provides a template for a checkbox button.
     *
     * @param title    The {@link Component} title of the button.
     * @param supplier A {@link BooleanSupplier} that yields whether the checkbox is selected.
     * @return A new {@link ButtonBuilder} instance.
     * @see #checkbox(Translation, BooleanSupplier)
     */
    @PublicAPI
    public static ButtonBuilder checkbox(Component title, BooleanSupplier supplier)
    {
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
    public static ButtonBuilder checkbox(Translation lang, BooleanSupplier supplier)
    {
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
    public static ButtonBuilder toggle(BooleanSupplier getter, BooleanConsumer setter)
    {
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
    public static ButtonBuilder openFolder(Path path)
    {
        return ButtonWidget.create(Lang.Button.OPEN_FOLDER)
            .icon(Icons.FOLDER)
            .onPress(() -> Util.getPlatform().openFile(path.toFile()));
    }
}
