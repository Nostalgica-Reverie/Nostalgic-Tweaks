package mod.adrenix.nostalgic.client.gui.screen.home.overlay.warning;

import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;

public abstract class WarningBanner {
    /**
     * Whether the warning banner is active. It can be closed by the user for the remainder of the gaming session if the
     * banner's "x" button is pressed.
     */
    public static final FlagHolder ACTIVE = FlagHolder.off();

    /**
     * This controls whether the banner ever shows up again when the home screen is opened.
     */
    private static final FlagHolder HIDDEN = FlagHolder.off();

    /**
     * Set up the mod conflict warning banner if needed.
     *
     * @param screen The {@link HomeScreen} instance.
     */
    public static void setupIfNeeded(HomeScreen screen) {
        ACTIVE.set(false/*WarningOverlay.isActive()*/); //TODO

        if (!ACTIVE.get() || HIDDEN.get())
            return;

        TextWidget disabled = TextWidget.create(Lang.Home.WARNING_BANNER)
                .icon(Icons.WARNING)
                .posY(1)
                .useTextWidth()
                .centerInScreenX()
                .centerAligned()
                .visibleIf(ACTIVE::get)
                .build(screen::addWidget);

        TextWidget click = TextWidget.create(Lang.Home.WARNING_BANNER_CLICK)
                .cannotFocus()
                .below(disabled, 1)
                .useTextWidth()
                .centerInScreenX()
                .centerAligned()
                .visibleIf(ACTIVE::get)
//                .onPress(WarningOverlay::open) //TODO
                .build(screen::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.SILVER_CHALICE)
                .height(1)
                .below(click, 1)
                .extendWidthToScreenEnd(0)
                .visibleIf(ACTIVE::get)
                .build(screen::addWidget);

        BlankWidget background = BlankWidget.create()
                .extendWidthToScreenEnd(0)
                .extendHeightTo(separator, 0)
                .renderer((widget, graphics, mouseX, mouseY, partialTick) -> {
                    if (!ACTIVE.get())
                        return;

                    graphics.fill(0, 0, widget.getWidth(), widget.getHeight(), Color.COPPER_RED.fromAlpha(0.8F).get());
                })
                .build(screen::addWidget);

        IconTemplate.close()
                .onPress(CollectionUtil.runAll(ACTIVE::disable, HIDDEN::enable))
                .centerInWidgetY(background)
                .tabOrderGroup(-1)
                .fromScreenEndX(3)
                .visibleIf(ACTIVE::get)
                .build(screen::addWidget);
    }
}
