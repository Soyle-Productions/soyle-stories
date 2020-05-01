/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:53 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.resolveLater
import com.soyle.stories.gui.ThreadTransformer
import tornadofx.ItemViewModel
import tornadofx.rebind
import tornadofx.toProperty

class BaseStoryStructureModel : ItemViewModel<BaseStoryStructureViewModel>(), BaseStoryStructureView {

    override val scope: BaseStoryStructureScope = super.scope as BaseStoryStructureScope

    val sections = bindImmutableList(BaseStoryStructureViewModel::sections)
    val availableLocations = bindImmutableList(BaseStoryStructureViewModel::availableLocations)
    val locationsAvailable = bind { item?.availableLocations?.isNotEmpty().toProperty() }

    private val threadTransformer: ThreadTransformer by resolveLater(scope.projectScope.applicationScope)

    override fun update(update: BaseStoryStructureViewModel?.() -> BaseStoryStructureViewModel) {
        threadTransformer.gui {
            rebind { item = item.update() }
        }
    }

    override fun updateOrInvalidated(update: BaseStoryStructureViewModel.() -> BaseStoryStructureViewModel) {
        threadTransformer.gui {
            rebind { item = item?.update() }
        }
    }
}