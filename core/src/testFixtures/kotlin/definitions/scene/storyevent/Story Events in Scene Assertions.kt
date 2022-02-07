package com.soyle.stories.core.definitions.scene.storyevent

import com.soyle.stories.core.framework.scene.`Covered Story Events Steps`
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldContainAny

class `Story Events in Scene Assertions` : `Covered Story Events Steps`.Then {

    override fun the(
        storyEventsInScene: StoryEventsInScene
    ): `Covered Story Events Steps`.Then.StoryEventsInSceneStateAssertions = object : `Covered Story Events Steps`.Then.StoryEventsInSceneStateAssertions {
        override fun `should not include any story events`() {
            storyEventsInScene.shouldBeEmpty()
        }

        override fun `should include the`(storyEvent: StoryEvent.Id) {
            storyEventsInScene.shouldContainAny { it.storyEventId == storyEvent }
        }

        override fun `should include the`(vararg storyEvent: StoryEvent.Id, and: StoryEvent.Id) {
            storyEventsInScene.map { it.storyEventId }.shouldContainAll(storyEvent.toList() + and)
        }
    }

}