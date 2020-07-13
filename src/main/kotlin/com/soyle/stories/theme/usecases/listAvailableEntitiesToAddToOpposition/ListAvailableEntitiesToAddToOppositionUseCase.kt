package com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.theme.OppositionValueDoesNotExist
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.SymbolItem
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
            characters.map { CharacterItem(it.id.uuid, it.name, null) },
            locations.map { LocationItem(it.id.uuid, it.name) },
            symbols.map { SymbolItem(it.id.uuid, it.name) }
        )
    }

}