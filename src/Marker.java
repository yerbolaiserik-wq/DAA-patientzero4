import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int[] pi = new int[p.length()];
        for (int i = 1; i < p.length(); i++) {
            int j = pi[i - 1];
            // Try shorter borders until the next letter matches.
            while (j > 0 && p.charAt(i) != p.charAt(j)) j = pi[j - 1];
            if (p.charAt(i) == p.charAt(j)) j++;
            pi[i] = j;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        // The empty word occurs at every boundary, including after the text.
        if (p.isEmpty()) {
            int[] all = new int[text.length() + 1];
            for (int i = 0; i < all.length; i++) all[i] = i;
            return all;
        }
        int[] pi = prefixFunction(p);
        int[] hits = new int[text.length()];
        int count = 0;
        int j = 0;
        for (int i = 0; i < text.length(); i++) {
            while (j > 0 && text.charAt(i) != p.charAt(j)) j = pi[j - 1];
            if (text.charAt(i) == p.charAt(j)) j++;
            if (j == p.length()) {
                hits[count++] = i - p.length() + 1;
                j = pi[j - 1]; // Keep the border so overlapping matches count.
            }
        }
        return Arrays.copyOf(hits, count);
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k < 0) throw new IllegalArgumentException("Negative word length");
        if (k == 0) return a.length() + 1;
        if (k > a.length() || k > b.length()) return 0;
        final long base = 257;
        final long mod = 1_000_000_007;
        long power = 1;
        for (int i = 1; i < k; i++) power = power * base % mod;

        // Keep every position in each hash bucket: different words can collide.
        Map<Long, List<Integer>> positions = new HashMap<>();
        long hash = 0;
        for (int i = 0; i < k; i++) hash = (hash * base + b.charAt(i)) % mod;
        for (int i = 0; i <= b.length() - k; i++) {
            positions.computeIfAbsent(hash, key -> new ArrayList<>()).add(i);
            if (i < b.length() - k) {
                hash = (hash - b.charAt(i) * power % mod + mod) % mod;
                hash = (hash * base + b.charAt(i + k)) % mod;
            }
        }

        int count = 0;
        hash = 0;
        for (int i = 0; i < k; i++) hash = (hash * base + a.charAt(i)) % mod;
        for (int i = 0; i <= a.length() - k; i++) {
            List<Integer> candidates = positions.get(hash);
            if (candidates != null) {
                for (int start : candidates) {
                    int j = 0;
                    while (j < k && a.charAt(i + j) == b.charAt(start + j)) j++;
                    if (j == k) {
                        count++;
                        break; // Count this position in A only once.
                    }
                }
            }
            if (i < a.length() - k) {
                hash = (hash - a.charAt(i) * power % mod + mod) % mod;
                hash = (hash * base + a.charAt(i + k)) % mod;
            }
        }
        return count;
    }
}
