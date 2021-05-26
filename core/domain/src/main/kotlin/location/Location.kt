package com.soyle.stories.domain.location

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.location.exceptions.LocationAlreadyHostsScene
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.entitySetOf
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

    fun withName(name: SingleNonBlankLine) = copy(name = name)
    fun withDescription(description: String) = copy(description = description)

    fun withSceneHosted(sceneId: Scene.Id, sceneName: String): LocationUpdate<SceneHostedAtLocation> {
        if (hostedScenes.containsEntityWithId(sceneId)) return noUpdate(LocationAlreadyHostsScene(id, sceneId))
        return Updated(
            location = copy(hostedScenes = hostedScenes + HostedScene(sceneId, sceneName)),
            event = SceneHostedAtLocation(id, sceneId, sceneName)
        )
    }

    private fun noUpdate(reason: Any? = null) = NoUpdate(this, reason)

    data class Id(val uuid: UUID = UUID.randomUUID()) {

        override fun toString(): String = "Location($uuid)"
    }

}

class LocationRenamed(val locationId: Location.Id, val newName: String)