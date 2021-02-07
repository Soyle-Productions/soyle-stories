package com.soyle.stories.usecase.scene.renameScene

import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameScene {

	class RequestModel(val sceneId: UUID, val name: NonBlankString, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameSceneFailure(failure: Exception)
		fun receiveRenameSceneResponse(response: ResponseModel)
	}

}