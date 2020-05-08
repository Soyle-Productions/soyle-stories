package com.soyle.stories.di.scene

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneControllerImpl
import com.soyle.stories.scene.createNewScene.CreateNewSceneNotifier
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogController
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogPresenter
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogViewListener
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialogModel
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneNotifier
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.scene.usecases.renameScene.RenameScene
import com.soyle.stories.scene.usecases.renameScene.RenameSceneUseCase

object SceneModule {

	init {

		scoped<ProjectScope> {

			provide<CreateNewScene> {
				CreateNewSceneUseCase(
				  projectId,
				  get()
				)
			}
			provide<ListAllScenes> {
				ListAllScenesUseCase(
				  projectId,
				  get()
				)
			}
			provide<RenameScene> {
				RenameSceneUseCase(
				  get()
				)
			}

			provide(CreateNewScene.OutputPort::class) {
				CreateNewSceneNotifier()
			}
			provide(RenameScene.OutputPort::class) {
				RenameSceneNotifier()
			}

			provide<CreateNewSceneController> {
				CreateNewSceneControllerImpl(
				  applicationScope.get(),
				  applicationScope.get(),
				  get(),
				  get()
				)
			}
			provide<RenameSceneController> {
				RenameSceneControllerImpl(
				  applicationScope.get(),
				  applicationScope.get(),
				  get(),
				  get()
				)
			}

			provide<CreateNewSceneDialogViewListener> {
				CreateNewSceneDialogController(
				  CreateNewSceneDialogPresenter(
					get<CreateSceneDialogModel>(),
					get<CreateNewSceneNotifier>()
				  ),
				  get()
				)
			}

		}

		SceneListModule

	}
}