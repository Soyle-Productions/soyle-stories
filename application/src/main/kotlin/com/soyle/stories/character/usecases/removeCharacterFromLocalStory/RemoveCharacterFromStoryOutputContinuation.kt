package com.soyle.stories.character.usecases.removeCharacterFromLocalStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import kotlin.coroutines.Continuation

class RemoveCharacterFromStoryOutputContinuation(
    private val continuation: Continuation<RemoveCharacterFromStory.ResponseModel>
) : RemoveCharacterFromStory.OutputPort {
    override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {
        continuation.resumeWith(Result.failure(failure))
    }

    override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
        continuation.resumeWith(Result.success(response))
    }
}