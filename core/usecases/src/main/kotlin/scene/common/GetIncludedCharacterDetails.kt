package com.soyle.stories.usecase.scene.common

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository

internal suspend fun getScenesBefore(scene: Scene, sceneRepository: SceneRepository): List<Scene> {
	return sceneRepository.listAllScenesInProject(scene.projectId)
	  .sortedByProjectOrder(scene.projectId, sceneRepository)
	  .takeWhile { it.id != scene.id }
}

internal suspend fun List<Scene>.sortedByProjectOrder(projectId: Project.Id, sceneRepository: SceneRepository): List<Scene> {
	val indexOf = sceneRepository.getSceneIdsInOrder(projectId)
	  .withIndex()
	  .associate { it.value to it.index }
	return sortedBy { indexOf.getValue(it.id) }
}

internal fun getLastSetMotivation(
  reversedScenesBefore: List<Scene>, characterId: Character.Id
): InheritedMotivation?
{
	val lastSetScene = reversedScenesBefore.find {
		it.includesCharacter(characterId) && !it.getMotivationForCharacter(characterId)!!.isInherited()
	} ?: return null
	return InheritedMotivation(
	  lastSetScene.id.uuid,
	  lastSetScene.name.value,
	  lastSetScene.getMotivationForCharacter(characterId)!!.motivation!!
	)
}