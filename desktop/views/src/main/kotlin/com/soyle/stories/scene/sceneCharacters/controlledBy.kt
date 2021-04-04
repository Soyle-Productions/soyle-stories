package com.soyle.stories.scene.sceneCharacters

internal inline fun <N> N.controlledBy(controller: (N) -> Unit): N {
    controller(this)
    return this
}