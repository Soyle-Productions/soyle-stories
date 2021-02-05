package com.soyle.stories.character

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectScope
import kotlinx.coroutines.runBlocking

fun ProjectScope.givenACharacterHasBeenCreatedNamed(characterName: String): Character
{
    get<CreateCharacterDialogViewListener>().createCharacter(characterName)
    val repository = get<CharacterRepository>()
    return runBlocking {
        repository.listCharactersInProject(Project.Id(projectId)).find { it.name == characterName }!!
    }
}