package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.container.group.GameplayGroup;
import mod.adrenix.nostalgic.tweak.enums.StaminaRegain;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.listing.DefaultListing;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.world.entity.MobCategory;

// @formatter:off
public interface GameplayTweak
{
    // Bugs

    TweakFlag OLD_LADDER_GAP = TweakFlag.server(true, GameplayGroup.BUGS).noSSO().build();
    TweakFlag OLD_SQUID_MILKING = TweakFlag.server(true, GameplayGroup.BUGS).build();

    // Mob AI

    TweakFlag DISABLE_ANIMAL_TEMPTING = TweakFlag.server(false, GameplayGroup.MOB_AI).build();
    TweakFlag DISABLE_ANIMAL_PANIC = TweakFlag.server(true, GameplayGroup.MOB_AI).build();
    TweakFlag DISABLE_MONSTER_AVOID_SUN = TweakFlag.server(true, GameplayGroup.MOB_AI).build();
    TweakFlag DISABLE_SKELETON_STRAFING = TweakFlag.server(false, GameplayGroup.MOB_AI).build();
    TweakFlag OLD_CREEPER_STRAFE_ON_SWELL = TweakFlag.server(true, GameplayGroup.MOB_AI).build();

    // Monsters

    TweakFlag DISABLE_MONSTER_ITEM_PICKUP = TweakFlag.server(false, GameplayGroup.MOB_MONSTER).build();

    // Monster Spawning

