package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.add;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing.FilterOverlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.input.GenericInput;
import mod.adrenix.nostalgic.client.gui.widget.list.Row;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.tweak.listing.ItemListing;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
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
import mod.adrenix.nostalgic.util.common.text.TextWrap;
import mod.adrenix.nostalgic.util.common.world.ItemFilter;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemOverlay<V, L extends ItemListing<V, L>>
{
    /* Fields */

    final RowList rowList;
    final IconWidget close;
    final TextWidget title;
    final GenericInput search;
    final ButtonWidget abacus;
    final SeparatorWidget top;
    final SeparatorWidget bottom;
    final IntegerHolder tabOrder;

    private final ItemListing<V, L> listing;
    private final HashSet<ItemRule> rules;
    private final Overlay overlay;
    private final FilterOverlay filter;
    private final GenericDatabase<ItemStack> database;
    private final UniqueArrayList<ItemStack> items;
    private final HashMap<Item, TextureIcon> icons;
    private final Consumer<ItemStack> onItemAdd;
    private final Runnable onEmptyAdd;
    private String lastQuery = "";
    private ItemStack selected = null;
    private List<ItemStack> found = new ArrayList<>();

    /* Constructor */

    public ItemOverlay(ItemListing<V, L> listing, Runnable onEmptyAdd, Consumer<ItemStack> onItemAdd)
    {
        this.listing = listing;
        this.rules = listing.rules();
        this.onEmptyAdd = onEmptyAdd;
        this.onItemAdd = onItemAdd;

        this.database = new GenericDatabase<>();
        this.items = new UniqueArrayList<>();
        this.icons = new HashMap<>();
        this.tabOrder = IntegerHolder.create(0);

        this.overlay = Overlay.create()
            .gradientBackground(Gradient.vertical(Color.RICH_BLACK.fromAlpha(96), Color.DARK_BLUE.fromAlpha(160)))
            .outlineColor(Color.WHITE)
            .onResize(this::onResize)
            .onClose(this::close)
            .resizeUsingPercentage(0.7D)
            .padding(2)
            .borderless()
            .build();

        this.close = IconTemplate.close()
            .fromScreenEndX(0)
            .tabOrderGroup(this.tabOrder.getAndIncrement())
            .onPress(this.overlay::close)
            .build(this.overlay::addWidget);

        this.title = TextWidget.create(Lang.Listing.ADD)
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
            .disableIf(listing::hasItemRule)
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

        this.top = SeparatorWidget.create(Color.SILVER_CHALICE)
            .below(this.title, 1)
            .height(1)
            .extendWidthToScreenEnd(0)
            .build(this.overlay::addWidget);

        this.bottom = SeparatorWidget.create(Color.SILVER_CHALICE)
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
    }

    /**
     * Instructions for when the overlay is closed.
     */
    private void close()
    {
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
     * This is needed since the filter button relies on the filter overlay and vice-versa. The filter button's on-press
     * runnable will reference this optional to prevent a NPE.
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
        else if (this.rules.contains(ItemRule.ONLY_DAMAGEABLE))
            this.items.addAll(items.filter(Item::canBeDepleted).map(Item::getDefaultInstance).toList());
        else
        {
            items.forEach(item -> {
                ItemStack itemStack = item.getDefaultInstance();

                boolean areToolsFiltered = ItemFilter.isToolLike(item) && this.rules.contains(ItemRule.NO_TOOLS);
                boolean areItemsFiltered = ItemFilter.isItemLike(item) && this.rules.contains(ItemRule.NO_ITEMS);
                boolean areBlocksFiltered = ItemFilter.isBlockLike(item) && this.rules.contains(ItemRule.NO_BLOCKS);
                boolean areEdiblesFiltered = item.isEdible() && this.rules.contains(ItemRule.NO_EDIBLES);
                boolean areDamageableFiltered = item.canBeDepleted() && this.rules.contains(ItemRule.INVINCIBLE);
                boolean isFiltered = areToolsFiltered || areItemsFiltered || areBlocksFiltered || areEdiblesFiltered || areDamageableFiltered;

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
     * Builds all the {@link Square}s needed for the row list.
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
            Square square = new Square(i, builder -> builder.rightOf(row.get().getWidgets().getLast(), 2));

            row.get().addWidget(square.getButton());

            if (isLastSquare || squaresPerRow == row.get().getWidgets().size())
            {
                this.rowList.addBottomRow(row.get());

                if (!isLastSquare)
                    row.set(Row.create(this.rowList).build());
            }
        }
    }

    /**
     * This class acts as a placeholder for items in the heavily populated row list. Having squares with indexes that
     * point to an item in the item list (or found items list) is <i>a lot</i> faster than rebuilding the row list each
     * time a character is added or removed from the search query.
     */
    private class Square
    {
        /* Fields */

        private final int index;
        private final Consumer<ButtonBuilder> extraSteps;

        /* Constructor */

        public Square(int index, Consumer<ButtonBuilder> extraSteps)
        {
            this.index = index;
            this.extraSteps = extraSteps;
        }

        /* Methods */

        /**
         * @return A shortcut for retrieve the parent {@link ItemOverlay} super class.
         */
        private ItemOverlay<V, L> parent()
        {
            return ItemOverlay.this;
        }

        /**
         * @return Gets an {@link ItemStack} from either the {@code found} items list or the pre-populated {@code item}
         * items list.
         */
        private ItemStack getItemStack()
        {
            if (this.parent().found.size() > this.index)
                return this.parent().found.get(this.index);

            return this.parent().items.get(this.index);
        }

        /**
         * Instructions to perform when this square is pressed.
         */
        private void onPress()
        {
            this.parent().selected = this.getItemStack();
            this.parent().overlay.close();
        }

        /**
         * @return Gets a {@link TextureIcon} that matches the {@link ItemStack} this square is pointing to.
         */
        private TextureIcon getIcon()
        {
            return this.parent().icons.get(this.getItemStack().getItem());
        }

        /**
         * @return Provides a {@link List} of {@link Component}s from the {@link ItemStack} this square is pointing to.
         */
        private List<Component> getListTooltip()
        {
            try
            {
                if (this.isInList())
                {
                    Component tooltip = Lang.Listing.ALREADY_ADDED.get(this.getItemStack().getHoverName().getString());
                    return TextWrap.tooltip(tooltip, 40);
                }

                return this.getItemStack().getTooltipLines(null, TooltipFlag.Default.NORMAL);
            }
            catch (Throwable throwable)
            {
                return List.of(Component.literal(ItemUtil.getResourceKey(this.getItemStack())));
            }
        }

        /**
         * @return Whether the {@link ItemStack} this square is pointing to is already in the listing.
         */
        private boolean isInList()
        {
            return this.parent().listing.containsKey(ItemUtil.getResourceKey(this.getItemStack()));
        }

        /**
         * Checks if the {@link ItemStack} this square is pointing to wasn't found in the {@code found} items list.
         *
         * @return Whether the {@link ButtonWidget} is invisible.
         */
        private boolean isInvisible()
        {
            return !this.parent().found.isEmpty() && this.parent().found.size() <= this.index;
        }

        /**
         * Renderer helper method for rendering the contents of this square using the {@link ButtonWidget}.
         */
        private void render(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            Color color = this.isInList() ? Color.RED.fromAlpha(85) : Color.WHITE.fromAlpha(85);

            boolean isMouseOver = this.parent().rowList.isMouseOver(mouseX, mouseY) && button.isMouseOver(mouseX, mouseY);
            boolean isFocused = button.isFocused();
            boolean isHoveredOrFocused = isMouseOver || isFocused;

            if (isHoveredOrFocused)
                RenderUtil.fill(graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), color);

            int startY = button.getIconY() - (isHoveredOrFocused ? 1 : 0);

            button.getIconManager().pos(button.getIconX(), startY);
            button.getIconManager().render(graphics, mouseX, mouseY, partialTick);

            if (isHoveredOrFocused)
                button.getIconManager().get().setY(startY + 1);
        }

        /**
         * @return A {@link ButtonWidget} that will be used for alignment within a row and display the correct
         * information about the {@link ItemStack} this square is pointing to.
         */
        public ButtonWidget getButton()
        {
            ButtonBuilder builder = ButtonWidget.create()
                .darkenOnDisable(0.5F)
                .onPress(this::onPress)
                .icon(this::getIcon)
                .listTooltip(this::getListTooltip)
                .invisibleIf(this::isInvisible)
                .renderer(this::render);

            this.extraSteps.accept(builder);

            return builder.build();
        }
    }
}
