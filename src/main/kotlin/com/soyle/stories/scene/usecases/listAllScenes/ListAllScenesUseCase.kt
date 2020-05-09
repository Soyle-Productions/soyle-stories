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
		output.receiveListAllScenesResponse(ListAllScenes.ResponseModel(sceneRepository.listAllScenesInProject(projectId).map {
			SceneItem(it.id.uuid, it.name)
		}))
	}

}