package com.soyle.stories.di.scene

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.renameCharacter.RenamedCharacterNotifier
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogController
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogViewListener
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.coverArcSectionsInScene.*
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
import com.soyle.stories.scene.includeCharacterInScene.*
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneControllerImpl
import com.soyle.stories.scene.linkLocationToScene.LinkLocationToSceneNotifier
import com.soyle.stories.scene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.removeCharacterFromScene.RemoveCharacterFromSceneNotifier
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneNotifier
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.scene.reorderScene.ReorderSceneControllerImpl
import com.soyle.stories.scene.reorderScene.ReorderSceneNotifier
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogController
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogModel
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogPresenter
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialogViewListener
import com.soyle.stories.scene.reorderSceneRamifications.*
import com.soyle.stories.scene.sceneDetails.*
import com.soyle.stories.scene.sceneDetails.includedCharacter.*
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneController
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInScenePresenter
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneState
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneViewListener
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneControllerImpl
import com.soyle.stories.scene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.*
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteSceneUseCase
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.scene.usecases.getPotentialChangeFromReorderingScene.GetPotentialChangesFromReorderingSceneUseCase
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingScene
import com.soyle.stories.scene.usecases.getPotentialChangesFromDeletingScene.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetailsUseCase
import com.soyle.stories.scene.usecases.includeCharacterInScene.GetAvailableCharactersToAddToScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInSceneUseCase
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import com.soyle.stories.scene.usecases.renameScene.RenameScene
import com.soyle.stories.scene.usecases.renameScene.RenameSceneUseCase
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene
import com.soyle.stories.scene.usecases.reorderScene.ReorderSceneUseCase
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
            provide(IncludeCharacterInScene::class, GetAvailableCharactersToAddToScene::class) {
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
            provide<GetSceneDetails> {
                GetSceneDetailsUseCase(get(), get())
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
                CreateNewSceneNotifier(applicationScope.get(), get<CreateStoryEventNotifier>())
            }
            provide(RenameScene.OutputPort::class) {
                RenameSceneNotifier(applicationScope.get())
            }
            provide(DeleteScene.OutputPort::class) {
                DeleteSceneNotifier(applicationScope.get())
            }
            provide(SetMotivationForCharacterInScene.OutputPort::class) {
                SetMotivationForCharacterInSceneNotifier(applicationScope.get())
            }
            provide(IncludeCharacterInScene.OutputPort::class) {
                IncludeCharacterInSceneOutput(get(), get())
            }

            provide(LinkLocationToScene.OutputPort::class) {
                LinkLocationToSceneNotifier(applicationScope.get())
            }
            provide(RemoveCharacterFromScene.OutputPort::class) {
                RemoveCharacterFromSceneNotifier(applicationScope.get())
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
                RemoveCharacterFromSceneController(
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
                        projectScope.get<DeleteSceneNotifier>(),
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
                        projectScope.get<DeleteSceneNotifier>(),
                        projectScope.get<RemoveCharacterFromSceneNotifier>(),
                        projectScope.get<SetMotivationForCharacterInSceneNotifier>()
                    ),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }

        scoped<SceneDetailsScope> {
            provide<SceneDetailsViewListener> {
                SceneDetailsController(
                    sceneId.toString(),
                    projectScope.applicationScope.get(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    SceneDetailsPresenter(
                        sceneId.toString(),
                        get<SceneDetailsModel>(),
                        projectScope.get(),
                        projectScope.get<LinkLocationToSceneNotifier>(),
                        projectScope.get<ReorderSceneNotifier>(),
                    ),
                    projectScope.get(),
                )
            }

            provide<IncludedCharactersInSceneViewListener> {
                val presenter = IncludedCharactersInScenePresenter(
                    sceneId.toString(),
                    get<IncludedCharactersInSceneState>()
                ).also {
                    it listensTo projectScope.get<CreatedCharacterNotifier>()
                    it listensTo projectScope.get<RemovedCharacterNotifier>()
                    it listensTo projectScope.get<IncludedCharacterInSceneNotifier>()
                    it listensTo projectScope.get<RemoveCharacterFromSceneNotifier>()
                }

                IncludedCharactersInSceneController(
                    sceneId.toString(),
                    projectScope.get(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }

        scoped<IncludedCharacterScope> {
            provide<IncludedCharacterInSceneViewListener> {

                val presenter = IncludedCharacterInScenePresenter(
                    sceneDetailsScope.sceneId.toString(),
                    characterId,
                    get<IncludedCharacterInSceneState>()
                ).apply {
                    listensTo(projectScope.get<RenamedCharacterNotifier>())
                    listensTo(projectScope.get<SetMotivationForCharacterInSceneNotifier>())
                    listensTo(projectScope.get<CharacterArcSectionsCoveredBySceneNotifier>())
                    listensTo(projectScope.get<CharacterArcSectionUncoveredInSceneNotifier>())
                }

                IncludedCharacterInSceneController(
                    sceneDetailsScope.sceneId.toString(),
                    storyEventId,
                    characterId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    presenter
                )
            }
        }

        SceneListModule

    }
}