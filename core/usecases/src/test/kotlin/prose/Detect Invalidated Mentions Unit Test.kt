package com.soyle.stories.usecase.prose

import arrow.core.const
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.*
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.usecase.prose.detectInvalidMentions.DetectInvalidatedMentionsUseCase
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.validation.NonBlankString
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

    private fun resetTest() {
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

            private fun givenMentionedCharacter(
                displayName: NonBlankString = characterName(),
                nameVariants: List<NonBlankString> = listOf(),
                configureCharacter: Character.() -> Character = { this }
            ): MentionedCharacterId {
                val character = makeCharacter(name = displayName, otherNames = nameVariants.toSet())
                    .configureCharacter()
                characterRepository.givenCharacter(character)
                return character.id.mentioned()
            }

            private fun givenMentionedLocation(name: NonBlankString): MentionedLocationId {
                val location = makeLocation()
                locationRepository.givenLocation(location)
                return location.id.mentioned()
            }

            private fun givenMentionedSymbol(name: NonBlankString): MentionedSymbolId {
                val symbol = makeSymbol()
                val theme = makeTheme(symbols = listOf(symbol))
                themeRepository.givenTheme(theme)
                return symbol.id.mentioned(theme.id)
            }

            @TestFactory
            fun `should not output entity id`() = listOf(
                { givenMentionedCharacter(it) },
                ::givenMentionedLocation,
                ::givenMentionedSymbol
            ).map {
                val displayName = nonBlankStr("Entity Name")
                val mentionedEntityId = it(displayName)
                dynamicTest("should not output ${mentionedEntityId.id}") {
                    proseRepository.givenProse(
                        prose.withTextInserted("$displayName Mentioned").prose
                            .withEntityMentioned(mentionedEntityId, 0, displayName.length).prose
                    )
                    detectInvalidatedMentions()
                    result!!.invalidEntityIds.isEmpty().mustEqual(true)

                    resetTest()
                }
            }

            @Nested
            inner class `When Mentioned Character No Longer has Used Name Variant` {

                @Test
                fun `should output character id`() {
                    val mentionedCharacterId = givenMentionedCharacter(displayName = nonBlankStr("Bob"))
                    proseRepository.givenProse(
                        prose.withTextInserted("Robert").prose.withEntityMentioned(
                            mentionedCharacterId, 0, 6
                        ).prose
                    )
                    detectInvalidatedMentions()
                    result!!.invalidEntityIds.single().mustEqual(mentionedCharacterId)
                }

            }

            @Nested
            inner class `When Mentioned Character is No Longer in Project` {

                @Test
                fun `should output character id`() {
                    val mentionedCharacterId = givenMentionedCharacter(displayName = nonBlankStr("Bob")) {
                        removedFromStory().character
                    }
                    proseRepository.givenProse(
                        prose.withTextInserted("Bob").prose.withEntityMentioned(
                            mentionedCharacterId, 0, 3
                        ).prose
                    )
                    detectInvalidatedMentions()
                    result!!.invalidEntityIds.single().mustEqual(mentionedCharacterId)
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