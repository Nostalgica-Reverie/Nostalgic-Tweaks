package mod.adrenix.nostalgic.util.client.timer;

import mod.adrenix.nostalgic.util.common.timer.TickTimer;

import java.util.HashSet;

public class ClientTimer implements TickTimer.Manager {
    /* Singleton */

    private static final ClientTimer INSTANCE = new ClientTimer();
    private final HashSet<TickTimer> timers = new HashSet<>();

    /* Fields */

    private ClientTimer() {
    }

    /* Constructor */

    public static ClientTimer getInstance() {
        return INSTANCE;
    }

    /* Methods */

    @Override
    public HashSet<TickTimer> getTimers() {
        return this.timers;
    }
}
