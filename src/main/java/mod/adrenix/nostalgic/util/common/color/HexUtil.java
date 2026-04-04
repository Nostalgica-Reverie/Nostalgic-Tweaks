package mod.adrenix.nostalgic.util.common.color;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HexUtil {
    /**
     * Checks if the given hexadecimal string is a valid (0-9, A-F) entry. This does not remove any prefixed "#" tags.
     *
     * @param hex The hexadecimal to check.
     * @return Whether the input is a valid hexadecimal.
     */
    private static boolean isHexadecimal(String hex) {
        try {
            Integer.parseInt(hex, 16);
        } catch (NumberFormatException ignored) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the given string is a valid hexadecimal. This checker will remove any # found in the string. The
     * hexadecimal can be of length 6 or 8, if any other length is found, then result will be {@code false}.
     *
     * @param check The hexadecimal string to check.
     * @return Whether the given string is a valid hexadecimal.
     */
    @PublicAPI
    public static boolean isValid(String check) {
        check = check.replaceAll("#", "");

        if (check.length() != 6 && check.length() != 8)
            return false;

        String[] split = splitInTwo(check);

        for (String hex : split) {
            if (!isHexadecimal(hex))
                return false;
        }

        return true;
    }

    /**
     * Converts a hexadecimal string (e.g., #8B8B8BFF {@code #RRGGBBAA}) into an array of integers (e.g., [139, 139,
     * 139, 255]). Any invalid input strings will have a default array of [255, 255, 255, 255].
     *
     * @param convert The hexadecimal string to convert.
     * @return An array of base 16 integers.
     */
    @PublicAPI
    public static int[] parseRGBA(String convert) {
        int[] rgba = {0xFF, 0xFF, 0xFF, 0xFF};

        convert = convert.replaceAll("#", "");
        convert = convert.replaceAll("0x", "");

        if (convert.length() != 6 && convert.length() != 8)
            return rgba;

        String[] hex = splitInTwo(convert);

        for (int i = 0; i < hex.length; i++) {
            if (isHexadecimal(hex[i]))
                rgba[i] = Integer.parseInt(hex[i], 16);
        }

        return rgba;
    }

    /**
     * Converts a hexadecimal string (e.g., #8B8B8BFF {@code #RRGGBBAA}) into an array of floats (e.g., [1.83F, 1.83F,
     * 1.83F, 1.0F]). Any invalid input strings will have a default array of [1.0F, 1.0F, 1.0F, 1.0F].
     *
     * @param convert The hexadecimal string to convert.
     * @return An array of four floats from 0.0F to 1.0F.
     */
    @PublicAPI
    public static float[] parseFloatRGBA(String convert) {
        int[] rgba = parseRGBA(convert);
        return new float[]{rgba[0] / 255.0F, rgba[1] / 255.0F, rgba[2] / 255.0F, rgba[3] / 255.0F};
    }

    /**
     * Converts an array of four normalized floats (RGBA) into an ARGB integer.
     *
     * @param rgba An array of four normalized floats (RGBA). All floats are clamped.
     * @return An ARGB integer.
     */
    @PublicAPI
    public static int parseInt(float[] rgba) {
        int r = (int) (Mth.clamp(rgba[0], 0.0F, 1.0F) * 255.0F);
        int g = (int) (Mth.clamp(rgba[1], 0.0F, 1.0F) * 255.0F);
        int b = (int) (Mth.clamp(rgba[2], 0.0F, 1.0F) * 255.0F);
        int a = (int) (Mth.clamp(rgba[3], 0.0F, 1.0F) * 255.0F);

        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Converts a hexadecimal string (e.g., #8B8B8BFF {@code #RRGGBBAA}) into an ARGB integer (e.g., -7631989). Any
     * invalid input strings will have a malformed color.
     *
     * @param convert The hexadecimal string to convert.
     * @return An ARGB integer.
     */
    @PublicAPI
    public static int parseInt(String convert) {
        int[] hex = parseRGBA(convert);
        int r = hex[0];
        int g = hex[1];
        int b = hex[2];
        int a = hex[3];

        return a << 24 | r << 16 | g << 8 | b;
    }

    /**
     * Converts an array of RGBA integers [0-255] to a hex string. The # is prefixed before creating the string. Each
     * integer in the RGBA array is range checked before conversion.
     *
     * @param rgba The array of RGBA integers to convert.
     * @return A hex string (e.g., #8B8B8BFF {@code #RRGGBBAA})
     */
    @PublicAPI
    public static String parseString(int[] rgba) {
        StringBuilder hex = new StringBuilder("#");

        for (int color : rgba) {
            if (color >= 0 && color <= 255)
                hex.append(color <= 15 ? "0" : "").append(Integer.toHexString(color).toUpperCase());
        }

        return hex.toString();
    }

    private static String[] splitInTwo(String convert) {
        String[] split = {"FF", "FF", "FF", "FF"};
        String regex = "(..)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(convert);

        int i = -1;

        while (matcher.find())
            split[++i] = matcher.group(0);

        return split;
    }

    /**
     * Check if an ARGB integer is opaque.
     *
     * @param color An ARGB integer.
     * @return Whether the given integer is an opaque color.
     */
    @PublicAPI
    public static boolean isOpaque(int color) {
        return (color >> 24 & 0xFF) == 255 || (color >> 24 & 0xFF) == 0;
    }

    /**
     * Check if a color has alpha transparency.
     *
     * @param color An ARGB or RGB integer.
     * @return Whether the given integer has alpha transparency.
     */
    @PublicAPI
    public static boolean hasAlpha(int color) {
        return !isOpaque(color);
    }

    /**
     * Get the alpha component (0-255) from the ARGB integer.
     *
     * @param argb An alpha, red, green, and blue integer.
     * @return The alpha component as an integer [0-255].
     */
    @PublicAPI
    public static int getAlpha(int argb) {
        return argb >> 24 & 0xFF;
    }
}
