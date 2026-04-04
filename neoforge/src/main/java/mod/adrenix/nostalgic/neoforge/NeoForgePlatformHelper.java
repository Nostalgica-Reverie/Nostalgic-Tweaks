package mod.adrenix.nostalgic.neoforge;

import mod.adrenix.nostalgic.services.PlatformHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    public boolean isModPresent(String mod) {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(mod) != null;
    }

    @Override
    public boolean isDevEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getCurrent().getDist() == Dist.CLIENT;
    }

    @Override
    public Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Path getGameDirectory() {
        return FMLLoader.getCurrent().getGameDir();
    }
}
