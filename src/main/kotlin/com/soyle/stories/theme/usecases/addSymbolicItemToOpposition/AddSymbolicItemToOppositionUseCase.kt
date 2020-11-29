package com.soyle.stories.theme.usecases.addSymbolicItemToOpposition

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.theme.OppositionValueDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.entities.theme.oppositionValue.CharacterAddedToOpposition
import com.soyle.stories.entities.theme.oppositionValue.LocationAddedToOpposition
import com.soyle.stories.entities.theme.oppositionValue.SymbolAddedToOpposition
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.entities.theme.valueWeb.CharacterAlreadyRepresentationValueInValueWeb
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

class AddSymbolicItemToOppositionUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : AddSymbolicItemToOpposition {

    override suspend fun invoke(
        oppositionId: UUID,
        symbolicItemId: SymbolicItemId,
        output: AddSymbolicItemToOpposition.OutputPort
    ) {
        when (symbolicItemId) {
            is CharacterId -> addCharacterAsSymbol(oppositionId, symbolicItemId.characterId, output)
            is LocationId -> addLocationAsSymbol(oppositionId, symbolicItemId.locationId, output)
            is SymbolId -> addSymbolAsSymbol(oppositionId, symbolicItemId.symbolId, output)
        }
    }

    override suspend fun addCharacterAsSymbol(
        oppositionId: UUID,
        characterId: UUID,
        output: AddSymbolicItemToOpposition.OutputPort
    ) {
        val theme = getTheme(oppositionId)
        val valueWeb = getValueWeb(theme, oppositionId)

        val character = getCharacter(characterId)
        val representation = SymbolicRepresentation(characterId, character.name.value)

        val characterIncludedInTheme: CharacterIncludedInTheme?
        val themeWithCharacter = if (!theme.containsCharacter(character.id)) {
            theme.withCharacterIncluded(character.id, character.name.value, character.media).also {
                characterIncludedInTheme = CharacterIncludedInTheme(
                    theme.id.uuid,
                    "",
                    characterId,
                    "",
                    false
                )
            }
        } else {
            characterIncludedInTheme = null
            theme
        }
        val removed = addRepresentationToTheme(oppositionId, representation, valueWeb, themeWithCharacter)

        output.addedSymbolicItemToOpposition(
            AddSymbolicItemToOpposition.ResponseModel(
                CharacterAddedToOpposition(
                    theme.id.uuid,
                    valueWeb.id.uuid,
                    valueWeb.name,
                    oppositionId,
                    valueWeb.oppositions.find { it.id.uuid == oppositionId }!!.name,
                    representation.name,
                    representation.entityUUID
                ),
                listOfNotNull(removed),
                listOfNotNull(characterIncludedInTheme)
            )
        )
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

        val removed = addRepresentationToTheme(oppositionId, representation, valueWeb, theme)

        output.addedSymbolicItemToOpposition(
            AddSymbolicItemToOpposition.ResponseModel(
                LocationAddedToOpposition(
                    theme.id.uuid,
                    valueWeb.id.uuid,
                    valueWeb.name,
                    oppositionId,
                    valueWeb.oppositions.find { it.id.uuid == oppositionId }!!.name,
                    representation.name,
                    representation.entityUUID
                ),
                listOfNotNull(removed),
                emptyList()
            )
        )
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

        val removed = addRepresentationToTheme(oppositionId, representation, valueWeb, theme)

        output.addedSymbolicItemToOpposition(
            AddSymbolicItemToOpposition.ResponseModel(
                SymbolAddedToOpposition(
                    theme.id.uuid,
                    valueWeb.id.uuid,
                    valueWeb.name,
                    oppositionId,
                    valueWeb.oppositions.find { it.id.uuid == oppositionId }!!.name,
                    representation.name,
                    representation.entityUUID
                ),
                listOfNotNull(removed),
                emptyList()
            )
        )
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
    ): RemovedSymbolicItem? {

        val (updatedValueWeb, removedItem) = try {
            valueWeb.withRepresentationOf(representation, OppositionValue.Id(oppositionId)) to null
        } catch (duplicate: CharacterAlreadyRepresentationValueInValueWeb) {
            valueWeb.withoutRepresentation(representation.entityUUID)
                .withRepresentationOf(representation, OppositionValue.Id(oppositionId)) to
                    RemovedSymbolicItem(
                        duplicate.themeId,
                        duplicate.valueWebId,
                        duplicate.oppositionValueId,
                        duplicate.characterId
                    )
        }

        val updatedTheme = theme.withoutValueWeb(valueWeb.id).withValueWeb(updatedValueWeb)
        themeRepository.updateTheme(updatedTheme)
        return removedItem
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