package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.gui.View
import com.soyle.stories.scene.create.SceneCreatedReceiver
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel

class CreateScenePresenter(
    private val view: View.Nullable<SceneListViewModel>
) : SceneCreatedReceiver {

    override suspend fun receiveSceneCreated(event: SceneCreated, orderUpdate: SceneOrderUpdate<*>) {
        view.updateOrInvalidated {

            val sceneOrder = orderUpdate.sceneOrder.order.withIndex().associate { it.value to it.index }

            copy(
                scenes = (scenes.map {
                    if (it.index != sceneOrder.getValue(it.id)) {
                        it.withIndex(sceneOrder.getValue(it.id))
                    } else it
                } + SceneItemViewModel(
                    event.sceneId,
                    event.proseId,
                    event.name,
                    sceneOrder.getValue(event.sceneId),
                    invalidEntitiesMentioned = false,
                    unusedSymbols = false,
                    inconsistentSettings = false
                )).sortedBy { it.index }
            )
        }
    }

}