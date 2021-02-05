package com.soyle.stories.scene.usecases.deleteScene

import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneException
import java.util.*

interface DeleteScene {

	suspend operator fun invoke(sceneId: UUID, locale: Locale, output: OutputPort)

	class ResponseModel(val sceneId: UUID)

	interface OutputPort
	{
		fun receiveDeleteSceneFailure(failure: SceneException)
		fun receiveDeleteSceneResponse(responseModel: ResponseModel)
	}

}