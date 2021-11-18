package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredBySceneUseCase
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class `Story Events Created with Scenes are Covered` {

    val projectId = Project.Id()

    var savedScene: Scene? = null
    var result: Any? = null

    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val proseRepository = ProseRepositoryDouble()

    private val createNewScene = CreateNewSceneUseCase(
        SceneOrderService(),
        StoryEventTimeService(storyEventRepository),
        sceneRepository,
        proseRepository,
        storyEventRepository
    )

    @Test
    fun `should list created story event after creating scene`() {
        var createSceneId: Scene.Id? = null

        runBlocking {
            createNewScene.invoke(CreateNewScene.RequestModel(nonBlankStr("Some name"), projectId)) {
                createSceneId = it.sceneCreated.sceneId
            }
        }

        var storyEventsInScene: StoryEventsInScene? = null

        runBlocking {
            ListStoryEventsCoveredBySceneUseCase(sceneRepository, storyEventRepository).invoke(createSceneId!!) {
                storyEventsInScene = it
            }
        }

        storyEventsInScene!!.sceneId.mustEqual(createSceneId!!)
        storyEventsInScene!!.size.mustEqual(1)
    }

}