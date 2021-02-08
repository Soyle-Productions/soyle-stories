package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.domain.character.CharacterRenamed
import com.soyle.stories.domain.location.LocationRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.location.deleteLocation.DeletedLocationReceiver
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.theme.renameSymbol.RenamedSymbolReceiver
import com.soyle.stories.theme.themeList.SymbolListItemViewModel
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition.EntitiesAvailableToAddToOpposition
import com.soyle.stories.usecase.theme.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOpposition
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.usecase.theme.renameSymbol.RenamedSymbol
import java.util.*

class AddSymbolDialogPresenter(
    themeId: String,
    oppositionId: String,
    private val view: View.Nullable<AddSymbolDialogViewModel>
) : ListAvailableEntitiesToAddToOpposition.OutputPort,
    CreatedCharacterReceiver, CharacterRenamedReceiver, RemovedCharacterReceiver,
    CreateNewLocation.OutputPort, LocationRenamedReceiver, DeletedLocationReceiver, SymbolAddedToThemeReceiver,
    RenamedSymbolReceiver, SymbolRemovedFromThemeReceiver, AddSymbolicItemToOpposition.OutputPort {

    private val themeId = UUID.fromString(themeId)
    private val oppositionId = UUID.fromString(oppositionId)

    override suspend fun availableEntitiesListedToAddToOpposition(response: EntitiesAvailableToAddToOpposition) {
        view.update {
            copyOrDefault(
                characters = response.characters.map {
                    CharacterItemViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        ""
                    )
                }.sortedBy { it.characterName },
                locations = response.locations.map { LocationItemViewModel(it) }.sortedBy { it.name },
                symbols = response.symbols.map { SymbolListItemViewModel(it.symbolId.toString(), it.symbolName) }
                    .sortedBy { it.symbolName }
            )
        }
    }

    override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
        view.updateOrInvalidated {
            copyOrDefault(
                characters = characters + CharacterItemViewModel(
                    createdCharacter.characterId.toString(),
                    createdCharacter.characterName,
                    ""
                )
            )
        }
    }

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        val characterId = characterRenamed.characterId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                characters = characters.map {
                    if (it.characterId == characterId) it.copy(characterName = characterRenamed.newName)
                    else it
                }
            )
        }
    }

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        val characterId = characterRemoved.characterId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                characters = characters.filterNot { it.characterId == characterId }
            )
        }
    }

    override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
        view.updateOrInvalidated {
            copyOrDefault(
                locations = locations + LocationItemViewModel(
                    response.locationId.toString(),
                    response.locationName
                )
            )
        }
    }

    override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
        val locationId = locationRenamed.locationId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                locations = locations.map {
                    if (it.id == locationId) LocationItemViewModel(it.id, name = locationRenamed.newName)
                    else it
                }
            )
        }
    }

    override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
        val locationId = deletedLocation.location.uuid.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                locations = locations.filterNot { it.id == locationId }
            )
        }
    }

    override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
        if (symbolAddedToTheme.themeId != themeId) return
        view.updateOrInvalidated {
            copyOrDefault(
                symbols = symbols + SymbolListItemViewModel(
                    symbolAddedToTheme.symbolId.toString(),
                    symbolAddedToTheme.symbolName
                )
            )
        }
    }

    override suspend fun receiveRenamedSymbol(renamedSymbol: RenamedSymbol) {
        if (renamedSymbol.themeId != themeId) return
        val symbolId = renamedSymbol.symbolId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                symbols = symbols.map {
                    if (it.symbolId == symbolId) it.copy(symbolName = renamedSymbol.newName)
                    else it
                }
            )
        }
    }

    override suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme) {
        if (symbolRemovedFromTheme.themeId != themeId) return
        val symbolId = symbolRemovedFromTheme.symbolId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                symbols = symbols.filterNot { it.symbolId == symbolId }
            )
        }
    }

    override suspend fun addedSymbolicItemToOpposition(response: AddSymbolicItemToOpposition.ResponseModel) {
        val addedItem = response.addedSymbolicItem
        if (addedItem.themeId != themeId) return
        if (addedItem.oppositionId != oppositionId) return
        view.updateOrInvalidated {
            copyOrDefault(completed = true)
        }
    }

    private fun AddSymbolDialogViewModel?.copyOrDefault(
        characters: List<CharacterItemViewModel> = this?.characters ?: listOf(),
        locations: List<LocationItemViewModel> = this?.locations ?: listOf(),
        symbols: List<SymbolListItemViewModel> = this?.symbols ?: listOf(),
        completed: Boolean = this?.completed ?: false
    ) = AddSymbolDialogViewModel(
        characters = characters.sortedBy { it.characterName },
        locations = locations.sortedBy { it.name },
        symbols = symbols.sortedBy { it.symbolName },
        completed = completed
    )

    override fun receiveCreateNewLocationFailure(failure: Exception) {}
}