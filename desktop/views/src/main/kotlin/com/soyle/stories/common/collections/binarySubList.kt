package com.soyle.stories.common.collections

/**
 * If the list is sorted, filter using a binary search to more quickly find the sublist with the matching predicate
 */
fun <T> List<T>.binarySubList(predicate: (T) -> Boolean, lookForward: (T) -> Boolean): List<T> {
    val midIndex = binarySearch {
        if (predicate(it)) {
            0
        } else if (lookForward(it)) {
            1
        } else -1
    }

    if (midIndex !in 0 .. size) return emptyList()

    return (subList(0, midIndex).asReversed().asSequence()
        .takeWhile(predicate) +
            subList(midIndex, size).asSequence()
                .takeWhile(predicate)).toList()
}