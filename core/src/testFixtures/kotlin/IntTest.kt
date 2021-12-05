package com.soyle.stories.core

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.repositories.*
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.logging.Level
import java.util.logging.Logger

abstract class IntTest {

    companion object {
        var logEvents = true
            set(value) {
                if (value) Logger.getGlobal().level = Level.ALL
                else Logger.getGlobal().level = Level.OFF
                field = value
            }
        init {
            logEvents = false
        }
    }

    private val projectRepository = object : ProjectRepository {
        override suspend fun addNewProject(project: Project) {

        }
    }
    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val proseRepository = ProseRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val themeRepository = ThemeRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble()

    interface Givens : `Project Steps`.Given, `Character Steps`.Given, `Scene Steps`.Given, `Story Event Steps`.Given {
        override val `when`: Whens
    }

    val Givens.and: Givens get() = this
    val Givens.but: Givens get() = this

    interface Whens : `Character Steps`.When, `Scene Steps`.When, `Story Event Steps`.When

    interface Thens : `Scene Steps`.Then, `Story Event Steps`.Then

    val Thens.and: Thens get() = this
    val Thens.but: Thens get() = this

    val `when`: Whens = object : Whens {
        override val sceneRepository: SceneRepository = this@IntTest.sceneRepository
        override val storyEventRepository: StoryEventRepository = this@IntTest.storyEventRepository
        override val locationRepository: LocationRepository = this@IntTest.locationRepository
        override val characterRepository: CharacterRepository = this@IntTest.characterRepository
        override val themeRepository: ThemeRepository = this@IntTest.themeRepository
        override val characterArcRepository: CharacterArcRepository = this@IntTest.characterArcRepository
        override val proseRepository: ProseRepository = this@IntTest.proseRepository
    }

    val given = object : Givens {
        override val projectRepository: ProjectRepository = this@IntTest.projectRepository
        override val sceneRepository: SceneRepository = this@IntTest.sceneRepository
        override val storyEventRepository: StoryEventRepository = this@IntTest.storyEventRepository
        //override val proseRepository: ProseRepository = this@IntTest.proseRepository
        override val characterRepository: CharacterRepository = this@IntTest.characterRepository
        override val themeRepository: ThemeRepository = this@IntTest.themeRepository

        override val `when`: Whens = this@IntTest.`when`
    }

    val then: Thens = object : Thens {

        override val sceneRepository: SceneRepository = this@IntTest.sceneRepository
        override val storyEventRepository: StoryEventRepository = this@IntTest.storyEventRepository
    }

}