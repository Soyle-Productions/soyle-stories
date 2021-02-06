package com.soyle.stories.domain.validation

import java.util.*

abstract class EntityNotFoundException(val entityId: UUID) : SoyleStoriesException()