package mod.adrenix.nostalgic.client.gui.widget.blank;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

public class BlankWidget extends DynamicWidget<BlankBuilder, BlankWidget> {
    /* Builder */

    protected BlankWidget(BlankBuilder builder) {
        super(builder);
    }

    /* Constructor */

    /**
     * Start the creation process of a new {@link BlankWidget}. Blank widgets are just that, blank. A use case would be
     * when another widget needs to extend to a certain point on the screen. A blank widget can be positioned there, and
     * the widget can use the blank widget as the width extension reference.
     *
     * @return A new {@link BlankBuilder} instance.
     */
    public static BlankBuilder create() {
        return new BlankBuilder();
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isInactive() || this.isInvisible())
            return false;

        if (this.isValidClick(event) && this.getBuilder().onPress != null) {
            if (this.getBuilder().useClickSound)
                GuiUtil.playClick();

            this.getBuilder().onPress.run();
        }

        return super.mouseClicked(event, doubleClick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        this.getBuilder().renderer.accept(this, graphics, mouseX, mouseY, partialTick);
        this.renderDebug(graphics);
    }
}
