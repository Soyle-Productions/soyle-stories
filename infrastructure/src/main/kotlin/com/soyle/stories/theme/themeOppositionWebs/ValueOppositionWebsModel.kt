package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.valueOppositionWebs.OppositionValueViewModel
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewModel
import com.soyle.stories.theme.valueOppositionWebs.ValueWebItemViewModel
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.onChange

class ValueOppositionWebsModel : Model<ValueOppositionWebsScope, ValueOppositionWebsViewModel>(ValueOppositionWebsScope::class) {

    val valueWebs = bindImmutableList(ValueOppositionWebsViewModel::valueWebs)
    val selectedValueWeb = bind(ValueOppositionWebsViewModel::selectedValueWeb) as SimpleObjectProperty
    val oppositionValues = bindImmutableList(ValueOppositionWebsViewModel::oppositionValues)
    val errorMessage = bind(ValueOppositionWebsViewModel::errorMessage) as SimpleStringProperty
    val errorSource = bind(ValueOppositionWebsViewModel::errorSource) as SimpleStringProperty

    val editingProperty = SimpleStringProperty(null)

    override fun viewModel(): ValueOppositionWebsViewModel? {
        return item?.copy(
            selectedValueWeb = selectedValueWeb.value,
            errorSource = null,
            errorMessage = null
        )
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope
}