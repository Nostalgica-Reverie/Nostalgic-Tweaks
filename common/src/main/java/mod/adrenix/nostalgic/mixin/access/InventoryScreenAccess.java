package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryScreen.class)
public interface InventoryScreenAccess
{
    @Accessor("RECIPE_BUTTON_LOCATION")
    static ResourceLocation NT$RECIPE_BUTTON_LOCATION()
    {
        return new ResourceLocation("[N.T] How did you get here?");
    }
}
