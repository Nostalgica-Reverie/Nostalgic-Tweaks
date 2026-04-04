package mod.adrenix.nostalgic.util.client.timer;

import mod.adrenix.nostalgic.mixin.access.TimerAccess;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.client.Minecraft;

public abstract class PartialTick {
    /**
     * This will be 1.0F if the level tick rate manager is frozen or is trying to catch up. This will also use the delta
     * tick residual if the game is paused. Use {@link #normal()} if the frozen effect should be ignored. Use
     * {@link #legacy()} if both of these effects are not desirable.
     *
     * @return Framerate based normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static float get() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    /**
     * This will ignore if the level tick rate manager is frozen or is trying to catch up. This will use the delta tick
     * residual if the game is paused. Use {@link #legacy()} if this effect is not desirable.
     *
     * @return Framerate based normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static float normal() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }

    /**
     * Get the legacy partial tick tracker that ignores if the game is frozen/paused.
     *
     * @return Framerate based normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static float legacy() {
        return ((TimerAccess) Minecraft.getInstance().getDeltaTracker()).nt$getPartialTick();
    }

    /**
     * @return Realtime based normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static float realtime() {
        return Minecraft.getInstance().getDeltaTracker().getRealtimeDeltaTicks();
    }
}
