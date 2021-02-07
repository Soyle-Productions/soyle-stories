package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.CharacterArcTemplate

interface CharacterArcTemplateRepository {
    suspend fun getDefaultTemplate(): CharacterArcTemplate
}