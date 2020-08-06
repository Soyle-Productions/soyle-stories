package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import java.util.*

/**
 * Created by Brendan
 * Date: 2/25/2020
 * Time: 5:38 PM
 */
class ListAllCharacterArcsUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository
) : ListAllCharacterArcs {

    override suspend fun invoke(projectId: UUID, outputPort: ListAllCharacterArcs.OutputPort) {
        val (characters, themes) = getCharactersAndArcsInProject(Project.Id(projectId))
        val groupedArcs = groupArcsByCharacter(themes, characters)
        val response = CharacterArcsByCharacter(groupedArcs)
        outputPort.receiveCharacterArcList(response)
    }

    private suspend fun getCharactersAndArcsInProject(projectId: Project.Id): Pair<List<Character>, List<Theme>> {
        return characterRepository.listCharactersInProject(projectId) to themeRepository.listAllThemesInProject(
            projectId
        )
    }

    private fun groupArcsByCharacter(themes: List<Theme>, characters: List<Character>): List<Pair<CharacterItem, List<CharacterArcItem>>> {
        val groupedArcs = themes.flatMap { it.characters }.filterIsInstance<MajorCharacter>().groupBy { it.id }
        return characters.map {
            CharacterItem(it) to groupedArcs.getOrDefault(it.id, emptyList()).map { it.characterArc }.map(::CharacterArcItem)
        }
    }
}