package mod.adrenix.nostalgic.util.common.text;

import com.google.common.collect.Lists;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.data.Holder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class TextUtil {
    /**
     * Extract strings from another string with the given regex. If nothing was found, then an empty string will be the
     * only entry in the returned array list.
     *
     * @param from  The string to extract data from.
     * @param regex The regex pattern to use.
     * @return An array list of matched strings or a list with a single empty string entry if nothing was found.
     */
    @PublicAPI
    public static ArrayList<String> extractAll(String from, String regex) {
        ArrayList<String> results = new ArrayList<>();

        Pattern.compile(regex).matcher(from).results().map(match -> match.group(0)).forEach(results::add);

        if (results.isEmpty())
            results.add("");

        return results;
    }

    /**
     * Shortcut for the {@link TextUtil#extractAll(String, String)} method where only the first entry in the array list
     * will be returned. If nothing was found, then an empty string is returned.
     *
     * @param from  The string to extract data from.
     * @param regex The regex pattern to use.
     * @return The first string matched by the regex, otherwise an empty string.
     */
    @PublicAPI
    public static String extract(String from, String regex) {
        return extractAll(from, regex).getFirst();
    }

    /**
     * Extract a regex match using the given arguments.
     *
     * @param from       The string to extract data from.
     * @param regex      The regex pattern to use.
     * @param groupIndex The group index (0 represents the entire pattern) to use. The index will be bound checked.
     * @return The string found using the given arguments, otherwise an empty string.
     */
    @PublicAPI
    public static String extract(String from, String regex, int groupIndex) {
        return Pattern.compile(regex)
                .matcher(from)
                .results()
                .map(match -> match.group(Mth.clamp(groupIndex, 0, match.groupCount())))
                .findFirst()
                .orElse("");
    }

    /**
     * Limit the size of a string to the given amount. The string is trimmed if it exceeds the given amount, otherwise
     * it is returned. Unicode and emojis are properly handled.
     *
     * @param input  The string to limit.
     * @param amount The amount of content allowed in the string.
     * @return A string limited to the given amount.
     */
    @PublicAPI
    public static String limit(String input, int amount) {
        return input.codePoints()
                .limit(amount)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Replace all content in the input within the given range.
     *
     * @param input       The input to replace.
     * @param regex       The regular expression to find matches with.
     * @param replacement The replacement for any found matches.
     * @param start       The starting position of where to perform replacements at.
     * @param end         The ending position of where to perform replacements at.
     * @return A modified string based on the given arguments.
     */
    @PublicAPI
    public static String replaceInRange(String input, String regex, String replacement, int start, int end) {
        String snip = input.substring(start, end);
        String result = snip.replaceAll(regex, replacement);

        return input.substring(0, start) + result + input.substring(end);
    }

    /**
     * Apply a Minecraft color section code to the given percent based on the given percent's value. The higher the
     * percentage value is, the more <font color="red">dangerous</font> the color will appear. A reset section code will
     * be applied to the end of the returned string.
     *
     * @param percent The percent value to check (<font color="green">0</font>-<font color="red">100</font>).
     * @return The given percent value with a Minecraft color § code attached and a reset section code appended.
     */
    @PublicAPI
    public static String getPercentColorHigh(int percent) {
        if (percent < 20)
            return "§a" + percent + "§r";
        else if (percent < 40)
            return "§2" + percent + "§r";
        else if (percent < 60)
            return "§e" + percent + "§r";
        else if (percent < 80)
            return "§6" + percent + "§r";
        else if (percent < 100)
            return "§c" + percent + "§r";

        return "§4" + percent + "§r";
    }

    /**
     * Apply a Minecraft color section code to the given percent based on the given percent's value. The lower the
     * percentage value is, the more <font color="red">dangerous</font> the color will appear. A reset section code will
     * be applied to the end of the returned string.
     *
     * @param percent The percent value to check (<font color="red">0</font>-<font color="green">100</font>).
     * @return The given percent value with a Minecraft color § code attached and a reset section code appended.
     */
    @PublicAPI
    public static String getPercentColorLow(int percent) {
        if (percent <= 0)
            return "§4" + percent + "§r";
        else if (percent < 20)
            return "§c" + percent + "§r";
        else if (percent < 40)
            return "§6" + percent + "§r";
        else if (percent < 60)
            return "§e" + percent + "§r";
        else if (percent < 80)
            return "§2" + percent + "§r";

        return "§a" + percent + "§r";
    }

    /**
     * Converts a string into a title case format. Handles space and underscore delimiters.
     *
     * @param convert The string to convert.
     * @return A string that is in title case format.
     */
    @PublicAPI
    public static String toTitleCase(String convert) {
        String delimiters = " _";
        StringBuilder builder = new StringBuilder(convert.toLowerCase());
        boolean next = true;

        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);

            c = next ? Character.toUpperCase(c) : Character.toLowerCase(c);
            builder.deleteCharAt(i);
            builder.replace(i, i, String.valueOf(c));
            next = delimiters.indexOf(c) >= 0;

            if (next) {
                builder.deleteCharAt(i);
                builder.replace(i, i, " ");
            }
        }

        return builder.toString();
    }

    /**
     * Makes the first letter in a string uppercase.
     *
     * @param convert The string to convert.
     * @return A string where the first letter is uppercase.
     */
    @PublicAPI
    public static String uppercaseFirstLetter(String convert) {
        if (convert.isEmpty())
            return convert;

        char[] chars = convert.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        return new String(chars);
    }

    /**
     * Makes the first letter in a string lowercase.
     *
     * @param convert The string to convert.
     * @return A string where the first letter is lowercase.
     */
    @PublicAPI
    public static String lowercaseFirstLetter(String convert) {
        if (convert.isEmpty())
            return convert;

        char[] chars = convert.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);

        return new String(chars);
    }

    /**
     * Removes the last three characters of a string and replaces them with ellipsis points (...) if the given text
     * exceeds the given maximum width.
     *
     * @param fontWidth A function that accepts a string and returns its width. Pass in {@code Font::getWidth} which can
     *                  be obtained from {@code GuiUtil} or {@code Minecraft#getInstance()}.
     * @param in        The string to modify.
     * @param maxWidth  The maximum width allowed.
     * @return The input string with ellipsis points if the given input exceeded the max width.
     */
    @PublicAPI
    public static String ellipsis(Function<String, Integer> fontWidth, String in, int maxWidth) {
        if (fontWidth.apply(in) <= maxWidth)
            return in;

        StringBuilder builder = new StringBuilder(in);

        for (int i = in.length() - 1; i >= 0; i--) {
            if (fontWidth.apply(builder + "...") <= maxWidth)
                break;

            builder.deleteCharAt(i);
        }

        return builder + "...";
    }

    /**
     * Overload method for {@link TextUtil#ellipsis(Function, String, int)} but accepts and returns a chat component.
     *
     * @param fontWidth A function that accepts a string and returns its width. Pass in {@code Font::getWidth} which can
     *                  be obtained from {@code GuiUtil} or {@code Minecraft#getInstance()}.
     * @param in        The component to modify.
     * @param maxWidth  The max width allowed.
     * @return The input component with ellipsis points if the given component exceeded the max width.
     */
    @PublicAPI
    public static Component ellipsis(Function<String, Integer> fontWidth, Component in, int maxWidth) {
        return Component.literal(ellipsis(fontWidth, in.getString(), maxWidth));
    }

    /**
     * Combine multiple Minecraft {@link Component Components} into one single component.
     *
     * @param lines An array of {@link Component Components}.
     * @return One single {@link Component}
     */
    @PublicAPI
    public static Component combine(Component[] lines) {
        StringBuilder builder = new StringBuilder();

        for (Component component : lines)
            builder.append(component.getString()).append("\n\n");

        return Component.literal(builder.toString());
    }

    /**
     * Color a json-like string.
     *
     * @param json The json string to format.
     * @return A colored string derived from the given json string.
     */
    @PublicAPI
    public static String colorJson(String json) {
        record Result(String replace, String color, int start, int end) {
            public void apply(Holder<String> formatting) {
                String replaceWith = String.format("%s%s%s", this.color, this.replace, ChatFormatting.RESET);
                StringBuffer buffer = new StringBuffer(formatting.get()).replace(this.start, this.end, replaceWith);

                formatting.set(buffer.toString());
            }
        }

        ArrayList<Result> results = new ArrayList<>();
        Holder<String> formatting = Holder.create(json);

        BiConsumer<String, String> paint = (regex, color) -> Pattern.compile(regex)
                .matcher(formatting.get())
                .results()
                .forEach(match -> {
                    String replace = match.group(match.groupCount());
                    int start = match.start(match.groupCount());
                    int end = match.end(match.groupCount());

                    results.add(new Result(replace, color, start, end));
                });

        paint.accept("(\"([^\"]*)\")\\s*:", ChatFormatting.GRAY.toString());
        paint.accept(":\\s*(-?\\d*\\.?\\d+)", ChatFormatting.AQUA.toString());
        paint.accept(":\\s*(\\\"([^\\\"]*)\\\")", ChatFormatting.YELLOW.toString());
        paint.accept(":\\s*(true)", ChatFormatting.GREEN.toString());
        paint.accept(":\\s*(false)", ChatFormatting.RED.toString());

        results.sort(Comparator.comparingInt(Result::start));
        Lists.reverse(results).forEach(result -> result.apply(formatting));

        return formatting.get();
    }
}
