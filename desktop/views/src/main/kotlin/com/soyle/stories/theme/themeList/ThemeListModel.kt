package com.soyle.stories.theme.themeList

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleObjectProperty
import tornadofx.select
import tornadofx.selectBoolean
import tornadofx.toProperty

class ThemeListModel : Model<ProjectScope, ThemeListViewModel>(ProjectScope::class) {

    val themes = bind(ThemeListViewModel::themes)
    val hasThemes = themes.select { (! it.isNullOrEmpty()).toProperty() }
    val emptyMessage = bind(ThemeListViewModel::emptyMessage)
    val createFirstThemeButtonLabel = bind(ThemeListViewModel::createFirstThemeButtonLabel)
    val createThemeButtonLabel = bind(ThemeListViewModel::createThemeButtonLabel)
    val createSymbolButtonLabel = bind(ThemeListViewModel::createSymbolButtonLabel)
    val deleteButtonLabel = bind(ThemeListViewModel::deleteButtonLabel)

    val selectedItem = SimpleObjectProperty<Any?>(null)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope
}