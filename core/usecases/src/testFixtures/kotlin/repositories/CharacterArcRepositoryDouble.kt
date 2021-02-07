package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository

class CharacterArcRepositoryDouble(
    private val onAddNewCharacterArc: (CharacterArc) -> Unit = {},
    private val onUpdateCharacterArc: (CharacterArc) -> Unit = {},
    private val onRemoveCharacterArc: (CharacterArc) -> Unit = {}
) : CharacterArcRepository {

    private val characterArcsByTheme = mutableMapOf<Theme.Id, MutableMap<Character.Id, CharacterArc>>().withDefault { mutableMapOf() }
    private val characterArcsById = mutableMapOf<CharacterArc.Id, CharacterArc>()
    private val characterArcsByArcSectionId = mutableMapOf<CharacterArcSection.Id, CharacterArc.Id>()
    /**
     * Available for tests
     */
    fun givenCharacterArc(characterArc: CharacterArc)
    {
        characterArcsByTheme[characterArc.themeId] = characterArcsByTheme.getValue(characterArc.themeId)
        characterArcsByTheme.getValue(characterArc.themeId)[characterArc.characterId] = characterArc
        characterArcsById[characterArc.id] = characterArc
        characterArc.arcSections.forEach {
            characterArcsByArcSectionId[it.id] = characterArc.id
        }
    }

    val characterArcs
        get() = characterArcsById.values.toList()

    fun getCharacterArcSection(arcSectionId: CharacterArcSection.Id): CharacterArcSection? {
        return characterArcsByArcSectionId[arcSectionId]?.let {
                characterArcsById[it]
            }?.getArcSection(arcSectionId)
    }

    override suspend fun getCharacterArcByCharacterAndThemeId(
        characterId: Character.Id,
        themeId: Theme.Id
    ): CharacterArc? = characterArcsByTheme.getValue(themeId)[characterId]

    override suspend fun listAllCharacterArcsInTheme(themeId: Theme.Id): List<CharacterArc> =
        characterArcsByTheme.getValue(themeId).values.toList()

    override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
        onAddNewCharacterArc.invoke(characterArc)
        characterArcsByTheme[characterArc.themeId] = characterArcsByTheme.getValue(characterArc.themeId)
        characterArcsByTheme.getValue(characterArc.themeId)[characterArc.characterId] = characterArc
        characterArcsById[characterArc.id] = characterArc
        characterArc.arcSections.forEach {
            characterArcsByArcSectionId[it.id] = characterArc.id
        }
    }

    private fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
        characterArcsByTheme.getValue(themeId).remove(characterId)?.let { arc ->
            onRemoveCharacterArc.invoke(arc)
            characterArcsById.remove(arc.id)
            arc.arcSections.forEach {
                characterArcsByArcSectionId.remove(it.id)
            }
        }
    }
    override suspend fun removeCharacterArcs(vararg characterArcs: CharacterArc) {
        characterArcs.forEach { arc ->
            removeCharacterArc(arc.themeId, arc.characterId)
        }
    }

    override suspend fun listCharacterArcsForCharacter(characterId: Character.Id): List<CharacterArc> {
        return characterArcsById.values.filter { it.characterId == characterId }
    }

    override suspend fun getCharacterArcContainingArcSection(arcSectionId: CharacterArcSection.Id): CharacterArc? =
        characterArcsByArcSectionId[arcSectionId]?.let { characterArcsById[it] }


    override suspend fun getCharacterArcsContainingArcSections(arcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArc> {
        return arcSectionIds.mapNotNull {
            characterArcsByArcSectionId[it]
        }.toSet().mapNotNull { characterArcsById[it] }
    }

    override suspend fun getCharacterArcsWithSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArc> {
        return characterArcsById.values.filter { arc ->
            arc.arcSections.any { it.linkedLocation == locationId }
        }
    }

    override suspend fun updateCharacterArcs(characterArcs: Set<CharacterArc>) = replaceCharacterArcs(*characterArcs.toTypedArray())

    override suspend fun replaceCharacterArcs(vararg characterArcs: CharacterArc) {
        characterArcs.forEach { characterArc ->
            onUpdateCharacterArc(characterArc)
            val existingArc = characterArcsById[characterArc.id]
            characterArcsByTheme[characterArc.themeId] = characterArcsByTheme.getValue(characterArc.themeId)
            characterArcsByTheme.getValue(characterArc.themeId)[characterArc.characterId] = characterArc
            characterArcsById[characterArc.id] = characterArc
            existingArc?.arcSections?.forEach {
                characterArcsByArcSectionId.remove(it.id)
            }
            characterArc.arcSections.forEach {
                characterArcsByArcSectionId[it.id] = characterArc.id
            }
        }
    }

    override suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc> {
        return characterArcsById.values.toList()
    }

}