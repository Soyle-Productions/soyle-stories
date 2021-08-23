package com.soyle.stories.desktop.config.drivers.storyevent

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.storyevent.CreateStoryEventScope
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.storyevent.create.`Create Story Event Dialog Access`.Companion.access
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialog
import com.soyle.stories.storyevent.list.creationButton.StoryEventListTool
import tornadofx.Stylesheet.Companion.form
import tornadofx.uiComponent
import java.lang.ref.WeakReference

fun WorkBench.getOpenCreateStoryEventDialog(): CreateStoryEventForm? {
    val forms = DI.activeScopes.filterIsInstance<CreateStoryEventScope>().filter { it.projectScope == scope }
        .map { DI.getRegisteredTypes(it).get(CreateStoryEventForm::class) }
        .mapNotNull { it as? CreateStoryEventForm }
        .toList()
    return forms.find { it.root.scene?.window?.isShowing ?: false }
}

fun WorkBench.getOpenCreateStoryEventDialogOrError(): CreateStoryEventForm =
    getOpenCreateStoryEventDialog() ?: error("Create Story Event Dialog is not open")

fun WorkBench.givenCreateStoryEventDialogHasBeenOpened(): CreateStoryEventForm =
    getOpenCreateStoryEventDialog() ?: run {
        givenStoryEventListToolHasBeenOpened().openCreateStoryEventDialog()
        getOpenCreateStoryEventDialogOrError()
    }

fun CreateStoryEventForm.createStoryEventNamed(name: String, time: Int? = null) {
    with(access()) {
        interact {
            nameInput.text = name
            if (time != null) {
                timeInput?.editor?.text = "$time"
                timeInput?.commitValue()
            }
            submitButton.fire()
        }
    }
}