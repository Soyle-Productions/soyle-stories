package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.CharactersAvailableToIncludeInTheme
import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInTheme
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.entities.theme.oppositionValue.CharacterAddedToOpposition
import com.soyle.stories.gui.View
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.removeCharacterFromComparison.RemovedCharacterFromThemeReceiver
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.compareCharacterValues.CharacterValueComparison
import com.soyle.stories.theme.usecases.compareCharacterValues.CompareCharacterValues
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.theme.usecases.listAvailableOppositionValuesForCharacterInTheme.OppositionValuesAvailableForCharacterInTheme
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemoveSymbolicItem
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import java.util.*

class CharacterValueComparisonPresenter(
    themeId: String,
    private val view: View.Nullable<CharacterValueComparisonViewModel>
) : CompareCharacterValues.OutputPort, ListCharactersAvailableToIncludeInTheme.OutputPort,
    RemoveSymbolicItem.OutputPort, AddSymbolicItemToOpposition.OutputPort,
    RemovedCharacterFromThemeReceiver, CharacterIncludedInThemeReceiver,
    ChangeCharacterPropertyValue.OutputPort, ListAvailableOppositionValuesForCharacterInTheme.OutputPort {

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
                                it.oppositionId.toString(),
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
                    CharacterItemViewModel(it.characterId.toString(), it.characterName, "")
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
                            addedItem.oppositionId.toString(),
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
                        val removedOppositionIds = removedSymbolicIds.getValue(it.characterId).map { it.oppositionValueId.toString() }.toSet()
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

    override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {

    }

}