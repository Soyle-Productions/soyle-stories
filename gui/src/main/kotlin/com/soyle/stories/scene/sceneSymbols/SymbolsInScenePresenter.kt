package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.SymbolTrackedInScene
import com.soyle.stories.entities.TrackedSymbolRemoved
import com.soyle.stories.entities.TrackedSymbolRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInScene
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

class SymbolsInScenePresenter(
    internal val view: View.Nullable<SymbolsInSceneViewModel>
) : ListSymbolsInScene.OutputPort, SymbolsInSceneEventReceiver {

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
                                it.symbolName
                            )
                        }
                    )
                }
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
                                    it.trackedSymbol.symbolName
                                )
                            }
                        )
                    }
                    else it
                } + newThemesInScene.map { (themeId, symbols) ->
                    SymbolsInSceneViewModel.ThemeInScene(
                        themeId,
                        symbols.first().themeName,
                        symbolsInScene = symbols.map {
                            SymbolsInSceneViewModel.SymbolInScene(
                                it.trackedSymbol.symbolId,
                                it.trackedSymbol.symbolName
                            )
                        }
                    )
                }
            )
        }
    }

    override suspend fun receiveTrackedSymbolsRenamed(trackedSymbolsRenamed: List<TrackedSymbolRenamed>) {
        val affectedThemeIds = trackedSymbolsRenamed.map { it.trackedSymbol.themeId }.toSet()
        val newNamesBySymbolId = trackedSymbolsRenamed.associate { it.trackedSymbol.symbolId to it.trackedSymbol.symbolName }
        view.updateOrInvalidated {
            copy(
                themesInScene = themesInScene.map {
                    if (it.themeId in affectedThemeIds) {
                        it.copy(
                            symbolsInScene = it.symbolsInScene.map {
                                if (it.symbolId in newNamesBySymbolId) {
                                    SymbolsInSceneViewModel.SymbolInScene(it.symbolId, newNamesBySymbolId.getValue(it.symbolId))
                                } else it
                            }
                        )
                    }
                    else it
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
                    }
                    else it
                }.filterNot { it.symbolsInScene.isEmpty() }.toList()
            )
        }
    }
}