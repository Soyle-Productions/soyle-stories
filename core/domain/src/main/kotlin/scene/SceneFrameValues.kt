package com.soyle.stories.domain.scene

sealed class SceneFrameValue

data class SceneConflict(val value: String) : SceneFrameValue()
data class SceneResolution(val value: String) : SceneFrameValue()