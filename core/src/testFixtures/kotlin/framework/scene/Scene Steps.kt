package com.soyle.stories.core.framework.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.sceneName
import com.soyle.stories.usecase.scene.delete.PotentialChangesOfDeletingScene

interface `Scene Steps` {

    interface Given : `Scene Character Steps`.Given {

        fun `a scene`(named: String = sceneName().value, atIndex: Int? = null): ExistenceExpectations
        interface ExistenceExpectations {
            infix fun `has been created in the`(project: Project.Id): Scene.Id
        }

        fun the(scene: Scene.Id): StateExpectations
        interface StateExpectations {
            fun `has been removed from the`(project: Project.Id)
        }

    }

    interface When : `Scene Character Steps`.When {

        fun `a scene`(named: String = sceneName().value, atIndex: Int? = null): CreationActions
        interface CreationActions {
            infix fun `is created in the`(projectId: Project.Id): Scene.Id
        }

        infix fun the(scene: Scene.Id): SceneActions
        interface SceneActions {
            fun `is deleted`()
        }

        interface UserQueries {
            interface PotentialWhens {
                fun the(scene: Scene.Id): PotentialActions
                interface PotentialActions {
                    fun `being removed from the`(project: Project.Id): PotentialChangesOfDeletingScene
                }
            }
        }

    }

    interface Then : `Scene Character Steps`.Then, `Covered Story Events Steps`.Then {

        infix fun the(sceneId: Scene.Id): StateAssertions
        interface StateAssertions {
            fun `should not include any characters`()
            infix fun `should include the`(characterId: Character.Id)
            infix fun `should not include the`(characterId: Character.Id)
        }

    }
}