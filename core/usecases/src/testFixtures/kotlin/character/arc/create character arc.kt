package com.soyle.stories.usecase.character.arc

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.usecase.character.CrossDomainCharacterScope
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.PlanNewCharacterArcUseCase
import kotlinx.coroutines.runBlocking

fun CrossDomainCharacterScope.`has a character arc`(named: String = "Character Arc"): CharacterArc
{
    val existingArc = test.characterArcRepository.characterArcs.find { it.characterId == character.id && it.name == named }
    if (existingArc != null) return existingArc

    var plannedCharacterArc: CharacterArc? = null
    val useCase = PlanNewCharacterArcUseCase(test.characterRepository, test.themeRepository, test.characterArcRepository)
    runBlocking {
        useCase.invoke(character.id.uuid, "Growing Up") {
            plannedCharacterArc = test.characterArcRepository
                .getCharacterArcOrError(it.createdCharacterArc.characterId, it.createdCharacterArc.themeId)
        }
    }

    return plannedCharacterArc!!
}