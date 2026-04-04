package mod.adrenix.nostalgic.client.gui.screen.home.overlay;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.state.SwitchGroup;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.text.TextUtil;

import java.util.function.Consumer;

//import mod.adrenix.nostalgic.network.ModConnection;

public class DebugOverlay {
    /* Fields */

    private final Overlay overlay;

    /* Constructor */

    public DebugOverlay() {
        int padding = 2;

        this.overlay = Overlay.create(Lang.Home.DEBUG)
                .icon(Icons.BUG)
                .padding(padding)
                .resizeUsingPercentage(0.6D)
                .build();

        /* Toggles */

        Group toggle = Group.create(this.overlay)
                .title(Lang.Text.TOGGLE)
                .icon(Icons.BUG)
                .extendWidthToScreenEnd(0)
                .border(Color.ALERT_RED)
                .build(this.overlay::addWidget);

        Translation debugHead = Lang.Manage.OPERATIONS_DEBUG;
        Translation debugInfo = Lang.Home.DEBUG_SWITCH;

        SwitchGroup.Widgets debugSwitch = SwitchGroup.create(toggle, debugHead, debugInfo, NostalgicTweaks::isDebugging, NostalgicTweaks.LOGGER::setDebug)
                .getWidgets();

        debugSwitch.header().getBuilder().extendWidthToEnd(toggle, toggle.getInsidePaddingX());
        debugSwitch.description().getBuilder().extendWidthToEnd(toggle, toggle.getInsidePaddingX());
        debugSwitch.subscribeTo(toggle);

        SeparatorWidget toggleSeparator = SeparatorWidget.create(toggle.getColor())
                .below(debugSwitch.description(), padding * 2)
                .width(toggle::getInsideWidth)
                .build(toggle::addWidget);

        Translation fpsHead = Lang.Home.FPS_SWITCH;
        Translation fpsInfo = Lang.Home.FPS_INFO;

        SwitchGroup.Widgets fpsSwitch = SwitchGroup.create(toggle, fpsHead, fpsInfo, GuiUtil::isShowingFps, GuiUtil::setShowFps)
                .getWidgets();

        fpsSwitch.toggle().getBuilder().below(toggleSeparator, padding * 2);
        fpsSwitch.header().getBuilder().extendWidthToEnd(toggle, toggle.getInsidePaddingX());
        fpsSwitch.description().getBuilder().extendWidthToEnd(toggle, toggle.getInsidePaddingX());
        fpsSwitch.subscribeTo(toggle);

        /* Details */

        //TODO
//        ModConnection connection = NostalgicTweaks.getConnection().orElseGet(ModConnection::disconnected);

        Group details = Group.create(this.overlay)
                .title(Lang.Home.DEBUG_INFO)
                .icon(Icons.CLIPBOARD)
                .extendWidthToScreenEnd(0)
                .below(toggle, padding)
                .border(Color.DEER_BROWN)
                .build(this.overlay::addWidget);

        TextWidget shortcut = TextWidget.create(Lang.Home.DEBUG_SHORTCUT)
                .width(details::getInsideWidth)
                .build(details::addWidget);

        Holder<TextWidget> text = Holder.create(shortcut);

        NullableHolder<DynamicWidget<?, ?>> separator = NullableHolder.create(TextWidget.create("Mod Loader")
                .centerAligned()
                .color(Color.NOSTALGIC_GRAY)
                .separator(details.getColor())
                .below(shortcut, padding * 2)
                .width(details::getInsideWidth)
                .build(details::addWidget));

        Consumer<String> debug = (output) -> {
            DynamicWidget<?, ?> below = separator.orElse(text.get());

            text.set(TextWidget.create(output)
                    .below(below, separator.isPresent() ? padding * 2 : padding)
                    .width(details::getInsideWidth)
                    .build(details::addWidget));

            separator.set(null);
        };

        Consumer<String> separate = (title) -> separator.set(TextWidget.create(title)
                .centerAligned()
                .color(Color.NOSTALGIC_GRAY)
                .separator(details.getColor())
                .below(text.get(), padding * 2)
                .width(details::getInsideWidth)
                .build(details::addWidget));

        //TODO
//        debug.accept(String.format("Loader: §d%s", NostalgicTweaks.getLoader()));
//        debug.accept(String.format("Version: §e%s", NostalgicTweaks.getShortVersion()));
//        debug.accept(String.format("Protocol: §b%s", NostalgicTweaks.PROTOCOL));
//
//        separate.accept("Network");
//
//        debug.accept(this.getColored("Singleplayer: %s", NetUtil.isSingleplayer()));
//        debug.accept(this.getColored("Multiplayer: %s", NetUtil.isMultiplayer()));
//        debug.accept(this.getColored("Connected: %s", NetUtil.isConnected()));
//        debug.accept(this.getColored("Operator: %s", NetUtil.isPlayerOp()));
//        debug.accept(this.getColored("Verified: %s", NostalgicTweaks.isNetworkVerified()));
//        debug.accept(this.getColored("Local Host: %s", NetUtil.isLocalHost()));
//
//        separate.accept("Server");
//
//        debug.accept(String.format("Server Protocol: §b%s", connection.getProtocol()));
//        debug.accept(String.format("Server Version: §e%s", connection.getVersion()));
//        debug.accept(String.format("Server Loader: §d%s", connection.getLoader()));
//        debug.accept(this.getColored("Connected: %s", NostalgicTweaks.getConnection().isPresent()));

        separate.accept("Mod Tracker");

        for (ModTracker tracker : ModTracker.values())
            debug.accept(this.getColored(TextUtil.toTitleCase(tracker.toString() + ": %s"), tracker.isInstalled()));
    }

    /* Methods */

    public void open() {
        this.overlay.open();
    }

    /**
     * Get a colored formatted string based on the given format and value.
     *
     * @param format The string to format.
     * @param value  The value to check if coloring can be applied.
     * @return A formatted string with color.
     */
    private String getColored(String format, Object value) {
        String colored = String.format("§f%s", value);

        if (value instanceof Boolean flag)
            colored = String.format("%s%s", flag ? "§2" : "§4", value);

        return String.format(format, colored);
    }
}
