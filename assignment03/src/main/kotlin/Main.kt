import kotlin.math.min

/**
 * Create a Kotlin class to represent a directed, weighted graph
 * Note: I kept the addVertex function from the original graph class so i could call it for unit tests
 */

class WeightedGraph<VertexType> {
    private var vertices: MutableSet<VertexType> = mutableSetOf()
    //private var edges: MutableMap<VertexType, MutableSet<VertexType>> = mutableMapOf()
    private var adjacency: MutableMap<VertexType, MutableMap<VertexType, Double>> = mutableMapOf()

    /**
     * Add the vertex [v] to the graph
     */
    fun addVertex(v: VertexType): Boolean = vertices.add(v)
    /**
     * @return the vertices in the graph
     */
    fun getVertices(): Set<VertexType> = vertices
    /**
     * Add an edge between [from] and [to] with edge weight [cost]
     */
    fun addEdge(from: VertexType, to: VertexType, cost: Double) {
        if (!vertices.contains(from) || !vertices.contains(to)) {
            return
        }
        //
        var connected = adjacency[from]
        if (connected == null) {
            connected = mutableMapOf()
            adjacency[from] = connected
        }
        if (connected[to] != null) {
            return
        }
        connected[to] = cost
    }
    /**
     * Get all the edges that begin at [from]
     * @return a map where each key represents a vertex connected to [from] and the value represents the edge weight.
     */
    fun getEdges(from: VertexType): Map<VertexType, Double> = adjacency[from]?.toMap() ?: emptyMap()
    /**
     * Clear all vertices and edges
     */
    fun clear() {
        vertices.clear()
        adjacency.clear()
    }
}

class MinPriorityQueue<T> {
    private var priorityQueue: MutableMap<T, Double> = mutableMapOf()

    /**
     * @return true if the queue is empty, false otherwise
     */
    fun isEmpty(): Boolean = priorityQueue.isEmpty()

    /**
     * Add [elem] with at level [priority]
     */
    fun addWithPriority(elem: T, priority: Double){
        priorityQueue[elem] = priority
    }

    /**
     * Get the next (highest priority) element and remove this element from the queue.
     * @return the next element in terms of priority.  If empty, return null.
     */
    fun next(): T?{
        if (priorityQueue.isEmpty()){
            return null
        }
        val next = priorityQueue.minByOrNull { it.value }!!
        priorityQueue.remove(next.key)
        return next.key
    }
    /**
     * Adjust the priority of the given element
     * @param elem whose priority should change
     * @param newPriority the priority to use for the element
     *   the lower the priority the earlier the element int
     *   the order.
     */
    fun adjustPriority(elem: T, newPriority: Double){
        priorityQueue[elem] = newPriority
    }
}

/**
 * class minheap was here, copied from github
 * don't need it for dijkstra's so i removed it to make the code less bulky
 */

/**
 * implementing Dijkstra’s algorithm
 * using WeightedGraph and MinPriorityQueue
 */
fun <VertexType> dijkstra(graph: WeightedGraph<VertexType>, start: VertexType): Pair<Map<VertexType, Double>, Map<VertexType, VertexType?>>{
    // define distance between vertecies, previous vertex, and the queue
    val distance = mutableMapOf<VertexType, Double>()
    val prev = mutableMapOf<VertexType, VertexType?>()
    val queue = MinPriorityQueue<VertexType>()

    for (v in graph.getVertices()){
        //make the distance infinite for our conditional
        distance[v] = Double.POSITIVE_INFINITY
        prev[v] = null
        queue.addWithPriority(v, distance[v]!!)
    }
    //lowest dist has the highest priority
    distance[start] = 0.0
    queue.adjustPriority(start, 0.0)

    while (!queue.isEmpty()){
        val next = queue.next() ?: continue
        //find the shortest/lowest weight route
        for ((connected, weight) in graph.getEdges(next)) {
            val alternative = distance[next]!! + weight
            // will always be less than infinity
            if (alternative < distance[connected]!!){
                distance[connected] = alternative
                prev[connected] = next
                queue.adjustPriority(connected, alternative)
            }
        }
    }

    return Pair(distance,prev)

}


fun main() {
    //unit tests for WeightedGraph
    val g = WeightedGraph<Int>()
    g.addVertex(2)
    g.addVertex(5)
    g.addEdge(2, 5, 2.0)
    println(g.getEdges(2))
    println(g.getVertices())
    g.clear()
    println(g.getVertices())

    //unit tests for MinPriorityQueue
    val q = MinPriorityQueue<Int>()
    println(q.isEmpty())
    q.addWithPriority(1, 1.0)
    q.addWithPriority(2, 2.0)
    q.addWithPriority(3, 3.0)
    println(q.next())
    q.adjustPriority(2, 4.0)
    println(q.next())

    //unit test for Dijkstra's
    //build the graph
    val graph = WeightedGraph<String>()
    graph.addVertex("A")
    graph.addVertex("B")
    graph.addVertex("C")
    graph.addVertex("D")

    graph.addEdge("A", "B", 1.0)
    graph.addEdge("A", "C", 4.0)
    graph.addEdge("B", "C", 2.0)
    graph.addEdge("B", "D", 6.0)
    graph.addEdge("C", "D", 3.0)

    println(dijkstra(graph, "A"))

    //City example of Dijkstra implementation (for "Solving Problems with Dijkstra")
    val map = WeightedGraph<String>()
    graph.addVertex("Boston")
    graph.addVertex("Tokyo")
    graph.addVertex("Paris")
    graph.addVertex("Dubai")

    graph.addEdge("Boston", "Tokyo", 6703.0)
    graph.addEdge("Boston", "Paris", 3435.0)
    graph.addEdge("Tokyo", "Paris",  6048.0)
    graph.addEdge("Tokyo", "Dubai", 7545.0)
    graph.addEdge("Paris", "Dubai", 3259.0)
    graph.addEdge("Boston", "Dubai", 6647.0)
    // add to be bidirectional
    graph.addEdge("Tokyo", "Boston", 6703.0)
    graph.addEdge("Paris", "Boston", 3435.0)
    graph.addEdge("Paris", "Tokyo",  6048.0)
    graph.addEdge("Dubai", "Tokyo", 7545.0)
    graph.addEdge("Dubai", "Paris", 3259.0)
    graph.addEdge("Dubai", "Boston", 6647.0)

    val (distances, previous) = dijkstra(graph, "Boston")

    fun pathTo(target: String): List<String>{
        val path = mutableListOf<String>()
        var current: String? = target
        while (current != null) {
            path.add(0, current)
            current = previous[current]
        }
    return path
    }
    // results are pretty predictable...
    println(pathTo("Tokyo"))
    println(pathTo("Paris"))
    println(pathTo("Dubai"))
}