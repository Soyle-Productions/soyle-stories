package com.soyle.stories.usecase.scene.prose

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.prose.mentions.AvailableStoryElementsToMentionInScene
import com.soyle.stories.usecase.scene.prose.mentions.GetStoryElementsToMentionInScene
import com.soyle.stories.usecase.scene.prose.mentions.GetStoryElementsToMentionInSceneUseCase
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Get Story Elements to Mention in Scene Unit Test` {

    private val scene = makeScene()
    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    private var result: AvailableStoryElementsToMentionInScene? = null

    @Test
    fun `scene doesn't exist should throw error`() {
        val error = assertThrows<SceneDoesNotExist> {
            getStoryElementsToMentionInScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Nested
        inner class `Characters from Project Should be Listed` {

            @Test
            fun `only characters from same project should be listed`() {
                val characters = listOf(
                    makeCharacter(name = nonBlankStr("Billy")),
                    makeCharacter(projectId = scene.projectId, name = nonBlankStr("Bob"))
                )
                characters.onEach { characterRepository.givenCharacter(it) }

                getStoryElementsToMentionInScene()

                result!!.getMatches(nonBlankStr("B")).mustEqual(
                    listOf(
                        AvailableStoryElementItem(characters[1].id.mentioned(), "Bob", null)
                    )
                )
            }

            @Test
            fun `should include separate entry for each secondary character name`() {
                val frank = makeCharacter(projectId = scene.projectId, name = nonBlankStr("Frank"),
                    otherNames = setOf(
                        nonBlankStr("Billy")
                    )
                ).also(characterRepository::givenCharacter)
                val bob = makeCharacter(projectId = scene.projectId, name = nonBlankStr("Bob"),
                    otherNames = setOf(
                        nonBlankStr("Robert")
                    )
                ).also(characterRepository::givenCharacter)

                getStoryElementsToMentionInScene()

                result!!.getMatches(nonBlankStr("b")).mustEqual(
                    listOf(
                        AvailableStoryElementItem(bob.id.mentioned(), "Bob", null),
                        AvailableStoryElementItem(frank.id.mentioned(), "Billy", "Frank"),
                        AvailableStoryElementItem(bob.id.mentioned(), "Robert", "Bob")
                    )
                )
            }

            @Nested
            inner class `Given Characters Included in Scene` {

                val characters = listOf(
                    makeCharacter(projectId = scene.projectId, name = nonBlankStr("Robert")),
                    makeCharacter(projectId = scene.projectId, name = nonBlankStr("Bob"))
                ).onEach { characterRepository.givenCharacter(it) }

                init {
                    sceneRepository.givenScene(scene.withCharacterIncluded(characters[0]).scene)
                }

                @Test
                fun `should prioritize included characters`() {
                    getStoryElementsToMentionInScene()

                    result!!.getMatches(nonBlankStr("b")).first().run {
                        name.mustEqual(characters[0].displayName)
                        entityId.mustEqual(characters[0].id.mentioned())
                    }
                }

            }

        }

        @Nested
        inner class `Locations from Project Should be Listed` {

            @Test
            fun `only locations from same project should be listed`() {
                val locations = listOf(
                    makeLocation(name = locationName("Barcelona")),
                    makeLocation(projectId = scene.projectId, name = locationName("Bangladesh"))
                )
                locations.onEach { locationRepository.givenLocation(it) }

                getStoryElementsToMentionInScene()

                result!!.getMatches(nonBlankStr("B")).mustEqual(
                    listOf(
                        AvailableStoryElementItem(locations[1].id.mentioned(), "Bangladesh", null)
                    )
                )
            }

            @Nested
            inner class `Given Locations Used in Scene` {

                val locations = listOf(
                    makeLocation(projectId = scene.projectId, name = locationName("Nevada")),
                    makeLocation(projectId = scene.projectId, name = locationName("Vermont"))
                ).onEach { locationRepository.givenLocation(it) }

                init {
                    sceneRepository.givenScene(scene.withLocationLinked(locations[0]).scene)
                }

                @Test
                fun `should prioritize used locations`() {
                    getStoryElementsToMentionInScene()

                    result!!.getMatches(nonBlankStr("v")).first().run {
                        name.mustEqual("Nevada")
                        entityId.mustEqual(locations[0].id.mentioned())
                    }
                }

            }

        }

    }

    private fun getStoryElementsToMentionInScene() {
        val useCase: GetStoryElementsToMentionInScene =
            GetStoryElementsToMentionInSceneUseCase(
                sceneRepository,
                characterRepository,
                locationRepository
            )
        runBlocking {
            useCase.invoke(scene.id) { response -> result = response }
        }
    }

}