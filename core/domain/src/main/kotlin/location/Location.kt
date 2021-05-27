package com.soyle.stories.domain.location

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.events.HostedSceneRenamed
import com.soyle.stories.domain.location.events.LocationRenamed
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.location.exceptions.HostedSceneAlreadyHasName
import com.soyle.stories.domain.location.exceptions.LocationAlreadyHostsScene
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.SingleNonBlankLine
import java.util.*

class Location(
    override val id: Id,
    val projectId: Project.Id,
    val name: SingleNonBlankLine,
    val description: String = "",
    val hostedScenes: EntitySet<HostedScene>
) : Entity<Location.Id> {

    private fun copy(
        name: SingleNonBlankLine = this.name,
        description: String = this.description,
        hostedScenes: EntitySet<HostedScene> = this.hostedScenes
    ) = Location(id, projectId, name, description, hostedScenes)

    fun withName(name: SingleNonBlankLine): LocationUpdate<LocationRenamed> =
        Updated(copy(name = name), LocationRenamed(id, name.value))

    fun withDescription(description: String) = copy(description = description)

    fun withSceneHosted(sceneId: Scene.Id, sceneName: String): LocationUpdate<SceneHostedAtLocation> {
        if (hostedScenes.containsEntityWithId(sceneId)) return noUpdate(LocationAlreadyHostsScene(id, sceneId))
        return Updated(
            location = copy(hostedScenes = hostedScenes + HostedScene(sceneId, sceneName)),
            event = SceneHostedAtLocation(id, sceneId, sceneName)
        )
    }

    fun withHostedScene(sceneId: Scene.Id): HostedSceneModifications? {
        val hostedScene = hostedScenes.getEntityById(sceneId) ?: return null
        return object : HostedSceneModifications {

            override fun renamed(to: String): LocationUpdate<HostedSceneRenamed> {
                if (hostedScene.sceneName == to) return noUpdate(reason = HostedSceneAlreadyHasName(id, sceneId, to))
                return Updated(
                    location = copy(hostedScenes = hostedScenes.plus(hostedScene.withName(to))),
                    event = HostedSceneRenamed(id, sceneId, to)
                )
            }
            
        }
    }

    private fun noUpdate(reason: Any? = null) = NoUpdate(this, reason)

    data class Id(val uuid: UUID = UUID.randomUUID()) {

        override fun toString(): String = "Location($uuid)"
    }

    interface HostedSceneModifications {

        fun renamed(to: String): LocationUpdate<HostedSceneRenamed>
    }

}

