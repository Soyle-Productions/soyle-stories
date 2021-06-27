package com.soyle.stories.usecase.character

import com.soyle.stories.usecase.framework.CrossDomainTest
import com.soyle.stories.domain.character.Character

class CrossDomainCharacterScope private constructor(val character: Character, val test: CrossDomainTest) {
    companion object {
        fun CrossDomainTest.`given the character`(character: Character) = CrossDomainCharacterScope(character, this)
    }
}