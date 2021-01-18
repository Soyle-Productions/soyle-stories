package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.characterarc.usecaseControllers.PromoteMinorCharacterController
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.entities.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.theme.createTheme.CreateThemeController
import com.soyle.stories.theme.includeCharacterInTheme.IncludeCharacterInComparisonController
import com.soyle.stories.theme.repositories.ThemeRepository
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.NoSuchElementException

/**
 * This class should only be used for `givens`, never whens or thens.  These classes facilitate the preparation before
 * the actual interaction under test.
 */
class ThemeDriver private constructor(private val projectScope: ProjectScope)
{

    /**
     * Create Theme with Name
     */

    private val previouslyNamedThemes = mutableMapOf<String, Theme.Id>()

    fun givenThemeNamed(themeName: String): Theme =
        getThemeByName(themeName) ?: createThemeWithName(themeName)

    fun getThemeByNameOrError(themeName: String): Theme =
        getThemeByName(themeName) ?: throw NoSuchElementException("No theme named $themeName in project ${projectScope.projectViewModel.name}")

    fun getThemeByName(themeName: String): Theme? {
        val themeRepository = projectScope.get<ThemeRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allThemes = runBlocking { themeRepository.listThemesInProject(projectId) }
        val theme = allThemes.find { it.name == themeName }
        theme?.let { previouslyNamedThemes[themeName] = it.id }
        return theme
    }

    fun createThemeWithName(themeName: String): Theme
    {
        projectScope.get<CreateThemeController>()
            .createTheme(themeName) { throw it }
        return getThemeByNameOrError(themeName)
    }

    fun getThemeAtOnePointNamedOrError(previousThemeName: String): Pair<Theme.Id?, Theme?> =
        getThemeAtOnePointNamed(previousThemeName).also {
            if (it.first == null) error("No theme was ever registered with the name $previousThemeName")
        }

    fun getThemeAtOnePointNamed(previousThemeName: String): Pair<Theme.Id?, Theme?>
    {
        val themeId = previouslyNamedThemes[previousThemeName] ?: return (null to null)
        val themeRepository = projectScope.get<ThemeRepository>()
        return runBlocking { themeId to themeRepository.getThemeById(themeId) }
    }


    /**
     * Promote Character in Theme
     */

    fun givenCharacterIsMajorCharacterInTheme(characterId: Character.Id, themeId: Theme.Id): MajorCharacter =
        getMajorCharacterInTheme(characterId, themeId) ?: run {
            val includedCharacter = givenCharacterIsIncludedInTheme(characterId, themeId)
            promoteCharacterInTheme(includedCharacter.id, themeId)
        }

    fun getMajorCharacterInThemeOrError(characterId: Character.Id, themeId: Theme.Id): MajorCharacter =
        getMajorCharacterInTheme(characterId, themeId) ?: throw NoSuchElementException("Character $characterId is not a major character in the theme $themeId")

    fun getMajorCharacterInTheme(characterId: Character.Id, themeId: Theme.Id): MajorCharacter?
    {
        val themeRepository = projectScope.get<ThemeRepository>()
        val theme = runBlocking { themeRepository.getThemeById(themeId) } ?: throw NoSuchElementException("theme $themeId does not exist")
        return theme.getMajorCharacterById(characterId)
    }

    fun promoteCharacterInTheme(characterId: Character.Id, themeId: Theme.Id): MajorCharacter
    {
        projectScope.get<PromoteMinorCharacterController>()
            .let {
                runBlocking {
                    it.promoteCharacter(themeId.uuid.toString(), characterId.uuid.toString())
                }
            }
        return getMajorCharacterInThemeOrError(characterId, themeId)
    }


    /**
     * Include Character in Theme
     */

    fun givenCharacterIsIncludedInTheme(characterId: Character.Id, themeId: Theme.Id): CharacterInTheme =
        getIncludedCharacterInTheme(characterId, themeId) ?: includeCharacterInTheme(characterId, themeId)

    fun getIncludedCharacterInThemeOrError(characterId: Character.Id, themeId: Theme.Id): CharacterInTheme =
        getIncludedCharacterInTheme(characterId, themeId) ?: throw NoSuchElementException("Character $characterId has not been included in the theme $themeId")

