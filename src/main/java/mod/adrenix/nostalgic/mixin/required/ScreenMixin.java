package mod.adrenix.nostalgic.mixin.required;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    /**
     * Clears a {@link DynamicScreen}'s dynamic widgets if this screen implements that interface.
     */
    @Inject(
            method = "clearWidgets",
            at = @At(value = "HEAD")
    )
    private void nt_required$onClearWidgets(CallbackInfo callback) {
        if (this instanceof DynamicScreen<?> screen)
            screen.getWidgets().clear();
    }

    /**
     * Changes the screen's children if this implements the {@link DynamicScreen} interface.
     */
    @ModifyReturnValue(
            method = "children",
            at = @At("RETURN")
    )
    private List<? extends GuiEventListener> nt_required$modifyChildren(List<? extends GuiEventListener> children) {
        if (this instanceof DynamicScreen<?> screen)
            return screen.getWidgets();

        return children;
    }
}
