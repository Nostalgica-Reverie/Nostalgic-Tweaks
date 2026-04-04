package mod.adrenix.nostalgic.mixin.required;

import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerEventHandler.class)
public abstract class AbstractContainerEventHandlerMixin {
    /**
     * Provides additional focusing logic if this screen implements {@link DynamicScreen}.
     */
    @Inject(
            method = "setFocused",
            at = @At("HEAD")
    )
    private void nt_required$onSetFocused(@Nullable GuiEventListener focused, CallbackInfo callback) {
        if (this instanceof DynamicScreen<?> screen)
            screen.setDynamicFocus(focused);
    }
}
