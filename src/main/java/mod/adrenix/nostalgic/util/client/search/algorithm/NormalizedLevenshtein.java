/*
 * The MIT License
 *
 * Copyright 2015 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package mod.adrenix.nostalgic.util.client.search.algorithm;

/**
 * Original author Thibault Debatty (<i>see package README for repository details</i>)
 *
 * <p><br>Some changes have been made to the original code, such as parameters being renamed and score adjustments
 * based on string lengths and similar string matching.
 */
public abstract class NormalizedLevenshtein {
    public static double get(final String entry, final String query) {
        if (entry == null)
            throw new NullPointerException("entry must not be null");

        if (query == null)
            throw new NullPointerException("query must not be null");

        if (entry.equals(query))
            return 1.0D;

        int maxLength = Math.max(entry.length(), query.length());
        double containsBoost = 1.0D;
        double startsWithBoost = 1.0D;

        if (maxLength == 0)
            return 0.0D;

        if (entry.startsWith(query))
            startsWithBoost = 2.5D;

        if (entry.length() > 2 && query.length() > 2 && entry.contains(query))
            containsBoost = 1.7D;

        double levenshtein = 1.0D - (distance(entry, query) / maxLength);

        if (levenshtein == 1.0D)
            return 1.0D;

        levenshtein *= startsWithBoost;

        if (levenshtein >= 1.0D)
            return 0.99D;

        levenshtein *= containsBoost;

        if (levenshtein >= 1.0D)
            return 0.98D;

        return levenshtein;
    }

    private static double distance(final String entry, final String query) {
        if (entry == null)
            throw new NullPointerException("entry must not be null");

        if (query == null)
            throw new NullPointerException("query must not be null");

        if (entry.equals(query))
            return 0;

        if (entry.isEmpty())
            return query.length();

        if (query.isEmpty())
            return entry.length();

        // create two work vectors of integer distances
        int[] v0 = new int[query.length() + 1];
        int[] v1 = new int[query.length() + 1];
        int[] vTemp;

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++)
            v0[i] = i;

        for (int i = 0; i < entry.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0
            // first element of v1 is A[i+1][0]
            // edit distance is deleted (i+1) chars from s to match empty t
            v1[0] = i + 1;

            int minV1 = v1[0];

            // use formula to fill in the rest of the row
            for (int j = 0; j < query.length(); j++) {
                int cost = 1;

                if (entry.charAt(i) == query.charAt(j))
                    cost = 0;

                v1[j + 1] = Math.min(
                        v1[j] + 1,          // Cost of insertion
                        Math.min(
                                v0[j + 1] + 1,  // Cost of remove
                                v0[j] + cost)); // Cost of substitution

                minV1 = Math.min(minV1, v1[j + 1]);
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            // System.arraycopy(v1, 0, v0, 0, v0.length);

            // Flip references to current and previous row
            vTemp = v0;
            v0 = v1;
            v1 = vTemp;

        }

        return v0[query.length()];
    }
}
