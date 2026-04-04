package mod.adrenix.nostalgic.client.gui.screen.vanilla.title;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.text.FallingBlockText;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.mixin.access.TitleScreenAccess;
import mod.adrenix.nostalgic.tweak.enums.TitleLayout;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.FallingBlockRenderer;
//import mod.adrenix.nostalgic.tweak.config.CandyTweak;

public class NostalgicTitleScreen extends TitleScreen implements DynamicScreen<NostalgicTitleScreen> {
    /* Static */

    private static final FlagHolder TOGGLE_LOGO = FlagHolder.off();

    /* Fields */
    private final LogoRenderer imageLogo;
    private final UniqueArrayList<DynamicWidget<?, ?>> empty;
    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    private final TitleWidgets titleWidgets;
    private final TitleScreenAccess titleAccess;
    //    private FallingBlockRenderer blockLogo; //TODO
    private boolean isLayoutSet;

    /* Constructor */

    /**
     * Create a new {@link NostalgicTitleScreen} instance.
     */
    public NostalgicTitleScreen() {
        this.empty = new UniqueArrayList<>();
        this.widgets = new UniqueArrayList<>();
        this.titleWidgets = new TitleWidgets(this);
//        this.blockLogo = new FallingBlockRenderer(); //TODO
        this.imageLogo = new LogoRenderer(false);
        this.titleAccess = (TitleScreenAccess) this;

        //TODO
//        if (CandyTweak.USE_CUSTOM_FALLING_LOGO.get()) {
//            if (FallingBlockConfig.read())
//                NostalgicTweaks.LOGGER.debug("[Falling Blocks] Successfully read config into title screen");
//            else
//                NostalgicTweaks.LOGGER.warn("[Falling Blocks] The falling blocks config is corrupt!");
//
//            if (FallingBlockConfig.hasNoBlocks()) {
//                FallingBlockConfig.setBlockDataToDefault();
//                NostalgicTweaks.LOGGER.warn("[Falling Blocks] The falling blocks config is empty! Showing default logo.");
//            }
//
//            this.blockLogo = new FallingBlockRenderer(FallingBlockConfig.getData());
//        }
    }

    /* Methods */

    /**
     * @return The {@link TitleLayout} being used by the nostalgic title screen.
     */
    public TitleLayout getLayout() {
        return TitleLayout.BETA;//CandyTweak.TITLE_BUTTON_LAYOUT.get(); //TODO
    }

    /**
     * Reset the falling block logo animation.
     */
    public void resetBlockLogo() {
        //TODO
//        if (CandyTweak.USE_CUSTOM_FALLING_LOGO.get()) {
//            if (FallingBlockConfig.hasNoBlocks())
//                FallingBlockConfig.setBlockDataToDefault();
//
//            this.blockLogo = new FallingBlockRenderer(FallingBlockConfig.getData());
//        } else
//            this.blockLogo = new FallingBlockRenderer();
    }

