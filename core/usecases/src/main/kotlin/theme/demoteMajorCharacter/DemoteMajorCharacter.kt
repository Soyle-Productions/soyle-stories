/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 3:42 PM
 */
package com.soyle.stories.usecase.theme.demoteMajorCharacter

import java.util.*

interface DemoteMajorCharacter {
    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    class ResponseModel(val themeId: UUID, val characterId: UUID, val removedCharacterArcSections: List<UUID>, val themeRemoved: Boolean)

    interface OutputPort {
        fun receiveDemoteMajorCharacterFailure(failure: Exception)
        fun receiveDemoteMajorCharacterResponse(response: ResponseModel)
    }
}