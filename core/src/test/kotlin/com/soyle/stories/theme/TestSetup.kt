/**
 * Created by Brendan
 * Date: 3/11/2020
 * Time: 10:18 AM
 */
package com.soyle.stories.theme

import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import kotlinx.coroutines.runBlocking

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
    override val themeRepository: ThemeRepository = ThemeRepositoryDouble(
        onUpdateTheme = updateTheme,
        onDeleteTheme = deleteTheme
    )
    init {
        runBlocking {
            initialThemes.forEach {
                themeRepository.addTheme(it)
            }
        }
    }
}