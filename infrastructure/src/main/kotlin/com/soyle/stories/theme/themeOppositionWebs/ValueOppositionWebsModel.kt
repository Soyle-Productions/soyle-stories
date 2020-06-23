package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.theme.valueOppositionWebs.ValueOppositionWebsViewModel
import com.soyle.stories.theme.valueOppositionWebs.ValueWebItemViewModel

class ValueOppositionWebsModel : Model<ValueOppositionWebsScope, ValueOppositionWebsViewModel>(ValueOppositionWebsScope::class) {

    val valueWebs = bindImmutableList(ValueOppositionWebsViewModel::valueWebs)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope
}