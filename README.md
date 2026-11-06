# Assignment 4. Patient Zero

Student ID: 254319  
Name: Aisyerik  
Group: SE-2536

## Program output

The four personal input files were generated from student ID 254319. Strain A has 1074 letters and strain B has 1080 letters.

```text
Mutations: 83
Shared core: 1021
Marker hits: 7
First hit: 3774
Border length: 3
Shared words: 414
Tests needed: 38
Cheapest testing: 159
Longest chain: 28
```

All nine lines match the lab console. All three supplied examples (tiny, small and dense) pass the checker.

## Outbreak report

![Outbreak console showing all five missions correct](output/outbreak.png)

## Questions

### Mission 1. Mutations

`d[i][j]` is the smallest number of edits needed to turn the first i letters of A into the first j letters of B. The first row and column count insertions and deletions; every other cell takes the minimum of deleting a letter, inserting a letter, or using the diagonal with cost zero for equal letters and one for a replacement. The bottom-right cell gives 83 edits for my strains.

### Mission 2. Shared core

A subsequence keeps letters in their original order but may skip letters, while a substring must be continuous. My LCS has length 1021, although only 296 letters match at the same index, because an insertion or deletion shifts later letters and LCS can still match those letters at different indices. In the table, equal letters extend the diagonal result by one; otherwise I keep the larger result from the cell above or to the left.

### Mission 3. The marker

The prefix function stores the length of the longest proper prefix that is also a suffix for each prefix of the marker. On a mismatch, KMP uses this table to shorten the matched pattern prefix while keeping the text index moving forward, so it avoids repeating text comparisons from scratch. My marker is `TCCTTTCC` and its border length is 3 because `TCC` is both its beginning and end; KMP finds seven hits, starting at position 3774.

### Mission 4. Copied words

A hash is only a number, so two different words can have the same hash; comparing their letters prevents a collision from becoming a false match. I store B's window positions by hash and roll through A by removing the outgoing letter, multiplying by the base and adding the incoming letter; each A position counts at most once after a full letter match. If many unequal windows share a hash, many comparisons are needed and the worst-case cost can reach O(n*m*k), rather than the expected near-linear cost for a fixed window length.

### Mission 5. Whom to test

For person v, `dp[v][0]` is the minimum subtree cost when v is not tested, and `dp[v][1]` is the minimum when v is tested: the first adds the tested cost of every child, while the second starts with v's own price and adds the cheaper state of each child. I rebuild the chosen people from these states; for the longest chain, I combine the two longest downward paths at every person. A greedy choice can fail: on a star with a centre costing 9 and three leaves costing 1 each, testing the centre with the most links costs 9, but testing the three leaves costs only 3.

## AI use

I used OpenAI Codex to read the task, generate implementations of the mission methods, add explanatory comments, check the code, run the examples and benchmarks, capture the outbreak page, and draft this report. The code and report were generated with AI assistance. The starter Java files, example inputs and lab page were supplied with the assignment; no external website or book code was used.

## Timing (Mission 7)

Measured with the supplied Bench class using JDK 25. Each value is the average of three runs, in milliseconds.

| test | size | time (ms) |
|---|---:|---:|
| naive search | 20 000 | 164.3 |
| naive search | 40 000 | 255.6 |
| naive search | 80 000 | 496.1 |
| KMP | 20 000 | 6.0 |
| KMP | 40 000 | 3.4 |
| KMP | 80 000 | 0.9 |
| edit distance | 500 | 14.4 |
| edit distance | 1000 | 12.1 |
| edit distance | 2000 | 29.1 |
| edit distance | 4000 | 107.3 |

The naive times grow about 1.56x and 1.94x as the text doubles; the pattern length stays fixed at 2000, so these data do not demonstrate quadratic growth in text length alone. KMP's measured time falls and the first edit-distance measurement is also noisy because the supplied benchmark has no separate warm-up, so these small timings should not be read as growth rates; KMP's theoretical cost is O(n+m). At the larger edit-distance sizes, doubling both strain lengths increases time about 2.40x and 3.69x, approaching the expected fourfold growth from the quadratic table.

## Complexity

Here n is the length of A or the text, m is the length of B or the pattern, k is the window length, and v is the number of people. Space includes the arrays and returned results used by these implementations.

| algorithm | time | space | assumptions |
|---|---|---|---|
| Edit distance | O(n*m) | O(n*m) | Full table, unit-cost insertion, deletion and replacement. |
| LCS | O(n*m) | O(n*m) | Full table; skipped letters are allowed. |
| Prefix function | O(m) | O(m) | Border fallback uses the prefix table. |
| KMP | O(n+m) | O(n+m) | Includes prefix table and a hit buffer of length n; overlaps count. |
| Rabin-Karp (shared words) | O(n+m+q*k), worst O(n*m*k) | O(m) | q is the number of candidate windows verified; expected O(n+m) for fixed k and few collisions. All B positions are kept, and hashing uses fixed-size integer arithmetic. |
| Tree DP and reconstruction | O(v) | O(v) | Connected tree with v-1 links; includes recursion and state arrays. |
| Longest chain | O(v) | O(v) | Tree DFS with two downward lengths; recursion may reach v. |
