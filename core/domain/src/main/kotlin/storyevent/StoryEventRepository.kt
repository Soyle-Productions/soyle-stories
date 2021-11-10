package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.project.Project

/**
 * Read-only repository to retrieve story events
 */
interface StoryEventRepository {
    suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent>
}