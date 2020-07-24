package com.soyle.stories.theme.characterConflict

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import java.util.*

class CharacterConflictController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val examineCentralConflict: ExamineCentralConflictOfTheme,
    private val examineCentralConflictOutputPort: ExamineCentralConflictOfTheme.OutputPort
) : CharacterConflictViewListener {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            examineCentralConflict.invoke(themeId, examineCentralConflictOutputPort)
        }
    }

}