package com.soyle.stories.theme.characterConflict

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.cleanBind

class CharacterConflictModel : Model<CharacterConflictScope, CharacterConflictViewModel>(CharacterConflictScope::class) {

    val centralConflictFieldLabel = bind(CharacterConflictViewModel::centralConflictFieldLabel)
    val centralConflict = bind(CharacterConflictViewModel::centralConflict)
    val perspectiveCharacterLabel = bind(CharacterConflictViewModel::perspectiveCharacterLabel)
    val selectedPerspectiveCharacter = bind(CharacterConflictViewModel::selectedPerspectiveCharacter)
    val availablePerspectiveCharacters = bind(CharacterConflictViewModel::availablePerspectiveCharacters)
    val desireLabel = bind(CharacterConflictViewModel::desireLabel)
    val desire = bind(CharacterConflictViewModel::desire)
    val psychologicalWeaknessLabel = bind(CharacterConflictViewModel::psychologicalWeaknessLabel)
    val psychologicalWeakness = bind(CharacterConflictViewModel::psychologicalWeakness)
    val moralWeaknessLabel = bind(CharacterConflictViewModel::moralWeaknessLabel)
    val moralWeakness = bind(CharacterConflictViewModel::moralWeakness)
    val characterChangeLabel = bind(CharacterConflictViewModel::characterChangeLabel)
    val characterChange = bind(CharacterConflictViewModel::characterChange)

    val opponentSectionsLabel = bind(CharacterConflictViewModel::opponentSectionsLabel)
    val attackSectionLabel = bind(CharacterConflictViewModel::attackSectionLabel)
    val similaritiesSectionLabel = bind(CharacterConflictViewModel::similaritiesSectionLabel)
    val powerStatusOrAbilitiesLabel = bind(CharacterConflictViewModel::powerStatusOrAbilitiesLabel)
    val mainOpponent = bind(CharacterConflictViewModel::mainOpponent)
    val opponents = bind(CharacterConflictViewModel::opponents)
    val availableOpponents = bind(CharacterConflictViewModel::availableOpponents)

    val isSmall = SimpleBooleanProperty()
    val isLarge = isSmall.not()

    val selectedOpponentPropertyColumn = SimpleStringProperty().apply {
        cleanBind(attackSectionLabel)
    }

    override fun viewModel(): CharacterConflictViewModel? {
        return item?.copy(
            availableOpponents = availableOpponents.value
        )
    }

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}