/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        DisjointSet chain = new DisjointSet(10_000);
        for (int i = 1; i < 10_000; i++) chain.union(i - 1, i);
        check(chain.connected(0, 9_999), "large chain is connected");
        check(chain.count() == 1, "one set after joining a large chain");
        check(!chain.union(5_000, 5_000), "union of an element with itself returns false");
        check(chain.count() == 1, "self-union does not change the count");

        DisjointSet groups = new DisjointSet(8);
        groups.union(0, 1); groups.union(2, 3); groups.union(4, 5); groups.union(6, 7);
        groups.union(0, 2); groups.union(4, 6); groups.union(0, 4);
        check(groups.count() == 1, "count is correct after balanced unions");
        check(groups.connected(1, 7), "balanced unions join all elements");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
