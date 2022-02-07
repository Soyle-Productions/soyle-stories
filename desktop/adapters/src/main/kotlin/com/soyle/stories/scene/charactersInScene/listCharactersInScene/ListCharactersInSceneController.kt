package com.soyle.stories.scene.charactersInScene.listCharactersInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.list.ListCharactersInScene
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface ListCharactersInSceneController {

    fun getCharactersInScene(sceneId: Scene.Id, output: ListCharactersInScene.OutputPort): Job

    class Implementation(
        private val mainContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val listCharactersInScene: ListCharactersInScene
    ) : ListCharactersInSceneController, CoroutineScope by CoroutineScope(asyncContext) {
        override fun getCharactersInScene(sceneId: Scene.Id, output: ListCharactersInScene.OutputPort): Job {
            return launch {
                listCharactersInScene.invoke(sceneId) {
                    withContext(mainContext) {
                        output.receiveCharactersInScene(it)
                    }
                }

            }
        }
    }

}