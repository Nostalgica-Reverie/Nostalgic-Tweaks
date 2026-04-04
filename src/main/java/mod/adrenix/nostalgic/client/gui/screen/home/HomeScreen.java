package mod.adrenix.nostalgic.client.gui.screen.home;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.GearSpinner;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

//import mod.adrenix.nostalgic.tweak.config.ModTweak;

public class HomeScreen extends EnhancedScreen<HomeScreen, HomeWidgets> {
    /* Fields */

    private static final TextureLocation TITLE_LOCATION = TextureLocation.NOSTALGIC_TWEAKS;

    private final boolean isRedirected;
    private HomeWidgets homeWidgets;
    private String splash;

    /* Constructor */

    public HomeScreen(@Nullable Screen parentScreen, boolean isRedirected) {
        super(HomeWidgets::new, parentScreen, Lang.TITLE.get());

        this.isRedirected = isRedirected;
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public HomeScreen self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HomeWidgets getWidgetManager() {
        return this.homeWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidgetManager(HomeWidgets widgetManager) {
        this.homeWidgets = widgetManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();

        this.splash = HomeSplash.getInstance().get();
        this.focusFirst();

        //TODO
//        if (this.isRedirected) {
//            this.isRedirected = false;
//
//            switch (ModTweak.DEFAULT_SCREEN.get()) {
//                case CONFIG_SCREEN -> this.minecraft.setScreen(new ConfigScreen(this));
//                case PACKS_SCREEN -> this.minecraft.setScreen(new PacksListScreen(this));
//                default -> this.minecraft.setScreen(this);
//            }
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick() {
        super.tick();

        //TODO
//        if (!ModTweak.OPENED_CONFIG_SCREEN.get() && !SodiumOverlay.OPENED.get()) {
//            ModTweak.OPENED_CONFIG_SCREEN.setDiskAndSave(true);
//            SetupOverlay.open();
//        }
//
//        if (!ModTweak.OPENED_SODIUM_SCREEN.get() && !SetupOverlay.OPENED.get() && ModTracker.SODIUM.isInstalled()) {
//            ModTweak.OPENED_SODIUM_SCREEN.setDiskAndSave(true);
//            SodiumOverlay.open();
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.hasControlDown() && KeyboardUtil.match(event.key(), InputConstants.KEY_SPACE)) {
            if (NTPanorama.isPaused())
                NTPanorama.unpause();
            else
                NTPanorama.pause();

            return true;
        }

        if (KeyboardUtil.isGoingRight(event)) {
            NTPanorama.forward();
            return true;
        }

        if (KeyboardUtil.isGoingLeft(event)) {
            NTPanorama.backward();
            return true;
        }

        return super.keyPressed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
//        this.extractBlurredBackground(graphics);

        if (this.minecraft.level == null)
            NTPanorama.extractRenderState(graphics);
        else
            graphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        this.renderTextures(graphics);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Renders the mod's textures (logos and splashes) to the home screen.
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    private void renderTextures(GuiGraphicsExtractor graphics) {
        float pulseScale = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2)) * 0.1F);
        float modScale = pulseScale * 12.0F / (float) (this.font.width("N.T"));
        float splashScale = pulseScale * 100.0f / (float) (this.font.width(this.splash) + 32);
        float titleScale = 0.15F;
        float gearScale = 0.07F;

        int titleW = Math.round(TITLE_LOCATION.width() * titleScale);
        int titleH = Math.round(TITLE_LOCATION.height() * titleScale);
        int titleX = Math.round(MathUtil.center(titleW, this.width));
        int titleY = Math.round(GuiUtil.getGuiHeight() * 0.09F);
        int gearY = Math.round(GuiUtil.getGuiHeight() - (gearScale * 512.0F));
        int gearX = 0;

        float splashX = titleX + titleW - 20.0F;
        float splashY = titleY + titleH - 2.0F;
        float modX = gearX + (gearScale * 512.0F) - 12.0F;
        float modY = gearY + (gearScale * 512.0F) - 9.0F;

        GearSpinner.getInstance().extractRenderState(graphics, gearScale, gearX, gearY);

        graphics.pose().pushMatrix();
        graphics.pose().translate(modX, modY);
        graphics.pose().rotate(-20.0F * Mth.DEG_TO_RAD);
        graphics.pose().scale(modScale, modScale);

        DrawText.begin(graphics, "N.T").pos(1, -6).color(Color.YELLOW).center().draw();

        graphics.pose().popMatrix();

        int width = (int) (TITLE_LOCATION.width() * titleScale);
        int height = (int) (TITLE_LOCATION.height() * titleScale);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TITLE_LOCATION.location(), titleX, titleY, 0.0F, 0.0F, width, height, width, height);

        graphics.pose().pushMatrix();
        graphics.pose().translate(splashX, splashY);
        graphics.pose().rotate(-20.0F * Mth.DEG_TO_RAD);
        graphics.pose().scale(splashScale, splashScale);

        DrawText.begin(graphics, this.splash).pos(0, -8).color(Color.YELLOW).center().draw();

        graphics.pose().popMatrix();
    }
}
