package com.soyle.stories.usecase.character.listAllCharacterArcs

import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.character.CharacterArcRepository
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