package com.soyle.stories.scene.usecases.getStoryElementsToMention

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene.*
import com.soyle.stories.theme.repositories.ThemeRepository

class GetStoryElementsToMentionInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository,
    private val themeRepository: ThemeRepository
) : GetStoryElementsToMentionInScene {

    override suspend fun invoke(
        sceneId: Scene.Id,
        query: NonBlankString,
        output: OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val matchingStoryElements = sequenceOf(
            getMatchingCharacterElements(scene, query.value),
            getMatchingLocationElements(scene, query.value),
            getMatchingSymbolElements(scene, query.value)
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

    private suspend fun getMatchingSymbolElements(
        scene: Scene,
        query: String
    ) = getSymbolsInProjectWithMatchingName(scene.projectId, query).map { symbolAsMatchingStoryElement(it.first, it.second) }

    private suspend fun getCharactersInProjectWithMatchingName(projectId: Project.Id, query: String) =
        characterRepository.listCharactersInProject(projectId).asSequence()
            .filter { it.name.value.matchesQuery(query) }

    private suspend fun getLocationsInProjectWithMatchingName(projectId: Project.Id, query: String) =
        locationRepository.getAllLocationsInProject(projectId).asSequence()
            .filter { it.name.value.matchesQuery(query) }

    private suspend fun getSymbolsInProjectWithMatchingName(projectId: Project.Id, query: String) =
        themeRepository.listThemesInProject(projectId).asSequence()
            .flatMap { theme ->
                theme.symbols.asSequence()
                    .filter { it.name.matchesQuery(query) }
                    .map { it to theme }
            }


    private fun String.matchesQuery(query: String) = contains(query, ignoreCase = true)

    private fun characterAsMatchingStoryElement(character: Character) =
        MatchingStoryElement(character.id.mentioned(), character.name.value, null)

    private fun locationAsMatchingStoryElement(location: Location) =
        MatchingStoryElement(location.id.mentioned(), location.name.value, null)

    private fun symbolAsMatchingStoryElement(symbol: Symbol, theme: Theme) =
        MatchingStoryElement(symbol.id.mentioned(theme.id), symbol.name, theme.name)

}