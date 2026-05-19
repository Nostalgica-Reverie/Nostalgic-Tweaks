package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.mixin.access.SheepAccess;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This utility class is used by both the client and server.
 */
public abstract class MobLootHelper
{
    /**
     * Caches an entity type and its associated loot table, so it does not have to be recalculated again.
     */
    private static final Map<EntityType<?>, EntityLoot> LOOT_MAP = new HashMap<>();

    /**
     * Helper record that associates an entity with its tweak controller and old loot table.
     *
     * @param entityType The {@link EntityType} instance.
     * @param tweak      The {@link TweakFlag} controller.
     * @param lootTable  The old {@link LootTable} instance.
     */
    private record EntityLoot(EntityType<?> entityType, TweakFlag tweak, LootTable lootTable)
    {
        EntityLoot
        {
            LOOT_MAP.put(entityType, this);
        }

        LootTable getTable(LootTable vanilla)
        {
            if (this.tweak.get())
                return this.lootTable;

            return vanilla;
        }
    }

    /**
     * Add an item to a loot table builder.
     *
     * @param builder    The {@link LootTable.Builder} instance.
     * @param registries The {@link RegistryAccess} instance.
     * @param item       The {@link ItemLike} instance to add.
     * @param max        The maximum number of items that can drop from a roll.
     * @param canSmelt   Whether the item should smelt if the entity died while on fire.
     * @return The given {@link LootTable.Builder} instance with the new item entry.
     */
    private static LootTable.Builder addToTable(LootTable.Builder builder, RegistryAccess registries, ItemLike item, int max, boolean canSmelt)
    {
        if (canSmelt)
        {
            return builder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, max)))
                    .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                    .apply(SmeltItemFunction.smelted()
                        .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity()
                            .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true)))))));
        }

        return builder.withPool(LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1.0F))
            .add(LootItem.lootTableItem(item)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, max)))
                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))));
    }

    /**
     * Make a simple loot table with a single item and a maximum amount to drop.
     *
     * @param registries The {@link RegistryAccess} instance.
     * @param item       The {@link ItemLike} loot.
     * @param max        The maximum number of items that can drop.
     * @return An old {@link LootTable} instance.
     */
    private static LootTable makeTable(RegistryAccess registries, ItemLike item, int max)
    {
        return addToTable(LootTable.lootTable(), registries, item, max, false).build();
    }

    /**
     * Make a simple pork chop loot table.
     *
     * @param registries The {@link RegistryAccess} instance.
     * @return A custom old {@link LootTable} instance for pork chops.
     */
    private static LootTable makePorkTable(RegistryAccess registries)
    {
        return addToTable(LootTable.lootTable(), registries, Items.PORKCHOP, 2, true).build();
    }

    /**
     * Make a simple skeleton loot table.
     *
     * @param registries The {@link RegistryAccess} instance.
     * @return A custom old {@link LootTable} instance for skeletons.
     */
    private static LootTable makeSkeletonTable(RegistryAccess registries)
    {
        LootTable.Builder builder = LootTable.lootTable();

        addToTable(builder, registries, Items.ARROW, 2, false);
        addToTable(builder, registries, Items.BONE, 2, false);

        return builder.build();
    }

    /* Tables */

    private static final BiFunction<RegistryAccess, ItemLike, LootTable> WOOL_TABLE = Util.memoize((registries, item) -> makeTable(registries, item, 1));

    /* Helpers */

    /**
     * Initialize the old entity loot tables.
     */
    public static void init(ServerLevel level)
    {
        RegistryAccess registries = level.registryAccess();

        final LootTable COOKED_PORK_CHOP_TABLE = makeTable(registries, Items.COOKED_PORKCHOP, 2);
        final LootTable FEATHER_TABLE = makeTable(registries, Items.FEATHER, 2);
        final LootTable STRING_TABLE = makeTable(registries, Items.STRING, 2);
        final LootTable LEATHER_TABLE = makeTable(registries, Items.LEATHER, 2);
        final LootTable RABBIT_HIDE_TABLE = makeTable(registries, Items.RABBIT_HIDE, 1);
        final LootTable PORK_CHOP_TABLE = makePorkTable(registries);
        final LootTable ARROW_BONE_TABLE = makeSkeletonTable(registries);

        new EntityLoot(EntityType.ZOMBIFIED_PIGLIN, GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS, COOKED_PORK_CHOP_TABLE);
        new EntityLoot(EntityType.ZOMBIE, GameplayTweak.OLD_ZOMBIE_DROPS, FEATHER_TABLE);
        new EntityLoot(EntityType.ZOMBIE_VILLAGER, GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS, FEATHER_TABLE);
        new EntityLoot(EntityType.DROWNED, GameplayTweak.OLD_STYLE_DROWNED_DROPS, FEATHER_TABLE);
        new EntityLoot(EntityType.HUSK, GameplayTweak.OLD_STYLE_HUSK_DROPS, FEATHER_TABLE);
        new EntityLoot(EntityType.SPIDER, GameplayTweak.OLD_SPIDER_DROPS, STRING_TABLE);
        new EntityLoot(EntityType.CAVE_SPIDER, GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS, STRING_TABLE);
        new EntityLoot(EntityType.COW, GameplayTweak.OLD_COW_DROPS, LEATHER_TABLE);
        new EntityLoot(EntityType.MOOSHROOM, GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS, LEATHER_TABLE);
        new EntityLoot(EntityType.RABBIT, GameplayTweak.OLD_STYLE_RABBIT_DROPS, RABBIT_HIDE_TABLE);
        new EntityLoot(EntityType.CHICKEN, GameplayTweak.OLD_CHICKEN_DROPS, FEATHER_TABLE);
        new EntityLoot(EntityType.PIG, GameplayTweak.OLD_PIG_DROPS, PORK_CHOP_TABLE);
        new EntityLoot(EntityType.STRAY, GameplayTweak.OLD_STYLE_STRAY_DROPS, ARROW_BONE_TABLE);
        new EntityLoot(EntityType.BOGGED, GameplayTweak.OLD_STYLE_BOGGED_DROPS, ARROW_BONE_TABLE);
    }

    /**
     * Get an old entity loot table, or the vanilla loot table if the tweak context is not applicable.
     *
     * @param entity  The {@link Entity} instance to get context from.
     * @param vanilla The entity's original {@link LootTable} instance.
     * @return A {@link LootTable} instance to use.
     */
    public static LootTable getTable(Entity entity, LootTable vanilla)
    {
        EntityType<?> entityType = entity.getType();

        if (LOOT_MAP.containsKey(entityType))
            return LOOT_MAP.get(entityType).getTable(vanilla);

        if (entityType == EntityType.SHEEP && GameplayTweak.OLD_SHEEP_DROPS.get())
        {
            Sheep sheep = (Sheep) entityType.tryCast(entity);
            RegistryAccess registries = entity.level().registryAccess();

            if (sheep != null && !sheep.isSheared())
                return WOOL_TABLE.apply(registries, SheepAccess.NT$ITEM_BY_DYE().get(sheep.getColor()));
            else
                return LootTable.EMPTY;
        }

        return vanilla;
    }
}
