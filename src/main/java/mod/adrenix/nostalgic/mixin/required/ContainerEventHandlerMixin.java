package mod.adrenix.nostalgic.mixin.required;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {
    /**
     * If this implements {@link DynamicScreen} then changes the results based on its key press helper.
     */
    @ModifyReturnValue(
            method = "keyPressed",
            at = @At("RETURN")
    )
    private boolean nt_required$onKeyPressed(boolean isKeyPressed, KeyEvent keyEvent) {
        if (!isKeyPressed && this instanceof DynamicScreen<?> screen)
            return screen.isKeyPressed(keyEvent);

        return isKeyPressed;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its key release helper.
     */
    @ModifyReturnValue(
            method = "keyReleased",
            at = @At("RETURN")
    )
    private boolean nt_required$onKeyReleased(boolean isKeyReleased, KeyEvent keyEvent) {
        if (!isKeyReleased && this instanceof DynamicScreen<?> helper)
            return helper.isKeyReleased(keyEvent);

        return isKeyReleased;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse clicked helper.
     */
    @ModifyReturnValue(
            method = "mouseClicked",
            at = @At("RETURN")
    )
    default boolean nt_required$onMouseClicked(boolean isMouseClicked, MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseClicked && this instanceof DynamicScreen<?> screen)
            return screen.isMouseClicked(event, doubleClick);

        return isMouseClicked;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse released helper.
     */
    @ModifyReturnValue(
            method = "mouseReleased",
            at = @At("RETURN")
    )
    default boolean nt_required$onMouseReleased(boolean isMouseReleased, MouseButtonEvent event) {
        if (!isMouseReleased && this instanceof DynamicScreen<?> screen)
            return screen.isMouseReleased(event);

        return isMouseReleased;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse dragged helper.
     */
    @ModifyReturnValue(
            method = "mouseDragged",
            at = @At("RETURN")
    )
    default boolean nt_required$onMouseDragged(boolean isMouseDragged, MouseButtonEvent event, double dragX, double dragY) {
        if (!isMouseDragged && this instanceof DynamicScreen<?> screen)
            return screen.isMouseDragged(event, dragX, dragY);

        return isMouseDragged;
    }

    /**
     * If this implements {@link DynamicScreen} then changes the results based on its mouse scrolled helper.
     */
    @ModifyReturnValue(
            method = "mouseScrolled",
            at = @At("RETURN")
    )
    default boolean nt_required$onMouseScrolled(boolean isMouseScrolled, double mouseX, double mouseY, double deltaX, double deltaY) {
        if (!isMouseScrolled && this instanceof DynamicScreen<?> helper)
            return helper.isMouseScrolled(mouseX, mouseY, deltaX, deltaY);

        return isMouseScrolled;
    }
}
