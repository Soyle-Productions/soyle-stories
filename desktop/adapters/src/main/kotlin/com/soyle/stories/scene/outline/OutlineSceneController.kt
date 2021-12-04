package com.soyle.stories.scene.outline

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface OutlineSceneController {

    fun outlineScene(sceneId: Scene.Id): Job

    companion object {
        fun Implementation(
            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            sceneRepository: SceneRepository,

            getOutline: suspend (Scene.Id, String) -> SceneOutlineReport,

            listStoryEventsCoveredByScene: ListStoryEventsCoveredByScene
        ): OutlineSceneController  = object : OutlineSceneController, CoroutineScope by CoroutineScope(guiContext) {
            override fun outlineScene(sceneId: Scene.Id): Job = launch {
                // validate the scene
                val scene = withContext(asyncContext) {
                    sceneRepository.getSceneOrError(sceneId.uuid)
                }

                // Inform User Outline Is Being Loaded
                val outline = getOutline(scene.id, scene.name.value)

                // load story events and display result
                try {
                    withContext(asyncContext) {
                        listStoryEventsCoveredByScene(scene.id) {
                            withContext(guiContext) {
                                outline.receiveStoryEventsCoveredByScene(it)
                            }
                        }
                    }
                } catch (failure: Throwable) {
                    outline.displayFailure(failure)
                }
            }
        }
    }

}