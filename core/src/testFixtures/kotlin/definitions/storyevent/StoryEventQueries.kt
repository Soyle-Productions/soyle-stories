package com.soyle.stories.core.definitions.storyevent

import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.involve.AvailableCharactersToInvolveInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.GetAvailableCharactersToInvolveInStoryEventUseCase
import kotlinx.coroutines.runBlocking

class StoryEventQueries(
    private val storyEventRepository: StoryEventRepository,
    private val characterRepository: CharacterRepository,
    private val sceneRepository: SceneRepository
) : `Story Event Steps`.When.UserQueries {
    override fun `lists the available characters to involve in the`(storyEventId: StoryEvent.Id): AvailableCharactersToInvolveInStoryEvent {
        val useCase = GetAvailableCharactersToInvolveInStoryEventUseCase(storyEventRepository, characterRepository)
        lateinit var response: AvailableCharactersToInvolveInStoryEvent
        runBlocking {
            useCase(storyEventId) {
                response = it
            }
        }
        return response
    }

    override fun `lists the potential changes of`(): `Story Event Steps`.When.UserQueries.PotentialWhens = PotentialStoryEventQueries(
        characterRepository,
        storyEventRepository,
        sceneRepository
    )
}