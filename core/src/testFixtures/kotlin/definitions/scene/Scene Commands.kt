package com.soyle.stories.core.definitions.scene

import com.soyle.stories.core.definitions.scene.character.`Characters in Scene Commands`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.usecase.scene.delete.DeleteSceneUseCase
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger

class `Scene Commands`(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val proseRepository: ProseRepository,
    private val locationRepository: LocationRepository,
    private val characterRepository: CharacterRepository
) : `Scene Steps`.When,
    `Scene Character Steps`.When by `Characters in Scene Commands`(sceneRepository, storyEventRepository, characterRepository)
{

    override fun `a scene`(named: String, atIndex: Int?): `Scene Steps`.When.CreationActions = object :
        `Scene Steps`.When.CreationActions {
        override fun `is created in the`(projectId: Project.Id): Scene.Id {
            val deferred = CompletableDeferred<Scene.Id>()
            val useCase = CreateNewSceneUseCase(
                SceneOrderService(),
                StoryEventTimeService(storyEventRepository),
                sceneRepository,
                proseRepository,
                storyEventRepository
            )
            runBlocking {
                val request = CreateNewScene.RequestModel(nonBlankStr(named), projectId).run {
                    if (atIndex != null) {
                        before(sceneRepository.getSceneIdsInOrder(projectId)!!.order.toList()[atIndex])
                    } else this
                }
                useCase.invoke(request) {
                    Logger.getGlobal().info(it.sceneCreated.toString())
                    Logger.getGlobal().info(it.sceneOrderUpdated.change.toString())
                    Logger.getGlobal().info(it.storyEventCreated.toString())
                    Logger.getGlobal().info(it.storyEventCoveredByScene.toString())
                    deferred.complete(sceneRepository.getSceneOrError(it.sceneCreated.sceneId.uuid).id)
                }
            }
            return runBlocking { deferred.await() }
        }
    }

    override fun the(scene: Scene.Id): `Scene Steps`.When.SceneActions = object : `Scene Steps`.When.SceneActions {
        override fun `is deleted`() {
            runBlocking {
                DeleteSceneUseCase(sceneRepository, locationRepository, storyEventRepository).invoke(scene) {
//                    Logger.getGlobal().info(it.sceneRemoved.toString())
//                    it.storyEventsUncovered.forEach {
//                        Logger.getGlobal().info(it.toString())
//                    }
//                    it.hostedScenesRemoved.forEach {
//                        Logger.getGlobal().info(it.toString())
//                    }
                }
            }
        }
    }
}