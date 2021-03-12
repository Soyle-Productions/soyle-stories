package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

data class SymbolTrackedInScene(
    override val sceneId: Scene.Id,
    val themeName: String,
    val trackedSymbol: Scene.TrackedSymbol
) : SceneEvent()