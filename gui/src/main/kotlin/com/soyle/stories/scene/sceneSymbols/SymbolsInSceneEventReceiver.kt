package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.scene.trackSymbolInScene.SymbolsTrackedInSceneReceiver
import com.soyle.stories.scene.trackSymbolInScene.TrackedSymbolsRemovedReceiver
import com.soyle.stories.scene.trackSymbolInScene.TrackedSymbolsRenamedReceiver

interface SymbolsInSceneEventReceiver : TrackedSymbolsRenamedReceiver, TrackedSymbolsRemovedReceiver,
    SymbolsTrackedInSceneReceiver