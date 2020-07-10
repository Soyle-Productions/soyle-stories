package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogScope
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogViewModel

class AddSymbolDialogModel : Model<AddSymbolDialogScope, AddSymbolDialogViewModel>(AddSymbolDialogScope::class) {

    val characters = bindImmutableList(AddSymbolDialogViewModel::characters)
    val locations = bindImmutableList(AddSymbolDialogViewModel::locations)
    val symbols = bindImmutableList(AddSymbolDialogViewModel::symbols)
    val completed = bind(AddSymbolDialogViewModel::completed)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope
}