    fun getIncludedCharacterInTheme(characterId: Character.Id, themeId: Theme.Id): CharacterInTheme?
    {
        val themeRepository = projectScope.get<ThemeRepository>()
        val theme = runBlocking { themeRepository.getThemeById(themeId) } ?: throw NoSuchElementException("theme $themeId does not exist")
        return theme.getIncludedCharacterById(characterId)
    }

    fun includeCharacterInTheme(characterId: Character.Id, themeId: Theme.Id): CharacterInTheme
    {
        projectScope.get<IncludeCharacterInComparisonController>()
            .includeCharacterInTheme(themeId.uuid.toString(), characterId.uuid.toString())
        return getIncludedCharacterInThemeOrError(characterId, themeId)
    }


    /**
     * Create Symbol in Theme with Name
     */

    private val previouslyNamedSymbols = mutableMapOf<Pair<Theme.Id, String>, Symbol.Id>()

    fun givenSymbolInThemeNamed(themeId: Theme.Id, symbolName: String): Symbol =
        getSymbolInThemeNamed(themeId, symbolName) ?: createSymbolInThemeNamed(themeId, symbolName)

    fun getSymbolInThemeNamedOrError(themeId: Theme.Id, symbolName: String): Symbol =
        getSymbolInThemeNamed(themeId, symbolName) ?: error("No symbol in theme $themeId named $symbolName")

    fun getSymbolInThemeNamed(themeId: Theme.Id, symbolName: String): Symbol?
    {
        val themeRepository = projectScope.get<ThemeRepository>()
        val theme = runBlocking { themeRepository.getThemeById(themeId) } ?: throw NoSuchElementException("theme $themeId does not exist")
        val symbol = theme.symbols.find { it.name == symbolName }
        symbol?.let { previouslyNamedSymbols[theme.id to symbolName] = symbol.id }
        return symbol
    }

    fun createSymbolInThemeNamed(themeId: Theme.Id, symbolName: String): Symbol
    {
        projectScope.get<AddSymbolToThemeController>()
            .addSymbolToTheme(themeId.uuid.toString(), symbolName) { throw it }
        return getSymbolInThemeNamedOrError(themeId, symbolName)
    }

    fun getSymbolInThemeAtOnePointNamedOrError(themeId: Theme.Id, previousSymbolName: String): Pair<Symbol.Id, Symbol?> =
        getSymbolInThemeAtOnePointNamed(themeId, previousSymbolName).let {
            val (id, symbol) = it
            if (id == null) error("No symbol was ever registered with the name $previousSymbolName")
            id to symbol
        }

    fun getSymbolInThemeAtOnePointNamed(themeId: Theme.Id, previousSymbolName: String): Pair<Symbol.Id?, Symbol?>
    {
        val symbolId = previouslyNamedSymbols[themeId to previousSymbolName] ?: return (null to null)
        val themeRepository = projectScope.get<ThemeRepository>()
        return runBlocking { symbolId to themeRepository.getThemeById(themeId)?.symbols?.find { it.id == symbolId } }
    }


    /**
     * Create Value Web in Theme with Name
     */

    private val previouslyNamedValueWebs = mutableMapOf<Pair<Theme.Id, String>, ValueWeb.Id>()

    fun givenValueWebInThemeNamed(themeId: Theme.Id, valueWebName: String): ValueWeb =
        getValueWebInThemeNamed(themeId, valueWebName) ?: createValueWebInThemeNamed(themeId, valueWebName)

    fun getValueWebInThemeNamedOrError(themeId: Theme.Id, valueWebName: String): ValueWeb =
        getValueWebInThemeNamed(themeId, valueWebName) ?: error("No value web in theme $themeId named $valueWebName")

    fun getValueWebInThemeNamed(themeId: Theme.Id, valueWebName: String): ValueWeb?
    {
        val themeRepository = projectScope.get<ThemeRepository>()
        val theme = runBlocking { themeRepository.getThemeById(themeId) } ?: throw NoSuchElementException("theme $themeId does not exist")
        val valueWeb = theme.valueWebs.find { it.name == valueWebName }
        valueWeb?.let { previouslyNamedValueWebs[theme.id to valueWebName] = valueWeb.id }
        return valueWeb
    }

