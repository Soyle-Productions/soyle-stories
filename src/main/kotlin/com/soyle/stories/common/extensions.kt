package com.soyle.stories.common

typealias PairOf<T> = Pair<T, T>
fun <T> pairOf(t: T): PairOf<T> = t to t

fun <T> List<T>.plusElementAt(index: Int, element: T): List<T> = subList(0, index) + element + subList(index, size)

typealias MultiMap<K, V> = Map<K, List<V>>

fun <K, V> MultiMap<K, V>.plus(key: K, value: V): MultiMap<K, V> =
    minus(key).plus(key to getOrDefault(key, listOf()).plus(value))