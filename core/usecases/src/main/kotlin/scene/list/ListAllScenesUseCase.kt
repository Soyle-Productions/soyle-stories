package com.soyle.stories.usecase.scene.list

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.scene.SceneRepository

class ListAllScenesUseCase(
    private val sceneRepository: SceneRepository
) : ListAllScenes {

    override suspend fun invoke(projectId: Project.Id, output: ListAllScenes.OutputPort) {
        val sceneOrder = sceneRepository.getSceneIdsInOrder(projectId)?.order ?: setOf()
        val indexOf = sceneOrder.withIndex().associate { it.value to it.index }
        output.receiveListOfScenesInStory(
            ListAllScenes.ListOfScenesInStory(
                projectId,
                sceneRepository.listAllScenesInProject(projectId)
                    .sortedBy { indexOf[it.id] }
                    .map {
                        ListAllScenes.SceneListItem(
                            it.id,
                            it.name.value,
                            it.proseId
                        )
                    }
            ))
    }

}