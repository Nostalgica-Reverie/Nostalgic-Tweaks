package mod.adrenix.nostalgic.mixin.tweak.sound.game_music;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.helper.sound.MusicHelper;
import mod.adrenix.nostalgic.tweak.config.SoundTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin
{
    @Shadow private int nextSongDelay;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private SoundInstance currentMusic;

    /**
     * Removes the delay until the next song if music is to play continuously.
     */
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void nt_game_music$skipMusicDelay(CallbackInfo callback)
    {
        if (this.currentMusic == null && this.minecraft.level != null && SoundTweak.PLAY_MUSIC_CONTINUOUSLY.get())
        {
            if (this.nextSongDelay > SoundTweak.CONTINUOUS_DELAY.get() * 20)
                this.nextSongDelay = SoundTweak.CONTINUOUS_DELAY.get() * 20;
        }
    }

    /**
     * Instructs the music manager to also check if our music is still playing.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;isActive(Lnet/minecraft/client/resources/sounds/SoundInstance;)Z"
        )
    )
    private boolean nt_game_music$shouldStopCurrentMusic(boolean isMusicActive)
    {
        if (MusicHelper.CURRENT_SONG.isEmpty())
            return isMusicActive;

        if (this.minecraft.getSoundManager().isActive(MusicHelper.CURRENT_SONG.getOrThrow()))
            return true;
        else
            MusicHelper.CURRENT_SONG.clear();

        return isMusicActive;
    }

    /**
     * Changes the music played based on game context.
     */
    @ModifyArg(
        method = "startPlaying",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"
        )
    )
    private SoundInstance nt_game_music$playC418Music(SoundInstance sound)
    {
        ResourceLocation soundLocation = sound.getLocation();

        if (soundLocation.getNamespace().equals("minecraft"))
            return MusicHelper.apply(sound);

        return sound;
    }

    /**
     * Stops overridden C418 music if it is playing.
     */
    @Inject(
        method = "stopPlaying()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundManager;stop(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"
        )
    )
    private void nt_game_music$stopC418Music(CallbackInfo callback)
    {
        MusicHelper.CURRENT_SONG.ifPresent(song -> this.minecraft.getSoundManager().stop(song));
        MusicHelper.CURRENT_SONG.clear();
    }
}
