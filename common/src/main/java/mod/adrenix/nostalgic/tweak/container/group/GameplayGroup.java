package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

// @formatter:off
public interface GameplayGroup
{
    // Bugs

    Container BUGS = Container.group(Category.GAMEPLAY, "bugs").color(0xFB4A4C).icon(Icons.BUG).build();

    // Mechanics

    Container MECHANICS = Container.group(Category.GAMEPLAY, "mechanics").color(0xFFDB5C).icon(Icons.MECHANICAL_TOOLS).build();
    Container MECHANICS_FIRE = Container.group(MECHANICS, "mechanics_fire").color(0xCE7806).icon(Icons.FIRE).build();
    Container MECHANICS_CART = Container.group(MECHANICS, "mechanics_cart").color(0x8D8E93).icon(Items.MINECART).build();
    Container MECHANICS_BOAT = Container.group(MECHANICS, "mechanics_boat").color(0x896727).icon(Items.OAK_BOAT).build();
    Container MECHANICS_BLOCK = Container.group(MECHANICS, "mechanics_block").color(0x98C767).icon(Blocks.GRASS_BLOCK).build();
    Container MECHANICS_BLOCK_BED = Container.group(MECHANICS_BLOCK, "mechanics_block_bed").color(0xC25654).icon(Blocks.RED_BED).build();
    Container MECHANICS_BLOCK_TNT = Container.group(MECHANICS_BLOCK, "mechanics_block_tnt").color(0xE63D14).icon(Blocks.TNT).build();
    Container MECHANICS_BLOCK_CHEST = Container.group(MECHANICS_BLOCK, "mechanics_block_chest").color(0xFFB444).icon(Blocks.CHEST).build();
    Container MECHANICS_BLOCK_SPONGE = Container.group(MECHANICS_BLOCK, "mechanics_block_sponge").color(0xE5E533).icon(Blocks.SPONGE).build();
    Container MECHANICS_FARMING = Container.group(MECHANICS, "mechanics_farming").color(0x69B038).icon(Items.WHEAT_SEEDS).build();
    Container MECHANICS_FISHING = Container.group(MECHANICS, "mechanics_fishing").color(0x896727).icon(Items.FISHING_ROD).build();
    Container MECHANICS_PLAYER = Container.group(MECHANICS, "mechanics_player").color(0xB6896C).icon(Items.PLAYER_HEAD).build();
    Container MECHANICS_TOOLS = Container.group(MECHANICS, "mechanics_tools").color(0x8D8E93).icon(Items.IRON_AXE).build();
    Container MECHANICS_SWIMMING = Container.group(MECHANICS, "mechanics_swimming").color(0x375DCA).icon(Items.WATER_BUCKET).build();
    Container MECHANICS_ITEMS = Container.group(MECHANICS, "mechanics_items").color(0x8CF4E2).icon(Items.DIAMOND).build();

    // Experience

    Container EXPERIENCE = Container.group(Category.GAMEPLAY, "experience").color(0xF5FF8F).icon(Items.EXPERIENCE_BOTTLE).build();
    Container EXPERIENCE_ORB = Container.group(EXPERIENCE, "experience_orb").color(0xBBB71B).icon(Icons.EXPERIENCE).build();
    Container EXPERIENCE_BLOCK = Container.group(EXPERIENCE, "experience_block").color(0xA4EDDC).icon(Blocks.ENCHANTING_TABLE).build();

    // Stamina

    Container STAMINA = Container.group(Category.GAMEPLAY, "stamina").color(0xFFE47C).icon(Icons.LIGHTNING).build();
    Container STAMINA_EFFECTS = Container.group(STAMINA, "stamina_effects").color(0x375DCA).icon(Items.POTION).build();

    // Hunger

    Container HUNGER = Container.group(Category.GAMEPLAY, "hunger").color(0xFF8C8C).icon(Items.PORKCHOP).build();
    Container HUNGER_FOOD = Container.group(HUNGER, "hunger_food").color(0xDA8947).icon(Items.COOKIE).build();

    // Combat

    Container COMBAT = Container.group(Category.GAMEPLAY, "combat").color(0x2BC7AC).icon(Items.DIAMOND_SWORD).build();
    Container COMBAT_BOW = Container.group(COMBAT, "combat_bow").color(0x896727).icon(Items.BOW).build();
    Container COMBAT_BLOCKING = Container.group(COMBAT, "combat_blocking").color(0x9B9B9B).icon(Items.SHIELD).build();

    // Mob

    Container MOB = Container.group(Category.GAMEPLAY, "mob").color(0x70DC5D).icon(Items.CREEPER_HEAD).build();
    Container MOB_AI = Container.group(MOB, "mob_ai").color(0xF57C83).icon(Icons.BRAIN).build();
    Container MOB_MONSTER = Container.group(MOB, "mob_monster").color(0x9B9B9B).icon(Items.GUNPOWDER).build();
    Container MOB_MONSTER_SPAWN = Container.group(MOB_MONSTER, "mob_monster_spawn").color(0x377BA8).icon(Blocks.SPAWNER).build();
    Container MOB_ANIMAL = Container.group(MOB, "mob_animal").color(0xD96927).icon(Items.TROPICAL_FISH).build();
    Container MOB_ANIMAL_SPAWN = Container.group(MOB_ANIMAL, "mob_animal_spawn").color(0x377BA8).icon(Blocks.SPAWNER).build();
    Container MOB_ANIMAL_SHEEP = Container.group(MOB_ANIMAL, "mob_animal_sheep").color(0xFFFFFF).icon(Blocks.WHITE_WOOL).build();
    Container MOB_DROPS = Container.group(MOB, "mob_drops").color(0xC65C35).icon(Items.LEATHER).build();
    Container MOB_DROPS_CLASSIC = Container.group(MOB_DROPS, "mob_drops_classic").color(0xCECECE).icon(Items.FEATHER).build();
    Container MOB_DROPS_MODERN = Container.group(MOB_DROPS, "mob_drops_modern").color(0xC79E67).icon(Items.RABBIT_FOOT).build();
}
