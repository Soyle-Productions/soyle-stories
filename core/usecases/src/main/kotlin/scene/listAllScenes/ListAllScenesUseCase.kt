package com.soyle.stories.usecase.scene.listAllScenes

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class ListAllScenesUseCase(
  projectId: UUID,
  private val sceneRepository: SceneRepository
) : ListAllScenes {

	private val projectId = Project.Id(projectId)

	override suspend fun invoke(output: ListAllScenes.OutputPort) {
		val sceneOrder = sceneRepository.getSceneIdsInOrder(projectId)?.order ?: setOf()
		val indexOf = sceneOrder.withIndex().associate { it.value to it.index }
		output.receiveListAllScenesResponse(ListAllScenes.ResponseModel(sceneRepository.listAllScenesInProject(projectId).map {
			SceneItem(it.id.uuid, it.proseId, it.name.value, indexOf.getValue(it.id))
		}))
	}

}