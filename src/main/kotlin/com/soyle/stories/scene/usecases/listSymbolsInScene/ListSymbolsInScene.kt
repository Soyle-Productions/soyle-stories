package com.soyle.stories.scene.usecases.listSymbolsInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol

interface ListSymbolsInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(symbols: List<SymbolInScene>) : List<SymbolInScene> by symbols

    class SymbolInScene(val themeId: Theme.Id, val themeName: String, val symbolId: Symbol.Id, val symbolName: String)

    interface OutputPort {
        suspend fun receiveSymbolsInSceneList(response: ResponseModel)
    }

}