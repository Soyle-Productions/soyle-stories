package com.soyle.stories.domain.validation

const val anyNewlinePattern = "\\r\\n|\\r|\\n"
val anyNewLineCharacter = Regex(anyNewlinePattern)

sealed class StringLineCount

/**
 * @return [MultiLine] if [value] contains a newline character, and [SingleLine] otherwise
 */
fun countLines(value: String): StringLineCount {
    if (value.isEmpty()) return SingleLine(value)
    if (value.contains(Regex(anyNewlinePattern))) return MultiLine(value)
    return SingleLine(value)
}

class SingleLine internal constructor(protected val value: String) : Comparable<SingleLine>, CharSequence by value,
    StringLineCount() {
    override fun equals(other: Any?): Boolean = (other as? CharSequence)?.equals(value) ?: false
    override fun toString(): String = value
    override fun compareTo(other: SingleLine): Int = value.compareTo(other.value)
    operator fun compareTo(other: String): Int = value.compareTo(other)
    override fun hashCode(): Int = value.hashCode()
}

class MultiLine internal constructor(private val value: String) :
    Comparable<MultiLine>,
    CharSequence by value,
    StringLineCount()
{
    override fun compareTo(other: MultiLine): Int = value.compareTo(other.value)
    operator fun compareTo(other: String): Int = value.compareTo(other)
    val lines: List<String> by lazy {
        value.split(Regex(anyNewlinePattern))
    }
}