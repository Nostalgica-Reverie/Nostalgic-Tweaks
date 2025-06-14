package mod.adrenix.nostalgic.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.mixin.access.MusicManagerAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;

import java.util.Optional;
import java.util.function.Function;

/**
 * This helper class keeps a record of all key mappings that are used by the mod. These mappings are used by both mod
 * loaders.
 */
public abstract class ClientKeyMapping
{
    /**
     * The config key, when pressed, opens the mod's configuration user interface.
     */
    public static final KeyMapping CONFIG_KEY = new KeyMapping(Lang.Binding.OPEN_CONFIG.getString(), InputConstants.KEY_O, Lang.TITLE.getString());

    /**
     * The fog key, when pressed, toggles the game's rendering distance. The value is changed by the old predefined
     * rendering distances used before the game switched to a slider. This will cycle between the following distance;
     * far (16), normal (8), short (4), and tiny (2).
     */
    public static final KeyMapping FOG_KEY = new KeyMapping(Lang.Binding.TOGGLE_FOG.getString(), -1, Lang.TITLE.getString());

    /**
     * The next song key, when pressed, stops the current song and disables the next song delay. This will trigger a new
     * song immediately.
     */
    public static final KeyMapping NEXT_SONG_KEY = new KeyMapping(Lang.Binding.NEXT_SONG.getString(), -1, Lang.TITLE.getString());

    /**
     * The stop song key, when pressed, stops the current song if it is playing.
     */
    public static final KeyMapping STOP_SONG_KEY = new KeyMapping(Lang.Binding.STOP_SONG.getString(), -1, Lang.TITLE.getString());

    /**
     * The toggle key, when pressed, toggles the mod between the enabled and disabled state.
     */
    public static final KeyMapping TOGGLE_KEY = new KeyMapping(Lang.Binding.TOGGLE_KEY.getString(), InputConstants.KEY_HOME, Lang.TITLE.getString());

    /**
     * Get a key mapping based on the given {@link KeybindingId}.
     *
     * @param id A {@link KeybindingId} enumeration.
     * @return A {@link KeyMapping} that is linked to the given key type.
     */
    public static KeyMapping getFromId(KeybindingId id)
    {
        return switch (id)
        {
            case CONFIG -> CONFIG_KEY;
            case FOG -> FOG_KEY;
            case NEXT_SONG -> NEXT_SONG_KEY;
            case STOP_SONG -> STOP_SONG_KEY;
            case TOGGLE -> TOGGLE_KEY;
        };
    }

    /**
     * Register the client's key mappings using Architectury.
     */
    public static void register()
    {
        KeyMappingRegistry.register(CONFIG_KEY);
        KeyMappingRegistry.register(FOG_KEY);
        KeyMappingRegistry.register(STOP_SONG_KEY);
        KeyMappingRegistry.register(NEXT_SONG_KEY);
        KeyMappingRegistry.register(TOGGLE_KEY);

        ClientScreenInputEvent.KEY_PRESSED_POST.register((minecraft, screen, keyCode, scanCode, modifiers) -> {
            Function<Translation, Boolean> isBindingMatched = (translation) -> {
                Optional<KeyMapping> mapping = KeyboardUtil.find(translation);

                return mapping.isPresent() && mapping.get().matches(keyCode, scanCode);
            };

            if (isBindingMatched.apply(Lang.Binding.OPEN_CONFIG))
            {
                gotoSettingsIfPossible(minecraft);
                return EventResult.pass();
            }

            if (isBindingMatched.apply(Lang.Binding.STOP_SONG))
            {
                stopCurrentSong(minecraft);
                return EventResult.pass();
            }

            if (isBindingMatched.apply(Lang.Binding.NEXT_SONG))
            {
                playNewSong(minecraft);
                return EventResult.pass();
            }

            if (isBindingMatched.apply(Lang.Binding.TOGGLE_KEY))
            {
                toggleEnabled(minecraft);
                return EventResult.pass();
            }

            return EventResult.pass();
        });

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (CONFIG_KEY.consumeClick())
                gotoSettingsIfPossible(minecraft);

            while (FOG_KEY.consumeClick())
                cycleRenderDistance(minecraft);

            while (STOP_SONG_KEY.consumeClick())
                stopCurrentSong(minecraft);

            while (NEXT_SONG_KEY.consumeClick())
                playNewSong(minecraft);

            while (TOGGLE_KEY.consumeClick())
                toggleEnabled(minecraft);
        });
    }

    /**
     * This method provides instructions for the mod to perform after the settings screen mapping key is pressed.
     */
    private static void gotoSettingsIfPossible(Minecraft minecraft)
    {
        if (minecraft.screen != null && ClassUtil.isNotInstanceOf(minecraft.screen, TitleScreen.class))
            return;

        minecraft.setScreen(new HomeScreen(minecraft.screen, true));
    }

    /**
     * This method cycles the game's render distance after the toggle fog mapping key is pressed. The player must be in
     * a level for a cycle to take effect.
     */
    private static void cycleRenderDistance(Minecraft minecraft)
    {
        if (minecraft.level != null)
        {
            int distance = minecraft.options.renderDistance().get();

            if (distance >= 16)
                minecraft.options.renderDistance().set(8);
            else if (distance >= 8)
                minecraft.options.renderDistance().set(4);
            else if (distance >= 4)
                minecraft.options.renderDistance().set(2);
            else if (distance >= 2)
                minecraft.options.renderDistance().set(16);

            minecraft.levelRenderer.needsUpdate();
            minecraft.options.save();
        }
    }

    /**
     * This method stops the current music if it is playing.
     */
    private static void stopCurrentSong(Minecraft minecraft)
    {
        minecraft.getMusicManager().stopPlaying();
    }

    /**
     * This method stops the current music if it is playing and then resets the next song delay so that on the next tick
     * new music plays.
     */
    private static void playNewSong(Minecraft minecraft)
    {
        minecraft.getMusicManager().stopPlaying();
        ((MusicManagerAccess) minecraft.getMusicManager()).nt$setNextSongDelay(0);
    }

    private static void toggleEnabled(Minecraft minecraft)
    {
        boolean newValue = !ModTweak.ENABLED.get();
        ModTweak.ENABLED.setCacheAndDiskThenSave(newValue);
        AfterConfigSave.reloadAndRun();
    }
}
