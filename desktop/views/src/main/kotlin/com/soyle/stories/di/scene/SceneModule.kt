package com.soyle.stories.di.scene

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.*
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneControllerImpl
import com.soyle.stories.scene.createNewScene.CreateNewSceneNotifier
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogController
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogPresenter
import com.soyle.stories.scene.createNewSceneDialog.CreateNewSceneDialogViewListener
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialogModel
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneControllerImpl
import com.soyle.stories.scene.deleteScene.DeleteSceneOutput
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogController
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogModel
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogPresenter
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialogViewListener
import com.soyle.stories.scene.deleteSceneRamifications.*
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.*
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneOutput
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.scene.reorderScene.ReorderSceneControllerImpl
import com.soyle.stories.scene.reorderScene.ReorderSceneNotifier
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogController
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogModel
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogPresenter
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogViewListener
import com.soyle.stories.scene.reorderSceneRamifications.*
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.deleteScene.SceneDeletedNotifier
import com.soyle.stories.storyevent.create.CreateStoryEventOutput
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.*
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.usecase.scene.deleteScene.DeleteSceneUseCase
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingSceneUseCase
import com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.usecase.scene.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.character.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.usecase.scene.character.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenes
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import com.soyle.stories.usecase.scene.renameScene.RenameScene
import com.soyle.stories.usecase.scene.renameScene.RenameSceneUseCase
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.usecase.scene.reorderScene.ReorderSceneUseCase
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase

object SceneModule {

    init {

        scoped<ProjectScope> {

            provide<CreateNewScene> {
                CreateNewSceneUseCase(
                    projectId,
                    get(),
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
                    get(),
                    get()
                )
            }
            provide<DeleteScene> {
                DeleteSceneUseCase(
                    get(),
                    get()
                )
            }
            provide(IncludeCharacterInScene::class, ListAvailableCharactersToIncludeInScene::class) {
                IncludeCharacterInSceneUseCase(
                    get(),
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
            provide<GetPotentialChangesFromReorderingScene> {
                GetPotentialChangesFromReorderingSceneUseCase(get())
            }
            provide<LinkLocationToScene> {
                LinkLocationToSceneUseCase(get(), get())
            }
            provide<RemoveCharacterFromScene> {
                RemoveCharacterFromSceneUseCase(get())
            }
            provide<ReorderScene> {
                ReorderSceneUseCase(get())
            }
            provide(CoverCharacterArcSectionsInScene::class, GetAvailableCharacterArcsForCharacterInScene::class) {
                CoverCharacterArcSectionsInSceneUseCase(get(), get())
            }
            provide<ChangeCharacterArcSectionValueAndCoverInScene> {
                ChangeCharacterArcSectionValueAndCoverInSceneUseCase(get(), get(), get())
            }
            provide(CreateCharacterArcSectionAndCoverInScene::class, GetAvailableCharacterArcSectionTypesForCharacterArc::class) {
                CreateCharacterArcSectionAndCoverInSceneUseCase(get(), get())
            }


            provide(IncludedCharacterInSceneReceiver::class) {
                IncludedCharacterInSceneNotifier()
            }
            provide(CharacterArcSectionsCoveredBySceneReceiver::class) {
                CharacterArcSectionsCoveredBySceneNotifier()
            }
            provide(CharacterArcSectionUncoveredInSceneReceiver::class) {
                CharacterArcSectionUncoveredInSceneNotifier()
            }

            provide(CreateNewScene.OutputPort::class) {
                CreateNewSceneNotifier(applicationScope.get(), get<CreateStoryEventOutput>())
            }
            provide(RenameScene.OutputPort::class) {
                RenameSceneOutput(get(), get())
            }
            provide(DeleteScene.OutputPort::class) {
                DeleteSceneOutput(applicationScope.get(), get(), get())
            }
            provide(SetMotivationForCharacterInScene.OutputPort::class) {
                SetMotivationForCharacterInSceneNotifier(applicationScope.get())
            }
            provide(IncludeCharacterInScene.OutputPort::class) {
                IncludeCharacterInSceneOutput(get(), get())
            }

            provide(ReorderScene.OutputPort::class) {
                ReorderSceneNotifier(applicationScope.get())
            }
            provide(
                CoverCharacterArcSectionsInScene.OutputPort::class,
                ChangeCharacterArcSectionValueAndCoverInScene.OutputPort::class,
                CreateCharacterArcSectionAndCoverInScene.OutputPort::class
            ) {
                CoverCharacterArcSectionsInSceneOutputPort(get(), get(), get())
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
            provide(IncludeCharacterInSceneController::class) {
                IncludeCharacterInSceneControllerImpl(
                    applicationScope.get(),
                    get(),
                    get()
                )
            }
            provide<LinkLocationToSceneController> {
                LinkLocationToSceneControllerImpl(
                    applicationScope.get(),
                    applicationScope.get(),
                    get(),
                    get()
                )
            }
            provide {
                RemoveCharacterFromSceneControllerImpl(
                    applicationScope.get(),
                    applicationScope.get(),
                    get(),
                    get()
                )
            }
            provide<ReorderSceneController> {
                ReorderSceneControllerImpl(
                    applicationScope.get(),
                    applicationScope.get(),
                    get(),
                    get()
                )
            }
            provide<CoverArcSectionsInSceneController> {
                CoverArcSectionsInSceneControllerImpl(
                    applicationScope.get(),
                    get(),
                    get(),
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
            provide<ReorderSceneDialogViewListener> {
                ReorderSceneDialogController(
                    applicationScope.get(),
                    ReorderSceneDialogPresenter(
                        get<ReorderSceneDialogModel>()
                    ),
                    get(),
                    get(),
                    get(),
                    get()
                )
            }

        }

        scoped<DeleteSceneRamificationsScope> {

            provide<DeleteSceneRamificationsViewListener> {
                DeleteSceneRamificationsController(
                    sceneId,
                    toolId,
                    applicationScope.get(),
                    applicationScope.get(),
                    projectScope.get(),
                    DeleteSceneRamificationsPresenter(
                        get<DeleteSceneRamificationsModel>(),
                        projectScope.get<SceneDeletedNotifier>(),
                        projectScope.get<RemovedCharacterNotifier>(),
                        projectScope.get<SetMotivationForCharacterInSceneNotifier>()
                    ),
                    projectScope.get(),
                    projectScope.get()
                )
            }

        }

        scoped<ReorderSceneRamificationsScope> {
            provide<ReorderSceneRamificationsViewListener> {
                ReorderSceneRamificationsController(
                    sceneId,
                    toolId,
                    reorderIndex,
                    applicationScope.get(),
                    projectScope.get(),
                    ReorderSceneRamificationsPresenter(
                        get<ReorderSceneRamificationsModel>(),
                        projectScope.get<SceneDeletedNotifier>(),
                        projectScope.get<RemovedCharacterFromSceneNotifier>(),
                        projectScope.get<SetMotivationForCharacterInSceneNotifier>()
                    ),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }

    }
}