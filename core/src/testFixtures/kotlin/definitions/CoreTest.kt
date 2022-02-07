package com.soyle.stories.core.definitions

import com.soyle.stories.core.definitions.character.CharacterQueries
import com.soyle.stories.core.definitions.character.`Character Assertions`
import com.soyle.stories.core.definitions.character.`Character Commands`
import com.soyle.stories.core.definitions.character.`Character Expectations`
import com.soyle.stories.core.definitions.project.`Project Expectations`
import com.soyle.stories.core.definitions.scene.SceneQueries
import com.soyle.stories.core.definitions.scene.`Scene Assertions`
import com.soyle.stories.core.definitions.scene.`Scene Commands`
import com.soyle.stories.core.definitions.scene.`Scene Expectations`
import com.soyle.stories.core.definitions.scene.character.`Characters in Scene Query`
import com.soyle.stories.core.definitions.scene.storyevent.`Story Events in Scene Query`
import com.soyle.stories.core.definitions.storyevent.StoryEventQueries
import com.soyle.stories.core.definitions.storyevent.`Story Event Assertions`
import com.soyle.stories.core.definitions.storyevent.`Story Event Commands`
import com.soyle.stories.core.definitions.storyevent.`Story Event Expectations`
import com.soyle.stories.core.framework.PotentialChangesAssertions
import com.soyle.stories.core.framework.`Character Steps`
import com.soyle.stories.core.framework.`Functional Test`
import com.soyle.stories.core.framework.`Project Steps`
import com.soyle.stories.core.framework.scene.`Covered Story Events Steps`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.core.framework.storyevent.`Story Event Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.repositories.*
import com.soyle.stories.usecase.scene.character.effects.ImplicitCharacterRemovedFromScene
import com.soyle.stories.usecase.shared.potential.PotentialChanges
import kotlinx.coroutines.runBlocking
import java.util.logging.Level
import java.util.logging.Logger

abstract class CoreTest : `Functional Test` {

    companion object {
        private val logger = Logger.getGlobal()

        init {
            logger.level = Level.OFF
        }
    }

    private val characterRepository = CharacterRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble()
    private val proseRepository = ProseRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()
    private val projectRepository = object : ProjectRepository {
        private val projects = mutableMapOf<Project.Id, Project>()
        override suspend fun addNewProject(project: Project) {
            projects[project.id] = project
        }

        override suspend fun getProject(projectId: Project.Id): Project? = projects[projectId]
    }

    // commands
    private val sceneCommands = `Scene Commands`(
        sceneRepository,
        storyEventRepository,
        proseRepository,
        locationRepository,
        characterRepository
    )
    private val characterCommands = `Character Commands`(
        projectRepository,
        characterRepository,
        storyEventRepository,
        sceneRepository,
        themeRepository,
        characterArcRepository,
        sceneCommands
    )
    private val storyEventCommands = `Story Event Commands`(storyEventRepository, sceneRepository)

    // queries
    private val storyEventQueries = StoryEventQueries(
        storyEventRepository,
        characterRepository,
        sceneRepository
    )
    private val sceneQueries = SceneQueries(
        sceneRepository,
        characterRepository,
        locationRepository,
        storyEventRepository
    )
    private val storyEventSceneQueries = `Story Events in Scene Query`(
        sceneRepository,
        storyEventRepository,
        characterRepository
    )

    private val potentialWhen: `Functional Test`.PotentialWhens = object : `Functional Test`.PotentialWhens,
        `Scene Steps`.When.UserQueries.PotentialWhens by sceneQueries,
        `Story Event Steps`.When.UserQueries.PotentialWhens by storyEventQueries.`lists the potential changes of`(),
        `Character Steps`.When.UserQueries.PotentialWhens by CharacterQueries(
            storyEventQueries.`lists the potential changes of`(),
            characterRepository,
            storyEventRepository,
            sceneRepository
        ) {}

    // when
    final override val `when`: `Functional Test`.Whens = object : `Functional Test`.Whens,
        `Character Steps`.When by characterCommands,
        `Story Event Steps`.When by storyEventCommands,
        `Scene Steps`.When by sceneCommands {

        override fun `the user`(): `Functional Test`.Queries = object : `Functional Test`.Queries,
            `Story Event Steps`.When.UserQueries by storyEventQueries,
            `Scene Character Steps`.When.UserQueries by sceneQueries.characters,
            `Covered Story Events Steps`.When.UserQueries by storyEventSceneQueries {
            override fun `lists the potential changes of`(): `Functional Test`.PotentialWhens = potentialWhen
        }
    }

    // expectations
    private val sceneExpectations = `Scene Expectations`(sceneRepository, sceneCommands)
    private val storyEventExpectations = `Story Event Expectations`(storyEventRepository, storyEventCommands)
    private val characterExpectations = `Character Expectations`(
        characterRepository,
        storyEventRepository,

        characterCommands,

        sceneExpectations
    )
    private val projectExpectations = `Project Expectations`(projectRepository)

    // given
    final override val given: `Functional Test`.Givens = object : `Functional Test`.Givens,
        `Character Steps`.Given by characterExpectations,
        `Story Event Steps`.Given by storyEventExpectations,
        `Scene Steps`.Given by sceneExpectations,
        `Project Steps`.Given by projectExpectations {}

    // assertions
    private val sceneAssertions = `Scene Assertions`(sceneRepository)
    private val storyEventAssertions = `Story Event Assertions`(storyEventRepository)
    private val characterAssertions = `Character Assertions`(sceneAssertions)

    // then
    final override val then: `Functional Test`.Thens = object : `Functional Test`.Thens,
        `Character Steps`.Then by characterAssertions,
        `Scene Steps`.Then by sceneAssertions,
        `Story Event Steps`.Then by storyEventAssertions {
        override fun the(potentialChanges: PotentialChanges<*>): PotentialChangesAssertions =
            PotentialChangesAssertionsDef(potentialChanges)
    }

    final override fun effect(): `Functional Test`.PotentialEffectBuilder =
        object : `Functional Test`.PotentialEffectBuilder {
            override fun the(character: Character.Id): `Scene Character Steps`.PotentialEffectBuilder.SceneCharacterEffectBuilder {
                return object : `Scene Character Steps`.PotentialEffectBuilder.SceneCharacterEffectBuilder {
                    override fun `will be removed from the`(scene: Scene.Id): ImplicitCharacterRemovedFromScene {
                        return runBlocking {
                            ImplicitCharacterRemovedFromScene(
                                scene,
                                sceneRepository.getSceneOrError(scene.uuid).name.value,
                                character,
                                characterRepository.getCharacterOrError(character.uuid).displayName.value
                            )
                        }
                    }
                }
            }
        }
}