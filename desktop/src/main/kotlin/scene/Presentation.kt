package com.soyle.stories.desktop.config.scene

import com.soyle.stories.character.createArcSection.CreateArcSectionController
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.desktop.config.locale.LocaleHolder
import com.soyle.stories.di.InScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.editProse.ContentReplacedNotifier
import com.soyle.stories.prose.invalidateRemovedMentions.DetectInvalidatedMentionsOutput
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneNotifier
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.assignRole.AssignRoleToCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedNotifier
import com.soyle.stories.scene.charactersInScene.assignRole.CharacterRoleInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene.*
import com.soyle.stories.scene.create.CreateNewSceneOutput
import com.soyle.stories.scene.delete.DeleteScenePromptViewModel
import com.soyle.stories.scene.deleteSceneRamifications.*
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludeCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneNotifier
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.scene.charactersInScene.listAvailableCharacters.ListAvailableCharactersToIncludeInSceneController
import com.soyle.stories.scene.charactersInScene.listCharactersInScene.ListCharactersInSceneController
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromSceneController
import com.soyle.stories.scene.listOptionsToReplaceMention.ListOptionsToReplaceMentionController
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LinkLocationToSceneController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneNotifier
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneNotifier
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneNotifier
import com.soyle.stories.scene.charactersInScene.removeCharacterFromScene.RemovedCharacterFromSceneReceiver
import com.soyle.stories.scene.charactersInScene.setDesire.CharacterDesireInSceneChangedNotifier
import com.soyle.stories.scene.charactersInScene.setDesire.CharacterDesireInSceneChangedReceiver
import com.soyle.stories.scene.charactersInScene.setDesire.SetCharacterDesireInSceneController
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneController
import com.soyle.stories.scene.reorder.ReorderSceneNotifier
import com.soyle.stories.scene.reorderSceneRamifications.*
import com.soyle.stories.scene.sceneCharacters.SceneCharactersController
import com.soyle.stories.scene.sceneCharacters.SceneCharactersViewListener
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
import com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneNotifier
import com.soyle.stories.scene.create.SceneCreatedNotifier
import com.soyle.stories.scene.delete.SceneDeletedNotifier
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesNotifier
import com.soyle.stories.scene.outline.SceneOutlineComponent
import com.soyle.stories.scene.outline.SceneOutlinePrompt
import com.soyle.stories.scene.outline.SceneOutlinePromptPresenter
import com.soyle.stories.scene.outline.SceneOutlineViewModel
import com.soyle.stories.scene.renameScene.SceneRenamedNotifier
import com.soyle.stories.scene.sceneCharacters.SceneCharactersState
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import com.soyle.stories.scene.target.SceneTargetedNotifier
import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeNotifier

object Presentation {

    init {
        scoped<ProjectScope> {
            sceneList()
            sceneSetting()
            symbolsInScene()
            charactersInScene()
            sceneOutline()
        }
        sceneEditor()

    }

    private fun InScope<ProjectScope>.sceneList() {
        provide<SceneListViewListener> {
            SceneListController(
                applicationScope.get(),
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
                    applicationScope.get<LocaleHolder>(),
                    get<SceneRenamedNotifier>(),
                    get<SceneDeletedNotifier>(),
                    get<SceneTargetedNotifier>(),
                    get()
                )
            }
        }
        provide<SceneSettingItemList.Factory> {
            object :  SceneSettingItemList.Factory {
                override fun invoke(sceneId: Scene.Id): SceneSettingItemList = SceneSettingItemList(
                    sceneId,
                    applicationScope.get<LocaleHolder>(),
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
                    applicationScope.get<LocaleHolder>(),
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
                    applicationScope.get<LocaleHolder>(),
                    get(),
                    get(),
                    get()
                )
            }
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
        provide<SceneCharactersViewListener> {
            SceneCharactersController(
                object : SceneCharactersController.Dependencies {
                    override val listCharactersInSceneController: ListCharactersInSceneController
                        get() = get()
                    override val listAvailableCharactersToIncludeInSceneController: ListAvailableCharactersToIncludeInSceneController
                        get() = get()
                    override val includeCharacterInSceneController: IncludeCharacterInSceneController
                        get() = get()
                    override val listAvailableArcSectionsToCoverInSceneController: ListAvailableArcSectionsToCoverInSceneController
                        get() = get()
                    override val coverArcSectionsInSceneController: CoverArcSectionsInSceneController
                        get() = get()
                    override val createArcSectionController: CreateArcSectionController
                        get() = get()
                    override val removeCharacterFromSceneController: RemoveCharacterFromSceneController
                        get() = get()
                    override val setMotivationForCharacterInSceneController: SetMotivationForCharacterInSceneController
                        get() = get()
                    override val assignRoleToCharacterInSceneController: AssignRoleToCharacterInSceneController
                        get() = get()
                    override val setCharacterDesireInSceneController: SetCharacterDesireInSceneController
                        get() = get()

                    override val includedCharacterInSceneNotifier: Notifier<IncludedCharacterInSceneReceiver>
                        get() = get<IncludedCharacterInSceneNotifier>()
                    override val removedCharacterFromSceneNotifier: Notifier<RemovedCharacterFromSceneReceiver>
                        get() = get<RemovedCharacterFromSceneNotifier>()
                    override val renamedCharacterInSceneNotifier: Notifier<RenamedCharacterInSceneReceiver>
                        get() = get<RenamedCharacterInSceneNotifier>()
                    override val characterArcSectionsCoveredBySceneNotifier: Notifier<CharacterArcSectionsCoveredBySceneReceiver>
                        get() = get<CharacterArcSectionsCoveredBySceneNotifier>()
                    override val characterArcSectionUncoveredInSceneNotifier: Notifier<CharacterArcSectionUncoveredInSceneReceiver>
                        get() = get<CharacterArcSectionUncoveredInSceneNotifier>()
                    override val characterRoleInSceneChangedNotifier: Notifier<CharacterRoleInSceneChangedReceiver>
                        get() = get<CharacterRoleInSceneChangedNotifier>()
                    override val characterDesireInSceneChangedNotifier: Notifier<CharacterDesireInSceneChangedReceiver>
                        get() = get<CharacterDesireInSceneChangedNotifier>()
                },
                get<SceneCharactersState>()
            )
        }
    }

    private fun InProjectScope.sceneOutline() {
        provide { SceneOutlineViewModel() }
        provide<SceneOutlinePrompt> { SceneOutlinePromptPresenter(this, get()) }
        provide<SceneOutlineComponent> { SceneOutlineComponent.Implementation(this, get()) }
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
                        override val includeCharacterInSceneController: IncludeCharacterInSceneController
                            get() = projectScope.get()
                        override val linkLocationToSceneController: LinkLocationToSceneController
                            get() = projectScope.get()
                        override val listOptionsToReplaceMentionController: ListOptionsToReplaceMentionController
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