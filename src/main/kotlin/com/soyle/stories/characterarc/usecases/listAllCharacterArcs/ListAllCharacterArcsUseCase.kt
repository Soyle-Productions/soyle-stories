package com.soyle.stories.characterarc.usecases.listAllCharacterArcs

import com.soyle.stories.entities.Project
import java.util.*

/**
 * Created by Brendan
 * Date: 2/25/2020
 * Time: 5:38 PM
 */
class ListAllCharacterArcsUseCase(
    projectId: UUID,
    private val characterRepository: com.soyle.stories.characterarc.repositories.CharacterRepository,
    private val characterArcRepository: com.soyle.stories.characterarc.repositories.CharacterArcRepository
) : ListAllCharacterArcs {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(outputPort: ListAllCharacterArcs.OutputPort) {
        val characters = characterRepository.listCharactersInProject(projectId)
        val arcs = characterArcRepository.listAllCharacterArcsInProject(projectId)

        val groupedArcs = arcs.groupBy { it.characterId }

        outputPort.receiveCharacterArcList(
            ListAllCharacterArcs.ResponseModel(
                characters.associate {
                    CharacterItem(
                        it.id.uuid,
                        it.name
                    ) to groupedArcs.getOrDefault(it.id, emptyList()).map {
                        CharacterArcItem(
                            it.characterId.uuid,
                            it.name,
                            it.themeId.uuid
                        )
                    }
                })
        )
    }
}