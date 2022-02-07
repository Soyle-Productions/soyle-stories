package com.soyle.stories.scene.charactersInScene.inspect

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.inspect.InspectCharacterInScene
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface InspectCharacterInSceneController {

    fun inspectCharacter(sceneId: Scene.Id, characterId: Character.Id, output: InspectCharacterInScene.OutputPort): Job

    class Implementation(
        asyncContext: CoroutineContext,

        private val inspectCharacterInScene: InspectCharacterInScene
    ) : InspectCharacterInSceneController {

        private val scope = CoroutineScope(asyncContext)

        override fun inspectCharacter(
            sceneId: Scene.Id,
            characterId: Character.Id,
            output: InspectCharacterInScene.OutputPort
        ): Job {
            return scope.launch {
                inspectCharacterInScene.invoke(sceneId, characterId, output)
            }
        }

        protected fun finalize() { scope.cancel() }

    }

}