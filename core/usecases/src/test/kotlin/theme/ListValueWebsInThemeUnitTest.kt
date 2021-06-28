package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.listValueWebsInTheme.ListValueWebsInTheme
import com.soyle.stories.usecase.theme.listValueWebsInTheme.ListValueWebsInThemeUseCase
import com.soyle.stories.usecase.theme.listValueWebsInTheme.ValueWebList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class ListValueWebsInThemeUnitTest {

    private val themeId = Theme.Id()

    private var result: Any? = null

    @Test
    fun `theme does not exist`() {
        whenValueWebsAreListedInTheme()
        result shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `no value webs in theme`() {
        givenThemeExists()
        whenValueWebsAreListedInTheme()
        result shouldBe ::emptyResponseModel
    }

    @Test
    fun `some value webs in theme`() {
        givenThemeExists(valueWebs = listOf("A", "E", "R", "O"))
        whenValueWebsAreListedInTheme()
        result shouldBe responseModel("A", "E", "R", "O")
    }

    private val themeRepository = ThemeRepositoryDouble()
    private val uuidToIdentifier = mutableMapOf<UUID, String>()

    private fun givenThemeExists(valueWebs: List<String> = emptyList())
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = valueWebs.map {
            val web = ValueWeb(themeId, nonBlankStr(it))
            uuidToIdentifier[web.id.uuid] = it
            web
        })
    }

    private fun identifier(uuid: UUID): String = uuidToIdentifier.getValue(uuid)

    private fun whenValueWebsAreListedInTheme()
    {
        val useCase: ListValueWebsInTheme = ListValueWebsInThemeUseCase(themeRepository)
        val output = object : ListValueWebsInTheme.OutputPort {
            override suspend fun valueWebsListedInTheme(response: ValueWebList) {
                result = response
            }
        }
        runBlocking {
            try {
                useCase.invoke(themeId.uuid, output)
            } catch (t: Throwable) { result = t }
        }
    }

    private fun emptyResponseModel(actual: Any?)
    {
        actual as ValueWebList
        assertTrue(actual.isEmpty())
    }

    private fun responseModel(vararg expectedValueWebs: String): (Any?) -> Unit = { actual ->
        actual as ValueWebList
        assertEquals(expectedValueWebs.toSet(), actual.valueWebs.map { identifier(it.valueWebId) }.toSet()) {
            "Expected ids not in output"
        }
        actual.valueWebs.forEach {
            assertEquals(identifier(it.valueWebId), it.valueWebName)
        }
    }

}