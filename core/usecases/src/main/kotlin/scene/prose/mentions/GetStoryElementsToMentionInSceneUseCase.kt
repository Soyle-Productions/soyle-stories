package com.soyle.stories.usecase.scene.prose.mentions

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.scene.prose.mentions.GetStoryElementsToMentionInScene.OutputPort
import com.soyle.stories.usecase.shared.availability.AvailableStoryElementItem

class GetStoryElementsToMentionInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository,
) : GetStoryElementsToMentionInScene {

    override suspend fun invoke(
        sceneId: Scene.Id,
        output: OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        output.receiveStoryElementsToMentionInScene(
            AvailableStoryElementsToMentionInScene(
                scene.id,
                scene.includedCharacterIds + scene.usedLocationIds,
                charactersInProjectAsAvailableElements(scene.projectId) +
                        locationsInProjectAsAvailableElements(scene.projectId)

            )
        )
    }

    private val Scene.includedCharacterIds get() = includedCharacters.map { it.id.mentioned() }.toSet()
    private val Scene.usedLocationIds get() = settings.map { it.id.mentioned() }.toSet()

    private suspend fun charactersInProjectAsAvailableElements(projectId: Project.Id) =
        characterRepository.listCharactersInProject(projectId).flatMap { character ->
            val mentionedId = character.id.mentioned()
            character.names.map { name ->
                AvailableStoryElementItem(
                    mentionedId,
                    name.value,
                    character.displayName.value.takeUnless { it == name.value })
            }
        }.toSet()

    private suspend fun locationsInProjectAsAvailableElements(projectId: Project.Id) =
        locationRepository.getAllLocationsInProject(projectId).map { location ->
            AvailableStoryElementItem(
                location.id.mentioned(),
                location.name.value,
                null
            )
        }
}