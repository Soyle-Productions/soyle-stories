package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

class CompoundEvent<E : SceneEvent> private constructor(override val sceneId: Scene.Id, val events: List<E>) :
    SceneEvent() {

    constructor(sceneId: Scene.Id) : this(sceneId, emptyList())
    constructor(events: List<E>) : this(events.first().sceneId, events)
    constructor(vararg events: E) : this(events.first().sceneId, events.toList())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CompoundEvent<*>

        if (sceneId != other.sceneId) return false
        if (events != other.events) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sceneId.hashCode()
        result = 31 * result + events.hashCode()
        return result
    }

    override fun toString(): String {
        return "CompoundEvent(sceneId=$sceneId, events=$events)"
    }


}