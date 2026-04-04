package mod.adrenix.nostalgic.util.common.log;

import mod.adrenix.nostalgic.services.NostalgicServices;

/**
 * This enumeration is only used in development environments since the terminal supports ANSI coloring. Production
 * builds will not be using log colors.
 */
public enum LogColor {
    /* Colors */

    BLACK("\u001B[0;30m", "0"),          // Black §0
    DARK_BLUE("\u001B[0;34m", "1"),      // Dark Blue §1
    DARK_GREEN("\u001B[0;32m", "2"),     // Dark Green §2
    DARK_AQUA("\u001B[0;36m", "3"),      // Dark Aqua §3
    DARK_RED("\u001B[0;31m", "4"),       // Dark Red §4
    DARK_PURPLE("\u001B[0;35m", "5"),    // Dark Purple §5
    GOLD("\u001B[0;33m", "6"),           // Gold §6
    GRAY("\u001B[0;37m", "7"),           // Gray §7
    DARK_GRAY("\u001B[0;30;1m", "8"),    // Dark Gray §8
    BLUE("\u001B[0;34;1m", "9"),         // Blue §9
    GREEN("\u001B[0;32;1m", "a"),        // Green §a
    AQUA("\u001B[0;36;1m", "b"),         // Aqua §b
    RED("\u001B[0;31;1m", "c"),          // Red §c
    LIGHT_PURPLE("\u001B[0;35;1m", "d"), // Light Purple §d
    YELLOW("\u001B[0;33;1m", "e"),       // Yellow §e
    WHITE("\u001B[0;37;1m", "f"),        // White §f

    OBFUSCATED("\u001B[5m", "k"),        // Obfuscated §k
    BOLD("\u001B[1m", "l"),              // Bold §l
    STRIKETHROUGH("\u001B[9m", "m"),     // Strikethrough §m
    UNDERLINE("\u001B[4m", "n"),         // Underline §n
    ITALIC("\u001B[3m", "o"),            // Italic §o

    RESET("\u001B[m", "r");              // Reset §r

    /**
     * The ANSI color string for this enumeration.
     */
    private final String ansi;

    /**
     * The Minecraft color code for this enumeration.
     */
    private final String mc;

    /**
     * Color enumeration.
     *
     * @param ansi The ANSI color string.
     * @param mc   The Minecraft color code.
     */
    LogColor(String ansi, String mc) {
        this.ansi = ansi;
        this.mc = mc;
    }

    /**
     * Applies a log color to the given string.
     *
     * @param color The desired color.
     * @param to    The string to apply a color to.
     * @return A modified string with the color applied. An appended <code>RESET</code> code will be included.
     */
    public static String apply(LogColor color, String to) {
        return color + to + LogColor.RESET;
    }

    /**
     * Converts Minecraft color codes to ANSI color strings when the mod is in a development environment.
     *
     * @param in The string to modify.
     * @return A modified string with Minecraft color codes replaced with ANSI strings.
     */
    public String convert(String in) {
        return NostalgicServices.PLATFORM.isDevEnvironment() ? in.replaceAll("§" + this.mc, this.ansi) : in;
    }

    /**
     * An override of the <code>toString</code> method that replaces ANSI strings with empty strings if the mod is
     * loaded in a production environment.
     *
     * @return A modified string based on what environment the mod is loaded in.
     */
    @Override
    public String toString() {
        return NostalgicServices.PLATFORM.isDevEnvironment() ? this.ansi : "";
    }
}
