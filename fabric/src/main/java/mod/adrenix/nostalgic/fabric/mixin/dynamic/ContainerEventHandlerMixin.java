package mod.adrenix.nostalgic.fabric.mixin.dynamic;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin
{
    /**
     * If this implements {@link DynamicScreen} then changes the results based on its key press helper.
     */
    @ModifyReturnValue(
        method = "keyPressed",
        at = @At("RETURN")
    )
    private boolean nt_required$onKeyPressed(boolean isKeyPressed, int keyCode, int scanCode, int modifiers)
    {
        if (!isKeyPressed && this instanceof DynamicScreen<?> helper)
            return helper.isKeyPressed(keyCode, scanCode, modifiers);

        return isKeyPressed;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its key release helper.
     */
    @ModifyReturnValue(
        method = "keyReleased",
        at = @At("RETURN")
    )
    private boolean nt_required$onKeyReleased(boolean isKeyReleased, int keyCode, int scanCode, int modifiers)
    {
        if (!isKeyReleased && this instanceof DynamicScreen<?> helper)
            return helper.isKeyReleased(keyCode, scanCode, modifiers);

        return isKeyReleased;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse clicked helper.
     */
    @ModifyReturnValue(
        method = "mouseClicked",
        at = @At("RETURN")
    )
    default boolean nt_required$onMouseClicked(boolean isMouseClicked, double mouseX, double mouseY, int button)
    {
        if (!isMouseClicked && this instanceof DynamicScreen<?> helper)
            return helper.isMouseClicked(mouseX, mouseY, button);

        return isMouseClicked;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse released helper.
     */
    @ModifyReturnValue(
        method = "mouseReleased",
        at = @At("RETURN")
    )
    default boolean nt_required$onMouseReleased(boolean isMouseReleased, double mouseX, double mouseY, int button)
    {
        if (!isMouseReleased && this instanceof DynamicScreen<?> helper)
            return helper.isMouseReleased(mouseX, mouseY, button);

        return isMouseReleased;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse dragged helper.
     */
    @ModifyReturnValue(
        method = "mouseDragged",
        at = @At("RETURN")
    )
    default boolean nt_required$onMouseDragged(boolean isMouseDragged, double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (!isMouseDragged && this instanceof DynamicScreen<?> helper)
            return helper.isMouseDragged(mouseX, mouseY, button, dragX, dragY);

        return isMouseDragged;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse scrolled helper.
     */
    @ModifyReturnValue(
        method = "mouseScrolled",
        at = @At("RETURN")
    )
    default boolean nt_required$onMouseScrolled(boolean isMouseScrolled, double mouseX, double mouseY, double deltaY)
    {
        if (!isMouseScrolled && this instanceof DynamicScreen<?> helper)
            return helper.isMouseScrolled(mouseX, mouseY, deltaY);

        return isMouseScrolled;
    }
}
