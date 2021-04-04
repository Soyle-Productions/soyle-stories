package com.soyle.stories.domain.validation

import java.util.*

abstract class EntityNotFoundException(val entityId: UUID) : SoyleStoriesException() {
    open override fun getLocalizedMessage(): String = "Entity was not found $entityId"
}