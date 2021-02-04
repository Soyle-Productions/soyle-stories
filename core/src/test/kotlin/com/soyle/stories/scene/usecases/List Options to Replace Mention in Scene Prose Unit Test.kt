package com.soyle.stories.scene.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.makeLocation
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import com.soyle.stories.scene.usecases.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProseUseCase
import com.soyle.stories.theme.makeSymbol
import com.soyle.stories.theme.makeTheme
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
    inner class `When Prose Exists` {
        init {
            sceneRepository.givenScene(scene)
        }

        @Nested
        inner class `Should List Like-Kinded Entities` {

            @Nested
            inner class `When Mention is Character Id` {

                private val characterId = Character.Id().mentioned()

                private val charactersInProject = List(4) { makeCharacter(projectId = scene.projectId) }
                private val charactersInOtherProjects = List(3) { makeCharacter() }

                init {
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
                            newScene.withLocationLinked(location.id)
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