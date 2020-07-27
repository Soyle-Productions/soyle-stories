package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.*
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.compareCharacterValues.CharacterValueComparison
import com.soyle.stories.theme.usecases.compareCharacterValues.CompareCharacterValues
import com.soyle.stories.theme.usecases.compareCharacterValues.CompareCharacterValuesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

class CompareCharacterValuesUnitTest {

    private val themeId = Theme.Id()

    private var result: Any? = null

    @Test
    fun `theme doesn't exist`() {
        result = assertThrows<ThemeDoesNotExist> {
            compareCharacterValues()
        }
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `theme has no characters`() {
        givenTheme()
        compareCharacterValues()
        result shouldBe characterValueComparison {
            assertEquals(themeId.uuid, it.themeId)
            assertTrue(it.characters.isEmpty())
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `Theme has characters` {

        val idToCharacter: Map<UUID, CharacterInTheme>

        init {
            givenTheme(4)
            idToCharacter = themeRepository.themes[themeId]!!.characters.associateBy { it.id.uuid }
            compareCharacterValues()
        }

        @Test
        fun `all characters in theme are compared`() {
            result shouldBe characterValueComparison {
                assertEquals(
                    idToCharacter.keys,
                    it.characters.map { it.characterId }.toSet()
                )
            }
        }

        @Test
        fun `check all characters have correct names`() {
            result shouldBe characterValueComparison {
                it.characters.forEach {
                    assertEquals(idToCharacter[it.characterId]!!.name, it.characterName)
                }
            }
        }

        @Test
        fun `check all characters have correct archetypes`() {
            result shouldBe characterValueComparison {
                it.characters.forEach {
                    assertEquals(idToCharacter[it.characterId]!!.archetype, it.characterArchetype)
                }
            }
        }

        @Test
        fun `check characters are labelled as major or minor characters`() {
            result shouldBe characterValueComparison {
                it.characters.forEach {
                    val characterInTheme = idToCharacter[it.characterId]!!
                    when (characterInTheme){
                        is MajorCharacter -> assertTrue(it.isMajorCharacter)
                        else -> assertFalse(it.isMajorCharacter)
                    }
                }
            }
        }

        @Test
        fun `check no characters have values`() {
            result shouldBe characterValueComparison {
                it.characters.forEach {
                    assertTrue(it.characterValues.isEmpty())
                }
            }
        }

        @AfterAll
        fun `make sure result has correct theme id`() {
            result shouldBe characterValueComparison {
                assertEquals(themeId.uuid, it.themeId)
            }
        }
    }

    @Nested
    inner class `Characters are symbolic items in value webs` {

        init {
            givenTheme(3)
            givenValueWebs(
                "Love" to listOf("Hate", "Love"),
                "Greed" to listOf("Generosity", "Greed"),
                "Justice" to listOf("Injustice")
            )
            givenCharactersRepresent(
                setOf("Hate", "Generosity"), // character 1
                setOf("Love", "Greed"), // character 2
                setOf("Injustice") // character 3
            )
            compareCharacterValues()
        }

        @Test
        fun `check character has all oppositions for which they are a representation`() {
            result shouldBe characterValueComparison {
                assertEquals(
                    """
                    ${it.characters[0].characterId}: [(Love) Hate], [(Greed) Generosity]
                    ${it.characters[1].characterId}: [(Love) Love], [(Greed) Greed]
                    ${it.characters[2].characterId}: [(Justice) Injustice]
                """.trimIndent(),
                    it.characters.map {
                        "${it.characterId}: " + it.characterValues.map { "[(${it.valueWebName}) ${it.oppositionName}]" }
                            .joinToString()
                    }.joinToString(separator = "\n")
                )
            }
        }

    }


    private val themeRepository = ThemeRepositoryDouble()

    private fun givenTheme(characterCount: Int = 0) {
        themeRepository.themes[themeId] = makeTheme(themeId, includedCharacters = List(characterCount) {
            val name = "Character ${UUID.randomUUID().toString().takeLast(3)}"
            val archetype = "Archetype [${UUID.randomUUID().toString().takeLast(3)}]"
            if (it % 2 == 0) {
                MinorCharacter(Character.Id(), name, archetype, "", listOf())
            } else {
                MajorCharacter(Character.Id(), name, archetype, "", listOf(), CharacterPerspective(mapOf(), mapOf()), "")
            }
        }.associateBy { it.id })
    }

    private fun givenValueWebs(vararg webs: Pair<String, List<String>>) {
        val theme = themeRepository.themes[themeId]!!
        themeRepository.themes[themeId] = webs.fold(theme) { currentTheme, (webName, oppositions) ->
            currentTheme.withValueWeb(makeValueWeb(name = webName, oppositions = oppositions.map {
                makeOppositionValue(name = it)
            }))
        }
    }

    private fun givenCharactersRepresent(vararg representations: Set<String>) {
        val theme = themeRepository.themes[themeId]!!
        themeRepository.themes[themeId] = representations.withIndex().fold(theme) { it, (index, oppositions) ->
            val character = it.characters.toList()[index]
            oppositions.fold(it) { currentTheme, opposition ->
                val valueWeb = currentTheme.valueWebs.find { it.oppositions.any { it.name == opposition } }!!
                val oppositionValue = valueWeb.oppositions.find { it.name == opposition }!!
                currentTheme.withoutValueWeb(valueWeb.id).withValueWeb(
                    valueWeb.withoutOpposition(oppositionValue.id).withOpposition(
                        oppositionValue.withRepresentation(
                            SymbolicRepresentation(character.id.uuid, character.name)
                        )
                    )
                )
            }
        }
    }

    private fun compareCharacterValues() {
        val useCase: CompareCharacterValues = CompareCharacterValuesUseCase(themeRepository)
        val output = object : CompareCharacterValues.OutputPort {
            override suspend fun charactersCompared(response: CharacterValueComparison) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, output)
        }
    }

    private fun characterValueComparison(assertions: (CharacterValueComparison) -> Unit) = fun(actual: Any?) {
        actual as CharacterValueComparison
        assertions(actual)
    }
}