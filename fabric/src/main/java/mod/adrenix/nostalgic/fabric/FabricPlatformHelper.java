package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.services.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements PlatformHelper {
    private final FabricLoader loader = FabricLoader.getInstance();

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModPresent(String mod) {
        return loader.isModLoaded(mod);
    }

    @Override
    public boolean isDevEnvironment() {
        return loader.isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() {
        return loader.getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public Path getConfigFolder() {
        return loader.getConfigDir();
    }

    @Override
    public Path getGameDirectory() {
        return loader.getGameDir();
    }
}
