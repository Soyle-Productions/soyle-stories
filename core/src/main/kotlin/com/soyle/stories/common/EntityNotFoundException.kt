package com.soyle.stories.common

import java.util.*

abstract class EntityNotFoundException(val entityId: UUID) : SoyleStoriesException()