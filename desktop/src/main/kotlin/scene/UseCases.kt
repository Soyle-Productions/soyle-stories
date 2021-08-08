package com.soyle.stories.desktop.config.scene

import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.editProse.ContentReplacedNotifier
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneOutput
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverArcSectionsInSceneController
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverArcSectionsInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.CoverCharacterArcSectionsInSceneOutputPort
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.ListAvailableArcSectionsToCoverInSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneController
import com.soyle.stories.scene.createNewScene.CreateNewSceneControllerImpl
import com.soyle.stories.scene.createNewScene.CreateNewSceneNotifier
import com.soyle.stories.scene.deleteScene.DeleteSceneController
import com.soyle.stories.scene.deleteScene.DeleteSceneControllerImpl
import com.soyle.stories.scene.deleteScene.DeleteSceneOutput
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneOutput
import com.soyle.stories.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneController
import com.soyle.stories.scene.charactersInScene.listCharactersInScene.ListCharactersInSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionController
import com.soyle.stories.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionControllerImpl
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneController
import com.soyle.stories.scene.listSymbolsInScene.ListSymbolsInSceneControllerImpl
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
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneOutput
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneController
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneOutput
import com.soyle.stories.scene.renameScene.RenameSceneController
import com.soyle.stories.scene.renameScene.RenameSceneControllerImpl
import com.soyle.stories.scene.renameScene.RenameSceneOutput
import com.soyle.stories.scene.reorderScene.ReorderSceneController
import com.soyle.stories.scene.reorderScene.ReorderSceneControllerImpl
import com.soyle.stories.scene.reorderScene.ReorderSceneNotifier
import com.soyle.stories.scene.sceneFrame.*
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneControllerImpl
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.locationsInScene.detectInconsistencies.DetectInconsistenciesInSceneSettingsController
import com.soyle.stories.scene.locationsInScene.detectInconsistencies.DetectInconsistenciesInSceneSettingsOutput
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneController
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneOutput
import com.soyle.stories.scene.target.TargetScene
import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.storyevent.createStoryEvent.CreateStoryEventNotifier
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInScene
import com.soyle.stories.usecase.scene.character.assignRole.AssignRoleToCharacterInSceneUseCase
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
import com.soyle.stories.usecase.scene.character.listIncluded.ListCharactersInScene
import com.soyle.stories.usecase.scene.character.listIncluded.ListCharactersInSceneUseCase
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenes
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProse
import com.soyle.stories.usecase.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionInSceneProseUseCase
import com.soyle.stories.usecase.scene.symbol.listSymbolsInScene.ListSymbolsInScene
import com.soyle.stories.usecase.scene.symbol.listSymbolsInScene.ListSymbolsInSceneUseCase
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInSceneUseCase
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInSceneUseCase
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromSceneUseCase
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInSceneUseCase
import com.soyle.stories.usecase.scene.renameScene.RenameScene
import com.soyle.stories.usecase.scene.renameScene.RenameSceneUseCase
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene
import com.soyle.stories.usecase.scene.reorderScene.ReorderSceneUseCase
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrame
import com.soyle.stories.usecase.scene.sceneFrame.GetSceneFrameUseCase
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValue
import com.soyle.stories.usecase.scene.sceneFrame.SetSceneFrameValueUseCase
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettings
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettingsUseCase
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInScene
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInSceneUseCase
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.*

object UseCases {

    init {
        scoped<ProjectScope> {
            createNewScene()
            listAllScenes()
            renameScene()
            deleteScene()
            targetScene()
            includeCharacterInScene()
            setMotivationForCharacterInScene()
            listCharactersInScene()
            linkLocationToScene()
            removeCharacterFromScene()
            reorderScene()
            coverCharacterArcSectionsInScene()
            assignRoleToCharacter()
            setCharacterDesireInScene()
            listOptionsToReplaceMention()
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
        }
    }

