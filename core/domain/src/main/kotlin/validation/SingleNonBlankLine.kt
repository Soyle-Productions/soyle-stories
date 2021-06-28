package com.soyle.stories.domain.validation


class SingleNonBlankLine private constructor(val value: String) : Comparable<SingleNonBlankLine>, CharSequence by value {
    companion object {
        fun create(value: SingleLine): SingleNonBlankLine?
        {
            return NonBlankString.create(value.toString())?.let {
                SingleNonBlankLine(it.toString())
            }
        }
    }
    operator fun component1() = value
    override fun compareTo(other: SingleNonBlankLine): Int = value.compareTo(other.value)
    override fun equals(other: Any?): Boolean = (other as? SingleNonBlankLine)?.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = value
}