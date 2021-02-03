/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:38 AM
 */
package com.soyle.stories.theme

import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import kotlinx.coroutines.runBlocking

class TestContext(
    initialThemes: List<Theme> = emptyList(),
    initialCharacters: List<Character> = emptyList(),
    initialCharacterArcs: List<CharacterArc> = emptyList(),
    initialCharacterArcSections: List<CharacterArcSection> = emptyList(),

    addNewCharacterArc: (CharacterArc) -> Unit = {},
    removeCharacterArc: (CharacterArc) -> Unit = {},
    updateCharacterArcSection: (CharacterArcSection) -> Unit = {},
    addNewCharacterArcSections: (List<CharacterArcSection>) -> Unit = {},
    removeArcSections: (List<CharacterArcSection>) -> Unit = {},
    updateTheme: (Theme) -> Unit = {},
    deleteTheme: (Theme) -> Unit = {}
) : Context {

    data class PersistenceLog(val type: String, val data: Any) {
        override fun toString(): String {
            return "$type -> $data)"
        }
    }

    private val _persistedItems = mutableListOf<PersistenceLog>()
    val persistedItems: List<PersistenceLog>
        get() = _persistedItems

    override val characterArcRepository: CharacterArcRepository = CharacterArcRepositoryDouble(
        onAddNewCharacterArc = addNewCharacterArc,
        onRemoveCharacterArc = removeCharacterArc
    ).apply {
        initialCharacterArcs.forEach {
            givenCharacterArc(it)
        }
    }
    override val characterRepository: CharacterRepository = object : CharacterRepository {
        val characters = mutableMapOf<Character.Id, Character>()
        init {
            characters.putAll(initialCharacters.map { it.id to it })
        }
        override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]
        override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters.values.filter { it.projectId == projectId }
        override suspend fun updateCharacter(character: Character) {
            characters[character.id] = character
        }
    }
    override val themeRepository: ThemeRepository = ThemeRepositoryDouble(
        onUpdateTheme = {
            this@TestContext._persistedItems.add(
                PersistenceLog("updateTheme", it)
            )
            updateTheme.invoke(it)
        },
        onDeleteTheme = {
            this@TestContext._persistedItems.add(PersistenceLog("deleteTheme", it))
            deleteTheme.invoke(it)
        }
    )
    init {
        runBlocking {
            initialThemes.forEach {
                themeRepository.addTheme(it)
            }
        }
    }
}