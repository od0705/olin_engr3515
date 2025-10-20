import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * square matrix class with multiplication operations
 */
class Matrix(private val size: Int) {
    // creates an array of arrays of size n
    private val data: Array<DoubleArray> = Array(size) {DoubleArray(size)}

    // getting a value at cell (i, j)
    operator fun get(i: Int, j: Int): Double {
        // assume i and j are within the range of the matrix
        return data[i][j]
    }

    // setting a value at cell (i, j)
    operator fun set(i: Int, j: Int, value: Double) {
        // assume i and j are within the range of the matrix
        data[i][j] = value
    }

    // dividing the matrix into four n/2×n/2 matrices
    // for Strassen's algorithm
    fun split(): List<Matrix> {
        // assume all of our matricies divide evenly
        require (size % 2 == 0)
        val half = size / 2

        val quad1 = Matrix(half)
        val quad2 = Matrix(half)
        val quad3 = Matrix(half)
        val quad4 = Matrix(half)

        // loop to insert values into our sub matricies
        for (i in 0 until half){
            for (j in 0 until half){
                quad1[i, j] = this[i, j]
                quad2[i, j] = this[i, j + half]
                quad3[i, j] = this[i + half, j]
                quad4[i, j] = this[i + half, j + half]
            }
        }
        return listOf(quad1, quad2, quad3, quad4)
    }

    // matrix addition (for straussens)
    operator fun plus(other: Matrix): Matrix {
        // assume matricies both are size n
        val result = Matrix(size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                result[i, j] = this[i, j] + other[i, j]
            }
        }
        return result
    }

    // matrix subtraction (for straussens)
    operator fun minus(other: Matrix): Matrix {
        // assume matricies both are size n
        val result = Matrix(size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                result[i, j] = this[i, j] - other[i, j]
            }
        }
        return result
    }

    /**
     * Multiply [this] matrix by [other].
     * You can implement this either using block-based matrix multiplication or
     * traditional matrix multiplication (the kind you learn about in math classes!)
     * @return [this]*[other] if the dimensions are compatible and null otherwise
     */
    fun multiply(other: Matrix):Matrix? {
        // assume matricies both are size n
        // create a resulting matrix of the same size (empty)
        val result = Matrix(size)
        // multiplication operation
        for (i in 0 until size) {
            for (j in 0 until size) {
                var sum = 0.0
                for (k in 0 until size) {
                    // addition of cells to get product
                    sum += this[i, k] * other[k, j]
                }
                result[i, j] = sum
            }
        }
        return result
    }

    /**
     * Multiply [this] matrix by [other].
     * Your code should use Strassen's algorithm
     * @return [this]*[other] if the dimensions are compatible and null otherwise
     */
    // to rejoin quadrants after mutliplying them
    fun join(c11: Matrix, c12: Matrix, c21: Matrix, c22: Matrix): Matrix {
        val n = c11.size * 2
        val result = Matrix(n)
        val half = c11.size
        for (i in 0 until half) {
            for (j in 0 until half) {
                result[i, j] = c11[i, j]
                result[i, j + half] = c12[i, j]
                result[i + half, j] = c21[i, j]
                result[i + half, j + half] = c22[i, j]
            }
        }
        return result
    }

    fun strassenMultiply(other: Matrix):Matrix {
        // assume matricies both are size n
        // base case, matrix is a 1x1 square
        if (size == 1) {
            val result = Matrix(1)
            result[0, 0] = this[0, 0] * other[0, 0]
            return result
        }
        // split up both matricies in quardents
        val (a11, a12, a21, a22) = this.split()
        val (b11, b12, b21, b22) = other.split()

        // the strassen operations
        val m1 = (a11 + a22).strassenMultiply(b11 + b22)
        val m2 = (a21 + a22).strassenMultiply(b11)
        val m3 = (a11).strassenMultiply(b12 - b22)
        val m4 = (a22).strassenMultiply(b21 - b11)
        val m5 = (a11 + a12).strassenMultiply(b22)
        val m6 = (a21 - a11).strassenMultiply(b11 + b12)
        val m7 = (a12 - a22).strassenMultiply(b21 + b22)
        // more adding
        val c11 = m1 + m4 - m5 + m7
        val c12 = m3 + m5
        val c21 = m2 + m4
        val c22 = m1 - m2 + m3 + m6

        // rejoin quadrents
        return join(c11, c12, c21, c22)
    }

    fun hybridMultiply(other :Matrix, cutoff: Int): Matrix? {
        // hybrid implementation of both types of matrix multiplication
        // sets a cutoff value size to do strassen or reg multiplication
        return if (this.size <= cutoff) {
            this.multiply(other)
        } else {
            this.strassenMultiply(other)
        }
    }
}



/**
 * Needleman-Wunsch implementation, using Wikipedia pseudocode as a template
 */

