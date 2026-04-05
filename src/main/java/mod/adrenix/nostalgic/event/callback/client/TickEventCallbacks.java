package mod.adrenix.nostalgic.event.callback.client;

import mod.adrenix.nostalgic.client.gui.screen.home.NTPanorama;
import mod.adrenix.nostalgic.util.client.animate.Animator;
import mod.adrenix.nostalgic.util.client.timer.ClientTimer;
import net.minecraft.client.Minecraft;

public abstract class TickEventCallbacks {
    /**
     * Instructions to perform at the start of every tick.
     *
     * @param minecraft The {@link Minecraft} singleton instance.
     */
    public static void onPreTick(Minecraft minecraft) {
        ClientTimer.getInstance().onTick();
        Animator.onTick();
        NTPanorama.onTick();

//        LightingHelper.onTick();
    }
}
