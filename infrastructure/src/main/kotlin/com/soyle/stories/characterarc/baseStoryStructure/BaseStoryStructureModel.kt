package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.observable
import tornadofx.select
import tornadofx.toProperty

class BaseStoryStructureModel : Model<BaseStoryStructureScope, BaseStoryStructureViewModel>(BaseStoryStructureScope::class) {

    val sections = bind(BaseStoryStructureViewModel::sections)
    val availableLocations = bind(BaseStoryStructureViewModel::availableLocations)
    val locationsAvailable = availableLocations.select { it.observable(List<LocationItemViewModel>::isEmpty) }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}