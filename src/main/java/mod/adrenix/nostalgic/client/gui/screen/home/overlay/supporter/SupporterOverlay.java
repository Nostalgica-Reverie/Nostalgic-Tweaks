package mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.InternetTexture;
import mod.adrenix.nostalgic.util.common.LinkLocation;
import mod.adrenix.nostalgic.util.common.ThreadMaker;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.ModAsset;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//import mod.adrenix.nostalgic.tweak.config.ModTweak;

public class SupporterOverlay {
    static final AtomicInteger THREAD_ID = new AtomicInteger(0);

    /* Static */
    static final HashSet<String> NAME_KEYS = new HashSet<>();
    static final HashMap<String, Color> NAMES = new HashMap<>();
    static final HashMap<String, PlayerFace> FACES = new HashMap<>();
    /**
     * Version tracking for supporter json data. This will ensure the mod doesn't use a downloaded json that will not
     * work on this version of the mod.
     */
    private static final int JSON_VERSION = 2;
    @Nullable
    private static GithubJson cache = null;
    private static boolean isConnecting = false;
    private static boolean isDataInvalid = false;
    private static boolean isVersionWrong = false;

    /* Fields */
    final Overlay overlay;
    final RowList rowList;

    /* Constructor */

    public SupporterOverlay() {
        this.overlay = Overlay.create(Lang.Home.SUPPORTERS)
                .icon(Icons.HEART)
                .resizeUsingPercentage(0.7D)
                .runOnKeyPressed(this::keyPressed)
                .build();

        this.rowList = RowList.create()
                .useSeparators(Color.GRAY, 1, 2)
                .emptyMessage(this::getEmptyMessage)
                .defaultRowHeight(GuiUtil.textHeight())
                .extendWidthToScreenEnd(0)
                .extendHeightToScreenEnd(0)
                .build(this.overlay::addWidget);

        ButtonWidget reconnect = ButtonWidget.create(Lang.Button.RECONNECT)
                .icon(Icons.REDO)
                .hoverIcon(Icons.REDO_HOVER)
                .useTextWidth()
                .centerInScreenX()
                .cooldown(4L, TimeUnit.SECONDS)
                .onPress(this::clearAndReconnect)
                .visibleIf(this::isReconnectVisible)
                .enableIf(this::isReconnectActive)
                .below(this.rowList.getEmptyMessage(), 2)
                .build(this.overlay::addWidget);

        this.rowList.getEmptyMessage().getBuilder().centerInWidgetY(this.rowList, () -> reconnect.isVisible() ? 22 : 0);
    }

    /* Methods */

    /**
     * Check if the name cache needs built.
     */
    private static void checkIfNamesNeedBuilt() {
        if (cache == null && !isConnecting) {
            isConnecting = true;
            ThreadMaker.create(getThreadName(), SupporterOverlay::getSupporterJson, () -> isConnecting = false).start();
        } else if (cache != null && NAMES.isEmpty()) {
            for (Map.Entry<String, GithubJson.Supporter> entry : cache.supporters.entrySet()) {
                NAME_KEYS.add(entry.getKey());
                NAMES.put(entry.getKey(), new Color(entry.getValue().color));
            }
        }
    }

    /**
     * @return A {@link HashMap} of supporter names and their respective color.
     */
    public static HashMap<String, Color> getNames() {
        checkIfNamesNeedBuilt();

        return NAMES;
    }

    /**
     * @return A {@link HashSet} of supporter name keys used in the supporter names map.
     */
    public static HashSet<String> getNameKeys() {
        checkIfNamesNeedBuilt();

        return NAME_KEYS;
    }

    /**
     * @return A thread name used by the json downloader thread.
     */
    private static String getThreadName() {
        return "Supporter Connector #" + THREAD_ID.incrementAndGet();
    }

