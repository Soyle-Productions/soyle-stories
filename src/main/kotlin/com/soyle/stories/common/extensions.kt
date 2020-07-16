package com.soyle.stories.common

typealias PairOf<T> = Pair<T, T>
fun <T> pairOf(t: T): PairOf<T> = t to t