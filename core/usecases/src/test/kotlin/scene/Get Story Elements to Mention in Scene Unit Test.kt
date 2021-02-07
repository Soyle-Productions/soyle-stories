package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.prose.MentionedSymbolId
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.scene.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.usecase.scene.getStoryElementsToMention.GetStoryElementsToMentionInScene.MatchingStoryElement
import com.soyle.stories.usecase.scene.getStoryElementsToMention.GetStoryElementsToMentionInSceneUseCase
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
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
    private val themeRepository = ThemeRepositoryDouble()

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
                    MatchingStoryElement(charactersByName.getValue("Billy").id.mentioned(), "Billy", null),
                    MatchingStoryElement(charactersByName.getValue("John Boy").id.mentioned(), "John Boy", null),
                    MatchingStoryElement(charactersByName.getValue("Hallboid").id.mentioned(), "Hallboid", null),
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
                    MatchingStoryElement(locationsByName.getValue("Barcelona").id.mentioned(), "Barcelona", null),
                    MatchingStoryElement(
                        locationsByName.getValue("Golden Gate Bridge").id.mentioned(),
                        "Golden Gate Bridge", null
                    ),
                    MatchingStoryElement(locationsByName.getValue("Mt. Grumble").id.mentioned(), "Mt. Grumble", null),
                )
            )
        }

    }

    @Nested
    inner class `Symbols with String Matching` {

        init {
            sceneRepository.givenScene(scene)
            listOf(
                makeTheme(
                    projectId = scene.projectId,
                    name = "Save the World",
                    symbols = listOf(
                        makeSymbol(name = "Stinger"),
                        makeSymbol(name = "Seven"),
                        makeSymbol(name = "Grapes"),
                        makeSymbol(name = "Some Glue")
                    )
                ),
                makeTheme(
                    projectId = scene.projectId,
                    name = "Save the Galaxy",
                    symbols = listOf(
                        makeSymbol(name = "Ghost"),
                        makeSymbol(name = "The Globe"),
                        makeSymbol(name = "Ring"),
                        makeSymbol(name = "Cat")
                    )
                ),
                makeTheme(
                    projectId = scene.projectId,
                    name = "Save the Universe",
                    symbols = listOf(
                        makeSymbol(name = "Pineapple"),
                        makeSymbol(name = "Bazinga"),
                        makeSymbol(name = "Orange Gummy Bear"),
                        makeSymbol(name = "Gorilla")
                    )
                ),
                makeTheme(
                    name = "Save no one",
                    symbols = List(3) { makeSymbol() })
            ).onEach(themeRepository::givenTheme)
        }

        @Test
        fun `should list symbols with matching string in same project`() {
            getStoryElementsToMentionInScene("G")
            result!!.map { it.name }.mustEqual(
                listOf(
                    "Stinger",
                    "Grapes",
                    "Some Glue",
                    "Ghost",
                    "The Globe",
                    "Ring",
                    "Bazinga",
                    "Orange Gummy Bear",
                    "Gorilla",
                )
            )
        }

        @Test
        fun `each symbol should be listed with the associated theme`() {
            getStoryElementsToMentionInScene("G")
            result!!.map { it.entityId }.filterIsInstance<MentionedSymbolId>().forEach { mentionedSymbolId ->
                themeRepository.themes[mentionedSymbolId.themeId]!!.symbols.find { it.id == mentionedSymbolId.id }!!
            }
            result!!.map { it.parentEntityName }.mustEqual(
                List(3) { "Save the World" } +
                        List(3) { "Save the Galaxy" } +
                        List(3) { "Save the Universe" }
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
            GetStoryElementsToMentionInSceneUseCase(
                sceneRepository,
                characterRepository,
                locationRepository,
                themeRepository
            )
        runBlocking {
            useCase.invoke(scene.id, nonBlankStr(query), output)
        }
    }

}