package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

class CompoundEvent<E : SceneEvent> private constructor(override val sceneId: Scene.Id, val events: List<E>) :
    SceneEvent() {

    constructor(sceneId: Scene.Id) : this(sceneId, emptyList())
    constructor(events: List<E>) : this(events.first().sceneId, events)
    constructor(vararg events: E) : this(events.first().sceneId, events.toList())
}