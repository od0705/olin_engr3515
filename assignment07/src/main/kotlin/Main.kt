import kotlin.math.absoluteValue
// baeldung + gpt used for class implementation/understanding how to explicitly write out hashing

/**
 * Represents a mapping of keys to values.
 * @param K the type of the keys
 * @param V the type of the values
 */
// represent entries in array as key-value pairs
data class Entry<K, V>(val key: K, var value: V)

// associative array class
class AssociativeArray<K, V>{
    // define array size, capacity, and number of buckets we can input into (only define within class)
    // pick a prime number, i picked 11
    private var capacity = 11
    private var size = 0
    private var buckets = Array(capacity) { mutableListOf<Entry<K, V>>() }

    // threshold is 75% full before requesting more memory
    private val threshold = 0.75

    /**
     * hashing function, mod division
     */
    private fun hash(key: K): Int = key.hashCode().absoluteValue % capacity

    /**
     * makes the hash table bigger
     */
    private fun rehash() {
        val oldBuckets = buckets
        // increase capacity to the next biggest prime number
        capacity = nextPrime(capacity * 2)
        buckets = Array(capacity) { mutableListOf<Entry<K, V>>() }
        size = 0
        // reenter values into new array
        for (bucket in oldBuckets) {
            for (entry in bucket) {
                this[entry.key] = entry.value
            }
        }
    }

    /**
     * find the next prime number
     */
    private fun nextPrime(n: Int): Int {
        var candidate = n + 1
        while (!isPrime(candidate)) candidate++
        return candidate
    }

    /**
     * checks if the number is prime
     */
    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) return false
        }
        return true
    }

    /**
     * Insert the mapping from the key, [k], to the value, [v].
     * If the key already maps to a value, replace the mapping.
     */
    operator fun set(k: K, v: V) {
        // find bucket at index that corresponds to key
        val index = hash(k)
        val bucket = buckets[index]

        // if key exists update its value
        for (entry in bucket) {
            if (entry.key == k) {
                entry.value = v
                return
            }
        }

        // if key doesnt exist add an entry for it
        bucket.add(Entry(k, v))
        size++

        // rehash if needed
        if (size.toDouble() / capacity > threshold) {
            rehash()
        }
    }
    /**
     * @return true if [k] is a key in the associative array
     */
    operator fun contains(k: K): Boolean{
        val index = hash(k)
        return buckets[index].any { it.key == k }
    }

    /**
     * @return the value associated with the key [k] or null if it doesn't exist
     */
    operator fun get(k: K): V?{
        val index = hash(k)
        for (entry in buckets[index]) {
            if (entry.key == k) return entry.value
        }
        return null
    }

    /**
     * Remove the key, [k], from the associative array
     * @param k the key to remove
     * @return true if the item was successfully removed and false if the element was not found
     */
    fun remove(k: K): Boolean{
        val index = hash(k)
        val bucket = buckets[index]
        val iterator = bucket.iterator()
        // reduce size when value is removed
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == k) {
                iterator.remove()
                size--
                return true
            }
        }
        return false
    }

    /**
     * @return the number of elements stored in the hash table
     */
    fun size(): Int = size

    /**
     * @return the full list of key value pairs for the associative array
     */
    fun keyValuePairs(): List<Pair<K, V>>{
        // flatMap creates a list of the buckets which each contain a key and its paired value
        return buckets.flatMap { bucket -> bucket.map { it.key to it.value } }
    }
}

fun LempelZivEncode(input: String): List<Int> {
    // use of AssocArray class
    val dictionary = AssociativeArray<String, Int>()

    // create a dictionary of size and assign key-value to be
    for (i in 0..255) {
        dictionary[i.toChar().toString()] = i
    }

    var dictSize = 256
    var string = ""
    val result = mutableListOf<Int>()

    for (char in input) {
        val substring = string + char
        // if the substring exists, assign an existing code to it
        if (substring in dictionary){
            string = substring
        } else {
            // else, make a code for it and store it
            dictionary[string]?.let { result.add(it) }
            dictionary[substring] = dictSize++
            string = char.toString()

        }
    }
    // check is seq is finished
    if (string.isNotEmpty()) {
        dictionary[string]?.let { result.add(it) }
    }
    return result
}


fun main() {
    // testing AssocArray class
    val array = AssociativeArray<String, Int>()

    array["cat"] = 3
    array["dog"] = 5
    array["hamster"] = 8

    println("cat in assoc? ${"cat" in array}")
    println("cat value: ${array["cat"]}")

    array["cat"] = 10
    println("updated cat: ${array["cat"]}")

    println("removing dog: ${array.remove("dog")}")
    println("size: ${array.size()}")

    println("pairs: ${array.keyValuePairs()}")

    //lzw unit test
    val input = "ABCCBABACBBABCBABABCABBCBBABCABCBBCABACBACBABAACABACBABACBACABACBACBBBCABCAAAACABCBCABACACBACBACBACABCABCABCBBCABCABACBACABCA"
    val encoded = LempelZivEncode(input)
    println(input)
    println(encoded)
    println(encoded.size)
}

/**
 * Results write-up:
 * I implemented Lempel-Zev and found that using my associative array class really helped in value storage
 * I tried a number of different letter sequences and noticed that it could store really long sequences
 * as smaller lists in code, saving lots of memory (as compression does).
 * For example, in the above unit test it stores a string of 125 charcters as a list of 49 values, saving more that
 * half of the space needed.
 */