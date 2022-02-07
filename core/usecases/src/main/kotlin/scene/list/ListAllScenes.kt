package com.soyle.stories.usecase.scene.list

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene

interface ListAllScenes {

	suspend operator fun invoke(projectId: Project.Id, output: OutputPort)

	class SceneListItem(
		val scene: Scene.Id,
		val name: String,
		val prose: Prose.Id
	)
	class ListOfScenesInStory(
		val project: Project.Id,
		items: List<SceneListItem>
	) : List<SceneListItem> by items

	fun interface OutputPort
	{
		suspend fun receiveListOfScenesInStory(response: ListOfScenesInStory)
	}

}