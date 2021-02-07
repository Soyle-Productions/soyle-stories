package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import java.util.*

interface SceneRepository {

    suspend fun createNewScene(scene: Scene, idOrder: List<Scene.Id>)
    suspend fun listAllScenesInProject(projectId: Project.Id): List<Scene>
    suspend fun getSceneIdsInOrder(projectId: Project.Id): List<Scene.Id>
    suspend fun updateSceneOrder(projectId: Project.Id, order: List<Scene.Id>)
    suspend fun getSceneById(sceneId: Scene.Id): Scene?
    suspend fun getSceneOrError(sceneId: UUID) = getSceneById(Scene.Id(sceneId))
        ?: throw SceneDoesNotExist(sceneId)

    suspend fun getSceneForStoryEvent(storyEventId: StoryEvent.Id): Scene?
    suspend fun getSceneThatOwnsProse(proseId: Prose.Id): Scene?
    suspend fun updateScene(scene: Scene)
    suspend fun updateScenes(scenes: List<Scene>)
    suspend fun removeScene(scene: Scene)
    suspend fun getScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene>

}