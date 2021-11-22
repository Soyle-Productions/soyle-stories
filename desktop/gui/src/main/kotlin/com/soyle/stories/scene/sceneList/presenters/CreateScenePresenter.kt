package com.soyle.stories.scene.sceneList.presenters

import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.gui.View
import com.soyle.stories.scene.create.SceneCreatedReceiver
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListViewModel
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

class CreateScenePresenter(
    private val view: View.Nullable<SceneListViewModel>
) : SceneCreatedReceiver {

    override suspend fun receiveSceneCreated(event: SceneCreated) {
        TODO()
        /*view.updateOrInvalidated {

            event

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
                    false,
					false,
                    false
                )).sortedBy { it.index }
            )
        }*/
    }

}