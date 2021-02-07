package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.renameCharacterArc.RenameCharacterArc
import com.soyle.stories.usecase.character.renameCharacterArc.RenameCharacterArcUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RenameCharacterArcUnitTest {

    private val characterId = UUID.randomUUID()
    private val themeId = UUID.randomUUID()
    private val characterArcName = nonBlankStr("Original Name")

    private var inputName = nonBlankStr("New Name")

    private var updatedArc: CharacterArc? = null

    private val characterRepository = CharacterRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedArc::set)

    private var result: RenameCharacterArc.ResponseModel? = null

    @Test
    fun `character does not exist`() {
        givenNoCharacters()
        val result = assertThrows<CharacterDoesNotExist> {
            whenUseCaseIsExecuted()
        }
        result.characterId.mustEqual(characterId)
    }

    @Test
    fun `character arc does not exist`() {
        given(characterWithId = characterId, andThemeWithId = themeId, andThemeHasCharacter = true)
        val result = assertThrows<CharacterArcDoesNotExist> {
            whenUseCaseIsExecuted()
        }
        result.characterId.mustEqual(characterId)
        result.themeId.mustEqual(themeId)
    }

    @Test
    fun `same name`() {
        given(
            characterWithId = characterId,
            andThemeWithId = themeId,
            andThemeHasCharacter = true,
            andCharacterIsMajorCharacter = true
        )
        inputName = characterArcName
        whenUseCaseIsExecuted()
        assertResultIsResponseModel()
        updatedArc.mustEqual(null) { "Character Arc should not have been updated" }
    }

    @Test
    fun `valid name`() {
        given(
            characterWithId = characterId,
            andThemeWithId = themeId,
            andThemeHasCharacter = true,
            andCharacterIsMajorCharacter = true
        )
        whenUseCaseIsExecuted()
        assertResultIsResponseModel()
        assertThemeHasCharacterWithRenamedCharacterArc()
    }

    private fun givenNoCharacters() = given()
    private fun givenNoThemes() = given(characterWithId = characterId)

    private fun given(
        characterWithId: UUID? = null,
        andThemeWithId: UUID? = null,
        andThemeHasCharacter: Boolean = false,
        andCharacterIsMajorCharacter: Boolean = false
    ) {
        val character =
            characterWithId?.let {
                makeCharacter(
                    Character.Id(characterWithId),
                    Project.Id(),
                    nonBlankStr()
                )
            }?.also(characterRepository::givenCharacter)
        andThemeWithId?.let {
            val theme = makeTheme(Theme.Id(andThemeWithId), name = characterArcName.value)
            if (andThemeHasCharacter) {
                theme.withCharacterIncluded(character!!.id, character.name.value, character.media).let {
                    if (andCharacterIsMajorCharacter) {
                        characterArcRepository.givenCharacterArc(
                            CharacterArc.planNewCharacterArc(
                                character.id,
                                it.id,
                                it.name
                            )
                        )
                        it.withCharacterPromoted(character.id)
                    } else it
                }
            } else theme
        }
    }

    private fun whenUseCaseIsExecuted() {
        val useCase: RenameCharacterArc = RenameCharacterArcUseCase(characterRepository, characterArcRepository)
        val output = object : RenameCharacterArc.OutputPort {
            override fun receiveRenameCharacterArcResponse(response: RenameCharacterArc.ResponseModel) {
                result = response
            }
        }

        runBlocking {
            useCase.invoke(RenameCharacterArc.RequestModel(characterId, themeId, inputName), output)
        }
    }

    private fun assertResultIsResponseModel() {
        val result = result as RenameCharacterArc.ResponseModel
        result.characterId.mustEqual(characterId)
        result.themeId.mustEqual(themeId)
        result.newName.mustEqual(inputName.value)
    }

    private fun assertThemeHasCharacterWithRenamedCharacterArc() {
        val updatedArc = updatedArc!!
        updatedArc.name.mustEqual(inputName.value)
    }

}