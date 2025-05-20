package mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller;

import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl.ItemMapOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl.ItemSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.impl.StringSetOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.TweakRowLayout;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;

public class Controller
{
    /* Constants */

    public static final int BUTTON_WIDTH = 120;
    public static final int ENUM_WIDTH = 183;
    public static final int ENUM_HEIGHT = 130;

    /* Fields */

    private final Tweak<?> tweak;
    private final TweakRowLayout layout;

    /* Constructor */

    /**
     * Create a new controller widget instance. Use {@link Controller#create()} to build a widget that best suits the
     * provided tweak.
     *
     * @param layout A {@link TweakRowLayout} instance.
     */
    public Controller(TweakRowLayout layout)
    {
        this.layout = layout;
        this.tweak = layout.getTweak();
    }

    /* Methods */

    /**
     * @return The {@link TweakRowLayout} instance.
     */
    TweakRowLayout getLayout()
    {
        return this.layout;
    }

    /**
     * @return The widget that will be to the right of a {@link Controller} widget.
     */
    DynamicWidget<?, ?> getLeftOf()
    {
        return this.layout.getStartOfRightSide();
    }

    /**
     * Depending on the default value assigned to the tweak, different controllers will appear to match the class type
     * the tweak stores.
     *
     * @return A {@link DynamicWidget} instance.
     */
    public DynamicWidget<?, ?> create()
    {
        // Custom
        if (this.tweak instanceof TweakCustom custom)
            return new CustomController(this, custom).getWidget();

        // Boolean
        if (this.tweak instanceof TweakFlag flag)
            return new BooleanController(this, flag).getWidget();

        // String
        if (this.tweak instanceof TweakText text)
            return new StringController(this, text).getWidget();

        // Color
        if (this.tweak instanceof TweakColor color)
            return new StringController(this, color).getWidget();

        // Number
        if (this.tweak instanceof TweakNumber<?> number)
            return new NumberController(this, number).getWidget();

        // Keybinding
        if (this.tweak instanceof TweakBinding binding)
            return new KeybindingController(this, binding).getWidget();

        // Enumeration
        if (this.tweak instanceof TweakEnum<?> enumeration)
            return new EnumController(this, enumeration).getWidget();

        // String Set
        if (this.tweak instanceof TweakStringSet stringSet)
            return new ListingController(this, stringSet, StringSetOverlay::new).getWidget();

        // Item Set
        if (this.tweak instanceof TweakItemSet itemSet)
            return new ListingController(this, itemSet, ItemSetOverlay::new).getWidget();

        // Item Map
        if (ItemMap.cast(this.tweak).isPresent())
            return new ListingController(this, ItemMap.cast(this.tweak).get(), ItemMapOverlay::new).getWidget();

        // Unknown
        return ButtonWidget.create(Lang.literal("NO-IMPL"))
            .disabledTooltip(Lang.TweakRow.NO_IMPL, 45)
            .disableIf(BooleanSupplier.ALWAYS)
            .icon(Icons.NO_ENTRY)
            .leftOf(this.getLeftOf(), 1)
            .width(BUTTON_WIDTH)
            .padding(4)
            .build();
    }
}
