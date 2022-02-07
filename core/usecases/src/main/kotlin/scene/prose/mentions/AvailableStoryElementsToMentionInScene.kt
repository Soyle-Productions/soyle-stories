package com.soyle.stories.usecase.scene.prose.mentions

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.shared.availability.AnyAvailableStoryElementItem
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import kotlin.reflect.KClass

open class AvailableStoryElementsToMentionInScene(
    val scene: Scene.Id,
    protected open val alreadyMentionedIds: Set<MentionedEntityId<*>>,
    protected open val allAvailableElements: Set<AnyAvailableStoryElementItem>
) {

    fun <T : Any> getReplacements(
        mentionedId: MentionedEntityId<T>,
        currentName: String? = null
    ): List<AvailableStoryElementItem<T>> =
        allAvailableElements
            .asSequence()
            .withEntityType(mentionedId::class)
            .filter { it.entityId != mentionedId || currentName != null && it.name != currentName }
            .sortedWith(compareBy(
                { it.entityId != mentionedId },
                { it.entityId !in alreadyMentionedIds }
            ))
            .toList()

    private fun <T : Any> Sequence<AvailableStoryElementItem<*>>.withEntityType(
        type: KClass<out MentionedEntityId<T>>
    ): Sequence<AvailableStoryElementItem<T>> =
        filter { it.entityId::class == type }
            .map {
                @Suppress("UNCHECKED_CAST")
                it as AvailableStoryElementItem<T>
            }

    fun getMatches(query: NonBlankString): List<AnyAvailableStoryElementItem> = allAvailableElements
        .filter { it.name.contains(query.value, ignoreCase = true) }
        .sortedWith(compareBy(
            { it.entityId !in alreadyMentionedIds },
            matchAtStartOfWord(query),
            { it.name.length.toDouble() / query.length }
        ))

    private fun matchAtStartOfWord(query: NonBlankString): (AnyAvailableStoryElementItem) -> Comparable<*>? =
        {
            val index = it.name.indexOf(query.value, ignoreCase = true)
            when {
                index == 0 -> 0
                it.name.substring(index - 1, index) == " " -> 1
                else -> 2
            }
        }

    open fun withoutElement(element: AnyAvailableStoryElementItem): AvailableStoryElementsToMentionInScene {
        if (element !in allAvailableElements) return this
        return AvailableStoryElementsToMentionInScene(
            scene,
            alreadyMentionedIds,
            allAvailableElements - element
        )
    }

    open fun withElement(element: AnyAvailableStoryElementItem): AvailableStoryElementsToMentionInScene {
        if (element in allAvailableElements) return this
        return AvailableStoryElementsToMentionInScene(
            scene,
            alreadyMentionedIds,
            allAvailableElements + element
        )
    }

    open fun withMentionedId(id: MentionedEntityId<*>): AvailableStoryElementsToMentionInScene {
        if (id in alreadyMentionedIds) return this
        return AvailableStoryElementsToMentionInScene(
            scene,
            alreadyMentionedIds + id,
            allAvailableElements
        )
    }

    open fun withoutMentionedId(id: MentionedEntityId<*>): AvailableStoryElementsToMentionInScene {
        if (id !in alreadyMentionedIds) return this
        return AvailableStoryElementsToMentionInScene(
            scene,
            alreadyMentionedIds - id,
            allAvailableElements
        )
    }
}