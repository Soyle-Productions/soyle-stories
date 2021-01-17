package com.soyle.stories.scene.usecases.listOptionsToReplaceMention

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.*
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError

class ListOptionsToReplaceMentionInSceneProseUseCase(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ListOptionsToReplaceMentionInSceneProse {

    override suspend fun invoke(
        sceneId: Scene.Id,
        entityId: MentionedEntityId<*>,
        output: ListOptionsToReplaceMentionInSceneProse.OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        when (entityId) {
            is MentionedCharacterId -> {
                val characters = characterRepository.listCharactersInProject(scene.projectId)
                output.receiveOptionsToReplaceMention(ListOptionsToReplaceMentionInSceneProse.ResponseModel(
                    entityId,
                    characters.partition { scene.includesCharacter(it.id) }.let { it.first + it.second }.map {
                        ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(), it.name.value)
                    }
                ))
            }
            is MentionedLocationId -> {
                val locations = locationRepository.getAllLocationsInProject(scene.projectId)
                output.receiveOptionsToReplaceMention(ListOptionsToReplaceMentionInSceneProse.ResponseModel(
                    entityId,
                    locations.partition { scene.settings.contains(it.id) }.let { it.first + it.second }.map {
                        ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(), it.name.value)
                    }
                ))
            }
        }
    }

}