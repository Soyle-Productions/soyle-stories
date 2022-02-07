package com.soyle.stories.desktop.config.scene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.characters.tool.SceneCharactersToolScope
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneOutput
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverArcSectionsInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverCharacterArcSectionsInSceneOutputPort
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.ListAvailableArcSectionsToCoverInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.CharacterIncludedInSceneNotifier
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneOutput
import com.soyle.stories.scene.charactersInScene.inspect.InspectCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.involve.InvolveCharacterInSceneOutput
import com.soyle.stories.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneController
import com.soyle.stories.scene.charactersInScene.listCharactersInScene.ListCharactersInSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneOutput
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneController
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneOutput
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.*
import com.soyle.stories.scene.create.CreateNewSceneController
import com.soyle.stories.scene.create.CreateNewSceneOutput
import com.soyle.stories.scene.create.CreateScenePromptPresenter
import com.soyle.stories.scene.delete.DeleteSceneController
import com.soyle.stories.scene.delete.DeleteSceneOutput
import com.soyle.stories.scene.delete.deleteScenePrompt
import com.soyle.stories.scene.delete.ramifications.deleteSceneRamifications
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneController
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneControllerImpl
import com.soyle.stories.scene.locationsInScene.detectInconsistencies.DetectInconsistenciesInSceneSettingsController
import com.soyle.stories.scene.locationsInScene.detectInconsistencies.DetectInconsistenciesInSceneSettingsOutput
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneControllerImpl
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneOutput
import com.soyle.stories.scene.locationsInScene.listLocationsInScene.ListLocationsInSceneController
import com.soyle.stories.scene.locationsInScene.listLocationsInScene.ListLocationsInSceneControllerImpl
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneController
import com.soyle.stories.scene.locationsInScene.listLocationsToUse.ListLocationsToUseInSceneControllerImpl
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneController
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneControllerImpl
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneOutput
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneController
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneOutput
import com.soyle.stories.scene.outline.OutlineSceneController
import com.soyle.stories.scene.outline.SceneOutlineReportPresenter
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneOutput
import com.soyle.stories.scene.reorder.ReorderSceneController
import com.soyle.stories.scene.reorder.ReorderSceneControllerImpl
import com.soyle.stories.scene.reorder.ReorderSceneNotifier
import com.soyle.stories.scene.reorder.ReorderScenePromptPresenter
import com.soyle.stories.scene.reorder.ramifications.ReorderSceneRamificationsReportPresenter
import com.soyle.stories.scene.sceneFrame.*
import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInScene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CoverCharacterArcSectionsInSceneUseCase
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInScene
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.inspect.InspectCharacterInScene
import com.soyle.stories.usecase.scene.character.inspect.InspectCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.character.involve.InvolveCharacterInScene
import com.soyle.stories.usecase.scene.character.involve.InvolveCharacterInSceneService
import com.soyle.stories.usecase.scene.character.list.ListCharactersInScene
import com.soyle.stories.usecase.scene.character.list.ListCharactersInSceneUseCase
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneUseCase
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInSceneUseCase
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.usecase.scene.delete.DeleteScene
import com.soyle.stories.usecase.scene.delete.DeleteSceneUseCase
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingScene
import com.soyle.stories.usecase.scene.delete.GetPotentialChangesFromDeletingSceneUseCase
import com.soyle.stories.usecase.scene.list.ListAllScenes
import com.soyle.stories.usecase.scene.list.ListAllScenesUseCase
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettings
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettingsUseCase
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInSceneUseCase
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInSceneUseCase
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromSceneUseCase
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInScene
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInSceneUseCase
import com.soyle.stories.usecase.scene.renameScene.RenameScene
import com.soyle.stories.usecase.scene.renameScene.RenameSceneUseCase
import com.soyle.stories.usecase.scene.reorderScene.GetPotentialChangesFromReorderingScene
import com.soyle.stories.usecase.scene.reorderScene.GetPotentialChangesFromReorderingSceneUseCase
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.usecase.scene.reorderScene.ReorderSceneUseCase
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrameUseCase
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValue
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValueUseCase
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredBySceneUseCase
import com.soyle.stories.usecase.scene.symbol.listSymbolsInScene.ListSymbolsInScene
import com.soyle.stories.usecase.scene.symbol.listSymbolsInScene.ListSymbolsInSceneUseCase
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.*

object UseCases {

