package mod.adrenix.nostalgic.util.client.gui;

import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.services.NostalgicServices;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

//import dev.architectury.platform.Platform;

public abstract class GuiUtil {
    /* --- Sound */

    /**
     * A mod list screen supplier (defined in mod loaders).
     */
    public static @Nullable Function<Screen, Screen> modListScreen = null;

    /* --- Font */
    /**
     * Framerate viewing flag.
     */
    private static boolean showFps = NostalgicServices.PLATFORM.isDevEnvironment();

    /**
     * Plays a button click sound to the client.
     */
    @PublicAPI
    public static void playClick() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /* --- Screen */

    /**
     * @return Get the game's current font instance.
     */
    @PublicAPI
    public static Font font() {
        return Minecraft.getInstance().font;
    }

    /**
     * @return The line height provided by the game's current font.
     */
    @PublicAPI
    public static int textHeight() {
        return font().lineHeight;
    }

    /**
     * @return Get the game's current screen.
     */
    @PublicAPI
    public static Optional<Screen> getScreen() {
        Screen screen = Minecraft.getInstance().screen;

        if (screen == null)
            return Optional.empty();

        return Optional.of(screen);
    }

    /**
     * Get the game's current screen as a cast with the given class type.
     *
     * @param classType The class type to define the returned screen as.
     * @param <T>       The class.
     * @return An optional that will contain the screen with the given cast if casting was successful.
     */
    @PublicAPI
    public static <T extends Screen> Optional<T> getScreenAs(Class<T> classType) {
        if (getScreen().isEmpty())
            return Optional.empty();

        return ClassUtil.cast(getScreen().get(), classType);
    }

    /**
     * Get the game's current screen.
     *
     * @return The current screen, or {@code null} if the game does not have one.
     */
    @PublicAPI
    @Nullable
    public static Screen getScreenOrNull() {
        return getScreen().orElse(null);
    }

    /**
     * @return Get the current game screen's width.
     */
    @PublicAPI
    public static int getScreenWidth() {
        return getScreen().stream().mapToInt(screen -> screen.width).max().orElse(0);
    }

    /**
     * @return Get the current game screen's height.
     */
    @PublicAPI
    public static int getScreenHeight() {
        return getScreen().stream().mapToInt(screen -> screen.height).max().orElse(0);
    }

    /**
     * Check if the given screen is the screen being shown to the user.
     *
     * @param screen A {@link Screen} instance.
     * @return Whether the given screen is the current game screen.
     */
    @PublicAPI
    public static boolean isCurrentScreen(Screen screen) {
        return getScreen().isPresent() && screen == getScreen().get();
    }

    /* --- Window */

