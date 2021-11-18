package com.soyle.stories.scene.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface CreateNewSceneController {

	fun create(): Job
	fun before(sceneId: Scene.Id): Job
	fun after(sceneId: Scene.Id): Job

	companion object {
		fun Implementation(
			projectId: Project.Id,

			guiContext: CoroutineContext,
			asyncContext: CoroutineContext,

			prompt: CreateScenePrompt,

			createNewScene: CreateNewScene,
			createNewSceneOutput: CreateNewScene.OutputPort
		): CreateNewSceneController = object : CreateNewSceneController, CoroutineScope by CoroutineScope(guiContext) {
			override fun create(): Job = launch {
				val name = prompt.requestSceneName() ?: return@launch
				createScene(CreateNewScene.RequestModel(name, projectId))
			}

			override fun before(sceneId: Scene.Id): Job = launch {
				val name = prompt.requestSceneName() ?: return@launch
				val request = CreateNewScene.RequestModel(name, projectId).before(sceneId)
				createScene(request)
			}

			override fun after(sceneId: Scene.Id): Job = launch {
				val name = prompt.requestSceneName() ?: return@launch
				val request = CreateNewScene.RequestModel(name, projectId).after(sceneId)
				createScene(request)
			}

			private suspend fun createScene(request: CreateNewScene.RequestModel) {
				withContext(asyncContext) {
					createNewScene(request, createNewSceneOutput)
				}
				prompt.close()
			}
		}
	}

}