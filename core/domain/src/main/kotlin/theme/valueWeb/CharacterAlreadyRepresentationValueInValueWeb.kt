package com.soyle.stories.domain.theme.valueWeb

import com.soyle.stories.domain.validation.DuplicateOperationException
import java.util.*

class CharacterAlreadyRepresentationValueInValueWeb(
    val themeId: UUID,
    val valueWebId: UUID,
    val oppositionValueId: UUID,
    val attemptedOppositionValueId: UUID,
    val characterId: UUID
) : DuplicateOperationException()