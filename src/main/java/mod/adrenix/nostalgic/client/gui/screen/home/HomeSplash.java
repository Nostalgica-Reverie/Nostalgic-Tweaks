package mod.adrenix.nostalgic.client.gui.screen.home;

import mod.adrenix.nostalgic.util.common.CalendarUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.ModAsset;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeSplash extends SimplePreparableReloadListener<List<String>> {
    /* Fields */

    private static final HomeSplash SINGLETON = new HomeSplash();
    private static final Identifier SPLASHES_LOCATION = ModAsset.get("texts/splashes.txt");
    private static final RandomSource RANDOM_SOURCE = RandomSource.create();
    private final List<String> splashes = new ArrayList<>();

    /* Singleton */

    private HomeSplash() {
    }

    /* Constructor */

    public static HomeSplash getInstance() {
        return SINGLETON;
    }

    /* Methods */

    @Override
    protected @NotNull List<String> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<String> splashes;

        try (BufferedReader reader = Minecraft.getInstance().getResourceManager().openAsReader(SPLASHES_LOCATION)) {
            splashes = CollectionUtil.filterOut(reader.lines(), String::isBlank, String::isEmpty)
                    .map(String::trim)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException exception) {
            splashes = new ArrayList<>();
        }

        return splashes;
    }

    @Override
    protected void apply(List<String> splashes, ResourceManager manager, ProfilerFiller profiler) {
        this.splashes.clear();
        this.splashes.addAll(splashes);
    }

    /**
     * @return An N.T splash message.
     */
    public String get() {
        if (CalendarUtil.isToday(3, 13))
            return "Happy Birthday, N.T!";

        if (CalendarUtil.isToday(4, 1))
            return "Happy birthday, Spoono!";

        if (CalendarUtil.isToday(6, 1))
            return "Happy birthday, Notch!";

        if (CalendarUtil.isToday(10, 31))
            return "Added Herobrine...";

        if (CalendarUtil.isToday(12, 24))
            return "Merry Christmas Eve!";

        if (CalendarUtil.isToday(12, 25))
            return "Merry Christmas!";

        if (CalendarUtil.isToday(1, 1))
            return "Happy new year!";

        if (this.splashes.isEmpty())
            return "";

        return this.splashes.get(RANDOM_SOURCE.nextInt(this.splashes.size()));
    }
}
