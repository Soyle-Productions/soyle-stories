package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.buildNewCharacter.CreateCharacterPrompt
import com.soyle.stories.character.nameVariant.addNameVariant.AddCharacterNameVariantController
import com.soyle.stories.character.nameVariant.remove.RemoveCharacterNameVariantController
import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.character.removeCharacterFromStory.RamificationsReport
import com.soyle.stories.character.removeCharacterFromStory.RemoveCharacterFromStoryController
import com.soyle.stories.character.renameCharacter.RenameCharacterController
import com.soyle.stories.characterarc.planNewCharacterArc.PlanNewCharacterArcController
import com.soyle.stories.common.Confirmation
import com.soyle.stories.desktop.config.drivers.awaitWithTimeout
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import kotlinx.coroutines.runBlocking
import tornadofx.runLater

class CharacterDriver private constructor(private val projectScope: ProjectScope) {

    private val characterNameHistory = mutableMapOf<String, MutableSet<Character.Id>>().withDefault { mutableSetOf() }

    fun givenCharacterNamed(characterName: NonBlankString): Character =
        getCharacterByName(characterName.value) ?: createCharacterWithName(characterName)

    fun getCharacterByNameOrError(characterName: String): Character =
        getCharacterByName(characterName)
            ?: throw NoSuchElementException("No character named $characterName in project ${projectScope.projectViewModel.name}")

    fun getCharacterByName(characterName: String): Character? {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val allCharacters = runBlocking { characterRepository.listCharactersInProject(projectId) }
        allCharacters.onEach {
            characterNameHistory[it.displayName.value] = characterNameHistory.getValue(it.displayName.value).apply { add(it.id) }
        }
        return allCharacters.find { it.displayName.value == characterName }
    }

    fun getCharacterCountInProject(): Int {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        return runBlocking { characterRepository.listCharactersInProject(projectId) }.size
    }

    fun getCharacterAtOnePointNamed(formerName: String): Character? {
        val characterRepository = projectScope.get<CharacterRepository>()
        val projectId = Project.Id(projectScope.projectId)
        val charactersById = runBlocking { characterRepository.listCharactersInProject(projectId) }
            .associateBy { it.id }
        return characterNameHistory.getValue(formerName).lastOrNull()?.let { charactersById[it] }
    }

    private fun createCharacterWithName(characterName: NonBlankString): Character {
        val result = projectScope.get<BuildNewCharacterController>()
            .createCharacter { characterName }
        result.invokeOnCompletion { result.getCompletionExceptionOrNull()?.let { throw it } }
        awaitWithTimeout(1000) { result.isCompleted }
        return getCharacterByNameOrError(characterName.value)
    }

    fun givenCharacterHasANameVariant(character: Character, variant: String): NonBlankString {
        return getCharacterNameVariant(character.id, variant) ?: createNameVariant(character.id, variant).run {
            getCharacterNameVariantOrError(character.id, variant)
        }
    }

    fun getCharacterNameVariantOrError(characterId: Character.Id, variant: String): NonBlankString =
        getCharacterNameVariant(characterId, variant) ?: error("$characterId does not have name variant $variant")

    fun getCharacterNameVariant(characterId: Character.Id, variant: String): NonBlankString? = runBlocking {
        projectScope.get<CharacterRepository>()
            .getCharacterById(characterId)
            ?.names?.find { it.value == variant }
    }

    fun createNameVariant(characterId: Character.Id, variant: String) {
        runBlocking {
            projectScope.get<AddCharacterNameVariantController>().addCharacterNameVariant(characterId, NonBlankString.create(variant)!!).join()
        }
    }

    fun givenCharacterHasAnArcNamed(character: Character, name: String): CharacterArc =
        getCharacterArcByName(character, name) ?: createCharacterArcWithName(
            character,
            name
        ).run { getCharacterArcByNameOrError(character, name) }

    fun getCharacterArcByNameOrError(character: Character, name: String): CharacterArc =
        getCharacterArcByName(character, name) ?: error("No character arc for ${character.displayName} with name $name")

    fun getCharacterArcByName(character: Character, name: String): CharacterArc? {
        val repository = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repository.listCharacterArcsForCharacter(character.id)
                .find { it.name == name }
        }
    }

    fun getCharacterArcsForCharacter(character: Character): List<CharacterArc>
    {
        val repository = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repository.listCharacterArcsForCharacter(character.id)
        }
    }

    fun getCharacterArcByCharacterAndTheme(character: Character, theme: Theme): CharacterArc?
    {
        val repository = projectScope.get<CharacterArcRepository>()
        return runBlocking {
            repository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)
        }
    }

    fun getCharacterArcSectionByNameOrError(character: Character, theme: Theme, sectionName: String): CharacterArcSection =
        getCharacterArcSectionByName(character, theme, sectionName) ?: error("${character.displayName} does not have a $sectionName section for the ${theme.name} theme")

    fun getCharacterArcSectionByName(character: Character, theme: Theme, sectionName: String): CharacterArcSection?
    {
        val arc = getCharacterArcByCharacterAndTheme(character, theme) ?: return null
        return arc.moralArgument().arcSections.find { it.template.name == sectionName }
    }

    private fun createCharacterArcWithName(character: Character, name: String) {
        runBlocking {
            projectScope.get<PlanNewCharacterArcController>()
                .planCharacterArc(character.id.uuid.toString(), name).join()
        }
    }

    fun givenCharacterRenamedTo(characterId: Character.Id, currentName: String, name: String) {
        projectScope.get<RenameCharacterController>()
            .renameCharacter(characterId, NonBlankString.create(currentName)!!, NonBlankString.create(name)!!)
    }

    fun givenCharacterRemoved(character: Character) {
        projectScope.get<RemoveCharacterFromStoryController>()
            .removeCharacter(
                character.id,
                { Confirmation(ConfirmationPrompt.Response.Confirm, true) },
                {  }
            )
    }

    fun givenCharacterNameVariantRemoved(characterId: Character.Id, variant: String) {
        val nonBlankVariant = getCharacterNameVariant(characterId, variant) ?: return
        removeNameVariant(characterId, nonBlankVariant)
    }

    private fun removeNameVariant(characterId: Character.Id, variant: NonBlankString)
    {
        runBlocking {
            projectScope.get<RemoveCharacterNameVariantController>()
                .removeCharacterNameVariant(characterId, variant).join()
        }
    }

    companion object {
        private var isFirstCall = true
        operator fun invoke(workBench: WorkBench): CharacterDriver {
            if (isFirstCall) {
                scoped<ProjectScope> {
                    provide { CharacterDriver(this) }
                }
                isFirstCall = false
            }
            return workBench.scope.get()
        }
    }

}