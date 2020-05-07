package com.soyle.stories.scene.usecases.createNewScene

import com.soyle.stories.scene.SceneException
import java.util.*

interface CreateNewScene {

	suspend operator fun invoke(name: String, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val sceneName: String)

	interface OutputPort {
		fun receiveCreateNewSceneFailure(failure: SceneException)
		fun receiveCreateNewSceneResponse(response: ResponseModel)
	}

}