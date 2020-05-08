package com.soyle.stories.di.scene

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createNewScene.CreateNewSceneNotifier
import com.soyle.stories.scene.sceneList.SceneListController
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneList.SceneListPresenter
import com.soyle.stories.scene.sceneList.SceneListViewListener

object SceneListModule {

	init {
		scoped<ProjectScope> {
			provide<SceneListViewListener> {
				SceneListController(
				  applicationScope.get(),
				  get(),
				  SceneListPresenter(
					get<SceneListModel>(),
					get<CreateNewSceneNotifier>()
				  )
				)
			}
		}
	}

}