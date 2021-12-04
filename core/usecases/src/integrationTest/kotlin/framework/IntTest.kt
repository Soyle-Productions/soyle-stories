package com.soyle.stories.usecase.framework

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

abstract class IntTest {

    private val projectRepository = object : ProjectRepository {
        override suspend fun addNewProject(project: Project) {

        }
    }
    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val proseRepository = ProseRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    interface Givens : `Project Steps`.Given, `Scene Steps`.Given, `Story Event Steps`.Given

    val Givens.and: Givens get() = this
    val Givens.but: Givens get() = this

    interface Whens : `Scene Steps`.When, `Story Event Steps`.When

    interface Thens : `Scene Steps`.Then, `Story Event Steps`.Then

    val Thens.and: Thens get() = this
    val Thens.but: Thens get() = this

    val given = object : Givens {
        override val projectRepository: ProjectRepository = this@IntTest.projectRepository
        override val sceneRepository: SceneRepository = this@IntTest.sceneRepository
        override val storyEventRepository: StoryEventRepository = this@IntTest.storyEventRepository
        override val proseRepository: ProseRepository = this@IntTest.proseRepository
    }

    val `when`: Whens = object : Whens {
        override val sceneRepository: SceneRepository = this@IntTest.sceneRepository
        override val storyEventRepository: StoryEventRepository = this@IntTest.storyEventRepository
        override val locationRepository: LocationRepository = this@IntTest.locationRepository
    }

    val then: Thens = object : Thens {

        override val sceneRepository: SceneRepository = this@IntTest.sceneRepository
        override val storyEventRepository: StoryEventRepository = this@IntTest.storyEventRepository
    }

}