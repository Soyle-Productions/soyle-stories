package com.soyle.stories.core.definitions.scene

import com.soyle.stories.core.definitions.scene.character.`Characters in Scene Expectations`
import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.core.framework.scene.`Scene Steps`
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import kotlinx.coroutines.runBlocking

class `Scene Expectations`(
    private val sceneRepository: SceneRepository,

    private val `when`: `Scene Steps`.When
) : `Scene Steps`.Given,
    `Scene Character Steps`.Given by `Characters in Scene Expectations`(sceneRepository, `when`) {

    override fun `a scene`(named: String, atIndex: Int?): `Scene Steps`.Given.ExistenceExpectations = object :
        `Scene Steps`.Given.ExistenceExpectations {
        override fun `has been created in the`(project: Project.Id): Scene.Id {
            val id = runBlocking { sceneRepository.listAllScenesInProject(project) }
                .find { it.name.value == named }
                ?.id
            return id ?: `when`.`a scene`(named, atIndex).`is created in the`(project)
        }
    }

    override fun the(scene: Scene.Id): `Scene Steps`.Given.StateExpectations =
        object : `Scene Steps`.Given.StateExpectations {
            override fun `has been removed from the`(project: Project.Id) {
                val sceneEntity = runBlocking { sceneRepository.getSceneById(scene) }
                if (sceneEntity?.projectId != null) {
                    `when`.the(scene).`is deleted`()
                }
            }
        }

}