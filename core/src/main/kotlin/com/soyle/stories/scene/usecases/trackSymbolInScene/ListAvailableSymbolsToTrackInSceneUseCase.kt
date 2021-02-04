package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.SymbolItem
import com.soyle.stories.theme.usecases.ThemeItem
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme

class ListAvailableSymbolsToTrackInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val themeRepository: ThemeRepository
) : ListAvailableSymbolsToTrackInScene {

    override suspend fun invoke(sceneId: Scene.Id, output: ListAvailableSymbolsToTrackInScene.OutputPort) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        output.receiveAvailableSymbolsToTrackInScene(
            SymbolsByTheme(
                themeRepository.listThemesInProject(scene.projectId)
                    .asSequence()
                    .map { theme ->
                        val themeItem = ThemeItem(theme.id.uuid, theme.name)
                        val symbolItems = theme.symbols
                            .asSequence()
                            .filterNot { scene.trackedSymbols.isSymbolTracked(it.id) }
                            .map { SymbolItem(it.id.uuid, it.name) }
                            .toList()
                        themeItem to symbolItems
                    }
                    .filter {
                        it.second.isNotEmpty()
                    }
                    .toList()
            )
        )
    }
}