    /**
     * Toggle between the falling block logo animation and the resource pack title logo.
     */
    public void switchLogo() {
        if (true/*!CandyTweak.CLICK_ON_LOGO_TOGGLE.get()*/) //TODO
            return;

        TOGGLE_LOGO.toggle();

        this.resetBlockLogo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();

        if (this.getLayout() != TitleLayout.MODERN)
            this.clearWidgets();

        this.titleWidgets.init();

        for (Renderable widget : this.renderables) {
            if (widget instanceof SpriteIconButton iconButton && iconButton.getX() == this.width / 2 - 124)
                iconButton.visible = true;//!CandyTweak.REMOVE_TITLE_LANGUAGE_BUTTON.get(); //TODO
            else if (widget instanceof SpriteIconButton iconButton && iconButton.getX() == this.width / 2 + 104)
                iconButton.visible = true;//!CandyTweak.REMOVE_TITLE_ACCESSIBILITY_BUTTON.get(); //TODO
            else if (widget instanceof Button button) {
                boolean isRealms = button.getMessage().getString().equals(Lang.Vanilla.MENU_ONLINE.getString());
                boolean isRemovable = true;//CandyTweak.REMOVE_TITLE_REALMS_BUTTON.get(); //TODO

                button.visible = !isRealms || !isRemovable;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NostalgicTitleScreen self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets() {
        return this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Screen getParentScreen() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends GuiEventListener> children() {
        return this.getLayout() == TitleLayout.MODERN ? this.children : this.empty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.minecraft == null)
            return false;
        else if (event.key() == InputConstants.KEY_M)
            this.minecraft.setScreen(new NostalgicTitleScreen());

        return super.keyPressed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.getLayout() != TitleLayout.MODERN && !this.isLayoutSet) {
            if (false/*CandyTweak.REMOVE_EXTRA_TITLE_BUTTONS.get()*/) { //TODO
                this.clearWidgets();
                this.init();
            }

            this.isLayoutSet = true;
        }

        if (false/*CandyTweak.OLD_TITLE_BACKGROUND.get()*/) { //TODO
            if (false/*CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get()*/) //TODO
                this.extractMenuBackground(graphics);
            else
                GuiUtil.extractDirtBackground(graphics);
        } else
            this.extractPanorama(graphics, PartialTick.realtime());

        if (this.minecraft == null || this.minecraft.getOverlay() != null)
            return;

        if (FallingBlockConfig.LOGO_CHANGED.ifEnabledThenDisable() || FallingBlockText.LOGO_CHANGED.ifEnabledThenDisable())
            this.resetBlockLogo();

        if (false/*CandyTweak.OLD_ALPHA_LOGO.get()*/) { //TODO
            //TODO
//            if (TOGGLE_LOGO.get())
            this.imageLogo.extractRenderState(graphics, this.width, 1.0F);
//            else
//                this.blockLogo.render();
        } else {
            //TODO
//            if (TOGGLE_LOGO.get())
//                this.blockLogo.render();
//            else
            this.imageLogo.extractRenderState(graphics, this.width, 1.0F);
        }

        if (this.titleAccess.nt$getSplash() != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(0.0F/*CandyTweak.SPLASH_OFFSET_X.get()*/, /*CandyTweak.SPLASH_OFFSET_Y.get()*/ 0.0F); //TODO

            this.titleAccess.nt$getSplash().extractRenderState(graphics, this.width, this.font, 0xFFFF00);

            graphics.pose().popMatrix();
        }

        Component copyright = switch (this.getLayout()) {
            case ALPHA -> Lang.Title.COPYRIGHT_ALPHA.get();
            case BETA -> Lang.Title.COPYRIGHT_BETA.get();
            default -> Component.translatable("title.credits");
        };

        String minecraft = GameUtil.getVersion();//CandyTweak.TITLE_VERSION_TEXT.parse(GameUtil.getVersion());

        if (Minecraft.checkModStatus().shouldReportAsModified()/*!CandyTweak.REMOVE_TITLE_MOD_LOADER_TEXT.get()*/) //TODO
            minecraft = minecraft + "/" + this.minecraft.getVersionType() + Lang.Vanilla.MENU_MODDED.getString();

        int versionColor = /*CandyTweak.OLD_TITLE_BACKGROUND.get() && !minecraft.contains("§") ? 5263440 :*/ 0xFFFFFFFF; //TODO
        int height =/* CandyTweak.TITLE_BOTTOM_LEFT_TEXT.get() ? this.height - 10 :*/ 2; //TODO

        graphics.text(this.font, minecraft, 2, height, versionColor);
        graphics.text(this.font, copyright, this.width - this.font.width(copyright) - 2, this.height - 10, 0xFFFFFFFF);

        if (false/*CandyTweak.TITLE_TOP_RIGHT_DEBUG_TEXT.get()*/) { //TODO
            long max = Runtime.getRuntime().maxMemory();
            long total = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            long used = total - free;

            String memory = String.format("Free memory: %s%% of %sMB", used * 100L / max, MathUtil.bytesToMegabytes(max));
            String allocated = String.format("Allocated memory: %s%% (%sMB)", total * 100L / max, MathUtil.bytesToMegabytes(total));

            int memX = this.width - this.font.width(memory) - 2;
            int allX = this.width - this.font.width(allocated) - 2;

            graphics.text(this.font, memory, memX, 2, 0x808080);
            graphics.text(this.font, allocated, allX, GuiUtil.textHeight() + 3, 0x808080);
        }

        if (this.getLayout() != TitleLayout.MODERN)
            DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);
        else
            this.renderables.forEach(renderable -> renderable.extractRenderState(graphics, mouseX, mouseY, partialTick));

        //TODO
//        RenderSystem.enableDepthTest();

        if (this.titleAccess.nt$getRealmsNotificationsEnabled() && this.getLayout() == TitleLayout.MODERN)
            this.titleAccess.nt$getRealmsNotificationsScreen().extractRenderState(graphics, mouseX, mouseY, partialTick);
    }
}
