package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExaminedCentralConflict
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import java.util.*

class CharacterConflictPresenter(
    themeId: String,
    private val view: View.Nullable<CharacterConflictViewModel>
) : ExamineCentralConflictOfTheme.OutputPort, ListAvailablePerspectiveCharacters.OutputPort {

    private val themeId = UUID.fromString(themeId)

    override suspend fun centralConflictExamined(response: ExaminedCentralConflict) {
        if (response.themeId != themeId) return
        view.update {
            CharacterConflictViewModel(
                centralConflictFieldLabel = "Central Conflict",
                centralConflict = response.centralConflict,
                perspectiveCharacterLabel = "Perspective Character",
                selectedPerspectiveCharacter = response.characterChange?.let { CharacterItemViewModel(it.characterId.toString(), it.characterName) },
                availablePerspectiveCharacters = null,
                desireLabel = "Desire",
                desire = response.characterChange?.desire ?: "",
                psychologicalWeaknessLabel = "Psychological Weakness",
                psychologicalWeakness = response.characterChange?.psychologicalWeakness ?: "",
                moralWeaknessLabel = "Moral Weakness",
                moralWeakness = response.characterChange?.moralWeakness ?: "",
                characterChangeLabel = "Character Change",
                characterChange = response.characterChange?.changeAtEnd ?: ""
            )
        }
    }

    override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
        if (response.themeId != themeId) return
        view.updateOrInvalidated {
            copy(
                availablePerspectiveCharacters = response.map {
                    CharacterItemViewModel(it.characterId.toString(), it.characterName)
                }
            )
        }
    }

}