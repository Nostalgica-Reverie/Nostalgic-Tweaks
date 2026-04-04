package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.util.Mth;

/**
 * Simple wrapper utility that reduces the overhead of creating a new thread.
 */
public abstract class ThreadMaker {
    /**
     * Create a new thread.
     *
     * @param name     The name of thread.
     * @param runnable Instructions to run in the thread.
     * @return A new {@link Thread} instance.
     */
    public static Thread create(String name, Runnable runnable) {
        return new Thread(name) {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    /**
     * Create a new thread with callback instructions.
     *
     * @param name     The name of thread.
     * @param runnable Instructions to run in the thread.
     * @param callback Instructions to run after the given runnable has finished. This will run on the new thread and is
     *                 guaranteed to execute even if an error occurs in the given runnable.
     * @return A new {@link Thread} instance.
     */
    public static Thread create(String name, Runnable runnable, Runnable callback) {
        return new Thread(name) {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    callback.run();
                }
            }
        };
    }

    /**
     * Get the number of processors available. This value will be between 1 and 255 inclusive.
     *
     * @return Get the maximum number of processors available to the Java virtual machine.
     */
    public static int getNumberOfProcessors() {
        String propertyMaxThreads = System.getProperty("max.bg.threads");
        int maxThreads = 255;

        if (propertyMaxThreads != null) {
            try {
                int parsedProperty = Integer.parseInt(propertyMaxThreads);

                if (parsedProperty >= 1 && parsedProperty <= 255)
                    maxThreads = parsedProperty;

                NostalgicTweaks.LOGGER.error("Wrong %s property value '%s'. Should be an integer value between 1 and %s.", "max.bg.threads", propertyMaxThreads, 255);
            } catch (NumberFormatException exception) {
                NostalgicTweaks.LOGGER.error("Could not parse %s property value '%s'. Should be an integer value between 1 and %s.", "max.bg.threads", propertyMaxThreads, 255);
            }
        }

        return Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, maxThreads);
    }
}
