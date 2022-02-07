package com.soyle.stories.core.definitions.character

import com.soyle.stories.core.framework.`Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStory
import com.soyle.stories.usecase.character.remove.RemoveCharacterFromStoryUseCase
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEvent
import com.soyle.stories.usecase.storyevent.character.involve.InvolveCharacterInStoryEventUseCase
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEvent
import com.soyle.stories.usecase.storyevent.character.remove.RemoveCharacterFromStoryEventUseCase
import com.soyle.stories.usecase.theme.ThemeRepository
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger

class `Character Commands`(
    private val projectRepository: ProjectRepository,
    private val characterRepository: CharacterRepository,
    private val storyEventRepository: StoryEventRepository,
    private val sceneRepository: SceneRepository,
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository,

    private val sceneCharacterSteps: `Scene Character Steps`.When
) : `Character Steps`.When {

    override fun `a character`(named: String): `Character Steps`.When.CreationActions = object :
        `Character Steps`.When.CreationActions {
        override fun `is created in the`(project: Project.Id): Character.Id {
            val usecase = BuildNewCharacterUseCase(projectRepository, characterRepository)
            lateinit var characterId: Character.Id
            runBlocking {
                usecase.invoke(project, nonBlankStr(named)) {
                    characterId = it.characterId
                }
            }
            return characterId
        }
    }

    override fun the(character: Character.Id): `Character Steps`.When.CharacterActions = object :
        `Character Steps`.When.CharacterActions {
        override fun `is involved in the`(storyEvent: StoryEvent.Id) {
            val useCase =
                InvolveCharacterInStoryEventUseCase(storyEventRepository, characterRepository)
            runBlocking {
                useCase(storyEvent, character, InvolveCharacterInStoryEvent.OutputPort { })
            }
        }

        override fun `is involved in the`(vararg storyEvent: StoryEvent.Id, and: StoryEvent.Id) {
            val allStoryEvents = storyEvent.toList() + and
            allStoryEvents.forEach { `is involved in the`(it) }
        }

        override fun `is no longer involved in the`(storyEventId: StoryEvent.Id) {
            val useCase = RemoveCharacterFromStoryEventUseCase(storyEventRepository, sceneRepository)
            runBlocking {
                useCase.invoke(storyEventId, character, object : RemoveCharacterFromStoryEvent.OutputPort {

                    override suspend fun characterRemovedFromStoryEvent(
                        characterRemoved: CharacterRemovedFromStoryEvent
                    ) {
                    }

                })
            }
        }

        override fun `is removed from the`(projectId: Project.Id) {
            val useCase =
                RemoveCharacterFromStoryUseCase(characterRepository)
            runBlocking {
                useCase(character) {}
            }
        }

        override fun `is included in the`(scene: Scene.Id) = sceneCharacterSteps.includeCharacterInScene(scene, character)

        override fun `in the`(scene: Scene.Id): `Scene Character Steps`.When.CharacterInSceneActions =
            sceneCharacterSteps.characterInScene(scene, character)
    }

}