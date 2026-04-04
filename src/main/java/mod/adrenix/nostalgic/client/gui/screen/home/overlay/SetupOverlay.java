//package mod.adrenix.nostalgic.client.gui.screen.home.overlay;
//
//import mod.adrenix.nostalgic.client.AfterConfigSave;
//import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
//import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
//import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
//import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
//import mod.adrenix.nostalgic.client.gui.widget.group.Group;
//import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
//import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
//import mod.adrenix.nostalgic.config.cache.ConfigCache;
//import mod.adrenix.nostalgic.tweak.factory.Tweak;
//import mod.adrenix.nostalgic.tweak.factory.TweakListing;
//import mod.adrenix.nostalgic.tweak.factory.TweakPool;
//import mod.adrenix.nostalgic.util.common.ClassUtil;
//import mod.adrenix.nostalgic.util.common.asset.Icons;
//import mod.adrenix.nostalgic.util.common.color.Color;
//import mod.adrenix.nostalgic.util.common.data.FlagHolder;
//import mod.adrenix.nostalgic.util.common.lang.Lang;
//
//import java.util.function.BiConsumer;
//
//public abstract class SetupOverlay {
//    /**
//     * Flag holder for tracking the state of the overlay.
//     */
//    public static final FlagHolder OPENED = FlagHolder.off();
//
//    /**
//     * Open a new initial config setup overlay.
//     *
//     * @return The setup {@link Overlay} instance.
//     */
//    public static Overlay open() {
//        Overlay overlay = getOverlay();
//
//        OPENED.enable();
//        overlay.runOnClose(OPENED::disable);
//
//        return overlay.open();
//    }
//
//    /**
//     * @return The {@link Overlay} for initial config setup.
//     */
//    private static Overlay getOverlay() {
//        int padding = 2;
//
//        final Overlay overlay = Overlay.create(Lang.Home.INIT_CONFIG)
//                .icon(Icons.SAVE_FLOPPY)
//                .padding(padding)
//                .resizeUsingPercentage(0.6D)
//                .resizeHeightForWidgets()
//                .build();
//
//        Group setup = Group.create(overlay)
//                .icon(Icons.CLIPBOARD)
//                .title(Lang.Home.SETUP)
//                .border(Color.DEER_BROWN)
//                .extendWidthToScreenEnd(0)
//                .build(overlay::addWidget);
//
//        TextWidget setupInfo = TextWidget.create(Lang.Home.SETUP_INFO)
//                .width(setup::getInsideWidth)
//                .build(setup::addWidget);
//
//        SeparatorWidget top = SeparatorWidget.create(setup.getColor())
//                .height(1)
//                .below(setupInfo, padding * 2)
//                .width(setup::getInsideWidth)
//                .build(setup::addWidget);
//
//        final FlagHolder defaultFlag = FlagHolder.on();
//        final FlagHolder disableFlag = FlagHolder.off();
//
//        FlagHolder.radio(defaultFlag, disableFlag);
//
//        ButtonWidget defaultSetup = ButtonTemplate.checkbox(Lang.Home.DEFAULT, defaultFlag::get)
//                .extendWidthToEnd(setup, setup.getInsidePaddingX())
//                .below(top, padding * 2)
//                .onPress(defaultFlag::toggle)
//                .build(setup::addWidget);
//
//        TextWidget defaultInfo = TextWidget.create(Lang.Home.DEFAULT_INFO)
//                .posX(Icons.CHECKBOX.getWidth() + 5)
//                .extendWidthToEnd(setup, setup.getInsidePaddingX())
//                .below(defaultSetup, padding)
//                .build(setup::addWidget);
//
//        ButtonWidget disableSetup = ButtonTemplate.checkbox(Lang.Home.DISABLE, disableFlag::get)
//                .extendWidthToEnd(setup, setup.getInsidePaddingX())
//                .below(defaultInfo, padding * 2)
//                .onPress(disableFlag::toggle)
//                .build(setup::addWidget);
//
//        TextWidget disableInfo = TextWidget.create(Lang.Home.DISABLE_INFO)
//                .posX(Icons.CHECKBOX.getWidth() + 5)
//                .extendWidthToEnd(setup, setup.getInsidePaddingX())
//                .below(disableSetup, padding)
//                .build(setup::addWidget);
//
//        SeparatorWidget separator = SeparatorWidget.create(setup.getColor())
//                .height(1)
//                .below(disableInfo, padding * 2)
//                .width(setup::getInsideWidth)
//                .build(setup::addWidget);
//
//        Grid grid = Grid.create(setup, 2)
//                .columnSpacing(1)
//                .extendWidthToEnd(setup, setup.getInsidePaddingX())
//                .below(separator, padding * 2)
//                .build(setup::addWidget);
//
//        Runnable apply = () -> {
//            BiConsumer<Tweak<Object>, Object> processor = (tweak, value) -> {
//                TweakListing<?, ?> listing = ClassUtil.cast(tweak, TweakListing.class).orElse(null);
//
//                if (listing != null)
//                    listing.fromLocal().setDisabled(disableFlag.get());
//                else
//                    tweak.setLocal(value);
//            };
//
//            if (defaultFlag.get())
//                TweakPool.automated().forEach(tweak -> processor.accept(tweak, tweak.getDefault()));
//
//            if (disableFlag.get())
//                TweakPool.automated().forEach(tweak -> processor.accept(tweak, tweak.getDisabled()));
//
//            TweakPool.stream()
//                    .filter(Tweak::isNotIgnored)
//                    .filter(Tweak::isAnyCacheSavable)
//                    .forEach(Tweak::applyCacheAndSend);
//
//            ConfigCache.save();
//            AfterConfigSave.run();
//
//            overlay.close();
//        };
//
//        ButtonWidget.create(Lang.Button.APPLY).icon(Icons.GREEN_CHECK).onPress(apply).build(grid::addCell);
//        ButtonWidget.create(Lang.Vanilla.GUI_CANCEL).icon(Icons.RED_X).onPress(overlay::close).build(grid::addCell);
//
//        overlay.setFocused(defaultSetup);
//
//        return overlay;
//    }
//}
