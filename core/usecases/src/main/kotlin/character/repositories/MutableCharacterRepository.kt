package com.soyle.stories.usecase.character.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.shared.repositories.MutableRepository
import com.soyle.stories.usecase.shared.repositories.MutableTransaction

interface MutableCharacterRepository : CharacterRepository, MutableRepository<Character.Id, Character> {

    interface MutableCharacterTransaction : CharacterRepository.CharacterTransaction,
        MutableTransaction<Character.Id, Character>

    override fun startTransaction(): MutableCharacterTransaction

}