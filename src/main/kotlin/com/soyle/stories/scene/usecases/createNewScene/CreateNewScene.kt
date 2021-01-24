package com.soyle.stories.scene.usecases.createNewScene

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Prose
import com.soyle.stories.scene.Locale
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent
import java.util.*

interface CreateNewScene {

	class RequestModel private constructor(
		val name: NonBlankString,
		val storyEventId: UUID?,
		val relativeToScene: Pair<UUID, Boolean>?,
		val locale: Locale
	) {

		constructor(name: NonBlankString, locale: Locale) : this(name, null, null, locale)
		constructor(name: NonBlankString, storyEventId: UUID, locale: Locale) : this(name, storyEventId, null, locale)
		constructor(name: NonBlankString, sceneId: UUID, insertBefore: Boolean, locale: Locale) : this(name, null, sceneId to insertBefore, locale)

	}

	suspend operator fun invoke(request: RequestModel, output: OutputPort)

	class ResponseModel(val sceneId: UUID, val sceneProse: Prose.Id, val sceneName: String, val sceneIndex: Int, val affectedScenes: List<SceneItem>)

	interface OutputPort {
		val createStoryEventOutputPort: CreateStoryEvent.OutputPort
		fun receiveCreateNewSceneFailure(failure: Exception)
		fun receiveCreateNewSceneResponse(response: ResponseModel)
	}

}