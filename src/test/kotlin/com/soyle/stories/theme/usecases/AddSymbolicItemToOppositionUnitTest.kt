package com.soyle.stories.theme.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.locationDoesNotExist
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.addSymbolicItemToOpposition.*
import com.soyle.stories.theme.usecases.removeSymbolicItem.RemovedSymbolicItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class AddSymbolicItemToOppositionUnitTest {

    private val themeId = Theme.Id()
    private val valueWebId = ValueWeb.Id()
    private val oppositionId = OppositionValue.Id()

    private var updatedTheme: Theme? = null
    private var result: Any? = null
    private var removedItem: RemovedSymbolicItem? = null

    @Nested
    inner class `Add Character to Opposition` {

        private val characterId = Character.Id()
        private val characterName = "Character Name ${UUID.randomUUID().toString().takeLast(3)}"

        @Test
        fun `opposition value does not exist`() {
            assertThrowsOppositionValueDoesNotExist {
                addCharacterToOpposition()
            }
        }

        @Test
        fun `character does not exist`() {
            givenOppositionValue()
            result = assertThrows<CharacterDoesNotExist> {
                addCharacterToOpposition()
            }
            result shouldBe characterDoesNotExist(characterId.uuid)
        }

        @Test
        fun `opposition and character exist`() {
            givenOppositionValue()
            givenCharacter(characterId, characterName)
            addCharacterToOpposition()
            updatedTheme shouldBe ::themeWithCharacterAsSymbol
            result shouldBe ::characterAddedToOpposition
        }

        @Test
        fun `character already in theme`() {
            givenOppositionValue()
            givenCharacter(characterId, characterName, inTheme = true)
            addCharacterToOpposition()
            updatedTheme shouldBe ::themeWithCharacterAsSymbol
            result shouldBe ::characterAddedToOpposition
        }

        @Test
        fun `character already represents another opposition in value web`() {
            val preExistingOppositionId = OppositionValue.Id()
            givenCharacter(characterId, characterName)
            givenThemes(
                makeTheme(
                    themeId, valueWebs = listOf(
                        makeValueWeb(
                            valueWebId, themeId, oppositions = listOf(
                                makeOppositionValue(
                                    preExistingOppositionId,
                                    representations = listOf(SymbolicRepresentation(characterId.uuid, characterName))
                                ),
                                makeOppositionValue(oppositionId)
                            )
                        )
                    )
                )
            )
            addCharacterToOpposition()
            updatedTheme shouldBe ::themeWithCharacterAsSymbol
            result shouldBe ::characterAddedToOpposition
            removedItem shouldBe removedSymbolicItem(
                themeId.uuid,
                valueWebId.uuid,
                preExistingOppositionId.uuid,
                characterId.uuid
            )
        }

        private fun addCharacterToOpposition() {
            addSymbolicItemToOpposition { output ->
                addCharacterAsSymbol(oppositionId.uuid, characterId.uuid, output)
            }
        }

        private fun themeWithCharacterAsSymbol(actual: Any?) {
            actual shouldBe themeWithSymbolicRepresentation(characterId.uuid, characterName)
            actual shouldBe themeWithCharacterIncluded(characterId, characterName)
        }

        private fun characterAddedToOpposition(actual: Any?) {
            actual as CharacterAddedToOpposition
            actual shouldBe symbolicRepresentationAddedToOpposition(characterId.uuid, characterName)
        }

    }

    @Nested
    inner class `Add Location to Opposition` {

        private val locationId = Location.Id()
        private val locationName = "Location Name ${UUID.randomUUID().toString().takeLast(3)}"

        @Test
        fun `opposition value does not exist`() {
            assertThrowsOppositionValueDoesNotExist {
                addLocationToOpposition()
            }
        }

        @Test
        fun `location does not exist`() {
            givenOppositionValue()
            result = assertThrows<LocationDoesNotExist> {
                addLocationToOpposition()
            }
            result shouldBe locationDoesNotExist(locationId.uuid)
        }

        @Test
        fun `opposition and location exist`() {
            givenOppositionValue()
            givenLocation(locationId, locationName)
            addLocationToOpposition()
            updatedTheme shouldBe ::themeWithLocationAsSymbol
            result shouldBe ::locationAddedToOpposition
        }

        private fun addLocationToOpposition() {
            addSymbolicItemToOpposition { output ->
                addLocationAsSymbol(oppositionId.uuid, locationId.uuid, output)
            }
        }

        private fun themeWithLocationAsSymbol(actual: Any?) {
            actual shouldBe themeWithSymbolicRepresentation(locationId.uuid, locationName)
        }

        private fun locationAddedToOpposition(actual: Any?) {
            actual as LocationAddedToOpposition
            actual shouldBe symbolicRepresentationAddedToOpposition(locationId.uuid, locationName)
        }

    }

    @Nested
    inner class `Add Symbol to Opposition` {

        private val symbolId = Symbol.Id()
        private val symbolName = "Symbol Name ${UUID.randomUUID().toString().takeLast(3)}"

        @Test
        fun `opposition value does not exist`() {
            assertThrowsOppositionValueDoesNotExist {
                addSymbolToOpposition()
            }
        }

        @Test
        fun `symbol does not exist`() {
            givenOppositionValue()
            result = assertThrows<SymbolDoesNotExist> {
                addSymbolToOpposition()
            }
            result shouldBe symbolDoesNotExist(symbolId.uuid)
        }

        @Test
        fun `symbol exists in different theme`() {
            givenOppositionValue()
            givenSymbol(symbolId, symbolName)
            result = assertThrows<SymbolDoesNotExist> {
                addSymbolToOpposition()
            }
            result shouldBe symbolDoesNotExist(symbolId.uuid)
        }

        @Test
        fun `opposition and symbol exist`() {
            givenOppositionValue()
            givenSymbol(symbolId, symbolName, inSameTheme = true)
            addSymbolToOpposition()
            updatedTheme shouldBe ::themeWithSymbolAsSymbol
            result shouldBe ::symbolAddedToOpposition
        }

        private fun addSymbolToOpposition() {
            addSymbolicItemToOpposition { output ->
                addSymbolAsSymbol(oppositionId.uuid, symbolId.uuid, output)
            }
        }

        private fun themeWithSymbolAsSymbol(actual: Any?) {
            actual shouldBe themeWithSymbolicRepresentation(symbolId.uuid, symbolName)
        }

        private fun symbolAddedToOpposition(actual: Any?) {
            actual as SymbolAddedToOpposition
            actual shouldBe symbolicRepresentationAddedToOpposition(symbolId.uuid, symbolName)
        }

    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = { updatedTheme = it })
    private val characterRepository = CharacterRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    private fun givenThemes(vararg themes: Theme) {
        themes.forEach {
            themeRepository.themes[it.id] = it
        }
    }

    private fun givenOppositionValue() {
        themeRepository.themes[themeId] = makeTheme(
            themeId, valueWebs = listOf(
                makeValueWeb(
                    valueWebId, oppositions = listOf(
                        makeOppositionValue(oppositionId)
                    )
                )
            )
        )
    }

    private fun givenCharacter(characterId: Character.Id, name: String, inTheme: Boolean = false) {
        val character = makeCharacter(characterId, Project.Id(), name)
        characterRepository.characters[characterId] = character
        if (inTheme) {
            themeRepository.themes[themeId]!!.withCharacterIncluded(character.id, character.name, character.media)
                .let {
                    themeRepository.themes[themeId] = it
                }
        }
    }

    private fun givenLocation(locationId: Location.Id, name: String) {
        locationRepository.locations[locationId] = Location(locationId, Project.Id(), name)
    }

    private fun givenSymbol(symbolId: Symbol.Id, name: String, inSameTheme: Boolean = false) {
        val symbol = Symbol(symbolId, name)
        if (inSameTheme) {
            val theme = themeRepository.themes[themeId]!!
            themeRepository.themes[themeId] = theme.withSymbol(symbol)
        } else {
            makeTheme(symbols = listOf(symbol)).let {
                themeRepository.themes[it.id] = it
            }
        }
    }

    private fun addSymbolicItemToOpposition(block: suspend AddSymbolicItemToOpposition.(AddSymbolicItemToOpposition.OutputPort) -> Unit) {
        val useCase: AddSymbolicItemToOpposition =
            AddSymbolicItemToOppositionUseCase(themeRepository, characterRepository, locationRepository)
        val output = object : AddSymbolicItemToOpposition.OutputPort {
            override suspend fun addedSymbolicItemToOpposition(response: AddSymbolicItemToOpposition.ResponseModel) {
                result = response.addedSymbolicItem
                removedItem = response.removedSymbolicItems.singleOrNull()
            }
        }
        runBlocking {
            useCase.block(output)
        }
    }

    private fun assertThrowsOppositionValueDoesNotExist(block: () -> Unit) {
        result = assertThrows<OppositionValueDoesNotExist> {
            block()
        }
        result shouldBe oppositionValueDoesNotExist(oppositionId.uuid)
    }

    private fun themeWithSymbolicRepresentation(entityUUID: UUID, entityName: String) = fun(actual: Any?): Unit {
        actual as Theme
        assertEquals(themeId, actual.id)
        val updatedValueWeb = actual.valueWebs.find { it.id == valueWebId }!!
        val updatedOpposition = updatedValueWeb.oppositions.find { it.id == oppositionId }!!
        val representation = updatedOpposition.representations.find {
            it.entityUUID == entityUUID
        }!!
        assertEquals(entityName, representation.name)
    }

    private fun themeWithCharacterIncluded(characterId: Character.Id, characterName: String) = fun(actual: Any?) {
        actual as Theme
        assertEquals(themeId, actual.id)
        assertEquals(
            characterName,
            actual.getMinorCharacterById(characterId)!!.name
        )

    }

    private fun symbolicRepresentationAddedToOpposition(itemUUID: UUID, itemName: String): (Any?) -> Unit = { actual ->
        actual as SymbolicRepresentationAddedToOpposition
        assertEquals(themeId.uuid, actual.themeId)
        assertEquals(valueWebId.uuid, actual.valueWebId)
        assertEquals(oppositionId.uuid, actual.oppositionId)
        assertEquals(itemUUID, actual.itemId())
        assertEquals(itemName, actual.itemName)
    }

}