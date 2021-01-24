package com.soyle.stories.prose.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ProseRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.mentioned
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.makeLocation
import com.soyle.stories.prose.ProseDoesNotExist
import com.soyle.stories.prose.makeProse
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentionsUseCase
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
                { Location.Id().mentioned() }
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
            DetectInvalidatedMentionsUseCase(proseRepository, characterRepository, locationRepository)
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