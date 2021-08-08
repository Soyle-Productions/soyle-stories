package com.soyle.stories.desktop.view.theme.valueWeb.opposition.create

import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme

class CreateOppositionValueFormFactory(
    val onInvoke: (ValueWeb.Id, suspend (OppositionAddedToValueWeb) -> Unit) -> Unit = {_,_ ->}
) : CreateOppositionValueForm.Factory {

    override fun invoke(
        valueWebId: ValueWeb.Id,
        onCreateOppositionValue: suspend (OppositionAddedToValueWeb) -> Unit
    ): CreateOppositionValueForm {
        onInvoke(valueWebId, onCreateOppositionValue)
        return CreateOppositionValueForm(
            valueWebId,
            onCreateOppositionValue,
            CreateOppositionValueFormLocaleMock(),
            AddOppositionToValueWebControllerDouble()
        )
    }
}