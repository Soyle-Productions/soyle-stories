/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:38 AM
 */
package com.soyle.stories.theme

import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

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
    override val themeRepository: ThemeRepository = object : ThemeRepository {
        val themes = mutableMapOf<Theme.Id, Theme>()
        init {
            themes.putAll(initialThemes.map { it.id to it })
        }
        override suspend fun updateThemes(themes: List<Theme>) {
            TODO("Not yet implemented")
        }

        override suspend fun addTheme(theme: Theme) {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme> {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
            TODO("Not yet implemented")
        }

        override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme? {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
            TODO("Not yet implemented")
        }
        override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

        override suspend fun updateTheme(theme: Theme) {
            _persistedItems.add(PersistenceLog("updateTheme", theme))
            updateTheme.invoke(theme)
            themes[theme.id] = theme
        }

        override suspend fun deleteTheme(theme: Theme) {
            _persistedItems.add(PersistenceLog("deleteTheme", theme))
            deleteTheme.invoke(theme)
            themes.remove(theme.id)
        }

    }
}