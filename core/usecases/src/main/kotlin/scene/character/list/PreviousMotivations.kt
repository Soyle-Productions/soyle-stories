package com.soyle.stories.usecase.scene.character.list

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.common.InheritedMotivation
import kotlinx.coroutines.*
import java.util.logging.Logger

internal class PreviousMotivations(private val scene: Scene, private val sceneRepository: SceneRepository, scope: CoroutineScope)
{
	private val scenesBefore = scope.async {
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
			lastSetScene.id,
			characterId,
			lastSetScene.name.value,
			lastSetScene.getMotivationForCharacter(characterId)!!.motivation!!
		)
	}

}