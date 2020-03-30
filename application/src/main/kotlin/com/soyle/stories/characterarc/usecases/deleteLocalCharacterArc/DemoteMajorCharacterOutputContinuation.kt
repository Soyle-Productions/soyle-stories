package com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc

import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DemoteMajorCharacterOutputContinuation(private val continuation: Continuation<DemoteMajorCharacter.ResponseModel>) : DemoteMajorCharacter.OutputPort {
    override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
        continuation.resume(response)
    }

    override fun receiveDemoteMajorCharacterFailure(failure: Exception) {
        continuation.resumeWithException(failure)
    }
}