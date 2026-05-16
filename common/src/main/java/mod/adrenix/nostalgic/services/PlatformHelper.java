package mod.adrenix.nostalgic.services;

import java.nio.file.Path;

public interface PlatformHelper {
    String getPlatformName();

    boolean isModPresent(String mod);

    boolean isDevEnvironment();

    boolean isClient();

    Path getConfigFolder();

    Path getGameDirectory();
}
