package com.soyle.stories.usecase.scene.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.shared.repositories.Repository
import com.soyle.stories.usecase.shared.repositories.Transaction

interface SceneRepository : Repository<Scene.Id, Scene>{

    interface SceneTransaction : Transaction<Scene.Id, Scene> {

        /**
         * @throws SceneDoesNotExist
         */
        override suspend fun Repository<Scene.Id, Scene>.getOrError(id: Scene.Id): Scene =
            get(id) ?: throw SceneDoesNotExist(id.uuid)

        suspend fun SceneRepository.listAllScenesInProject(projectId: Project.Id, exclude: Set<Scene.Id> = emptySet()): List<Scene>

        suspend fun SceneRepository.getProjectSceneOrder(projectId: Project.Id): SceneOrder?

        suspend fun SceneRepository.getSceneThatOwnsProse(proseId: Prose.Id): Scene?

        suspend fun SceneRepository.listScenesIncludingCharacter(characterId: Character.Id): List<Scene>
        suspend fun SceneRepository.listScenesTrackingSymbol(symbolId: Symbol.Id): List<Scene>
        suspend fun SceneRepository.listScenesUsingLocation(locationId: Location.Id): List<Scene>
    }

    override fun startTransaction(): SceneTransaction
}