fun needleman (seqA: String, seqB: String): Triple <String, String, Double> {
    // need a fun to count score between two bases
    fun score(baseA: Char, baseB: Char): Int {
        return if (baseA == baseB) 1 else -1
    }

    val lenA = seqA.length
    val lenB = seqB.length

    //put our sequences in a square matrix
    val size = maxOf(lenA, lenB) + 1
    val matrix = Matrix(size)

    //add values to first row column of matrix
    for (i in 0 until lenA + 1){
        matrix[i, 0] = (-1.0) * i
    }
    for (j in 0 until lenB + 1){
        matrix[0, j] = (-1.0) * j
    }

    //loop to fill in whole matrix
    for (i in 1 until lenA + 1){
        for (j in 1 until lenB + 1){
            val match = matrix[i - 1, j - 1] + score(seqA[i - 1], seqB[j - 1])
            val delete = matrix[i - 1, j] + -1
            val insert = matrix[i, j - 1] + -1
            matrix[i, j] = maxOf(match, delete, insert)
        }
    }

    //traceback step...
    var i = lenA
    var j = lenB
    var alignmentA = ""
    var alignmentB = ""

    while (i > 0 || j > 0) {
        when {
            i > 0 && j > 0 && matrix[i, j] == matrix[i - 1, j - 1] + score(seqA[i - 1], seqB[j - 1]) -> {
                alignmentA = seqA[i - 1] + alignmentA
                alignmentB = seqB[j - 1] + alignmentB
                i--
                j--
            }
                    i > 0 && matrix[i, j] == matrix[i - 1, j] -1 -> {
            alignmentA = seqA[i - 1] + alignmentA
            alignmentB = "-" + alignmentB
            i--
        }
            else -> {
                alignmentA = "-" + alignmentA
                alignmentB = seqB[j - 1] + alignmentB
                j--
            }

        }
    }

    return Triple(alignmentA, alignmentB, matrix[lenA, lenB])
}

fun main() {

    // for generating a random matrix for unit tests
    fun randomMatrix(size: Int): Matrix {
        val random = Matrix(size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                // pick a random value between 0 and 50
                random[i, j] = Random.nextDouble(0.0, 50.0)
            }
        }
        return random
    }

    // unit testing + benchmarking multiply and strassen multiply
    // strassen optimized for matricies powers of 2
    val sizes = listOf(16, 64, 256, 1024)

    for (n in sizes) {
        val A = randomMatrix(n)
        val B = randomMatrix(n)

        val regularTime = measureTimeMillis {
            A.multiply(B)
        }

        val strassenTime = measureTimeMillis {
            A.strassenMultiply(B)
        }

        val hybridTime = measureTimeMillis {
            // 256 appears to be an optimal cutoff for a hybrid solution
            A.hybridMultiply(B, 256)
        }

        println("Size: $n x $n")
        println("Regular multiply: $regularTime ms")
        println("Strassen multiply: $strassenTime ms")
        println("Hybrid multiply: $hybridTime ms")
        println()
    }

    val genomeSnippet = "TGGCGACAACCGTAGCGGAATATTTTCGCGACCAGGGAAAACGGGTCGTGCTTTTTATCGATTCCATGACCCGTTATGCGCGTGCTTTGCGAGACGTGGCACTGGCGTCGGGAGAGCGTCCGGCTCGTCGAGGTTATCCCGCCTCCGTATTCGATAATTTGCCCCGCTTGCTGGAACGCCCAGGGGCGACCAGCGAGGGAAGCATTACTGCCTTTTATACGGTACTGCTGGAAAGCGAGGAAGAGGCGGACCCGATGGCGGATGAAATTCGCTCTATCCTTGACGGTCACCTGTATCTGAGCAGAAAGCTGGCCGGGCAGGGACATTACCCGGCAATCGATGTACTGAAAAGCGTAAGCCGCGTTTTT"
    val testAgainst = "TGGCCACCACGATAGCAGAATTTTTTCGCGATAATGGAAAGCGAGTCGTCTTGCTTGCCGACTCACTGACGCGTTATGCCAGGGCCGCACGGGAAATCGCTCTGGCCGCCGGAGAGACCGCGGTTTCTGGAGAATATCCGCCAGGCGTATTTAGTGCATTGCCACGACTTTTAGAACGTACGGGAATGGGAGAAAAAGGCAGTATTACCGCATTTTATACGGTACTGGTGGAAGGCGATGATATGAATGAGCCGTTGGCGGATGAAGTCCGTTCACTGCTTGATGGACATATTGTACTATCCCGACGGCTTGCAGAGAGGGGGCATTATCCTGCCATTGACGTGTTGGCAACGCTCAGCCGCGTTTTT"

    val (alignedA, alignedB, score) = needleman(genomeSnippet, testAgainst)
    println("Score: $score")
    println(alignedA)
    println(alignedB)

}