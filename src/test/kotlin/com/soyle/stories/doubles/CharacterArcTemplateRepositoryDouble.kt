package com.soyle.stories.doubles

import com.soyle.stories.characterarc.repositories.CharacterArcTemplateRepository
import com.soyle.stories.entities.CharacterArcTemplate

class CharacterArcTemplateRepositoryDouble : CharacterArcTemplateRepository {

    private var default: CharacterArcTemplate = CharacterArcTemplate(listOf())

    fun givenDefaultTemplate(template: CharacterArcTemplate)
    {
        default = template
    }

    override suspend fun getDefaultTemplate(): CharacterArcTemplate = default

}