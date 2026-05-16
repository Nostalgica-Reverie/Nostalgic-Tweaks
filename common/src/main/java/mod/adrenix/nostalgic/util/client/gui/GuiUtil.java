package mod.adrenix.nostalgic.util.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.services.NostalgicServices;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class GuiUtil
{
    /* --- Sound */

    /**
     * Plays a button click sound to the client.
     */
    @PublicAPI
    public static void playClick()
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /* --- Font */

    /**
     * @return Get the game's current font instance.
     */
    @PublicAPI
    public static Font font()
    {
        return Minecraft.getInstance().font;
    }

    /**
     * @return The line height provided by the game's current font.
     */
    @PublicAPI
    public static int textHeight()
    {
        return font().lineHeight;
    }

    /* --- Screen */

    /**
     * A mod list screen supplier (defined in mod loaders).
     */
    public static @Nullable Function<Screen, Screen> modListScreen = null;

    /**
     * @return Get the game's current screen.
     */
    @PublicAPI
    public static Optional<Screen> getScreen()
    {
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
    public static <T extends Screen> Optional<T> getScreenAs(Class<T> classType)
    {
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
    public static Screen getScreenOrNull()
    {
        return getScreen().orElse(null);
    }

    /**
     * @return Get the current game screen's width.
     */
    @PublicAPI
    public static int getScreenWidth()
    {
        return getScreen().stream().mapToInt(screen -> screen.width).max().orElse(0);
    }

    /**
     * @return Get the current game screen's height.
     */
    @PublicAPI
    public static int getScreenHeight()
    {
        return getScreen().stream().mapToInt(screen -> screen.height).max().orElse(0);
    }

    /**
     * Check if the given screen is the screen being shown to the user.
     *
     * @param screen A {@link Screen} instance.
     * @return Whether the given screen is the current game screen.
     */
    @PublicAPI
    public static boolean isCurrentScreen(Screen screen)
    {
        return getScreen().isPresent() && screen == getScreen().get();
    }

    /**
     * Render the old dirt block background for screens.
     *
     * @param graphics The {@link GuiGraphics} instance.
     */
    @PublicAPI
    public static void renderDirtBackground(GuiGraphics graphics)
    {
        int width = getScreenWidth();
        int height = getScreenHeight();

        graphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        graphics.blit(TextureLocation.DIRT_BACKGROUND, 0, 0, 0, 0.0F, 0.0F, width, height, 32, 32);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /* --- Window */

    /**
     * @return The game's window instance.
     */
    @PublicAPI
    public static Window getWindow()
    {
        return Minecraft.getInstance().getWindow();
    }

    /**
     * @return A pointer to the native window object that is passed to GLFW.
     */
    @PublicAPI
    public static long getWindowPointer()
    {
        return getWindow().getWindow();
    }

    /**
     * @return The game's window gui scale.
     */
    @PublicAPI
    public static double getGuiScale()
    {
        return getWindow().getGuiScale();
    }

    /**
     * @return The game's window width that is scaled to the gui.
     */
    @PublicAPI
    public static int getGuiWidth()
    {
        return getWindow().getGuiScaledWidth();
    }

    /**
     * @return The game's window height that is scaled to the gui.
     */
    @PublicAPI
    public static int getGuiHeight()
    {
        return getWindow().getGuiScaledHeight();
    }

    /**
     * @return The x-coordinate of the game mouse.
     */
    @PublicAPI
    public static int getMouseX()
    {
        Minecraft minecraft = Minecraft.getInstance();
        double scaledWidth = minecraft.mouseHandler.xpos() * (double) getGuiWidth();

        return (int) (scaledWidth / (double) minecraft.getWindow().getScreenWidth());
    }

    /**
     * @return The y-coordinate of the game mouse.
     */
    @PublicAPI
    public static int getMouseY()
    {
        Minecraft minecraft = Minecraft.getInstance();
        double scaledHeight = minecraft.mouseHandler.ypos() * (double) getGuiHeight();

        return (int) (scaledHeight / (double) minecraft.getWindow().getScreenHeight());
    }

    /* --- Scissoring */

    /**
     * Alternative to using instanced graphics scissoring. The given dimensions will be scaled relative to the current
     * gui state.
     *
     * @param minX The left side of the scissoring window.
     * @param minY The top side of the scissoring window.
     * @param maxX The right side of the scissoring window.
     * @param maxY The bottom side of the scissoring window.
     */
    @PublicAPI
    public static void enableScissor(int minX, int minY, int maxX, int maxY)
    {
        Window window = Minecraft.getInstance().getWindow();
        int height = window.getHeight();
        double scale = window.getGuiScale();

        double x0 = (double) minX * scale;
        double y0 = (double) height - (double) maxY * scale;
        double x1 = (double) (maxX - minX) * scale;
        double y1 = (double) (maxY - minY) * scale;

        RenderSystem.enableScissor((int) x0, (int) y0, Math.max(0, (int) x1), Math.max(0, (int) y1));
    }

    /**
     * Disable the render system's scissoring.
     */
    @PublicAPI
    public static void disableScissor()
    {
        RenderSystem.disableScissor();
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
    public static boolean isComponentEmpty(Component component)
    {
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
    public static boolean isComponentEmpty(List<Component> components)
    {
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
    public static boolean isComponentPresent(Component component)
    {
        return !isComponentEmpty(component);
    }

    /**
     * Check if the given {@link List} of {@link Component} instances is present. The list will be considered present if
     * it has more than one {@link Component}. Or if there is only one {@link Component}, then that component will be
     * considered present if it passes {@link #isComponentPresent(Component)}.
     *
     * @param components A {@link List} of {@link Component} instances.
     * @return Whether the list of components is considered non-empty.
     */
    @PublicAPI
    public static boolean isComponentPresent(List<Component> components)
    {
        return !isComponentEmpty(components);
    }

    /* --- Debug Framerate */

    /**
     * Framerate viewing flag.
     */
    private static boolean showFps = NostalgicServices.PLATFORM.isDevEnvironment();

    /**
     * @return Whether the framerate viewing window is visible.
     */
    public static boolean isShowingFps()
    {
        return showFps;
    }

    /**
     * Set the {@code showFps} flag for this utility.
     *
     * @param state A {@code boolean} flag.
     */
    public static void setShowFps(boolean state)
    {
        showFps = state;
    }

    /**
     * Toggle whether the framerate viewing window is visible.
     */
    public static void toggleShowFps()
    {
        showFps = !showFps;
    }

    /**
     * Render debug information such as FPS and GPU usage.
     *
     * @param graphics A {@link GuiGraphics} instance.
     */
    public static void renderDebug(GuiGraphics graphics)
    {
        if (!showFps)
            return;

        boolean isDebugging = NostalgicTweaks.isDebugging();
        float width = isDebugging ? 27.0F : 30.0F;

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 240.0F);

        RenderUtil.beginBatching();
        RenderUtil.fill(graphics, 0.0F, 0.0F, width, ((textHeight() / 2.0F) + 1) * 3, 0xA5000000);

        graphics.pose().scale(0.5F, 0.5F, 0.5F);
        graphics.pose().translate(1.0F, 2.0F, 0.0F);

        int nextX = DrawText.begin(graphics, "DEBUG: ").color(0xFFFF00).draw();
        int color = isDebugging ? 0x00FF00 : 0xFF0000;
        String state = isDebugging ? "ON" : "OFF";

        DrawText.begin(graphics, state).posX(nextX).color(color).draw();

        graphics.pose().translate(0.0F, 11.0F, 0.0F);
        nextX = DrawText.begin(graphics, "FPS: ").draw();
        DrawText.begin(graphics, String.format("%s", Minecraft.getInstance().getFps())).posX(nextX).draw();

        graphics.pose().translate(0.0F, 11.0F, 0.0F);
        nextX = DrawText.begin(graphics, "GPU: ").draw();

        String gpu = TextUtil.extract(Minecraft.getInstance().fpsString, "GPU:.+");
        String usage = TextUtil.extract(gpu, "\\d+");

        if (!usage.isEmpty())
            DrawText.begin(graphics, TextUtil.getPercentColorHigh(Integer.parseInt(usage)) + "%").posX(nextX).draw();

        RenderUtil.endBatching();
        graphics.pose().popPose();
    }
}
