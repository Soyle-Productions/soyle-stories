package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.character.list.ListCharactersInScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RemoveCharacterFromSceneControllerImpl(
	asyncContext: CoroutineContext,
	private val mainContext: CoroutineContext,
	private val sceneRepository: SceneRepository,
	private val characterRepository: CharacterRepository,
	private val listCharactersInScene: ListCharactersInScene,
	private val listStoryEventsInScene: ListStoryEventsCoveredByScene,
	private val removeCharacterFromScene: RemoveCharacterFromScene,
	private val removeCharacterFromSceneOutput: RemoveCharacterFromScene.OutputPort
) : RemoveCharacterFromSceneController {

	private val scope: CoroutineScope = CoroutineScope(asyncContext)

	override fun removeCharacterFromScene(
		sceneId: Scene.Id,
		characterId: Character.Id,
		confirmationPrompt: RemoveCharacterConfirmationPrompt
	): Job {
		return scope.launch {
			val scene = sceneRepository.getSceneOrError(sceneId.uuid)
			val character = characterRepository.getCharacterOrError(characterId.uuid)
			listStoryEventsInScene(sceneId) { storyEvents ->
				val storyEventsInvolvingCharacter = storyEvents.filter { storyEvent ->
					storyEvent.involvedCharacters.any { it.characterId == characterId.uuid }
				}
				if (storyEventsInvolvingCharacter.isNotEmpty()) {
					withContext(mainContext) {
						confirmationPrompt.confirmRemoval(
							scene.name.value,
							character.displayName.value,
							storyEventsInvolvingCharacter
						)
					}
				}
				removeCharacterFromScene.invoke(sceneId, characterId, removeCharacterFromSceneOutput)
			}
		}
	}

	override fun removeCharacterFromScene(
		sceneId: Scene.Id,
		selectCharacterPrompt: SelectCharacterPrompt,
		confirmationPrompt: RemoveCharacterConfirmationPrompt
	): Job {
		return scope.launch {
			listCharactersInScene(sceneId) {
				val characterId = withContext(mainContext) { selectCharacterPrompt.selectCharacter(it) }
					?: return@listCharactersInScene
				removeCharacterFromScene(sceneId, characterId, confirmationPrompt).join()
			}
		}
	}

	fun finalize() { scope.cancel() }

}