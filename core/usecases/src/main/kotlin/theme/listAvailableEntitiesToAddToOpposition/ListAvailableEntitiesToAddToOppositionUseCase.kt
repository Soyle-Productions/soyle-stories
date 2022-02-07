package com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.OppositionValueDoesNotExist
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.theme.SymbolItem
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ListAvailableEntitiesToAddToOppositionUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ListAvailableEntitiesToAddToOpposition {

    override suspend fun invoke(oppositionId: UUID, output: ListAvailableEntitiesToAddToOpposition.OutputPort) {
        val (opposition, theme) = getOppositionValueAndParentTheme(oppositionId)

        val characters = charactersInProjectNotAlreadyInOpposition(theme.projectId, opposition)
        val locations = locationsInProjectNotAlreadyInOpposition(theme.projectId, opposition)
        val symbols = symbolsInThemeNotAlreadyInOpposition(theme, opposition)

        val response = respondWith(characters, locations, symbols)

        output.availableEntitiesListedToAddToOpposition(response)
    }

    private suspend fun getOppositionValueAndParentTheme(oppositionId: UUID): Pair<OppositionValue, Theme>
    {
        val theme = themeRepository.getThemeContainingOppositionValueWithId(OppositionValue.Id(oppositionId))
            ?: throw OppositionValueDoesNotExist(oppositionId)
        return theme.valueWebs.asSequence().flatMap { it.oppositions.asSequence() }.find { it.id.uuid == oppositionId }!! to theme
    }

    private suspend fun charactersInProjectNotAlreadyInOpposition(projectId: Project.Id, oppositionValue: OppositionValue): List<Character>
    {
        return characterRepository.listCharactersInProject(projectId)
            .filterNot { oppositionValue.hasEntityAsRepresentation(it.id.uuid) }
    }

    private suspend fun locationsInProjectNotAlreadyInOpposition(projectId: Project.Id, oppositionValue: OppositionValue): List<Location>
    {
        return locationRepository.getAllLocationsInProject(projectId)
            .filterNot { oppositionValue.hasEntityAsRepresentation(it.id.uuid) }
    }

    private fun symbolsInThemeNotAlreadyInOpposition(theme: Theme, oppositionValue: OppositionValue): List<Symbol>
    {
        return theme.symbols.filterNot { oppositionValue.hasEntityAsRepresentation(it.id.uuid) }
    }

    private fun respondWith(
        characters: List<Character>,
        locations: List<Location>,
        symbols: List<Symbol>
    ): EntitiesAvailableToAddToOpposition {
        return EntitiesAvailableToAddToOpposition(
            characters.map { CharacterItem(it.id.uuid, it.displayName.value, null) },
            locations.map { LocationItem(it.id, it.name.value) },
            symbols.map { SymbolItem(it.id.uuid, it.name) }
        )
    }

}