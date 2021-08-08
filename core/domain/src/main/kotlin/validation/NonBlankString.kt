package com.soyle.stories.domain.validation

class NonBlankString private constructor(val value: String) : Comparable<NonBlankString>, CharSequence by value {

    operator fun component1() = value

    companion object {
        fun create(value: String): NonBlankString? {
            if (value.isBlank()) return null
            return NonBlankString(value)
        }
    }

    override fun compareTo(other: NonBlankString): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is String) return value == other
        if (javaClass != other?.javaClass) return false

        other as NonBlankString

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value
}
