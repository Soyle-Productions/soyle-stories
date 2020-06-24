package com.soyle.stories.theme.valueOppositionWebs

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.ValueWebAddedToTheme
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ListValueWebsInTheme
import com.soyle.stories.theme.usecases.listValueWebsInTheme.ValueWebList
import java.util.*

class ValueOppositionWebsPresenter(
    themeId: String,
    private val view: View.Nullable<ValueOppositionWebsViewModel>
) : ListValueWebsInTheme.OutputPort, AddValueWebToTheme.OutputPort {

    private val themeId = UUID.fromString(themeId)

    override suspend fun valueWebsListedInTheme(response: ValueWebList) {
        view.update {
            ValueOppositionWebsViewModel(
                response.valueWebs.map {
                    ValueWebItemViewModel(it.valueWebId.toString(), it.valueWebName)
                }
            )
        }
    }

    override suspend fun addedValueWebToTheme(response: ValueWebAddedToTheme) {
        if (themeId != response.themeId) return
        view.updateOrInvalidated {
            copy(
                valueWebs = valueWebs + ValueWebItemViewModel(response.valueWebId.toString(), response.valueWebName)
            )
        }
    }

}