package mod.adrenix.nostalgic.util.client.search;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

interface LevenshteinDatabase<T>
{
    /**
     * Get a {@link List} of sorted {@link Map} entries based on normalized Levenshtein values.
     *
     * @param query     A string that represents a search query.
     * @param threshold A normalized threshold that is between 0.0 and 1.0.
     * @return A {@link List} of {@link Map} entries with sorted database values as the keys and a normalized (0.0-1.0)
     * score, where 0.0 represents no match and 1.0 represents a perfect match, as the values.
     */
    default List<Map.Entry<T, Double>> findEntries(String query, double threshold)
    {
        double cacheThreshold = this.getThreshold();
        this.setThreshold(threshold);

        List<Map.Entry<T, Double>> entries = this.levenshtein().apply(query.toLowerCase().trim());
        this.setThreshold(cacheThreshold);

        return entries;
    }

    /**
     * Get a {@link List} of sorted database values based on normalized Levenshtein values using the given threshold
     * amount.
     *
     * @param query     A string that represents a search query.
     * @param threshold A normalized threshold that is between 0.0 and 1.0.
     * @return A {@link List} of sorted values found from the database cache map.
     */
    default List<T> findValues(String query, double threshold)
    {
        return this.findEntries(query, threshold).stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Get a {@link List} of sorted database values based on normalized Levenshtein values using a threshold of
     * {@code 0.01}.
     *
     * @param query A string that represents a search query.
     * @return A {@link List} of sorted values found from the database cache map.
     */
    default List<T> findValues(String query)
    {
        return this.findValues(query, 0.01D);
    }

    /**
     * In information theory, linguistics, and computer science, the Levenshtein distance is a string metric for
     * measuring the difference between two sequences.
     * <p><br>
     * However, this implementation does not yield a metric. It instead yields a normalized value computed from a
     * Levenshtein distance divided by the length of the longest string. This gives better results for smaller query
     * strings.
     *
     * @return The {@link LevenshteinResult} instance used by this {@link LevenshteinDatabase} instance.
     */
    LevenshteinResult<T> levenshtein();

    /**
     * Each {@link LevenshteinDatabase} has a database {@link Map} of a large set of strings. The {@link #levenshtein()}
     * instance will use the cache map to perform lookup calculations using a given query.
     *
     * @return A {@link Map} using strings as the keys. Values are a list of objects that are associated with a key.
     */
    Map<String, List<T>> getDatabase();

    /**
     * Change the minimum threshold requirements that a search result must meet before it is added to the list of
     * results.
     *
     * @param threshold A normalized threshold that is between 0.0 and 1.0.
     */
    void setThreshold(double threshold);

    /**
     * @return The current threshold of the database.
     */
    double getThreshold();

    /**
     * This will reset a {@link LevenshteinDatabase}'s {@link Map} of cached strings to use for lookup. Use case is for
     * when the game's language is changed and the cache map needs updated with new translation strings.
     */
    default void reset()
    {
        this.getDatabase().clear();
    }
}
