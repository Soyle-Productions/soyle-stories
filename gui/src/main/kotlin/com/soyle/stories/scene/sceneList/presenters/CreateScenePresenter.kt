package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.gui.View
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent

class CreateScenePresenter(
    private val view: View.Nullable<SceneListViewModel>
) : CreateNewScene.OutputPort {

    override val createStoryEventOutputPort: CreateStoryEvent.OutputPort
        get() = error("$this does not supply create story event output port")

    override fun receiveCreateNewSceneFailure(failure: Exception) {
    }

    override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
        view.updateOrInvalidated {

            val affectedScenes = response.affectedScenes.associateBy { it.id.toString() }

            copy(
                scenes = (scenes.map {
                    if (it.id in affectedScenes) {
                        SceneItemViewModel(affectedScenes.getValue(it.id), it.hasProblem)
                    } else it
                } + SceneItemViewModel(
                    response.sceneId.toString(),
                    response.sceneProse,
                    response.sceneName,
                    response.sceneIndex,
					false
                )).sortedBy { it.index }
            )
        }
    }

}