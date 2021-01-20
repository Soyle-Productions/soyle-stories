package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope

class BaseStoryStructureModel : Model<BaseStoryStructureScope, BaseStoryStructureViewModel>(BaseStoryStructureScope::class) {

    val sections = bind(BaseStoryStructureViewModel::sections)
    val availableLocations = bind(BaseStoryStructureViewModel::availableLocations)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}