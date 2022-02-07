package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.scene.outline.SceneOutlineView
import com.soyle.stories.scene.outline.remove.ConfirmRemoveStoryEventFromScenePromptView

fun SceneOutlineView.givenConfirmRemoveStoryEventFromSceneDialogHasBeenOpened(storyEventId: StoryEvent.Id): ConfirmRemoveStoryEventFromScenePromptView {
    return getOpenConfirmRemoveStoryEventFromSceneDialog(focusedSceneId!!, storyEventId) ?: run {
        openConfirmRemoveStoryEventFromSceneDialog(storyEventId)
        awaitWithTimeout(1000) { getOpenConfirmRemoveStoryEventFromSceneDialog(focusedSceneId!!, storyEventId) != null }
        getOpenConfirmRemoveStoryEventFromSceneDialogOrError(focusedSceneId!!, storyEventId)
    }
}

fun getOpenConfirmRemoveStoryEventFromSceneDialog(
    sceneId: Scene.Id,
    storyEventId: StoryEvent.Id
): ConfirmRemoveStoryEventFromScenePromptView? {
    return robot.getOpenDialog {
        it.viewModel.sceneId == sceneId && it.viewModel.storyEventId == storyEventId
    }
}

fun getOpenConfirmRemoveStoryEventFromSceneDialogOrError(
    sceneId: Scene.Id,
    storyEventId: StoryEvent.Id
): ConfirmRemoveStoryEventFromScenePromptView =
    getOpenConfirmRemoveStoryEventFromSceneDialog(sceneId, storyEventId) ?: error("Confirm Remove $storyEventId from $sceneId Dialog is not open")
