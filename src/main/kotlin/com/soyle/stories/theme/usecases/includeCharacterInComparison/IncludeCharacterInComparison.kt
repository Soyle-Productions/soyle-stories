package com.soyle.stories.theme.usecases.includeCharacterInComparison

import java.util.*

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 2:47 PM
 */
interface IncludeCharacterInComparison {
    suspend operator fun invoke(characterId: UUID, themeId: UUID, output: OutputPort)

    class ResponseModel(
        val themeId: UUID,
        val characterId: UUID,
        val includedCharacters: List<com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem>
    )

    interface OutputPort {
        fun receiveIncludeCharacterInComparisonFailure(failure: Exception)
        fun receiveIncludeCharacterInComparisonResponse(response: ResponseModel)
    }
}