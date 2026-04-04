package mod.adrenix.nostalgic.client.gui.widget.input;

class HexInput {
    /**
     * Updates input so that it is always a valid hexadecimal.
     *
     * @param input  Input from the widget.
     * @param opaque Whether the color is opaque.
     * @return A string that starts with a pound sign and contains a valid hexadecimal.
     */
    public static String update(String input, boolean opaque) {
        input = input.replace("#", "").trim().toUpperCase();
        input = input.replaceAll("[^a-fA-F\\d]", "F");

        if (opaque && input.length() > 6)
            input = input.substring(0, 6);
        else if (input.length() > 8)
            input = input.substring(0, 8);

        StringBuilder buffer = new StringBuilder(input);

        if (opaque) {
            while (buffer.length() < 6)
                buffer.append("F");

            input = buffer.toString();
            input = input.substring(0, 6);
        } else {
            while (buffer.length() < 8)
                buffer.append("F");

            input = buffer.toString();
            input = input.substring(0, 8);
        }

        return "#" + input;
    }
}
