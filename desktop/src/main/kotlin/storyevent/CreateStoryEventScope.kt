package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.common.SubProjectScope
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import tornadofx.Stylesheet.Companion.form
import tornadofx.onChangeOnce

class CreateStoryEventScope(
    val relativePlacement: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative?,
    projectScope: ProjectScope
) : SubProjectScope(projectScope) {

    val createStoryEventForm: CreateStoryEventForm by lazy {
        CreateStoryEventForm(relativePlacement, projectScope.get()).also { form ->
            form.root.sceneProperty().onChangeOnce { scene ->
                scene?.window?.showingProperty()?.onChangeUntil({ it == false }) {
                    if (it == false) {
                        println("closing create story event scope")
                        close()
                    }
                }
            }
        }
    }

}