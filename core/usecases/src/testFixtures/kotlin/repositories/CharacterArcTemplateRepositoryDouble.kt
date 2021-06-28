package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.usecase.character.CharacterArcTemplateRepository

class CharacterArcTemplateRepositoryDouble : CharacterArcTemplateRepository {

    private var default: CharacterArcTemplate = CharacterArcTemplate(listOf())

    fun givenDefaultTemplate(template: CharacterArcTemplate)
    {
        default = template
    }

    override suspend fun getDefaultTemplate(): CharacterArcTemplate = default

}