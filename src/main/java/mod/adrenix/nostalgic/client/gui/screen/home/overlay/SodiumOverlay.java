package mod.adrenix.nostalgic.client.gui.screen.home.overlay;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.services.NostalgicServices;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public abstract class SodiumOverlay {
    /**
     * Flag holder for tracking the state of the overlay.
     */
    public static final FlagHolder OPENED = FlagHolder.off();

    /**
     * The Sodium mixin property that disables clouds override.
     */
    public static final String CLOUDS_MIXIN_PROPERTY = "mixin.features.render.world.clouds=false";

    /**
     * Open a new initial Sodium information overlay.
     *
     * @return The Sodium information {@link Overlay} instance.
     */
    public static Overlay open() {
        Overlay overlay = getOverlay();

        OPENED.enable();
        overlay.runOnClose(OPENED::disable);

        return overlay.open();
    }

    /**
     * @return The {@link Overlay} for Sodium information.
     */
    private static Overlay getOverlay() {
        int padding = 2;

        final Overlay overlay = Overlay.create(Lang.Home.SODIUM_TITLE)
                .icon(Icons.SODIUM)
                .padding(padding)
                .resizeWidthUsingPercentage(0.65D)
                .resizeHeightUsingPercentage(0.8D)
                .resizeHeightForWidgets()
                .build();

        Group clouds = Group.create(overlay)
                .icon(Icons.SKY)
                .title(Lang.Home.SODIUM_CLOUDS_TITLE)
                .border(Color.LIGHT_BLUE)
                .extendWidthToScreenEnd(0)
                .build(overlay::addWidget);

        TextWidget cloudsInfo = TextWidget.create(Lang.Home.SODIUM_CLOUDS_INFO)
                .width(clouds::getInsideWidth)
                .build(clouds::addWidget);

        TextWidget cloudsOverride = TextWidget.create(Lang.Home.SODIUM_CLOUDS_OVERRIDE)
                .below(cloudsInfo, padding * 3)
                .width(clouds::getInsideWidth)
                .build(clouds::addWidget);

        TextWidget cloudsRestart = TextWidget.create(Lang.Home.SODIUM_CLOUDS_RESTART)
                .below(cloudsOverride, padding * 3)
                .width(clouds::getInsideWidth)
                .build(clouds::addWidget);

        SeparatorWidget separateTextAndButtons = SeparatorWidget.create(clouds.getColor())
                .height(1)
                .below(cloudsRestart, padding * 2)
                .width(clouds::getInsideWidth)
                .build(clouds::addWidget);

        Grid cloudButtons = Grid.create(clouds, 2)
                .columnSpacing(1)
                .extendWidthToEnd(clouds, clouds.getInsidePaddingX())
                .below(separateTextAndButtons, padding * 2)
                .build(clouds::addWidget);

        ButtonWidget.create(Lang.Home.SODIUM_OPEN_PROPERTIES)
                .icon(Icons.FOLDER)
                .tooltip(Lang.Home.SODIUM_OPEN_PROPERTIES, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.SODIUM_PROPERTIES, 35)
                .onPress(SodiumOverlay::openSodiumMixinConfig)
                .build(cloudButtons::addCell);

        ButtonWidget.create(Lang.Home.SODIUM_COPY_CLOUDS)
                .icon(Icons.CLIPBOARD)
                .tooltip(Lang.Home.SODIUM_COPY_CLOUDS, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.SODIUM_COPY_PROPERTY, 35)
                .onPress(() -> Minecraft.getInstance().keyboardHandler.setClipboard(CLOUDS_MIXIN_PROPERTY))
                .build(cloudButtons::addCell);

        return overlay;
    }

    /**
     * Attempt to open Sodium's mixin config properties file.
     */
    private static void openSodiumMixinConfig() {
        Path properties = NostalgicServices.PLATFORM.getConfigFolder().resolve("sodium-mixins.properties");

        if (Files.exists(properties))
            Util.getPlatform().openPath(properties);
        else {
            MessageOverlay.create(MessageType.ERROR, Lang.Home.SODIUM_MISSING_FILE_HEADER, Lang.Home.SODIUM_MISSING_FILE_BODY)
                    .addButton(ButtonTemplate.openFolder(NostalgicServices.PLATFORM.getConfigFolder()))
                    .setResizePercentage(0.7D)
                    .build()
                    .open();
        }
    }
}
