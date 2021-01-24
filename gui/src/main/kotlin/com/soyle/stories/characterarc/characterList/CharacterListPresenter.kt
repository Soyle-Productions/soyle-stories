package com.soyle.stories.characterarc.characterList

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcReceiver
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcsByCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.characterarc.usecases.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.entities.CharacterRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme

class CharacterListPresenter(
    private val view: View.Nullable<CharacterListViewModel>,
    characterList: LiveCharacterList
) : CreatedCharacterArcReceiver, CharacterListListener, ListAllCharacterArcs.OutputPort,
    DemoteMajorCharacter.OutputPort, RenameCharacterArc.OutputPort, CharacterIncludedInThemeReceiver {

    init {
        characterList.addListener(this)
    }

    override suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter) {
        view.update {
            CharacterListViewModel(
                response.characters.map {
                    CharacterTreeItemViewModel(
                        it.first.characterId.toString(),
                        it.first.characterName,
                        it.first.mediaId?.toString() ?: "",
                        false,
                        it.second.map {
                            CharacterArcItemViewModel(
                                it.characterId.toString(),
                                it.themeId.toString(),
                                it.characterArcName
                            )
                        }
                    )
                }
            )
        }
    }

    override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
        view.updateOrInvalidated {
            copy(
                characters = (characters + CharacterTreeItemViewModel(
                    createdCharacter.characterId.toString(),
                    createdCharacter.characterName,
                    createdCharacter.mediaId?.toString() ?: "",
                    false,
                    emptyList()
                )).sortedBy { it.name }
            )
        }
    }

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        val renamedCharacterId = characterRenamed.characterId.uuid.toString()
        view.updateOrInvalidated {
            copy(
                characters = characters.map {
                    if (it.id == renamedCharacterId) it.copy(name = characterRenamed.newName)
                    else it
                }.sortedBy { it.name }
            )
        }
    }

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        val removedCharacterId = characterRemoved.characterId.toString()
        view.updateOrInvalidated {
            copy(
                characters = characters.filterNot { it.id == removedCharacterId }
            )
        }
    }

    override suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme) {
        if (!characterIncludedInTheme.isMajorCharacter) return
        CharacterArcItemViewModel(
            characterIncludedInTheme.characterId.toString(),
            characterIncludedInTheme.themeId.toString(),
            characterIncludedInTheme.themeName
        ).let(this::addNewCharacterArcItem)
    }

    override suspend fun receiveCreatedCharacterArc(createdCharacterArc: CreatedCharacterArc) {
        CharacterArcItemViewModel(
            createdCharacterArc.characterId.toString(),
            createdCharacterArc.themeId.toString(),
            createdCharacterArc.characterArcName
        ).let(this::addNewCharacterArcItem)
    }

    private fun addNewCharacterArcItem(newItem: CharacterArcItemViewModel) {
        view.updateOrInvalidated {
            copy(
                characters = characters.map {
                    if (it.id == newItem.characterId) it.copy(isExpanded = true, arcs = it.arcs + newItem)
                    else it
                }
            )
        }
    }

    override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
        val removedArcCharacterId = response.characterId.toString()
        val removedArcThemeId = response.themeId.toString()
        view.updateOrInvalidated {
            copy(
                characters = characters.map {
                    if (it.id == removedArcCharacterId) it.copy(arcs = it.arcs.filterNot { it.themeId == removedArcThemeId })
                    else it
                }
            )
        }
    }

    override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
        val renamedArcCharacterId = response.characterId.toString()
        val renamedArcThemeId = response.themeId.toString()
        view.updateOrInvalidated {
            copy(
                characters = characters.map {
                    if (it.id == renamedArcCharacterId) it.copy(isExpanded = true, arcs = it.arcs.map {
                        if (it.themeId == renamedArcThemeId) it.copy(name = response.newName)
                        else it
                    })
                    else it
                }
            )
        }
    }

    override fun receiveDemoteMajorCharacterFailure(failure: Exception) {}

}