package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListingWidgets<V, L extends Listing<V, L>>
{
    /* Fields */

    private final Overlay overlay;
    public final RowList rowList;
    public final ButtonWidget add;
    public final ButtonWidget manage;
    public final ButtonWidget undo;
    public final GenericInput search;
    public final ButtonWidget finish;
    public final SeparatorWidget separator;
    public final IntegerHolder tabOrder;
    public final LinkedHashSet<ButtonWidget> shrinkable;
    final ListingOverlay<V, L> listingOverlay;
    final LinkedHashSet<AbstractRow<?, ?>> sorted;
    final GenericDatabase<AbstractRow<?, ?>> database;
    @Nullable AbstractRow<?, ?> highlighted;

    /* Constructor */

    public ListingWidgets(ListingOverlay<V, L> listingOverlay)
    {
        this.listingOverlay = listingOverlay;
        this.overlay = listingOverlay.getOverlay();
        this.sorted = new LinkedHashSet<>();
        this.shrinkable = new LinkedHashSet<>();
        this.database = new GenericDatabase<>();
        this.tabOrder = IntegerHolder.create(0);

        /* Warning Header */

        BlankWidget anchor = BlankWidget.create()
            .size(0)
            .pos(this.overlay::getInsideX, this.overlay::getInsideY)
            .build(this.overlay::addWidget);

        TextWidget disabled = TextWidget.create(Lang.Listing.DISABLED_WARNING)
            .posY(1)
            .widthOfScreen(0.8F)
            .centerInScreenX()
            .centerAligned()
            .icon(Icons.WARNING)
            .visibleIf(this::isListingDisabled)
            .build(this.overlay::addWidget);

        SeparatorWidget.create(Color.SILVER_CHALICE)
            .height(1)
            .below(disabled, 1)
            .extendWidthToScreenEnd(0)
            .visibleIf(this::isListingDisabled)
            .build(this.overlay::addWidget);

        BlankWidget.create().renderer((widget, graphics, mouseX, mouseY, partialTick) -> {
            if (this.isListingDisabled())
            {
                int x0 = this.overlay.getInsideX();
                int y0 = this.overlay.getInsideY();
                int x1 = this.overlay.getInsideEndX();
                int y1 = disabled.getEndY() + 1;

                RenderUtil.fill(graphics, x0, y0, x1, y1, Color.ALERT_RED.fromAlpha(0.3F));
            }
        }).build(this.overlay::addWidget);

        /* Common Widgets */

        this.add = ButtonWidget.create(Lang.Button.ADD)
            .posX(1)
            .fromScreenEndY(1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .icon(Icons.ADD)
            .tooltip(Lang.Button.ADD, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.ADD, 35)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .disableIf(listingOverlay::isLocked)
            .onPress(listingOverlay::onAdd)
            .build(List.of(this.overlay::addWidget, this.shrinkable::add));

        this.separator = SeparatorWidget.create(Color.SILVER_CHALICE)
            .above(this.add, 1)
            .height(1)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.rowList = RowList.create()
            .belowAll(() -> this.isListingDisabled() ? 2 : 0, anchor, disabled)
            .highlight(0.15D, Animate.linear(150L, TimeUnit.MILLISECONDS))
            .emptyMessage(this::getEmptyMessage)
            .extendHeightTo(this.separator, 0)
            .extendWidthToScreenEnd(0)
            .heightOverflowMargin(1)
            .showSelectionBorder()
            .horizontalMargin(2)
            .verticalMargin(2)
            .useSeparators()
            .build(this.overlay::addWidget);

        this.undo = ButtonWidget.create(Lang.Button.UNDO)
            .enableIf(CollectionUtil.areAllTrue(listingOverlay.getTweak()::isCacheUndoable, listingOverlay::isUnlocked))
            .onPress(listingOverlay::onUndo)
            .rightOf(this.add, 1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .icon(Icons.UNDO)
            .hoverIcon(Icons.UNDO_HOVER)
            .tooltip(Lang.Button.UNDO, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.UNDO, 35)
            .build();

        this.finish = ButtonWidget.create(Lang.Vanilla.GUI_DONE)
            .icon(Icons.GREEN_CHECK)
            .onPress(this.overlay::close)
            .fromScreenEndX(1)
            .fromScreenEndY(1)
            .useTextWidth()
            .padding(5)
            .build();

        this.manage = ButtonWidget.create(Lang.Button.MANAGE)
            .onPress(() -> new ManageListOverlay(listingOverlay).open())
            .leftOf(this.finish, 1)
            .skipFocusOnClick()
            .useTextWidth()
            .padding(5)
            .icon(Icons.MECHANICAL_TOOLS)
            .tooltip(Lang.Button.MANAGE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.MANAGE_LISTING, 35)
            .disableIf(listingOverlay::isLocked)
            .build(this.overlay::addWidget);

        this.search = GenericInput.create()
            .onInput(this::find)
            .icon(Icons.SEARCH)
            .whenEmpty(Lang.Input.SEARCH)
            .border(Color.BLACK, Color.WHITE)
            .background(Color.OLIVE_BLACK, Color.OLIVE_BLACK)
            .maxLength(100)
            .searchShortcut()
            .extendWidthTo(this.manage, 1)
            .rightOf(this.undo, 1)
            .afterSync(this::resizeSearch)
            .build();

        listingOverlay.createExtraWidgets(this);
        listingOverlay.setTabOrder(this);

        this.overlay.addWidgets(this.undo, this.finish, this.manage, this.search);
        this.shrinkable.add(this.undo);
    }

    /* Methods */

    /**
     * Check if buttons need resized so that the search widget is not too small.
     */
    private void resizeSearch(GenericInput search)
    {
        if (search.getWidth() >= 120)
            return;

        this.shrinkable.forEach(ButtonWidget::shrink);

        search.getBuilder().sync();
    }

    /**
     * @return Whether the listing is disabled.
     */
    private boolean isListingDisabled()
    {
        return this.listingOverlay.getListing().isDisabled();
    }

    /**
     * Find and highlight a row from the given string.
     *
     * @param string       A string to find within a map's keys or a set's elements.
     * @param smoothScroll Whether to smoothly scroll to the highlighted.
     */
    public void findAndHighlight(String string, boolean smoothScroll)
    {
        AbstractRow<?, ?> row = this.database.getFirstResult(string);

        if (row == null)
            return;
        else
            this.highlighted = row;

        this.rowList.setFocused(row, smoothScroll);
    }

    /**
     * Find and highlight a row from the given string. This will smoothly scroll to the highlighted row. If this is not
     * desired, then use {@link #findAndHighlight(String, boolean)}.
     *
     * @param string A string to find within a map's keys or a set's elements.
     */
    public void findAndHighlight(String string)
    {
        this.findAndHighlight(string, true);
    }

    /**
     * If the row has a focused widget, or is highlighted, then a color will be returned. Otherwise, the transparent
     * color is returned.
     *
     * @param row A {@link AbstractRow} instance.
     * @return A {@link Color} instance.
     */
    public Color getColor(AbstractRow<?, ?> row, String key, BooleanSupplier isDeleted)
    {
        return new Color(() -> {
            if (isDeleted.getAsBoolean())
                return Color.fromFormatting(ChatFormatting.RED);

            if (row.isWidgetFocused())
                return Color.FRENCH_SKY_BLUE;

            if (row == this.highlighted)
                return Color.GREEN_APPLE;

            boolean isAdded = switch (this.listingOverlay.getTweak().getCacheMode())
            {
                case LOCAL -> !this.listingOverlay.getTweak().fromDisk().containsKey(key);
                case NETWORK -> !this.listingOverlay.getTweak().fromServer().containsKey(key);
            };

            return isAdded ? Color.MUGHAL_GREEN : Color.TRANSPARENT;
        });
    }

    /**
     * Filter out rows that are not related to the given search query.
     *
     * @param query A string that filters out unrelated rows and highlights highly relatable ones.
     */
    private void find(String query)
    {
        if (query.isEmpty())
        {
            this.rowList.clear();
            this.rowList.addAll(this.sorted);

            return;
        }

        this.rowList.clear();
        this.rowList.addAll(this.database.findValues(query));
    }

    /**
     * @return An empty message that depends on whether nothing is found or nothing is saved.
     */
    private Component getEmptyMessage()
    {
        if (this.search == null)
            return Component.empty();

        if (this.search.getInput().isEmpty() && this.rowList.getVisibleRows().isEmpty())
            return Lang.Listing.NOTHING_SAVED.get();
        else
            return Lang.Listing.NOTHING_FOUND.get();
    }

    /**
     * Render a highlight outline around a row based on current list context.
     *
     * @param row         The {@link AbstractRow} instance.
     * @param graphics    The {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public void renderOutline(AbstractRow<?, ?> row, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Color color = row.getHighlightColor();

        if (color.isEmpty())
            return;

        RenderUtil.outline(graphics, row.getX(), row.getY(), row.getWidth(), row.getHeight(), color);
    }
}
