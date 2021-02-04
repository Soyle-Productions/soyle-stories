package com.soyle.stories.scene.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.entities.theme.Symbol

interface SceneRepository {

	suspend fun createNewScene(scene: Scene, idOrder: List<Scene.Id>)
	suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene>
	suspend fun getSceneIdsInOrder(projectId: Project.Id): List<Scene.Id>
	suspend fun updateSceneOrder(projectId: Project.Id, order: List<Scene.Id>)
	suspend fun getSceneById(sceneId: Scene.Id): Scene?
	suspend fun getSceneForStoryEvent(storyEventId: StoryEvent.Id): Scene?
	suspend fun getSceneThatOwnsProse(proseId: Prose.Id): Scene?
	suspend fun updateScene(scene: Scene)
	suspend fun updateScenes(scenes: List<Scene>)
	suspend fun removeScene(scene: Scene)
	suspend fun getScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene>

}