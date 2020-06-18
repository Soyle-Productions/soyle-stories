package com.soyle.stories.theme.themeList

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.toProperty

class ThemeListModel : Model<ProjectScope, ThemeListViewModel>(ProjectScope::class) {

    val themes = bindImmutableList(ThemeListViewModel::themes)
    val hasThemes = bind { (item?.themes?.isNotEmpty() ?: false).toProperty() }
    val emptyMessage = bind(ThemeListViewModel::emptyMessage)
    val createFirstThemeButtonLabel = bind(ThemeListViewModel::createFirstThemeButtonLabel)
    val createThemeButtonLabel = bind(ThemeListViewModel::createThemeButtonLabel)

    override val applicationScope: ApplicationScope
        get() = scope.applicationScope
}