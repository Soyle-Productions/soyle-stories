package com.soyle.stories.theme.usecases.addSymbolicItemToOpposition

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.theme.OppositionValueDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class AddSymbolicItemToOppositionUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : AddSymbolicItemToOpposition {

    override suspend fun addCharacterAsSymbol(
        oppositionId: UUID,
        characterId: UUID,
        output: AddSymbolicItemToOpposition.OutputPort
    ) {
        val theme = getTheme(oppositionId)
        val valueWeb = getValueWeb(theme, oppositionId)

        val character = getCharacter(characterId)
        val representation = SymbolicRepresentation(characterId, character.name)

        addRepresentationToTheme(oppositionId, representation, valueWeb, theme)

        output.addedSymbolicItemToOpposition(CharacterAddedToOpposition(
            theme.id.uuid,
            valueWeb.id.uuid,
            oppositionId,
            representation.name,
            representation.entityUUID
        ))
    }

    override suspend fun addLocationAsSymbol(
        oppositionId: UUID,
        locationId: UUID,
        output: AddSymbolicItemToOpposition.OutputPort
    ) {
        val theme = getTheme(oppositionId)
        val valueWeb = getValueWeb(theme, oppositionId)

        val location = getLocation(locationId)
        val representation = SymbolicRepresentation(locationId, location.name)

        addRepresentationToTheme(oppositionId, representation, valueWeb, theme)

        output.addedSymbolicItemToOpposition(LocationAddedToOpposition(
            theme.id.uuid,
            valueWeb.id.uuid,
            oppositionId,
            representation.name,
            representation.entityUUID
        ))
    }

    override suspend fun addSymbolAsSymbol(
        oppositionId: UUID,
        symbolId: UUID,
        output: AddSymbolicItemToOpposition.OutputPort
    ) {
        val theme = getTheme(oppositionId)
        val valueWeb = getValueWeb(theme, oppositionId)

        val symbol = theme.symbols.find { it.id.uuid == symbolId }
            ?: throw SymbolDoesNotExist(symbolId)
        val representation = SymbolicRepresentation(symbolId, symbol.name)

        addRepresentationToTheme(oppositionId, representation, valueWeb, theme)

        output.addedSymbolicItemToOpposition(SymbolAddedToOpposition(
            theme.id.uuid,
            valueWeb.id.uuid,
            oppositionId,
            representation.name,
            representation.entityUUID
        ))
    }

    private fun getValueWeb(
        theme: Theme,
        oppositionId: UUID
    ): ValueWeb {
        return theme.valueWebs.find { it.oppositions.any { it.id.uuid == oppositionId } }!!
    }

    private suspend fun addRepresentationToTheme(
        oppositionId: UUID,
        representation: SymbolicRepresentation,
        valueWeb: ValueWeb,
        theme: Theme
    ) {
        val oppositionValue = valueWeb.oppositions.find { it.id.uuid == oppositionId }!!
        val updatedOpposition = oppositionValue.withRepresentation(representation)
        val updatedValueWeb = valueWeb.withoutOpposition(oppositionValue.id).withOpposition(updatedOpposition)
        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(updatedValueWeb)
        themeRepository.updateTheme(updatedTheme)
    }

    private suspend fun getTheme(oppositionId: UUID): Theme {
        val theme = themeRepository.getThemeContainingOppositionValueWithId(OppositionValue.Id(oppositionId))
            ?: throw OppositionValueDoesNotExist(oppositionId)
        return theme
    }

    private suspend fun getLocation(locationId: UUID): Location {
        return locationRepository.getLocationById(Location.Id(locationId))
            ?: throw LocationDoesNotExist(locationId)
    }

    private suspend fun getCharacter(characterId: UUID): Character {
        return characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)
    }


}