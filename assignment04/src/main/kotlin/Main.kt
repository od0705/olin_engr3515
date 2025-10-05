import kotlin.time.measureTime
import kotlin.time.DurationUnit
import kotlin.random.Random

/**
 * merge sort part 1, once we are down to our split lists, sorting the values
 * used baeldung.com for pseudocode + kotlin function guide
 */
fun merge(leftHalf: IntArray, rightHalf: IntArray): IntArray {
    val wholeArray = IntArray(leftHalf.size + rightHalf.size)
    // initiilize indicies to be 0
    var leftIndex = 0
    var rightIndex = 0
    var wholeIndex = 0

    // lots of while loops
    while (leftIndex < leftHalf.size && rightIndex < rightHalf.size) {
        if (leftHalf[leftIndex] <= rightHalf[rightIndex]) {
            wholeArray[wholeIndex] = leftHalf[leftIndex]
            leftIndex++
        } else {
            wholeArray[wholeIndex] = rightHalf[rightIndex]
            rightIndex++
        }
        wholeIndex++
    }

    while (leftIndex < leftHalf.size) {
        wholeArray[wholeIndex] = leftHalf[leftIndex]
        leftIndex++
        wholeIndex++
    }

    while (rightIndex < rightHalf.size) {
        wholeArray[wholeIndex] = rightHalf[rightIndex]
        rightIndex++
        wholeIndex++
    }
    return wholeArray
}

/**
 * merge sort part 2, splitting the list and recursive calling of merge
 */
fun mergeSort(list: IntArray): IntArray {
    //condition once we are down to lists of 1
    if (list.size <= 1) {
        return list
    }
    // split the list in half
    val middle = list.size / 2
    val leftHalf = list.sliceArray(0 until middle)
    val rightHalf = list.sliceArray(middle until list.size)
    // recursive sorting
    return merge(mergeSort(leftHalf), mergeSort(rightHalf))
}

/**
 * selection sort
 */
fun selectSort(list: IntArray): IntArray {
// for each i index in the list
    for (i in 0 until list.size - 1) {
        var indexOfMin = i
        // set the minimum each time for each j in the indicies
        for (j in (i + 1 until list.size)) {
            if (list[j] < list[indexOfMin]) {
                indexOfMin = j
            }
        }
        // swap the current list value with the new found minimum
        val tempMin = list[i]
        list[i] = list[indexOfMin]
        list[indexOfMin] = tempMin
    }
    return list
}

/**
 * heap sort
 * class from assignment03
 */
class MinHeap<T> {
    private var vertices: MutableList<Pair<T, Double>> = mutableListOf()
    private var indexMap: MutableMap<T, Int> = mutableMapOf()

    /**
     * @return true if the heap is empty and false otherwise
     */
    fun isEmpty(): Boolean {
        return vertices.isEmpty()
    }

    /**
     * Insert [data] into the heap with value [heapNumber]
     * @return true if [data] is added and false if [data] was already there
     */
    fun insert(data: T, heapNumber: Double):Boolean {
        if (contains(data)) {
            return false
        }
        vertices.add(Pair<T, Double>(data, heapNumber))
        indexMap[data] = vertices.size - 1
        percolateUp(vertices.size - 1)
        return true
    }

    /**
     * Gets the minimum value from the heap and removes it.
     * @return the minimum value in the heap (or null if heap is empty)
     */
    fun getMin(): T? {
        when (vertices.size) {
            0 -> {
                return null
            }
            1 -> {
                val tmp = vertices[0].first
                vertices = mutableListOf()
                return tmp
            }
            else -> {
                val tmp = vertices[0].first
                swap(0, vertices.size - 1)
                vertices.removeLast()
                indexMap.remove(tmp)
                bubbleDown(0)
                return tmp
            }
        }
    }

    /**
     * Change the number of an element
     * @param vertex the element to change
     * @param newNumber the new number for the element
     */
    fun adjustHeapNumber(vertex: T, newNumber: Double) {
        getIndex(of=vertex)?.also{ index ->
            vertices[index] = Pair(vertices[index].first, newNumber)
            // do both operations to avoid explicitly testing which way to go
            percolateUp(startIndex=index)
            bubbleDown(startIndex=index)
        }
    }

    /**
     * @return true if the element is in the heap, false otherwise
     */
    fun contains(vertex: T): Boolean {
        return getIndex(of=vertex) != null
    }

    /**
     * @return the index in the list where the element is stored (or null if
     *     not there)
     */
    private fun getIndex(of: T): Int? {
        return indexMap[of]
    }

