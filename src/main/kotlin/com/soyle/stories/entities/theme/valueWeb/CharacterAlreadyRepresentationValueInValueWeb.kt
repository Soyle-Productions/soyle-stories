package com.soyle.stories.entities.theme.valueWeb

import com.soyle.stories.common.DuplicateOperationException
import java.util.*

class CharacterAlreadyRepresentationValueInValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionValueId: UUID,
    val attemptedOppositionValueId: UUID,
    val characterId: UUID
) : DuplicateOperationException()