package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.scene.order.exceptions.cannotAddSceneOutOfBounds
import com.soyle.stories.domain.scene.order.exceptions.sceneAlreadyAtIndex
import com.soyle.stories.domain.scene.order.exceptions.sceneCannotBeAddedTwice
import kotlin.IndexOutOfBoundsException

class SceneOrder private constructor(
    val projectId: Project.Id,
    val order: Set<Scene.Id>
) {

    companion object {
        @JvmStatic
        fun initializeInProject(projectId: Project.Id) = SceneOrder(projectId, setOf())

        fun reInstantiate(projectId: Project.Id, order: List<Scene.Id>) = SceneOrder(projectId, order.toSet())
    }

    private fun copy(
        order: Set<Scene.Id> = this.order
    ) = SceneOrder(projectId, order = order)

    fun withScene(sceneCreated: SceneCreated, at: Int = -1): SceneOrderUpdate<SceneCreated> {
        val sceneId = sceneCreated.sceneId
        if (sceneId in order) return noUpdate(sceneCannotBeAddedTwice(sceneId))
        if (at !in -1 .. order.size) return noUpdate(cannotAddSceneOutOfBounds(sceneId, at))
        if (at < 0) {
            return copy(order + sceneId).updatedBy(sceneCreated)
        } else {
            val indexedOrder = order.toList()
            val newOrder = indexedOrder.subList(0, at) + sceneId + indexedOrder.subList(at, indexedOrder.size)
            return copy(newOrder.toSet()).updatedBy(sceneCreated)
        }
    }

    fun withScene(sceneId: Scene.Id): SceneModifications? {
        if (sceneId !in order) return null
        return object : SceneModifications {
            override fun movedTo(index: Int): SceneOrderUpdate<Nothing?> {
                if (index < 0 || index >= order.size) return noUpdate(IndexOutOfBoundsException(""))
                if (index == order.indexOf(sceneId)) return noUpdate(sceneAlreadyAtIndex(sceneId, index))
                val newOrder = order.minus(sceneId).toMutableList().apply { add(index, sceneId) }.toSet()
                return copy(newOrder).updatedBy(null)
            }

            override fun removed(): SceneOrderUpdate<SceneRemoved> {
                val newOrder = order - sceneId
                return copy(order = newOrder).updatedBy(SceneRemoved(sceneId, newOrder.toList()))
            }
        }
    }

    interface SceneModifications {
        fun movedTo(index: Int): SceneOrderUpdate<Nothing?>
        fun removed(): SceneOrderUpdate<SceneRemoved>
    }

    private fun noUpdate(reason: Throwable) = UnSuccessfulSceneOrderUpdate(this, reason)
    private fun <T> updatedBy(change: T) = SceneOrderUpdate.Successful(this, change)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SceneOrder

        if (projectId != other.projectId) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = projectId.hashCode()
        result = 31 * result + order.hashCode()
        return result
    }

    override fun toString(): String {
        return "SceneOrder(projectId=$projectId, order=$order)"
    }


}