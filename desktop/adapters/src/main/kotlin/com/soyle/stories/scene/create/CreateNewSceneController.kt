package com.soyle.stories.scene.create

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface CreateNewSceneController {

    fun create(linkedLocation: Location.Id? = null): Job
    fun before(sceneId: Scene.Id): Job
    fun after(sceneId: Scene.Id): Job

    companion object {
        fun Implementation(
            projectId: Project.Id,

            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            prompt: CreateScenePrompt,

            createNewScene: CreateNewScene,
            createNewSceneOutput: CreateNewScene.OutputPort,

            linkLocationToSceneController: LinkLocationToSceneController
        ): CreateNewSceneController = object : CreateNewSceneController, CoroutineScope by CoroutineScope(guiContext) {
            override fun create(linkedLocation: Location.Id?): Job = launch {
                val name = prompt.requestSceneName() ?: return@launch
                createScene(CreateNewScene.RequestModel(name, projectId), linkedLocation)
            }

            override fun before(sceneId: Scene.Id): Job = launch {
                val name = prompt.requestSceneName() ?: return@launch
                val request = CreateNewScene.RequestModel(name, projectId).before(sceneId)
                createScene(request, null)
            }

            override fun after(sceneId: Scene.Id): Job = launch {
                val name = prompt.requestSceneName() ?: return@launch
                val request = CreateNewScene.RequestModel(name, projectId).after(sceneId)
                createScene(request, null)
            }

            private suspend fun createScene(request: CreateNewScene.RequestModel, linkedLocation: Location.Id?) {
                prompt.use {
                    withContext(asyncContext) {
                        createNewScene(request) {
                            createNewSceneOutput.newSceneCreated(it)
                            if (linkedLocation != null) {
                                linkLocationToSceneController.linkLocationToScene(
                                    it.sceneCreated.sceneId,
                                    linkedLocation
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}