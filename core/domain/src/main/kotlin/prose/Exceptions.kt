package com.soyle.stories.domain.prose

import com.soyle.stories.domain.validation.EntityNotFoundException
import com.soyle.stories.domain.validation.ValidationException

data class ProseDoesNotExist(val proseId: Prose.Id) : EntityNotFoundException(proseId.uuid)
class ProseMentionCannotBeBisected() : ValidationException()
class MentionOverlapsExistingMention : ValidationException()
class MentionDoesNotExistInProse(val proseId: Prose.Id, val mention: ProseMention<*>) : NoSuchElementException()