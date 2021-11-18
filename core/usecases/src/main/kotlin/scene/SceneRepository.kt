package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.*

interface SceneRepository {

    suspend fun createNewScene(scene: Scene)
    suspend fun listAllScenesInProject(projectId: Project.Id, exclude: Set<Scene.Id> = emptySet()): List<Scene>
    suspend fun getSceneIdsInOrder(projectId: Project.Id): SceneOrder?
    suspend fun updateSceneOrder(sceneOrder: SceneOrder)
    suspend fun getSceneById(sceneId: Scene.Id): Scene?
    suspend fun getSceneOrError(sceneId: UUID) = getSceneById(Scene.Id(sceneId))
        ?: throw SceneDoesNotExist(sceneId)

    suspend fun getSceneForStoryEvent(storyEventId: StoryEvent.Id): Scene?
    suspend fun getSceneThatOwnsProse(proseId: Prose.Id): Scene?
    suspend fun updateScene(scene: Scene)
    suspend fun updateScenes(scenes: List<Scene>) = coroutineScope {
        scenes.map {
            async { updateScene(it) }
        }.awaitAll()
    }

    suspend fun removeScene(sceneId: Scene.Id)
    suspend fun getScenesIncludingCharacter(characterId: Character.Id): List<Scene>
    suspend fun getScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene>
    suspend fun getScenesUsingLocation(locationId: Location.Id): List<Scene>
}
