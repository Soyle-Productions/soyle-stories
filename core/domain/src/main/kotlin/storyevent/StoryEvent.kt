package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import java.util.*

class StoryEvent(
    val id: Id,
    val name: String,
    val projectId: Project.Id,
    val previousStoryEventId: Id?,
    val nextStoryEventId: Id?,
    val linkedLocationId: Location.Id?,
    val includedCharacterIds: List<Character.Id>
) {

    constructor(name: String, projectId: Project.Id) : this(Id(), name, projectId, null, null, null, emptyList())

    private fun copy(
        name: String = this.name,
        previousStoryEventId: Id? = this.previousStoryEventId,
        nextStoryEventId: Id? = this.nextStoryEventId,
        linkedLocationId: Location.Id? = this.linkedLocationId,
        includedCharacterIds: List<Character.Id> = this.includedCharacterIds
    ) = StoryEvent(id, name, projectId, previousStoryEventId, nextStoryEventId, linkedLocationId, includedCharacterIds)


    fun withName(newName: String) = copy(name = newName)
    fun withPreviousId(storyEventId: Id?) = copy(previousStoryEventId = storyEventId)
    fun withNextId(storyEventId: Id?) = copy(nextStoryEventId = storyEventId)
    fun withLocationId(locationId: Location.Id?) = copy(linkedLocationId = locationId)
    fun withIncludedCharacterId(characterId: Character.Id) =
        copy(includedCharacterIds = includedCharacterIds + characterId)

    fun withoutCharacterId(characterId: Character.Id) = copy(includedCharacterIds = includedCharacterIds - characterId)

    companion object {
        private val equalityProps
            get() = listOf(
                StoryEvent::id,
                StoryEvent::name,
                StoryEvent::projectId,
                StoryEvent::previousStoryEventId,
                StoryEvent::nextStoryEventId,
                StoryEvent::linkedLocationId,
                StoryEvent::includedCharacterIds
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoryEvent

        return equalityProps.all { it.get(this) == it.get(other) }
    }

    private val cachedHashCode: Int by lazy {
        equalityProps.drop(1)
            .fold(equalityProps.first().get(this).hashCode()) { result, prop ->
                31 * result + prop.get(this).hashCode()
            }
    }

    override fun hashCode(): Int = cachedHashCode

    override fun toString(): String {
        return "StoryEvent(${equalityProps.joinToString(", ") { "${it.name}=${it.get(this)}" }})"
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "StoryEvent($uuid)"
    }

}