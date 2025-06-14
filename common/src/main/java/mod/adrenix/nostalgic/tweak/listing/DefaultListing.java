package mod.adrenix.nostalgic.tweak.listing;

import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public interface DefaultListing
{
    // Candy Listings

    static ItemSet ambientOcclusion()
    {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.add(ItemUtil.getResourceKey(Blocks.POWDER_SNOW));
        set.add(ItemUtil.getResourceKey(Blocks.COMPOSTER));

        return new ItemSet(ItemRule.ONLY_BLOCKS).startWith(set);
    }

    static ItemSet disabledOffsets()
    {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.addAll(ItemListing.getWildcardKeys(Blocks.POPPY));
        set.addAll(ItemListing.getWildcardKeys(Blocks.ROSE_BUSH));

        return new ItemSet(ItemRule.ONLY_BLOCKS).startWith(set);
    }

    static ItemSet blockOutlines()
    {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.addAll(ItemListing.getWildcardKeys(Blocks.COBBLESTONE_STAIRS));
        set.addAll(ItemListing.getWildcardKeys(Blocks.COBBLESTONE_WALL));
        set.addAll(ItemListing.getWildcardKeys(Blocks.OAK_FENCE));
        set.addAll(ItemListing.getWildcardKeys(Blocks.CHEST));
        set.addAll(ItemListing.getWildcardKeys(Blocks.ENDER_CHEST));
        set.addAll(ItemListing.getWildcardKeys(Blocks.CAULDRON));
        set.addAll(ItemListing.getWildcardKeys(Blocks.HOPPER));
        set.addAll(ItemListing.getWildcardKeys(Blocks.ANVIL));

        return new ItemSet(ItemRule.ONLY_BLOCKS).startWith(set);
    }

    static ItemSet ignoredHoldingItems()
    {
        return new ItemSet(ItemRule.NO_BLOCKS).startWith(ItemUtil.getKeysFromItems(Items.CROSSBOW));
    }

    static ItemSet old2dExceptions()
    {
        return new ItemSet().startWith(ItemUtil.getKeysFromItems(Items.DECORATED_POT));
    }

    // Gameplay Listings

    static StringSet oldAnimalSpawns()
    {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.add(EntityType.getKey(EntityType.CHICKEN).toString());
        set.add(EntityType.getKey(EntityType.COW).toString());
        set.add(EntityType.getKey(EntityType.MOOSHROOM).toString());
        set.add(EntityType.getKey(EntityType.PIG).toString());
        set.add(EntityType.getKey(EntityType.SHEEP).toString());

        return new StringSet(ListingSuggestion.CREATURE).startWith(set);
    }

    static ItemSet selfBlockDrops()
    {
        LinkedHashSet<String> set = new LinkedHashSet<>();

        set.add(ItemUtil.getResourceKey(Blocks.IRON_ORE));
        set.add(ItemUtil.getResourceKey(Blocks.GOLD_ORE));
        set.add(ItemUtil.getResourceKey(Blocks.COPPER_ORE));
        set.add(ItemUtil.getResourceKey(Blocks.DEEPSLATE_IRON_ORE));
        set.add(ItemUtil.getResourceKey(Blocks.DEEPSLATE_GOLD_ORE));
        set.add(ItemUtil.getResourceKey(Blocks.DEEPSLATE_COPPER_ORE));

        ItemSet itemSet = new ItemSet(ItemRule.ONLY_BLOCKS).startWith(set);
        itemSet.setDisabled(true);

        return itemSet;
    }

    static ItemMap<Integer> foodHealth()
    {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        map.put(ItemUtil.getResourceKey(Items.ROTTEN_FLESH), 0);
        map.put(ItemUtil.getResourceKey(Items.SPIDER_EYE), 0);
        map.put(ItemUtil.getResourceKey(Items.CARROT), 1);
        map.put(ItemUtil.getResourceKey(Items.MELON_SLICE), 1);
        map.put(ItemUtil.getResourceKey(Items.CHORUS_FRUIT), 1);
        map.put(ItemUtil.getResourceKey(Items.SWEET_BERRIES), 1);
        map.put(ItemUtil.getResourceKey(Items.GLOW_BERRIES), 1);
        map.put(ItemUtil.getResourceKey(Items.MUSHROOM_STEW), 10);
        map.put(ItemUtil.getResourceKey(Items.BEETROOT_SOUP), 10);
        map.put(ItemUtil.getResourceKey(Items.RABBIT_STEW), 10);
        map.put(ItemUtil.getResourceKey(Items.SUSPICIOUS_STEW), 10);
        map.put(ItemUtil.getResourceKey(Items.GOLDEN_APPLE), 20);
        map.put(ItemUtil.getResourceKey(Items.ENCHANTED_GOLDEN_APPLE), 20);

        return new ItemMap<>(10).startWith(map).rules(ItemRule.ONLY_EDIBLES);
    }

    static ItemMap<Integer> foodStacks()
    {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        map.put(ItemUtil.getResourceKey(Items.COOKIE), 8);
        map.put(ItemUtil.getResourceKey(Items.BEETROOT), 8);
        map.put(ItemUtil.getResourceKey(Items.CARROT), 8);
        map.put(ItemUtil.getResourceKey(Items.CHORUS_FRUIT), 8);
        map.put(ItemUtil.getResourceKey(Items.DRIED_KELP), 9);
        map.put(ItemUtil.getResourceKey(Items.MELON_SLICE), 8);
        map.put(ItemUtil.getResourceKey(Items.POTATO), 8);
        map.put(ItemUtil.getResourceKey(Items.POISONOUS_POTATO), 8);
        map.put(ItemUtil.getResourceKey(Items.SWEET_BERRIES), 8);
        map.put(ItemUtil.getResourceKey(Items.GLOW_BERRIES), 8);
        map.put(ItemUtil.getResourceKey(Items.APPLE), 1);
        map.put(ItemUtil.getResourceKey(Items.BAKED_POTATO), 1);
        map.put(ItemUtil.getResourceKey(Items.BEEF), 1);
        map.put(ItemUtil.getResourceKey(Items.BEETROOT_SOUP), 1);
        map.put(ItemUtil.getResourceKey(Items.BREAD), 1);
        map.put(ItemUtil.getResourceKey(Items.CHICKEN), 1);
        map.put(ItemUtil.getResourceKey(Items.COD), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_BEEF), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_CHICKEN), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_COD), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_MUTTON), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_PORKCHOP), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_RABBIT), 1);
        map.put(ItemUtil.getResourceKey(Items.COOKED_SALMON), 1);
        map.put(ItemUtil.getResourceKey(Items.ENCHANTED_GOLDEN_APPLE), 1);
        map.put(ItemUtil.getResourceKey(Items.GOLDEN_APPLE), 1);
        map.put(ItemUtil.getResourceKey(Items.GOLDEN_CARROT), 1);
        map.put(ItemUtil.getResourceKey(Items.HONEY_BOTTLE), 4);
        map.put(ItemUtil.getResourceKey(Items.MUSHROOM_STEW), 1);
        map.put(ItemUtil.getResourceKey(Items.MUTTON), 1);
        map.put(ItemUtil.getResourceKey(Items.PORKCHOP), 1);
        map.put(ItemUtil.getResourceKey(Items.PUFFERFISH), 1);
        map.put(ItemUtil.getResourceKey(Items.PUMPKIN_PIE), 1);
        map.put(ItemUtil.getResourceKey(Items.RABBIT), 1);
        map.put(ItemUtil.getResourceKey(Items.RABBIT_STEW), 1);
        map.put(ItemUtil.getResourceKey(Items.SALMON), 1);
        map.put(ItemUtil.getResourceKey(Items.SUSPICIOUS_STEW), 1);
        map.put(ItemUtil.getResourceKey(Items.TROPICAL_FISH), 1);

        return new ItemMap<>(1).startWith(map).rules(ItemRule.ONLY_EDIBLES);
    }
}
