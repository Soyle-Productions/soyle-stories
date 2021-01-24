package com.soyle.stories.theme

import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository

interface Context {
    val themeRepository: ThemeRepository
    val characterRepository: CharacterRepository
    val characterArcRepository: CharacterArcRepository
}