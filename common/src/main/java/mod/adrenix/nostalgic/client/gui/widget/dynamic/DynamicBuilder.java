package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.client.renderer.RenderPass;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.data.NullableAction;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.function.FloatSupplier;
import mod.adrenix.nostalgic.util.common.function.ToFloatFunction;
import mod.adrenix.nostalgic.util.common.timer.FlagTimer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public abstract class DynamicBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
    implements SelfBuilder<Builder, Widget>
{
    /* Abstraction */

    /**
     * This is used to create a new widget instance. The {@link SelfBuilder#build()} method will handle finalizing the
     * building process.
     *
     * @return A new {@link Widget} instance.
     */
    protected abstract Widget construct();

    /* Widget Holder */

    protected final NullableHolder<Widget> widget = NullableHolder.empty();

    /**
     * @return The {@link NullableHolder} that holds a {@code nullable} {@link Widget}.
     */
    public NullableHolder<Widget> getWidget()
    {
        return this.widget;
    }

    /* Widget Options */

    @Nullable protected Consumer<Widget> afterSync = null;
    @Nullable protected Consumer<Widget> beforeSync = null;
    @Nullable protected Consumer<Widget> whenFocused = null;
    protected BooleanSupplier canFocus = BooleanSupplier.ALWAYS;
    protected RenderPass renderPass = RenderPass.MIDDLE;
    protected boolean focusOnClick = true;
    protected int tabOrderGroup = 0;
    protected final UniqueArrayList<Object> attachedData = new UniqueArrayList<>();

    /**
     * Add data to this widget. You can check if a widget has the data by using {@link DynamicWidget#has(Object)}.
     *
     * @param data A data {@link Object} to attach to this widget.
     */
    @PublicAPI
    public Builder attach(Object... data)
    {
        this.attachedData.addAll(List.of(data));

        return this.self();
    }

    /**
     * Remove data from this widget.
     *
     * @param data The data {@link Object} to remove from this widget.
     */
    @PublicAPI
    public Builder detach(Object... data)
    {
        this.attachedData.removeAll(List.of(data));

        return this.self();
    }

    /**
     * Perform specialized instructions after the widget has synced with its builder.
     *
     * @param updater A {@link Consumer} that accepts the built {@link Widget}.
     */
    @PublicAPI
    public Builder afterSync(Consumer<Widget> updater)
    {
        this.afterSync = updater;

        return this.self();
    }

    /**
     * Perform specialized instructions after the widget has applied builder updates.
     *
     * @param updater A {@link Runnable} to run.
     */
    @PublicAPI
    public Builder afterSync(Runnable updater)
    {
        return this.afterSync(widget -> updater.run());
    }

    /**
     * Perform specialized instructions before the widget syncs with its builder.
     *
     * @param updater A {@link Consumer} that accepts the built {@link Widget}.
     */
    @PublicAPI
    public Builder beforeSync(Consumer<Widget> updater)
    {
        this.beforeSync = updater;

        return this.self();
    }

    /**
     * Perform specialized instructions before the widget syncs with its builder.
     *
     * @param updater A {@link Consumer} that accepts the built {@link Widget}.
     */
    @PublicAPI
    public Builder beforeSync(Runnable updater)
    {
        return this.beforeSync(widget -> updater.run());
    }

    /**
     * Consider this widget as hovered or focused when any of the given widgets are also hovered or focused.
     *
     * @param syncTo A varargs list of {@link DynamicWidget} to sync to.
     */
    @PublicAPI
    public Builder hoverOrFocusSync(DynamicWidget<?, ?>... syncTo)
    {
        this.hoverSync.addAll(List.of(syncTo));

        return this.self();
    }

    /**
     * Change when this widget is rendered by defining its render pass level.
     *
     * @param pass A {@link RenderPass} enumeration value.
     */
    @PublicAPI
    public Builder renderWhen(RenderPass pass)
    {
        this.renderPass = pass;

        return this.self();
    }

    /**
     * Define a dynamic {@link BooleanSupplier} that indicates when a widget can be focused. This will
     * <b color=red>override</b> default widget focusing logic.
     *
     * @param when A {@link BooleanSupplier} instance.
     */
    @PublicAPI
    public Builder focusWhen(BooleanSupplier when)
    {
        this.canFocus = when;

        return this.self();
    }

    /**
     * Define instructions to run when the built widget's {@code focused} flag is changed to {@code true}.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Widget}.
     */
    @PublicAPI
    public Builder whenFocused(Consumer<Widget> consumer)
    {
        this.whenFocused = consumer;

        return this.self();
    }

    /**
     * Define instructions to run when the built widget's {@code focused} flag is changed to {@code true}.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    public Builder whenFocused(Runnable runnable)
    {
        return this.whenFocused(widget -> runnable.run());
    }

    /**
     * Set the widget as not focusable.
     */
    @PublicAPI
    public Builder cannotFocus()
    {
        this.canFocus = BooleanSupplier.NEVER;

        return this.self();
    }

    /**
     * This will prevent the widget from being focused after it has been clicked on by the mouse.
     */
    @PublicAPI
    public Builder skipFocusOnClick()
    {
        this.focusOnClick = false;

        return this.self();
    }

    /**
     * Define the tab order group for this widget. The game categorizes groups of widgets by their tab order group
     * number. Groups of widgets with lower group numbers are selected first. The order within the group is defined by
     * when the widget was subscribed to the screen. Any widget that does not define their tab order group number is
     * automatically assigned to group {@code 0}.
     *
     * @param tabOrderGroup A tab group order number.
     */
    @PublicAPI
    public Builder tabOrderGroup(int tabOrderGroup)
    {
        this.tabOrderGroup = tabOrderGroup;

        return this.self();
    }

    /* Builder Layout */

    @Nullable protected ToIntFunction<Widget> x = null;
    @Nullable protected ToIntFunction<Widget> y = null;
    @Nullable protected ToIntFunction<Widget> width = null;
    @Nullable protected ToIntFunction<Widget> height = null;
    @Nullable protected ToFloatFunction<Widget> scaleWidth = null;
    @Nullable protected ToFloatFunction<Widget> scaleHeight = null;
    @Nullable protected RelativeLayout relativeLayout = null;
    protected final UniqueArrayList<DynamicWidget<?, ?>> hoverSync = new UniqueArrayList<>();
    protected final UniqueArrayList<DynamicWidget<?, ?>> followers = new UniqueArrayList<>();
    protected boolean forceRelativeX = false;
    protected boolean forceRelativeY = false;
    protected boolean relativeAnchor = false;
    protected int relativeX = 0;
    protected int relativeY = 0;
    protected int defaultX = 0;
    protected int defaultY = 0;
    protected int defaultWidth = 20;
    protected int defaultHeight = 20;

    /**
     * @return A width scaling amount.
     */
    protected float getScaleWidth(Widget widget)
    {
        return NullableResult.getOrElse(this.scaleWidth, 1.0F, resizer -> resizer.applyAsFloat(widget));
    }

    /**
     * @return A height scaling amount.
     */
    protected float getScaleHeight(Widget widget)
    {
        return NullableResult.getOrElse(this.scaleHeight, 1.0F, resizer -> resizer.applyAsFloat(widget));
    }

    /* Builder Icons */

    protected Supplier<TextureIcon> iconSupplier = () -> TextureIcon.EMPTY;
    protected Supplier<TextureIcon> hoverIconSupplier = () -> TextureIcon.EMPTY;
    protected Supplier<TextureIcon> disabledIconSupplier = () -> TextureIcon.EMPTY;
    protected int iconWidth = 0;
    protected int iconHeight = 0;
    protected int hoverIconWidth = 0;
    protected int hoverIconHeight = 0;
    protected int disabledIconWidth = 0;
    protected int disabledIconHeight = 0;
    protected boolean brightenOnHover = false;
    protected boolean darkenOnDisable = true;
    protected FloatSupplier brightenAmount = () -> this.iconSupplier.get().getMaxBrightenAmount();
    protected FloatSupplier darkenAmount = () -> 0.4F;

    /* Builder Tooltips */

    @Nullable protected Supplier<List<Component>> multilineTooltip = null;
    @Nullable protected Supplier<List<Component>> multilineInfoTooltip = null;
    @Nullable protected Supplier<List<Component>> disabledMultilineTooltip = null;
    @Nullable protected Supplier<List<Component>> disabledMultilineInfoTooltip = null;
    @Nullable protected Supplier<Component> tooltip = null;
    @Nullable protected Supplier<Component> infoTooltip = null;
    @Nullable protected Supplier<Component> disabledTooltip = null;
    @Nullable protected Supplier<Component> disabledInfoTooltip = null;
    @Nullable protected FlagTimer disabledTooltipTimer = null;
    @Nullable protected FlagTimer tooltipTimer = null;
    @Nullable protected FlagTimer hideTimer = null;

    /* Builder Active/Visible */

    @Nullable protected Predicate<Widget> active = null;
    @Nullable protected Predicate<Widget> visible = null;
    @Nullable protected Consumer<Widget> onActiveChange = null;
    @Nullable protected Consumer<Widget> onVisibleChange = null;
    private boolean lastActive = true;
    private boolean lastVisible = false;

    /* Builder Functions */

    private final HashSet<DynamicField> managing = new HashSet<>();
    private final LinkedHashMap<Class<?>, DynamicFunction<Builder, Widget>> lowFunctions = new LinkedHashMap<>();
    private final LinkedHashMap<Class<?>, DynamicFunction<Builder, Widget>> highFunctions = new LinkedHashMap<>();
    private final LinkedHashMap<Class<?>, DynamicFunction<Builder, Widget>> layouts = new LinkedHashMap<>();
    private boolean hasInitialized = false;
    protected boolean isProcessingLayout = false;

    /**
     * Check if this dynamic builder is managing any of the given dynamic fields.
     *
     * @param fields A varargs of {@link DynamicField} to check.
     * @return Whether this builder is managing a given field(s).
     */
    public boolean isManaging(DynamicField... fields)
    {
        for (DynamicField field : fields)
        {
            boolean isManaged = switch (field)
            {
                case X -> this.x != null;
                case Y -> this.y != null;
                case WIDTH -> this.width != null;
                case HEIGHT -> this.height != null;
                case ACTIVE -> this.active != null;
                case VISIBLE -> this.visible != null;
            };

            if (isManaged || this.managing.contains(field))
                return true;
        }

        return false;
    }

    /**
     * Check if this dynamic builder is <b>not</b> managing any of the given dynamic fields.
     *
     * @param fields A varargs of {@link DynamicField} to check.
     * @return Whether this builder is not managing any of the given field(s).
     */
    public boolean isNotManaging(DynamicField... fields)
    {
        return !this.isManaging(fields);
    }

    /**
     * Add a dynamic function to this builder.
     *
     * @param function A {@link DynamicFunction} instance.
     */
    public Builder addFunction(DynamicFunction<Builder, Widget> function)
    {
        this.managing.addAll(function.getManaging(this.self()));

        if (function instanceof DynamicLayout)
            this.layouts.put(function.getClass(), function);
        else
        {
            if (function.priority() == DynamicPriority.LOW)
                this.lowFunctions.put(function.getClass(), function);
            else
                this.highFunctions.put(function.getClass(), function);
        }

        return this.self();
    }

    /**
     * Clears all {@link DynamicLayout} functions stored in this builder and resets x, y, width, and height suppliers.
     * The builder's {@link RelativeLayout} is removed and any followers following this builder is removed.
     */
    public void resetLayout()
    {
        this.x = null;
        this.y = null;
        this.width = null;
        this.height = null;
        this.scaleWidth = null;
        this.scaleHeight = null;
        this.relativeLayout = null;

        this.managing.clear();
        this.followers.clear();
        this.layouts.clear();

        this.lowFunctions.values().forEach(function -> this.managing.addAll(function.getManaging(this.self())));
        this.highFunctions.values().forEach(function -> this.managing.addAll(function.getManaging(this.self())));
    }

    /**
     * Initialize layout and functional data onto a newly built {@link Widget}.
     *
     * @param widget A {@link Widget} instance.
     */
    protected void init(Widget widget)
    {
        // Cache built widget
        this.widget.set(widget);

        // Set default layout
        widget.setX(this.defaultX);
        widget.setY(this.defaultY);
        widget.setWidth(this.defaultWidth);
        widget.setHeight(this.defaultHeight);

        // Set the last known widget state
        this.lastActive = widget.active;
        this.lastVisible = widget.visible;

        // Apply dimensional fields from suppliers
        this.syncDimensions(widget);

        // Apply low-priority functions
        this.lowFunctions.forEach((classType, function) -> function.apply(widget));

        // Apply layout functions
        this.syncLayout(widget);

        // Apply high-priority functions
        this.highFunctions.forEach((classType, function) -> function.apply(widget));

        // Update followers
        DynamicWidget.syncWithoutCache(this.followers);

        // Set relative position
        this.relativeX = widget.x;
        this.relativeY = widget.y;

        this.hasInitialized = true;
    }

    /**
     * Find a {@link DynamicLayout} function in the layout map, if it is present, and execute an update on the found
     * function.
     *
     * @param layout A {@link DynamicLayout} class.
     * @param widget A {@link Widget} instance.
     */
    private void findAndExecute(Class<? extends DynamicLayout> layout, Widget widget)
    {
        DynamicFunction<Builder, Widget> function = this.layouts.get(layout);

        if (function != null)
        {
            if (this.hasInitialized)
                function.ifReapplyThenApply(widget);
            else
                function.apply(widget);
        }
    }

    /**
     * @return The x-coordinate of this builder's {@link RelativeLayout} if present. Otherwise, {@code zero}.
     */
    public int getRelativeLayoutX()
    {
        Widget widget = this.widget.get();
        RelativeLayout layout = this.relativeLayout;

        if (widget == null || this.relativeLayout == null)
            return 0;

        return this.relativeAnchor ? layout.getAnchoredX(widget) : layout.getRelativeX(widget);
    }

    /**
     * @return The y-coordinate of this builder's {@link RelativeLayout} if present. Otherwise, {@code zero}.
     */
    public int getRelativeLayoutY()
    {
        Widget widget = this.widget.get();
        RelativeLayout layout = this.relativeLayout;

        if (widget == null || this.relativeLayout == null)
            return 0;

        return this.relativeAnchor ? layout.getAnchoredY(widget) : layout.getRelativeY(widget);
    }

    /**
     * Sync the relative position of the built widget so that it remains relative to the assigned layout container.
     */
    public void relativeSync()
    {
        if (this.relativeLayout == null)
            return;

        Widget widget = this.widget.get();

        if (widget == null)
            return;

        int dx = this.getRelativeLayoutX() + this.relativeX;
        int dy = this.getRelativeLayoutY() + this.relativeY;

        if (this.forceRelativeX || this.isNotManaging(DynamicField.X))
            widget.setX(dx);

        if (this.forceRelativeY || this.isNotManaging(DynamicField.Y))
            widget.setY(dy);
    }

    /**
     * Syncs this builder's built widget using the functions that were defined during the building phase or
     * post-building.
     */
    public void sync()
    {
        Widget widget = this.widget.get();

        if (widget == null)
            return;

        this.isProcessingLayout = true;

        this.syncDimensions(widget);
        this.lowFunctions.forEach((classType, function) -> function.ifReapplyThenApply(widget));
        this.syncLayout(widget);

        this.highFunctions.forEach((classType, function) -> function.ifReapplyThenApply(widget));
        this.syncLastState(widget);

        DynamicWidget.syncWithoutCache(this.followers);

        this.isProcessingLayout = false;
    }

    /**
     * Perform pre-sync instructions if any are available.
     */
    protected void preSync()
    {
        if (this.beforeSync != null && this.widget.isPresent())
            this.beforeSync.accept(this.widget.get());
    }

    /**
     * Perform post-sync instructions if any are available.
     */
    protected void postSync()
    {
        if (this.afterSync != null && this.widget.isPresent())
            this.afterSync.accept(this.widget.get());
    }

    /**
     * Syncs state data and performs update functions as needed.
     */
    private void syncLastState(Widget widget)
    {
        if (this.onActiveChange != null && this.lastActive != widget.active)
            this.onActiveChange.accept(widget);

        if (this.onVisibleChange != null && this.lastVisible != widget.visible)
            this.onVisibleChange.accept(widget);

        this.lastActive = widget.active;
        this.lastVisible = widget.visible;
    }

    /**
     * Syncs dimensional widget fields from this builder's suppliers if any are present.
     */
    private void syncDimensions(Widget widget)
    {
        NullableAction.attempt(this.x, function -> widget.setX(function.applyAsInt(widget)));
        NullableAction.attempt(this.y, function -> widget.setY(function.applyAsInt(widget)));
        NullableAction.attempt(this.width, function -> widget.setWidth(function.applyAsInt(widget)));
        NullableAction.attempt(this.height, function -> widget.setHeight(function.applyAsInt(widget)));
    }

    /**
     * Syncs layout field data in a certain order and syncs state data between this builder and the built widget.
     */
    private void syncLayout(Widget widget)
    {
        this.findAndExecute(DynamicLayout.XPos.FromScreenEnd.class, widget);
        this.findAndExecute(DynamicLayout.XPos.FromWidgetEnd.class, widget);
        this.findAndExecute(DynamicLayout.XYPos.LeftOf.class, widget);
        this.findAndExecute(DynamicLayout.XYPos.RightOf.class, widget);
        this.findAndExecute(DynamicLayout.XPos.AlignFlush.class, widget);
        this.findAndExecute(DynamicLayout.YPos.AlignVertical.class, widget);
        this.findAndExecute(DynamicLayout.YPos.Above.class, widget);
        this.findAndExecute(DynamicLayout.YPos.Below.class, widget);
        this.findAndExecute(DynamicLayout.YPos.BelowAll.class, widget);
        this.findAndExecute(DynamicLayout.Width.ExtendToWidgetStart.class, widget);
        this.findAndExecute(DynamicLayout.Width.ExtendToWidgetEnd.class, widget);
        this.findAndExecute(DynamicLayout.Height.ExtendToWidgetStart.class, widget);
        this.findAndExecute(DynamicLayout.Height.ExtendToWidgetEnd.class, widget);
        this.findAndExecute(DynamicLayout.Width.OfWidget.class, widget);
        this.findAndExecute(DynamicLayout.Width.OfScreen.class, widget);
        this.findAndExecute(DynamicLayout.Height.OfWidget.class, widget);
        this.findAndExecute(DynamicLayout.Height.OfScreen.class, widget);
        this.findAndExecute(DynamicLayout.Width.ExtendToScreen.class, widget);
        this.findAndExecute(DynamicLayout.Width.ExtendToLargestEnd.class, widget);
        this.findAndExecute(DynamicLayout.Height.ExtendToScreen.class, widget);
        this.findAndExecute(DynamicLayout.Height.ExtendToLargestEnd.class, widget);
        this.findAndExecute(DynamicLayout.YPos.FromScreenEnd.class, widget);
        this.findAndExecute(DynamicLayout.YPos.FromWidgetEnd.class, widget);
        this.findAndExecute(DynamicLayout.XPos.CenterInScreen.class, widget);
        this.findAndExecute(DynamicLayout.YPos.CenterInScreen.class, widget);
        this.findAndExecute(DynamicLayout.XPos.CenterInWidget.class, widget);
        this.findAndExecute(DynamicLayout.YPos.CenterInWidget.class, widget);

        this.syncDimensions(widget);

        NullableAction.attempt(this.active, predicate -> widget.setActive(predicate.test(widget)));
        NullableAction.attempt(this.visible, predicate -> widget.setVisible(predicate.test(widget)));
    }
}
