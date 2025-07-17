package mod.adrenix.nostalgic.client.gui.screen.config.overlay.listing;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.OverlayBuilder;
import mod.adrenix.nostalgic.client.gui.widget.list.AbstractRow;
import mod.adrenix.nostalgic.client.gui.widget.list.RowMaker;
import mod.adrenix.nostalgic.tweak.factory.TweakListing;
import mod.adrenix.nostalgic.tweak.listing.Listing;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface ListingOverlay<V, L extends Listing<V, L>>
{
    /**
     * @return The {@link TweakListing} instance used by this overlay.
     */
    TweakListing<V, L> getTweak();

    /**
     * @return The {@link ListingWidgets} instance used by this overlay.
     */
    ListingWidgets<V, L> getWidgets();

    /**
     * @return The {@link Overlay} instance used by this overlay.
     */
    Overlay getOverlay();

    /**
     * @return The default overlay builder for listings.
     */
    default OverlayBuilder getDefaultOverlay()
    {
        return Overlay.create(this.getTweak().getTranslation())
            .icon(this.getTweak().getIcon().orElse(Icons.GENERIC_OVERLAY))
            .resizeHeightUsingPercentage(0.9D)
            .resizeWidthUsingPercentage(0.6D, 425)
            .minWidth(380);
    }

    /**
     * @return The {@link Listing} stored in the tweak used by this implementation.
     */
    default Listing<V, L> getListing()
    {
        return this.getTweak().fromCache();
    }

    /**
     * @return Whether the tweak is locked out because the list is in network mode and the player is not an operator.
     */
    default boolean isLocked()
    {
        return this.getTweak().isNetworkLocked();
    }

    /**
     * @return Whether the tweak is unlocked because the list is local or the player is an operator in network mode.
     */
    default boolean isUnlocked()
    {
        return !this.isLocked();
    }

    /**
     * This method is responsible for adding new rows to the overlay's row list based on a map or set list. Another
     * overlay will appear with selectable items.
     */
    void onAdd();

    /**
     * Add a new object to the listing.
     *
     * @param object An {@link Object} instance.
     */
    void onRowAdd(Object object);

    /**
     * The keys must be in string format so that row titles can be localized and searched. The value can be whatever the
     * listing is storing.
     *
     * @return A {@link Collection} of key/value entries that correspond to a listing's map keys or a set's elements in
     * string format.
     */
    Collection<Pair<String, V>> getEntries();

    /**
     * Get a formatted row name based on the given listing key string. If the given string is a resource location, then
     * it will be localized if possible.
     *
     * @param listKey A listing key string.
     * @return A formatting row name or the given listing key.
     */
    String getLocalizedKey(String listKey);

    /**
     * Localize the entries from the listing before the row list is created.
     *
     * @param collection A {@link Collection} of {@link Pair} with the left side of the pair being the listing key and
     *                   the right side of the pair being the key's value.
     * @return A new {@link HashMap} where the keys of the map is the original pair and the values are the localized
     * string.
     */
    HashMap<Pair<String, V>, String> getLocalizedEntries(Collection<Pair<String, V>> collection);

    /**
     * Supply a row maker based on the given key and value.
     *
     * @param key   A key that is within a listing.
     * @param value A value that corresponds to the key, or the key itself if the listing is a set.
     * @return A new {@link RowMaker} instance.
     */
    @Nullable AbstractRow<?, ?> getRow(String key, V value);

    /**
     * Open the listing overlay.
     */
    default void open()
    {
        this.getOverlay().open();
    }

    /**
     * Set the tab order for all widgets. All the default widgets will be built by the time {@link ListingWidgets}
     * invokes this method.
     *
     * @param widgets The default {@link ListingWidgets} that is invoking this method.
     */
    default void setTabOrder(ListingWidgets<V, L> widgets)
    {
        widgets.add.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.undo.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.search.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.manage.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.finish.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
        widgets.rowList.setTabOrderGroup(widgets.tabOrder.getAndIncrement());
    }

    /**
     * If a listing implementation needs to add extra widgets to the overlay, then this is the method to override to
     * perform extra registration. All the default widgets will be built by the time {@link ListingWidgets} invokes this
     * method.
     *
     * @param defaultWidgets The {@link ListingWidgets} that is invoking this method.
     */
    default void createExtraWidgets(ListingWidgets<V, L> defaultWidgets)
    {
    }

    /**
     * Undoes the listing back to the initial state when this overlay was opened.
     */
    default void onUndo()
    {
        this.getWidgets().database.clear();
        this.getWidgets().rowList.clear();

        this.getTweak().sync();
        this.createListRows();
    }

    /**
     * The icon to use for a row based on the given list key. The default icon is empty.
     *
     * @param listKey The row's list key.
     * @return A {@link TextureIcon} for the row.
     */
    default TextureIcon getRowIcon(String listKey)
    {
        return TextureIcon.EMPTY;
    }

    /**
     * Get a {@link Component} that contains the formatting for the row title header.
     *
     * @param listKey A list key string.
     * @return A {@link Component} with the row's name and formatting.
     */
    default Component getRowTitle(final String listKey)
    {
        MutableComponent formatted = Component.literal(this.getLocalizedKey(listKey));
        boolean isAdded = this.isAdded(listKey);

        if (isAdded)
        {
            formatted.append(Component.literal(" ("))
                .append(Lang.Tag.NEW.withStyle(ChatFormatting.GREEN))
                .append(Component.literal(")").withStyle(ChatFormatting.RESET));
        }

        return this.appendToRowTitle(listKey, formatted);
    }

    /**
     * Check if the given list key has been newly added to the listing.
     *
     * @param listKey The list key string.
     * @return Whether the list key is new for the listing.
     */
    private boolean isAdded(String listKey)
    {
        TweakListing<V, L> tweak = this.getTweak();
        boolean isAdded = false;

        if (tweak.fromCache() instanceof Map<?, ?> cache && tweak.fromDisk() instanceof Map<?, ?> disk)
        {
            if (cache.containsKey(listKey) && !disk.containsKey(listKey))
                isAdded = true;
        }

        if (tweak.fromCache() instanceof Set<?> cache && tweak.fromDisk() instanceof Set<?> disk)
        {
            if (cache.contains(listKey) && !disk.contains(listKey))
                isAdded = true;
        }

        return isAdded;
    }

    /**
     * Append additional formatting to a row title before it is used by the row in the row list.
     *
     * @param rowTitle A {@link MutableComponent} instance.
     * @return The formatted {@link Component}.
     */
    default Component appendToRowTitle(final String listKey, MutableComponent rowTitle)
    {
        return rowTitle;
    }

    /**
     * Creates and localizes the default rows for when a listing needs to build rows.
     */
    default void createListRows()
    {
        HashMap<Pair<String, V>, String> localized = this.getLocalizedEntries(this.getEntries());

        this.getWidgets().rowList.clear();
        this.getWidgets().database.clear();

        localized.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(String::compareToIgnoreCase))
            .forEach(this::addRowToBottom);

        this.getWidgets().sorted.clear();
        this.getWidgets().sorted.addAll(this.getWidgets().rowList.getRows());
    }

    /**
     * Functional shortcut for adding a localized map entry to the row list.
     *
     * @param entry A localized {@link Map.Entry} instance.
     */
    private void addRowToBottom(Map.Entry<Pair<String, V>, String> entry)
    {
        String key = entry.getKey().left();
        V value = entry.getKey().right();
        AbstractRow<?, ?> row = this.getRow(key, value);

        if (row != null)
        {
            this.getWidgets().database.put(this.getLocalizedKey(key), row);
            this.getWidgets().rowList.addBottomRow(row);
        }
    }
}
