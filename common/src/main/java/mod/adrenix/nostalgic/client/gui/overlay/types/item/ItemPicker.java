package mod.adrenix.nostalgic.client.gui.overlay.types.item;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.FilterOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import mod.adrenix.nostalgic.util.common.world.ItemFilter;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemPicker
{
    /* Builder */

    /**
     * Create a new {@link ItemPicker} using only an item consumer and optional selection rules.
     *
     * @param onItemAdd A {@link Consumer} that accepts an {@link ItemStack} if one was chosen.
     * @param rules     A varargs list of {@link ItemRule}.
     * @return A {@link ItemizeMaker} for more building options.
     * @see #create(Consumer, Runnable, ItemRule...)
     */
    public static ItemizeMaker create(Consumer<ItemStack> onItemAdd, ItemRule... rules)
    {
        return new ItemizeMaker(onItemAdd, () -> { }, new HashSet<>(List.of(rules)));
    }

    /**
     * Create a new {@link ItemPicker} using an item consumer, a runnable instance if no item is chosen, and optional
     * selection rules.
     *
     * @param onItemAdd  A {@link Consumer} that accepts an {@link ItemStack} if one was chosen.
     * @param onEmptyAdd A {@link Runnable} that will run if no {@link ItemStack} is chosen.
     * @param rules      A varargs list of {@link ItemRule}.
     * @return A {@link ItemizeMaker} for more building options.
     * @see #create(Consumer, ItemRule...)
     */
    public static ItemizeMaker create(Consumer<ItemStack> onItemAdd, Runnable onEmptyAdd, ItemRule... rules)
    {
        return new ItemizeMaker(onItemAdd, onEmptyAdd, new HashSet<>(List.of(rules)));
    }

    /* Fields */

    final RowList rowList;
    final IconWidget close;
    final TextWidget title;
    final GenericInput search;
    final ButtonWidget abacus;
    final SeparatorWidget top;
    final SeparatorWidget bottom;
    final IntegerHolder tabOrder;

    final ItemizeMaker builder;
    final HashSet<ItemRule> rules;
    final Overlay overlay;
    final FilterOverlay filter;
    final GenericDatabase<ItemStack> database;
    final UniqueArrayList<ItemStack> items;
    final HashMap<Item, TextureIcon> icons;
    final Consumer<ItemStack> onItemAdd;
    final Runnable onEmptyAdd;

    double lastScrollAmount = -1;
    String lastQuery = "";
    ItemStack selected = null;
    List<ItemStack> found = new ArrayList<>();

    /* Constructor */

    protected ItemPicker(ItemizeMaker builder)
    {
        this.builder = builder;
        this.rules = builder.rules;
        this.onEmptyAdd = builder.onEmptyAdd;
        this.onItemAdd = builder.onItemAdd;

        this.database = new GenericDatabase<>();
        this.items = new UniqueArrayList<>();
        this.icons = new HashMap<>();
        this.tabOrder = IntegerHolder.create(0);

        this.overlay = Overlay.create()
            .outlineColor(builder.borderColor != null ? builder.borderColor : Color.WHITE)
            .onResize(this::onResize)
            .onClose(this::close)
            .resizeUsingPercentage(0.7D)
            .padding(2)
            .borderless()
            .build();

        if (builder.backgroundGradient == null && builder.backgroundColor == null)
            this.overlay.getBuilder()
                .gradientBackground(Gradient.vertical(Color.RICH_BLACK.fromAlpha(96), Color.DARK_BLUE.fromAlpha(160)));
        else if (builder.backgroundGradient != null)
            this.overlay.getBuilder().gradientBackground(builder.backgroundGradient);
        else
            this.overlay.getBuilder().backgroundColor(builder.backgroundColor);

        this.close = IconTemplate.close()
            .fromScreenEndX(0)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .onPress(this.overlay::close)
            .build(this.overlay::addWidget);

        this.title = TextWidget.create(builder.title != null ? builder.title : Lang.Picker.ITEM::get)
            .tooltip(Lang.Overlay.DRAG_TIP, 36, 1L, TimeUnit.SECONDS)
            .onPress(this.overlay::move, Color.LEMON_YELLOW)
            .intersection(this.close)
            .extendWidthToScreenEnd(0)
            .disableUnderline()
            .centerAligned()
            .cannotFocus()
            .shorten()
            .build(this.overlay::addWidget);

        this.abacus = ButtonWidget.create(Lang.Button.FILTER)
            .onPress(() -> this.getFilter().ifPresent(FilterOverlay::open))
            .enableIf(this.rules::isEmpty)
            .skipFocusOnClick()
            .fromScreenEndY(0)
            .useTextWidth()
            .padding(5)
            .icon(Icons.FILTER)
            .tooltip(Lang.Button.FILTER, 500L, TimeUnit.MILLISECONDS)
            .disabledTooltip(Lang.Tooltip.FILTER_DISABLED, 45, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Tooltip.FILTER, 45)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .build(this.overlay::addWidget);

        this.filter = new FilterOverlay(this.abacus, this::filter);

        this.top = SeparatorWidget.create(builder.separatorColor != null ? builder.separatorColor : Color.SILVER_CHALICE)
            .below(this.title, 1)
            .height(1)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.bottom = SeparatorWidget.create(builder.separatorColor != null ? builder.separatorColor : Color.SILVER_CHALICE)
            .above(this.abacus, 1)
            .height(1)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.search = GenericInput.create()
            .onInput(this::find)
            .icon(Icons.SEARCH)
            .whenEmpty(Lang.Input.SEARCH)
            .background(Color.OLIVE_BLACK, Color.OLIVE_BLACK)
            .border(Color.BLACK, Color.WHITE)
            .maxLength(100)
            .searchShortcut()
            .rightOf(this.abacus, 1)
            .extendWidthToScreenEnd(0)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .build(this.overlay::addWidget);

        this.rowList = RowList.create()
            .below(this.top, 0)
            .extendWidthToScreenEnd(0)
            .extendHeightTo(this.bottom, 0)
            .horizontalMargin(0)
            .verticalMargin(2)
            .emptyMessage(Lang.Listing.NOTHING_FOUND)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .useScissorRectangle(this::getListScissoringBounds)
            .build(this.overlay::addWidget);

        this.populateItemList();
    }

    /* Methods */

    /**
     * Open the add new value to list overlay.
     */
    public void open()
    {
        this.overlay.open();

        if (this.lastScrollAmount > 0.0D)
            this.rowList.setScrollAmount(this.lastScrollAmount);
    }

    /**
     * Instructions for when the overlay is closed.
     */
    private void close()
    {
        this.lastScrollAmount = this.rowList.getScrollAmount();

        this.close.setUnfocused();

        if (this.selected != null)
            this.onItemAdd.accept(this.selected);
        else
            this.onEmptyAdd.run();
    }

    /**
     * Instructions to perform when the overlay window resizes.
     */
    private void onResize()
    {
        this.populateSquaresForList();
        this.find(this.search.getInput());
    }

    /**
     * Filter out rows and item widgets that are not related to the given search query.
     *
     * @param query A string to search for overlay items.
     */
    private void find(String query)
    {
        if (query.equals(this.lastQuery))
            return;
        else
            this.lastQuery = query;

        this.found.clear();

        if (!query.isEmpty())
            this.found = this.database.findValues(query);
    }

    /**
     * Filter out items based on manual filtering options.
     */
    private void filter()
    {
        this.found.clear();

        if (!this.lastQuery.isEmpty())
            this.found = this.database.findValues(this.lastQuery);

        if (this.found.isEmpty())
            this.found = CollectionUtil.filterOut(this.items, this.filter::isFiltered).collect(Collectors.toList());
        else
            this.found = CollectionUtil.filterOut(this.found, this.filter::isFiltered).collect(Collectors.toList());
    }

    /**
     * This is necessary since the filter button relies on the filter overlay and vice versa. The filter button's
     * on-press runnable will reference this optional to prevent a NPE.
     *
     * @return An {@link Optional} {@link FilterOverlay}.
     */
    private Optional<FilterOverlay> getFilter()
    {
        return Optional.ofNullable(this.filter);
    }

    /**
     * Create a OpenGL scissoring bound using a {@link Rectangle}.
     *
     * @return The scissoring rectangle for the row list.
     */
    private Rectangle getListScissoringBounds()
    {
        int startX = this.overlay.getInsideX();
        int startY = this.top.getY() + 2;
        int endX = this.overlay.getInsideEndX();
        int endY = this.bottom.getY() - 2;

        return new Rectangle(startX, startY, endX, endY);
    }

    /**
     * Fills the {@link Item} list with items from the vanilla {@link BuiltInRegistries}.
     */
    private void populateItemList()
    {
        Stream<Item> items = CollectionUtil.filterOut(BuiltInRegistries.ITEM.stream(), ItemFilter::isUnique);

        if (this.rules.contains(ItemRule.NONE))
            this.items.addAll(items.map(Item::getDefaultInstance).toList());
        else if (this.rules.contains(ItemRule.ONLY_TOOLS))
            this.items.addAll(items.filter(ItemFilter::isToolLike).map(Item::getDefaultInstance).toList());
        else if (this.rules.contains(ItemRule.ONLY_ITEMS))
            this.items.addAll(items.filter(ItemFilter::isItemLike).map(Item::getDefaultInstance).toList());
        else if (this.rules.contains(ItemRule.ONLY_BLOCKS))
            this.items.addAll(items.filter(ItemFilter::isBlockLike).map(Item::getDefaultInstance).toList());
        else if (this.rules.contains(ItemRule.ONLY_CHESTS))
            this.items.addAll(items.filter(ItemFilter::isChestLike).map(Item::getDefaultInstance).toList());
        else if (this.rules.contains(ItemRule.ONLY_EDIBLES))
            this.items.addAll(items.filter(Item::isEdible).map(Item::getDefaultInstance).toList());
        else
        {
            items.forEach(item -> {
                ItemStack itemStack = item.getDefaultInstance();

                boolean areToolsFiltered = ItemFilter.isToolLike(item) && this.rules.contains(ItemRule.NO_TOOLS);
                boolean areItemsFiltered = ItemFilter.isItemLike(item) && this.rules.contains(ItemRule.NO_ITEMS);
                boolean areBlocksFiltered = ItemFilter.isBlockLike(item) && this.rules.contains(ItemRule.NO_BLOCKS);
                boolean areEdiblesFiltered = item.isEdible() && this.rules.contains(ItemRule.NO_EDIBLES);
                boolean isFiltered = areToolsFiltered || areItemsFiltered || areBlocksFiltered || areEdiblesFiltered;

                if (!isFiltered)
                    this.items.add(itemStack);
            });
        }

        this.items.forEach(itemStack -> {
            this.database.put(ItemUtil.getLocalizedItem(itemStack), itemStack);
            this.icons.put(itemStack.getItem(), TextureIcon.fromItem(itemStack.getItem()));
        });
    }

    /**
     * Builds all the item squares needed for the row list.
     */
    private void populateSquaresForList()
    {
        int squaresPerRow = Math.round(this.rowList.getRowWidth() / 22.0F);
        int scrollbarSize = this.rowList.getScrollbar().getWidth();
        int rowListWidth = 22 * squaresPerRow + scrollbarSize + 2;

        this.rowList.clear();
        this.rowList.setWidth(rowListWidth);
        this.overlay.resizeForOverflow();

        final AtomicReference<Row> row = new AtomicReference<>(Row.create(this.rowList).build());

        for (int i = 0; i < this.items.size(); i++)
        {
            boolean isLastSquare = i == this.items.size() - 1;

            ItemSquare square = new ItemSquare(this, i, builder -> builder.rightOf(row.get()
                .getWidgets()
                .getLast(), 2));

            row.get().addWidget(square.getButton());

            if (isLastSquare || squaresPerRow == row.get().getWidgets().size())
            {
                this.rowList.addBottomRow(row.get());

                if (!isLastSquare)
                    row.set(Row.create(this.rowList).build());
            }
        }
    }
}
