package com.soyle.stories.storyevent.list

import com.soyle.stories.domain.project.Project
import javafx.scene.Node
import tornadofx.UIComponent

interface StoryEventListTool {
    operator fun invoke(projectId: Project.Id): Node
}