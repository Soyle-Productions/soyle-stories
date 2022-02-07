package com.soyle.stories.usecase.theme.removeSymbolFromTheme

import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.theme.SymbolDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RemoveSymbolFromThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val sceneRepository: SceneRepository
) : RemoveSymbolFromTheme {

    override suspend fun invoke(symbolId: UUID, output: RemoveSymbolFromTheme.OutputPort) {
        val symbolRemovedFromTheme = removeSymbolFromTheme(symbolId)
        val sceneUpdates = removeSymbolFromScenes(Symbol.Id(symbolRemovedFromTheme.symbolId))

        output.removedSymbolFromTheme(
            RemoveSymbolFromTheme.ResponseModel(
                symbolRemovedFromTheme,
                sceneUpdates.mapNotNull { (it as? Successful<*>)?.event as? TrackedSymbolRemoved }
            )
        )
    }

    private suspend fun removeSymbolFromTheme(symbolId: UUID): SymbolRemovedFromTheme
    {
        val theme = getThemeWithSymbol(symbolId)
        val symbol = theme.symbols.find { it.id.uuid == symbolId }!!
        val updatedTheme = theme.withoutSymbol(symbol.id)
        themeRepository.updateTheme(updatedTheme)
        return SymbolRemovedFromTheme(theme.id.uuid, symbol.id.uuid, symbol.name)
    }

    private suspend fun getThemeWithSymbol(symbolId: UUID) =
        (themeRepository.getThemeContainingSymbolWithId(Symbol.Id(symbolId))
            ?: throw SymbolDoesNotExist(symbolId))

    private suspend fun removeSymbolFromScenes(symbolId: Symbol.Id): List<SceneUpdate<TrackedSymbolRemoved>> {
        val sceneUpdates = sceneRepository.getScenesTrackingSymbol(symbolId).map { it.withoutSymbolTracked(symbolId) }
        sceneRepository.updateScenes(sceneUpdates.map { it.scene })
        return sceneUpdates
    }

}