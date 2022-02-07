package com.soyle.stories.usecase.character

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.character.buildNewCharacter.CharacterCreated
import com.soyle.stories.usecase.project.exceptions.ProjectDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ProjectRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInScene
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

/**
 * Creates a new character in the provided project
 */
class `Build New Character Unit Test` {

 // Pre-Conditions

    /** The [project] must exist */
    private val project = Project(Project.Id(), nonBlankStr("The Greatest Story Ever Told"))

 // Post-Conditions

    /** A new [createdCharacter] must be added to the [project] */
    private var createdCharacter: Character? = null

 // Wiring

    private val projectRepository = ProjectRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble(onAddNewCharacter = ::createdCharacter::set)

 // Tests

    @Nested
    inner class `Project Must Exist` {

        @Test
        fun `given project does not exist - should return error`() {
            val error = buildNewCharacter().exceptionOrNull()

            error.shouldBeEqualTo(ProjectDoesNotExist(project.id))
        }

    }

    @Test
    fun `should create new character`() {
        projectRepository.givenProject(project)

        val inputName = characterName()
        buildNewCharacter(inputName)

        createdCharacter!!.let { createdCharacter ->
            createdCharacter.projectId.shouldBeEqualTo(project.id)
            createdCharacter.displayName.shouldBeEqualTo(inputName)
        }
    }

    @Test
    fun `should output character created response`() {
        projectRepository.givenProject(project)

        val characterCreated: CharacterCreated = buildNewCharacter().getOrThrow()

        characterCreated.projectId.shouldBeEqualTo(project.id)
        characterCreated.characterId.shouldBeEqualTo(createdCharacter!!.id)
        characterCreated.name.shouldBeEqualTo(createdCharacter!!.displayName.value)
    }

    private fun buildNewCharacter(name: NonBlankString = characterName()): Result<CharacterCreated> = runBlocking {
        val useCase: BuildNewCharacter = BuildNewCharacterUseCase(projectRepository, characterRepository)
        var result: Result<CharacterCreated> = Result.failure(Error("No response received"))
        useCase(project.id, name) {
            result = Result.success(it)
        }.mapCatching { result.getOrThrow() }
    }

}