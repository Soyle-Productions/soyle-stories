package com.soyle.stories.scene.usecases.listOptionsToReplaceMention

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.repositories.getSceneOrError
import com.soyle.stories.theme.repositories.ThemeRepository

class ListOptionsToReplaceMentionInSceneProseUseCase(
    private val sceneRepository: SceneRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository,
    private val themeRepository: ThemeRepository
) : ListOptionsToReplaceMentionInSceneProse {

    override suspend fun invoke(
        sceneId: Scene.Id,
        entityId: MentionedEntityId<*>,
        output: ListOptionsToReplaceMentionInSceneProse.OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val response: ListOptionsToReplaceMentionInSceneProse.ResponseModel<*> = when (entityId) {
            is MentionedCharacterId -> getReplacementCharacters(scene, entityId)
            is MentionedLocationId -> getReplacementLocations(scene, entityId)
            is MentionedSymbolId -> getReplacementSymbols(scene, entityId)
        }
        output.receiveOptionsToReplaceMention(response)
    }

    private suspend fun getReplacementCharacters(
        scene: Scene,
        entityId: MentionedCharacterId
    ) : ListOptionsToReplaceMentionInSceneProse.ResponseModel<Character.Id> {
        val characters = characterRepository.listCharactersInProject(scene.projectId)
        return ListOptionsToReplaceMentionInSceneProse.ResponseModel(
            entityId,
            characters.partition { scene.includesCharacter(it.id) }.let { it.first + it.second }.map {
                ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(), it.name.value, null)
            }
        )
    }

    private suspend fun getReplacementLocations(
        scene: Scene,
        entityId: MentionedLocationId
    ) : ListOptionsToReplaceMentionInSceneProse.ResponseModel<Location.Id> {
        val locations = locationRepository.getAllLocationsInProject(scene.projectId)
        return ListOptionsToReplaceMentionInSceneProse.ResponseModel(
            entityId,
            locations.partition { scene.settings.contains(it.id) }.let { it.first + it.second }.map {
                ListOptionsToReplaceMentionInSceneProse.MentionOption(it.id.mentioned(), it.name.value, null)
            }
        )
    }

    private suspend fun getReplacementSymbols(
        scene: Scene,
        entityId: MentionedSymbolId
    ) : ListOptionsToReplaceMentionInSceneProse.ResponseModel<Symbol.Id> {
        val themes = themeRepository.listThemesInProject(scene.projectId)
        return ListOptionsToReplaceMentionInSceneProse.ResponseModel(
            entityId,
            themes.flatMap { theme ->
                theme.symbols.map {
                    ListOptionsToReplaceMentionInSceneProse.MentionOption(
                        it.id.mentioned(theme.id),
                        it.name,
                        theme.name
                    )
                }
            }
        )
    }

}