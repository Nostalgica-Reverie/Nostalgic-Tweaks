package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.tweak.factory.TweakCustom;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

public class CustomController
{
    /* Fields */

    private final Controller controller;
    private final TweakCustom tweak;

    /* Constructor */

    /**
     * Create a new keybinding controller instance.
     *
     * @param controller The originating controller.
     * @param tweak      The keybinding tweak this controller manages.
     */
    public CustomController(Controller controller, TweakCustom tweak)
    {
        this.controller = controller;
        this.tweak = tweak;
    }

    /* Methods */

    /**
     * @return Create a new custom button widget instance that will open a custom tweak editor.
     */
    public ButtonWidget getWidget()
    {
        ButtonWidget widget = ButtonWidget.create(this.tweak.getController().getTitle())
            .width(Controller.BUTTON_WIDTH)
            .leftOf(this.controller.getLeftOf(), 1)
            .onPress(this.tweak.getController().getOnPress())
            .build();

        this.controller.getLayout().getModern().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout().getSave().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout().getUndo().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout().getReset().getBuilder().disableIf(BooleanSupplier.ALWAYS);
        this.controller.getLayout()
            .getStatus()
            .getBuilder()
            .disableIf(BooleanSupplier.ALWAYS)
            .icon(Icons.TRAFFIC_LIGHT_OFF);

        return widget;
    }
}