    /**
     * Bubble down from [startIndex] if needed
     * @param startIndex the index in the tree to start the bubbling
     */
    private fun bubbleDown(startIndex: Int) {
        val startNumber = vertices[startIndex].second
        val leftIndex = getLeftIndex(of=startIndex)
        val rightIndex = getRightIndex(of=startIndex)
        val leftNumber = if (leftIndex >= vertices.size) null else vertices[leftIndex].second
        val rightNumber = if (rightIndex >= vertices.size) null else vertices[rightIndex].second

        /*
         * We determine whether we need to continue with bubbling
         * Case 1: for each child, either the number is less or the child doesn't exist
         * Case 2: either the right child doesn't exist (meaning the left child must) or
         *    the right child exists, the left child exists, and left is smaller than right
         * Case 3: this will capture the case where we need to swap to the right
         */
        if ((leftNumber == null || startNumber < leftNumber) &&
            (rightNumber == null || startNumber < rightNumber)) {
            return
        } else if (rightNumber == null || (leftNumber != null && leftNumber < rightNumber)) {
            // swap with left since it is smallest
            swap(leftIndex, startIndex)
            bubbleDown(leftIndex)
            return
        } else {
            // swap with right since it is smallest
            swap(rightIndex, startIndex)
            bubbleDown(rightIndex)
            return
        }
    }

    /**
     * Swap [index1] and [index2] in the tree
     * @param index1 the first element to swap
     * @param index2 the second element to swap
     */
    private fun swap(index1: Int, index2: Int) {
        // update our index map so we still can find thigns
        indexMap[vertices[index1].first] = index2
        indexMap[vertices[index2].first] = index1
        val tmp = vertices[index1]
        vertices[index1] = vertices[index2]
        vertices[index2] = tmp
    }

    /**
     * Percolate up from [startIndex] if needed
     * @param startIndex the index in the tree to start the percolation
     */
    private fun percolateUp(startIndex: Int) {
        val parentIndex = getParentIndex(of = startIndex)
        if (parentIndex < 0) {
            // we must be at the root
            return
        } else if (vertices[startIndex].second < vertices[parentIndex].second) {
            swap(parentIndex, startIndex)
            percolateUp(parentIndex)
        }
    }

    /**
     * Get the parent index in the list
     * @param of the index to start from
     * @return the index where the parent is stored (if applicable)
     */
    private fun getParentIndex(of: Int):Int {
        return (of - 1) / 2
    }

    /**
     * Get the left index in the list
     * @param of the index to start from
     * @return the index where the left child is stored (if applicable)
     */
    private fun getLeftIndex(of: Int):Int {
        return of * 2 + 1
    }

    /**
     * Get the right index in the list
     * @param of the index to start from
     * @return the index where the right child is stored (if applicable)
     */
    private fun getRightIndex(of: Int):Int {
        return of * 2 + 2
    }
}

/**
 * heap sort function
 */
fun heapSort(list: IntArray): IntArray {
    val minHeap = MinHeap<Int>()
    // insert all values in int array into heap
    for (n in list) {
        minHeap.insert(n, n.toDouble())
    }
    // create a empty list
    val sorted = mutableListOf<Int>()
    // get min and put into list
    while (!minHeap.isEmpty()) {
        sorted.add(minHeap.getMin()!!)
    }
    return sorted.toIntArray()
}

/**
 * insertion sort
 */
fun insertSort(list: IntArray): IntArray {
    for (i in 1 until list.size) {
        var value = list[i]
        // j is our boundary position
        var j = i -1

        while (j>= 0 && value < list[j]) {
            list[j + 1] = list[j]
            j--
        }
        list[j+1] = value
    }
    return list
}


fun main() {
    // test case for merge sort
    val list1 = intArrayOf(3, 6, 2, 4, 9, 23, 200, 22)
    val sort1 = mergeSort(list1)
    println(sort1.contentToString())

    // test case for selection sort
    val list2 = intArrayOf(2, 7, 21, 15, 109, 23)
    val sort2 = selectSort(list2)
    println(sort2.contentToString())

    // test case for heap sort
    val list3 = intArrayOf(9, 8, 7, 1, 2, 3, 10)
    val sort3 = heapSort(list3)
    println(sort3.contentToString())

    // test case for insertion sort
    val list4 = intArrayOf(13, 14, 21, 12, 1, 4, 5)
    val sort4 = insertSort(list4)
    println(sort4.contentToString())

    // testing run times of sorting algorithms
    val sizes = listOf(10, 100, 1000, 10000)
    val results = mutableListOf<MutableList<Double>>()

    for (size in sizes) {
        val data = IntArray(size) { Random.nextInt(0, 100000) }
        val times = mutableListOf(
            measureTime { insertSort(data.copyOf()) }.toDouble(DurationUnit.MILLISECONDS),
            measureTime { selectSort(data.copyOf()) }.toDouble(DurationUnit.MILLISECONDS),
            measureTime { mergeSort(data.copyOf()) }.toDouble(DurationUnit.MILLISECONDS),
            measureTime { heapSort(data.copyOf()) }.toDouble(DurationUnit.MILLISECONDS)
        )
        results.add(times)
    }
    println(results)
}