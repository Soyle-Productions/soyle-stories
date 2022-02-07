package com.soyle.stories.desktop.config.scene

import com.soyle.stories.Locale
import com.soyle.stories.common.ComponentContext
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.desktop.config.scene.Presentation.effects
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.prose.editProse.ContentReplacedNotifier
import com.soyle.stories.prose.invalidateRemovedMentions.DetectInvalidatedMentionsOutput
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptLocale
import com.soyle.stories.scene.FocusedSceneQueries
import com.soyle.stories.scene.FocusedSceneViewModel
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemLocale
import com.soyle.stories.scene.characters.remove.ConfirmRemoveCharacterFromScenePromptLocale
import com.soyle.stories.scene.characters.tool.SceneCharactersToolLocale
import com.soyle.stories.scene.characters.tool.SceneCharactersToolScope
import com.soyle.stories.scene.create.CreateScenePrompt
import com.soyle.stories.scene.create.CreateScenePromptPresenter
import com.soyle.stories.scene.create.SceneCreatedNotifier
import com.soyle.stories.scene.delete.DeleteSceneConfirmationPromptLocale
import com.soyle.stories.scene.delete.SceneDeletedNotifier
import com.soyle.stories.scene.effects.InheritedMotivationChangedLocale
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesNotifier
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneNotifier
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneNotifier
import com.soyle.stories.scene.outline.SceneOutlineComponent
import com.soyle.stories.scene.outline.SceneOutlineReportPresenter
import com.soyle.stories.scene.outline.SceneOutlineViewModel
import com.soyle.stories.scene.outline.item.OutlinedStoryEventItemComponent
import com.soyle.stories.scene.outline.remove.ConfirmRemoveStoryEventFromScenePromptLocale
import com.soyle.stories.scene.outline.remove.confirmRemoveStoryEventFromScenePrompt
import com.soyle.stories.scene.outline.remove.ramifications.UncoverStoryEventRamificationsReportLocale
import com.soyle.stories.scene.outline.remove.ramifications.uncoverStoryEventRamifications
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.reorder.ReorderSceneNotifier
import com.soyle.stories.scene.reorder.ReorderScenePromptPresenter
import com.soyle.stories.scene.reorder.ramifications.ReorderSceneRamificationsReportPresenter
import com.soyle.stories.scene.sceneEditor.SceneEditorController
import com.soyle.stories.scene.sceneEditor.SceneEditorScope
import com.soyle.stories.scene.sceneEditor.SceneEditorState
import com.soyle.stories.scene.sceneEditor.SceneEditorViewListener
import com.soyle.stories.scene.sceneFrame.GetSceneFrameController
import com.soyle.stories.scene.sceneFrame.SetSceneFrameValueController
import com.soyle.stories.scene.sceneList.SceneListController
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.sceneList.SceneListPresenter
import com.soyle.stories.scene.sceneList.SceneListViewListener
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneController
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneState
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneViewListener
import com.soyle.stories.scene.setting.SceneSettingToolLocale
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeNotifier
import kotlin.coroutines.CoroutineContext

object Presentation {

    init {
        scoped<ProjectScope> {
            createScenePrompt()
            reorderScenePrompt()
            deleteScenePrompt()

            sceneList()
            sceneSetting()
            symbolsInScene()
            charactersInScene()
            sceneOutline()

            effects()

            reorderSceneRamifications()
            deleteSceneRamifications()
        }
        sceneEditor()

    }

    private fun InProjectScope.createScenePrompt() {
        provide(CreateScenePrompt::class) { CreateScenePromptPresenter(get<WorkBench>()::currentStage) }
    }

    private fun InProjectScope.reorderScenePrompt() {
        provide { ReorderScenePromptPresenter() }
    }

    private fun InProjectScope.deleteScenePrompt() {
        provide< DeleteSceneConfirmationPromptLocale> { applicationScope.get<Locale>().scenes.remove.prompt }
    }

    private fun InScope<ProjectScope>.sceneList() {
        provide<SceneListViewListener> {
            SceneListController(
                applicationScope.get(),
                Project.Id(projectId),
                get(),
                SceneListPresenter(
                    get<SceneListModel>(),
                    get<SceneCreatedNotifier>(),
                    get<SceneRenamedNotifier>(),
                    get<SceneDeletedNotifier>(),
                    get<ReorderSceneNotifier>(),
                    get<DetectInvalidatedMentionsOutput>(),
                    get<DetectUnusedSymbolsOutput>(),
                    get<SceneInconsistenciesNotifier>()
                ),
                get(),
                get(),
                get(),
                get()
            )
        }
    }

