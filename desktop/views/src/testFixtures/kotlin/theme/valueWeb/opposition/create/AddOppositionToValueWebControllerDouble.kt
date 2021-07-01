package com.soyle.stories.desktop.view.theme.valueWeb.opposition.create

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job

class AddOppositionToValueWebControllerDouble(
    var onAddOpposition: (String, NonBlankString?, String?) -> Deferred<OppositionAddedToValueWeb> = {_,_,_ -> CompletableDeferred() },
) : AddOppositionToValueWebController {

    override fun addOpposition(valueWebId: String): Deferred<OppositionAddedToValueWeb> {
        return onAddOpposition(valueWebId, null, null)
    }

    override fun addOpposition(valueWebId: String, name: NonBlankString): Deferred<OppositionAddedToValueWeb> {
        return onAddOpposition(valueWebId, name, null)

    }

    override fun addOppositionWithCharacter(valueWebId: String, name: NonBlankString, characterId: String): Deferred<OppositionAddedToValueWeb> {
        return onAddOpposition(valueWebId, name, characterId)
    }
}