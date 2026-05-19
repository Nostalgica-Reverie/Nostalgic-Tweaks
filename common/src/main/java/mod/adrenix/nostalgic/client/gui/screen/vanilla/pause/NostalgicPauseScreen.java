package mod.adrenix.nostalgic.client.gui.screen.vanilla.pause;

import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.PauseLayout;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class NostalgicPauseScreen extends PauseScreen implements DynamicScreen<NostalgicPauseScreen>
{
    /* Fields */

    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    private final PauseWidgets pauseWidgets;
    private final PauseLayout layout;
    private boolean isTitleHigh;
    private boolean isLayoutSet;

    /* Constructor */

    public NostalgicPauseScreen()
    {
        super(true);

        this.layout = CandyTweak.OLD_PAUSE_MENU.get();
        this.widgets = new UniqueArrayList<>();
        this.pauseWidgets = new PauseWidgets(this);
    }

    /* Methods */

    /**
     * @return The {@link PauseLayout} being used by the nostalgic pause screen.
     */
    public PauseLayout getLayout()
    {
        return this.layout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        this.isTitleHigh = this.layout == PauseLayout.ALPHA_BETA || this.layout == PauseLayout.ACHIEVE_LOWER;

        this.title = switch (this.layout)
        {
            case ALPHA_BETA, ACHIEVE_LOWER, ACHIEVE_UPPER, LAN -> Lang.Pause.GAME_MENU.get();
            case ADVANCEMENT, MODERN -> Lang.Vanilla.MENU_GAME.get();
        };

        this.clearWidgets();
        this.pauseWidgets.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NostalgicPauseScreen self()
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (!this.isLayoutSet)
        {
            if (CandyTweak.REMOVE_EXTRA_PAUSE_BUTTONS.get())
            {
                this.clearWidgets();
                this.init();
            }

            this.isLayoutSet = true;
        }

        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);

        int titleY = this.isTitleHigh ? 40 : this.height / 4 - 20;

        graphics.drawCenteredString(this.font, this.title, this.width / 2, titleY, 0xFFFFFF);
    }
}
