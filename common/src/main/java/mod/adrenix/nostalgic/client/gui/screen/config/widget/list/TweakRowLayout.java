package mod.adrenix.nostalgic.client.gui.screen.config.widget.list;

import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.client.gui.screen.config.ConfigScreen;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.StatusOverlay;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.crumb.CrumbWidget;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.list.controller.Controller;
import mod.adrenix.nostalgic.client.gui.screen.config.widget.tag.TagWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.TooltipBuilder;
import mod.adrenix.nostalgic.client.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.tweak.FavoriteTweak;
import mod.adrenix.nostalgic.tweak.TweakContext;
import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TweakRowLayout
{
    /* Fields */

    final int x = 5;
    final int y = 5;
    final int padding = 5;
    final NullableHolder<DynamicWidget<?, ?>> controller = NullableHolder.empty();
    final NullableHolder<DynamicWidget<?, ?>> picker = NullableHolder.empty();
    final LinkedHashSet<TextWidget> descriptions = new LinkedHashSet<>();
    final CrumbWidget breadcrumbs;
    final TagWidget tags;
    final TextWidget title;
    final ButtonWidget modern;
    final ButtonWidget reset;
    final ButtonWidget undo;
    final ButtonWidget save;
    final ButtonWidget cache;
    final ButtonWidget status;
    final ButtonWidget favorite;
    final DynamicWidget<?, ?> startOfRightSide;
    final Tweak<?> tweak;
    final TweakRow row;

    /* Constructor */

    TweakRowLayout(TweakRow row)
    {
        this.row = row;

        this.tweak = row.getTweak();

        this.breadcrumbs = CrumbWidget.create(this.tweak)
            .pos(this.x, this.y)
            .syncWithRow(this.row)
            .build(this.row::addWidget);

        this.tags = TagWidget.create(this.tweak)
            .alignFlushTo(this.breadcrumbs)
            .below(this.breadcrumbs, this.padding)
            .build(this.row::addWidget);

        this.title = TextWidget.create(this.tweak.getTranslation())
            .color(this.row.getColor().brighter())
            .below(this.tags, this.padding + 2)
            .alignFlushTo(this.breadcrumbs)
            .italicsWhen(this.tweak::isCurrentCacheSavable)
            .build(this.row::addWidget);

        this.modern = ButtonWidget.create()
            .below(this.tags, 1)
            .icon(Icons.RED_X)
            .tooltip(Lang.Button.MODERN, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.TweakRow.MODERN, 40)
            .disabledInfoTooltip(Lang.TweakRow.MODERN_OFF, 45)
            .disableIf(CollectionUtil.areAnyTrue(this.tweak::isCacheDisabled, this.tweak::isNetworkLocked))
            .fromWidgetEndX(this.row, this.padding)
            .onPress(this.tweak::setCacheDisabled)
            .build(this.row::addWidget);

        this.reset = ButtonWidget.create()
            .leftOf(this.modern, 1)
            .icon(Icons.RED_REDO)
            .tooltip(Lang.Button.RESET, 40, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.TweakRow.RESET, 40)
            .disabledInfoTooltip(Lang.TweakRow.RESET_OFF, 45)
            .disableIf(CollectionUtil.areAnyTrue(this.tweak::isCacheDefault, this.tweak::isNetworkLocked))
            .onPress(this.tweak::setCacheToDefault)
            .build(this.row::addWidget);

        this.undo = ButtonWidget.create()
            .leftOf(this.reset, 1)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .tooltip(Lang.Button.UNDO, 500L, TimeUnit.MILLISECONDS)
            .disableIf(CollectionUtil.areAnyTrue(this.tweak::isCacheNotUndoable, this.tweak::isNetworkLocked))
            .onPress(this.tweak::undoCache)
            .build(this.row::addWidget);

        this.save = ButtonWidget.create()
            .leftOf(this.undo, 1)
            .icon(Icons.SAVE_FLOPPY)
            .tooltip(Lang.Button.SAVE, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.supply(this.tweak::isLocalMode, Lang.Tooltip.SAVE_TWEAK_LOCAL, Lang.Tooltip.SAVE_TWEAK_NETWORK), 35)
            .onPress(CollectionUtil.runAll(this.tweak::applyCurrentCache, ConfigCache::save, AfterConfigSave::run))
            .enableIf(CollectionUtil.areAllTrue(this.tweak::isCurrentCacheSavable, this.tweak::isNetworkUnlocked))
            .build(this.row::addWidget);

        // This is defined here, so it can be referenced by controller rows later
        this.status = ButtonWidget.create()
            .onPress(() -> new StatusOverlay(this.tweak).open())
            .icon(() -> TweakContext.from(this.tweak).getIcon(this.isFlashing()))
            .tooltip(Lang.Button.STATUS, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.TweakRow.STATUS, 40)
            .build();

        // This is defined here, so it can be referenced by controller rows later
        this.favorite = ButtonWidget.create()
            .onPress(() -> FavoriteTweak.toggle(this.tweak))
            .icon(() -> FavoriteTweak.isPresent(this.tweak) ? Icons.STAR_ON : Icons.STAR_OFF)
            .tooltip(Lang.Button.FAVORITE, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.supply(() -> FavoriteTweak.isAbsent(this.tweak), Lang.TweakRow.STAR, Lang.TweakRow.STAR_OFF), 40)
            .build();

        // This field needs updated if the first widget to the right of tweak controller changes
        this.startOfRightSide = this.save;

        TweakDescription.init(this);
        DynamicWidget<?, ?> controller = new Controller(this).create();

        this.controller.set(controller);
        this.controller.ifPresent(this.row::addWidget);

        if (controller.getBuilder() instanceof TooltipBuilder<?, ?> builder)
            builder.disabledTooltip(Lang.Tooltip.NOT_OPERATOR, 45, 500L, TimeUnit.MILLISECONDS);

        if (controller instanceof ColorInput colorInput)
            this.picker.set(colorInput.getPickerButton().leftOf(controller, 1).build(this.row::addWidget));

        this.cache = ButtonWidget.create()
            .icon(() -> this.tweak.isLocalMode() ? Icons.CLIENT : Icons.SERVER)
            .tooltip(Lang.Button.LOGICAL_SIDE, 700L, TimeUnit.MILLISECONDS)
            .infoTooltip(this::getCacheTooltip, 40)
            .onPress(this.tweak::toggleCacheMode)
            .enableIf(this.tweak::isNetworkAvailable)
            .leftOf(this.picker.orElse(controller), 1)
            .build(this.row::addWidget);

        this.status.getBuilder().leftOf(this.cache, 1);
        this.favorite.getBuilder().leftOf(this.status, 1);

        this.row.addWidget(this.status);
        this.row.addWidget(this.favorite);
        this.title.getBuilder().extendWidthTo(this.favorite, 3);

        this.setTabOrder();
        this.setUpdateOrder();
    }

    /* Methods */

    /**
     * @return The {@link Tweak} assigned to this layout.
     */
    public Tweak<?> getTweak()
    {
        return this.tweak;
    }

    /**
     * @return The {@link TweakRow} instance managing this layout.
     */
    public TweakRow getRow()
    {
        return this.row;
    }

    /**
     * The start of the right-side widgets. These are the widgets to the right of the tweak controller.
     *
     * @return A {@link DynamicWidget} instance.
     */
    public DynamicWidget<?, ?> getStartOfRightSide()
    {
        return this.startOfRightSide;
    }

    /**
     * @return The favorite {@link ButtonWidget}.
     */
    @PublicAPI
    public ButtonWidget getFavorite()
    {
        return this.favorite;
    }

    /**
     * @return The status {@link ButtonWidget}.
     */
    @PublicAPI
    public ButtonWidget getStatus()
    {
        return this.status;
    }

    /**
     * @return The save {@link ButtonWidget}.
     */
    @PublicAPI
    public ButtonWidget getSave()
    {
        return this.save;
    }

    /**
     * @return The undo {@link ButtonWidget}.
     */
    @PublicAPI
    public ButtonWidget getUndo()
    {
        return this.undo;
    }

    /**
     * @return The reset {@link ButtonWidget}.
     */
    @PublicAPI
    public ButtonWidget getReset()
    {
        return this.reset;
    }

    /**
     * @return The modern {@link ButtonWidget}.
     */
    @PublicAPI
    public ButtonWidget getModern()
    {
        return this.modern;
    }

    /**
     * @return A {@link Component} to display for the sided tweak-cache context.
     */
    private Component getCacheTooltip()
    {
        if (this.tweak.isClient())
            return Lang.TweakRow.CACHE_CLIENT.get();

        if (this.tweak.isNetworkUnavailable())
            return Lang.TweakRow.NETWORK_DISCONNECTED.get();

        return Lang.TweakRow.CACHE.get(this.tweak.isNetworkMode() ? "client" : "server");
    }

    /**
     * @return Whether the current config screen {@code flash} flag state is enabled.
     */
    private boolean isFlashing()
    {
        return GuiUtil.getScreenAs(ConfigScreen.class).stream().anyMatch(ConfigScreen::getTimerState);
    }

    /**
     * Set the correct tab order for the widgets in this row.
     */
    private void setTabOrder()
    {
        IntegerHolder order = IntegerHolder.create(0);
        Consumer<DynamicWidget<?, ?>> setOrder = (widget) -> widget.setTabOrderGroup(order.getAndIncrement());

        setOrder.accept(this.favorite);
        setOrder.accept(this.status);
        setOrder.accept(this.cache);

        if (this.picker.isPresent())
            setOrder.accept(this.picker.get());

        if (this.controller.isPresent())
            setOrder.accept(this.controller.get());

        setOrder.accept(this.save);
        setOrder.accept(this.undo);
        setOrder.accept(this.reset);
        setOrder.accept(this.modern);
    }

    /**
     * Set the correct order in which row widgets update.
     */
    private void setUpdateOrder()
    {
        this.row.moveToFront(this.favorite);
        this.row.moveToFront(this.status);
        this.row.moveToFront(this.cache);

        if (this.picker.isPresent())
            this.row.moveToFront(this.picker.getOrThrow());

        if (this.controller.isPresent())
            this.row.moveToFront(this.controller.getOrThrow());

        this.row.moveToFront(this.save);
        this.row.moveToFront(this.undo);
        this.row.moveToFront(this.reset);
        this.row.moveToFront(this.modern);
        this.row.moveToFront(this.tags);
        this.row.moveToFront(this.breadcrumbs);
    }
}
