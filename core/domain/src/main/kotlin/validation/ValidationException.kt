package com.soyle.stories.domain.validation

open class ValidationException(override val message: String? = null) : SoyleStoriesException()