package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.list.StoryEventListToolAccess.Companion.drive
import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneList.SceneListView
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import tornadofx.FX

fun WorkBench.getOpenStoryEventListTool(): StoryEventListTool?
    = scope.get<StoryEventListTool>().takeIf { it.root.scene?.window?.isShowing == true }

fun WorkBench.getOpenStoryEventListToolOrError(): StoryEventListTool
    = getOpenStoryEventListTool() ?: error("Story Event List Tool was not opened in this workbench ${scope.projectViewModel}")

fun WorkBench.givenStoryEventListToolHasBeenOpened(): StoryEventListTool
    = getOpenStoryEventListTool() ?: run {
        openStoryEventListTool()
        getOpenStoryEventListToolOrError()
    }

fun WorkBench.openStoryEventListTool()
{
    findMenuItemById("tools_storyeventlist")!!
        .apply { robot.interact { fire() } }
}

fun StoryEventListTool.openCreateStoryEventDialog() {
    drive {
        createStoryEventButton!!.fire()
    }
}