package com.soyle.stories.storyevent.timeline.viewport.grid.label

import com.soyle.stories.storyevent.item.StoryEventItemMenuComponent
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import javafx.scene.control.ContextMenu

interface StoryPointLabelMenuComponent {

    fun StoryPointLabelMenu(selection: StoryEventItemSelection): ContextMenu
}

class StoryPointLabelMenuViewModel(
    val selection: StoryEventItemSelection
) {

}

fun storyPointLabelMenu(
    viewModel: StoryPointLabelMenuViewModel,

    storyEventItemMenu: StoryEventItemMenuComponent
) = storyEventItemMenu.StoryEventItemMenu(viewModel.selection)