package com.soyle.stories.core.framework.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.PotentialChangesFromUncoveringStoryEvent

interface `Covered Story Events Steps` {

    interface When {

        interface UserQueries {
            fun `lists the story events covered by the`(scene: Scene.Id): StoryEventsInScene
            fun `lists the story events covered by the`(scene: Scene.Id, andInvolveThe: Character.Id): StoryEventsInScene
        }

    }

    interface Then {
        infix fun the(storyEventsInScene: StoryEventsInScene): StoryEventsInSceneStateAssertions
        interface StoryEventsInSceneStateAssertions {
            fun `should not include any story events`()
            fun `should include the`(storyEvent: StoryEvent.Id)
            fun `should include the`(vararg storyEvent: StoryEvent.Id, and: StoryEvent.Id)
        }
    }

}