package mod.adrenix.nostalgic.mixin.tweak.candy.mip_map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin
{
    /* Shadows */

    @Shadow public abstract void updateMaxMipLevel(int level);

    /* Injections */

    /**
     * Defines the instructions to perform when the mipmap tweak is changed.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_mip_map$onInit(CallbackInfo ci)
    {
        CandyTweak.REMOVE_MIPMAP_TEXTURE.whenChanged(() ->
            this.updateMaxMipLevel(Minecraft.getInstance().options.mipmapLevels().get()));
    }

    /**
     * Changes the mipmap level on model manager initialization.
     */
    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/resources/model/ModelManager;maxMipmapLevels:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void nt_mip_map$onMipmapLevelsInit(ModelManager instance, int value, Operation<Void> original)
    {
        if (CandyTweak.REMOVE_MIPMAP_TEXTURE.get())
            value = 0;

        original.call(instance, value);
    }

    /**
     * Changes the mipmap level on when the mipmap level is updated.
     */
    @WrapOperation(
        method = "updateMaxMipLevel",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/resources/model/ModelManager;maxMipmapLevels:I",
            opcode = Opcodes.PUTFIELD
        )
    )
    private void nt_mip_map$onUpdateMaxMipLevel(ModelManager instance, int value, Operation<Void> original)
    {
        if (CandyTweak.REMOVE_MIPMAP_TEXTURE.get())
            value = 0;

        original.call(instance, value);
    }
}
