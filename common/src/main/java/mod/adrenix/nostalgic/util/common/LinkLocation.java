package mod.adrenix.nostalgic.util.common;

/**
 * This utility stores URLs that are used by the mod.
 */
public interface LinkLocation
{
    String GITHUB = "https://github.com/Adrenix/Nostalgic-Tweaks/";
    String DISCORD = "https://discord.gg/Un7b9AWSsu";
    String KOFI = "https://ko-fi.com/adrenix";
    String GOLDEN_DAYS = "https://github.com/PoeticRainbow/golden-days/releases";
    String DOWNLOAD = "https://modrinth.com/mod/nostalgic-tweaks/versions";
    String SUPPORTERS = "https://raw.githubusercontent.com/Adrenix/Nostalgic-Tweaks/data/supporters-v2.json";

    static String getSupporterFace(String uuid)
    {
        return String.format("https://crafthead.net/avatar/%s/8", uuid);
    }
}
