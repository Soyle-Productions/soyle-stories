package com.soyle.stories.di.scene

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.*
import com.soyle.stories.scene.create.CreateNewSceneController
import com.soyle.stories.scene.create.CreateNewSceneOutput
import com.soyle.stories.scene.delete.DeleteSceneController
import com.soyle.stories.scene.delete.DeleteSceneOutput
import com.soyle.stories.scene.deleteSceneRamifications.*
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.*
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneOutput
import com.soyle.stories.scene.reorder.ReorderSceneController
import com.soyle.stories.scene.reorder.ReorderSceneControllerImpl
import com.soyle.stories.scene.reorder.ReorderSceneNotifier
import com.soyle.stories.scene.reorderSceneRamifications.*
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.delete.DeleteScenePromptPresenter
import com.soyle.stories.scene.delete.SceneDeletedNotifier
import com.soyle.stories.scene.delete.ramifications.DeleteSceneRamificationsReportPresenter
import com.soyle.stories.scene.reorder.ReorderScenePromptPresenter
import com.soyle.stories.scene.reorder.ramifications.ReorderSceneRamificationsReportPresenter
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
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import kotlinx.coroutines.CompletableDeferred

object SceneModule {

    init {

        scoped<ProjectScope> {

            provide<CreateNewScene> {
                CreateNewSceneUseCase(
                    get(),
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
                CreateNewSceneOutput(applicationScope.get(), get())
            }
            provide(RenameScene.OutputPort::class) {
                RenameSceneOutput(get(), get())
            }
            provide(DeleteScene.OutputPort::class) {
                DeleteSceneOutput(applicationScope.get(), get())
            }
            provide(SetMotivationForCharacterInScene.OutputPort::class) {
                SetMotivationForCharacterInSceneNotifier(applicationScope.get())
            }
            provide(IncludeCharacterInScene.OutputPort::class) {
                IncludeCharacterInSceneOutput(get(), get())
            }

            provide(ReorderScene.OutputPort::class) { ReorderSceneNotifier() }
            provide(
                CoverCharacterArcSectionsInScene.OutputPort::class,
                ChangeCharacterArcSectionValueAndCoverInScene.OutputPort::class,
                CreateCharacterArcSectionAndCoverInScene.OutputPort::class
            ) {
                CoverCharacterArcSectionsInSceneOutputPort(get(), get(), get())
            }

            provide<CreateNewSceneController> {
                CreateNewSceneController.Implementation(
                    Project.Id(projectId),
                    applicationScope.get<ThreadTransformer>().guiContext,
                    applicationScope.get<ThreadTransformer>().asyncContext,
                    get(),
                    get(),
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
            provide<suspend (DialogType) -> DialogPreference> {
                {
                    val deferred = CompletableDeferred<DialogPreference>()
                    get<GetDialogPreferences>().invoke(it, object : GetDialogPreferences.OutputPort {
                        override fun gotDialogPreferences(response: DialogPreference) {
                            deferred.complete(response)
                        }

                        override fun failedToGetDialogPreferences(failure: Exception) {
                            deferred.complete(DialogPreference(it, true))
                        }
                    })
                    deferred.await()
                }
            }
            provide<DeleteSceneController> {
                DeleteSceneController.Implementation(
                    applicationScope.get<ThreadTransformer>().guiContext,
                    applicationScope.get<ThreadTransformer>().asyncContext,
                    get(),
                    get<DeleteScenePromptPresenter>(),
                    get<DeleteSceneRamificationsReportPresenter>(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
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
                    get(),
                    get<ReorderScenePromptPresenter>(),
                    get<ReorderSceneRamificationsReportPresenter>(),
                    get(),
                    get(),
                    get(),
                    get(),
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

        }

    }
}