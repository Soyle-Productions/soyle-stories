package com.soyle.stories.common

/**
 *
 * Unline a [Pair], the order of the two items within is not maintained.  A [Couple] is considered equal to another
 * [Couple] if both items are contained in the other [Couple]
 *
 */
class Couple<T, R : T, S : T>(r: R, s: S)
{
    private val _set = setOf(r, s)

    operator fun contains(a: Any?): Boolean = _set.contains(a)

    fun forBoth(block: (T) -> Unit) {
        for (t in _set) block(t)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Couple<*, *, *>

        if (_set != other._set) return false

        return true
    }

    override fun hashCode(): Int {
        return _set.hashCode()
    }

    override fun toString(): String {
        return "Couple(${_set.joinToString(", ")})"
    }

}

typealias CoupleOf<T> = Couple<T, T, T>

fun <T> coupleOf(a: T, b: T) = Couple(a, b)