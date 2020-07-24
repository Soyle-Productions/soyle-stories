package com.soyle.stories.theme.characterConflict

import com.soyle.stories.common.Model
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleStringProperty

class CharacterConflictModel : Model<CharacterConflictScope, CharacterConflictViewModel>(CharacterConflictScope::class) {

    val centralConflictFieldLabel = bind(CharacterConflictViewModel::centralConflictFieldLabel)
    val centralConflict = bind(CharacterConflictViewModel::centralConflict)

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

}