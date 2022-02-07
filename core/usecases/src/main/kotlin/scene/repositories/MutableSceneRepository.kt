package com.soyle.stories.usecase.scene.repositories

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.usecase.shared.repositories.MutableRepository
import com.soyle.stories.usecase.shared.repositories.MutableTransaction

interface MutableSceneRepository : MutableRepository<Scene.Id, Scene>, SceneRepository {

    interface MutableSceneTransaction : SceneRepository.SceneTransaction, MutableTransaction<Scene.Id, Scene> {

        suspend fun MutableSceneRepository.save(sceneOrder: SceneOrder)

        suspend fun MutableSceneRepository.remove(sceneId: Scene.Id)
    }

    override fun startTransaction(): MutableSceneTransaction

}