package mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

class GithubJson {
    long version;
    Map<String, Supporter> supporters;

    static class Supporter {
        boolean member;
        String color;
        @Nullable String uuid;
        @Nullable String description;
        @Nullable String twitter;
        @Nullable String youtube;
        @Nullable String twitch;
        String[] links;
    }
}
