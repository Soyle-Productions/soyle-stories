/**
 * Created by Brendan
 * Date: 3/4/2020
 * Time: 10:37 PM
 */
package com.soyle.stories.theme.usecases.promoteMinorCharacter

import com.soyle.stories.theme.ThemeException
import java.util.*

interface PromoteMinorCharacter {

    class RequestModel(val themeId: UUID, val characterId: UUID, val characterArcName: String? = null)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(val themeId: UUID, val characterId: UUID, val characterArcName: String)

    interface OutputPort {
        fun receivePromoteMinorCharacterFailure(failure: ThemeException)
        fun receivePromoteMinorCharacterResponse(response: ResponseModel)
    }
}