/**
 * Author: Oumayma Dakhama
 * Date: 9/10/25
 * Purpose: manipulates strings of 'A', 'C', 'U', 'T', and 'G' letters representing DNA and RNA base pairs.
 * originally written in Python, translated to Kotlin
 */

fun templateBase(base : Char): Char = when (base) {
    /**
     * converts DNA base to its complementary RNA base
     */
    'A' -> 'T'
    'T' -> 'U'
    'G' -> 'C'
    'C' -> 'G'
    else -> '?'
}

fun templateSequence(seq: String): String{
    /**
     * creates an RNA sequence from a DNA template sequence
     */
    var templateSeq = ""
    for (base in seq){
        var addedBase = templateBase(base)
        templateSeq += addedBase
    }
    return templateSeq
}

fun onlyAU(RNAseq : String) : String {
    /**
     * returns on the A and U in an RNA seq
     */
    var onlyAUseq = ""
    for (base in RNAseq){
        if (base in "AU") {
            onlyAUseq += base
        }
    }
    return onlyAUseq
}

fun main(){
    /**
     * unit tests
     */
    println(templateBase('A'))
    println(templateSequence("ATAGTACT"))
    println(onlyAU("AGCUGAGUA"))
}