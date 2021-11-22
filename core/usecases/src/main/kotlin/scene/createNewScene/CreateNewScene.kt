package com.soyle.stories.usecase.scene.createNewScene

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import java.util.*

interface CreateNewScene {

    class RequestModel private constructor(
        val name: NonBlankString,
        val projectId: Project.Id,
        private val relative: Pair<Scene.Id, Boolean>?
    ) {

        constructor(name: NonBlankString, projectId: Project.Id) : this(name, projectId, null)

        fun before(sceneId: Scene.Id) = RequestModel(name, projectId, sceneId to true)
        fun after(sceneId: Scene.Id) = RequestModel(name, projectId, sceneId to false)

        val isBeforeScene: Boolean
            get() = relative?.second == true

        val relativeSceneId: Scene.Id?
            get() = relative?.first
    }

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val sceneCreated: SceneCreated,
        val storyEventCreated: StoryEventCreated,
        val sceneOrderUpdated: SceneOrderUpdate.Successful<*>
    )

    fun interface OutputPort {
        suspend fun newSceneCreated(response: ResponseModel)
    }

}