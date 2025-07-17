package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.lang.Translation;

import java.util.Locale;

/**
 * These rules define how items are added to item-like listings when using the config user interface. These rules are
 * <b>not</b> enforced when data is read from a json file. Runtime code that uses a listing is responsible for ensuring
 * the data its reading is valid for the situation.
 *
 * <ul>
 *  <li>{@code NONE} - Any item within the item registry will be displayed in the item picker overlay (default).</li>
 *  <li>{@code ONLY_TOOLS} - Only tool items will be displayed in the item picker overlay.</li>
 *  <li>{@code ONLY_ITEMS} - Only items that are not tools, blocks, or edibles will be displayed in the item picker overlay.</li>
 *  <li>{@code ONLY_BLOCKS} - Only block items will be displayed in the item picker overlay.</li>
 *  <li>{@code ONLY_EDIBLES} - Only edible food items will be displayed in the item picker overlay.</li>
 *  <li>{@code ONLY_DAMAGEABLE} - Only items that can take damage will be displayed in the item picker overlay.</li>
 *  <li>{@code INVINCIBLE} - Only items that are invincible (<i>cannot take damage</i>) will be displayed in the item picker overlay.</li>
 *  <li>{@code NO_TOOLS} - No tool items will be displayed in the item picker overlay.</li>
 *  <li>{@code NO_ITEMS} - No item-like (<i>opposite to {@code ONLY_ITEMS}</i>) will be displayed in the item picker overlay.</li>
 *  <li>{@code NO_BLOCKS} - No block items will be displayed in the item picker overlay.</li>
 *  <li>{@code NO_EDIBLES} - No edible food items will be displayed in the item picker overlay.</li>
 * </ul>
 */
public enum ItemRule
{
    NONE,
    ONLY_TOOLS,
    ONLY_ITEMS,
    ONLY_BLOCKS,
    ONLY_CHESTS,
    ONLY_EDIBLES,
    ONLY_DAMAGEABLE,
    INVINCIBLE,
    NO_TOOLS,
    NO_ITEMS,
    NO_BLOCKS,
    NO_EDIBLES;

    /* Methods */

    /**
     * @return The json lang file key for a listing rule.
     */
    private String getLang()
    {
        return String.format("gui.%s.listing.item_rule.", NostalgicTweaks.MOD_ID);
    }

    /**
     * @return A formatted header name for this enumeration.
     */
    public Translation getName()
    {
        return new Translation(this.getLang() + this.name().toLowerCase(Locale.ROOT));
    }

    /**
     * @return A formatted description for this enumeration.
     */
    public Translation getInfo()
    {
        return new Translation(this.getName().langKey() + ".info");
    }
}
