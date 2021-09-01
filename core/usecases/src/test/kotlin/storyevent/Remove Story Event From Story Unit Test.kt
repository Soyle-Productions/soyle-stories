package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProjectUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Remove Story Event From Story Unit Test` {

    // Summary
    /** The specified story event is removed from the project and all references made to it are invalidated */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()
    /** The story event exists */
    private val storyEvent = makeStoryEvent(projectId = projectId)

    // post conditions
    /** outputs a story event removed event */
    private var removedStoryEvent: StoryEventNoLongerHappens? = null

    /** removes the story event from the repository */
    private var removedStoryEventId: StoryEvent.Id? = null
    private val storyEventRepository = StoryEventRepositoryDouble(onRemoveStoryEvent = ::removedStoryEventId::set)

    // Use Case
    private val useCase: RemoveStoryEventFromProject = RemoveStoryEventFromProjectUseCase(storyEventRepository)
    private fun removeStoryEvent() {
        runBlocking {
            useCase.invoke(storyEvent.id) {
                removedStoryEvent = it.storyEventNoLongerHappens
            }
        }
    }

    @Test
    fun `story event must exist`() {
        val error = assertThrows<StoryEventDoesNotExist> { removeStoryEvent() }
        error.storyEventId.mustEqual(storyEvent.id.uuid)
    }

    @Test
    fun `story event must be removed from repository`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        removeStoryEvent()
        removedStoryEventId.mustEqual(storyEvent.id)
    }

    @Test
    fun `story event no longer happens event must be produced`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        removeStoryEvent()
        removedStoryEvent.mustEqual(StoryEventNoLongerHappens(storyEvent.id))
    }

}