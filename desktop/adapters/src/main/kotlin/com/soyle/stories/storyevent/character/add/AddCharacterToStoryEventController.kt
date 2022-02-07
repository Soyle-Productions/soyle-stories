package com.soyle.stories.storyevent.character.add

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.GetAvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface AddCharacterToStoryEventController {

    fun addCharacterToStoryEvent(storyEventId: StoryEvent.Id, prompt: SelectCharacterPrompt): Job

    class Implementation(
        mainContext: CoroutineContext,
        private val asyncContext: CoroutineContext,

        private val storyEventRepository: StoryEventRepository,

        private val getAvailableCharacters: GetAvailableCharactersToInvolveInStoryEvent,
        private val involveCharacterInStoryEvent: InvolveCharacterInStoryEvent,
        private val involveCharacterInStoryEventOutput: InvolveCharacterInStoryEvent.OutputPort
    ) : AddCharacterToStoryEventController, CoroutineScope by CoroutineScope(mainContext) {

        override fun addCharacterToStoryEvent(storyEventId: StoryEvent.Id, prompt: SelectCharacterPrompt): Job =
            launch {
                val mainContext = coroutineContext
                val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
                withContext(asyncContext) {
                    getAvailableCharacters(storyEvent.id) {
                        withContext(mainContext) { prompt.selectCharacterToAddToStoryEvent(storyEvent, it) }
                    }
                }
            }

        private suspend fun SelectCharacterPrompt.selectCharacterToAddToStoryEvent(
            storyEvent: StoryEvent,
            availableCharacters: AvailableCharactersToInvolveInStoryEvent
        ) {
            val characterId = selectCharacter(availableCharacters) ?: return
            involveCharacterInStoryEvent.invoke(storyEvent.id, characterId, involveCharacterInStoryEventOutput)
        }
    }

}