package com.soyle.stories.prose

import com.soyle.stories.common.EntityNotFoundException
import com.soyle.stories.common.ValidationException
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMention

data class ProseDoesNotExist(val proseId: Prose.Id) : EntityNotFoundException(proseId.uuid)
class ProseMentionCannotBeBisected() : ValidationException()
class MentionOverlapsExistingMention : ValidationException()
class MentionDoesNotExistInProse(val proseId: Prose.Id, val mention: ProseMention<*>) : NoSuchElementException()