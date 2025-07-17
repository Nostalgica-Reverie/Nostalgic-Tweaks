package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.item.ItemStack;

/**
 * This utility class is used by both the client and server.
 */
public abstract class FoodHelper
{
    /**
     * Check if the given item is eligible to be instantaneously eaten by the player.
     *
     * @param itemStack The {@link ItemStack} instance to check.
     * @return Whether the edible can be instantly consumed.
     */
    public static boolean isInstantaneousEdible(ItemStack itemStack)
    {
        return GameplayTweak.INSTANT_EAT.get() && !GameplayTweak.IGNORED_EDIBLES.get().containsItem(itemStack);
    }
}
