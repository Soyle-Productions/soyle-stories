package com.soyle.stories.storyevent.character.add

import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent
import com.soyle.stories.scene.charactersInScene.involve.CharacterInvolvedInSceneNotifier
import com.soyle.stories.scene.charactersInScene.source.added.SourceAddedToCharacterInSceneNotifier
import com.soyle.stories.usecase.scene.character.involve.InvolveCharacterInScene
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEvent

class AddCharacterToStoryEventOutput(
	private val characterInvolvedInStoryEventReceiver: IncludedCharacterInStoryEventReceiver,
	private val characterInvolvedInSceneNotifier: CharacterInvolvedInSceneNotifier,
	private val sourceAddedToCharacterInSceneNotifier: SourceAddedToCharacterInSceneNotifier,

	private val involveCharacterInScene: InvolveCharacterInScene,
	private val involveCharacterInSceneOutput: InvolveCharacterInScene.OutputPort
) : InvolveCharacterInStoryEvent.OutputPort {

	override suspend fun characterInvolvedInStoryEvent(characterInvolved: CharacterInvolvedInStoryEvent) {
		characterInvolvedInStoryEventReceiver.receiveCharacterInvolvedInStoryEvent(characterInvolved)
		involveCharacterInScene(characterInvolved, involveCharacterInSceneOutput)
	}
}