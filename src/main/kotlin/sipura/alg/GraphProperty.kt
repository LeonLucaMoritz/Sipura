package sipura.alg

import sipura.graphs.SimpleGraph
import kotlin.math.max

object GraphProperty {

    /**
     *
     * @see <a href="https://en.wikipedia.org/wiki/Tree_(graph_theory)">Definition of a tree</a>
     *
     *
     * @return true if [g] is a tree, false otherwise
     */
    fun <V> isTree(g: SimpleGraph<V>): Boolean =
        (g.m == g.n - 1) && Connectivity.isConnected(g) // fail fast: checking edge count runs in constant time

    /**
     * runtime: O( 1 )  ->  Is constant because we know that a complete graph has n * (n - 1) / 2 edges.
     * This term can be computed and checked in constant time.
     *
     * @return True if [g] is complete, meaning that every vertex is connected to every other vertex.
     */
    fun <V> isComplete(g: SimpleGraph<V>): Boolean = g.m == g.n * (g.n - 1) / 2

    /**
     * @return True if [g] is acyclic, meaning that there is no path of length greater 0 from a vertex to itself.
     */
    fun <V> isAcyclic(g: SimpleGraph<V>): Boolean =
        g.m == g.n - Connectivity.numberOfConnectedComponents(g)

    /**
     * @throws IllegalArgumentException if [g] is empty or if [k] is negative
     *
     * @return True if every vertex in [g] has degree [k], false otherwise.
     */
    fun <V> isKRegular(g: SimpleGraph<V>, k: Int): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Regularity for empty graph doesn't really make sense.")
        if (k < 0) throw IllegalArgumentException("k-regularity is not defined for negative values of k.")

        return g.V.all { g.degreeOf(it) == k }
    }

    /**
     * A graph is cubic if every vertex has degree 3. This is a special case of [isKRegular] with k = 3.
     *
     * @return True if every vertex in [g] has degree 3, false otherwise.
     */
    fun <V> isCubic(g: SimpleGraph<V>): Boolean = isKRegular(g, 3)

    /**
     * A graph is bipartite if its vertices can be partitioned into two disjoint sets such that every edge connects
     * a vertex in one set to a vertex in the other set.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bipartite_graph">Wikipedia: Bipartite graph</a>
     *
     * @return True if [g] is bipartite, false otherwise.
     *
     */
    fun <V> isBipartite(g: SimpleGraph<V>): Boolean {
        val components: MutableCollection<MutableSet<V>> = Connectivity.listAllConnectedComponents(g).values

        for (component in components) {
            val layerIter = Traversal.breadthFirstSearchLayerIterator(g, component.first())

            val sides = HashMap<V, Int>()
            var toggle = 1

            for (layer in layerIter) {
                for (v in layer) {
                    sides[v] = toggle
                    if (g.neighbors(v).any { sides.getOrDefault(it, 0) == toggle }) {
                        return false
                    }
                }
                toggle = -toggle
            }
        }
        return true
    }

    /**
     * Calculates the h-index of the given graph [g].
     *
     * The h-index is defined as the largest natural number *h* such that [g] has at least *h* vertices
     * with degree at least *h*.
     *
     * Runtime: O(n)
     *
     * @return The h-index of [g].
     */
    fun <V> hIndex(g: SimpleGraph<V>): Int {
        if (g.n == 0) throw IllegalArgumentException()

        val arr = IntArray(g.maxDegree() + 2) // Indices are: 0, 1, ..., maxDegree, maxDegree+1
        for (v in g.V) arr[g.degreeOf(v)] += 1

        for (i in g.maxDegree() downTo 0) {
            arr[i] += arr[i + 1] // arr[i] contains number of vertices with degree at least i
            if (arr[i] >= i) return i
        }

        throw RuntimeException("Shouldn't reach this part as the h-index is always at least 0 per definition")
    }

    /**
     *  Checks if the graph has no triangles.
     *
     *  @return true if the graph has no triangles, false otherwise.
     */
    fun <V> isTriangleFree(g: SimpleGraph<V>): Boolean {
        for (v in g.V) {
            val neighbors = g.neighbors(v)
            for (u in neighbors) {
                for (w in neighbors) {
                    if (u != w && g.hasEdge(u, w)) return false
                }
            }
        }
        return true
    }

    /**
     * Calculates the diameter of the given graph [g] by performing a breadth-first-search from every vertex
     * in the graph.
     *
     * Runtime: O(n * m)
     * @return the diameter of the graph. 0 if the graph is empty. -1 if the graph is not connected.
     */
    fun <V> diameter(g: SimpleGraph<V>): Int {
        if (g.V.isEmpty()) return 0
        if (!Connectivity.isConnected(g)) return -1
        var diameter = 0
        for (v in g.V) {
            var dist = -1
            for (l in Traversal.breadthFirstSearchLayerIterator(g, v)) {
                dist++
            }
            diameter = max(diameter, dist)
        }
        return diameter
    }

    /**
     * Calculates the smallest degree of any vertex in the given graph [g].
     *
     * Runtime: O(n)
     * @throws NoSuchElementException if the graph does not contain any vertices.
     * @return the smallest degree of any vertex in the graph.
     */
    fun <V> minDegree(g: SimpleGraph<V>): Int {
        return g.V.minOf { g.degreeOf(it) }
    }

    /**
     * Calculates the average degree of all vertices in the given graph [g].
     *
     * Runtime: O(n)
     * @throws NoSuchElementException if the graph does not contain any vertices.
     * @return the average degree of all vertex in the graph.
     */
    fun <V> averageDegree(g: SimpleGraph<V>): Float {
        if (g.V.isEmpty()) throw NoSuchElementException("The given graph is empty.")
        return g.V.sumOf { g.degreeOf(it) }.toFloat() / g.V.size.toFloat()
    }
}
