package mod.adrenix.nostalgic.helper.sound;

import mod.adrenix.nostalgic.client.ClientSound;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import mod.adrenix.nostalgic.tweak.enums.MusicType;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * This helper class is used only by the client.
 */
public abstract class MusicHelper
{
    /**
     * Holds the current music that is separate from the music manager's current music.
     */
    public static final NullableHolder<SoundInstance> CURRENT_SONG = NullableHolder.empty();

    /**
     * Set the current music and get a new sound instance.
     *
     * @param sound The {@link SoundEvent} of the music to play.
     * @param fallback Fallback {@link SoundInstance} to use in case the {@link SoundEvent} is null.
     * @return The {@link SoundInstance} for the new music.
     */
    private static SoundInstance setAndGetOrElse(SoundEvent sound, SoundInstance fallback)
    {
        return sound != null ? CURRENT_SONG.setAndGet(SimpleSoundInstance.forMusic(sound)) : fallback;
    }

    /**
     * Get a sound to replace with based on game context and tweak context.
     *
     * @param sound The original {@link SoundInstance} that was to be played.
     * @return The given {@link SoundInstance} or a different {@link SoundInstance} to play.
     */
    public static SoundInstance apply(SoundInstance sound)
    {
        ResourceLocation soundLocation = sound.getLocation();

        if (soundLocation.equals(SoundEvents.MUSIC_MENU.value().getLocation()))
        {
            MusicType musicType = SoundTweak.MUSIC_FOR_MENU.get();

            if (musicType == MusicType.MODERN)
                return sound;

            return setAndGetOrElse(switch (musicType)
            {
                case ALPHA -> ClientSound.MUSIC_ALPHA.getOrNull();
                case BETA -> ClientSound.MUSIC_MENU_BETA.getOrNull();
                case BLENDED -> ClientSound.MUSIC_MENU_BLENDED.getOrNull();
                default -> ClientSound.BLANK.getOrNull();
            }, sound);
        }

        if (soundLocation.equals(SoundEvents.MUSIC_CREATIVE.value().getLocation()))
        {
            MusicType musicType = SoundTweak.MUSIC_FOR_CREATIVE.get();

            if (musicType == MusicType.MODERN)
                return sound;

            return setAndGetOrElse(switch (musicType)
            {
                case ALPHA -> ClientSound.MUSIC_ALPHA.getOrNull();
                case BETA -> ClientSound.MUSIC_CREATIVE_BETA.getOrNull();
                case BLENDED -> ClientSound.MUSIC_CREATIVE_BLENDED.getOrNull();
                default -> ClientSound.BLANK.getOrNull();
            }, sound);
        }

        if (SoundTweak.REPLACE_OVERWORLD_BIOME_MUSIC.get() && GameUtil.isInOverworld())
        {
            if (soundLocation.getPath().contains("music.overworld"))
                return setAndGetOrElse(ClientSound.MUSIC_ALPHA.getOrNull(), sound);

            if (soundLocation.equals(SoundEvents.MUSIC_UNDER_WATER.value().getLocation()))
                return setAndGetOrElse(ClientSound.MUSIC_ALPHA.getOrNull(), sound);
        }

        if (SoundTweak.REPLACE_NETHER_BIOME_MUSIC.get() && GameUtil.isInNether())
        {
            if (soundLocation.getPath().contains("music.nether"))
                return setAndGetOrElse(ClientSound.MUSIC_ALPHA.getOrNull(), sound);
        }

        if (SoundTweak.REPLACE_GAMEPLAY_MUSIC.get())
        {
            if (soundLocation.equals(SoundEvents.MUSIC_GAME.value().getLocation()))
                return setAndGetOrElse(ClientSound.MUSIC_ALPHA.getOrNull(), sound);
        }

        return sound;
    }
}
