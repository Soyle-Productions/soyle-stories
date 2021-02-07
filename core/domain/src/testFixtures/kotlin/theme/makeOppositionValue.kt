package com.soyle.stories.domain.theme

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.validation.NonBlankString


fun makeOppositionValue(
    id: OppositionValue.Id = OppositionValue.Id(),
    name: NonBlankString = nonBlankStr("Opposition Value ${str()}"),
    representations: List<SymbolicRepresentation> = listOf()
) = OppositionValue(id, name, representations)