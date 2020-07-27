package com.soyle.stories.theme.characterConflict

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.ExamineCentralConflictOfTheme
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import java.util.*

class CharacterConflictController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val examineCentralConflict: ExamineCentralConflictOfTheme,
    private val examineCentralConflictOutputPort: ExamineCentralConflictOfTheme.OutputPort,
    private val getAvailablePerspectiveCharacters: ListAvailablePerspectiveCharacters,
    private val getAvailablePerspectiveCharactersOutputPort: ListAvailablePerspectiveCharacters.OutputPort
) : CharacterConflictViewListener {

    private val themeId = UUID.fromString(themeId)

    override fun getValidState(characterId: String?) {
        val preparedCharacterId = characterId?.let(UUID::fromString)
        threadTransformer.async {
            examineCentralConflict.invoke(themeId, preparedCharacterId, examineCentralConflictOutputPort)
        }
    }

    override fun getAvailableCharacters() {
        threadTransformer.async {
            getAvailablePerspectiveCharacters.invoke(
                themeId, getAvailablePerspectiveCharactersOutputPort
            )
        }
    }

}