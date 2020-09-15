package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.entities.*
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PromoteCharacterTest {

    // preconditions
    private val themeName = "Theme ${str()}"

    // provided data
    private val themeId = Theme.Id()
    private val characterId = Character.Id()

    // persisted data
    private var updatedTheme: Theme? = null
    private var createdCharacterArc: CharacterArc? = null
    private var createdArcSections: List<CharacterArcSection>? = null

    // output data
    private var result: PromoteMinorCharacter.ResponseModel? = null

    @Nested
    inner class Degenerates {

        @Test
        fun `theme doesn't exist`() {
            degenerate<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId.uuid)
        }

        @Test
        fun `character not in theme`() {
            givenTheme()
            degenerate<CharacterNotInTheme>() shouldBe characterNotInTheme(themeId.uuid, characterId.uuid)
        }

        @Test
        fun `character already promoted`() {
            givenTheme()
            givenThemeHasCharacter(characterId, asMajorCharacter = true)
            degenerate<CharacterIsAlreadyMajorCharacterInTheme>() shouldBe
                    characterIsAlreadyMajorCharacterInTheme(themeId.uuid, characterId.uuid)
        }

        @Test
        fun `character arc already exists`() {
            givenTheme()
            givenThemeHasCharacter(characterId, asMajorCharacter = false)
            givenCharacterArcExists()
            degenerate<CharacterArcAlreadyExistsForCharacterInTheme>() shouldBe {
                assertEquals(themeId.uuid, it.themeId)
                assertEquals(characterId.uuid, it.characterId)
            }
        }

        private inline fun <reified T : Throwable> degenerate(): T {
            val t = assertThrows<T> {
                promoteMinorCharacter()
            }
            assertNull(updatedTheme)
            assertNull(createdCharacterArc)
            assertNull(createdArcSections)
            assertNull(result)
            return t
        }

    }

    @Test
    fun `character is minor character`() {
        givenTheme()
        givenThemeHasCharacter(characterId, asMajorCharacter = false)
        promoteMinorCharacter()
        updatedTheme!! shouldBe themeWithCharacterPromoted(characterId)
        createdCharacterArc!! shouldBe characterArc(themeId, characterId, themeName)
        createdCharacterArc!!.arcSections shouldBe listOfArcSections(themeId, characterId)
        result!! shouldBe responseModel(themeId, characterId, themeName)
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })
    private val characterArcRepository = CharacterArcRepositoryDouble(onAddNewCharacterArc = {
        createdCharacterArc = it
    })
    private val characterArcSectionRepository = CharacterArcSectionRepositoryDouble(onAddNewCharacterArcSections = {
        createdArcSections = it
    })

    private fun givenTheme() {
        themeRepository.themes[themeId] = makeTheme(themeId, name = themeName)
    }

    private fun givenThemeHasCharacter(characterId: Character.Id, asMajorCharacter: Boolean) {
        themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId)
            .withCharacterIncluded(characterId, "", null)
            .let {
                if (asMajorCharacter) it.withCharacterPromoted(characterId)
                else it
            }
    }

    private fun givenCharacterArcExists() {
        characterArcRepository.givenCharacterArc(
            CharacterArc.planNewCharacterArc(characterId, themeId, "")
        )

    }


    private fun promoteMinorCharacter() {
        val useCase: PromoteMinorCharacter = PromoteMinorCharacterUseCase(
            themeRepository, characterArcRepository
        )
        val output = object : PromoteMinorCharacter.OutputPort {
            override suspend fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(
                PromoteMinorCharacter.RequestModel(themeId.uuid, characterId.uuid),
                output
            )
        }
    }

    private fun themeWithCharacterPromoted(characterId: Character.Id) = fun(actual: Theme) {
        assertNotNull(actual.getMajorCharacterById(characterId))
    }

    private fun characterArc(themeId: Theme.Id, characterId: Character.Id, name: String) = fun(actual: CharacterArc) {
        assertEquals(themeId, actual.themeId)
        assertEquals(characterId, actual.characterId)
        assertEquals(name, actual.name)
    }

    private fun listOfArcSections(themeId: Theme.Id, characterId: Character.Id) =
        fun(actual: List<CharacterArcSection>) {
            actual.forEach {
                assertEquals(themeId, it.themeId)
                assertEquals(characterId, it.characterId)
                assertNull(it.linkedLocation)
                assertEquals("", it.value)
            }
        }

    private fun responseModel(themeId: Theme.Id, characterId: Character.Id, name: String) =
        fun(actual: PromoteMinorCharacter.ResponseModel) {
            assertEquals(themeId.uuid, actual.createdCharacterArc.themeId)
            assertEquals(characterId.uuid, actual.createdCharacterArc.characterId)
            assertEquals(name, actual.createdCharacterArc.characterArcName)
        }
}