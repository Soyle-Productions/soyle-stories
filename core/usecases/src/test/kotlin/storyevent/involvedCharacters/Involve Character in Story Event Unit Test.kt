package com.soyle.stories.usecase.storyevent.involvedCharacters

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent
import com.soyle.stories.domain.storyevent.character.exceptions.DuplicateInvolvedCharacterOperationException
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.shared.exceptions.RejectedUpdateException
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Involve Character in Story Event Unit Test` {

    private val storyEvent = makeStoryEvent()
    private val character = makeCharacter()

    private var characterInvolved: CharacterInvolvedInStoryEvent? = null

    private var storyEventPersistenceFailure: Throwable? = null
    private var updatedStoryEvent: StoryEvent? = null

    private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = {
        storyEventPersistenceFailure?.let { throw it }; updatedStoryEvent = it
    })
    private val characterRepository = CharacterRepositoryDouble()

    private fun involveCharacterInStoryEvent() {
        val useCase: InvolveCharacterInStoryEvent = InvolveCharacterInStoryEventUseCase(
            storyEventRepository,
            characterRepository
        )
        val characterInvolvedProp = ::characterInvolved

        runBlocking {
            useCase(storyEvent.id, character.id,
                InvolveCharacterInStoryEvent.OutputPort { characterInvolved ->
                    characterInvolvedProp.set(
                        characterInvolved
                    )
                })
        }
    }

    @Test
    fun `when story event doesn't exist, should throw error`() {
        characterRepository.givenCharacter(character)

        val error = assertThrows<StoryEventDoesNotExist> {
            involveCharacterInStoryEvent()
        }
        error.storyEventId.mustEqual(storyEvent.id.uuid)
        assertNull(characterInvolved)
        assertNull(updatedStoryEvent)
    }

    @Test
    fun `when character doesn't exist, should throw error`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        val error = assertThrows<CharacterDoesNotExist> {
            involveCharacterInStoryEvent()
        }
        error.characterId.mustEqual(character.id)
        assertNull(characterInvolved)
        assertNull(updatedStoryEvent)
    }

    @Nested
    inner class `Given Character and Story Event Exist` {

        init {
            characterRepository.givenCharacter(character)
            storyEventRepository.givenStoryEvent(storyEvent)
        }

        @Test
        fun `should produce character involved event`() {
            involveCharacterInStoryEvent()

            with(characterInvolved!!) {
                storyEventId.mustEqual(storyEvent.id)
                characterId.mustEqual(character.id)
            }
        }

        @Test
        fun `should update story event to involve character`() {
            involveCharacterInStoryEvent()

            updatedStoryEvent!!.involvedCharacters.getEntityById(character.id)
        }

        @Nested
        inner class `Given Character Already Involved` {

            init {
                storyEventRepository.givenStoryEvent(storyEvent.withCharacterInvolved(character).storyEvent)
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<DuplicateInvolvedCharacterOperationException> {
                    involveCharacterInStoryEvent()
                }
                error.storyEventId.mustEqual(storyEvent.id)
                error.characterId.mustEqual(character.id)
            }

            @Test
            fun `should produce no updates`() {
                assertThrows<DuplicateInvolvedCharacterOperationException> {
                    involveCharacterInStoryEvent()
                }
                assertNull(characterInvolved)
                assertNull(updatedStoryEvent)
            }

        }

        @Nested
        inner class `Story Event Repository Rejects Update` {

            init {
                storyEventPersistenceFailure = Error("Intentional failure")
            }

            @Test
            fun `should throw error`() {
                val error = assertThrows<RejectedUpdateException> { involveCharacterInStoryEvent() }
                error.cause.mustEqual(storyEventPersistenceFailure)
            }

            @Test
            fun `should not update story event or scene`() {
                assertThrows<Throwable> { involveCharacterInStoryEvent() }

                assertNull(characterInvolved)
                assertNull(updatedStoryEvent)
            }

        }

    }

}