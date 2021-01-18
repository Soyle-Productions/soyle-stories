package com.soyle.stories.scene.usecases.listAllScenes

import com.soyle.stories.entities.Project
import com.soyle.stories.scene.repositories.SceneRepository
import java.util.*

class ListAllScenesUseCase(
  projectId: UUID,
  private val sceneRepository: SceneRepository
) : ListAllScenes {

	private val projectId = Project.Id(projectId)

	override suspend fun invoke(output: ListAllScenes.OutputPort) {
		val indexOf = sceneRepository.getSceneIdsInOrder(projectId).withIndex().associate { it.value to it.index }
		output.receiveListAllScenesResponse(ListAllScenes.ResponseModel(sceneRepository.listAllScenesInProject(projectId).map {
			SceneItem(it.id.uuid, it.proseId, it.name.value, indexOf.getValue(it.id))
		}))
	}

}