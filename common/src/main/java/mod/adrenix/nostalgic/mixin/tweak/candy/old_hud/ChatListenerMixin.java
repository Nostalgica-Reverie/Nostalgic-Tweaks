package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.multiplayer.chat.ChatListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin
{
    /**
     * Forcefully moves system messages to chat based on tweak context.
     */
    @ModifyVariable(
        argsOnly = true,
        method = "handleSystemMessage",
        at = @At("HEAD")
    )
    private boolean nt_old_hud$modifyIsOverlayOnSystemMessage(boolean isOverlay)
    {
        if (CandyTweak.MOVE_MESSAGES_TO_CHAT.get())
            return false;

        return isOverlay;
    }
}
