package mod.adrenix.nostalgic.mixin.required;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.ByteBuffer;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin {
    /**
     * Allows non-png images to be loaded by the game. Ideally, this should only be allowed for the mod's compressed
     * panorama textures, but there is not a good way to track this yet.
     */
    @WrapWithCondition(
            method = "read(Lcom/mojang/blaze3d/platform/NativeImage$Format;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/platform/NativeImage;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/PngInfo;validateHeader(Ljava/nio/ByteBuffer;)V"
            )
    )
    private static boolean nt_required$onValidateImageHeader(ByteBuffer textureData) {
        return false;
    }
}
