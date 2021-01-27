package com.soyle.stories.theme.usecases.renameSymbol

import com.soyle.stories.entities.SceneEvent
import com.soyle.stories.entities.SceneUpdate
import com.soyle.stories.entities.Single
import com.soyle.stories.entities.TrackedSymbolRenamed
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.theme.SymbolAlreadyHasName
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.validateSymbolName
import java.util.*

class RenameSymbolUseCase(
    private val themeRepository: ThemeRepository,
    private val sceneRepository: SceneRepository
) : RenameSymbol {

    override suspend fun invoke(symbolId: UUID, name: String, output: RenameSymbol.OutputPort) {
        validateSymbolName(name)
        val theme = getThemeContainingSymbol(symbolId)
        val symbol = theme.symbols.find { it.id.uuid == symbolId }!!
        if (symbol.name == name) throw SymbolAlreadyHasName(symbolId, name)
        val renamedSymbol = symbol.withName(name)

        val sceneUpdates = updatedTrackedSymbolsInScenes(renamedSymbol)

        themeRepository.updateTheme(theme.withoutSymbol(symbol.id).withSymbol(renamedSymbol))
        output.symbolRenamed(
            RenameSymbol.ResponseModel(
                RenamedSymbol(theme.id.uuid, symbolId, name),
                sceneUpdates.mapNotNull { (it as? Single<*>)?.event as? TrackedSymbolRenamed }
            )
        )
    }

    private suspend fun updatedTrackedSymbolsInScenes(
        renamedSymbol: Symbol
    ): List<SceneUpdate<SceneEvent>> {
        val sceneUpdates = sceneRepository.getScenesTrackingSymbol(renamedSymbol.id)
            .map { it.withSymbolTracked(renamedSymbol) }
        if (sceneUpdates.isNotEmpty()) {
            sceneRepository.updateScenes(sceneUpdates.map { it.scene })
        }
        return sceneUpdates
    }

    private suspend fun getThemeContainingSymbol(symbolId: UUID) =
        (themeRepository.getThemeContainingSymbolWithId(Symbol.Id(symbolId))
            ?: throw SymbolDoesNotExist(symbolId))

}