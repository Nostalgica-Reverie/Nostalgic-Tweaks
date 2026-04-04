package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class KeyboardUtil {
    /* Key Press Helpers */

    /**
     * This cache prevents the need to cycle through all the key mappings. Extremely useful for when there are a lot of
     * key mappings.
     */
    private static final Map<String, KeyMapping> MAPPING_CACHE = new HashMap<>();

    /**
     * @param input A given {@link InputWithModifiers} for checking inputs.
     * @return Checks if the shift, ctrl, or alt key is held down.
     */
    @PublicAPI
    public static boolean isModifierDown(InputWithModifiers input) {
        return input.hasShiftDown() || input.hasControlDown() || input.hasAltDown();
    }

    /**
     * A key combination for moving right.
     *
     * @param event A {@link KeyEvent} for the keypress.
     * @return Whether the ctrl or alt key is held down, and the right arrow key is pressed.
     */
    @PublicAPI
    public static boolean isGoingRight(KeyEvent event) {
        if (event.hasShiftDown())
            return false;

        return (event.hasControlDown() || event.hasAltDown()) && event.key() == InputConstants.KEY_RIGHT;
    }

    /**
     * A key combination for moving left.
     *
     * @param event A {@link KeyEvent} for the keypress.
     * @return Whether the ctrl or alt key is held down and the left arrow key is pressed.
     */
    @PublicAPI
    public static boolean isGoingLeft(KeyEvent event) {
        if (event.hasShiftDown())
            return false;

        return (event.hasControlDown() || event.hasAltDown()) && event.key() == InputConstants.KEY_LEFT;
    }

    /**
     * A key combination for searching.
     *
     * @param event A {@link KeyEvent} for the keypress.
     * @return Whether the ctrl key is held down and the F key is pressed.
     */
    @PublicAPI
    public static boolean isSearching(KeyEvent event) {
        if (event.hasShiftDown())
            return false;

        return event.hasControlDown() && event.key() == InputConstants.KEY_F;
    }

    /**
     * A key combination for saving.
     *
     * @param event A {@link KeyEvent} for the keypress.
     * @return Whether the ctrl key is held down and the S key is pressed.
     */
    @PublicAPI
    public static boolean isSaving(KeyEvent event) {
        if (event.hasShiftDown())
            return false;

        return event.hasControlDown() && event.key() == InputConstants.KEY_S;
    }

    /**
     * A key combination for selecting everything.
     *
     * @param event A {@link KeyEvent} for the keypress.
     * @return Whether the ctrl key is held down and the A key is pressed.
     */
    @PublicAPI
    public static boolean isSelectAll(KeyEvent event) {
        if (event.hasShiftDown())
            return false;

        return event.hasControlDown() && event.key() == InputConstants.KEY_A;
    }

    /**
     * This returns true if the number pad enter key is pressed, the space key is pressed, or the keyboard enter key is
     * pressed. Use the {@code see also} method if the space key is not acceptable.
     *
     * @param key A pressed key.
     * @return Whether the number pad enter key, space key, or keyboard enter key is pressed.
     * @see #isReturnOrEnter(int)
     */
    @PublicAPI
    public static boolean isEnterLike(int key) {
        return key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER || key == InputConstants.KEY_SPACE;
    }

    /**
     * This returns true if the number pad enter key is pressed or the keyboard enter key is pressed. Use the
     * {@code see also} method is the space key is also acceptable.
     *
     * @param key A pressed key.
     * @return Whether the number pad enter key or keyboard enter key is pressed.
     * @see #isEnterLike(int)
     */
    @PublicAPI
    public static boolean isReturnOrEnter(int key) {
        return key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER;
    }

    /**
     * Checks if the Tab key is pressed.
     *
     * @param key A pressed key.
     * @return Whether the Tab key was pressed.
     */
    @PublicAPI
    public static boolean isTab(int key) {
        return key == InputConstants.KEY_TAB;
    }

    /**
     * Checks if the Esc key is pressed.
     *
     * @param key A pressed key.
     * @return Whether the Esc key was pressed.
     */
    @PublicAPI
    public static boolean isEsc(int key) {
        return key == InputConstants.KEY_ESCAPE;
    }

    /**
     * Check if the given keycode is being held down.
     *
     * @param key The keycode to check.
     * @return Whether the given key is being held down.
     */
    @PublicAPI
    public static boolean isDown(int key) {
        return InputConstants.isKeyDown(GuiUtil.getWindow(), key);
    }

    /**
     * Checks if any of the given keys matches the given key code.
     *
     * @param keyCode A key code to check against.
     * @param keys    A varargs list of keys to check.
     * @return Whether any of the given keys matched the given key code.
     */
    @PublicAPI
    public static boolean match(int keyCode, int... keys) {
        return Arrays.stream(keys).anyMatch(key -> key == keyCode);
    }

    /* Key Helpers */

    /**
     * Finds a key mapping based on the given {@code langKey}.
     *
     * @param langKey The key to search for in the game's key mappings.
     * @return A key mapping if one was found.
     */
    @PublicAPI
    public static Optional<KeyMapping> find(String langKey) {
        KeyMapping cache = MAPPING_CACHE.get(langKey);

        if (cache != null)
            return Optional.of(cache);

        KeyMapping[] allMappings = Minecraft.getInstance().options.keyMappings;

        for (KeyMapping keyMapping : allMappings) {
            if (keyMapping.getName().equals(langKey)) {
                MAPPING_CACHE.put(langKey, keyMapping);
                return Optional.of(keyMapping);
            }
        }

        return Optional.empty();
    }

    /**
     * Finds a key mapping based on the given argument.
     *
     * @param langKey A {@link Translation} instance.
     * @return A key mapping if one was found.
     */
    @PublicAPI
    public static Optional<KeyMapping> find(Translation langKey) {
        return find(langKey.langKey());
    }

    /**
     * Checks if the given mapping conflicts with another key mapping.
     *
     * @param mapping A key mapping instance.
     * @return Whether the key mapping conflicts with another key mapping.
     */
    @PublicAPI
    public static boolean isMappingConflict(KeyMapping mapping) {
        if (!mapping.isUnbound()) {
            KeyMapping[] allMappings = Minecraft.getInstance().options.keyMappings;

            for (KeyMapping keyMapping : allMappings) {
                if (keyMapping != mapping && mapping.same(keyMapping))
                    return true;
            }
        }

        return false;
    }
}
