package com.soyle.stories.theme.usecases.renameSymbol

import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.prose.MentionTextReplaced
import com.soyle.stories.prose.repositories.ProseRepository
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.theme.SymbolAlreadyHasName
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.validateSymbolName
import java.util.*

class RenameSymbolUseCase(
    private val themeRepository: ThemeRepository,
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository
) : RenameSymbol {

    override suspend fun invoke(symbolId: UUID, name: String, output: RenameSymbol.OutputPort) {
        validateSymbolName(name)
        val theme = getThemeContainingSymbol(symbolId)
        val symbol = theme.symbols.find { it.id.uuid == symbolId }!!
        if (symbol.name == name) throw SymbolAlreadyHasName(symbolId, name)
        val renamedSymbol = symbol.withName(name)

        val sceneUpdates = updatedTrackedSymbolsInScenes(renamedSymbol)
        val proseUpdates = replaceProseMentionText(theme.id, symbol, name)

        themeRepository.updateTheme(theme.withoutSymbol(symbol.id).withSymbol(renamedSymbol))
        output.symbolRenamed(
            RenameSymbol.ResponseModel(
                RenamedSymbol(theme.id.uuid, symbolId, name),
                sceneUpdates.mapNotNull { (it as? Updated<*>)?.event as? TrackedSymbolRenamed },
                proseUpdates
            )
        )
    }

    private suspend fun updatedTrackedSymbolsInScenes(
        renamedSymbol: Symbol
    ): List<SceneUpdate<SceneEvent>> {
        val sceneUpdates = sceneRepository.getScenesTrackingSymbol(renamedSymbol.id)
            .map { it.withSymbolRenamed(renamedSymbol.id, renamedSymbol.name) }
        if (sceneUpdates.isNotEmpty()) {
            sceneRepository.updateScenes(sceneUpdates.map { it.scene })
        }
        return sceneUpdates
    }

    private suspend fun getThemeContainingSymbol(symbolId: UUID) =
        (themeRepository.getThemeContainingSymbolWithId(Symbol.Id(symbolId))
            ?: throw SymbolDoesNotExist(symbolId))


    private suspend fun replaceProseMentionText(themeId: Theme.Id, symbol: Symbol, name: String): List<MentionTextReplaced> {
        val entityId = symbol.id.mentioned(themeId)
        val updates = proseRepository.getProseThatMentionEntity(entityId)
            .map {
                it.withMentionTextReplaced(entityId, name)
            }
        proseRepository.replaceProse(updates.map { it.prose })
        return updates.mapNotNull { it.event }
    }
}