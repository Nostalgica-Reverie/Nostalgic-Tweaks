package mod.adrenix.nostalgic.util.client.dialog;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.NostalgicTweaks;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLProperties;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Utility for opening file dialog boxes on the client's operating system.
 */
public abstract class FileDialog {
    private static int makeSDLDialogProperties(MemoryStack stack, Window window, String title, Path folder, List<Filter> filters) {
        int props = SDLProperties.SDL_CreateProperties();
        if (props == 0)
            throw new RuntimeException("Failed to make dialog properties");

        SDLProperties.SDL_SetPointerProperty(props, SDLDialog.SDL_PROP_FILE_DIALOG_WINDOW_POINTER, window.handle());
        SDLProperties.SDL_SetStringProperty(props, SDLDialog.SDL_PROP_FILE_DIALOG_TITLE_STRING, title);

        if (!filters.isEmpty()) {
            SDL_DialogFileFilter.Buffer pFilters = SDL_DialogFileFilter.calloc(filters.size(), stack);
            for (int i = 0; i < filters.size(); i++) {
                Filter filter = filters.get(i);
                //noinspection resource
                pFilters.get(i).name(stack.UTF8(filter.description())).pattern(stack.UTF8(filter.pattern()));
            }

            SDLProperties.SDL_SetPointerProperty(props, SDLDialog.SDL_PROP_FILE_DIALOG_FILTERS_POINTER, pFilters.address());
            SDLProperties.SDL_SetNumberProperty(props, SDLDialog.SDL_PROP_FILE_DIALOG_NFILTERS_NUMBER, filters.size());
        }

        SDLProperties.SDL_SetStringProperty(props, SDLDialog.SDL_PROP_FILE_DIALOG_LOCATION_STRING, folder.toString());

        return props;
    }

    public static void openFileDialog(Window window, String title, Path folder, List<Filter> filters,
                                      boolean multiChoice, ResultCallback<List<String>> callback) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int props = makeSDLDialogProperties(stack, window, NostalgicTweaks.MOD_NAME + " - " + title, folder, filters);
            SDLProperties.SDL_SetBooleanProperty(props, SDLDialog.SDL_PROP_FILE_DIALOG_MANY_BOOLEAN, multiChoice);

            SDLDialog.SDL_ShowFileDialogWithProperties(
                SDLDialog.SDL_FILEDIALOG_OPENFILE,
                (userdata, filelist, filter) ->
                    openDialogFileCallback(userdata, filelist, filter != -1 ? filters.get(filter) : null, callback),
                0L,
                props
            );
        }
    }

    private static void openDialogFileCallback(long userData, long fileList, Filter filter, ResultCallback<List<String>> callback) {
        if (fileList == 0) {
            throw new RuntimeException("Failed to get files: " + SDLError.SDL_GetError());
        }

        ImmutableList.Builder<String> builder = ImmutableList.builder();

        long p = fileList;
        long fp = MemoryUtil.memGetAddress(p);
        while (fp != 0) {
            builder.add(MemoryUtil.memUTF8(fp));

            p += Pointer.POINTER_SIZE;
            fp = MemoryUtil.memGetAddress(p);
        }

        ImmutableList<String> paths = builder.build();
        if (!paths.isEmpty()) {
            callback.onCallback(true, paths);
            return;
        }

        callback.onCallback(false, null);
    }

    public static void saveFileDialog(Window window, String title, Path folder, List<Filter> filters, ResultCallback<String> callback) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int props = makeSDLDialogProperties(stack, window, NostalgicTweaks.MOD_NAME + " - " + title, folder, filters);

            SDLDialog.SDL_ShowFileDialogWithProperties(
                SDLDialog.SDL_FILEDIALOG_SAVEFILE,
                (userdata, filelist, filter) ->
                    saveDialogFileCallback(userdata, filelist, filter != -1 ? filters.get(filter) : null, callback),
                0L,
                props
            );
        }
    }

    private static void saveDialogFileCallback(long userData, long fileList, Filter filter, ResultCallback<String> callback) {
        if (fileList == 0) {
            throw new RuntimeException("Failed to get files: " + SDLError.SDL_GetError());
        }

        long fp = MemoryUtil.memGetAddress(fileList);
        if (fp == 0) {
            callback.onCallback(false, null);
            return;
        }

        String append = filter.suffix().isPresent() ? filter.suffix().get() : "";
        callback.onCallback(true, MemoryUtil.memUTF8(fp) + append);
    }

    public record Filter(String pattern, String description, Optional<String> suffix) {
        public Filter(String pattern, String description) {
            this(pattern, description, Optional.of(pattern));
        }
    }

    @FunctionalInterface
    public interface ResultCallback<R> {
        void onCallback(boolean hasResult, R result);
    }
}
