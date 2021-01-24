package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.scene.CharacterNotInScene
import com.soyle.stories.scene.SceneAlreadyContainsCharacter
import java.util.*

class Scene(
    override val id: Id,
    val projectId: Project.Id,
    val name: NonBlankString,
    val storyEventId: StoryEvent.Id,
    val settings: Set<Location.Id>,
    val proseId: Prose.Id,
    private val charactersInScene: List<CharacterInScene>
) : Entity<Scene.Id> {

    constructor(
        projectId: Project.Id,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        proseId: Prose.Id,
        settings: Set<Location.Id> = emptySet(),
    ) : this(Id(), projectId, name, storyEventId, settings, proseId, listOf())

    private val charactersById by lazy { charactersInScene.associateBy { it.characterId } }

    fun includesCharacter(characterId: Character.Id): Boolean {
        return charactersById.containsKey(characterId)
    }

    fun getMotivationForCharacter(characterId: Character.Id): CharacterMotivation? {
        return charactersById[characterId]?.let {
            CharacterMotivation(it.characterId, it.characterName, it.motivation)
        }
    }

    fun getCoveredCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection.Id>? {
        return charactersById[characterId]?.coveredArcSections
    }

    private val allCharacterArcSections by lazy {
        charactersById.values.flatMap { it.coveredArcSections }.toSet()
    }

    fun isCharacterArcSectionCovered(characterArcSectionId: CharacterArcSection.Id): Boolean {
        return allCharacterArcSections.contains(characterArcSectionId)
    }

    val includedCharacters: List<IncludedCharacter> by lazy {
        charactersInScene.map { IncludedCharacter(it.characterId, it.characterName) }
    }

    val coveredArcSectionIds by lazy {
        charactersInScene.flatMap { it.coveredArcSections }
    }

    fun hasCharacters(): Boolean = charactersInScene.isNotEmpty()

    private fun copy(
        name: NonBlankString = this.name,
        settings: Set<Location.Id> = this.settings,
        charactersInScene: List<CharacterInScene> = this.charactersInScene
    ) = Scene(id, projectId, name, storyEventId, settings, this.proseId, charactersInScene)

    fun withName(newName: NonBlankString) = copy(name = newName)

    fun withCharacterIncluded(character: Character): Scene {
        if (includesCharacter(character.id)) throw SceneAlreadyContainsCharacter(id.uuid, character.id.uuid)
        return copy(
            charactersInScene = charactersInScene + CharacterInScene(
                character.id,
                id,
                character.name.value,
                null,
                listOf()
            )
        )
    }

    fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene {
        if (!includesCharacter(characterId)) throw CharacterNotInScene(id.uuid, characterId.uuid)
        return copy(charactersInScene = charactersInScene.map {
            if (it.characterId == characterId) CharacterInScene(
                it.characterId,
                id,
                it.characterName,
                motivation,
                listOf()
            )
            else it
        })
    }

    fun withLocationLinked(locationId: Location.Id) = copy(settings = settings + locationId)
    fun withoutLocation(locationId: Location.Id) = copy(settings = settings.minus(locationId))
    fun withoutCharacter(characterId: Character.Id) =
        copy(charactersInScene = charactersInScene.filterNot { it.characterId == characterId })

    fun withCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        charactersById[characterArcSection.characterId] ?: throw CharacterNotInScene(
            id.uuid,
            characterArcSection.characterId.uuid
        )
        return copy(
            charactersInScene = charactersInScene.map {
                if (it.characterId != characterArcSection.characterId) it
                else it.withCoveredArcSection(characterArcSection)
            }
        )
    }

    fun withoutCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        return copy(
            charactersInScene = charactersInScene.map {
                if (it.characterId != characterArcSection.characterId) it
                else it.withoutCoveredArcSection(characterArcSection)
            }
        )
    }

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Scene($uuid)"
    }

    class CharacterMotivation(val characterId: Character.Id, val characterName: String, val motivation: String?) {
        fun isInherited() = motivation == null
    }

    class IncludedCharacter(val characterId: Character.Id, val characterName: String)
}