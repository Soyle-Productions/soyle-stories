package com.soyle.stories.core.definitions.scene

import com.soyle.stories.core.definitions.scene.character.`Characters in Scene Query`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.delete.PotentialChangesOfDeletingScene
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.runBlocking

class SceneQueries(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository,
    private val storyEventRepository: StoryEventRepository
) : `Scene Steps`.When.UserQueries, `Scene Steps`.When.UserQueries.PotentialWhens {

    val characters: `Scene Character Steps`.When.UserQueries = `Characters in Scene Query`(
        sceneRepository,
        characterRepository,
        storyEventRepository
    )

    override fun the(scene: Scene.Id): `Scene Steps`.When.UserQueries.PotentialWhens.PotentialActions =
        object :  `Scene Steps`.When.UserQueries.PotentialWhens.PotentialActions {
            override fun `being removed from the`(project: Project.Id): PotentialChangesOfDeletingScene {
                val useCase = GetPotentialChangesFromDeletingSceneUseCase(sceneRepository, locationRepository, storyEventRepository, characterRepository)
                var result = Result.failure<PotentialChangesOfDeletingScene>(Error("No response received"))
                runBlocking {
                    useCase.invoke(scene) {
                        result = Result.success(it)
                    }
                }
                return result.getOrThrow()
            }
        }

}