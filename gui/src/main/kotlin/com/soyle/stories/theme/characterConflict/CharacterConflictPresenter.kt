package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.changeSectionValue.ChangedCharacterDesireReceiver
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.changeCharacterChange.ChangedCharacterChangeReceiver
import com.soyle.stories.theme.includeCharacterInTheme.CharacterIncludedInThemeReceiver
import com.soyle.stories.theme.updateThemeMetaData.ThemeWithCentralConflictChangedReceiver
import com.soyle.stories.theme.useCharacterAsOpponent.OpponentCharacterReceiver
import com.soyle.stories.theme.usecases.changeCharacterChange.ChangedCharacterChange
import com.soyle.stories.theme.usecases.changeCharacterDesire.ChangedCharacterDesire
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExaminedCentralConflict
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.AvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.ListAvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.updateThemeMetaData.ThemeWithCentralConflictChanged
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent
import java.util.*

class CharacterConflictPresenter(
    themeId: String,
    private val view: View.Nullable<CharacterConflictViewModel>
) : ExamineCentralConflictOfTheme.OutputPort, ListAvailablePerspectiveCharacters.OutputPort,
    ListAvailableCharactersToUseAsOpponents.OutputPort, OpponentCharacterReceiver,
    ThemeWithCentralConflictChangedReceiver, ChangedCharacterDesireReceiver, ChangedCharacterChangeReceiver, ChangeCharacterPropertyValue.OutputPort {

    private val themeId = UUID.fromString(themeId)

    override suspend fun centralConflictExamined(response: ExaminedCentralConflict) {
        if (response.themeId != themeId) return
        view.update {
            CharacterConflictViewModel(
                centralConflictFieldLabel = "Central Conflict",
                centralConflict = response.centralConflict,
                perspectiveCharacterLabel = "Perspective Character",
                selectedPerspectiveCharacter = response.characterChange?.let {
                    CharacterItemViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        ""
                    )
                },
                availablePerspectiveCharacters = null,
                desireLabel = "Desire",
                desire = response.characterChange?.desire ?: "",
                psychologicalWeaknessLabel = "Psychological Weakness",
                psychologicalWeakness = response.characterChange?.psychologicalWeakness ?: "",
                moralWeaknessLabel = "Moral Weakness",
                moralWeakness = response.characterChange?.moralWeakness ?: "",
                characterChangeLabel = "Character Change",
                characterChange = response.characterChange?.changeAtEnd ?: "",
                opponentSectionsLabel = response.characterChange?.let { "Opponents to ${it.characterName}" }
                    ?: "Opponents",
                attackSectionLabel = response.characterChange?.let { "How They Attack ${it.characterName}" }
                    ?: "Attacks",
                similaritiesSectionLabel = response.characterChange?.let { "Similarities to ${it.characterName}" }
                    ?: "Similarities",
                powerStatusOrAbilitiesLabel = "Power / Status / Abilities",
                mainOpponent = response.characterChange?.opponents?.singleOrNull { it.isMainOpponent }?.let {
                    CharacterChangeOpponentViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.attack,
                        it.similarities,
                        it.powerStatusOrAbility
                    )
                },
                opponents = response.characterChange?.opponents?.filterNot { it.isMainOpponent }?.map {
                    CharacterChangeOpponentViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.attack,
                        it.similarities,
                        it.powerStatusOrAbility
                    )
                } ?: emptyList(),
                availableOpponents = null
            )
        }
    }

    override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availablePerspectiveCharacters = response.map {
                    AvailablePerspectiveCharacterViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.isMajorCharacter
                    )
                }
            )
        }
    }

    override suspend fun receiveAvailableCharactersToUseAsOpponents(response: AvailableCharactersToUseAsOpponents) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availableOpponents = response.map {
                    AvailableOpponentViewModel(it.characterId.toString(), it.characterName, it.includedInTheme)
                }
            )
        }
    }

    override suspend fun receiveOpponentCharacter(opponentCharacter: OpponentCharacter) {
        if (opponentCharacter.themeId != themeId) return
        view.updateOrInvalidated {
            if (opponentCharacter.opponentOfCharacterId.toString() != selectedPerspectiveCharacter?.characterId) return@updateOrInvalidated this

            val existingOpponent = mainOpponent?.takeIf { it.characterId == opponentCharacter.characterId.toString() }
                ?: opponents.find { it.characterId == opponentCharacter.characterId.toString() }

            copy(
                mainOpponent = when {
                    opponentCharacter.isMainOpponent -> existingOpponent?: CharacterChangeOpponentViewModel(
                        opponentCharacter.characterId.toString(),
                        opponentCharacter.characterName,
                        "",
                        "",
                        ""
                    )
                    opponentCharacter.characterId.toString() == mainOpponent?.characterId && !opponentCharacter.isMainOpponent -> null
                    else -> mainOpponent
                },
                opponents = if (!opponentCharacter.isMainOpponent) opponents + (existingOpponent ?: CharacterChangeOpponentViewModel(
                    opponentCharacter.characterId.toString(),
                    opponentCharacter.characterName,
                    "",
                    "",
                    ""
                )) else opponents.filterNot { it.characterId == opponentCharacter.characterId.toString() }
            )
        }
    }

    override suspend fun receiveThemeWithCentralConflictChanged(themeWithCentralConflictChanged: ThemeWithCentralConflictChanged) {
        if (themeWithCentralConflictChanged.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                centralConflict = themeWithCentralConflictChanged.centralConflict
            )
        }
    }

    override suspend fun receiveChangedCharacterDesire(changedCharacterDesire: ChangedCharacterDesire) {
        if (changedCharacterDesire.themeId != themeId) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != changedCharacterDesire.characterId.toString())
                return@updateOrInvalidated this

            copy(
                desire = changedCharacterDesire.newValue
            )
        }
    }

    override suspend fun receiveChangedCharacterChange(changedCharacterChange: ChangedCharacterChange) {
        if (changedCharacterChange.themeId != themeId) return
        view.updateOrInvalidated {
            if (selectedPerspectiveCharacter?.characterId != changedCharacterChange.characterId.toString())
                return@updateOrInvalidated this

            copy(
                characterChange = changedCharacterChange.characterChange
            )
        }
    }

    override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
        if (response.themeId != themeId) return
        if (response.property != ChangeCharacterPropertyValue.Property.Ability) return
        val characterId = response.characterId.toString()
        view.updateOrInvalidated {
            if (opponents.none { it.characterId == characterId } && mainOpponent?.characterId != characterId) {
                return@updateOrInvalidated this
            }

            copy(
                mainOpponent = if (mainOpponent?.characterId == characterId) {
                    mainOpponent.copy(powerStatusOrAbilities = response.newValue)
                } else mainOpponent,
                opponents = opponents.map {
                    if (it.characterId == characterId) it.copy(powerStatusOrAbilities = response.newValue)
                    else it
                }
            )
        }
    }

    override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
        /* no-op */
    }

}