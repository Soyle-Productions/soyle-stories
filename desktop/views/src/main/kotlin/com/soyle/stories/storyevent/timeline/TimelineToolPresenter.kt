package com.soyle.stories.storyevent.timeline

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.layout.config.fixed.Timeline
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.project.ProjectScope
import javafx.application.Platform
import javafx.stage.Stage
import kotlinx.coroutines.withContext
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Not to be confused with the TimelinePresenter.  This accepts requests to view the timeline in various ways from
 * other components and opens the timeline tool, if needed.
 */
class TimelineToolPresenter(
    private val projectScope: ProjectScope,
    private val threadTransformer: ThreadTransformer,
    private val openTool: OpenTool,
    private val openToolOutputPort: OpenTool.OutputPort
) {

    fun viewTimeline() {
        threadTransformer.async {
            openTool.invoke(Timeline, openToolOutputPort)
        }
    }

    fun viewTimeline(withStoryEventFocused: StoryEvent.Id) {
        threadTransformer.async {
            openTool.invoke(Timeline, openToolOutputPort)
            withContext(threadTransformer.guiContext) {
                val timeline = Stage.getWindows().asSequence()
                    .mapNotNull { it.scene?.root?.lookup(".${TimelineStyles.timeline.name}") as? com.soyle.stories.storyevent.timeline.Timeline }
                    .firstOrNull()
                    ?: return@withContext Logger.getGlobal().log(Level.WARNING, "Could not find timeline in workspace")
                timeline.focusOn(withStoryEventFocused)
            }
        }
    }

}