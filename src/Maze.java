import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet sets = new DisjointSet(w.cells());
        int[] selected = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int wall = 0; wall < w.count() && count < selected.length; wall++) {
            if (sets.union(w.a()[wall], w.b()[wall])) {
                selected[count++] = wall;
            }
        }
        if (count != selected.length) {
            throw new IllegalArgumentException("The wall graph is not connected");
        }
        return selected;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        long[] order = new long[w.count()];
        for (int wall = 0; wall < w.count(); wall++) {
            order[wall] = ((long) w.cost()[wall] << 32) | (wall & 0xffffffffL);
        }
        Arrays.sort(order);

        DisjointSet sets = new DisjointSet(w.cells());
        int[] selected = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (long entry : order) {
            int wall = (int) entry;
            if (sets.union(w.a()[wall], w.b()[wall])) {
                selected[count++] = wall;
                if (count == selected.length) break;
            }
        }
        if (count != selected.length) {
            throw new IllegalArgumentException("The wall graph is not connected");
        }
        return selected;
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n == 0) return 0;

        int[] degree = new int[n];
        for (int wall = 0; wall < w.count(); wall++) {
            degree[w.a()[wall]]++;
            degree[w.b()[wall]]++;
        }
        int[][] incident = new int[n][];
        for (int cell = 0; cell < n; cell++) incident[cell] = new int[degree[cell]];
        int[] filled = new int[n];
        for (int wall = 0; wall < w.count(); wall++) {
            incident[w.a()[wall]][filled[w.a()[wall]]++] = wall;
            incident[w.b()[wall]][filled[w.b()[wall]]++] = wall;
        }

        boolean[] inTree = new boolean[n];
        PriorityQueue<Candidate> queue = new PriorityQueue<>();
        inTree[0] = true;
        int visited = 1;
        addCandidates(0, w, incident, inTree, queue);
        long total = 0;
        while (!queue.isEmpty() && visited < n) {
            Candidate candidate = queue.remove();
            if (inTree[candidate.cell]) continue;
            inTree[candidate.cell] = true;
            visited++;
            total += candidate.cost;
            addCandidates(candidate.cell, w, incident, inTree, queue);
        }
        if (visited != n) {
            throw new IllegalArgumentException("The wall graph is not connected");
        }
        return total;
    }

    private static void addCandidates(int cell, Input.Walls w, int[][] incident,
                                      boolean[] inTree, PriorityQueue<Candidate> queue) {
        for (int wall : incident[cell]) {
            int other = w.a()[wall] == cell ? w.b()[wall] : w.a()[wall];
            if (!inTree[other]) queue.add(new Candidate(other, w.cost()[wall]));
        }
    }

    private record Candidate(int cell, int cost) implements Comparable<Candidate> {
        @Override
        public int compareTo(Candidate other) {
            return Integer.compare(cost, other.cost);
        }
    }
}
