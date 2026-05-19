package mod.adrenix.nostalgic.helper.gameplay.combat;

import net.minecraft.world.item.*;

import java.util.Map;
import java.util.function.Function;

/**
 * This utility class is used by both the client and server.
 */
public abstract class DamageHelper
{
    /**
     * A cache map that contains old tiered damage values.
     */
    // @formatter:off
    private static final Map<Class<? extends TieredItem>, Function<TieredItem, Float>> OLD_DAMAGE_MAP = Map.of(
        SwordItem.class, item -> item.getTier().getAttackDamageBonus() + 6.0F,
        AxeItem.class, item -> item.getTier().getAttackDamageBonus() + 2.0F,
        PickaxeItem.class, item -> item.getTier().getAttackDamageBonus() + 1.0F,
        ShovelItem.class, item -> item.getTier().getAttackDamageBonus() + 0.5F,
        HoeItem.class, item -> item.getTier().getAttackDamageBonus()
    );
    // @formatter:on

    /**
     * Check if the given item has a tier and has an old value.
     *
     * @param item The {@link TieredItem} to check.
     * @return Whether the given item has an old damage value.
     */
    public static boolean isApplicable(TieredItem item)
    {
        return OLD_DAMAGE_MAP.containsKey(item.getClass());
    }

    /**
     * Get the old damage value based on the provided item.
     *
     * @param item The {@link TieredItem} instance.
     * @return An old damage value, if it is a vanilla item.
     */
    public static float get(TieredItem item)
    {
        Class<? extends TieredItem> tierClass = item.getClass();

        if (OLD_DAMAGE_MAP.containsKey(tierClass))
            return OLD_DAMAGE_MAP.get(tierClass).apply(item);

        return 0.0F;
    }
}
