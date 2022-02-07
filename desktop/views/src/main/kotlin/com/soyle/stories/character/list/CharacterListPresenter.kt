package com.soyle.stories.character.list

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcReceiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterArcsByCharacter
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.usecase.character.arc.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.usecase.character.buildNewCharacter.CharacterCreated
import com.soyle.stories.usecase.character.remove.RemovedCharacter
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import tornadofx.Controller
import tornadofx.observableListOf
import tornadofx.toObservable
import tornadofx.toProperty

class CharacterListPresenter : Controller(), CreatedCharacterArcReceiver, CharacterListListener,
    ListAllCharacterArcs.OutputPort,
    DemoteMajorCharacter.OutputPort, RenameCharacterArc.OutputPort, CharacterIncludedInThemeReceiver {

    override val scope: ProjectScope = super.scope as ProjectScope
    private val state = resolve<CharacterListState>()

    private fun update(op: () -> Unit) {
        scope.applicationScope.get<ThreadTransformer>().gui { op() }
    }

    override suspend fun receiveCharacterArcList(response: CharacterArcsByCharacter) {
        update {
            state.characters.value = response.characters.map { (characterItem, arcItems) ->
                CharacterListState.CharacterListItem.CharacterItem(
                    CharacterItemViewModel(
                        Character.Id(characterItem.characterId),
                        characterItem.characterName,
                        characterItem.mediaId?.toString() ?: "",
                    ).toProperty(),
                    false.toProperty(),
                    arcItems.map {
                        CharacterListState.CharacterListItem.ArcItem(
                            CharacterArcItemViewModel(
                                it.characterId.toString(),
                                it.themeId.toString(),
                                it.characterArcName
                            ).toProperty(),
                            false.toProperty()
                        )
                    }.sortedBy { it.arc.value.name }.toObservable()
                )
            }.sortedBy { it.character.value.characterName }.toObservable()
        }
    }

    override suspend fun receiveCreatedCharacter(characterCreated: CharacterCreated) {
        val newCharacterItem = CharacterListState.CharacterListItem.CharacterItem(
            CharacterItemViewModel(
                characterCreated.characterId,
                characterCreated.name,
                "",
            ).toProperty(),
            true.toProperty(),
            observableListOf()
        )
        update {
            val insertIndex = state.characters.indexOfFirst { it.character.value.characterName > characterCreated.name }
            if (insertIndex == -1) state.characters.add(newCharacterItem)
            else state.characters.add(insertIndex, newCharacterItem)
        }
    }

    override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
        val renamedCharacterId = characterRenamed.characterId
        update {
            val characterItem =
                state.characters.find { it.character.value.characterId == renamedCharacterId } ?: return@update
            if (characterItem.character.value.characterName != characterRenamed.oldName) return@update
            characterItem.character.value = characterItem.character.value.copy(characterName = characterRenamed.name)
            state.characters.setAll(state.characters.sortedBy { it.character.value.characterName })
        }
    }

    override suspend fun receiveEvent(event: CharacterRemovedFromStory) {
        val removedCharacterId = event.characterId
        update {
            val characterItemIndex =
                state.characters.indexOfFirst { it.character.value.characterId == removedCharacterId }
                    .takeIf { it > -1 } ?: return@update
            state.characters.removeAt(characterItemIndex)
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
        val newArc = CharacterListState.CharacterListItem.ArcItem(
            CharacterArcItemViewModel(
                newItem.characterId,
                newItem.themeId,
                newItem.name
            ).toProperty(),
            true.toProperty()
        )
        update {
            val character = state.characters.find { it.character.value.characterId.uuid.toString() == newItem.characterId } ?: return@update
            val insertIndex = character.arcs.indexOfFirst { it.arc.value.name > newItem.name }
            if (insertIndex == -1) character.arcs.add(newArc)
            else character.arcs.add(insertIndex, newArc)
            character.hasNew.value = true
        }
    }

    override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
        val removedArcCharacterId = response.characterId
        val removedArcThemeId = response.themeId.toString()
        update {
            val character = state.characters.find { it.character.value.characterId.uuid == removedArcCharacterId } ?: return@update
            val indexOfArc = character.arcs.indexOfFirst { it.arc.value.themeId == removedArcThemeId } ?: return@update
            character.arcs.removeAt(indexOfArc)
        }
    }

    override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
        val renamedArcCharacterId = response.characterId
        val renamedArcThemeId = response.themeId.toString()

        update {
            val characterItem =
                state.characters.find { it.character.value.characterId.uuid == renamedArcCharacterId } ?: return@update
            val arcItem = characterItem.arcs.find { it.arc.value.themeId == renamedArcThemeId } ?: return@update
            arcItem.arc.value = arcItem.arc.value.copy(name = response.newName)
            characterItem.arcs.setAll(characterItem.arcs.sortedBy { it.arc.value.name })
        }
    }

    override fun receiveDemoteMajorCharacterFailure(failure: Exception) {}

}