    init {
        scoped<ProjectScope> {
            provide { SceneOrderService() }

            createNewScene()
            listAllScenes()
            renameScene()
            deleteScene()
            outlineScene()
            includeCharacterInScene()
            setMotivationForCharacterInScene()
            listCharactersInScene()
            linkLocationToScene()
            removeCharacterFromScene()
            reorderScene()
            coverCharacterArcSectionsInScene()
            assignRoleToCharacter()
            setCharacterDesireInScene()
            synchronizeTrackedSymbolsWithProse()
            listSymbolsInScene()
            listAvailableSymbolsToTrackInScene()
            pinSymbolToScene()
            unpinSymbolFromScene()
            detectUnusedSymbols()
            getSceneFrame()
            setSceneFrameValue()
            listLocationsInScene()
            listLocationsToUse()
            removeLocationFromScene()
            detectInconsistenciesInScene()
            replaceSettingInScene()
            inspectCharacterInScene()
        }
    }

    private fun InProjectScope.createNewScene() {
        provide<CreateNewScene> {
            CreateNewSceneUseCase(get(), get(), get(), get(), get())
        }

        provide {
            CreateNewSceneController.Implementation(
                Project.Id(projectId),
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get<CreateScenePromptPresenter>(),
                get(),
                get(),
                get()
            )
        }

        provide(CreateNewScene.OutputPort::class) {
            CreateNewSceneOutput(get(), get())
        }
    }

    private fun InProjectScope.listAllScenes() {
        provide<ListAllScenes> {
            ListAllScenesUseCase(get())
        }
    }

