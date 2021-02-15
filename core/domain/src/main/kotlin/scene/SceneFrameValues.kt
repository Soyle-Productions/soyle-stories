package com.soyle.stories.domain.scene

sealed class SceneFrameValue

data class SceneConflict(val value: String) : SceneFrameValue() {
    override fun toString(): String = "SceneConflict($value)"
}
data class SceneResolution(val value: String) : SceneFrameValue() {
    override fun toString(): String = "SceneResolution($value)"
}