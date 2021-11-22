package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene

class ReorderScenePresenter(
    private val view: View.Nullable<SceneListViewModel>
) : ReorderScene.OutputPort {

    override suspend fun sceneReordered(response: ReorderScene.ResponseModel) {
        val newIndices =
            response.sceneOrderUpdate.sceneOrder.order.withIndex().associate { it.value to it.index }
        view.updateOrInvalidated {
            copy(
                scenes = scenes.withIndex().map { (index, vm) ->
                    if (index != newIndices.getValue(vm.id)) {
                        vm.copy(index = index)
                    } else vm
                }.sortedBy { it.index }
            )
        }
    }

}