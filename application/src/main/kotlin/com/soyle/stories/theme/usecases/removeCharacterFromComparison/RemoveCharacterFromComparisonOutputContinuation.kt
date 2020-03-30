package com.soyle.stories.theme.usecases.removeCharacterFromComparison

import com.soyle.stories.theme.ThemeException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RemoveCharacterFromComparisonOutputContinuation(private val continuation: Continuation<RemoveCharacterFromComparison.ResponseModel>) : RemoveCharacterFromComparison.OutputPort {
    override fun receiveRemoveCharacterFromComparisonFailure(failure: ThemeException) {
        continuation.resumeWithException(failure)
    }

    override fun receiveRemoveCharacterFromComparisonResponse(response: RemoveCharacterFromComparison.ResponseModel) {
        continuation.resume(response)
    }
}