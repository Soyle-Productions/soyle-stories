package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.usecase.framework.CrossDomainTest
import com.soyle.stories.usecase.project.CrossDomainProjectScope
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import kotlinx.coroutines.runBlocking

fun CrossDomainProjectScope.`has a character`(named: String = "Character"): Character
{
    val existingCharacter = test.characterRepository.characters.values
        .find { it.projectId == project.id && it.name.value == named }
    if (existingCharacter != null) return existingCharacter

    var createdCharacter: Character? = null
    val useCase = BuildNewCharacterUseCase(test.characterRepository, test.themeRepository)
    val output = object : BuildNewCharacter.OutputPort {
        override fun receiveBuildNewCharacterFailure(failure: Exception) = throw failure
        override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) = Unit
        override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) = Unit
        override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
            createdCharacter = test.characterRepository.getCharacterOrError(response.characterId)
        }
    }

    runBlocking { useCase.invoke(project.id.uuid, NonBlankString.create(named)!!, output) }

    return createdCharacter!!

}