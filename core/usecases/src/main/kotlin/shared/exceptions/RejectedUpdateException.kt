package com.soyle.stories.usecase.shared.exceptions

class RejectedUpdateException(override val message: String?, override val cause: Throwable?) : Exception()