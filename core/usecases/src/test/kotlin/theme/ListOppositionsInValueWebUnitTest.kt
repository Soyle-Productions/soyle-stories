package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ListOppositionsInValueWebUnitTest {

    private val valueWebId = ValueWeb.Id()
    private val themeId = Theme.Id()

    private var result: Any? = null

    @Test
    fun `value web does not exist`() {
        result = assertThrows<ValueWebDoesNotExist> {
            listOppositionsInValueWeb()
        }
        (result as ValueWebDoesNotExist).valueWebId.mustEqual(valueWebId.uuid)
    }

    @Test
    fun `value web exists`() {
        val theme = makeTheme(themeId, valueWebs = listOf(
            makeValueWeb(
                valueWebId,
                themeId
            )
        ))
        repo.themes[themeId] = theme
        listOppositionsInValueWeb()
        result shouldBe ::emptyResponseModel
    }

    @Test
    fun `value web has oppositions`() {
        val oppositions = List(5) {
            OppositionValue(
                NonBlankString.create(it.toString())!!
            )
        }
        val theme = makeTheme(themeId, valueWebs = listOf(
            makeValueWeb(
                valueWebId,
                themeId,
                oppositions = oppositions
            )
        ))
        repo.themes[themeId] = theme
        listOppositionsInValueWeb()
        result shouldBe responseModel(oppositions)
    }

    @Test
    fun `oppositions have symbolic representations`() {
        val oppositions = List(5) {
            OppositionValue(
                OppositionValue.Id(),
                NonBlankString.create(it.toString())!!,
                List(it * it) {
                    when {
                        it % 2 == 0 -> SymbolicRepresentation(Character.Id().uuid, "Character $it")
                        it % 3 == 0 -> SymbolicRepresentation(Location.Id().uuid, "Location $it")
                        else -> SymbolicRepresentation(Symbol.Id().uuid, "Symbol $it")
                    }
                })
        }
        val theme = makeTheme(themeId, valueWebs = listOf(
            makeValueWeb(
                valueWebId,
                themeId,
                oppositions = oppositions
            )
        ))
        repo.themes[themeId] = theme
        listOppositionsInValueWeb()
        result shouldBe responseModel(oppositions)
    }

    private val repo = ThemeRepositoryDouble()

    private fun listOppositionsInValueWeb()
    {
        val useCase: ListOppositionsInValueWeb = ListOppositionsInValueWebUseCase(repo)
        val output = object : ListOppositionsInValueWeb.OutputPort {
            override suspend fun oppositionsListedInValueWeb(response: OppositionsInValueWeb) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(valueWebId.uuid, output)
        }
    }

    private fun emptyResponseModel(actual: Any?)
    {
        actual as OppositionsInValueWeb
        assertTrue(actual.isEmpty())
    }

    private fun responseModel(expectedOppositions: List<OppositionValue>): (Any?) -> Unit = { actual ->
        actual as OppositionsInValueWeb
        assertEquals(expectedOppositions.map {
            OppositionValueWithSymbols(it.id.uuid, it.name.value, it.representations.map {
                SymbolicItem(it.entityUUID, it.name)
            })
        }, actual.oppositions)
    }

}