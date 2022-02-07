package com.soyle.stories.usecase.storyevent.character.involve

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class GetAvailableCharactersToInvolveInStoryEventUseCase(
    private val storyEvents: StoryEventRepository,
    private val characters: CharacterRepository
) : GetAvailableCharactersToInvolveInStoryEvent {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        output: GetAvailableCharactersToInvolveInStoryEvent.OutputPort
    ): Throwable? {
        return runCatching { storyEvents.getStoryEventOrError(storyEventId) }
            .map { AvailableCharactersToInvolveInStoryEvent(it.id, collectCharacters(it).toSet()) }
            .onSuccess { output.receiveAvailableCharacters(it) }
            .exceptionOrNull()
    }

    private suspend fun collectCharacters(storyEvent: StoryEvent): List<AvailableStoryElementItem<Character.Id>> {
        return characters.listCharactersInProject(storyEvent.projectId)
            .filterNot { storyEvent.involvedCharacters.containsEntityWithId(it.id) }
            .flatMap(::collectNames)
    }

    private fun collectNames(character: Character) = character.names.map { name ->
        AvailableStoryElementItem(
            character.id.mentioned(),
            name.value,
            character.displayName.takeUnless { name == it }?.value
        )
    }
}