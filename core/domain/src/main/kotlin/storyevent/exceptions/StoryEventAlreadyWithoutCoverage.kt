package com.soyle.stories.domain.storyevent.exceptions

import com.soyle.stories.domain.validation.DuplicateOperationException

object StoryEventAlreadyWithoutCoverage : DuplicateOperationException(), StoryEventException