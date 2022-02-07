package com.soyle.stories.usecase.character.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.shared.repositories.Repository
import com.soyle.stories.usecase.shared.repositories.Transaction

interface CharacterRepository : Repository<Character.Id, Character> {

    interface CharacterTransaction : Transaction<Character.Id, Character> {
        /**
         * @throws CharacterDoesNotExist
         */
        override suspend fun Repository<Character.Id, Character>.getOrError(id: Character.Id): Character =
            get(id) ?: throw CharacterDoesNotExist(id)

        suspend fun CharacterRepository.listCharactersInProject(projectId: Project.Id): List<Character>

        suspend fun CharacterRepository.getAll(ids: Set<Character.Id>): List<Character>

    }

    override fun startTransaction(): CharacterTransaction

}