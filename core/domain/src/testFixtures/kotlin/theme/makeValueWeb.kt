package com.soyle.stories.domain.theme

import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

fun makeValueWeb(
    id: ValueWeb.Id = ValueWeb.Id(),
    themeId: Theme.Id = Theme.Id(),
    name: NonBlankString = NonBlankString.create("Value Web ${UUID.randomUUID().toString().take(3)}")!!,
    oppositions: List<OppositionValue> = listOf()
) = ValueWeb(id, themeId, name, oppositions)