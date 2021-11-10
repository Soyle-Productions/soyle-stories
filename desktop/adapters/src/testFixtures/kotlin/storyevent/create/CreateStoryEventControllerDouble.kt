package com.soyle.stories.desktop.adapter.storyevent.create

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import io.mockk.spyk
import kotlinx.coroutines.Job

class CreateStoryEventControllerDouble : CreateStoryEventController by spyk()