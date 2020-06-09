package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.toProperty

class BaseStoryStructureModel : Model<BaseStoryStructureScope, BaseStoryStructureViewModel>(BaseStoryStructureScope::class) {

    val sections = bindImmutableList(BaseStoryStructureViewModel::sections)
    val availableLocations = bindImmutableList(BaseStoryStructureViewModel::availableLocations)
    val locationsAvailable = bind { item?.availableLocations?.isNotEmpty().toProperty() }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}