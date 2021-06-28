package com.soyle.stories.usecase.scene.createNewScene

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.storyevent.createStoryEvent.CreateStoryEvent
import java.util.*

interface CreateNewScene {

	class RequestModel private constructor(
		val name: NonBlankString,
		val storyEventId: UUID?,
		val relativeToScene: Pair<UUID, Boolean>?,
		val locale: SceneLocale
	) {

		constructor(name: NonBlankString, locale: SceneLocale) : this(name, null, null, locale)
		constructor(name: NonBlankString, storyEventId: UUID, locale: SceneLocale) : this(name, storyEventId, null, locale)
		constructor(name: NonBlankString, sceneId: UUID, insertBefore: Boolean, locale: SceneLocale) : this(name, null, sceneId to insertBefore, locale)

	}

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val sceneProse: Prose.Id, val sceneName: String, val sceneIndex: Int, val affectedScenes: List<SceneItem>)

	interface OutputPort {
		val createStoryEventOutputPort: CreateStoryEvent.OutputPort
		fun receiveCreateNewSceneFailure(failure: Exception)
		fun receiveCreateNewSceneResponse(response: ResponseModel)
	}

}