package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ImageButton.class)
public interface ImageButtonAccess
{
    @Accessor("sprites")
    WidgetSprites nt$getSprites();
}
