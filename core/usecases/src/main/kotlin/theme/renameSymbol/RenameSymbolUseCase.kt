package com.soyle.stories.usecase.theme.renameSymbol

import com.soyle.stories.domain.prose.MentionTextReplaced
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.SceneEvent
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.TrackedSymbolRenamed
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.theme.SymbolAlreadyHasName
import com.soyle.stories.usecase.theme.SymbolDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class RenameSymbolUseCase(
    private val themeRepository: ThemeRepository,
    private val sceneRepository: SceneRepository,
    private val proseRepository: ProseRepository
) : RenameSymbol {

    override suspend fun invoke(symbolId: UUID, name: NonBlankString, output: RenameSymbol.OutputPort) {
        val theme = getThemeContainingSymbol(symbolId)
        val symbol = theme.symbols.find { it.id.uuid == symbolId }!!
        if (symbol.name == name.value) throw SymbolAlreadyHasName(symbolId, name.value)
        val renamedSymbol = symbol.withName(name.value)

        val sceneUpdates = updatedTrackedSymbolsInScenes(renamedSymbol)
        val proseUpdates = replaceProseMentionText(theme.id, symbol, name.value)

        themeRepository.updateTheme(theme.withoutSymbol(symbol.id).withSymbol(renamedSymbol))
        output.symbolRenamed(
            RenameSymbol.ResponseModel(
                RenamedSymbol(theme.id.uuid, symbolId, name.value),
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