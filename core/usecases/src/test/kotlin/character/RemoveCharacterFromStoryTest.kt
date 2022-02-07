package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStoryUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.storyevent.characterDoesNotExist
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveCharacterFromStoryTest {

    // pre conditions
    private val character = makeCharacter()

    // post conditions
    private var removedCharacter: Character? = null

    // wiring
    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::removedCharacter::set)

    // tests

    @Nested
    inner class `Character must exist` {

        @Test
        fun `given character does not exist - should throw error`() {
            val result = removeCharacterFromStory()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
            removedCharacter.shouldBeNull()
        }
    }

    @Test
    fun `should update character to no longer be in project`() {
        characterRepository.givenCharacter(character)

        removeCharacterFromStory()

        removedCharacter!!.id.shouldBeEqualTo(character.id)
        removedCharacter!!.projectId.shouldBeNull()
    }

    @Test
    fun `should output character removed from project event`() {
        characterRepository.givenCharacter(character)

        val result = removeCharacterFromStory()

        result.getOrThrow().characterRemoved.shouldBeEqualTo(CharacterRemovedFromStory(character.id, character.projectId!!))
    }


    private fun removeCharacterFromStory(): Result<RemoveCharacterFromStory.ResponseModel>
    {
        val useCase: RemoveCharacterFromStory = RemoveCharacterFromStoryUseCase(characterRepository)
        var result = Result.failure<RemoveCharacterFromStory.ResponseModel>(Error("No response received"))
        return runBlocking {
            kotlin.runCatching {
                useCase(character.id) { result = Result.success(it) }
            }
        }.mapCatching { result.getOrThrow() }
    }

}