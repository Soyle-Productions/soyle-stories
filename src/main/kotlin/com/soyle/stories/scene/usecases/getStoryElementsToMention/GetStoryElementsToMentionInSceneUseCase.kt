package com.soyle.stories.scene.usecases.getStoryElementsToMention

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.common.EntityId
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene.*

class GetStoryElementsToMentionInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : GetStoryElementsToMentionInScene {

    override suspend fun invoke(
        sceneId: Scene.Id,
        query: NonBlankString,
        output: OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val matchingStoryElements = sequenceOf(
            getMatchingCharacterElements(scene, query.value),
            getMatchingLocationElements(scene, query.value)
        ).flatten()
        output.receiveStoryElementsToMentionInScene(ResponseModel(matchingStoryElements.toList()))
    }

    private suspend fun getMatchingLocationElements(
        scene: Scene,
        query: String
    ) = getLocationsInProjectWithMatchingName(scene.projectId, query).map(::locationAsMatchingStoryElement)

    private suspend fun getMatchingCharacterElements(
        scene: Scene,
        query: String
    ) = getCharactersInProjectWithMatchingName(scene.projectId, query).map(::characterAsMatchingStoryElement)


    private suspend fun getCharactersInProjectWithMatchingName(projectId: Project.Id, query: String) =
        characterRepository.listCharactersInProject(projectId).asSequence()
            .filter { it.name.value.matchesQuery(query) }

    private suspend fun getLocationsInProjectWithMatchingName(projectId: Project.Id, query: String) =
        locationRepository.getAllLocationsInProject(projectId).asSequence()
            .filter { it.name.matchesQuery(query) }

    private fun String.matchesQuery(query: String) = contains(query, ignoreCase = true)

    private fun characterAsMatchingStoryElement(character: Character) =
        MatchingStoryElement(EntityId.of(character), character.name.value)

    private fun locationAsMatchingStoryElement(location: Location) =
        MatchingStoryElement(EntityId.of(location), location.name)

}