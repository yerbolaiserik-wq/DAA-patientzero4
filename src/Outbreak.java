import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];
        if (n == 0) return tested;
        long[][] dp = new long[n][2];
        testingCosts(0, -1, nb, cost, dp);
        chooseTests(0, -1, dp[0][1] <= dp[0][0], nb, dp, tested);
        return tested;
    }

    /** Compute costs from the leaves up; state 0 skips v, state 1 tests v. */
    private static void testingCosts(int v, int parent, int[][] nb, int[] cost, long[][] dp) {
        dp[v][1] = cost[v];
        for (int child : nb[v]) {
            if (child == parent) continue;
            testingCosts(child, v, nb, cost, dp);
            dp[v][0] += dp[child][1]; // If v is skipped, its child must cover the link.
            dp[v][1] += Math.min(dp[child][0], dp[child][1]);
        }
    }

    /** Rebuild a minimum-cost choice, forcing children to be tested if v is skipped. */
    private static void chooseTests(int v, int parent, boolean take, int[][] nb,
                                    long[][] dp, boolean[] tested) {
        tested[v] = take;
        for (int child : nb[v]) {
            if (child == parent) continue;
            boolean takeChild = !take || dp[child][1] <= dp[child][0];
            chooseTests(child, v, takeChild, nb, dp, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n == 0) return 0;
        int[] best = {0};
        chainHeight(0, -1, nb, best);
        return best[0];
    }

    /** Return the longest way down and update the best path through each vertex. */
    private static int chainHeight(int v, int parent, int[][] nb, int[] best) {
        int first = 0;
        int second = 0;
        for (int child : nb[v]) {
            if (child == parent) continue;
            int length = 1 + chainHeight(child, v, nb, best);
            if (length > first) {
                second = first;
                first = length;
            } else if (length > second) {
                second = length;
            }
        }
        best[0] = Math.max(best[0], first + second);
        return first;
    }
}
