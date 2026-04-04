package mod.adrenix.nostalgic.client.gui.overlay.types.info;

/**
 * These enumerations define the visuals of a message overlay.
 *
 * <ul>
 *     <li>{@code SUCCESS} - Displays a message box using a green check and semitransparent green background.</li>
 *     <li>{@code SEARCH} - Displays a message box using a search icon and semitransparent light-blue background.</li>
 *     <li>{@code ERROR} - Displays a message box using a red-x icon and semitransparent dark-red background.</li>
 *     <li>{@code WARNING} - Displays a message box using a warning icon and semitransparent yellow background.</li>
 *     <li>{@code RED_WARNING} - Displays a message box using a red warning icon and semitransparent dark-red background.</li>
 * </ul>
 */
public enum MessageType {
    SUCCESS,
    SEARCH,
    ERROR,
    HELP,
    WARNING,
    RED_WARNING
}
