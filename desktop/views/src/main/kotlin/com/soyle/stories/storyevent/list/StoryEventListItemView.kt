package com.soyle.stories.storyevent.list

import com.soyle.stories.common.components.text.TextStyles
import javafx.beans.value.ObservableValue
import javafx.scene.layout.HBox
import tornadofx.addClass
import tornadofx.dynamicContent
import tornadofx.label
import tornadofx.spacer

class StoryEventListItemView(
    viewModel: StoryEventListItemViewModel
) : HBox() {

    init {
        label(viewModel.nameProperty) {
            addClass(StoryEventListStyles.name)
            addClass(TextStyles.fieldLabel)
        }
        spacer()
        label(viewModel.timeProperty) {
            addClass(StoryEventListStyles.time)
        }
    }

}