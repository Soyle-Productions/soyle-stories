package com.soyle.stories.usecase.character.arc.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.character.CrossDomainCharacterScope.Companion.`given the character`
import com.soyle.stories.usecase.character.`has a character`
import com.soyle.stories.usecase.character.arc.`has a character arc`
import com.soyle.stories.usecase.character.arc.`then the character arc`
import com.soyle.stories.usecase.character.arc.`when the character arc`
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.usecase.character.arc.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.usecase.character.arc.section.`has its moral weakness set to`
import com.soyle.stories.usecase.character.arc.section.`has its psychological weakness set to`
import com.soyle.stories.usecase.character.arc.section.`should have its moral weakness as`
import com.soyle.stories.usecase.character.arc.section.`should have its psychological weakness as`
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeakness
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangeCharacterPsychologicalWeaknessUseCase
import com.soyle.stories.usecase.framework.CrossDomainTest
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.project.`given a project has been started`
import com.soyle.stories.usecase.project.`given the project`
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class `Character Arc Integrates with Theme Test` : CrossDomainTest() {

    private val project = `given a project has been started`()
    private val character = `given the project`(project).`has a character`()

    @Test
    fun `Provide Prospective Character with Psychological Weakness`() {
        val arc = `given the character`(character).`has a character arc`()

        `when the character arc`(arc).`has its psychological weakness set to`("laziness")

        `then the character arc`(arc).`should have its psychological weakness as`("laziness")
    }

    @Test
    fun `Provide Prospective Character with Moral Weakness`() {
        val arc = `given the character`(character).`has a character arc`()

        `when the character arc`(arc).`has its moral weakness set to`("evilness")

        `then the character arc`(arc).`should have its moral weakness as`("evilness")
    }

}