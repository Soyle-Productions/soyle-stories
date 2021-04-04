package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

data class SymbolUnpinnedFromScene(override val sceneId: Scene.Id, val trackedSymbol: Scene.TrackedSymbol) :
    SceneEvent()