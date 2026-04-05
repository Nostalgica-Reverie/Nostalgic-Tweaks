package mod.adrenix.nostalgic.client.gui.screen.home;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.DebugOverlay;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.SodiumOverlay;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter.SupporterOverlay;
import mod.adrenix.nostalgic.client.gui.screen.home.overlay.warning.WarningBanner;
import mod.adrenix.nostalgic.client.gui.screen.packs.PacksListScreen;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconFactory;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.gui.LinkUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderPass;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.timer.FlagTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

//import mod.adrenix.nostalgic.client.gui.screen.home.overlay.SetupOverlay;
//import mod.adrenix.nostalgic.client.gui.screen.home.overlay.warning.WarningOverlay;
//import mod.adrenix.nostalgic.tweak.config.ModTweak;

public class HomeWidgets implements WidgetManager {
    /* Fields */

    private final HomeScreen homeScreen;
    private final IconFactory heartOutline;
    private final FlagTimer flashTimer;

    /* Constructor */

    HomeWidgets(HomeScreen homeScreen) {
        this.homeScreen = homeScreen;
        this.flashTimer = FlagTimer.create(1L, TimeUnit.SECONDS).build();
        this.heartOutline = IconWidget.create(Icons.HEART_OUTLINE).cannotFocus().renderWhen(RenderPass.LAST).size(12);
    }

    /* Methods */

