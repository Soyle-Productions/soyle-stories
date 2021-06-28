package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

data class SymbolPinnedToScene(override val sceneId: Scene.Id, val trackedSymbol: Scene.TrackedSymbol) : SceneEvent()