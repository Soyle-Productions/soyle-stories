package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.entities.*
import java.util.*

sealed class Tool<T>(
    override val id: Id,
    val identifyingData: T,
    val isOpen: Boolean,
    val associatedData: Any? = null
) : Entity<Tool.Id> {

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Tool($uuid)"
    }

    protected abstract fun copy(
      isOpen: Boolean = this.isOpen,
      associatedData: Any? = this.associatedData
    ): Tool<T>

    fun open(): Tool<T> = copy(isOpen = true)
    fun close(): Tool<T> = copy(isOpen = false)

    abstract fun identifiedWithCharacter(characterId: Character.Id): Boolean
    abstract fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean

    class CharacterList(id: Id, projectId: Project.Id, isOpen: Boolean) : Tool<Project.Id>(id, projectId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Project.Id> = CharacterList(id, identifyingData, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = false
    }

    class LocationList(id: Id, projectId: Project.Id, isOpen: Boolean) : Tool<Project.Id>(id, projectId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Project.Id> = LocationList(id, identifyingData, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = false
    }

    class SceneList(id: Id, projectId: Project.Id, isOpen: Boolean) : Tool<Project.Id>(id, projectId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Project.Id> = SceneList(id, identifyingData, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = false
    }

    class StoryEventList(id: Id, projectId: Project.Id, isOpen: Boolean) : Tool<Project.Id>(id, projectId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Project.Id> = StoryEventList(id, identifyingData, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = false
    }

    class BaseStoryStructure(id: Id, val themeId: Theme.Id, val characterId: Character.Id, isOpen: Boolean) : Tool<Pair<Theme.Id, Character.Id>>(id, themeId to characterId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Pair<Theme.Id, Character.Id>> = BaseStoryStructure(id, themeId, characterId, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = characterId == this.characterId
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = themeId in themeIds
    }

    class CharacterComparison(id: Id, val themeId: Theme.Id, val characterId: Character.Id?, isOpen: Boolean) : Tool<Theme.Id>(id, themeId, isOpen, characterId) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Theme.Id> = CharacterComparison(id, themeId, associatedData as Character.Id?, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = themeId in themeIds
    }

    class LocationDetails(id: Id, val locationId: Location.Id, isOpen: Boolean) : Tool<Location.Id>(id, locationId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<Location.Id> = LocationDetails(id, locationId, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = false
    }

    class StoryEventDetails(id: Id, val storyEventId: StoryEvent.Id, isOpen: Boolean) : Tool<StoryEvent.Id>(id, storyEventId, isOpen, null) {
        override fun copy(isOpen: Boolean, associatedData: Any?): Tool<StoryEvent.Id> = StoryEventDetails(id, storyEventId, isOpen)
        override fun identifiedWithCharacter(characterId: Character.Id): Boolean = false
        override fun identifiedWithAnyThemeIdIn(themeIds: Set<Theme.Id>): Boolean = false
    }
}