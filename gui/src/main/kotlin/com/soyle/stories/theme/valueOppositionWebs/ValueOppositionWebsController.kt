package com.soyle.stories.theme.valueOppositionWebs

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.theme.renameOppositionValue.RenameOppositionValueController
import com.soyle.stories.theme.usecases.addOppositionToValueWeb.AddOppositionToValueWeb
import com.soyle.stories.theme.usecases.listOppositionsInValueWeb.ListOppositionsInValueWeb
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInTheme
import java.util.*

class ValueOppositionWebsController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val listValueWebsInTheme: ListValueWebsInTheme,
    private val listOppositionsInValueWeb: ListOppositionsInValueWeb,
    private val addOppositionToValueWebController: AddOppositionToValueWebController,
    private val renameOppositionValueController: RenameOppositionValueController,
    private val presenter: ValueOppositionWebsPresenter
) : ValueOppositionWebsViewListener {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            listValueWebsInTheme.invoke(
                themeId,
                presenter
            )
        }
    }

    override fun selectValueWeb(valueWebId: String) {
        val preparedValueWebId = UUID.fromString(valueWebId)
        threadTransformer.async {
            listOppositionsInValueWeb.invoke(
                preparedValueWebId,
                presenter
            )
        }
    }

    override fun addOpposition(valueWebId: String) {
        addOppositionToValueWebController.addOpposition(valueWebId)
    }

    override fun renameOppositionValue(oppositionId: String, name: String) {
        renameOppositionValueController.renameOpposition(oppositionId, name) {
            presenter.presentError(oppositionId, it)
        }
    }

}