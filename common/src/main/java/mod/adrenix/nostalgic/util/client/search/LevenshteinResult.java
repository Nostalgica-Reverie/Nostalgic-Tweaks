package mod.adrenix.nostalgic.util.client.search;

import mod.adrenix.nostalgic.util.client.search.algorithm.NormalizedLevenshtein;
import mod.adrenix.nostalgic.util.common.ThreadMaker;
import net.minecraft.Util;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class LevenshteinResult<T>
{
    /* Fields */

    private final Map<String, List<T>> map;
    private final double threshold;

    /* Constructor */

    private LevenshteinResult(Map<String, List<T>> map, double threshold)
    {
        this.map = map;
        this.threshold = threshold;
    }

    /* Static */

    /**
     * Prepares a {@link LevenshteinResult} instance to match the content of the given {@link Map} while considering the
     * given threshold. The matching will search for similar keys within the map. The result is backed by the map, so
     * each call to {@link #apply(String)} will reflect changes of the given map.
     *
     * @param map       A {@link Map} of strings to search through and returned values.
     * @param threshold The minimum Levenshtein result [0.0-1.0] required to be put in the result map.
     * @param <V>       The class type of the map values and returned values after searching.
     * @return A new {@link LevenshteinResult} instance to match the content of the given {@link Map} using the given
     * threshold.
     */
    public static <V> LevenshteinResult<V> with(Map<String, List<V>> map, double threshold)
    {
        return new LevenshteinResult<>(map, threshold);
    }

    /* Methods */

    /**
     * Apply a search query request.
     *
     * @param query The string to compare against the cache map.
     * @return A {@link List} of sorted {@link Map} entries based on normalized Levenshtein values.
     */
    public List<Map.Entry<T, Double>> apply(String query)
    {
        int sizeOfMap = this.map.size();

        ArrayList<Map.Entry<T, Double>> results = new ArrayList<>(sizeOfMap);
        HashMap<Map.Entry<T, Double>, String> pointers = new HashMap<>(sizeOfMap);

        Consumer<Map.Entry<T, Double>> processor = (entry) -> {
            double levenshtein = NormalizedLevenshtein.get(pointers.get(entry), query);

            if (levenshtein >= this.threshold)
                entry.setValue(levenshtein);
            else
                entry.setValue(-1.0D);
        };

        this.map.forEach((key, values) -> values.forEach(value -> {
            Map.Entry<T, Double> entry = new AbstractMap.SimpleEntry<>(value, 1.0D);

            results.add(entry);
            pointers.put(entry, key);
        }));

        if (query.isBlank())
            return results;

        int sizeOfResults = results.size();

        if (sizeOfResults > 1000)
        {
            HashSet<CompletableFuture<Void>> processes = new HashSet<>();
            int numberOfProcessors = ThreadMaker.getNumberOfProcessors();
            int chunkSize = sizeOfResults / numberOfProcessors;
            int leftOver = sizeOfResults % numberOfProcessors;

            for (int i = 0; i < numberOfProcessors; i++)
            {
                final int startIndex = (i * chunkSize);
                final int endIndex = startIndex + chunkSize + (i == numberOfProcessors - 1 ? leftOver : 0);

                CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> results.subList(startIndex, endIndex)
                        .forEach(processor), Util.backgroundExecutor());

                processes.add(future);
            }

            try
            {
                CompletableFuture.allOf(processes.toArray(CompletableFuture[]::new)).get();
            }
            catch (InterruptedException | ExecutionException exception)
            {
                throw new RuntimeException(exception);
            }
        }
        else
            results.forEach(processor);

        results.removeIf(entry -> entry.getValue() < 0.0D);
        results.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        return results;
    }
}