    @Override
    public void init() {
        int sectionMargin = 0;
        int sectionOffset = 6;

        /* Menus & Links */

        ButtonWidget config = ButtonWidget.create(Lang.Enum.SCREEN_CONFIG)
                .icon(Icons.MECHANICAL_TOOLS)
                .useTextWidth()
                .alignLeft(sectionOffset)
//                .onPress(() -> Minecraft.getInstance().setScreen(new ConfigScreen(this.homeScreen))) //TODO
                .backgroundRenderer(this::renderLeftTransparent)
                .build(this.homeScreen::addWidget);

        ButtonWidget presets = ButtonWidget.create(Lang.Enum.SCREEN_PACKS)
                .icon(Icons.ZIP_FILE)
                .alignFlushTo(config)
                .below(config, sectionMargin)
                .useTextWidth()
                .alignLeft(sectionOffset)
                .onPress(() -> Minecraft.getInstance().setScreen(new PacksListScreen(this.homeScreen)))
                .backgroundRenderer(this::renderLeftTransparent)
                .build(this.homeScreen::addWidget);

        ButtonWidget discord = ButtonWidget.create(Lang.Home.DISCORD)
                .icon(Icons.DISCORD)
                .alignFlushTo(presets)
                .below(presets, sectionMargin)
                .useTextWidth()
                .alignLeft(sectionOffset)
                .onPress(LinkUtil.onPress(LinkLocation.DISCORD))
                .backgroundRenderer(this::renderLeftTransparent)
                .build(this.homeScreen::addWidget);

        ButtonWidget kofi = ButtonWidget.create(Lang.Home.KOFI)
                .icon(Icons.KOFI)
                .alignFlushTo(discord)
                .below(discord, sectionMargin)
                .useTextWidth()
                .alignLeft(sectionOffset)
                .onPress(LinkUtil.onPress(LinkLocation.KOFI))
                .backgroundRenderer(this::renderLeftTransparent)
                .build(this.homeScreen::addWidget);

        ButtonWidget golden = ButtonWidget.create(Lang.Home.GOLDEN_DAYS)
                .icon(Icons.GOLDEN_DAYS)
                .alignFlushTo(kofi)
                .below(kofi, sectionMargin)
                .useTextWidth()
                .alignLeft(sectionOffset)
                .onPress(LinkUtil.onPress(LinkLocation.GOLDEN_DAYS))
                .backgroundRenderer(this::renderLeftTransparent)
                .build(this.homeScreen::addWidget);

        SeparatorWidget separator = SeparatorWidget.create(Color.WHITE)
                .height(1)
                .below(golden, 2)
                .build(this.homeScreen::addWidget);

        ButtonWidget.create(Lang.Affirm.QUIT_CANCEL)
                .icon(Icons.GO_BACK)
                .alignFlushTo(golden)
                .below(separator, 1)
                .useTextWidth()
                .alignLeft(sectionOffset)
                .onPress(this.homeScreen::onClose)
                .backgroundRenderer(this::renderLeftTransparent)
                .build(this.homeScreen::addWidget);

        List<DynamicWidget<?, ?>> extended = this.homeScreen.getWidgets().stream().toList();

        this.homeScreen.getWidgets().stream().map(DynamicWidget::getBuilder).forEach(builder -> {
            if (builder instanceof LayoutBuilder<?, ?> layout)
                layout.extendWidthToLargest(extended);
        });

        config.getBuilder().posY(() -> {
            int spacing = (extended.size() - 1) * (20 + sectionMargin);
            return Math.round(MathUtil.center(spacing - sectionMargin, GuiUtil.getGuiHeight()));
        });

        /* Panorama */

        IconWidget panoramaLast = IconTemplate.button(Icons.SMALL_REWIND, Icons.SMALL_REWIND_HOVER, Icons.SMALL_REWIND_OFF)
                .posX(38)
                .fromScreenEndY(3)
                .cannotFocus()
                .tooltip(Lang.Home.PREV_PANORAMA, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Home.PREV_PANORAMA_INFO, 35)
                .visibleIf(() -> Minecraft.getInstance().level == null)
                .onPress(NTPanorama::backward)
                .build(this.homeScreen::addWidget);

        Supplier<TextureIcon> cycleIcon = () -> this.getPlayOrPause(Icons.SMALL_PLAY, Icons.SMALL_PAUSE);
        Supplier<TextureIcon> cycleHover = () -> this.getPlayOrPause(Icons.SMALL_PLAY_HOVER, Icons.SMALL_PAUSE_HOVER);
        Supplier<TextureIcon> cyclePressed = () -> this.getPlayOrPause(Icons.SMALL_PLAY_OFF, Icons.SMALL_PAUSE_OFF);

        IconWidget panoramaCycle = IconTemplate.button(cycleIcon, cycleHover, cyclePressed)
                .cannotFocus()
                .rightOf(panoramaLast, 1)
                .tooltip(Lang.Home.CYCLE_PANORAMA, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Home.CYCLE_PANORAMA_INFO, 35)
                .visibleIf(() -> Minecraft.getInstance().level == null)
                .onPress(() -> {
                    if (NTPanorama.isPaused())
                        NTPanorama.unpause();
                    else
                        NTPanorama.pause();
                })
                .build(this.homeScreen::addWidget);

        IconTemplate.button(Icons.SMALL_NEXT, Icons.SMALL_NEXT_HOVER, Icons.SMALL_NEXT_OFF)
                .cannotFocus()
                .rightOf(panoramaCycle, 1)
                .tooltip(Lang.Home.NEXT_PANORAMA, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Home.NEXT_PANORAMA_INFO, 35)
                .visibleIf(() -> Minecraft.getInstance().level == null)
                .onPress(NTPanorama::forward)
                .build(this.homeScreen::addWidget);

        /* Extras */

        IntegerHolder tabOrder = IntegerHolder.create(4);

        if (ModTracker.SODIUM.isInstalled())
            tabOrder.getAndIncrement();

        //TODO
//        if (WarningOverlay.isActive())
//            tabOrder.getAndIncrement();

        ButtonWidget debug = ButtonWidget.create()
                .icon(Icons.BUG)
                .tooltip(Lang.Home.DEBUG, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.HOME_DEBUG, 35)
                .fromScreenEndX(1)
                .fromScreenEndY(1)
                .tabOrderGroup(tabOrder.getAndDecrement())
                .onPress(() -> new DebugOverlay().open())
                .build(this.homeScreen::addWidget);

        ButtonWidget heart = ButtonWidget.create()
                .icon(this::getHeart, 12)
                .tooltip(Lang.Home.SUPPORTERS, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.HOME_SUPPORTERS, 35)
                .leftOf(debug, 1)
                .tabOrderGroup(tabOrder.getAndDecrement())
                .onPress(() -> new SupporterOverlay().open())
                .build(this.homeScreen::addWidget);

        this.heartOutline.pos(heart::getIconX, heart::getIconY)
                .visibleIf(heart::isHoveredOrFocused)
                .build(this.homeScreen::addWidget);

        ButtonWidget init = ButtonWidget.create()
                .icon(Icons.SAVE_FLOPPY)
                .tooltip(Lang.Home.INIT_CONFIG, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Tooltip.HOME_INIT, 35)
                .leftOf(heart, 1)
                .tabOrderGroup(tabOrder.getAndDecrement())
//                .onPress(SetupOverlay::open) //TODO
                .build(this.homeScreen::addWidget);

        if (ModTracker.SODIUM.isInstalled()) {
            ButtonWidget.create()
                    .icon(Icons.SODIUM)
                    .tooltip(Lang.Home.SODIUM_TITLE, 35, 500L, TimeUnit.MILLISECONDS)
                    .infoTooltip(Lang.Tooltip.HOME_SODIUM, 35)
                    .leftOf(init, 1)
                    .tabOrderGroup(tabOrder.getAndDecrement())
                    .onPress(SodiumOverlay::open)
                    .build(this.homeScreen::addWidget);
        }

        //TODO
//        if (WarningOverlay.isActive()) {
//            ButtonWidget.create()
//                    .icon(Icons.WARNING)
//                    .tooltip(Lang.Home.WARNING_OVERLAY, 35, 500L, TimeUnit.MILLISECONDS)
//                    .infoTooltip(Lang.Tooltip.HOME_WARNING, 35)
//                    .tabOrderGroup(tabOrder.getAndDecrement())
//                    .onPress(WarningOverlay::open)
//                    .leftOf(this.homeScreen.getWidgets().getLast(), 1)
//                    .build(this.homeScreen::addWidget);
//        }

        WarningBanner.setupIfNeeded(this.homeScreen);

        /* Mod Information */

        String version = "3.0.0";//NostalgicTweaks.getTinyVersion(); //TODO
        String beta = "INDEV";//NostalgicTweaks.getBetaVersion(); //TODO

        TextWidget.create("Made by Adrenix\n" + "Version: v" + version + (beta.isEmpty() ? "" : "-" + beta))
                .onPress(LinkUtil.onPress(LinkLocation.GITHUB))
                .color(Color.fromFormatting(ChatFormatting.GRAY))
                .useTextWidth()
                .centerAligned()
                .centerInScreenX()
                .fromScreenEndY(1)
                .tabOrderGroup(tabOrder.getAndDecrement())
                .hoverCursor(CursorTypes.POINTING_HAND) //TODO, maybe should be part of TextWidget?
                .build(this.homeScreen::addWidget);
    }