    fun createValueWebInThemeNamed(themeId: Theme.Id, valueWebName: String): ValueWeb
    {
        projectScope.get<AddValueWebToThemeController>()
            .addValueWebToTheme(themeId.uuid.toString(), valueWebName) { throw it }
        return getValueWebInThemeNamedOrError(themeId, valueWebName)
    }

    fun getValueWebInThemeAtOnePointNamedOrError(themeId: Theme.Id, previousValueWebName: String): Pair<ValueWeb.Id, ValueWeb?> =
        getValueWebInThemeAtOnePointNamed(themeId, previousValueWebName).let {
            val (id, symbol) = it
            if (id == null) error("No value web was ever registered with the name $previousValueWebName")
            id to symbol
        }

    fun getValueWebInThemeAtOnePointNamed(themeId: Theme.Id, previousValueWebName: String): Pair<ValueWeb.Id?, ValueWeb?>
    {
        val valueWebId = previouslyNamedValueWebs[themeId to previousValueWebName] ?: return (null to null)
        val themeRepository = projectScope.get<ThemeRepository>()
        return runBlocking { valueWebId to themeRepository.getThemeById(themeId)?.valueWebs?.find { it.id == valueWebId } }
    }




    fun givenSymbolicItemInThemeAddedToOpposition(itemId: Any, oppositionValueId: OppositionValue.Id, isSameId: (UUID) -> Boolean): SymbolicRepresentation =
        getSymbolicItemInOppositionForItem(oppositionValueId, isSameId) ?: addSymbolicItemToOpposition(itemId, oppositionValueId)


    fun getSymbolicItemInOppositionForItemOrError(oppositionValueId: OppositionValue.Id, itemId: Any, isSameId: (UUID) -> Boolean): SymbolicRepresentation =
        getSymbolicItemInOppositionForItem(oppositionValueId, isSameId)
            ?: error("No symbolic representation in opposition value $oppositionValueId for item $itemId")

    fun getSymbolicItemInOppositionForItem(oppositionValueId: OppositionValue.Id, isSameId: (UUID) -> Boolean): SymbolicRepresentation?
    {
        val themeRepository = projectScope.get<ThemeRepository>()
        val theme = runBlocking { themeRepository.getThemeContainingOppositionValueWithId(oppositionValueId) }
            ?: error("No theme exists that contains opposition value $oppositionValueId")
        return theme.valueWebs
            .asSequence()
            .flatMap { it.oppositions.asSequence() }
            .find { it.id == oppositionValueId }
            ?.representations?.find { isSameId(it.entityUUID) }
    }

    fun getSymbolicItemsForItem(isItem: (UUID) -> Boolean): List<SymbolicRepresentation>
    {
        val themeRepository = projectScope.get<ThemeRepository>()
        val themes = runBlocking { themeRepository.listThemesInProject(Project.Id(projectScope.projectId)) }
        return themes.asSequence()
            .flatMap { it.valueWebs.asSequence() }
            .flatMap { it.oppositions.asSequence() }
            .flatMap { it.representations.asSequence() }
            .filter { isItem(it.entityUUID) }
            .toList()
    }

    fun addSymbolicItemToOpposition(itemId: Any, oppositionValueId: OppositionValue.Id): SymbolicRepresentation
    {
        val controller = projectScope.get<AddSymbolicItemToOppositionController>()
        val oppositionValueIdString = oppositionValueId.uuid.toString()
        val itemUUID: UUID
        when (itemId) {
            is Character.Id -> {
                itemUUID = itemId.uuid
                controller.addCharacterToOpposition(oppositionValueIdString, itemUUID.toString())
            }
            is Location.Id -> {
                itemUUID = itemId.uuid
                controller.addLocationToOpposition(oppositionValueIdString, itemUUID.toString())
            }
            is Symbol.Id -> {
                itemUUID = itemId.uuid
                controller.addSymbolToOpposition(oppositionValueIdString, itemUUID.toString())
            }
            else -> error("Do not currently support adding itemId $itemId as symbolic item")
        }
        return getSymbolicItemInOppositionForItemOrError(oppositionValueId, itemId) { it == itemUUID }
    }

    companion object {

        private var isFirstCall = true
        operator fun invoke(workBench: WorkBench): ThemeDriver
        {
            if (isFirstCall) {
                scoped<ProjectScope> {
                    provide { ThemeDriver(this) }
                }
                isFirstCall = false
            }
            return workBench.scope.get()
        }

    }

}