    private fun InProjectScope.createNewScene() {
        provide<CreateNewScene> {
            CreateNewSceneUseCase(projectId, get(), get(), get(), get())
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

        provide(CreateNewScene.OutputPort::class) {
            CreateNewSceneNotifier(applicationScope.get(), get<CreateStoryEventNotifier>())
        }
    }

    private fun InProjectScope.listAllScenes() {
        provide<ListAllScenes> {
            ListAllScenesUseCase(projectId, get())
        }
    }

    private fun InProjectScope.renameScene() {
        provide<RenameScene> {
            RenameSceneUseCase(get(), get())
        }

        provide<RenameSceneController> {
            RenameSceneControllerImpl(
                applicationScope.get(),
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
            GetPotentialChangesFromDeletingSceneUseCase(get())
        }
        provide<DeleteScene> {
            DeleteSceneUseCase(get(), get())
        }

        provide<DeleteSceneController> {
            DeleteSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(DeleteScene.OutputPort::class) {
            DeleteSceneOutput(applicationScope.get(), get(), get())
        }
    }

    private fun InProjectScope.targetScene() {
        provide<TargetScene> { TargetScene(applicationScope.get(), get()) }
    }

    private fun InProjectScope.includeCharacterInScene() {
        provide(
            IncludeCharacterInScene::class,
            ListAvailableCharactersToIncludeInScene::class
        ) {
            IncludeCharacterInSceneUseCase(get(), get(), get())
        }

        provide {
            ListAvailableCharactersToIncludeInSceneController(applicationScope.get(), get())
        }

        provide(IncludeCharacterInSceneController::class) {
            IncludeCharacterInSceneControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(IncludeCharacterInScene.OutputPort::class) {
            IncludeCharacterInSceneOutput(get(), get())
        }
    }

    private fun InProjectScope.setMotivationForCharacterInScene() {
        provide<SetMotivationForCharacterInScene> {
            SetMotivationForCharacterInSceneUseCase(get(), get())
        }

        provide<SetMotivationForCharacterInSceneController> {
            SetMotivationForCharacterInSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(SetMotivationForCharacterInScene.OutputPort::class) {
            SetMotivationForCharacterInSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.listCharactersInScene() {
        provide<ListCharactersInScene> {
            ListCharactersInSceneUseCase(get())
        }

        provide {
            ListCharactersInSceneController(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.linkLocationToScene() {
        provide<LinkLocationToScene> {
            LinkLocationToSceneUseCase(get(), get())
        }

        provide<LinkLocationToSceneController> {
            LinkLocationToSceneControllerImpl(
                applicationScope.get(),
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
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(RemoveCharacterFromScene.OutputPort::class) {
            RemoveCharacterFromSceneOutput(get())
        }
    }

    private fun InProjectScope.reorderScene() {
        provide<GetPotentialChangesFromReorderingScene> {
            GetPotentialChangesFromReorderingSceneUseCase(get())
        }
        provide<ReorderScene> {
            ReorderSceneUseCase(get())
        }

        provide<ReorderSceneController> {
            ReorderSceneControllerImpl(
                applicationScope.get(),
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide(ReorderScene.OutputPort::class) {
            ReorderSceneNotifier(applicationScope.get())
        }
    }

    private fun InProjectScope.coverCharacterArcSectionsInScene() {
        provide(
            CoverCharacterArcSectionsInScene::class,
            GetAvailableCharacterArcsForCharacterInScene::class
        ) {
            CoverCharacterArcSectionsInSceneUseCase(get(), get())
        }
        provide<ChangeCharacterArcSectionValueAndCoverInScene> {
            ChangeCharacterArcSectionValueAndCoverInSceneUseCase(get(), get(), get())
        }
        provide(
            CreateCharacterArcSectionAndCoverInScene::class,
            GetAvailableCharacterArcSectionTypesForCharacterArc::class
        ) {
            CreateCharacterArcSectionAndCoverInSceneUseCase(get(), get())
        }
        provide {
            ListAvailableArcSectionsToCoverInSceneController(applicationScope.get(), get())
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

        provide(
            CoverCharacterArcSectionsInScene.OutputPort::class,
            ChangeCharacterArcSectionValueAndCoverInScene.OutputPort::class,
            CreateCharacterArcSectionAndCoverInScene.OutputPort::class
        ) {
            CoverCharacterArcSectionsInSceneOutputPort(get(), get(), get())
        }
    }

    private fun InProjectScope.assignRoleToCharacter() {
        provide<AssignRoleToCharacterInScene> {
            AssignRoleToCharacterInSceneUseCase(get())
        }

        provide {
            AssignRoleToCharacterInSceneController(applicationScope.get(), get(), get())
        }
        provide<AssignRoleToCharacterInScene.OutputPort> {
            AssignRoleToCharacterInSceneOutput(get())
        }
    }

    private fun InProjectScope.setCharacterDesireInScene() {
        provide<SetCharacterDesireInScene> {
            SetCharacterDesireInSceneUseCase(get())
        }

        provide {
            SetCharacterDesireInSceneController(applicationScope.get(), get(), get())
        }
        provide<SetCharacterDesireInScene.OutputPort> {
            SetCharacterDesireInSceneOutput(get())
        }
    }

    private fun InProjectScope.listOptionsToReplaceMention() {
        provide<ListOptionsToReplaceMentionController> {
            ListOptionsToReplaceMentionControllerImpl(
                applicationScope.get(),
                get()
            )
        }

        provide<ListOptionsToReplaceMentionInSceneProse> {
            ListOptionsToReplaceMentionInSceneProseUseCase(get(), get(), get(), get())
        }
    }

    private fun InProjectScope.synchronizeTrackedSymbolsWithProse() {
        keepInScope {
            SynchronizeTrackedSymbolsWithProseController(get(), get()).also {
                it listensTo get<ContentReplacedNotifier>()
            }
        }
        provide<SynchronizeTrackedSymbolsWithProse> {
            SynchronizeTrackedSymbolsWithProseUseCase(get(), get(), get())
        }
        provide<SynchronizeTrackedSymbolsWithProse.OutputPort> {
            SynchronizeTrackedSymbolsWithProseOutput(get(), get())
        }
    }

    private fun InProjectScope.listSymbolsInScene()
    {
        provide<ListSymbolsInScene> {
            ListSymbolsInSceneUseCase(get(), get())
        }
        provide<ListSymbolsInSceneController> {
            ListSymbolsInSceneControllerImpl(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.listAvailableSymbolsToTrackInScene()
    {
        provide<ListAvailableSymbolsToTrackInSceneController> {
            ListAvailableSymbolsToTrackInSceneControllerImpl(applicationScope.get(), get())
        }

        provide<ListAvailableSymbolsToTrackInScene> {
            ListAvailableSymbolsToTrackInSceneUseCase(get(), get())
        }
    }

    private fun InProjectScope.pinSymbolToScene()
    {
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

    private fun InProjectScope.unpinSymbolFromScene()
    {
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

    private fun InProjectScope.detectUnusedSymbols()
    {
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
        provide<DetectInconsistenciesInSceneSettingsController> {
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
        provide<ReplaceSettingInSceneController> { ReplaceSettingInSceneController(applicationScope.get(), get(), get()) }
    }
}
