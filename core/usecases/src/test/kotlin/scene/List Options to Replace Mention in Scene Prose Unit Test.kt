package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeSymbol
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProseUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Options to Replace Mention in Scene Prose Unit Test` {

    private val scene = makeScene()

    private val sceneRepository = SceneRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()

    private var result: ListOptionsToReplaceMentionInSceneProse.ResponseModel<*>? = null

    @Test
    fun `scene must exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            listOptionsToReplaceMentionInSceneProse(Character.Id().mentioned())
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `When Mention is Character Id` {

        private val characterId = Character.Id().mentioned()

        private val charactersInProject = List(4) { makeCharacter(projectId = scene.projectId) }
        private val charactersInOtherProjects = List(3) { makeCharacter() }

        init {
            sceneRepository.givenScene(scene)
            charactersInProject.forEach(characterRepository::givenCharacter)
            charactersInOtherProjects.forEach(characterRepository::givenCharacter)
        }

        @Test
        fun `should list all characters in project`() {
            listOptionsToReplaceMentionInSceneProse(characterId)
            result!!.let {
                it.entityIdToReplace.mustEqual(characterId)
                it.options.mustEqual(charactersInProject.map {
                    ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(), it.name.value, null)
                })
            }
        }

        @Nested
        inner class `When Character is Included in Scene` {

            init {
                charactersInProject.takeLast(2).fold(scene) { newScene, character ->
                    newScene.withCharacterIncluded(character)
                }.let(sceneRepository::givenScene)
            }

            @Test
            fun `should prioritize included characters in result`() {
                listOptionsToReplaceMentionInSceneProse(characterId)
                result!!.let {
                    it.entityIdToReplace.mustEqual(characterId)
                    it.options.mustEqual(
                        (charactersInProject.takeLast(2) + charactersInProject.dropLast(2)).map {
                            ListOptionsToReplaceMentionInSceneProse.MentionOption(
                                it.id.mentioned(),
                                it.name.value,
                                null
                            )
                        })
                }
            }

        }

    }

    @Nested
    inner class `When Mention is Location Id` {

        private val locationId = Location.Id().mentioned()

        private val locationsInProject = List(4) { makeLocation(projectId = scene.projectId) }
        private val locationsInOtherProjects = List(3) { makeLocation() }

        init {
            sceneRepository.givenScene(scene)
            locationsInProject.forEach(locationRepository::givenLocation)
            locationsInOtherProjects.forEach(locationRepository::givenLocation)
        }

        @Test
        fun `should list all locations in project`() {
            listOptionsToReplaceMentionInSceneProse(locationId)
            result!!.let {
                it.entityIdToReplace.mustEqual(locationId)
                it.options.mustEqual(locationsInProject.map {
                    ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(), it.name.value, null)
                })
            }
        }

        @Nested
        inner class `When Location Used in Scene` {

            init {
                locationsInProject.takeLast(2).fold(scene) { newScene, location ->
                    newScene.withLocationLinked(location).scene
                }.let(sceneRepository::givenScene)
            }

            @Test
            fun `should prioritize used locations in result`() {
                listOptionsToReplaceMentionInSceneProse(locationId)
                result!!.let {
                    it.entityIdToReplace.mustEqual(locationId)
                    it.options.mustEqual(
                        (locationsInProject.takeLast(2) + locationsInProject.dropLast(2)).map {
                            ListOptionsToReplaceMentionInSceneProse.MentionOption(
                                it.id.mentioned(),
                                it.name.value,
                                null
                            )
                        })
                }
            }

        }

    }

    @Nested
    inner class `When mention is symbol id`
    {

        private val themeId = Theme.Id()
        private val symbolId = Symbol.Id().mentioned(themeId)

        private val themesInProject = List(4) { makeTheme(projectId = scene.projectId, symbols = List(3) { makeSymbol() }) }
        private val themesInOtherProjects = List(3) { makeTheme(symbols = List(2) { makeSymbol() }) }

        init {
            sceneRepository.givenScene(scene)
            themesInProject.forEach(themeRepository::givenTheme)
            themesInOtherProjects.forEach(themeRepository::givenTheme)
        }

        @Test
        fun `should list all symbols in the project`() {
            listOptionsToReplaceMentionInSceneProse(symbolId)
            result!!.let {
                it.entityIdToReplace.mustEqual(symbolId)
                it.options.mustEqual(themesInProject.flatMap { theme ->
                    theme.symbols.map {
                        ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(theme.id), it.name, theme.name)
                    }
                })
            }
        }

    }

    private fun listOptionsToReplaceMentionInSceneProse(entityId: MentionedEntityId<*>) {
        val useCase: ListOptionsToReplaceMentionInSceneProse =
            ListOptionsToReplaceMentionInSceneProseUseCase(sceneRepository, characterRepository, locationRepository, themeRepository)
        val output = object : ListOptionsToReplaceMentionInSceneProse.OutputPort {
            override suspend fun receiveOptionsToReplaceMention(response: ListOptionsToReplaceMentionInSceneProse.ResponseModel<*>) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, entityId, output)
        }
    }

}