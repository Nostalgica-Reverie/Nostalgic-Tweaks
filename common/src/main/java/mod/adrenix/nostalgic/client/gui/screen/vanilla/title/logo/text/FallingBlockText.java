package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.text;

import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.ModAsset;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FallingBlockText extends SimplePreparableReloadListener<List<String>>
{
    /* Fields */

    public static final FlagHolder LOGO_CHANGED = FlagHolder.off();
    private static final ResourceLocation LOGO_LOCATION = ModAsset.get("texts/logo.txt");
    private static final FallingBlockText SINGLETON = new FallingBlockText();
    private final List<String> lines = new ArrayList<>();

    /* Singleton */

    public static FallingBlockText getInstance()
    {
        return SINGLETON;
    }

    /* Constructor */

    private FallingBlockText()
    {
    }

    /* Methods */

    @Override
    protected List<String> prepare(ResourceManager resourceManager, ProfilerFiller profiler)
    {
        List<String> lines;

        try (BufferedReader reader = Minecraft.getInstance().getResourceManager().openAsReader(LOGO_LOCATION))
        {
            lines = CollectionUtil.filterOut(reader.lines(), String::isBlank, String::isEmpty)
                .map(String::trim)
                .collect(Collectors.toCollection(ArrayList::new));
        }
        catch (IOException exception)
        {
            lines = new ArrayList<>();
        }

        return lines;
    }

    @Override
    protected void apply(List<String> lines, ResourceManager manager, ProfilerFiller profiler)
    {
        this.lines.clear();
        this.lines.addAll(lines);

        LOGO_CHANGED.enable();
    }

    /**
     * @return The {@link List} of lines of characters for the logo.
     */
    public List<String> logo()
    {
        return new ArrayList<>(this.lines);
    }

    /**
     * @return The number of lines in the logo.
     */
    public int size()
    {
        return this.lines.size();
    }

    /**
     * @return The size of the longest line in the logo.
     */
    public int longestLine()
    {
        int size = 0;

        for (String line : this.lines)
        {
            if (line.length() > size)
                size = line.length();
        }

        return size;
    }
}
