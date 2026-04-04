package mod.adrenix.nostalgic.util.common.lang;

//import mod.adrenix.nostalgic.tweak.factory.Tweak;
//import mod.adrenix.nostalgic.tweak.factory.TweakPool;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for {@link Lang} that assists with decoding unique arguments within lang files. This is automatically
 * used by {@link Lang#component(Component)} and {@link Translation#get(Object...)}.
 * <p><br>
 * Any other translations that use decoding blocks outside of {@link Lang} that translate lang file keys must be parsed
 * through {@link DecodeLang#findAndReplace(Component)} or {@link DecodeLang#findAndReplace(String)}.
 */
public abstract class DecodeLang {
    private static final String DECODE_REGEX = "@decode\\{[^=]*=[^}]*}";
    private static final String KEY_VALUE_REGEX = "@decode\\{([^=]*)=([^}]*)}";

    /**
     * Find and replace any {@code @decode} blocks within the given {@link Component}.
     *
     * @param input A {@link Component} to examine.
     * @return A new {@link MutableComponent} with any found {@code @decode} blocks replaced with data.
     */
    public static MutableComponent findAndReplace(Component input) {
        return findAndReplace(input.getString());
    }

    /**
     * Find and replace any {@code @decode} blocks within the given string.
     *
     * @param input A String to examine.
     * @return A new {@link MutableComponent} with any found {@code @decode} blocks replaced with data.
     */
    public static MutableComponent findAndReplace(String input) {
        ArrayList<Found> results = Pattern.compile(KEY_VALUE_REGEX)
                .matcher(input)
                .results()
                .map(DecodeLang::find)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(results);

        for (Found found : results) {
            if (found.isEmpty())
                continue;

            input = replace(found, input);
        }

        return Component.literal(input);
    }

    /**
     * If the {@link MatchResult} has two groups, a {@code key=value} pair, then a new {@link Found} record is created.
     * Otherwise, a {@link Found#EMPTY} record is created.
     *
     * @param match A {@link MatchResult} to get {@code key=value} data from.
     * @return A new {@link Found} record if the match was successful, otherwise {@link Found#EMPTY}.
     */
    private static Found find(MatchResult match) {
        if (match.groupCount() == 2)
            return new Found(Key.get(match.group(1)), match.group(2), match.start(), match.end());

        return Found.EMPTY;
    }

    /**
     * This will use the given {@link Found} record to decode any {@code @decode} blocks within the given input string.
     *
     * @param found A {@link Found} record instance.
     * @param input The original input string to perform replacement in.
     * @return A new string with {@code @decode} blocks replaced with any found data associated with the
     * {@code key=value} pair.
     */
    private static String replace(Found found, String input) {
        return switch (found.key) {
            case EMPTY -> input;
            case TWEAK -> {
                yield input; //TODO
                /*String replacement = found.value;
                Optional<Tweak<?>> tweak = TweakPool.find(found.value);

                if (tweak.isPresent())
                    replacement = tweak.get().getTranslation().getString();

                yield TextUtil.replaceInRange(input, DECODE_REGEX, replacement, found.start, found.end);*/
            }
        };
    }

    /**
     * The following enumeration defines what keys are recognized by the replacement algorithm. Any unrecognized key
     * will be set to {@link Key#EMPTY} and nothing will be replaced.
     */
    private enum Key {
        EMPTY,
        TWEAK;

        /**
         * Gets a {@link Key} enumeration by checking if the given input string matches an enumeration name.
         *
         * @param input An input string that should be that of a {@link Key} name.
         * @return A {@link Key} enumeration or {@link Key#EMPTY}.
         */
        public static Key get(String input) {
            for (Key key : Key.values()) {
                if (input.toLowerCase(Locale.ROOT).equals(key.toString()))
                    return key;
            }

            return Key.EMPTY;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * A simple utility record that helps with keeping track of {@code @decode} objects within a lang key.
     *
     * @param key   A {@link Key} enumeration that represents the key within the {@code @decode} block.
     * @param value The value found within the {@code @decode} block.
     * @param start The starting position of where this {@code @decode} block was found in the lang key.
     * @param end   The ending position of where this {@code @decode} block was found in the lang key.
     */
    private record Found(Key key, String value, int start, int end) {
        public static final Found EMPTY = new Found(Key.EMPTY, "", 0, 0);

        public boolean isEmpty() {
            return this.equals(EMPTY);
        }
    }
}
