package com.soyle.stories.usecase.scene.listSymbolsInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Symbol

interface ListSymbolsInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(symbols: List<SymbolInScene>) : List<SymbolInScene> by symbols

    class SymbolInScene(val themeId: Theme.Id, val themeName: String, val symbolId: Symbol.Id, val symbolName: String, val isPinned: Boolean)

    interface OutputPort {
        suspend fun receiveSymbolsInSceneList(response: ResponseModel)
    }

}