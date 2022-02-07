package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.oppositionValue.CharacterAddedToOpposition
import com.soyle.stories.domain.theme.oppositionValue.RenamedOppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.gui.View
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeReceiver
import com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme.CharactersAvailableToIncludeInTheme
import com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInTheme
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.usecase.theme.compareCharacterValues.CharacterValueComparison
import com.soyle.stories.usecase.theme.compareCharacterValues.CompareCharacterValues
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.OppositionValuesAvailableForCharacterInTheme
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.usecase.theme.removeSymbolicItem.RemovedSymbolicItem
import com.soyle.stories.usecase.theme.renameOppositionValue.RenameOppositionValue
import com.soyle.stories.usecase.theme.renameValueWeb.RenameValueWeb
import com.soyle.stories.usecase.theme.renameValueWeb.RenamedValueWeb
import java.util.*

class CharacterValueComparisonPresenter(
    themeId: String,
    private val view: View.Nullable<CharacterValueComparisonViewModel>
) : CompareCharacterValues.OutputPort, ListCharactersAvailableToIncludeInTheme.OutputPort,
    RemoveSymbolicItem.OutputPort, AddSymbolicItemToOpposition.OutputPort,
    RemovedCharacterFromThemeReceiver, CharacterIncludedInThemeReceiver,
    ChangeCharacterPropertyValue.OutputPort, ListAvailableOppositionValuesForCharacterInTheme.OutputPort,
    RenameOppositionValue.OutputPort, RenameValueWeb.OutputPort {

    private val themeId = UUID.fromString(themeId)

    private val valueSectionLabel = "Values"
    private val removeButtonLabel = "Remove"
    private val addValueButtonLabel = "Add Value"
    private val removeMinorCharacterTooltip = """
        This will only remove the character from the comparison.  It will not remove the character from the story.
    """.trimIndent()
    private val removeMajorCharacterTooltip = """
        This character is a major character, meaning 
        they have a character arc associated with
        this theme.  Removing them from the theme
        will also remove that character arc.  This will
        not remove the character from the story.
    """.trimIndent()

    override suspend fun charactersCompared(response: CharacterValueComparison) {
        if (response.themeId != themeId) return
        view.update {
            CharacterValueComparisonViewModel(
                openValueWebToolButtonLabel = "Open value Web Tool",
                addCharacterButtonLabel = "Add Character ...",
                characters = response.characters.map {
                    CharacterComparedWithValuesViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        ArchetypeLabel(it.characterArchetype),
                        valueSectionHeaderLabel = valueSectionLabel,
                        removeButtonLabel = removeButtonLabel,
                        removeButtonToolTip = if (it.isMajorCharacter) removeMajorCharacterTooltip else removeMinorCharacterTooltip,
                        addValueButtonLabel = addValueButtonLabel,
                        values = it.characterValues.map {
                            CharacterValueViewModel(
                                ValueWeb.Id(it.valueWebId),
                                it.oppositionId.toString(),
                                it.valueWebName,
                                it.oppositionName,
                                "(${it.valueWebName}) ${it.oppositionName}"
                            )
                        }
                    )
                },
                availableCharacters = null,
                availableOppositionValues = null
            )
        }
    }

    override suspend fun availableCharactersToIncludeInThemeListed(response: CharactersAvailableToIncludeInTheme) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availableCharacters = response.map {
                    CharacterItemViewModel(Character.Id(it.characterId), it.characterName, "")
                }
            )
        }
    }

    override suspend fun addedSymbolicItemToOpposition(response: AddSymbolicItemToOpposition.ResponseModel) {
        val addedItem = response.addedSymbolicItem
        if (addedItem.themeId != themeId) return
        if (addedItem !is CharacterAddedToOpposition) return
        val characterId = addedItem.characterId.toString()
        view.updateOrInvalidated {

            copy(
                characters = characters.map {
                    if (it.characterId != characterId) it
                    else it.copy(
                        values = it.values + CharacterValueViewModel(
                            ValueWeb.Id(addedItem.valueWebId),
                            addedItem.oppositionId.toString(),
                            addedItem.valueWebName,
                            addedItem.oppositionName,
                            "(${addedItem.valueWebName}) ${addedItem.oppositionName}"
                        )
                    )
                }
            )
        }
    }

    override suspend fun symbolicItemsRemoved(response: List<RemovedSymbolicItem>) {
        val relevantItems = response.filter { it.themeId == themeId }
        if (relevantItems.isEmpty()) return
        val removedSymbolicIds = relevantItems.groupBy { it.symbolicItemId.toString() }
        view.updateOrInvalidated {

            copy(
                characters = characters.map {
                    if (it.characterId !in removedSymbolicIds) it
                    else {
                        val removedOppositionIds =
                            removedSymbolicIds.getValue(it.characterId).map { it.oppositionValueId.toString() }.toSet()
                        it.copy(
                            values = it.values.filterNot {
                                it.oppositionId in removedOppositionIds
                            }
                        )
                    }
                }
            )
        }
    }

    override suspend fun receiveCharacterIncludedInTheme(characterIncludedInTheme: CharacterIncludedInTheme) {
        if (characterIncludedInTheme.themeId != themeId) return
        val newCharacter = CharacterComparedWithValuesViewModel(
            characterIncludedInTheme.characterId.toString(),
            characterIncludedInTheme.characterName,
            ArchetypeLabel(""),
            valueSectionLabel,
            removeButtonLabel,
            removeMinorCharacterTooltip,
            addValueButtonLabel,
            emptyList()
        )
        view.updateOrInvalidated {
            copy(
                characters = characters + newCharacter
            )
        }
    }

    override suspend fun receiveRemovedCharacterFromTheme(removedCharacterFromTheme: RemovedCharacterFromTheme) {
        if (removedCharacterFromTheme.themeId != themeId) return
        val characterId = removedCharacterFromTheme.characterId.toString()
        view.updateOrInvalidated {
            copy(
                characters = characters.filterNot { it.characterId == characterId }
            )
        }
    }

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        if (response.themeId != themeId || response.property != ChangeCharacterPropertyValue.Property.Archetype) return
        val characterId = response.characterId.toString()
        view.updateOrInvalidated {
            copy(
                characters = characters.map {
                    if (it.characterId != characterId) it
                    else it.copy(
                        archetype = ArchetypeLabel(response.newValue)
                    )
                }
            )
        }
    }

    override suspend fun availableOppositionValuesListedForCharacterInTheme(response: OppositionValuesAvailableForCharacterInTheme) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availableOppositionValues = response.map {
                    AvailableValueWebViewModel(
                        it.valueWebId.toString(),
                        it.valueWebName,
                        it.oppositionCharacterRepresents?.let {
                            AvailableOppositionValue(it.oppositionValueId.toString(), it.oppositionValueName)
                        },
                        it.map {
                            AvailableOppositionValue(it.oppositionValueId.toString(), it.oppositionValueName)
                        }
                    )
                }
            )
        }
    }

    override suspend fun valueWebRenamed(response: RenamedValueWeb) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {

            copy(
                characters = characters.map { comparedCharacterVM ->
                    if (comparedCharacterVM.values.any { it.valueWebId.uuid == response.valueWebId }) {
                        comparedCharacterVM.copy(
                            values = comparedCharacterVM.values.map { characterValueVM ->
                                if (characterValueVM.valueWebId.uuid == response.valueWebId) {
                                    CharacterValueViewModel(
                                        characterValueVM.valueWebId,
                                        characterValueVM.oppositionId,
                                        response.newName,
                                        characterValueVM.oppositionValueName,
                                        "(${response.newName}) ${characterValueVM.oppositionValueName}"
                                    )
                                } else characterValueVM
                            }
                        )
                    } else comparedCharacterVM
                }
            )
        }
    }

    override suspend fun oppositionValueRenamed(response: RenamedOppositionValue) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {

            copy(
                characters = characters.map { comparedCharacterVM ->
                    if (comparedCharacterVM.values.any { it.oppositionId == response.oppositionValueId.toString() }) {
                        comparedCharacterVM.copy(
                            values = comparedCharacterVM.values.map { characterValueVM ->
                                if (characterValueVM.oppositionId == response.oppositionValueId.toString()) {
                                    CharacterValueViewModel(
                                        characterValueVM.valueWebId,
                                        characterValueVM.oppositionId,
                                        characterValueVM.valueWebName,
                                        response.oppositionValueName,
                                        "(${characterValueVM.valueWebName}) ${response.oppositionValueName}"
                                    )
                                } else characterValueVM
                            }
                        )
                    } else comparedCharacterVM
                }
            )
        }
    }

    override fun receiveChangeCharacterPropertyValueFailure(failure: Exception) {

    }

}