    /**
     * Thread locking method that downloads json data from GitHub.
     */
    private static void getSupporterJson() {
        isConnecting = true;

        try {
            Thread.sleep(1000L);
            NostalgicTweaks.LOGGER.info("Connecting to %s", LinkLocation.SUPPORTERS);

            URL url = new URI(LinkLocation.SUPPORTERS).toURL();
            InputStreamReader reader = new InputStreamReader(url.openStream());
            SupporterOverlay.cache = new Gson().fromJson(reader, GithubJson.class);

            NostalgicTweaks.LOGGER.info("Successfully downloaded supporter data from GitHub");

            if (cache == null)
                return;

            if (JSON_VERSION < cache.version) {
                isVersionWrong = true;
                return;
            }

            Map<String, GithubJson.Supporter> fromJson = cache.supporters;

            for (Map.Entry<String, GithubJson.Supporter> supporter : fromJson.entrySet()) {
                String name = supporter.getKey();
                String uuid = supporter.getValue().uuid;

                if (uuid == null)
                    continue;

                Identifier location = ModAsset.supporter(name);
                InternetTexture texture = new InternetTexture(LinkLocation.getSupporterFace(uuid), location);

                FACES.putIfAbsent(name, new PlayerFace(location, texture));
            }
        } catch (Exception exception) {
            NostalgicTweaks.LOGGER.error("Could not gather needed supporter data\n%s", exception);
            isDataInvalid = true;
        }
    }

    /**
     * Open the supporter overlay.
     */
    public void open() {
        //TODO
//        if (!ModTweak.OPENED_SUPPORTER_SCREEN.get())
//            ModTweak.OPENED_SUPPORTER_SCREEN.setDiskAndSave(true);

        if (cache == null)
            this.connect();
        else
            this.build();

        this.overlay.open();
    }

    /**
     * Clears static cache, reconnects, and downloads supporter data.
     */
    private void clearAndReconnect() {
        cache = null;
        isVersionWrong = false;
        isDataInvalid = false;

        FACES.clear();

        this.rowList.clear();
        this.connect();
    }

    /**
     * Keyboard shortcut to reload downloadable content.
     */
    private boolean keyPressed(Overlay overlay, KeyEvent event) {
        if (NostalgicTweaks.isDeveloping() && event.hasShiftDown() && event.hasControlDown() && event.key() == InputConstants.KEY_I) {
            this.clearAndReconnect();
            return true;
        }

        return false;
    }

    /**
     * @return Whether the "reconnect" button is active.
     */
    private boolean isReconnectActive() {
        return !isConnecting && !isVersionWrong && !isDataInvalid;
    }

    /**
     * @return Whether the "reconnect" button is visible.
     */
    private boolean isReconnectVisible() {
        return this.rowList.getVisibleRows().isEmpty();
    }

    /**
     * @return The {@link Component} for the empty list message.
     */
    private Component getEmptyMessage() {
        if (!isConnecting) {
            if (isVersionWrong)
                return Lang.Home.WRONG_VERSION.withStyle(ChatFormatting.RED);

            if (isDataInvalid)
                return Lang.Home.INVALID_DATA.withStyle(ChatFormatting.RED);

            return Lang.Home.DISCONNECTED.withStyle(ChatFormatting.RED);
        } else
            return Lang.Home.CONNECTING.withStyle(ChatFormatting.YELLOW);
    }

    /**
     * Stops the connection and builds the downloaded data into the row list on the main thread.
     */
    private void callback() {
        Minecraft.getInstance().execute(() -> {
            this.build();
            isConnecting = false;
        });
    }

    /**
     * Establish a connection with GitHub in a separate thread to download the supporter JSON file from the mod's data
     * branch.
     */
    private void connect() {
        ThreadMaker.create(getThreadName(), SupporterOverlay::getSupporterJson, this::callback).start();
    }

    /**
     * Creates the supporter message and message width cache for this banner instance.
     */
    private void build() {
        if (cache == null || isVersionWrong || isDataInvalid)
            return;

        FACES.forEach((username, face) -> face.register());

        for (Map.Entry<String, GithubJson.Supporter> entry : cache.supporters.entrySet())
            new SupporterRow(this, entry.getKey(), entry.getValue());
    }
}
