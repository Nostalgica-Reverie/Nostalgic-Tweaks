package mod.adrenix.nostalgic.mixin.tweak.candy.uncap_title_fps;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public ClientLevel level;

    @Shadow
    public abstract Window getWindow();

    /* Injections */

    /**
     * Uncaps the framerate limit imposed on the title screen.
     */
    @ModifyReturnValue(
        method = "getFramerateLimit",
        at = @At("RETURN")
    )
    private int nt_uncap_title_fps$modifyLimit(int framerate)
    {
        if (this.level != null)
            return this.getWindow().getFramerateLimit();

        return CandyTweak.UNCAP_TITLE_FPS.get() ? Math.max(this.getWindow().getFramerateLimit(), 60) : framerate;
    }
}
