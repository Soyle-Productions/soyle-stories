package com.soyle.stories.desktop.adapter.storyevent

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import io.mockk.spyk
import kotlinx.coroutines.Job

class AdjustStoryEventsTimeControllerDouble(val spy: AdjustStoryEventsTimeController = spyk()) : AdjustStoryEventsTimeController by spy