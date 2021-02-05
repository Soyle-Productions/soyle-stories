package com.soyle.stories.characterarc.repositories

import com.soyle.stories.entities.CharacterArcTemplate

interface CharacterArcTemplateRepository {

    suspend fun getDefaultTemplate(): CharacterArcTemplate

}