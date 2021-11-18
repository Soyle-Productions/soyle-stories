package com.soyle.stories.usecase.scene.common

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class PreviousMotivations(private val scene: Scene, private val sceneRepository: SceneRepository)
{
	private val scenesBefore = GlobalScope.async(Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
		sceneRepository.listAllScenesInProject(scene.projectId)
			.sortedByProjectOrder(scene.projectId, sceneRepository)
			.takeWhile { it.id != scene.id }
	}

	companion object {
		suspend fun List<Scene>.sortedByProjectOrder(projectId: Project.Id, sceneRepository: SceneRepository): List<Scene> {
			val indexOf = sceneRepository.getSceneIdsInOrder(projectId)!!
				.order
				.withIndex()
				.associate { it.value to it.index }
			return sortedBy { indexOf.getValue(it.id) }
		}
	}

	suspend fun getLastSetMotivation(characterId: Character.Id): InheritedMotivation?
	{
		val lastSetScene = scenesBefore.await().find {
			it.includesCharacter(characterId) && !it.getMotivationForCharacter(characterId)!!.isInherited()
		}
		if (lastSetScene == null) return null
		return InheritedMotivation(
			lastSetScene.id.uuid,
			lastSetScene.name.value,
			lastSetScene.getMotivationForCharacter(characterId)!!.motivation!!
		)
	}

}