package mod.adrenix.nostalgic.util.common.text;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextWrap {
    /**
     * Text wraps a Minecraft {@link Component} into an array of multiple {@link Component Components} with the given
     * line length.
     *
     * @param translation A {@link Component}.
     * @param lineLength  How long each line should be.
     * @return An array of {@link Component Components}.
     */
    @PublicAPI
    public static ArrayList<Component> tooltip(Component translation, int lineLength) {
        String translated = translation.getString();
        ArrayList<String> lines = wrap(translated, lineLength);

        ArrayList<Component> components = new ArrayList<>();
        lines.forEach((line) -> components.add(Component.literal(line)));

        return components;
    }

    /**
     * Wrap a string based on the given line length.
     *
     * @param string     The string to wrap.
     * @param lineLength The length of each line.
     * @return An array list of wrapped strings.
     */
    @PublicAPI
    public static ArrayList<String> wrap(String string, int lineLength) {
        ArrayList<String> processed = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();
        String last = "";

        for (String line : string.split(Pattern.quote("\n")))
            processed.add(inspect(line, lineLength));

        for (String row : processed) {
            for (String line : row.split(Pattern.quote("\n"))) {
                lines.add(getCodes(last) + line.trim());
                last = lines.getLast();
            }
        }

        return lines;
    }

    /**
     * Find Minecraft section color code identifiers within a string.
     *
     * @param row The string to check.
     * @return A string with processed color codes, otherwise an empty string.
     */
    private static String getCodes(String row) {
        Pattern pattern = Pattern.compile(".*(§.)");
        Matcher matcher = pattern.matcher(row);

        if (matcher.find())
            return matcher.group(1);

        return "";
    }

    /**
     * Examine a string to see if it exceeds the given line length.
     *
     * @param line       A string to check.
     * @param lineLength The maximum character length of each line.
     * @return A processed line string.
     */
    private static String inspect(String line, int lineLength) {
        if (line.isEmpty())
            return " \n";

        if (line.length() <= lineLength)
            return line + "\n";

        String[] words = line.split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        StringBuilder trimBuilder = new StringBuilder();
        StringBuilder stripBuilder = new StringBuilder();

        for (String word : words) {
            String stripped = word.replaceAll("§.", "");

            if (stripBuilder.length() + 1 + stripped.length() <= lineLength) {
                trimBuilder.append(word).append(" ");
                stripBuilder.append(stripped).append(" ");
            } else {
                lineBuilder.append(trimBuilder).append("\n");
                trimBuilder = new StringBuilder();
                trimBuilder.append(word).append(" ");

                stripBuilder = new StringBuilder();
                stripBuilder.append(stripped).append(" ");
            }
        }

        if (!stripBuilder.isEmpty())
            lineBuilder.append(trimBuilder);

        return lineBuilder.toString();
    }
}
