package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.gui.View
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.DetectUnusedSymbolsInScene
import com.soyle.stories.scene.usecases.trackSymbolInScene.ListAvailableSymbolsToTrackInScene
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme

class SymbolsInScenePresenter(
    internal val view: View.Nullable<SymbolsInSceneViewModel>
) : ListSymbolsInScene.OutputPort, ListAvailableSymbolsToTrackInScene.OutputPort, SymbolsInSceneEventReceiver {

    override suspend fun receiveSymbolsInSceneList(response: ListSymbolsInScene.ResponseModel) {
        view.update {
            SymbolsInSceneViewModel(
                targetScene = this?.targetScene,
                themesInScene = response.groupBy { it.themeId }.map { (themeId, symbols) ->
                    SymbolsInSceneViewModel.ThemeInScene(
                        themeId,
                        symbols.first().themeName,
                        symbolsInScene = symbols.map {
                            SymbolsInSceneViewModel.SymbolInScene(
                                it.symbolId,
                                it.symbolName,
                                it.isPinned,
                                false
                            )
                        }
                    )
                },
                availableThemesToTrack = listOf()
            )
        }
    }

    override suspend fun receiveSymbolsTrackedInScene(symbolsTrackedInScene: List<SymbolTrackedInScene>) {
        val newSymbolsInSceneByThemeId = symbolsTrackedInScene.groupBy { it.trackedSymbol.themeId }
        view.updateOrInvalidated {
            val existingThemesInScene = themesInScene.map { it.themeId }.toSet()
            val newThemesInScene = newSymbolsInSceneByThemeId - existingThemesInScene
            copy(
                themesInScene = themesInScene.map {
                    val newSymbolsInScene = newSymbolsInSceneByThemeId.getOrDefault(it.themeId, listOf())
                    if (newSymbolsInScene.isNotEmpty()) {
                        it.copy(
                            symbolsInScene = it.symbolsInScene + newSymbolsInScene.map {
                                SymbolsInSceneViewModel.SymbolInScene(
                                    it.trackedSymbol.symbolId,
                                    it.trackedSymbol.symbolName,
                                    it.trackedSymbol.isPinned,
                                    false
                                )
                            }
                        )
                    } else it
                } + newThemesInScene.map { (themeId, symbols) ->
                    SymbolsInSceneViewModel.ThemeInScene(
                        themeId,
                        symbols.first().themeName,
                        symbolsInScene = symbols.map {
                            SymbolsInSceneViewModel.SymbolInScene(
                                it.trackedSymbol.symbolId,
                                it.trackedSymbol.symbolName,
                                it.trackedSymbol.isPinned,
                                false
                            )
                        }
                    )
                }
            )
        }
    }

    override suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>) {
        val affectedThemeIds = trackedSymbolsRenamed.map { it.trackedSymbol.themeId }.toSet()
        val newNamesBySymbolId =
            trackedSymbolsRenamed.associate { it.trackedSymbol.symbolId to it.trackedSymbol.symbolName }
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.map {
                    if (it.themeId in affectedThemeIds) {
                        it.copy(
                            symbolsInScene = it.symbolsInScene.map {
                                if (it.symbolId in newNamesBySymbolId) {
                                    SymbolsInSceneViewModel.SymbolInScene(
                                        it.symbolId,
                                        newNamesBySymbolId.getValue(it.symbolId),
                                        it.isPinned,
                                        false
                                    )
                                } else it
                            }
                        )
                    } else it
                }
            )
        }
    }

    override suspend fun receiveSymbolPinnedToScene(symbolPinnedToScene: SymbolPinnedToScene) {
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.map {
                    if (it.themeId == symbolPinnedToScene.trackedSymbol.themeId) {
                        it.copy(
                            symbolsInScene = it.symbolsInScene.map {
                                if (it.symbolId == symbolPinnedToScene.trackedSymbol.symbolId) {
                                    SymbolsInSceneViewModel.SymbolInScene(
                                        it.symbolId,
                                        it.symbolName,
                                        true,
                                        false
                                    )
                                } else it
                            }
                        )
                    } else it
                }
            )
        }
    }

    override suspend fun receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene: SymbolUnpinnedFromScene) {
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.map {
                    if (it.themeId == symbolUnpinnedFromScene.trackedSymbol.themeId) {
                        it.copy(
                            symbolsInScene = it.symbolsInScene.map {
                                if (it.symbolId == symbolUnpinnedFromScene.trackedSymbol.symbolId) {
                                    SymbolsInSceneViewModel.SymbolInScene(
                                        it.symbolId,
                                        it.symbolName,
                                        false,
                                        false
                                    )
                                } else it
                            }
                        )
                    } else it
                }
            )
        }
    }

    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.map {
                    if (it.themeId.uuid == renamedTheme.themeId) {
                        it.copy(
                            themeName = renamedTheme.newName
                        )
                    } else it
                }
            )
        }
    }

    override suspend fun receiveTrackedSymbolsRemoved(trackedSymbolsRemoved: List<TrackedSymbolRemoved>) {
        val affectedThemeIds = trackedSymbolsRemoved.map { it.trackedSymbol.themeId }.toSet()
        val removedSymbolIds = trackedSymbolsRemoved.map { it.trackedSymbol.symbolId }.toSet()
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.asSequence().map {
                    if (it.themeId in affectedThemeIds) {
                        it.copy(
                            symbolsInScene = it.symbolsInScene.filterNot { it.symbolId in removedSymbolIds }
                        )
                    } else it
                }.filterNot { it.symbolsInScene.isEmpty() }.toList()
            )
        }
    }

    override suspend fun receiveAvailableSymbolsToTrackInScene(response: SymbolsByTheme) {
        view.updateOrInvalidated {
            copy(
                availableThemesToTrack = response.themes.map { (themeItem, symbolItems) ->
                    SymbolsInSceneViewModel.AvailableTheme(
                        Theme.Id(themeItem.themeId),
                        themeItem.themeName,
                        symbolItems.map {
                            SymbolsInSceneViewModel.AvailableSymbol(Symbol.Id(it.symbolId), it.symbolName)
                        })
                }
            )
        }
    }

    override suspend fun receiveDetectedUnusedSymbols(response: DetectUnusedSymbolsInScene.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.map {
                    it.copy(
                        symbolsInScene = it.symbolsInScene.map {
                            it.copy(
                                isUnused = it.symbolId in response.unusedSymbolIds
                            )
                        }
                    )
                }
            )
        }
    }
}