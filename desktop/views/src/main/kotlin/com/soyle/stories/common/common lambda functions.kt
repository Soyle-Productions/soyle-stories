package com.soyle.stories.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun doNothing() = Unit
fun <T> doNothing(t: T) = Unit
fun <T> T.applyNothing() = Unit

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
inline fun notNull(value: Any?): Boolean {
    contract {
        returns(true) implies(value != null)
    }
    return value != null
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
inline fun isNull(value: Any?): Boolean {
    contract {
        returns(true) implies(value == null)
    }
    return value == null
}