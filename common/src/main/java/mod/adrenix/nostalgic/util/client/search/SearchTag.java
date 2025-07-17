package mod.adrenix.nostalgic.util.client.search;

import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.ChatFormatting;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Search tags filters out tweaks within the {@link TweakDatabase}. For example, a search query with the {@code #client}
 * tag will remove all tweaks that are not controlled by the client. Then, any results after the filter is applied will
 * be returned.
 */
public enum SearchTag
{
    NEW(Tweak::isNew, ChatFormatting.RED, false),
    OLD(Tweak::isOld, ChatFormatting.GRAY, false),
    CLIENT(Tweak::isClient, ChatFormatting.AQUA, false),
    SERVER(Tweak::isServer, ChatFormatting.YELLOW, false),
    DYNAMIC(Tweak::isDynamic, ChatFormatting.DARK_PURPLE, false),
    CONFLICT(Tweak::isConflictOrFail, ChatFormatting.DARK_RED, true),
    RESET(Tweak::isCacheNotDefault, ChatFormatting.GOLD, true),
    SAVE(Tweak::isAnyCacheSavable, ChatFormatting.GREEN, true);

    /* Fields */

    final Map<String, List<Tweak<?>>> map;
    final Predicate<Tweak<?>> predicate;
    final ChatFormatting color;
    final boolean recalculate;

    /* Constructor */

    SearchTag(Predicate<Tweak<?>> predicate, ChatFormatting color, boolean recalculate)
    {
        this.map = new LinkedHashMap<>();
        this.color = color;
        this.predicate = predicate;
        this.recalculate = recalculate;

        this.init();
    }

    /* Static */

    /**
     * @return A {@link Stream} of all {@link SearchTag} that can be used.
     */
    public static Stream<SearchTag> stream()
    {
        return Arrays.stream(SearchTag.values());
    }

    /**
     * Get an optional search tag based on the given query.
     *
     * @param query A string query.
     * @return An {@link Optional} {@link SearchTag}.
     */
    public static Optional<SearchTag> get(String query)
    {
        String extracted = TextUtil.extract(query, "#\\w+");

        if (extracted.isEmpty())
            return Optional.empty();

        return SearchTag.stream().filter(tag -> tag.toString().equals(extracted.replace("#", ""))).findFirst();
    }

    /**
     * Check whether the given query is invalid. If the string query does not start with {@code #}, then this method
     * will always yield {@code false}. Otherwise, it will return {@code true} if a search tag was found.
     *
     * @param query A search query.
     * @return Whether the query tag is valid.
     */
    public static boolean isInvalid(String query)
    {
        if (query.startsWith("#"))
            return SearchTag.get(query).isEmpty();

        return false;
    }

    /* Methods */

    void init()
    {
        if (!this.map.isEmpty())
            this.map.clear();

        TweakPool.filter(this.predicate, Tweak::isNotIgnored).forEachOrdered(tweak -> {
            String key = tweak.getTranslation().getString().toLowerCase();

            this.map.computeIfAbsent(key, str -> new ArrayList<>()).add(tweak);
        });
    }

    Map<String, List<Tweak<?>>> getMap()
    {
        if (this.recalculate)
            this.init();

        return this.map;
    }

    /**
     * Start a search query for this tag.
     *
     * @return A query in the form of {@code "#this "}.
     */
    public String query()
    {
        return "#" + this + " ";
    }

    /**
     * Apply a search query to this tag.
     *
     * @param query The query to lookup.
     * @return The search tag with the query appended in the form of {@code "#this query"}.
     */
    public String query(String query)
    {
        return "#" + this + " " + query;
    }

    /**
     * @return A {@link Predicate} that tests a {@link Tweak} to see if it belongs in this tag.
     */
    public Predicate<Tweak<?>> getPredicate()
    {
        return this.predicate;
    }

    /**
     * Get a translatable component description of what this tag searches for.
     *
     * @return A {@link Translation} instance.
     */
    public Translation getDescription()
    {
        return Lang.literal("gui.nostalgic_tweaks.search_tag.info." + this);
    }

    /**
     * @return The {@link ChatFormatting} assigned to this tag.
     */
    public ChatFormatting getColor()
    {
        return this.color;
    }

    /**
     * @return The string form of {@link #getDescription()}.
     */
    public String toDescription()
    {
        return this.getDescription().get().getString();
    }

    @Override
    public String toString()
    {
        return super.toString().toLowerCase(Locale.ROOT);
    }
}
