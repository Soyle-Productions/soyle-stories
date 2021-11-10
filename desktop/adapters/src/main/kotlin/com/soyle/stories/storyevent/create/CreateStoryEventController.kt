package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.time.NormalizationPrompt
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface CreateStoryEventController {

    interface PropertiesPrompt {
        suspend fun requestName(): NonBlankString?
        suspend fun requestNameAndTime(): Pair<NonBlankString, Long>?
        suspend fun close()
    }

    companion object {
        fun Implementation(
            projectId: Project.Id,

            guiContext: CoroutineContext,
            asyncContext: CoroutineContext,

            prompt: PropertiesPrompt,
            normalizationPrompt: NormalizationPrompt,

            createStoryEvent: CreateStoryEvent,
            createStoryEventOutput: CreateStoryEvent.OutputPort,

            storyEventRepository: StoryEventRepository
        ): CreateStoryEventController = object : CreateStoryEventController, CoroutineScope by CoroutineScope(guiContext) {
            override fun create(): Job = launch {
                createStoryEvent()
            }

            private suspend fun createStoryEvent() {
                val (name, time) = prompt.requestNameAndTime() ?: return

                if (time < 0 && ! normalizationPrompt.confirmNormalization()) {
                    createStoryEvent() // recursively prompt for name and time
                } else {
                    createStoryEvent(
                        name,
                        CreateStoryEvent.RequestModel.RequestedStoryEventTime.Absolute(time)
                    )
                    prompt.close()
                }
            }

            override fun before(storyEventId: StoryEvent.Id): Job = launch {
                createStoryEventBefore(storyEventId)
            }

            private suspend fun createStoryEventBefore(storyEventId: StoryEvent.Id) {
                val name = prompt.requestName() ?: return
                val relative = withContext(asyncContext) { storyEventRepository.getStoryEventOrError(storyEventId) }

                if (relative.time.toLong() - 1 < 0 && ! normalizationPrompt.confirmNormalization()) {
                    createStoryEventBefore(storyEventId) // recursively prompt for name
                } else {
                    createStoryEvent(
                        name,
                        CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(storyEventId, -1)
                    )
                    prompt.close()
                }
            }

            override fun inPlaceWith(storyEventId: StoryEvent.Id): Job = launch {
                val name = prompt.requestName() ?: return@launch

                createStoryEvent(name, CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(storyEventId, 0))
                prompt.close()
            }

            override fun after(storyEventId: StoryEvent.Id): Job = launch {
                val name = prompt.requestName() ?: return@launch

                createStoryEvent(name, CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(storyEventId, +1))
                prompt.close()
            }

            private suspend fun createStoryEvent(name: NonBlankString, time: CreateStoryEvent.RequestModel.RequestedStoryEventTime) {
                withContext(asyncContext) {
                    createStoryEvent(
                        CreateStoryEvent.RequestModel(
                            name,
                            projectId,
                            time
                        ),
                        createStoryEventOutput
                    )
                }
            }
        }
    }

    fun create(): Job
    fun before(storyEventId: StoryEvent.Id): Job
    fun inPlaceWith(storyEventId: StoryEvent.Id): Job
    fun after(storyEventId: StoryEvent.Id): Job

}