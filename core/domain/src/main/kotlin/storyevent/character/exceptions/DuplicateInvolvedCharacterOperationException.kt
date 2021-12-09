package com.soyle.stories.domain.storyevent.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateInvolvedCharacterOperationException(
    override val characterId: Character.Id,
    override val storyEventId: StoryEvent.Id,
    override val message: String
) : DuplicateOperationException(), InvolvedCharacterException

internal fun storyEventAlreadyInvolvesCharacter(storyEventId: StoryEvent.Id, characterId: Character.Id) =
    DuplicateInvolvedCharacterOperationException(characterId, storyEventId, "$storyEventId already involves $characterId")


internal fun storyEventAlreadyWithoutCharacter(storyEventId: StoryEvent.Id, characterId: Character.Id) =
    DuplicateInvolvedCharacterOperationException(characterId, storyEventId, "$storyEventId already without $characterId")

internal fun involvedCharacterAlreadyHasName(storyEventId: StoryEvent.Id, characterId: Character.Id, name: String) =
    DuplicateInvolvedCharacterOperationException(characterId, storyEventId, "Involved $characterId in $storyEventId already has name \"$name\"")