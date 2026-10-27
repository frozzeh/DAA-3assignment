import java.util.Arrays;

/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] outgoing = Graphs.slidesFrom(s);
        boolean[] seen = new boolean[n];
        int[] finishOrder = new int[n];
        int finished = 0;
        int[] stackCell = new int[n];
        int[] stackEdge = new int[n];

        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stackCell[0] = start;
            stackEdge[0] = 0;
            seen[start] = true;
            while (top >= 0) {
                int cell = stackCell[top];
                if (stackEdge[top] < outgoing[cell].length) {
                    int slide = outgoing[cell][stackEdge[top]++];
                    int next = s.to()[slide];
                    if (!seen[next]) {
                        seen[next] = true;
                        top++;
                        stackCell[top] = next;
                        stackEdge[top] = 0;
                    }
                } else {
                    finishOrder[finished++] = cell;
                    top--;
                }
            }
        }

        int[][] incoming = Graphs.slidesInto(s);
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zone = 0;
        for (int order = n - 1; order >= 0; order--) {
            int start = finishOrder[order];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            stackCell[0] = start;
            zoneOf[start] = zone;
            while (top >= 0) {
                int cell = stackCell[top--];
                for (int slide : incoming[cell]) {
                    int next = s.from()[slide];
                    if (zoneOf[next] == -1) {
                        zoneOf[next] = zone;
                        stackCell[++top] = next;
                    }
                }
            }
            zone++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        if (zoneCount == 0) return new long[]{0, 0};

        int[] edgeCount = new int[zoneCount];
        for (int slide = 0; slide < s.count(); slide++) {
            int fromZone = zoneOf[s.from()[slide]];
            int toZone = zoneOf[s.to()[slide]];
            if (fromZone != toZone) edgeCount[fromZone]++;
        }
        int[][] edges = new int[zoneCount][];
        for (int zone = 0; zone < zoneCount; zone++) edges[zone] = new int[edgeCount[zone]];
        int[] filled = new int[zoneCount];
        for (int slide = 0; slide < s.count(); slide++) {
            int fromZone = zoneOf[s.from()[slide]];
            int toZone = zoneOf[s.to()[slide]];
            if (fromZone != toZone) edges[fromZone][filled[fromZone]++] = slide;
        }

        long[] distance = new long[zoneCount];
        Arrays.fill(distance, Long.MAX_VALUE);
        distance[zoneOf[0]] = 0;
        int[] stages = new int[zoneCount];
        Arrays.fill(stages, 1);
        int longest = 1;

        for (int zone = 0; zone < zoneCount; zone++) {
            longest = Math.max(longest, stages[zone]);
            for (int slide : edges[zone]) {
                int toZone = zoneOf[s.to()[slide]];
                stages[toZone] = Math.max(stages[toZone], stages[zone] + 1);
                longest = Math.max(longest, stages[toZone]);
                if (distance[zone] != Long.MAX_VALUE) {
                    distance[toZone] = Math.min(distance[toZone], distance[zone] + s.time()[slide]);
                }
            }
        }

        return new long[]{longest, distance[zoneOf[s.n() - 1]]};
    }
}
