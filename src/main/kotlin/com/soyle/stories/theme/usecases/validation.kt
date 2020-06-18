package com.soyle.stories.theme.usecases

import com.soyle.stories.theme.ThemeNameCannotBeBlank

fun validateThemeName(name: String)
{
    if (name.isBlank()) throw ThemeNameCannotBeBlank
}