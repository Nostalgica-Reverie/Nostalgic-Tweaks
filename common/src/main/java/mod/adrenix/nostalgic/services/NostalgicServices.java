package mod.adrenix.nostalgic.services;

import mod.adrenix.nostalgic.NostalgicTweaks;

import java.util.ServiceLoader;

public class NostalgicServices {
    public static PlatformHelper PLATFORM = loadService(PlatformHelper.class);

    private static <T> T loadService(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
