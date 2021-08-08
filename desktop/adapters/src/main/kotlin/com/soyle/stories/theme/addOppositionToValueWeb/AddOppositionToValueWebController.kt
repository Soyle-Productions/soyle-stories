package com.soyle.stories.theme.addOppositionToValueWeb

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

interface AddOppositionToValueWebController {

    fun addOpposition(valueWebId: String): Deferred<OppositionAddedToValueWeb>
    fun addOpposition(valueWebId: String, name: NonBlankString): Deferred<OppositionAddedToValueWeb>
    fun addOppositionWithCharacter(valueWebId: String, name: NonBlankString, characterId: String): Deferred<OppositionAddedToValueWeb>

}