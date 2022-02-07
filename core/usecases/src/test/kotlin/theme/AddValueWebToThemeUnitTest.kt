package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.theme.oppositionValue.CharacterAddedToOpposition
import com.soyle.stories.domain.theme.oppositionValue.SymbolicRepresentationAddedToOpposition
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.storyevent.characterDoesNotExist
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToTheme.RequestModel
import com.soyle.stories.usecase.theme.addValueWebToTheme.AddValueWebToThemeUseCase
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class AddValueWebToThemeUnitTest {

    private val themeId = Theme.Id()



    private var updatedTheme: Theme? = null

    private var result: AddValueWebToTheme.ResponseModel? = null
    private val addedValueWeb: ValueWebAddedToTheme?
        get() = result?.addedValueWeb
    private val includedCharacter: CharacterIncludedInTheme?
        get() = result?.includedCharacter
    private val symbolicItemAdded: SymbolicRepresentationAddedToOpposition?
        get() = result?.symbolicItemAdded

    @Test
    fun `theme does not exist`() {
        assertThrows<ThemeDoesNotExist> {
            whenValueWebIsAddedToTheme()
        } shouldBe themeDoesNotExist(themeId.uuid)
        assertNull(updatedTheme)
        assertNull(result)
    }

    @Nested
    inner class `Valid Value Web Name` {

        val name = nonBlankStr("Valid Value Web Name ${UUID.randomUUID()}")

        init {
            givenThemeExists()
            whenValueWebIsAddedToTheme(name)
        }

        @Test
        fun `check value web created correctly`() {
            updatedTheme!! shouldBe {
                assertEquals(themeId, it.id)
                val createdValueWeb = it.valueWebs.single()
                assertEquals(name, createdValueWeb.name)
                val firstOpposition = createdValueWeb.oppositions.single()
                assertEquals(name, firstOpposition.name)
            }
        }

        @Test
        fun `check output`() {
            val actual = addedValueWeb as ValueWebAddedToTheme
            assertEquals(themeId.uuid, actual.themeId)
            val createdValueWeb = updatedTheme!!.valueWebs.single()
            assertEquals(createdValueWeb.id.uuid, actual.valueWebId)
            assertEquals(name.value, actual.valueWebName)
            assertEquals(themeId.uuid, actual.oppositionAddedToValueWeb.themeId)
            assertEquals(createdValueWeb.id.uuid, actual.oppositionAddedToValueWeb.valueWebId)
            assertEquals(createdValueWeb.oppositions.single().id.uuid, actual.oppositionAddedToValueWeb.oppositionValueId)
            assertEquals(name.value, actual.oppositionAddedToValueWeb.oppositionValueName)
            assertFalse(actual.oppositionAddedToValueWeb.needsName)
        }
    }

    @Test
    fun `add another value web`() {
        val existingValueWebCount = 3
        givenThemeExists(existingValueWebCount)
        whenValueWebIsAddedToTheme()
        assertEquals(existingValueWebCount + 1, updatedTheme!!.valueWebs.size)
    }

    @Nested
    inner class `Add Character as Representation` {

        private val characterId = Character.Id()

        @Test
        fun `character doesn't exist`() {
            givenThemeExists()
            assertThrows<CharacterDoesNotExist> {
                whenValueWebIsAddedToTheme(withCharacter = characterId)
            } shouldBe characterDoesNotExist(characterId.uuid)
            assertNull(updatedTheme) { "Should not persist on failure" }
            assertNull(result)
        }

        @Test
        fun `character exists`() {
            givenThemeExists()
            givenCharacterExists()
            whenValueWebIsAddedToTheme(withCharacter = characterId)
            val character = characterRepository.characters[characterId]!!
            val createdValueWeb = updatedTheme!!.valueWebs.single()
            updatedTheme!! shouldBe {
                assertCharacterInTheme(character, it)
                assertValueWebHasSymbolicItem(createdValueWeb, character.id.uuid, character.displayName.value)
            }
            includedCharacter!! shouldBe {
                assertEquals(themeId.uuid, it.themeId)
                assertEquals(updatedTheme!!.name, it.themeName)
                assertEquals(characterId.uuid, it.characterId)
                assertEquals(character.displayName.value, it.characterName)
                assertFalse(it.isMajorCharacter)
            }
            symbolicItemAdded!! shouldBe {
                it as CharacterAddedToOpposition
                assertEquals(themeId.uuid, it.themeId)
                assertEquals(createdValueWeb.id.uuid, it.valueWebId)
                assertEquals(createdValueWeb.name.value, it.valueWebName)
                val firstOpposition = createdValueWeb.oppositions.single()
                assertEquals(firstOpposition.id.uuid, it.oppositionId)
                assertEquals(firstOpposition.name.value, it.oppositionName)
                assertEquals(character.id.uuid, it.characterId)
                assertEquals(character.displayName.value, it.itemName)
            }
        }

        @Test
        fun `character already in theme`() {
            givenCharacterExists()
            givenThemeExists(withCharacterIncluded = characterId)
            whenValueWebIsAddedToTheme(withCharacter = characterId)
            val character = characterRepository.characters[characterId]!!
            val createdValueWeb = updatedTheme!!.valueWebs.single()
            updatedTheme!! shouldBe {
                assertCharacterInTheme(character, it)
                assertValueWebHasSymbolicItem(createdValueWeb, character.id.uuid, character.displayName.value)
            }
            assertNull(includedCharacter)
            symbolicItemAdded!! shouldBe {
                it as CharacterAddedToOpposition
                assertEquals(themeId.uuid, it.themeId)
                assertEquals(createdValueWeb.id.uuid, it.valueWebId)
                assertEquals(createdValueWeb.name.value, it.valueWebName)
                val firstOpposition = createdValueWeb.oppositions.single()
                assertEquals(firstOpposition.id.uuid, it.oppositionId)
                assertEquals(firstOpposition.name.value, it.oppositionName)
                assertEquals(character.id.uuid, it.characterId)
                assertEquals(character.displayName.value, it.itemName)
            }
        }

        private fun givenCharacterExists() {
            characterRepository.characters[characterId] = makeCharacter(characterId)
        }

    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })
    private val characterRepository = CharacterRepositoryDouble()

    private fun givenThemeExists(valueWebCount: Int = 0, withCharacterIncluded: Character.Id? = null)
    {
        themeRepository.themes[themeId] = makeTheme(themeId, valueWebs = List(valueWebCount) {
            ValueWeb(themeId, nonBlankStr("Value Web $it"))
        })
        if (withCharacterIncluded != null) {
            val character = characterRepository.characters[withCharacterIncluded]!!
            themeRepository.themes[themeId] = themeRepository.themes[themeId]!!.withCharacterIncluded(
                character.id,
                character.displayName.value,
                character.media
            )
        }
    }

    private fun whenValueWebIsAddedToTheme(name: NonBlankString = nonBlankStr(), withCharacter: Character.Id? = null)
    {
        val useCase: AddValueWebToTheme = AddValueWebToThemeUseCase(themeRepository, characterRepository)
        val output = object : AddValueWebToTheme.OutputPort {
            override suspend fun addedValueWebToTheme(response: AddValueWebToTheme.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(RequestModel(themeId.uuid, name, withCharacter?.let { CharacterId(it.uuid) }), output)
        }
    }

    private fun assertCharacterInTheme(character: Character, theme: Theme)
    {
        assertTrue(theme.containsCharacter(character.id)) { "Theme does not contain character" }
        val characterInTheme = theme.getIncludedCharacterById(character.id)!!
        assertEquals(character.displayName.value, characterInTheme.name)
    }

    private fun assertValueWebHasSymbolicItem(valueWeb: ValueWeb, itemId: UUID, name: String)
    {
        val opposition = valueWeb.oppositions.single {
            it.hasEntityAsRepresentation(itemId)
        }
        val representation = opposition.representations.single {
            it.entityUUID == itemId
        }
        assertEquals(name, representation.name)
    }

}