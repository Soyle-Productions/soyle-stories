package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.component1
import com.soyle.stories.common.component2
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.makeLocation
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.EntitiesAvailableToAddToOpposition
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOpposition
import com.soyle.stories.theme.usecases.listAvailableEntitiesToAddToOpposition.ListAvailableEntitiesToAddToOppositionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ListAvailableEntitiesToAddToOppositionUnitTest {

    private val projectId = Project.Id()
    private val oppositionId = OppositionValue.Id()

    private var result: Any? = null

    @Test
    fun `opposition id must represent existing opposition value`() {
        result = assertThrows<OppositionValueDoesNotExist> {
            listAvailableEntitiesToAddToOpposition()
        }
        result shouldBe oppositionValueDoesNotExist(oppositionId.uuid)
    }

    @Nested
    inner class `Should list characters` {

        private var expectedCharacterIds: Set<UUID> = setOf()

        @AfterEach
        fun test() {
            listAvailableEntitiesToAddToOpposition()
            result shouldBe entitiesAvailableToAddToOpposition(characterIds = expectedCharacterIds)
        }

        @Test
        fun `characters not in same project should not be listed`() {
            givenOppositionExists()
            givenANumberOfCharactersExist(5)
            expectedCharacterIds = emptySet()
        }

        @Test
        fun `should list all existing characters in project`() {
            givenOppositionExists()
            givenANumberOfCharactersExist(5, projectId = projectId)
            expectedCharacterIds = characterRepository.characters.values.map { it.id.uuid }.toSet()
        }

        @Test
        fun `should filter characters already used as a symbol in the opposition`() {
            givenANumberOfCharactersExist(5, projectId = projectId)
            val (included, excluded) = characterRepository.characters.values.withIndex().groupBy { it.index < 3 }
            givenOppositionExists(characterSymbols = included!!.map { it.value })
            expectedCharacterIds = excluded!!.map { it.value.id.uuid }.toSet()
        }

        private fun givenANumberOfCharactersExist(number: Int, projectId: Project.Id? = null) {
            repeat(number) {
                val id = Character.Id()
                characterRepository.characters[id] = makeCharacter(id, projectId ?: Project.Id())
            }
        }
    }

    @Nested
    inner class `Should list locations` {

        private var expectedLocationIds: Set<UUID> = emptySet()

        @AfterEach
        fun test() {
            listAvailableEntitiesToAddToOpposition()
            result shouldBe entitiesAvailableToAddToOpposition(locationIds = expectedLocationIds)
        }

        @Test
        fun `locations not in same project should not be listed`() {
            givenOppositionExists()
            givenANumberOfLocationsExist(5)
            expectedLocationIds = emptySet()
        }

        @Test
        fun `should list all existing locations in project`() {
            givenOppositionExists()
            givenANumberOfLocationsExist(5, projectId = projectId)
            expectedLocationIds = locationRepository.locations.values.map { it.id.uuid }.toSet()
        }

        @Test
        fun `should filter locations already used as a symbol in the opposition`() {
            givenANumberOfLocationsExist(5, projectId = projectId)
            val (included, excluded) = locationRepository.locations.values.withIndex().groupBy { it.index < 3 }
            givenOppositionExists(locationSymbols = included!!.map { it.value })
            expectedLocationIds = excluded!!.map { it.value.id.uuid }.toSet()
        }

        private fun givenANumberOfLocationsExist(number: Int, projectId: Project.Id? = null) {
            repeat(number) {
                val id = Location.Id()
                locationRepository.locations[id] = makeLocation(id = id, projectId =  projectId ?: Project.Id())
            }
        }

    }

    @Test
    fun `should list all symbols in theme`() {
        givenOppositionExists()
        givenANumberOfSymbolsAreInTheme(5, themeId = themeRepository.themes.keys.first())
        listAvailableEntitiesToAddToOpposition()
        result shouldBe entitiesAvailableToAddToOpposition(symbolIds = themeRepository.themes.values.first().symbols.map { it.id.uuid }.toSet())
    }

    @Test
    fun `should filter symbols already used in the opposition`() {
        val included = List(3) { Symbol("Symbol ${UUID.randomUUID()}") }
        val excluded = List(2) { Symbol("Symbol ${UUID.randomUUID()}") }
        givenOppositionExists(symbols = included)
        givenSymbolsAreInTheme(themeId = themeRepository.themes.keys.first(), symbols = included + excluded)
        listAvailableEntitiesToAddToOpposition()
        result shouldBe entitiesAvailableToAddToOpposition(symbolIds = excluded.map { it.id.uuid }.toSet())
    }

    private val themeRepository = ThemeRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    private fun givenOppositionExists(
        characterSymbols: List<Character> = listOf(),
        locationSymbols: List<Location> = listOf(),
        symbols: List<Symbol> = listOf()
    ) {
        makeTheme(
            projectId = projectId, valueWebs = listOf(
                makeValueWeb(
                    oppositions = listOf(
                        makeOppositionValue(oppositionId, representations = characterSymbols.map {
                            SymbolicRepresentation(it.id.uuid, it.name.value)
                        } + locationSymbols.map {
                            SymbolicRepresentation(it.id.uuid, it.name.value)
                        } + symbols.map {
                            SymbolicRepresentation(it.id.uuid, it.name)
                        })
                    )
                )
            )
        ).let {
            themeRepository.themes[it.id] = it
        }
    }

    private fun givenSymbolsAreInTheme(themeId: Theme.Id, symbols: List<Symbol> = listOf()) {
        val theme = themeRepository.themes[themeId]!!
        val update = symbols.fold(theme) { current, symbol ->
            current.withSymbol(symbol)
        }
        themeRepository.themes[themeId] = update
    }
    private fun givenANumberOfSymbolsAreInTheme(number: Int, themeId: Theme.Id)
    {
        val theme = themeRepository.themes[themeId]!!
        val update = List(number) { Symbol("Symbol ${UUID.randomUUID()}") }.fold(theme) { current, symbol ->
            current.withSymbol(symbol)
        }
        themeRepository.themes[themeId] = update
    }

    private fun listAvailableEntitiesToAddToOpposition() {
        val useCase: ListAvailableEntitiesToAddToOpposition =
            ListAvailableEntitiesToAddToOppositionUseCase(themeRepository, characterRepository, locationRepository)
        val output = object : ListAvailableEntitiesToAddToOpposition.OutputPort {
            override suspend fun availableEntitiesListedToAddToOpposition(response: EntitiesAvailableToAddToOpposition) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(oppositionId.uuid, output)
        }
    }

    private fun entitiesAvailableToAddToOpposition(characterIds: Set<UUID> = setOf(), locationIds: Set<UUID> = setOf(), symbolIds: Set<UUID> = setOf()) =
        fun(actual: Any?) {
            actual as EntitiesAvailableToAddToOpposition
            assertEquals(characterIds, actual.characters.map { it.characterId }.toSet())
            assertEquals(
                characterIds.map { characterRepository.characters[Character.Id(it)]!!.name.value }.toSet(),
                actual.characters.map { it.characterName }.toSet()
            ) { "Character Names not properly mapped" }

            assertEquals(locationIds, actual.locations.map { it.id }.toSet())
            assertEquals(
                locationIds.map { locationRepository.locations[Location.Id(it)]!!.name.value }.toSet(),
                actual.locations.map { it.locationName }.toSet()
            ) { "Location Names not properly mapped" }

            assertEquals(symbolIds, actual.symbols.map { it.symbolId }.toSet())
            val themeSymbolMap = themeRepository.themes.values.first().symbols.associateBy { it.id.uuid }
            assertEquals(
                symbolIds.map { themeSymbolMap.getValue(it).name }.toSet(),
                actual.symbols.map { it.symbolName }.toSet()
            ) { "Symbol Names not properly mapped" }
        }

}
