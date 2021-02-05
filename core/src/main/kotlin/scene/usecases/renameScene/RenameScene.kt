package com.soyle.stories.scene.usecases.renameScene

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.SceneException
import java.util.*

interface RenameScene {

	class RequestModel(val sceneId: UUID, val name: NonBlankString, val locale: Locale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameSceneFailure(failure: SceneException)
		fun receiveRenameSceneResponse(response: ResponseModel)
	}

}