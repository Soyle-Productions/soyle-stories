package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.scene.trackSymbolInScene.*
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.DetectUnusedSymbolsInScene
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver

interface SymbolsInSceneEventReceiver : TrackedSymbolsRenamedReceiver, TrackedSymbolsRemovedReceiver,
    SymbolsTrackedInSceneReceiver, RenamedThemeReceiver, SymbolPinnedToSceneReceiver, SymbolUnpinnedFromSceneReceiver,
    DetectUnusedSymbolsInScene.OutputPort