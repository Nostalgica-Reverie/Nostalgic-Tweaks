//package mod.adrenix.nostalgic.client.gui.screen.home.overlay.warning;
//
//import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
//import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
//import mod.adrenix.nostalgic.client.gui.widget.group.Group;
//import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
//import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
//import mod.adrenix.nostalgic.util.common.asset.Icons;
//import mod.adrenix.nostalgic.util.common.data.NullableHolder;
//import mod.adrenix.nostalgic.util.common.lang.Lang;
//
//public class WarningOverlay {
//    /**
//     * Open a new mod issues warning overlay.
//     *
//     * @return The setup {@link Overlay} instance.
//     */
//    public static Overlay open() {
//        return getOverlay().open();
//    }
//
//    /**
//     * @return Whether there are active mod warnings that need to be seen by the user.
//     */
//    public static boolean isActive() {
//        return ModWarning.stream().anyMatch(ModWarning::isActive);
//    }
//
//    /**
//     * @return The {@link Overlay} for mod issue warnings.
//     */
//    private static Overlay getOverlay() {
//        int padding = 2;
//
//        final Overlay overlay = Overlay.create(Lang.Home.WARNING_OVERLAY)
//                .icon(Icons.SMALL_WARNING)
//                .padding(padding)
//                .resizeUsingPercentage(0.5D)
//                .resizeHeightForWidgets()
//                .build();
//
//        NullableHolder<DynamicWidget<?, ?>> previous = NullableHolder.empty();
//
//        for (ModWarning warning : ModWarning.values()) {
//            if (!warning.isActive())
//                continue;
//
//            Group group = Group.create(overlay)
//                    .extendWidthToScreenEnd(0)
//                    .below(previous.get(), padding * 2)
//                    .border(warning.getIssue().getColor())
//                    .build(overlay::addWidget);
//
//            TextWidget header = TextWidget.create(warning.getIssue().getTitle())
//                    .scale(1.2F)
//                    .posY(-3)
//                    .centerAligned()
//                    .extendWidthToEnd(group, group.getInsidePaddingX())
//                    .icon(warning.getIssue().getIcon())
//                    .color(warning.getIssue().getColor())
//                    .build(group::addWidget);
//
//            SeparatorWidget separator = SeparatorWidget.create(warning.getIssue().getColor())
//                    .height(1)
//                    .extendWidthToEnd(group, group.getInsidePaddingX())
//                    .below(header, padding * 2)
//                    .build(group::addWidget);
//
//            TextWidget.create(warning.getDescription())
//                    .extendWidthToEnd(group, group.getInsidePaddingX())
//                    .below(separator, padding * 3)
//                    .build(group::addWidget);
//
//            previous.set(group);
//        }
//
//        return overlay;
//    }
//}
