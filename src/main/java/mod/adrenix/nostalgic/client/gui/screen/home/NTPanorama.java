package mod.adrenix.nostalgic.client.gui.screen.home;

import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.common.array.CycleIndex;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

//import net.minecraft.client.renderer.PanoramaRenderer;

public enum NTPanorama implements PreparableReloadListener {
    ALPHA,
    BETA,
    CAVE,
    NETHER,
    CLASSIC,
    SLIDE;

    /* Static */

    private static final Animation FADE_IN_ANIMATION = Animate.linear(4L, TimeUnit.SECONDS);
    private static final SimpleTimer SWITCH_TIMER = SimpleTimer.create(15L, TimeUnit.SECONDS).immediate().build();
    private static final FlagHolder PAUSE_TIMER = FlagHolder.off();
    private static final CycleIndex CYCLE_INDEX = new CycleIndex(NTPanorama.values(), true);
//    private final PanoramaRenderer panorama;
//    private final CubeMap cubeMap;

    NTPanorama() {
        String branch = this.toString().toLowerCase(Locale.ROOT);
//
//        this.cubeMap = new CubeMap(LocateResource.mod("textures/panorama/" + branch + "/panorama"));
//        this.panorama = new PanoramaRenderer(this.cubeMap);
    }

    /**
     * @return The {@link NTPanorama} to display.
     */
    private static NTPanorama getDisplaying() {
        return NTPanorama.values()[CYCLE_INDEX.get()];
    }

    /**
     * @return The {@link NTPanorama} that was last displayed.
     */
    private static NTPanorama getLastDisplaying() {
        return NTPanorama.values()[CYCLE_INDEX.getLast()];
    }

    /**
     * Render the cycled panorama.
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    public static void extractRenderState(GuiGraphicsExtractor graphics) {
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//
//        if (FADE_IN_ANIMATION.isNotFinished())
//            NTPanorama.getLastDisplaying().draw(graphics, PartialTick.realtime());
//
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) FADE_IN_ANIMATION.getValue());
//        NTPanorama.getDisplaying().draw(graphics, PartialTick.realtime());
//
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Ticking instructions that handles the index cycle and fade animation.
     */
    public static void onTick() {
        if (SWITCH_TIMER.hasElapsed() && !PAUSE_TIMER.get()) {
            CYCLE_INDEX.cycle();

            FADE_IN_ANIMATION.reset();
            FADE_IN_ANIMATION.play();
        }
    }

    /**
     * Move the panorama cycle forward.
     */
    public static void forward() {
        CYCLE_INDEX.forward();
        SWITCH_TIMER.reset();

        FADE_IN_ANIMATION.reset();
        FADE_IN_ANIMATION.play();
    }

    /**
     * Move the panorama cycle backward.
     */
    public static void backward() {
        CYCLE_INDEX.backward();
        SWITCH_TIMER.reset();

        FADE_IN_ANIMATION.reset();
        FADE_IN_ANIMATION.play();
    }

    /* Fields */

    /**
     * Pause the panorama cycle.
     */
    public static void pause() {
        PAUSE_TIMER.enable();
    }

    /**
     * Unpause the panorama cycle.
     */
    public static void unpause() {
        PAUSE_TIMER.disable();
    }

    /* Constructor */

    /**
     * @return Whether the panorama cycle is paused.
     */
    public static boolean isPaused() {
        return PAUSE_TIMER.get();
    }

    /* Methods */

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.SharedState currentReload, Executor taskExecutor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor reloadExecutor) {
//        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
//
//        return CompletableFuture.runAsync(() -> this.cubeMap.preload(textureManager, backgroundExecutor), backgroundExecutor)
//                .thenCompose(barrier::wait);
        return null;
    }

    /**
     * Render this panorama.
     *
     * @param graphics    The current pose stack.
     * @param partialTick The normalized progress made between two ticks (0.0F-1.0F).
     */
    private void draw(GuiGraphicsExtractor graphics, float partialTick) {
//        this.panorama.render(graphics, 0, 0, 1.0F, partialTick);
//
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.enableBlend();
//
//        graphics.blit(TextureLocation.PANORAMA_OVERLAY, 0, 0, GuiUtil.getScreenWidth(), GuiUtil.getScreenHeight(), 16, 128, 16, 128);
    }
}
