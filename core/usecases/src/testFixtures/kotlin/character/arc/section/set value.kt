package com.soyle.stories.usecase.character.arc.section

import com.soyle.stories.domain.character.MoralWeakness
import com.soyle.stories.domain.character.PsychologicalWeakness
import com.soyle.stories.usecase.character.arc.CharacterArcThens
import com.soyle.stories.usecase.character.arc.CharacterArcWhens
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterMoralWeakness
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterMoralWeaknessUseCase
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeaknessUseCase
import com.soyle.stories.usecase.framework.CrossDomainTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals

fun CharacterArcWhens.`has its psychological weakness set to`(weakness: String) {
    val useCase = ChangeCharacterPsychologicalWeaknessUseCase(test.themeRepository, test.characterArcRepository)

    val request = ChangeCharacterPsychologicalWeakness.RequestModel(
        arc.themeId.uuid,
        arc.characterId.uuid,
        weakness
    )
    runBlocking {
        useCase.invoke(request) {  }
    }
}

fun CharacterArcThens.`should have its psychological weakness as`(expectedWeakness: String) {
    val weakness = runBlocking {
        test.characterArcRepository.getCharacterArcOrError(arc.characterId.uuid, arc.themeId.uuid)
            .arcSections.find { it.template isSameEntityAs PsychologicalWeakness }!!.value
    }
    assertEquals(expectedWeakness, weakness)
}

fun CharacterArcWhens.`has its moral weakness set to`(weakness: String) {
    val useCase = ChangeCharacterMoralWeaknessUseCase(test.themeRepository, test.characterArcRepository)

    val request = ChangeCharacterMoralWeakness.RequestModel(
        arc.themeId.uuid,
        arc.characterId.uuid,
        weakness
    )
    runBlocking {
        useCase.invoke(request) {  }
    }
}

fun CharacterArcThens.`should have its moral weakness as`(expectedWeakness: String) {
    val weakness = runBlocking {
        test.characterArcRepository.getCharacterArcOrError(arc.characterId.uuid, arc.themeId.uuid)
            .arcSections.find { it.template isSameEntityAs MoralWeakness }!!.value
    }
    assertEquals(expectedWeakness, weakness)
}