package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.events.*
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyInvolvesCharacter
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCharacter
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCoverage
import com.soyle.stories.domain.storyevent.exceptions.StoryEventException
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class StoryEvent(
    override val id: Id,
    val name: NonBlankString,
    val time: ULong,
    val projectId: Project.Id,
    val sceneId: Scene.Id?,
    val previousStoryEventId: Id?,
    val nextStoryEventId: Id?,
    val linkedLocationId: Location.Id?,
    val involvedCharacters: Set<Character.Id>
) : Entity<StoryEvent.Id> {

    companion object {
        internal fun create(
            name: NonBlankString,
            time: ULong,
            projectId: Project.Id
        ): StoryEventUpdate<StoryEventCreated> {
            val storyEvent = StoryEvent(Id(), name, time, projectId, null, null, null, null, setOf())
            val change = StoryEventCreated(storyEvent.id, name.value, time, projectId)
            return Successful(storyEvent, change)
        }

        private val equalityProps
            get() = listOf(
                StoryEvent::id,
                StoryEvent::name,
                StoryEvent::time,
                StoryEvent::projectId,
                StoryEvent::sceneId,
                StoryEvent::previousStoryEventId,
                StoryEvent::nextStoryEventId,
                StoryEvent::linkedLocationId,
                StoryEvent::involvedCharacters
            )
    }

    private fun copy(
        name: NonBlankString = this.name,
        time: ULong = this.time,
        sceneId: Scene.Id? = this.sceneId,
        previousStoryEventId: Id? = this.previousStoryEventId,
        nextStoryEventId: Id? = this.nextStoryEventId,
        linkedLocationId: Location.Id? = this.linkedLocationId,
        involvedCharacters: Set<Character.Id> = this.involvedCharacters
    ) = StoryEvent(
        id,
        name,
        time,
        projectId,
        sceneId,
        previousStoryEventId,
        nextStoryEventId,
        linkedLocationId,
        involvedCharacters
    )


    fun withName(newName: NonBlankString): StoryEventUpdate<StoryEventRenamed> {
        if (newName == name) return noUpdate()
        return Successful(copy(name = newName), StoryEventRenamed(id, newName.value))
    }

    internal fun withTime(newTime: ULong): StoryEventUpdate<StoryEventRescheduled> {
        if (newTime == time) return noUpdate()
        return Successful(copy(time = newTime), StoryEventRescheduled(id, newTime, time))
    }

    fun coveredByScene(sceneId: Scene.Id): StoryEventUpdate<StoryEventCoveredByScene> {
        val currentSceneId = this.sceneId
        if (currentSceneId == sceneId) return noUpdate()
        return copy(sceneId = sceneId).updatedBy(
            StoryEventCoveredByScene(
                id,
                sceneId,
                currentSceneId?.let { StoryEventUncoveredFromScene(id, currentSceneId) })
        )
    }

    fun withoutCoverage(): StoryEventUpdate<StoryEventUncoveredFromScene> {
        if (sceneId == null) return noUpdate(StoryEventAlreadyWithoutCoverage)
        return copy(sceneId = null).updatedBy(StoryEventUncoveredFromScene(id, sceneId))
    }

    fun withLocationId(locationId: Location.Id?) = copy(linkedLocationId = locationId)

    fun withCharacterInvolved(characterId: Character.Id): StoryEventUpdate<CharacterInvolvedInStoryEvent> {
        if (characterId in involvedCharacters) return noUpdate(StoryEventAlreadyInvolvesCharacter(id, characterId))
        return copy(involvedCharacters = involvedCharacters + characterId).updatedBy(CharacterInvolvedInStoryEvent(id))
    }

    fun withCharacterRemoved(characterId: Character.Id): StoryEventUpdate<CharacterRemovedFromStoryEvent> =
        if (characterId !in involvedCharacters) noUpdate(StoryEventAlreadyWithoutCharacter(id, characterId))
        else copy(involvedCharacters = involvedCharacters - characterId)
            .updatedBy(CharacterRemovedFromStoryEvent(id, characterId))

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

    fun noUpdate(reason: StoryEventException? = null) = UnSuccessful(this, reason as? Throwable)
    private fun <Change : StoryEventChange> updatedBy(change: Change) = Successful(this, change)

}