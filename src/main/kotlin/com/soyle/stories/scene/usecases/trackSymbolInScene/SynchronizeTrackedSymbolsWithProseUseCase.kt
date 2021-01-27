package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.*
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.prose.repositories.getProseOrError
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.theme.repositories.ThemeRepository

class SynchronizeTrackedSymbolsWithProseUseCase(
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository,
    private val themeRepository: ThemeRepository
) : SynchronizeTrackedSymbolsWithProse {

    override suspend fun invoke(proseId: Prose.Id, output: SynchronizeTrackedSymbolsWithProse.OutputPort) {
        val scene = sceneRepository.getSceneThatOwnsProse(proseId) ?: return
        val prose = proseRepository.getProseOrError(proseId)
        val symbolMentions = prose.mentions.mapNotNull { it.entityId as? MentionedSymbolId }
        val symbolIds = symbolMentions.map { it.id }.toSet()
        val themes = themeRepository.getThemesById(symbolMentions.map { it.themeId }.toSet())
        val symbols = themes.flatMap { theme -> theme.symbols.filter { it.id in symbolIds } }
        val events = mutableListOf<SceneEvent>()
        val sceneWithSymbols = symbols.fold(scene) { nextScene, symbol ->
            val update = nextScene.withSymbolTracked(symbol)
            when (update) {
                is NoUpdate -> {
                }
                is Single -> events.add(update.event)
                is Multi<*> -> events.addAll(update.events)
            }
            update.scene
        }
        val sceneWithoutSymbols = sceneWithSymbols.trackedSymbols.fold(sceneWithSymbols) { nextScene, trackedSymbol ->
            if (trackedSymbol.symbolId !in symbolIds) {
                val update = nextScene.withoutSymbolTracked(trackedSymbol.symbolId)
                when (update) {
                    is NoUpdate -> {
                    }
                    is Single -> events.add(update.event)
                    is Multi<*> -> events.addAll(update.events)
                }
                update.scene
            } else nextScene
        }

        if (events.isNotEmpty()) {
            sceneRepository.updateScene(sceneWithoutSymbols)
            output.symbolTrackedInScene(
                SynchronizeTrackedSymbolsWithProse.ResponseModel(
                    events.filterIsInstance<SymbolTrackedInScene>(),
                    events.filterIsInstance<TrackedSymbolRemoved>(),
                )
            )
        }
    }
}