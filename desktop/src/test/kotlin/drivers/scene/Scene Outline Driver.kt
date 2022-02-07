package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.config.drivers.soylestories.getWorkbenchForProjectOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.outline.SceneOutlineViewAccess.Companion.access
import com.soyle.stories.desktop.view.scene.outline.SceneOutlineViewAccess.Companion.drive
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.outline.SceneOutlineView
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import tornadofx.item


fun WorkBench.givenSceneOutlineToolHasBeenOpened(): SceneOutlineView =
    getOpenSceneOutlineTool() ?: run {
        openSceneOutlineTool()
        awaitWithTimeout(100) {
            getOpenSceneOutlineTool()?.let { it.width > 0.0 } ?: false
        }
        getOpenSceneOutlineToolOrError()
    }

fun WorkBench.getOpenSceneOutlineToolOrError(): SceneOutlineView =
    getOpenSceneOutlineTool() ?: error("No Scene Outline tool is open in the project")

fun WorkBench.getOpenSceneOutlineTool(): SceneOutlineView? {
    return robot.from(root).lookup("#scene-outline").queryAll<SceneOutlineView>().firstOrNull()
        ?.takeIf { it.scene.window?.isShowing == true }
}

fun WorkBench.openSceneOutlineTool() {
    findMenuItemById("tools_scene outline")!!
        .apply { robot.interact { fire() } }
}

fun WorkBench.openSceneOutlineTool(scene: Scene) {
    soyleStories.getWorkbenchForProjectOrError(scene.projectId.uuid)
        .givenSceneListToolHasBeenOpened()
        .apply { selectScene(scene) }
        .openSceneOutlineTool(scene)
}

fun SceneOutlineView.givenFocusedOn(scene: Scene): SceneOutlineView {
    if (!access().isFocusedOn(scene)) focusOn(scene)
    assert(access().isFocusedOn(scene))
    return this
}

fun SceneOutlineView.focusOn(scene: Scene) {
    soyleStories.getWorkbenchForProjectOrError(scene.projectId.uuid)
        .givenSceneListToolHasBeenOpened()
        .selectScene(scene)

    awaitWithTimeout(100) { !isLoading }
}

fun SceneOutlineView.addStoryEvent(storyEventId: StoryEvent.Id) {
    drive {
        additionMenu!!.show()
    }

    awaitWithTimeout(100) {
        access().additionMenu!!.items.run {
            isNotEmpty() && none { id == null }
        }
    }

    drive {
        additionMenu!!.items.find { it.id == storyEventId.toString() }!!.fire()
    }
}

fun SceneOutlineView.openConfirmRemoveStoryEventFromSceneDialog(storyEventId: StoryEvent.Id) {
    drive {
        val optionsMenu = getListedStoryEvent(storyEventId)!!
            .optionsMenu
        optionsMenu.show()

        optionsMenu.items.find { it.id == "remove" }!!
            .fire()
    }
}