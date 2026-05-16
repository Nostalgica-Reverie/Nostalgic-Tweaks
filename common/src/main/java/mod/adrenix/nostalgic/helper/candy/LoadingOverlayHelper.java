package mod.adrenix.nostalgic.helper.candy;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.services.NostalgicServices;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.Overlay;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

/**
 * This utility class is used only by the client.
 */
public abstract class LoadingOverlayHelper
{
    /**
     * Render a modded game loading overlay.
     *
     * @param graphics       The {@link GuiGraphics} instance.
     * @param modernLocation The modern overlay logo {@link ResourceLocation} instance.
     */
    public static void render(GuiGraphics graphics, ResourceLocation modernLocation)
    {
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        boolean isDarkMode = Minecraft.getInstance().options.darkMojangStudiosBackground().get();

        ResourceLocation background = switch (CandyTweak.OLD_LOADING_OVERLAY.get())
        {
            case ALPHA -> TextureLocation.MOJANG_ALPHA;
            case BETA -> TextureLocation.MOJANG_BETA;
            case RELEASE_ORANGE -> TextureLocation.MOJANG_RELEASE_ORANGE;
            case RELEASE_BLACK -> TextureLocation.MOJANG_RELEASE_BLACK;
            default -> modernLocation;
        };

        int x = (int) ((width / 4.0D) - (128 / 2.0D));
        int y = (int) ((height / 4.0D) - (128 / 2.0D));
        int color = isDarkMode ? Color.BLACK.get() : Color.WHITE.get();

        if (CandyTweak.CUSTOM_LOADING_OVERLAY_BACKGROUND.get())
            color = HexUtil.parseInt(CandyTweak.LOADING_OVERLAY_BACKGROUND_COLOR.get());
        else if (CandyTweak.OLD_LOADING_OVERLAY.get() == Overlay.ALPHA)
            color = Color.MOJANG_PURPLE.get();

        if (NostalgicServices.PLATFORM.isDevEnvironment())
        {
            background = TextureLocation.DEV_MODE;
            color = Color.BLACK.get();
        }

        graphics.fill(RenderType.guiOverlay(), 0, 0, width, height, color);
        graphics.pose().pushPose();
        graphics.pose().scale(2.0F, 2.0F, 2.0F);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        graphics.blit(background, x, y, 0, 0, 128, 128, 128, 128);
        graphics.pose().popPose();
    }

    /**
     * Get a progress bar offset based on the overlay logo being used.
     *
     * @return A progress bar offset based on the game window height.
     */
    public static int getProgressBarOffset()
    {
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (NostalgicServices.PLATFORM.isDevEnvironment())
            return (int) (height * 0.85D);

        return switch (CandyTweak.OLD_LOADING_OVERLAY.get())
        {
            case ALPHA -> (int) (height * 0.85D);
            case BETA -> (int) (height * 0.95D);
            default -> (int) (height * 0.69D);
        };
    }

    /**
     * @return The outline rectangle color of the progress bar.
     */
    public static int getOutlineProgressBarColor()
    {
        if (NostalgicServices.PLATFORM.isDevEnvironment())
            return FastColor.ARGB32.color(255, 255, 255, 255);

        if (CandyTweak.CUSTOM_LOADING_PROGRESS_BAR.get())
            return HexUtil.parseInt(CandyTweak.PROGRESS_BAR_OUTLINE_COLOR.get());

        Overlay overlay = CandyTweak.OLD_LOADING_OVERLAY.get();

        if (ModTracker.OPTIFINE.isInstalled())
        {
            return switch (overlay)
            {
                case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 221, 79, 59);
                case RELEASE_BLACK -> FastColor.ARGB32.color(255, 221, 31, 42);
                case ALPHA, MODERN -> FastColor.ARGB32.color(255, 255, 255, 255);
            };
        }

        return switch (overlay)
        {
            case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 221, 79, 59);
            case RELEASE_BLACK -> FastColor.ARGB32.color(255, 4, 7, 7);
            case ALPHA -> FastColor.ARGB32.color(255, 142, 132, 255);
            case MODERN -> FastColor.ARGB32.color(255, 255, 255, 255);
        };
    }

    /**
     * @return The inside rectangle color of the progress bar.
     */
    public static int getInsideProgressBarColor()
    {
        if (NostalgicServices.PLATFORM.isDevEnvironment())
            return FastColor.ARGB32.color(255, 0, 255, 0);

        if (CandyTweak.CUSTOM_LOADING_PROGRESS_BAR.get())
            return HexUtil.parseInt(CandyTweak.PROGRESS_BAR_INSIDE_COLOR.get());

        return switch (CandyTweak.OLD_LOADING_OVERLAY.get())
        {
            case ALPHA, MODERN -> FastColor.ARGB32.color(255, 255, 255, 255);
            case BETA, RELEASE_ORANGE -> FastColor.ARGB32.color(255, 246, 136, 62);
            case RELEASE_BLACK -> FastColor.ARGB32.color(255, 221, 31, 42);
        };
    }
}
