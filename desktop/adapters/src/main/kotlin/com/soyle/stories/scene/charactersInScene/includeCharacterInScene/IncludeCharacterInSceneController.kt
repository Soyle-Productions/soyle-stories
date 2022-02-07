package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.buildNewCharacter.CreateCharacterPrompt
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

interface IncludeCharacterInSceneController {

    fun includeCharacterInScene(
        sceneId: Scene.Id,
        storyEventPrompt: SelectStoryEventPrompt,
        selectCharacterPrompt: SelectCharacterPrompt,
        createCharacterPrompt: CreateCharacterPrompt
    ): Job

    class Implementation(
        private val mainContext: CoroutineContext,
        asyncContext: CoroutineContext,

        private val listAvailableCharactersToIncludeInScene: ListAvailableCharactersToIncludeInScene,
        private val listStoryEventsCoveredByScene: ListStoryEventsCoveredByScene,

        private val scenes: SceneRepository,
        private val characters: CharacterRepository,

        private val includeCharacterInScene: IncludeCharacterInScene,
        private val includeCharacterInSceneOutput: IncludeCharacterInScene.OutputPort,

        private val involveCharacterInStoryEvent: InvolveCharacterInStoryEvent,
        private val involveCharacterInStoryEventOutput: InvolveCharacterInStoryEvent.OutputPort,

        private val createStoryEvent: CreateStoryEvent,
        private val createStoryEventOutput: CreateStoryEvent.OutputPort,

        private val createCharacterController: BuildNewCharacterController
    ) : IncludeCharacterInSceneController, CoroutineScope by CoroutineScope(asyncContext) {

        override fun includeCharacterInScene(
            sceneId: Scene.Id,
            storyEventPrompt: SelectStoryEventPrompt,
            selectCharacterPrompt: SelectCharacterPrompt,
            createCharacterPrompt: CreateCharacterPrompt
        ): Job {
            return launch {
                includeCharacterInScene(
                    scenes.getSceneOrError(sceneId.uuid),
                    storyEventPrompt,
                    selectCharacterPrompt,
                    createCharacterPrompt
                )
            }
        }

        private suspend fun includeCharacterInScene(
            scene: Scene,
            storyEventPrompt: SelectStoryEventPrompt,
            selectCharacterPrompt: SelectCharacterPrompt,
            createCharacterPrompt: CreateCharacterPrompt,
        ) {
            listAvailableCharactersToIncludeInScene(scene.id) {
                val choice = withContext(mainContext) {
                    selectCharacterPrompt.selectCharacter(it)
                } ?: return@listAvailableCharactersToIncludeInScene
                when (choice) {
                    is SelectCharacterPrompt.CharacterSelection.CreateNew -> createCharacterAndInclude(
                        scene,
                        createCharacterPrompt,
                        storyEventPrompt
                    )
                    is SelectCharacterPrompt.CharacterSelection.Selected -> includeExistingCharacter(
                        scene,
                        choice.id,
                        storyEventPrompt
                    )
                }
                withContext(mainContext) { selectCharacterPrompt.done() }
            }
        }

        private suspend fun createCharacterAndInclude(
            scene: Scene,
            createCharacterPrompt: CreateCharacterPrompt,
            storyEventPrompt: SelectStoryEventPrompt,
        ) {
            val characterId = createCharacterController.createCharacter(createCharacterPrompt).await()?.getOrNull()
                ?: return
            includeCharacterInScene(scene.id, characterId, includeCharacterInSceneOutput)
            optionallyInvolveCharacterInStoryEvents(scene, characterId, storyEventPrompt)
        }

        private suspend fun includeExistingCharacter(
            scene: Scene,
            characterId: Character.Id,
            storyEventPrompt: SelectStoryEventPrompt,
        ) {
            includeCharacterInScene(scene.id, characterId, includeCharacterInSceneOutput)
            optionallyInvolveCharacterInStoryEvents(scene, characterId, storyEventPrompt)
        }

        private suspend fun optionallyInvolveCharacterInStoryEvents(
            scene: Scene,
            characterId: Character.Id,
            storyEventPrompt: SelectStoryEventPrompt,
        ) {
            val character = characters.getCharacterById(characterId)!!
            listStoryEventsCoveredByScene(scene.id) {
                val (storyEvents, newStoryEventSelection) = withContext(mainContext) {
                    storyEventPrompt.selectStoryEvent(character.displayName.value, it)
                } ?: return@listStoryEventsCoveredByScene
                storyEvents.forEach {
                    involveCharacterInStoryEvent(it.storyEventId, characterId, involveCharacterInStoryEventOutput)
                }
                if (newStoryEventSelection != null) {
                    val request = CreateStoryEvent.RequestModel(
                        newStoryEventSelection.name,
                        scene.projectId,
                        scene.id,
                        newStoryEventSelection.time
                    )
                    createStoryEvent(request) {
                        createStoryEventOutput.receiveCreateStoryEventResponse(it)
                        involveCharacterInStoryEvent(
                            it.createdStoryEvent.storyEventId,
                            characterId,
                            involveCharacterInStoryEventOutput
                        )
                    }
                }
                withContext(mainContext) { storyEventPrompt.done() }
            }
        }

    }

}