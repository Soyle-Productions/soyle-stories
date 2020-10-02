package com.soyle.stories.doubles

import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

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

    fun getCharacterArc(id: CharacterArc.Id): CharacterArc? = characterArcsById[id]
    fun getCharacterArcSection(id: CharacterArcSection.Id): CharacterArcSection? {
        val arcId = characterArcsByArcSectionId[id] ?: return null
        val arc = characterArcsById[arcId] ?: return null
        return arc.arcSections.find { it.id == id }
    }

    override suspend fun getCharacterArcByCharacterAndThemeId(
        characterId: Character.Id,
        themeId: Theme.Id
    ): CharacterArc? = characterArcsByTheme.getValue(themeId)[characterId]

        override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> =
        characterArcsByTheme.getValue(themeId).values.toList()

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

    override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {
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

    override suspend fun getCharacterArcContainingArcSection(characterArcSectionId: CharacterArcSection.Id): CharacterArc? =
        characterArcsByArcSectionId[characterArcSectionId]?.let { characterArcsById[it] }


    override suspend fun getCharacterArcsContainingArcSections(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArc> {
        return characterArcSectionIds.mapNotNull {
            characterArcsByArcSectionId[it]
        }.toSet().mapNotNull { characterArcsById[it] }
    }

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