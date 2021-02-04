package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

class ListAllCharacterArcsUseCase(
    private val characterRepository: CharacterRepository,
    private val characterArcRepository: CharacterArcRepository
) : ListAllCharacterArcs {

    override suspend fun invoke(projectId: UUID, outputPort: ListAllCharacterArcs.OutputPort) {
        val (characters, arcs) = getCharactersAndArcsInProject(Project.Id(projectId))
        val groupedArcs = groupArcsByCharacter(arcs, characters)
        val response = CharacterArcsByCharacter(groupedArcs)
        outputPort.receiveCharacterArcList(response)
    }

    private suspend fun getCharactersAndArcsInProject(projectId: Project.Id): Pair<List<Character>, List<CharacterArc>> {
        return characterRepository.listCharactersInProject(projectId) to characterArcRepository.listAllCharacterArcsInProject(
            projectId
        )
    }

    private fun groupArcsByCharacter(arcs: List<CharacterArc>, characters: List<Character>): List<Pair<CharacterItem, List<CharacterArcItem>>> {
        val groupedArcs = arcs.groupBy { it.characterId }
        return characters.map {
            CharacterItem(it) to groupedArcs.getOrDefault(it.id, emptyList()).map(::CharacterArcItem)
        }
    }
}