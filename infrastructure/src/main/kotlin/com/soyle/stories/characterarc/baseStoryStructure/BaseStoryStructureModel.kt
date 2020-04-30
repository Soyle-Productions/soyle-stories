/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:53 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.bindImmutableList
import javafx.application.Platform
import tornadofx.ItemViewModel
import tornadofx.rebind
import tornadofx.runLater
import tornadofx.toProperty

class BaseStoryStructureModel : ItemViewModel<BaseStoryStructureViewModel>(BaseStoryStructureViewModel()), BaseStoryStructureView {

    val sections = bindImmutableList(BaseStoryStructureViewModel::sections)
    val availableLocations = bindImmutableList(BaseStoryStructureViewModel::availableLocations)
    val locationsAvailable = bind { item.availableLocations.isNotEmpty().toProperty() }

    override fun update(update: BaseStoryStructureViewModel.() -> BaseStoryStructureViewModel) {
        if (! Platform.isFxApplicationThread()) return runLater { update(update) }
        rebind { item = item.update() }
    }
}