package com.soyle.stories.common

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 4:03 PM
 */

typealias PairOf<T> = Pair<T, T>
fun <T> pairOf(t: T): PairOf<T> = t to t