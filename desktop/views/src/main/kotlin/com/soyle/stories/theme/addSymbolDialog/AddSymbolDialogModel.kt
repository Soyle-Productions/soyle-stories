package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogScope
import com.soyle.stories.theme.addSymbolDialog.AddSymbolDialogViewModel

class AddSymbolDialogModel : Model<AddSymbolDialogScope, AddSymbolDialogViewModel>(AddSymbolDialogScope::class) {

    val characters = bind(AddSymbolDialogViewModel::characters)
    val locations = bind(AddSymbolDialogViewModel::locations)
    val symbols = bind(AddSymbolDialogViewModel::symbols)
    val completed = bind(AddSymbolDialogViewModel::completed)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope
}