    private fun InProjectScope.renameScene() {
        provide<RenameScene> {
            RenameSceneUseCase(get(), get())
        }

        provide<RenameSceneController> {
            RenameSceneControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(RenameScene.OutputPort::class) {
            RenameSceneOutput(get(), get())
        }
    }

    private fun InScope<ProjectScope>.deleteScene() {
        provide<GetPotentialChangesFromDeletingScene> {
            GetPotentialChangesFromDeletingSceneUseCase(get(), get(), get(), get())
        }
        provide<DeleteScene> {
            DeleteSceneUseCase(get(), get(), get())
        }

        provide<DeleteSceneController> {
            DeleteSceneController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                { deleteScenePrompt(this) },
                { deleteSceneRamifications(this, get(), it) },
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }

        provide(DeleteScene.OutputPort::class) {
            DeleteSceneOutput(get(), get(), get())
        }
    }

    private fun InProjectScope.outlineScene() {
        provide {
            OutlineSceneController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get<SceneOutlineReportPresenter>()::createOutline,
                get()
            )
        }

        provide<ListStoryEventsCoveredByScene> {
            ListStoryEventsCoveredBySceneUseCase(
                get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.includeCharacterInScene() {

        provide<ListAvailableCharactersToIncludeInScene> {
            ListAvailableCharactersToIncludeInSceneUseCase(get(), get(), get())
        }
        provide<IncludeCharacterInScene> {
            IncludeCharacterInSceneUseCase(get(), get())
        }
        provide<IncludeCharacterInScene.OutputPort> {
            IncludeCharacterInSceneOutput(get<CharacterIncludedInSceneNotifier>())
        }

        provide {
            ListAvailableCharactersToIncludeInSceneController(applicationScope.get(), get())
        }
        provide<InvolveCharacterInScene.OutputPort> {
            InvolveCharacterInSceneOutput(get(), get())
        }
        provide<InvolveCharacterInScene> {
            InvolveCharacterInSceneService(get(), get())
        }
        provide<IncludeCharacterInSceneController> {
            IncludeCharacterInSceneController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        scoped<SceneCharactersToolScope> {
            hoist<IncludeCharacterInSceneController> { projectScope }
        }
    }

    private fun InProjectScope.setMotivationForCharacterInScene() {
        provide<SetMotivationForCharacterInScene> {
            SetMotivationForCharacterInSceneUseCase(get(), get(), get())
        }

        provide<SetMotivationForCharacterInSceneController> {
            SetMotivationForCharacterInSceneControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }
        scoped<SceneCharactersToolScope> {
            hoist<SetMotivationForCharacterInSceneController> { projectScope }
        }

        provide(SetMotivationForCharacterInScene.OutputPort::class) {
            SetMotivationForCharacterInSceneOutput(
                get<CharacterGainedMotivationInSceneNotifier>(),
                get<CharacterMotivationInSceneClearedNotifier>(),
                get<CharacterIncludedInSceneNotifier>()
            )
        }
    }

    private fun InProjectScope.listCharactersInScene() {
        provide<ListCharactersInScene> {
            ListCharactersInSceneUseCase(get(), get(), get())
        }

        provide<ListCharactersInSceneController> {
            ListCharactersInSceneController.Implementation(
                applicationScope.get<ThreadTransformer>().guiContext,
                applicationScope.get<ThreadTransformer>().asyncContext,
                get()
            )
        }
        scoped<SceneCharactersToolScope> {
            hoist<ListCharactersInSceneController> { projectScope }
        }
    }

    private fun InProjectScope.linkLocationToScene() {
        provide<LinkLocationToScene> {
            LinkLocationToSceneUseCase(get(), get())
        }

        provide<LinkLocationToSceneController> {
            LinkLocationToSceneControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(LinkLocationToScene.OutputPort::class) {
            LinkLocationToSceneOutput(get(), get())
        }
    }

    private fun InProjectScope.removeCharacterFromScene() {
        provide<RemoveCharacterFromScene> {
            RemoveCharacterFromSceneUseCase(get())
        }

        provide(RemoveCharacterFromSceneController::class) {
            RemoveCharacterFromSceneControllerImpl(
                applicationScope.get<ThreadTransformer>().asyncContext,
                applicationScope.get<ThreadTransformer>().guiContext,
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        scoped<SceneCharactersToolScope> {
            hoist<RemoveCharacterFromSceneController> { projectScope }
        }

        provide(RemoveCharacterFromScene.OutputPort::class) {
            RemoveCharacterFromSceneOutput(get<RemovedCharacterFromSceneNotifier>())
        }
    }

    private fun InProjectScope.reorderScene() {
        provide<GetPotentialChangesFromReorderingScene> {
            GetPotentialChangesFromReorderingSceneUseCase(get(), get())
        }
        provide<ReorderScene> {
            ReorderSceneUseCase(get())
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

        provide(ReorderScene.OutputPort::class) {
            ReorderSceneNotifier()
        }
    }

    private fun InProjectScope.coverCharacterArcSectionsInScene() {
        provide(
            CoverCharacterArcSectionsInScene::class,
            GetAvailableCharacterArcsForCharacterInScene::class
        ) {
            CoverCharacterArcSectionsInSceneUseCase(get(), get())
        }
        provide {
            ListAvailableArcSectionsToCoverInSceneController(applicationScope.get(), get())
        }

        provide<CoverArcSectionsInSceneController> {
            CoverArcSectionsInSceneControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(
            CoverCharacterArcSectionsInScene.OutputPort::class
        ) {
            CoverCharacterArcSectionsInSceneOutputPort(get(), get())
        }
    }

    private fun InProjectScope.assignRoleToCharacter() {
        provide<AssignRoleToCharacterInScene> {
            AssignRoleToCharacterInSceneUseCase(get(), get(), get())
        }

        provide {
            AssignRoleToCharacterInSceneController(applicationScope.get(), get(), get())
        }
        scoped<SceneCharactersToolScope> {
            hoist<AssignRoleToCharacterInSceneController> { projectScope }
        }
        provide<AssignRoleToCharacterInScene.OutputPort> {
            AssignRoleToCharacterInSceneOutput(get())
        }
    }

    private fun InProjectScope.setCharacterDesireInScene() {
        provide<SetCharacterDesireInScene> {
            SetCharacterDesireInSceneUseCase(get(), get(), get())
        }

        provide {
            SetCharacterDesireInSceneController(applicationScope.get(), get(), get())
        }
        scoped<SceneCharactersToolScope> {
            hoist<SetCharacterDesireInSceneController> { projectScope }
        }
        provide<SetCharacterDesireInScene.OutputPort> {
            SetCharacterDesireInSceneOutput(get())
        }
    }

    private fun InProjectScope.synchronizeTrackedSymbolsWithProse() {
        provide<SynchronizeTrackedSymbolsWithProse> {
            SynchronizeTrackedSymbolsWithProseUseCase(get(), get(), get())
        }
        provide<SynchronizeTrackedSymbolsWithProse.OutputPort> {
            SynchronizeTrackedSymbolsWithProseOutput(get(), get())
        }
    }

    private fun InProjectScope.listSymbolsInScene() {
        provide<ListSymbolsInScene> {
            ListSymbolsInSceneUseCase(get(), get())
        }
        provide<ListSymbolsInSceneController> {
            ListSymbolsInSceneControllerImpl(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.listAvailableSymbolsToTrackInScene() {
        provide<ListAvailableSymbolsToTrackInSceneController> {
            ListAvailableSymbolsToTrackInSceneControllerImpl(applicationScope.get(), get())
        }

        provide<ListAvailableSymbolsToTrackInScene> {
            ListAvailableSymbolsToTrackInSceneUseCase(get(), get())
        }
    }

    private fun InProjectScope.pinSymbolToScene() {
        provide<PinSymbolToScene> {
            PinSymbolToSceneUseCase(get(), get())
        }
        provide(PinSymbolToScene.OutputPort::class) {
            PinSymbolToSceneOutput(get(), get())
        }
        provide<PinSymbolToSceneController> {
            PinSymbolToSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.unpinSymbolFromScene() {
        provide<UnpinSymbolFromScene> {
            UnpinSymbolFromSceneUseCase(get(), get())
        }
        provide(UnpinSymbolFromScene.OutputPort::class) {
            UnpinSymbolFromSceneOutput(get(), get())
        }
        provide<UnpinSymbolFromSceneController> {
            UnpinSymbolFromSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.detectUnusedSymbols() {
        provide<DetectUnusedSymbolsInScene> {
            DetectUnusedSymbolsInSceneUseCase(get(), get())
        }
        provide(DetectUnusedSymbolsInScene.OutputPort::class) {
            DetectUnusedSymbolsOutput()
        }
        provide<DetectUnusedSymbolsInSceneController> {
            DetectUnusedSymbolsInSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.getSceneFrame() {
        provide<GetSceneFrame> {
            GetSceneFrameUseCase(get())
        }
        provide<GetSceneFrameController> {
            GetSceneFrameControllerImpl(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.setSceneFrameValue() {
        provide<SetSceneFrameValue> {
            SetSceneFrameValueUseCase(get())
        }
        provide(SetSceneFrameValue.OutputPort::class) {
            SetSceneFrameValueOutput(get())
        }
        provide<SetSceneFrameValueController> {
            SetSceneFrameValueControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.listLocationsInScene() {
        provide<ListLocationsUsedInScene> {
            ListLocationsUsedInSceneUseCase(get())
        }
        provide<ListLocationsInSceneController> {
            ListLocationsInSceneControllerImpl(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.listLocationsToUse() {
        provide<ListAvailableLocationsToUseInScene> {
            ListAvailableLocationsToUseInSceneUseCase(get(), get())
        }
        provide<ListLocationsToUseInSceneController> {
            ListLocationsToUseInSceneControllerImpl(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.removeLocationFromScene() {
        provide<RemoveLocationFromScene> {
            RemoveLocationFromSceneUseCase(get(), get())
        }
        provide<RemoveLocationFromScene.OutputPort> {
            RemoveLocationFromSceneOutput(get(), get())
        }
        provide<RemoveLocationFromSceneController> {
            RemoveLocationFromSceneControllerImpl(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.detectInconsistenciesInScene() {
        provide<DetectInconsistenciesInSceneSettings> {
            DetectInconsistenciesInSceneSettingsUseCase(get(), get())
        }
        provide {
            DetectInconsistenciesInSceneSettingsController.invoke(applicationScope.get(), get(), get())
        }
        provide(DetectInconsistenciesInSceneSettings.OutputPort::class) {
            DetectInconsistenciesInSceneSettingsOutput(get())
        }
    }

    private fun InProjectScope.replaceSettingInScene() {
        provide<ReplaceSettingInScene> {
            ReplaceSettingInSceneUseCase(get(), get())
        }
        provide<ReplaceSettingInScene.OutputPort> {
            ReplaceSettingInSceneOutput(get(), get(), get(), get())
        }
        provide {
            ReplaceSettingInSceneController(
                applicationScope.get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.inspectCharacterInScene() {
        provide<InspectCharacterInScene> {
            InspectCharacterInSceneUseCase(get(), get(), get())
        }
        provide<InspectCharacterInSceneController> {
            InspectCharacterInSceneController.Implementation(
                applicationScope.get<ThreadTransformer>().asyncContext,
                get()
            )
        }
        scoped<SceneCharactersToolScope> {
            hoist<InspectCharacterInSceneController> { projectScope }
        }
    }
}
