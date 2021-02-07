package com.soyle.stories.usecase.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.prose.ProseDoesNotExist
import com.soyle.stories.domain.prose.makeProse
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentionsUseCase
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class `Detect Invalidated Mentions Unit Test` {

    private val prose = makeProse()

    private var result: DetectInvalidatedMentions.ResponseModel? = null

    private val characterRepository = CharacterRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()
    private val proseRepository = ProseRepositoryDouble()

    private fun resetTest()
    {
        result = null
    }

    @Test
    fun `when prose doesn't exist, should throw error`() {
        assertThrows<ProseDoesNotExist> {
            detectInvalidatedMentions()
        }
    }

    @Nested
    inner class `When Prose Exists` {

        @Test
        fun `should output prose id`() {
            proseRepository.givenProse(prose)
            detectInvalidatedMentions()
            result!!.proseId.mustEqual(prose.id)
        }

        @Nested
        inner class `When Mentioned Entity Exists` {

            @TestFactory
            fun `should not output entity id`() = listOf(
                {
                    val character = makeCharacter()
                    characterRepository.givenCharacter(character)
                    character.id.mentioned()
                },
                {
                    val location = makeLocation()
                    locationRepository.givenLocation(location)
                    location.id.mentioned()
                },
                {
                    val symbol = makeSymbol()
                    val theme = makeTheme(symbols = listOf(symbol))
                    themeRepository.givenTheme(theme)
                    symbol.id.mentioned(theme.id)
                }
            ).map {
                val mentionedEntityId = it()
                dynamicTest("should not output ${mentionedEntityId.id}") {
                    proseRepository.givenProse(
                        prose.withTextInserted("Mentioned entity").prose.withEntityMentioned(
                            mentionedEntityId, 0, 16
                        ).prose
                    )
                    detectInvalidatedMentions()
                    result!!.invalidEntityIds.isEmpty().mustEqual(true)

                    resetTest()
                }
            }
        }

        @Nested
        inner class `When Mentioned Entity No Longer Exists` {

            private val pastEntityGenerator = listOf(
                { Character.Id().mentioned() },
                { Location.Id().mentioned() },
                { Symbol.Id().mentioned(Theme.Id()) }
            )

            @TestFactory
            fun `should output entity id`() = pastEntityGenerator
                .map {
                    val mentionedEntityId = it()
                    dynamicTest("should output ${mentionedEntityId.id}") {
                        proseRepository.givenProse(
                            prose.withTextInserted("Bob").prose.withEntityMentioned(
                                mentionedEntityId, 0, 3
                            ).prose
                        )
                        detectInvalidatedMentions()
                        result!!.invalidEntityIds.single().mustEqual(mentionedEntityId)

                        resetTest()
                    }
                }

            @Nested
            inner class `When Entity is Mentioned Multiple Times` {

                @TestFactory
                fun `should only output the entity id once`() = pastEntityGenerator
                    .map {
                        val mentionedEntityId = it()
                        dynamicTest("should only output ${mentionedEntityId.id} once") {
                            val entityName = "Entity"
                            val proseText = List(5) { entityName }.joinToString(" ")
                            proseRepository.givenProse(
                                (0..4).fold(prose.withTextInserted(proseText).prose) { nextProse, index ->
                                    nextProse.withEntityMentioned(
                                        mentionedEntityId,
                                        index * (entityName.length + 1),
                                        entityName.length
                                    ).prose
                                }
                            )
                            detectInvalidatedMentions()
                            result!!.invalidEntityIds.single().mustEqual(mentionedEntityId)

                            resetTest()
                        }
                    }


            }

        }

    }

    private fun detectInvalidatedMentions() {
        val useCase: DetectInvalidatedMentions =
            DetectInvalidatedMentionsUseCase(proseRepository, characterRepository, locationRepository, themeRepository)
        val output = object : DetectInvalidatedMentions.OutputPort {
            override suspend fun receiveDetectedInvalidatedMentions(response: DetectInvalidatedMentions.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(prose.id, output)
        }
    }

}