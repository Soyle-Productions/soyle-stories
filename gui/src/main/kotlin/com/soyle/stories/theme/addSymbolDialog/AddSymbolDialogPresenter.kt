package com.soyle.stories.theme.addSymbolDialog

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.gui.View
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.theme.themeList.SymbolListItemViewModel
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.EntitiesAvailableToAddToOpposition
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOpposition
import com.soyle.stories.theme.usecases.listSymbolsInTheme.ListSymbolsInTheme
import com.soyle.stories.theme.usecases.listSymbolsInTheme.SymbolsInTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import java.util.*
import javax.xml.stream.Location

class AddSymbolDialogPresenter(
    themeId: String,
    oppositionId: String,
    private val view: View.Nullable<AddSymbolDialogViewModel>
) : ListAvailableEntitiesToAddToOpposition.OutputPort,
    BuildNewCharacter.OutputPort, RenameCharacter.OutputPort, RemoveCharacterFromStory.OutputPort,
    CreateNewLocation.OutputPort, RenameLocation.OutputPort, DeleteLocation.OutputPort, AddSymbolToTheme.OutputPort,
    RenameSymbol.OutputPort, RemoveSymbolFromTheme.OutputPort, AddSymbolicItemToOpposition.OutputPort {

    private val themeId = UUID.fromString(themeId)
    private val oppositionId = UUID.fromString(oppositionId)

    override suspend fun availableEntitiesListedToAddToOpposition(response: EntitiesAvailableToAddToOpposition) {
        view.update {
            copyOrDefault(
                characters = response.characters.map {
                    CharacterItemViewModel(
                        it.characterId.toString(),
                        it.characterName
                    )
                }.sortedBy { it.characterName },
                locations = response.locations.map { LocationItemViewModel(it) }.sortedBy { it.name },
                symbols = response.symbols.map { SymbolListItemViewModel(it.symbolId.toString(), it.symbolName) }
                    .sortedBy { it.symbolName }
            )
        }
    }

    override fun receiveBuildNewCharacterResponse(response: CharacterItem) {
        view.updateOrInvalidated {
            copyOrDefault(
                characters = characters + CharacterItemViewModel(
                    response.characterId.toString(),
                    response.characterName
                )
            )
        }
    }

    override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
        val characterId = response.characterId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                characters = characters.map {
                    if (it.characterId == characterId) it.copy(characterName = response.newName)
                    else it
                }
            )
        }
    }

    override fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
        val characterId = response.characterId.toString()
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

    override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
        val locationId = response.locationId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                locations = locations.map {
                    if (it.id == locationId) LocationItemViewModel(it.id, name = response.newName)
                    else it
                }
            )
        }
    }

    override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
        val locationId = response.locationId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                locations = locations.filterNot { it.id == locationId }
            )
        }
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copyOrDefault(
                symbols = symbols + SymbolListItemViewModel(
                    response.symbolId.toString(),
                    response.symbolName
                )
            )
        }
    }

    override suspend fun symbolRenamed(response: RenamedSymbol) {
        if (response.themeId != themeId) return
        val symbolId = response.symbolId.toString()
        view.updateOrInvalidated {
            copyOrDefault(
                symbols = symbols.map {
                    if (it.symbolId == symbolId) it.copy(symbolName = response.newName)
                    else it
                }
            )
        }
    }

    override suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme) {
        if (response.themeId != themeId) return
        val symbolId = response.symbolId.toString()
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

    override fun receiveBuildNewCharacterFailure(failure: CharacterException) {}
    override fun receiveRenameCharacterFailure(failure: CharacterException) {}
    override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {}
    override fun receiveCreateNewLocationFailure(failure: LocationException) {}
    override fun receiveRenameLocationFailure(failure: LocationException) {}
    override fun receiveDeleteLocationFailure(failure: LocationException) {}
    override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
        // do nothing
    }
}