    private fun InProjectScope.sceneSetting() {
        provide<SceneSettingToolRoot.Factory> {
            object : SceneSettingToolRoot.Factory {
                override fun invoke(initialScene: Pair<Scene.Id, String>?): SceneSettingToolRoot = SceneSettingToolRoot(
                    initialScene,
                    applicationScope.get<Locale>().scenes.locations,
                    get<SceneRenamedNotifier>(),
                    get<SceneDeletedNotifier>(),
                    get(),
                    get()
                )
            }
        }
        provide<SceneSettingItemList.Factory> {
            object : SceneSettingItemList.Factory {
                override fun invoke(sceneId: Scene.Id): SceneSettingItemList = SceneSettingItemList(
                    sceneId,
                    applicationScope.get<Locale>().scenes.locations.list,
                    get(),
                    get(),
                    get<LocationRemovedFromSceneNotifier>(),
                    get<LocationUsedInSceneNotifier>(),
                    get<DeletedLocationNotifier>(),
                    get(),
                    get()
                )
            }
        }
        provide<SceneSettingItemView.Factory> {
            object : SceneSettingItemView.Factory {
                override fun invoke(model: SceneSettingItemModel): SceneSettingItemView = SceneSettingItemView(
                    model,
                    applicationScope.get<Locale>().scenes.locations.list.item,
                    get(),
                    get(),
                    get(),
                    get<SceneSettingLocationRenamedNotifier>(),
                    get<SceneInconsistenciesNotifier>()
                )
            }
        }
        provide<UseLocationButton.Factory> {
            object : UseLocationButton.Factory {
                override fun invoke(sceneId: Scene.Id): UseLocationButton = UseLocationButton(
                    sceneId,
                    applicationScope.get<Locale>().scenes.locations.list.useLocation,
                    get(),
                    get(),
                    get()
                )
            }
        }
        provide<SceneSettingToolLocale> {
            applicationScope.get<Locale>().scenes.locations
        }
    }

    private fun InProjectScope.symbolsInScene() {
        provide<SymbolsInSceneViewListener> {
            SymbolsInSceneController(
                get<SymbolsInSceneState>(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            ).also {
                it listensTo get<SymbolsTrackedInSceneNotifier>()
                it listensTo get<TrackedSymbolsRenamedNotifier>()
                it listensTo get<TrackedSymbolsRemovedNotifier>()
                it listensTo get<RenamedThemeNotifier>()
                it listensTo get<SymbolPinnedToSceneNotifier>()
                it listensTo get<SymbolUnpinnedFromSceneNotifier>()
                it listensTo get<ContentReplacedNotifier>()
                it listensTo get<DetectUnusedSymbolsOutput>()
            }
        }
    }

    private fun InProjectScope.charactersInScene() {
        provide(FocusedSceneQueries::class) { FocusedSceneViewModel(this) }
        provide<ComponentContext> {
            object : ComponentContext {
                override val gui: CoroutineContext = applicationScope.get<ThreadTransformer>().guiContext
                override val projectScope: ProjectScope = this@provide
            }
        }
        provide<SceneCharactersToolLocale> { applicationScope.get<Locale>().scenes.characters.tool }
        provide<CharacterInSceneItemLocale> { applicationScope.get<Locale>().scenes.characters.list.item }

        provide<ConfirmRemoveCharacterFromScenePromptLocale> {
            applicationScope.get<Locale>().scenes.characters.remove
        }
        provide<ConfirmationPromptLocale> {
            applicationScope.get<Locale>().ramifications.confirmation
        }

        scoped<SceneCharactersToolScope> {
            hoist<Locale> { projectScope.applicationScope }
            provide<SceneCharactersToolLocale> { get<Locale>().scenes.characters.tool }
            provide<CharacterInSceneItemLocale> { get<Locale>().scenes.characters.list.item }
            hoist<FocusedSceneQueries> { projectScope }
            hoist<ConfirmRemoveCharacterFromScenePromptLocale> { projectScope }
            hoist<ConfirmationPromptLocale> { projectScope }
        }

    }

    private fun InProjectScope.effects() {
        provide<InheritedMotivationChangedLocale> { applicationScope.get<Locale>().scenes.effects }
    }

    private fun InProjectScope.reorderSceneRamifications() {
        provide { ReorderSceneRamificationsReportPresenter(get(), this) }
    }

    private fun InProjectScope.deleteSceneRamifications() {
//        provide { DeleteSceneRamificationsReportPresenter(get(), this) }
    }

    private fun InProjectScope.sceneOutline() {
        provide { SceneOutlineViewModel() }
        provide { SceneOutlineReportPresenter(this, get(), get()) }
        provide<SceneOutlineComponent> {
            SceneOutlineComponent.Implementation(
                this,
                applicationScope.get<ThreadTransformer>().guiContext,
                get(),
                get()
            )
        }
        provide<SceneOutlineComponent.Gui> {
            object : SceneOutlineComponent.Gui,
                OutlinedStoryEventItemComponent by get<OutlinedStoryEventItemComponent>() {}
        }
        provide<OutlinedStoryEventItemComponent> {
            OutlinedStoryEventItemComponent.Implementation(
                { confirmRemoveStoryEventFromScenePrompt(this, get<WorkBench>().currentStage) },
                { uncoverStoryEventRamifications(this, it) },
                get()
            )
        }
        provide<ConfirmRemoveStoryEventFromScenePromptLocale> {
            applicationScope.get<Locale>().scenes.storyEvents.remove.prompt
        }
        provide<UncoverStoryEventRamificationsReportLocale> {
            applicationScope.get<Locale>().scenes.storyEvents.remove.ramifications
        }
    }

    private fun sceneEditor() {
        scoped<SceneEditorScope> {
            provide<SceneEditorViewListener> {
                SceneEditorController(
                    sceneId,
                    object : SceneEditorController.Dependencies {
                        override val getSceneFrameController: GetSceneFrameController
                            get() = projectScope.get()
                        override val getStoryElementsToMentionController: GetStoryElementsToMentionController
                            get() = projectScope.get()
                        override val linkLocationToSceneController: LinkLocationToSceneController
                            get() = projectScope.get()
                        override val setSceneFrameValueController: SetSceneFrameValueController
                            get() = projectScope.get()
                    },
                    get<SceneEditorState>()
                )
            }
        }
    }

}