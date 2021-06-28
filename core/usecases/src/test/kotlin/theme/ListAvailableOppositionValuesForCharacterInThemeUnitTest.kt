package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableValueWebForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInThemeUseCase
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.OppositionValuesAvailableForCharacterInTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ListAvailableOppositionValuesForCharacterInThemeUnitTest {

    private val themeId = Theme.Id()
    private val characterId = Character.Id()

    private var result: OppositionValuesAvailableForCharacterInTheme? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            listAvailableOppositionValuesForCharacterInTheme()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `character isn't in theme`() {
        givenTheme()
        assertThrows<CharacterNotInTheme> {
            listAvailableOppositionValuesForCharacterInTheme()
        } shouldBe characterNotInTheme(themeId.uuid, characterId.uuid)
    }

    @Test
    fun `no value webs in theme`() {
        givenTheme(hasCharacter = true)
        listAvailableOppositionValuesForCharacterInTheme()
        result shouldBe {
            it!!
            assertTrue(it.isEmpty())
        }
    }

    @Nested
    inner class `Theme has value webs` {

        private val expectedValueWebs = List(5) { makeValueWeb(themeId = themeId) }

        init {
            givenTheme(hasCharacter = true)
            givenThemeHasValueWebs(expectedValueWebs)
        }

        @AfterEach
        fun `check output has proper ids`() {
            result shouldBe {
                it!!
                assertEquals(expectedValueWebs.size, it.size)
                assertEquals(themeId.uuid, it.themeId)
                assertEquals(characterId.uuid, it.characterId)
            }
        }

        @AfterEach
        fun `check all value webs are in output`() {
            result shouldBe {
                it!!
                val valueWebsById = expectedValueWebs.associateBy { it.id.uuid }
                it.forEach {
                    val valueWeb = valueWebsById.getValue(it.valueWebId)
                    assertEquals(valueWeb.name.value, it.valueWebName)
                }
            }
        }

        @Test
        fun `value webs with no oppositions`() {
            listAvailableOppositionValuesForCharacterInTheme()
            result shouldBe {
                it!!
                it.forEach {
                    assertTrue(it.isEmpty())
                }
            }
        }

        @Nested
        inner class `Value Webs have opposition values` {

            private val expectedOppositionValues: Map<ValueWeb.Id, List<OppositionValue>>

            init {
                val valueWebsWithOpposiitonValues = expectedValueWebs.map {
                    List((1..6).random()) { Unit }.fold(it) { a, _ ->
                        a.withOpposition(nonBlankStr("Opposition Value ${str()}"))
                    }
                }
                givenValueWebsHaveOppositions(valueWebsWithOpposiitonValues)
                expectedOppositionValues = valueWebsWithOpposiitonValues.associate {
                    it.id to it.oppositions
                }

            }

            @Test
            fun `check all opposition values in output`() {
                listAvailableOppositionValuesForCharacterInTheme()
                result shouldBe {
                    it!!
                    it.forEach {
                        val oppositions = expectedOppositionValues.getValue(ValueWeb.Id(it.valueWebId))
                        assertEquals(oppositions.size, it.size)
                        val oppositionsById = oppositions.associateBy { it.id.uuid }
                        it.forEach {
                            val opposition = oppositionsById.getValue(it.oppositionValueId)
                            assertEquals(opposition.name, it.oppositionValueName)
                        }
                    }
                }
            }

            @Test
            fun `character is representative of some opposition values`() {
                val opsWithRepresentation = expectedOppositionValues.entries.take(2).map { it.key to it.value.first() }
                givenCharacterRepresentsOppositionValues(opsWithRepresentation)
                listAvailableOppositionValuesForCharacterInTheme()
                result!! shouldBe {
                    assertEquals(opsWithRepresentation.size, it.filter { it.characterRepresentsAnOpposition }.size)
                    val opsWithRepresentationByWebId = opsWithRepresentation.toMap()
                    it.filter(AvailableValueWebForCharacterInTheme::characterRepresentsAnOpposition).forEach {
                        val opposition = opsWithRepresentationByWebId.getValue(ValueWeb.Id(it.valueWebId))
                        assertNull(it.find { it.oppositionValueId == opposition.id.uuid }) { "Should not include the opposition value this character represents" }
                        assertEquals(opposition.id.uuid, it.oppositionCharacterRepresents!!.oppositionValueId)
                        assertEquals(opposition.name, it.oppositionCharacterRepresents!!.oppositionValueName)
                    }
                }
            }

        }

    }

    private val themeRepository = ThemeRepositoryDouble()

    private fun givenTheme(hasCharacter: Boolean = false) {
        themeRepository.themes[themeId] = makeTheme(themeId)
        if (hasCharacter) {
            themeRepository.themes[themeId] =
                themeRepository.themes[themeId]!!.withCharacterIncluded(characterId, "", null)
        }
    }

    private fun givenThemeHasValueWebs(valueWebs: List<ValueWeb>) {
        themeRepository.themes[themeId] = valueWebs.fold(themeRepository.themes[themeId]!!) { theme, web ->
            theme.withValueWeb(web)
        }
    }

    private fun givenValueWebsHaveOppositions(valueWebs: List<ValueWeb>) {
        themeRepository.themes[themeId] =
            valueWebs.fold(themeRepository.themes[themeId]!!) { theme, web ->
                theme.withoutValueWeb(web.id).withValueWeb(web)
            }
    }

    private fun givenCharacterRepresentsOppositionValues(oppositions: List<Pair<ValueWeb.Id, OppositionValue>>) {
        themeRepository.themes[themeId] =
            oppositions.fold(themeRepository.themes[themeId]!!) { theme, (webId, op) ->
                val valueWeb = theme.valueWebs.find { it.id == webId }!!
                theme.withoutValueWeb(webId)
                    .withValueWeb(
                        valueWeb.withRepresentationOf(SymbolicRepresentation(characterId.uuid, ""), op.id)
                    )
            }
    }

    private fun listAvailableOppositionValuesForCharacterInTheme() {
        val useCase: ListAvailableOppositionValuesForCharacterInTheme =
            ListAvailableOppositionValuesForCharacterInThemeUseCase(themeRepository)
        val output = object : ListAvailableOppositionValuesForCharacterInTheme.OutputPort {
            override suspend fun availableOppositionValuesListedForCharacterInTheme(response: OppositionValuesAvailableForCharacterInTheme) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, characterId.uuid, output)
        }
    }

}