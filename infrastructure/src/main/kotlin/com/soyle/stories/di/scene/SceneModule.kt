package com.soyle.stories.di.scene

import com.soyle.stories.characterarc.eventbus.RemoveCharacterFromLocalStoryNotifier
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
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneControllerImpl
import com.soyle.stories.scene.deleteScene.DeleteSceneNotifier
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogController
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogModel
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogPresenter
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogViewListener
import com.soyle.stories.scene.deleteSceneRamifications.*
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.includeCharacterInScene.IncludeCharacterInSceneNotifier
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneNotifier
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneControllerImpl
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteSceneUseCase
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.scene.usecases.renameScene.RenameScene
import com.soyle.stories.scene.usecases.renameScene.RenameSceneUseCase
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventNotifier

object SceneModule {

	init {

		scoped<ProjectScope> {

			provide<CreateNewScene> {
				CreateNewSceneUseCase(
				  projectId,
				  get(),
				  get(),
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
			provide<DeleteScene> {
				DeleteSceneUseCase(
				  get()
				)
			}
			provide<IncludeCharacterInScene> {
				IncludeCharacterInSceneUseCase(
				  get(),
				  get()
				)
			}
			provide<SetMotivationForCharacterInScene> {
				SetMotivationForCharacterInSceneUseCase(
				  get(),
				  get()
				)
			}
			provide<GetPotentialChangesFromDeletingScene> {
				GetPotentialChangesFromDeletingSceneUseCase(get())
			}

			provide(CreateNewScene.OutputPort::class) {
				CreateNewSceneNotifier(get<CreateStoryEventNotifier>())
			}
			provide(RenameScene.OutputPort::class) {
				RenameSceneNotifier()
			}
			provide(DeleteScene.OutputPort::class) {
				DeleteSceneNotifier()
			}
			provide(SetMotivationForCharacterInScene.OutputPort::class) {
				SetMotivationForCharacterInSceneNotifier()
			}
			provide(IncludeCharacterInScene.OutputPort::class) {
				IncludeCharacterInSceneNotifier()
			}

			provide<CreateNewSceneController> {
				CreateNewSceneControllerImpl(
				  projectId.toString(),
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
			provide<DeleteSceneController> {
				DeleteSceneControllerImpl(
				  applicationScope.get(),
				  applicationScope.get(),
				  get(),
				  get()
				)
			}
			provide<SetMotivationForCharacterInSceneController> {
				SetMotivationForCharacterInSceneControllerImpl(
				  applicationScope.get(),
				  applicationScope.get(),
				  get(),
				  get()
				)
			}
			provide {
				IncludeCharacterInSceneController(
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
			provide<DeleteSceneDialogViewListener> {
				val presenter = DeleteSceneDialogPresenter(
				  get<DeleteSceneDialogModel>()
				)
				DeleteSceneDialogController(
				  applicationScope.get(),
				  presenter,
				  get(),
				  get(),
				  get(),
				  presenter,
				  get()
				)
			}

		}

		scoped<DeleteSceneRamificationsScope> {

			provide<DeleteSceneRamificationsViewListener> {
				DeleteSceneRamificationsController(
				  sceneId,
				  applicationScope.get(),
				  applicationScope.get(),
				  projectScope.get(),
				  DeleteSceneRamificationsPresenter(
					get<DeleteSceneRamificationsModel>(),
					projectScope.get<DeleteSceneNotifier>(),
					projectScope.get<RemoveCharacterFromLocalStoryNotifier>(),
					projectScope.get<SetMotivationForCharacterInSceneNotifier>()
				  )
				)
			}

		}

		SceneListModule

	}
}