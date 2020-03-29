/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 10:34 PM
 */
package com.soyle.stories.character.usecases.removeCharacterFromStory

import java.util.*

interface RemoveCharacterFromStory {
    suspend operator fun invoke(characterId: UUID, output: OutputPort)

    class ResponseModel(val characterId: UUID, val removedThemeIds: List<UUID>, val affectedThemeIds: List<UUID>)

    interface OutputPort {
        fun receiveRemoveCharacterFromStoryFailure(failure: Exception)
        fun receiveRemoveCharacterFromStoryResponse(response: ResponseModel)
    }
}