    TweakFlag DISABLE_BABY_ZOMBIE_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).build();
    TweakFlag DISABLE_BABY_PIGLIN_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).build();
    TweakFlag DISABLE_MONSTER_ITEM_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).build();
    TweakFlag DISABLE_MONSTER_ARMOR_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).build();
    TweakFlag DISABLE_MONSTER_ENCHANT_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).build();
    TweakFlag PIGLIN_ONLY_GOLD_SWORD_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_MONSTER_SPAWN).build();
    TweakNumber<Integer> MONSTER_SPAWN_CAP = TweakNumber.server(90, GameplayGroup.MOB_MONSTER_SPAWN)
        .slider(Lang.Slider.CAP, 0, 100)
        .whenDisabled(MobCategory.MONSTER.getMaxInstancesPerChunk())
        .build();

    // Animal Spawning

    TweakFlag DISABLE_ANIMAL_BREEDING = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).build();
    TweakFlag DISABLE_BABY_ANIMAL_SPAWNING = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).build();
    TweakFlag OLD_ANIMAL_SPAWNING = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).ignoreNetworkCheck().warningTag().build();
    TweakStringSet OLD_ANIMAL_SPAWN_LIST = TweakStringSet.server(DefaultListing.oldAnimalSpawns(), GameplayGroup.MOB_ANIMAL_SPAWN).ignoreNetworkCheck().build();
    TweakNumber<Integer> ANIMAL_SPAWN_CAP = TweakNumber.server(15, GameplayGroup.MOB_ANIMAL_SPAWN).slider(Lang.Slider.CAP, 0, 100).build();
    TweakFlag KEEP_BABY_ANIMAL_WHILE_OLD_SPAWN = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).ignoreNetworkCheck().build();
    TweakFlag IGNORE_ANIMAL_BIOME_RESTRICTIONS = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SPAWN).ignoreNetworkCheck().build();

    // Sheep

    TweakFlag DISABLE_SHEEP_EAT_GRASS = TweakFlag.server(true, GameplayGroup.MOB_ANIMAL_SHEEP).build();
    TweakFlag RANDOM_SHEEP_WOOL_REGEN = TweakFlag.server(true, GameplayGroup.MOB_ANIMAL_SHEEP).build();
    TweakFlag OLD_SHEEP_PUNCHING = TweakFlag.server(true, GameplayGroup.MOB_ANIMAL_SHEEP).build();
    TweakFlag ONE_WOOL_PUNCH = TweakFlag.server(false, GameplayGroup.MOB_ANIMAL_SHEEP).build();

    // Classic Mob Drops

    TweakFlag OLD_ZOMBIE_PIGMEN_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_SKELETON_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_CHICKEN_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_ZOMBIE_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_SPIDER_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_SHEEP_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_COW_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();
    TweakFlag OLD_PIG_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_CLASSIC).build();

    // Modern Mob Drops

    TweakFlag OLD_STYLE_ZOMBIE_VILLAGER_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();
    TweakFlag OLD_STYLE_CAVE_SPIDER_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();
    TweakFlag OLD_STYLE_MOOSHROOM_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();
    TweakFlag OLD_STYLE_DROWNED_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();
    TweakFlag OLD_STYLE_RABBIT_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();
    TweakFlag OLD_STYLE_STRAY_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();
    TweakFlag OLD_STYLE_HUSK_DROPS = TweakFlag.server(false, GameplayGroup.MOB_DROPS_MODERN).build();

    // Combat Blocking

    TweakFlag BLOCK_WITH_SWORD_ON_SHIELD = TweakFlag.client(true, GameplayGroup.COMBAT_BLOCKING).build();
    TweakFlag OLD_SWORD_BLOCKING = TweakFlag.server(true, GameplayGroup.COMBAT_BLOCKING).noSSO().build();
    TweakFlag ATTACK_WHILE_SWORD_BLOCKING = TweakFlag.server(true, GameplayGroup.COMBAT_BLOCKING).build();
    TweakNumber<Integer> SWORD_BLOCK_DAMAGE_REDUCTION = TweakNumber.server(50, GameplayGroup.COMBAT_BLOCKING).slider(Lang.Slider.PERCENTAGE, 0, 100, "%").build();

    // Combat

    TweakFlag OLD_DAMAGE_VALUES = TweakFlag.server(false, GameplayGroup.COMBAT).build();
    TweakFlag DISABLE_COOLDOWN = TweakFlag.server(false, GameplayGroup.COMBAT).build();
    TweakFlag DISABLE_MISS_TIMER = TweakFlag.server(false, GameplayGroup.COMBAT).build();
    TweakFlag DISABLE_CRITICAL_HIT = TweakFlag.server(false, GameplayGroup.COMBAT).build();
    TweakFlag DISABLE_SWEEP = TweakFlag.server(false, GameplayGroup.COMBAT).build();

    // Combat Bow

    TweakNumber<Integer> ARROW_SPEED = TweakNumber.server(70, GameplayGroup.COMBAT_BOW).slider(0, 100, SliderType.INTENSITY).build();
    TweakFlag INSTANT_BOW = TweakFlag.server(false, GameplayGroup.COMBAT_BOW).noSSO().build();
    TweakFlag INVINCIBLE_BOW = TweakFlag.server(false, GameplayGroup.COMBAT_BOW).build();

    // Experience Orb

    TweakFlag DISABLE_ORB_SPAWN = TweakFlag.server(false, GameplayGroup.EXPERIENCE_ORB).build();
    TweakFlag DISABLE_ORB_RENDERING = TweakFlag.client(false, GameplayGroup.EXPERIENCE_ORB).build();
    TweakFlag IMMEDIATE_EXPERIENCE_PICKUP = TweakFlag.server(false, GameplayGroup.EXPERIENCE_ORB).build();

    // Experience Blocks

    TweakFlag DISABLE_ANVIL = TweakFlag.server(false, GameplayGroup.EXPERIENCE_BLOCK).build();
    TweakFlag DISABLE_ENCHANT_TABLE = TweakFlag.server(false, GameplayGroup.EXPERIENCE_BLOCK).build();

    // Stamina Mechanics

    TweakFlag STAMINA_SPRINT = TweakFlag.dynamic(false, GameplayGroup.STAMINA).noSSO().alert(TweakAlert.STAMINA_SPRINTING_CONFLICT).build();
    TweakFlag STAMINA_INFINITE_PEACEFUL = TweakFlag.dynamic(false, GameplayGroup.STAMINA).build();
    TweakEnum<StaminaRegain> STAMINA_REGAIN_WHEN_MOVING = TweakEnum.dynamic(StaminaRegain.NORMAL, GameplayGroup.STAMINA).build();
    TweakNumber<Integer> STAMINA_DURATION = TweakNumber.dynamic(5, GameplayGroup.STAMINA).slider(Lang.Slider.SECONDS, 1, 60).build();
    TweakNumber<Integer> STAMINA_RECHARGE = TweakNumber.dynamic(7, GameplayGroup.STAMINA).slider(Lang.Slider.SECONDS, 1, 60).build();
    TweakNumber<Integer> STAMINA_COOLDOWN = TweakNumber.dynamic(0, GameplayGroup.STAMINA).slider(Lang.Slider.SECONDS, 0, 60).build();

    // Stamina Effects

    TweakFlag STAMINA_SATURATION_EFFECT = TweakFlag.dynamic(false, GameplayGroup.STAMINA_EFFECTS).build();
    TweakFlag STAMINA_HUNGER_EFFECT = TweakFlag.dynamic(false, GameplayGroup.STAMINA_EFFECTS).build();

    // Player Mechanics

    TweakFlag OLD_NIGHTMARES = TweakFlag.server(true, GameplayGroup.MECHANICS_PLAYER).build();
    TweakFlag DISABLE_SPRINT = TweakFlag.dynamic(false, GameplayGroup.MECHANICS_PLAYER).noSSO().build();
    TweakFlag LEFT_CLICK_DOOR = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).build();
    TweakFlag LEFT_CLICK_LEVER = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).build();
    TweakFlag LEFT_CLICK_BUTTON = TweakFlag.server(false, GameplayGroup.MECHANICS_PLAYER).build();

    // Fishing Mechanics

    TweakFlag OLD_FISHING_LOOT = TweakFlag.server(false, GameplayGroup.MECHANICS_FISHING).build();
    TweakFlag OLD_FISHING_LURING = TweakFlag.server(true, GameplayGroup.MECHANICS_FISHING).build();
    TweakFlag OLD_FISHING_CASTING = TweakFlag.dynamic(true, GameplayGroup.MECHANICS_FISHING).build();

    // Farming Mechanics

    TweakFlag INSTANT_BONEMEAL = TweakFlag.server(true, GameplayGroup.MECHANICS_FARMING).build();
    TweakFlag TILLED_GRASS_SEEDS = TweakFlag.server(true, GameplayGroup.MECHANICS_FARMING).build();

    // Fire Mechanics

    TweakFlag OLD_FIRE = TweakFlag.server(false, GameplayGroup.MECHANICS_FIRE).warningTag().build();
    TweakFlag INFINITE_BURN = TweakFlag.server(false, GameplayGroup.MECHANICS_FIRE).build();

    // Swimming Mechanics

    TweakFlag DISABLE_SWIM = TweakFlag.dynamic(false, GameplayGroup.MECHANICS_SWIMMING).noSSO().build();
    TweakFlag INSTANT_AIR = TweakFlag.server(true, GameplayGroup.MECHANICS_SWIMMING).build();

    // Minecart Mechanics

    TweakFlag CART_BOOSTING = TweakFlag.server(false, GameplayGroup.MECHANICS_CART).build();
    TweakFlag CART_RIDER_TURNING = TweakFlag.client(true, GameplayGroup.MECHANICS_CART).build();
    TweakFlag CART_SAFE_RIDER_TURNING = TweakFlag.client(true, GameplayGroup.MECHANICS_CART).warningTag().whenDisabled(true).build();

    // Boat Mechanics

    TweakFlag DISABLE_BOAT_BUSY_HANDS = TweakFlag.server(true, GameplayGroup.MECHANICS_BOAT).build();
    TweakFlag OLD_BOAT_WATER_LIFT = TweakFlag.server(true, GameplayGroup.MECHANICS_BOAT).build();
    TweakFlag OLD_BOAT_DROPS = TweakFlag.server(false, GameplayGroup.MECHANICS_BOAT).build();

    // Block Mechanics

    TweakFlag PUNCH_TNT_IGNITION = TweakFlag.server(false, GameplayGroup.MECHANICS_BLOCK_TNT).build();
    TweakFlag DISABLE_BED_BOUNCE = TweakFlag.server(true, GameplayGroup.MECHANICS_BLOCK_BED).build();
    TweakFlag ALWAYS_OPEN_CHEST = TweakFlag.server(true, GameplayGroup.MECHANICS_BLOCK_CHEST).build();
    TweakItemSet SELF_BLOCK_DROPS = TweakItemSet.server(DefaultListing.selfBlockDrops(), GameplayGroup.MECHANICS_BLOCK).ignoreNetworkCheck().warningTag().build();

    // Item Mechanics

    TweakItemMap<Integer> CUSTOM_ITEM_STACKING = TweakItemMap.server(new ItemMap<>(64), GameplayGroup.MECHANICS_ITEMS)
        .ignoreNetworkCheck()
        .warningTag()
        .slider(Lang.Slider.STACK, 1, 64)
        .build();

    // Tool Mechanics

    TweakFlag DISABLE_AXE_STRIPPING = TweakFlag.dynamic(false, GameplayGroup.MECHANICS_TOOLS).build();
    TweakFlag DISABLE_SHOVEL_PATHING = TweakFlag.dynamic(false, GameplayGroup.MECHANICS_TOOLS).build();

    // Hunger

    TweakFlag DISABLE_HUNGER = TweakFlag.server(false, GameplayGroup.HUNGER).build();
    TweakFlag PREVENT_HUNGER_EFFECT = TweakFlag.server(false, GameplayGroup.HUNGER).build();

    // Instant Food

    TweakFlag INSTANT_EAT = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).build();
    TweakFlag PREVENT_INSTANT_EAT_EFFECTS = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).build();
    TweakItemSet IGNORED_EDIBLES = TweakItemSet.server(new ItemSet(ItemRule.ONLY_EDIBLES), GameplayGroup.HUNGER_FOOD).build();
    TweakItemMap<Integer> CUSTOM_FOOD_HEALTH = TweakItemMap.server(DefaultListing.foodHealth(), GameplayGroup.HUNGER_FOOD)
        .icon(Icons.HEART)
        .slider(0, 20, SliderType.HEARTS).alert(TweakAlert.FOOD_HEALTH_CONFLICT)
        .build();

    // Food Stacking

    TweakFlag OLD_FOOD_STACKING = TweakFlag.server(false, GameplayGroup.HUNGER_FOOD).ignoreNetworkCheck().build();
    TweakItemMap<Integer> CUSTOM_FOOD_STACKING = TweakItemMap.server(DefaultListing.foodStacks(), GameplayGroup.HUNGER_FOOD)
        .ignoreNetworkCheck()
        .warningTag()
        .slider(Lang.Slider.STACK, 1, 64)
        .alert(TweakAlert.FOOD_STACKING_CONFLICT)
        .build();
}
