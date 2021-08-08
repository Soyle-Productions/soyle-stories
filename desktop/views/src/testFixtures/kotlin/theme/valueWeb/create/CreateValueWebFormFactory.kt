package com.soyle.stories.desktop.view.theme.valueWeb.create

import com.soyle.stories.desktop.view.theme.valueWeb.AddValueWebToThemeControllerDouble
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme

class CreateValueWebFormFactory(
    val onInvoke: (Theme.Id, suspend (ValueWebAddedToTheme) -> Unit) -> Unit = {_,_ ->}
) : CreateValueWebForm.Factory {

    override fun invoke(
        themeId: Theme.Id,
        onCreateValueWeb: suspend (ValueWebAddedToTheme) -> Unit
    ): CreateValueWebForm {
        onInvoke(themeId, onCreateValueWeb)
        return CreateValueWebForm(
            themeId,
            onCreateValueWeb,
            CreateValueWebFormLocaleMock(),
            AddValueWebToThemeControllerDouble()
        )
    }
}