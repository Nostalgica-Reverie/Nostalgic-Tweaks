package mod.adrenix.nostalgic.client.gui.screen.vanilla.title;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.FallingBlockRenderer;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.text.FallingBlockText;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.mixin.access.TitleScreenAccess;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.TitleLayout;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NostalgicTitleScreen extends TitleScreen implements DynamicScreen<NostalgicTitleScreen>
{
    /* Fields */

    private FallingBlockRenderer blockLogo;
    private final LogoRenderer imageLogo;
    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    private final TitleWidgets titleWidgets;
    private final TitleScreenAccess titleAccess;
    private boolean isLayoutSet;

    /* Constructor */

    /**
     * Create a new {@link NostalgicTitleScreen} instance.
     */
    public NostalgicTitleScreen()
    {
        this.widgets = new UniqueArrayList<>();
        this.titleWidgets = new TitleWidgets(this);
        this.blockLogo = new FallingBlockRenderer();
        this.imageLogo = new LogoRenderer(false);
        this.titleAccess = (TitleScreenAccess) this;

        if (CandyTweak.USE_CUSTOM_FALLING_LOGO.get())
        {
            if (FallingBlockConfig.read())
                NostalgicTweaks.LOGGER.debug("[Falling Blocks] Successfully read config into title screen");
            else
                NostalgicTweaks.LOGGER.warn("[Falling Blocks] The falling blocks config is corrupt!");

            if (FallingBlockConfig.hasNoBlocks())
            {
                FallingBlockConfig.setBlockDataToDefault();
                NostalgicTweaks.LOGGER.warn("[Falling Blocks] The falling blocks config is empty! Showing default logo.");
            }

            this.blockLogo = new FallingBlockRenderer(FallingBlockConfig.getBlockData());
        }
    }

    /* Methods */

    /**
     * @return The {@link TitleLayout} being used by the nostalgic title screen.
     */
    public TitleLayout getLayout()
    {
        return CandyTweak.TITLE_BUTTON_LAYOUT.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        super.init();

        if (this.getLayout() != TitleLayout.MODERN)
        {
            this.clearWidgets();
            this.titleWidgets.init();
        }

        for (Renderable widget : this.renderables)
        {
            if (widget instanceof SpriteIconButton iconButton && iconButton.getX() == this.width / 2 - 124)
                iconButton.visible = !CandyTweak.REMOVE_TITLE_LANGUAGE_BUTTON.get();
            else if (widget instanceof SpriteIconButton iconButton && iconButton.getX() == this.width / 2 + 104)
                iconButton.visible = !CandyTweak.REMOVE_TITLE_ACCESSIBILITY_BUTTON.get();
            else if (widget instanceof Button button)
            {
                boolean isRealms = button.getMessage().getString().equals(Lang.Vanilla.MENU_ONLINE.getString());
                boolean isRemovable = CandyTweak.REMOVE_TITLE_REALMS_BUTTON.get();

                button.visible = !isRealms || !isRemovable;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NostalgicTitleScreen self()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets()
    {
        return this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Screen getParentScreen()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends GuiEventListener> children()
    {
        return this.getLayout() == TitleLayout.MODERN ? this.children : this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.minecraft == null)
            return false;
        else if (keyCode == InputConstants.KEY_M)
            this.minecraft.setScreen(new NostalgicTitleScreen());

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.getLayout() != TitleLayout.MODERN && !this.isLayoutSet)
        {
            if (CandyTweak.REMOVE_EXTRA_TITLE_BUTTONS.get())
            {
                this.clearWidgets();
                this.init();
            }

            this.isLayoutSet = true;
        }

        if (CandyTweak.OLD_TITLE_BACKGROUND.get())
        {
            if (CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get())
                this.renderMenuBackground(graphics);
            else
                GuiUtil.renderDirtBackground(graphics);
        }
        else
            this.renderPanorama(graphics, PartialTick.realtime());

        if (this.minecraft == null || this.minecraft.getOverlay() != null)
            return;

        if (FallingBlockConfig.LOGO_CHANGED.ifEnabledThenDisable() || FallingBlockText.LOGO_CHANGED.ifEnabledThenDisable())
        {
            if (CandyTweak.USE_CUSTOM_FALLING_LOGO.get())
            {
                if (FallingBlockConfig.hasNoBlocks())
                    FallingBlockConfig.setBlockDataToDefault();

                this.blockLogo = new FallingBlockRenderer(FallingBlockConfig.getBlockData());
            }
            else
                this.blockLogo = new FallingBlockRenderer();
        }

        if (CandyTweak.OLD_ALPHA_LOGO.get())
            this.blockLogo.render();
        else
            this.imageLogo.renderLogo(graphics, this.width, 1.0F);

        if (this.titleAccess.nt$getSplash() != null)
        {
            graphics.pose().pushPose();
            graphics.pose().translate(CandyTweak.SPLASH_OFFSET_X.get(), CandyTweak.SPLASH_OFFSET_Y.get(), 0.0F);

            this.titleAccess.nt$getSplash().render(graphics, this.width, this.font, 0xFFFF00);

            graphics.pose().popPose();
        }

        Component copyright = switch (this.getLayout())
        {
            case ALPHA -> Lang.Title.COPYRIGHT_ALPHA.get();
            case BETA -> Lang.Title.COPYRIGHT_BETA.get();
            default -> Component.translatable("title.credits");
        };

        String minecraft = CandyTweak.TITLE_VERSION_TEXT.parse(GameUtil.getVersion());

        if (Minecraft.checkModStatus().shouldReportAsModified() && !CandyTweak.REMOVE_TITLE_MOD_LOADER_TEXT.get())
            minecraft = minecraft + "/" + this.minecraft.getVersionType() + Lang.Vanilla.MENU_MODDED.getString();

        int versionColor = CandyTweak.OLD_TITLE_BACKGROUND.get() && !minecraft.contains("§") ? 5263440 : 0xFFFFFF;
        int height = CandyTweak.TITLE_BOTTOM_LEFT_TEXT.get() ? this.height - 10 : 2;

        graphics.drawString(this.font, minecraft, 2, height, versionColor);
        graphics.drawString(this.font, copyright, this.width - this.font.width(copyright) - 2, this.height - 10, 0xFFFFFF);

        if (CandyTweak.TITLE_TOP_RIGHT_DEBUG_TEXT.get())
        {
            long max = Runtime.getRuntime().maxMemory();
            long total = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            long used = total - free;

            String memory = String.format("Free memory: %s%% of %sMB", used * 100L / max, MathUtil.bytesToMegabytes(max));
            String allocated = String.format("Allocated memory: %s%% (%sMB)", total * 100L / max, MathUtil.bytesToMegabytes(total));

            int memX = this.width - this.font.width(memory) - 2;
            int allX = this.width - this.font.width(allocated) - 2;

            graphics.drawString(this.font, memory, memX, 2, 0x808080);
            graphics.drawString(this.font, allocated, allX, GuiUtil.textHeight() + 3, 0x808080);
        }

        if (this.getLayout() != TitleLayout.MODERN)
            DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);
        else
            this.renderables.forEach(renderable -> renderable.render(graphics, mouseX, mouseY, partialTick));

        RenderSystem.enableDepthTest();

        if (this.titleAccess.nt$getRealmsNotificationsEnabled() && this.getLayout() == TitleLayout.MODERN)
            this.titleAccess.nt$getRealmsNotificationsScreen().render(graphics, mouseX, mouseY, partialTick);
    }
}
