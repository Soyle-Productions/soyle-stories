package com.soyle.stories.scene.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.EntityId
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.nonBlankStr
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.locationName
import com.soyle.stories.location.makeLocation
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene.MatchingStoryElement
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Get Story Elements to Mention in Scene Unit Test` {

    private val scene = makeScene()
    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    private var result: GetStoryElementsToMentionInScene.ResponseModel? = null

    @Test
    fun `scene doesn't exist should throw error`() {
        val error = assertThrows<SceneDoesNotExist> {
            getStoryElementsToMentionInScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `No matches` {

        @Test
        fun `should output empty response`() {
            sceneRepository.givenScene(scene)
            getStoryElementsToMentionInScene()
            assertTrue(result!!.isEmpty())
        }

    }

    @Nested
    inner class `Characters with string matching` {

        private val charactersByName: Map<String, Character>

        init {
            sceneRepository.givenScene(scene)
            charactersByName = listOf(
                makeCharacter(projectId = scene.projectId, name = nonBlankStr("Billy")),
                makeCharacter(projectId = scene.projectId, name = nonBlankStr("John Boy")),
                makeCharacter(projectId = scene.projectId, name = nonBlankStr("Hallboid")),
                makeCharacter(projectId = scene.projectId, name = nonBlankStr("Katherine")),
                makeCharacter(name = nonBlankStr("Benjamin"))
            ).onEach(characterRepository::givenCharacter)
                .associateBy { it.name.value }
        }

        @Test
        fun `should list characters with matching string in same project`() {
            getStoryElementsToMentionInScene("b")
            result!!.toList().mustEqual(
                listOf(
                    MatchingStoryElement(EntityId.of(charactersByName.getValue("Billy")), "Billy"),
                    MatchingStoryElement(EntityId.of(charactersByName.getValue("John Boy")), "John Boy"),
                    MatchingStoryElement(EntityId.of(charactersByName.getValue("Hallboid")), "Hallboid"),
                )
            )
        }

    }

    @Nested
    inner class `Locations with string matching` {

        private val locationsByName: Map<String, Location>

        init {
            sceneRepository.givenScene(scene)
            locationsByName = listOf(
                makeLocation(projectId = scene.projectId, name = locationName("Barcelona")),
                makeLocation(projectId = scene.projectId, name = locationName("Golden Gate Bridge")),
                makeLocation(projectId = scene.projectId, name = locationName("Mt. Grumble")),
                makeLocation(projectId = scene.projectId, name = locationName("Sydney")),
                makeLocation(name = locationName("Bangladesh"))
            ).onEach(locationRepository::givenLocation)
                .associateBy { it.name.value }
        }

        @Test
        fun `should list locations with matching string in same project`() {
            getStoryElementsToMentionInScene("b")
            result!!.toList().mustEqual(
                listOf(
                    MatchingStoryElement(EntityId.of(locationsByName.getValue("Barcelona")), "Barcelona"),
                    MatchingStoryElement(EntityId.of(locationsByName.getValue("Golden Gate Bridge")), "Golden Gate Bridge"),
                    MatchingStoryElement(EntityId.of(locationsByName.getValue("Mt. Grumble")), "Mt. Grumble"),
                )
            )
        }

    }

    private fun getStoryElementsToMentionInScene(query: String = nonBlankStr().value) {
        val output = object : GetStoryElementsToMentionInScene.OutputPort {
            override suspend fun receiveStoryElementsToMentionInScene(response: GetStoryElementsToMentionInScene.ResponseModel) {
                result = response
            }
        }
        val useCase: GetStoryElementsToMentionInScene =
            GetStoryElementsToMentionInSceneUseCase(sceneRepository, characterRepository, locationRepository)
        runBlocking {
            useCase.invoke(scene.id, nonBlankStr(query), output)
        }
    }

}