package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.arc.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.deleteTheme.DeleteTheme
import com.soyle.stories.usecase.theme.deleteTheme.DeleteThemeUseCase
import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class DeleteThemeUnitTest {

    private val themeId = Theme.Id()

    private var deletedTheme: Theme? = null
    private var result: Any? = null

    private var deletedCharacterArcs: List<DeletedCharacterArc>? = null

    @Test
    fun `theme does not exist`() {
        whenThemeIsDeleted()
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Nested
    inner class `Theme Exists` {

        init {
            givenThemeExists()
        }

        @Test
        fun `check theme was deleted`() {
            whenThemeIsDeleted()
            assertNotNull(deletedTheme)
        }

        @Test
        fun `check result represents deleted theme`() {
            whenThemeIsDeleted()
            val actual = result as DeletedTheme
            assertEquals(themeId.uuid, actual.themeId)
        }

        @Test
        fun `no character arcs deleted`() {
            whenThemeIsDeleted()
            assertNull(deletedCharacterArcs)
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class `Theme has Major Characters` {

            private val majorCharacters = List(4) { i ->
                makeCharacter(media = Media.Id().takeIf { i % 2 == 0 })
            }
            private val minorCharacterCount = 2

            init {
                majorCharacters.forEach(::givenThemeHasMajorCharacter)
                repeat(minorCharacterCount) {
                    givenThemeHasMinorCharacter()
                }
                whenThemeIsDeleted()
            }

            @Test
            fun `check deleted character arcs were output`() {
                val deletedArcs = deletedCharacterArcs!!.associateBy { it.characterId }
                majorCharacters.forEach {
                    val deletedArc = deletedArcs.getValue(it.id.uuid)
                    assertEquals(themeId.uuid, deletedArc.themeId)
                }
            }

            private fun givenThemeHasMajorCharacter(character: Character) {
                characterRepository.characters[character.id] = character
                themeRepository.themes[themeId] = themeRepository.themes[themeId]!!.withCharacterIncluded(
                    character.id, character.name.value, character.media
                ).withCharacterPromoted(character.id)
                characterArcRepository.givenCharacterArc(CharacterArc.planNewCharacterArc(character.id, themeId, themeRepository.themes[themeId]!!.name))
            }

            private fun givenThemeHasMinorCharacter() {
                val character = makeCharacter()
                characterRepository.characters[character.id] = character
                themeRepository.themes[themeId] = themeRepository.themes[themeId]!!.withCharacterIncluded(
                    character.id, character.name.value, character.media
                )
            }

        }

    }

    private val themeRepository = ThemeRepositoryDouble(onDeleteTheme = { deletedTheme = it })
    private val characterRepository = CharacterRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble()

    private fun givenThemeExists()
    {
        themeRepository.themes[themeId] = makeTheme(themeId)
    }

    private fun whenThemeIsDeleted()
    {
        val useCase: DeleteTheme = DeleteThemeUseCase(themeRepository, characterArcRepository)
        val output = object : DeleteTheme.OutputPort {
            override suspend fun themeDeleted(response: DeletedTheme) {
                result = response
            }

            override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
                deletedCharacterArcs = response
            }
        }
        runBlocking {
            try { useCase.invoke(themeId.uuid, output) }
            catch (t: Throwable) { result = t }
        }
    }

    private fun deletedTheme(actual: Any?)
    {
        actual as DeletedTheme
        assertEquals(themeId.uuid, actual.themeId)
    }

}