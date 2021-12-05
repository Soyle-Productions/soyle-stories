package com.soyle.stories.core

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.usecase.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventUseCase
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger

object `Character Steps` {

    interface Given {

        val storyEventRepository: StoryEventRepository
        val characterRepository: CharacterRepository
        val themeRepository: ThemeRepository

        val `when`: When

        interface CreationExpectations {
            infix fun `has been created in the`(project: Project.Id): Character.Id
        }

        fun `a character`(named: String = characterName().value): CreationExpectations = object : CreationExpectations {
            override fun `has been created in the`(project: Project.Id): Character.Id {
                val id = runBlocking { characterRepository.listCharactersInProject(project) }
                    .find { it.name.value == named }
                    ?.id
                if (id == null) {
                    val usecase = BuildNewCharacterUseCase(characterRepository, themeRepository)
                    val deferred = CompletableDeferred<Character.Id>()
                    runBlocking {
                        usecase.invoke(project.uuid, nonBlankStr(named), object : BuildNewCharacter.OutputPort {
                            override fun receiveBuildNewCharacterFailure(failure: Exception) {
                                throw failure
                            }

                            override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
                                deferred.complete(Character.Id(response.characterId))
                            }

                            override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {}
                            override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) {}
                        })
                    }
                    return runBlocking { deferred.await() }
                }
                return id
            }
        }

        interface StateExpectations {
            val characterId: Character.Id

            infix fun `has been involved in the`(storyEventId: StoryEvent.Id)
        }

        infix fun the(characterId: Character.Id): StateExpectations = object : StateExpectations {
            override val characterId: Character.Id = characterId
            override fun `has been involved in the`(storyEventId: StoryEvent.Id) {
                val storyEvent = runBlocking { storyEventRepository.getStoryEventOrError(storyEventId) }
                if (storyEvent.involvedCharacters.contains(characterId)) return
                `when`.the(characterId).`is involved in the`(storyEventId)
            }
        }

    }

    interface When {

        val characterRepository: CharacterRepository
        val storyEventRepository: StoryEventRepository
        val themeRepository: ThemeRepository
        val characterArcRepository: CharacterArcRepository

        interface CharacterActions {
            infix fun `is involved in the`(storyEvent: StoryEvent.Id)
            infix fun `is no longer involved in the`(storyEventId: StoryEvent.Id)
            infix fun `is removed from the`(projectId: Project.Id)
        }

        infix fun the(character: Character.Id): CharacterActions = object : CharacterActions {
            override fun `is involved in the`(storyEvent: StoryEvent.Id) {
                val useCase = AddCharacterToStoryEventUseCase(storyEventRepository, characterRepository)
                runBlocking {
                    useCase(storyEvent.uuid, character.uuid, object : AddCharacterToStoryEvent.OutputPort {
                        override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {
                            throw failure
                        }

                        override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
                            Logger.getGlobal().info(response.toString())
                        }
                    })
                }
            }

            override fun `is no longer involved in the`(storyEventId: StoryEvent.Id) {
                val useCase = RemoveCharacterFromStoryEventUseCase(storyEventRepository)
                runBlocking {
                    useCase.invoke(storyEventId.uuid, character.uuid, object : RemoveCharacterFromStoryEvent.OutputPort {
                        override fun receiveRemoveCharacterFromStoryEventFailure(failure: Exception) {
                            throw failure
                        }

                        override fun receiveRemoveCharacterFromStoryEventResponse(response: RemoveCharacterFromStoryEvent.ResponseModel) {
                            Logger.getGlobal().info(response.toString())
                        }
                    })
                }
            }

            override fun `is removed from the`(projectId: Project.Id) {
                val useCase =
                    RemoveCharacterFromStoryUseCase(characterRepository, themeRepository, characterArcRepository)
                runBlocking {
                    useCase.invoke(character.uuid, true, object : RemoveCharacterFromStory.OutputPort {
                        override suspend fun confirmDeleteCharacter(request: RemoveCharacterFromStory.ConfirmationRequest) {}
                        override suspend fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
                            Logger.getGlobal().info(response.removedCharacter.toString())
                            response.removedCharacterFromThemes.forEach {
                                Logger.getGlobal().info(it.toString())
                            }
                        }
                    })
                }
            }
        }

    }

}