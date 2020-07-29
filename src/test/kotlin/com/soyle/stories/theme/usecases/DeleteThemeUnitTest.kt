package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.entities.*
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteThemeUseCase
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

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
                val og = makeCharacter(media = Media.Id().takeIf { i % 2 == 0 })
                List(i) {Unit}.fold(og) { character, _ ->
                    character.withCharacterArc("Arc ${UUID.randomUUID().toString().take(3)}", Theme.Id())
                }.withCharacterArc("Arc ${UUID.randomUUID().toString().take(3)}", themeId)
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
            fun `check character arcs deleted`() {
                assertEquals(majorCharacters.size, characterRepository.updatedCharacters.size)
                val majorCharactersById = majorCharacters.associateBy { it.id }
                characterRepository.updatedCharacters.forEach {
                    val character = majorCharactersById.getValue(it.id)
                    assertEquals(character.name, it.name)
                    assertEquals(character.media, it.media)
                    assertEquals(character.characterArcs.size - 1, it.characterArcs.size)
                }
            }

            @Test
            fun `check deleted character arcs were output`() {
                val deletedArcs = deletedCharacterArcs!!.associateBy { it.characterId }
                majorCharacters.forEach {
                    val arc = it.characterArcs.single { it.themeId == themeId }
                    val deletedArc = deletedArcs.getValue(it.id.uuid)
                    assertEquals(themeId.uuid, deletedArc.themeId)
                }
            }

            private fun givenThemeHasMajorCharacter(character: Character) {
                characterRepository.characters[character.id] = character
                themeRepository.themes[themeId] = themeRepository.themes[themeId]!!.withCharacterIncluded(
                    character.id, character.name, character.media
                ).withCharacterPromoted(character.id)
            }

            private fun givenThemeHasMinorCharacter() {
                val character = makeCharacter()
                characterRepository.characters[character.id] = character
                themeRepository.themes[themeId] = themeRepository.themes[themeId]!!.withCharacterIncluded(
                    character.id, character.name, character.media
                )
            }

        }

    }

    private val themeRepository = ThemeRepositoryDouble(onDeleteTheme = { deletedTheme = it })
    private val characterRepository = CharacterRepositoryDouble()

    private fun givenThemeExists()
    {
        themeRepository.themes[themeId] = makeTheme(themeId)
    }

    private fun whenThemeIsDeleted()
    {
        val useCase: DeleteTheme = DeleteThemeUseCase(themeRepository, characterRepository)
        val output = object : DeleteTheme.OutputPort {
            override fun themeDeleted(response: DeletedTheme) {
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