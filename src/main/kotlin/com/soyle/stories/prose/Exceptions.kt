package com.soyle.stories.prose

import com.soyle.stories.common.EntityNotFoundException
import com.soyle.stories.common.ValidationException
import com.soyle.stories.entities.ProseParagraph
import java.util.*

data class ProseDoesNotExist(val proseId: UUID) : EntityNotFoundException(proseId)
class ProseMentionCannotBeBisected() : ValidationException()
class MentionOverlapsExistingMention : ValidationException()