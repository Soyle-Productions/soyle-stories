package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExaminedCentralConflict
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.AvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.ListAvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent
import java.util.*

class CharacterConflictPresenter(
    themeId: String,
    private val view: View.Nullable<CharacterConflictViewModel>
) : ExamineCentralConflictOfTheme.OutputPort, ListAvailablePerspectiveCharacters.OutputPort, ListAvailableCharactersToUseAsOpponents.OutputPort, UseCharacterAsOpponent.OutputPort {

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
                opponentSectionsLabel = response.characterChange?.let { "Opponents to ${it.characterName}" } ?: "Opponents",
                attackSectionLabel = response.characterChange?.let { "How They Attack ${it.characterName}" } ?: "Attacks",
                similaritiesSectionLabel = response.characterChange?.let { "Similarities to ${it.characterName}" } ?: "Similarities",
                powerStatusOrAbilitiesLabel = "Power / Status / Abilities",
                opponents = response.characterChange?.opponents?.map {
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
                    AvailablePerspectiveCharacterViewModel(it.characterId.toString(), it.characterName, it.isMajorCharacter)
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

    override suspend fun characterIsOpponent(response: UseCharacterAsOpponent.ResponseModel) {
        val newOpponent = response.characterAsOpponent
        if (newOpponent.themeId != themeId) return
        view.updateOrInvalidated {
            if (newOpponent.opponentOfCharacterId.toString() != selectedPerspectiveCharacter?.characterId) return@updateOrInvalidated this
            copy(
                opponents = opponents + CharacterChangeOpponentViewModel(
                    newOpponent.characterId.toString(),
                    newOpponent.characterName,
                    "",
                    "",
                    ""
                )
            )
        }
    }

}