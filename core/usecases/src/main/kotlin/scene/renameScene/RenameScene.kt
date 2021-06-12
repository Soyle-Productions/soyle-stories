package com.soyle.stories.usecase.scene.renameScene

import com.soyle.stories.domain.location.events.HostedSceneRenamed
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameScene {

	class RequestModel(val sceneId: UUID, val name: NonBlankString, val locale: SceneLocale)

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(
		val sceneId: Scene.Id,
		val requestedName: String,
		val sceneRenamed: SceneRenamed?,
		val hostedScenesRenamed: List<HostedSceneRenamed>
	)

	interface OutputPort {
		suspend fun receiveRenameSceneFailure(failure: Exception)
		suspend fun receiveRenameSceneResponse(response: ResponseModel)
	}

}
