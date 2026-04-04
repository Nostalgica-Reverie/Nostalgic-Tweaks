package mod.adrenix.nostalgic.client.gui.widget.button;

import net.minecraft.network.chat.Component;

public class ButtonBuilder extends AbstractButtonMaker<ButtonBuilder, ButtonWidget> {
    /* Constructor */

    protected ButtonBuilder(Component title) {
        super(title);
    }

    /* Methods */

    @Override
    public ButtonBuilder self() {
        return this;
    }

    @Override
    protected ButtonWidget construct() {
        return new ButtonWidget(this, this::onPress);
    }
}
