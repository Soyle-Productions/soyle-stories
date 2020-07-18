package com.soyle.stories.theme.usecases

import arrow.core.Valid
import com.soyle.stories.common.ValidationException
import com.soyle.stories.theme.OppositionValueNameCannotBeBlank
import com.soyle.stories.theme.ThemeNameCannotBeBlank

fun validateThemeName(name: String)
{
    if (name.isBlank()) throw ThemeNameCannotBeBlank
}

object SymbolNameCannotBeBlank : ValidationException()

fun validateSymbolName(name: String) {
    if (name.isBlank()) throw SymbolNameCannotBeBlank
}

object ValueWebNameCannotBeBlank : ValidationException()

fun validateValueWebName(name: String) {
    if (name.isBlank()) throw ValueWebNameCannotBeBlank
}

fun validateOppositionValueName(name: String){
    if (name.isBlank()) throw OppositionValueNameCannotBeBlank
}