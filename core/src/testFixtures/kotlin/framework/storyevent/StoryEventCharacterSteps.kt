package com.soyle.stories.core.framework.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.PotentialChangesOfRemovingCharacterFromStoryEvent

interface StoryEventCharacterSteps {

    interface When {

        fun `the user`()

        interface UserQueries {

            fun `lists the potential changes of`(): PotentialWhens
            interface PotentialWhens {

                fun characterRemovedFromStoryEvent(character: Character.Id, storyEvent: StoryEvent.Id): PotentialChangesOfRemovingCharacterFromStoryEvent

            }
        }

    }

}