package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.client.timer.ClientTimer;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.timer.TickTimer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class AbstractButtonMaker<Builder extends AbstractButtonMaker<Builder, Button>, Button extends AbstractButton<Builder, Button>>
        extends DynamicBuilder<Builder, Button>
        implements LayoutBuilder<Builder, Button>, IconBuilder<Builder, Button>, TooltipBuilder<Builder, Button>,
        ActiveBuilder<Builder, Button>, VisibleBuilder<Builder, Button> {
    /* Fields */

    protected final Component title;
    @Nullable
    protected Supplier<Component> titleSupplier = null;

    protected int offsetIcon = 0;
    protected int padding = -1;
    protected int iconTextPadding = -1;
    protected int iconCenterOffset = 0;
    protected int alignLeftOffset = 0;
    protected boolean useClickSound = true;
    protected boolean useTextWidth = false;
    protected boolean alignLeft = false;
    protected boolean shrunk = false;
    protected FlagHolder cooldownFlag = FlagHolder.off();
    @Nullable
    protected TickTimer holdTimer = null;
    @Nullable
    protected TickTimer cooldownTimer = null;

    protected boolean useTextColors;
    protected Color textColor = new Color(0xE0E0E0);
    protected Color hoverColor = new Color(0xFFFFA0);

    @Nullable
    protected Consumer<Button> onPress = null;
    @Nullable
    protected ButtonRenderer<Builder, Button> renderer = null;
    @Nullable
    protected ButtonRenderer<Builder, Button> postRenderer = null;
    @Nullable
    protected ButtonRenderer<Builder, Button> backgroundRenderer = null;

    /* Constructor */

    protected AbstractButtonMaker(Component title) {
        this.title = title;
        this.brightenOnHover = true;

        this.addFunction(new Cooldown.Disable<>());
        this.addFunction(new Cooldown.Enable<>());
        this.addFunction(new LayoutListener<>());
        this.addFunction(new TitleListener<>());
    }

    /* Methods */

    /**
     * Define a message to appear under a certain condition. If this is defined, then any previous title defined during
     * the builder creation process will be overridden.
     *
     * @param supplier A component message supplier.
     */
    @PublicAPI
    public Builder title(Supplier<Component> supplier) {
        this.titleSupplier = supplier;

        return this.self();
    }

    /**
     * Change the text colors used during button rendering.
     *
     * @param textColor  The normal {@link Color} for the button.
     * @param hoverColor The hover {@link Color} for the button.
     */
    @PublicAPI
    public Builder color(Color textColor, Color hoverColor) {
        this.useTextColors = true;
        this.textColor = textColor;
        this.hoverColor = hoverColor;

        return this.self();
    }

    /**
     * Change the text colors used during button rendering.
     *
     * @param textColor  The normal {@link IntSupplier} that provides an ARGB integer for the button.
     * @param hoverColor The hover {@link IntSupplier} that provides an ARGB integer for the button.
     */
    @PublicAPI
    public Builder color(IntSupplier textColor, IntSupplier hoverColor) {
        return this.color(new Color(textColor), new Color(hoverColor));
    }

    /**
     * Change the text colors used during button rendering.
     *
     * @param textColor  The normal ARGB integer text color for the button.
     * @param hoverColor The hover ARGB integer text color for the button.
     */
    @PublicAPI
    public Builder color(int textColor, int hoverColor) {
        return this.color(new Color(textColor), new Color(hoverColor));
    }

    /**
     * Set a padding amount for the button. Useful if the {@link Builder#width(int)} is not being used. This will be
     * applied to both sides of the text. If there is an icon present, then the padding will be applied to both sides of
     * the icon if {@link Builder#iconTextPadding(int)} is not set.
     *
     * <br><br>
     * <p>
     * If the {@link Builder#useTextWidth()} flag is set to {@code true} then the default {@link Builder#padding} size
     * is {@code 6} if this method is never invoked.
     *
     * @param padding The amount of padding to add to the button's width.
     */
    @PublicAPI
    public Builder padding(int padding) {
        this.padding = padding;

        return this.self();
    }

    /**
     * Set a padding amount between the icon and the button's text.
     *
     * <br><br>
     * <p>
     * If the {@link Builder#useTextWidth()} flag is set to {@code true} then the default padding size is {@code 6} if
     * this method is never invoked.
     *
     * @param padding The amount of padding between the button's icon and text.
     */
    @PublicAPI
    public Builder iconTextPadding(int padding) {
        this.iconTextPadding = padding;

        return this.self();
    }

    /**
     * Set an off-center amount for the icon. A positive offset will shift the icon up on the screen's y-axis. A
     * negative offset will shift the icon down on the screen's y-axis.
     *
     * @param offset An offset amount to apply to the icon's vertical centering.
     */
    @PublicAPI
    public Builder iconCenterOffset(int offset) {
        this.iconCenterOffset = offset;

        return this.self();
    }

    /**
     * Offset the starting x-position of any icon by the given amount. Useful if mirroring icons with neighboring
     * buttons that have uneven icon spacing.
     *
     * @param offset The offset position.
     */
    @PublicAPI
    public Builder offsetIcon(int offset) {
        this.offsetIcon = offset;

        return this.self();
    }

    /**
     * Disable the clicking sound that plays when the button's on-press action is performed. The on-press action can be
     * performed by the mouse or by the enter keys when the button is focused.
     */
    @PublicAPI
    public Builder noClickSound() {
        this.useClickSound = false;

        return this.self();
    }

    /**
     * Change whether the width of the text should be used when calculating button width. Use
     * {@link Builder#padding(int)} to add padding around the text.
     */
    @PublicAPI
    public Builder useTextWidth() {
        this.useTextWidth = true;

        return this.self();
    }

    /**
     * Disable the horizontal centering of a button's icon and/or text.
     */
    @PublicAPI
    public Builder alignLeft() {
        this.alignLeft = true;

        return this.self();
    }

    /**
     * Disable the horizontal centering of a button's icon and/or text and add the given offset from the starting
     * x-position.
     *
     * @param offset The left-alignment offset amount.
     */
    @PublicAPI
    public Builder alignLeft(int offset) {
        this.alignLeft = true;
        this.alignLeftOffset = offset;

        return this.self();
    }

    /**
     * Disable the button for a specific amount of time before it becomes enabled again. The cooldown will respect the
     * current {@code active} state of the button, so it will not randomly enable/disable.
     *
     * @param delay    The delay before the button is reactivated.
     * @param timeUnit The {@link TimeUnit} enumeration to use for the delay.
     */
    @PublicAPI
    public Builder cooldown(long delay, TimeUnit timeUnit) {
        this.cooldownTimer = ClientTimer.getInstance().create(delay, timeUnit);

        return this.self();
    }

    /**
     * Enforce that the button must be held by a successful mouse click for a certain amount of time before the on-press
     * instructions are fired.
     *
     * @param holdFor  The amount of time the button must be held for.
     * @param timeUnit The {@link TimeUnit} enumeration to use for holding time.
     */
    @PublicAPI
    public Builder holdFor(long holdFor, TimeUnit timeUnit) {
        this.holdTimer = ClientTimer.getInstance().create(holdFor, timeUnit);

        return this.self();
    }

    /**
     * Provide instructions to perform when this button is pressed. This overload method uses a consumer that receives
     * the button instance when invoked.
     *
     * @param onPress A consumer that accepts the current button instance and provides instructions when clicked.
     */
    @PublicAPI
    public Builder onPress(Consumer<Button> onPress) {
        this.onPress = onPress;

        return this.self();
    }

    /**
     * Provide instructions to perform when the button is pressed.
     *
     * @param onPress Post successful click instructions.
     */
    @PublicAPI
    public Builder onPress(Runnable onPress) {
        return this.onPress(widget -> onPress.run());
    }

    /**
     * Provide a custom renderer. This will override the default vanilla renderer and button factory renderer. The
     * custom renderer will need to render any icons, text, and backgrounds itself.
     *
     * @param renderer A custom renderer consumer that accepts the required parameters.
     */
    @PublicAPI
    public Builder renderer(ButtonRenderer<Builder, Button> renderer) {
        this.renderer = renderer;

        return this.self();
    }

    /**
     * Provide custom rendering instructions after the button finishes rendering. Unlike
     * {@link Builder#renderer(ButtonRenderer)}, this will <b>not</b> prevent default button rendering. Instead, the
     * provided function will be invoked after the button has finished default rendering.
     *
     * @param renderer A custom renderer consumer that accepts the required parameters.
     */
    @PublicAPI
    public Builder postRenderer(ButtonRenderer<Builder, Button> renderer) {
        this.postRenderer = renderer;

        return this.self();
    }

    /**
     * Provide a custom background renderer that will override the default background button rendering. The custom
     * renderer does not need to handle rendering the button's icon or text.
     *
     * @param renderer A custom renderer consumer that accepts the required parameters.
     */
    @PublicAPI
    public Builder backgroundRenderer(ButtonRenderer<Builder, Button> renderer) {
        this.backgroundRenderer = renderer;

        return this.self();
    }

    /**
     * The on-press instructions for buttons.
     *
     * @param button The built {@link Button} instance.
     */
    protected void onPress(Button button) {
        if (this.holdTimer != null && this.holdTimer.isTicking())
            return;

        if (this.onPress != null)
            this.onPress.accept(button);

        if (this.cooldownTimer != null) {
            this.cooldownFlag.enable();
            ClientTimer.getInstance().run(this.cooldownTimer, () -> this.cooldownFlag.disable());
        }
    }
}
