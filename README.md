# Assignment 3: Maze Escape

**Student ID:** 250653  
**Language:** Java 17 or newer  
**Maze size:** 12 × 12

## Build and run

Run these commands from the repository root:

```text
javac -d out src/*.java
java -cp out Main
java tools/Check.java
```

`Main` reads `data/walls.txt` and `data/slides.txt`. It prints the assignment results and generates the maze visualization in `output/maze.png` and `output/maze.html`.

## Program output

```text
Broken: 143
Kept: 121
Last broken wall: 209
MST cost: 42046
Heaviest broken wall: 786
Path length: 30
DFS visited: 119
Dead ends: 46
Zones: 12
Largest zone: 20
Is DAG: 0
Flow stages: 8
Fastest route cost: 19
```

## Mission questions

### Mission 0: Disjoint Set Union

`find` returns the representative root of the set containing an element, while `union` joins two different sets and reports whether a merge occurred. Path compression shortens parent chains during `find`, and union by size attaches the smaller tree below the larger tree, giving an amortized time of `O(α(n))` per operation.

### Mission 1: Build the maze

A connected acyclic graph on `R*C` cells is a tree, so it always contains exactly `R*C - 1` broken walls. The rectangular grid contains `R*(C-1) + C*(R-1)` inner walls; subtracting the broken walls leaves `(R-1)*(C-1)` kept walls.

### Mission 2: Cheapest maze

Kruskal and Prim both produce an MST cost of **42046**. Kruskal repeatedly accepts the cheapest wall that connects two different DSU components, while Prim repeatedly adds the cheapest wall crossing from the current tree to an unvisited cell; therefore both construct a minimum spanning tree of the same weighted connected graph.

### Mission 3: Escape

There is exactly one path between any two cells because the open maze is a tree: two distinct paths would form a cycle. The DFS uses explicit arrays as a stack instead of recursive calls, so its depth does not consume the Java call stack even for a 1000 × 1000 maze.

### Mission 4: Zones

The first Kosaraju pass records vertices by finishing time. Processing that order on the reversed graph keeps the second DFS inside one source component of the remaining condensation graph, so each traversal identifies exactly one SCC and the resulting zone numbers follow topological order. A directed graph has no cycle exactly when every SCC contains one cell, which is equivalent to `Zones` being equal to the number of cells.

### Mission 5: Fastest route

Every edge between different zones goes from a smaller zone number to a larger one, so processing zones once in numerical order processes every predecessor before its successors. Relaxing all condensation edges in this order computes both the longest zone chain and the cheapest route in `O(z + m)` time, where `z` is the number of zones and `m` is the number of slides.

## AI use

ChatGPT was used as a development assistant for algorithm implementation, debugging, testing, and README preparation. It generated substantial portions of the implementation and documentation under review during the development process.

## Timing (Mission 7)

Milliseconds, five runs each, measured with `java -cp out Bench`:

| Maze | Kruskal | Prim |
|---|---:|---:|
| 100 × 100 | 12 ms | 8 ms |
| 300 × 300 | 27 ms | 62 ms |
| 1000 × 1000 | 291 ms | 1357 ms |

Both algorithms take longer as the number of cells and walls increases, in line with their `O(m log m)` bounds. Prim was faster on the smallest input, but its priority-queue overhead grew substantially on the larger mazes; for the 1000 × 1000 input, Kruskal was about 4.7 times faster in this run.

## Complexity

| Algorithm | Time | Space | Assumptions |
|---|---|---|---|
| DSU `find` and `union` | `O(α(n))` amortized | `O(n)` | Union by size and path compression |
| Kruskal | `O(m log m)` | `O(n + m)` | Sorting all walls, then DSU operations |
| Prim | `O(m log m)` | `O(n + m)` | Binary priority queue and adjacency lists |
| Iterative DFS | `O(n + m)` | `O(n + m)` | Explicit stack; for a maze tree, `m = n - 1` |
| Kosaraju | `O(n + m)` | `O(n + m)` | Two iterative DFS passes and both graph directions |
| Route in the zone graph | `O(z + m)` | `O(z + m)` | Zones are already in topological order |

## Visualization

![Maze visualization](output/maze.png)

The interactive version is available at [`output/maze.html`](output/maze.html).
