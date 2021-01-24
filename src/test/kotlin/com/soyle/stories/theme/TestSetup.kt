/**
 * Created by Brendan
 * Date: 3/11/2020
 * Time: 10:18 AM
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

fun setupContext(
    initialThemes: List<Theme> = emptyList(),
    initialCharacters: List<Character> = emptyList(),
    initialCharacterArcs: List<CharacterArc> = emptyList(),

    addNewCharacterArc: (CharacterArc) -> Unit = {},
    removeCharacterArc: (CharacterArc) -> Unit = {},
    updateCharacterArc: (CharacterArc) -> Unit = {},
    updateTheme: (Theme) -> Unit = {},
    deleteTheme: (Theme) -> Unit = {}
): Context = object : Context {
    override val characterArcRepository: CharacterArcRepository = CharacterArcRepositoryDouble(
        onAddNewCharacterArc = addNewCharacterArc,
        onRemoveCharacterArc = removeCharacterArc,
        onUpdateCharacterArc = updateCharacterArc
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


        override suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme? {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
            TODO("Not yet implemented")
        }

        override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
            TODO("Not yet implemented")
        }

        override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
            TODO("Not yet implemented")
        }
        override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

        override suspend fun updateTheme(theme: Theme) {
            updateTheme.invoke(theme)
            themes[theme.id] = theme
        }

        override suspend fun deleteTheme(theme: Theme) {
            deleteTheme.invoke(theme)
            themes.remove(theme.id)
        }

    }
}