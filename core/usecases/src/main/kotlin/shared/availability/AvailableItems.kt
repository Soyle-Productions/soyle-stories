package com.soyle.stories.usecase.shared.availability

import com.soyle.stories.domain.validation.NonBlankString

abstract class AvailableItems <Id : Any> {
    protected abstract val allAvailableElements: Set<AvailableStoryElementItem<Id>>

    open fun getMatches(query: NonBlankString): List<AvailableStoryElementItem<Id>> = allAvailableElements
        .filter { it.name.contains(query.value, ignoreCase = true) }
        .sortedWith(compareBy(
            matchAtStartOfWord(query),
            { it.name.length.toDouble() / query.length }
        ))

    private fun matchAtStartOfWord(query: NonBlankString): (AvailableStoryElementItem<Id>) -> Comparable<*>? =
        {
            val index = it.name.indexOf(query.value, ignoreCase = true)
            when {
                index == 0 -> 0
                it.name.substring(index - 1, index) == " " -> 1
                else -> 2
            }
        }
}