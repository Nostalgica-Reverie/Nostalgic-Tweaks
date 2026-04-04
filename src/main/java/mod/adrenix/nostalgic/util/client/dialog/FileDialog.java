package mod.adrenix.nostalgic.util.client.dialog;

import mod.adrenix.nostalgic.NostalgicTweaks;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Utility for opening file dialog boxes on the client's operating system.
 */
public abstract class FileDialog {
    /**
     * Get the filepath location from the client's operating system to open or save a file to.
     *
     * <p>
     * <h3 color=red>Warning</h3>
     * Do not allow for user input to be included in <i>any</i> of the {@link TinyFileDialogs} boxes. This is for
     * security reasons since there are known exploits when allowing for custom input. All strings must be hard-coded
     * and <b>not</b> come from resource language files.
     *
     * @param windowTitle The hard-coded title of the window opened up on the user's operating system.
     * @param pathAndFile The path that points to what a default file would look like.
     * @param dialogType  The {@link DialogType} of the window.
     * @return A filepath location from the user's operating system or {@code null} if the window was canceled.
     */
    @Nullable
    public static String getJsonLocation(String windowTitle, @Nullable Path pathAndFile, DialogType dialogType) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer patterns = stack.mallocPointer(1);
            patterns.put(stack.UTF8("*.json"));
            patterns.flip();

            String desc = "JSON files (*.json)";
            String title = NostalgicTweaks.MOD_NAME + " - " + windowTitle;
            String defaultFile = Optional.ofNullable(pathAndFile).map(Path::toString).orElse(null);

            return switch (dialogType) {
                case OPEN_FILE -> TinyFileDialogs.tinyfd_openFileDialog(title, defaultFile, patterns, desc, false);
                case SAVE_FILE -> TinyFileDialogs.tinyfd_saveFileDialog(title, defaultFile, patterns, desc);
            };
        }
    }
}