    /**
     * @return A {@link TextureIcon} heart.
     */
    private TextureIcon getHeart() {
        this.flashTimer.getFlag();
        if (false/*!ModTweak.OPENED_SUPPORTER_SCREEN.get()*/) //TODO
            return Icons.HEART_EMPTY;

        return Icons.HEART;
    }

    /**
     * Get the correct play/pause icon based on panorama context.
     *
     * @param play  The play {@link TextureIcon}.
     * @param pause The pause {@link TextureIcon}.
     * @return A {@link TextureIcon} instanced based on panorama context.
     */
    private TextureIcon getPlayOrPause(TextureIcon play, TextureIcon pause) {
        if (NTPanorama.isPaused())
            return play;

        return pause;
    }

    /**
     * Handler method for rendering a transparent section button background.
     *
     * @param button      A {@link ButtonWidget} instance.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The current x-position of the mouse.
     * @param mouseY      The current y-position of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F-1.0F].
     */
    private void renderLeftTransparent(ButtonWidget button, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        final Color bar = Color.BLACK.fromAlpha(0.0F);
        final Color fill = Color.BLACK.fromAlpha(0.6F);

        if (button.isHoveredOrFocused()) {
            bar.set(Color.LEMON_YELLOW);
            bar.setAlpha(1.0F);

            graphics.fill(button.getX(), button.getY(), button.getEndX(), button.getEndY(), fill.get());
        }

        graphics.fill(button.getX(), button.getY(), button.getX() + 2, button.getEndY(), bar.get());
    }
}