    /**
     * Render the old dirt block background for screens.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     */
    @PublicAPI
    public static void extractDirtBackground(GuiGraphicsExtractor graphics) {
        int width = getScreenWidth();
        int height = getScreenHeight();

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TextureLocation.DIRT_BACKGROUND,
                0, 0,
                0.0F, 0.0F,
                width, height,
                32, 32,
                0xFF404040
        );
    }

    /**
     * @return The game's window instance.
     */
    @PublicAPI
    public static Window getWindow() {
        return Minecraft.getInstance().getWindow();
    }

    /**
     * @return A pointer to the native window object that is passed to GLFW.
     */
    @PublicAPI
    public static long getWindowPointer() {
        return getWindow().handle();
    }

    /**
     * @return The game's window gui scale.
     */
    @PublicAPI
    public static double getGuiScale() {
        return getWindow().getGuiScale();
    }

    /**
     * @return The game's window width that is scaled to the gui.
     */
    @PublicAPI
    public static int getGuiWidth() {
        return getWindow().getGuiScaledWidth();
    }

    /**
     * @return The game's window height that is scaled to the gui.
     */
    @PublicAPI
    public static int getGuiHeight() {
        return getWindow().getGuiScaledHeight();
    }

    /**
     * @return The x-coordinate of the game mouse.
     */
    @PublicAPI
    public static int getMouseX() {
        Minecraft minecraft = Minecraft.getInstance();
        double scaledWidth = minecraft.mouseHandler.xpos() * (double) getGuiWidth();

        return (int) (scaledWidth / (double) minecraft.getWindow().getScreenWidth());
    }

    /**
     * @return The y-coordinate of the game mouse.
     */
    @PublicAPI
    public static int getMouseY() {
        Minecraft minecraft = Minecraft.getInstance();
        double scaledHeight = minecraft.mouseHandler.ypos() * (double) getGuiHeight();

        return (int) (scaledHeight / (double) minecraft.getWindow().getScreenHeight());
    }

    /* --- Components */

    /**
     * Check if the given {@link Component} is considered empty. A {@link Component} is empty if it equals
     * {@link Component#empty()} or the string content is {@code empty} or {@code blank}.
     *
     * @param component A {@link Component} to check.
     * @return Whether the given component is considered empty.
     */
    @PublicAPI
    public static boolean isComponentEmpty(Component component) {
        String contents = component.getString();
        return component.equals(Component.empty()) || contents.isEmpty() || contents.isBlank();
    }

    /**
     * Check if the given {@link List} of {@link Component} instances is empty. The list will be considered empty if it
     * has only one {@link Component} and that component is empty using {@link #isComponentEmpty(Component)}.
     *
     * @param components A {@link List} of {@link Component} instances.
     * @return Whether the list of components is empty.
     */
    @PublicAPI
    public static boolean isComponentEmpty(List<Component> components) {
        if (components.isEmpty())
            return true;

        if (components.size() > 1)
            return false;

        return isComponentEmpty(components.getFirst());
    }

    /**
     * Check if the given {@link Component} is considered present. A {@link Component} is present if it not equal to
     * {@link Component#empty()} or the string content is not {@code empty} or {@code blank}.
     *
     * @param component A {@link Component} to check.
     * @return Whether the given component is considered present.
     */
    @PublicAPI
    public static boolean isComponentPresent(Component component) {
        return !isComponentEmpty(component);
    }

    /* --- Debug Framerate */

    /**
     * Check if the given {@link List} of {@link Component} instances is present. The list will be considered present if
     * it has more than one {@link Component}. Or if there is only one {@link Component}, then that component will be
     * considered present if it passes {@link #isComponentPresent(Component)}.
     *
     * @param components A {@link List} of {@link Component} instances.
     * @return Whether the list of components is considered non-empty.
     */
    @PublicAPI
    public static boolean isComponentPresent(List<Component> components) {
        return !isComponentEmpty(components);
    }

    /**
     * @return Whether the framerate viewing window is visible.
     */
    public static boolean isShowingFps() {
        return showFps;
    }

    /**
     * Set the {@code showFps} flag for this utility.
     *
     * @param state A {@code boolean} flag.
     */
    public static void setShowFps(boolean state) {
        showFps = state;
    }

    /**
     * Toggle whether the framerate viewing window is visible.
     */
    public static void toggleShowFps() {
        showFps = !showFps;
    }

    /**
     * Render debug information such as FPS and GPU usage.
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    public static void renderDebug(GuiGraphicsExtractor graphics) {
        if (!showFps)
            return;

        boolean isDebugging = NostalgicTweaks.isDebugging();
        int width = isDebugging ? 27 : 30;

        graphics.fill(0, 0, width, ((textHeight() / 2) + 1) * 3, 0xA5000000);

        graphics.pose().pushMatrix();
        graphics.pose().scale(0.5F, 0.5F);
        graphics.pose().translate(1.0F, 2.0F);

        int nextX = DrawText.begin(graphics, "DEBUG: ").color(0xFFFF00).draw();
        int color = isDebugging ? 0x00FF00 : 0xFF0000;
        String state = isDebugging ? "ON" : "OFF";

        DrawText.begin(graphics, state).posX(nextX).color(color).draw();

        graphics.pose().translate(0.0F, 11.0F);
        nextX = DrawText.begin(graphics, "FPS: ").draw();
        DrawText.begin(graphics, String.format("%s", Minecraft.getInstance().getFps())).posX(nextX).draw();

        graphics.pose().translate(0.0F, 11.0F);
        nextX = DrawText.begin(graphics, "GPU: ").draw();

        int usage = (int) Math.round(Minecraft.getInstance().getGpuUtilization());
        DrawText.begin(graphics, TextUtil.getPercentColorHigh(usage) + "%").posX(nextX).draw();

        graphics.pose().popMatrix();
    }
}
