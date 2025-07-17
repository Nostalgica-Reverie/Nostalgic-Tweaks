package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.container.group.SoundGroup;
import mod.adrenix.nostalgic.tweak.enums.MusicType;
import mod.adrenix.nostalgic.tweak.factory.TweakBinding;
import mod.adrenix.nostalgic.tweak.factory.TweakEnum;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.tweak.factory.TweakStringSet;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.tweak.listing.ListingSuggestion;
import mod.adrenix.nostalgic.tweak.listing.StringSet;

// @formatter:off
public interface SoundTweak
{
    // Disabled

    TweakStringSet DISABLED_POSITIONED_SOUNDS = TweakStringSet.client(new StringSet(ListingSuggestion.SOUND), SoundGroup.DISABLED).build();
    TweakStringSet DISABLED_GLOBAL_SOUNDS = TweakStringSet.client(new StringSet(ListingSuggestion.SOUND), SoundGroup.DISABLED).build();

    // Ambience

    TweakFlag OLD_CAVE_SOUNDS = TweakFlag.client(true, SoundGroup.AMBIENT).build();
    TweakFlag OLD_CAVE_AMBIENCE = TweakFlag.client(true, SoundGroup.AMBIENT).build();
    TweakFlag DISABLE_NETHER_AMBIENCE = TweakFlag.client(true, SoundGroup.AMBIENT).build();
    TweakFlag DISABLE_WATER_AMBIENCE = TweakFlag.client(true, SoundGroup.AMBIENT).build();

    // Music

    TweakFlag PLAY_MUSIC_WHEN_PAUSED = TweakFlag.client(true, SoundGroup.MUSIC).build();
    TweakEnum<MusicType> MUSIC_FOR_MENU = TweakEnum.client(MusicType.ALPHA, SoundGroup.MUSIC).whenDisabled(MusicType.MODERN).build();
    TweakEnum<MusicType> MUSIC_FOR_CREATIVE = TweakEnum.client(MusicType.ALPHA, SoundGroup.MUSIC).whenDisabled(MusicType.MODERN).build();
    TweakFlag REPLACE_OVERWORLD_BIOME_MUSIC = TweakFlag.client(true, SoundGroup.MUSIC).build();
    TweakFlag REPLACE_NETHER_BIOME_MUSIC = TweakFlag.client(true, SoundGroup.MUSIC).build();
    TweakFlag REPLACE_GAMEPLAY_MUSIC = TweakFlag.client(true, SoundGroup.MUSIC).build();

    // Music Controls

    TweakBinding STOP_SONG_BINDING = TweakBinding.client(-1, SoundGroup.MUSIC_CONTROLS, KeybindingId.STOP_SONG).build();
    TweakBinding NEXT_SONG_BINDING = TweakBinding.client(-1, SoundGroup.MUSIC_CONTROLS, KeybindingId.NEXT_SONG).build();

    // Bed Block

    TweakFlag OLD_BED = TweakFlag.client(true, SoundGroup.BLOCK_BED).build();
    TweakFlag DISABLE_BED_PLACE = TweakFlag.client(true, SoundGroup.BLOCK_BED).build();

    // Chest Block

    TweakFlag OLD_CHEST = TweakFlag.client(false, SoundGroup.BLOCK_CHEST).build();
    TweakFlag DISABLE_CHEST = TweakFlag.client(true, SoundGroup.BLOCK_CHEST).build();

    // Lava Block

    TweakFlag DISABLE_LAVA_AMBIENCE = TweakFlag.client(true, SoundGroup.BLOCK_LAVA).build();
    TweakFlag DISABLE_LAVA_POP = TweakFlag.client(true, SoundGroup.BLOCK_LAVA).build();

    // Furnace Block

    TweakFlag DISABLE_FURNACE = TweakFlag.client(true, SoundGroup.BLOCK_FURNACE).build();
    TweakFlag DISABLE_BLAST_FURNACE = TweakFlag.client(true, SoundGroup.BLOCK_FURNACE).build();

    // Blocks

    TweakFlag DISABLE_GROWTH = TweakFlag.client(true, SoundGroup.BLOCK).build();
    TweakFlag DISABLE_DOOR_PLACE = TweakFlag.client(true, SoundGroup.BLOCK).build();
    TweakFlag DISABLE_BUBBLE_COLUMN = TweakFlag.client(true, SoundGroup.BLOCK).build();

    // Damage

    TweakFlag OLD_ATTACK = TweakFlag.client(true, SoundGroup.DAMAGE).build();
    TweakFlag OLD_HURT = TweakFlag.client(true, SoundGroup.DAMAGE).build();
    TweakFlag OLD_FALL = TweakFlag.client(true, SoundGroup.DAMAGE).build();

    // Experience

    TweakFlag OLD_XP = TweakFlag.client(false, SoundGroup.EXPERIENCE).build();
    TweakFlag DISABLE_XP_PICKUP = TweakFlag.client(false, SoundGroup.EXPERIENCE).build();
    TweakFlag DISABLE_XP_LEVEL = TweakFlag.client(false, SoundGroup.EXPERIENCE).build();

    // Mobs

    TweakFlag DISABLE_GENERIC_SWIM = TweakFlag.client(true, SoundGroup.MOB_GENERIC).build();
    TweakFlag DISABLE_FISH_SWIM = TweakFlag.client(true, SoundGroup.MOB_FISH).build();
    TweakFlag DISABLE_FISH_HURT = TweakFlag.client(true, SoundGroup.MOB_FISH).build();
    TweakFlag DISABLE_FISH_DEATH = TweakFlag.client(true, SoundGroup.MOB_FISH).build();
    TweakFlag DISABLE_SQUID = TweakFlag.client(true, SoundGroup.MOB_SQUID).build();
    TweakFlag DISABLE_GLOW_SQUID_OTHER = TweakFlag.client(true, SoundGroup.MOB_SQUID).build();
    TweakFlag DISABLE_GLOW_SQUID_AMBIENCE = TweakFlag.client(false, SoundGroup.MOB_SQUID).build();
    TweakFlag OLD_STEP = TweakFlag.client(true, SoundGroup.MOB).build();
    TweakFlag IGNORE_MODDED_STEP = TweakFlag.client(false, SoundGroup.MOB).build();
}
