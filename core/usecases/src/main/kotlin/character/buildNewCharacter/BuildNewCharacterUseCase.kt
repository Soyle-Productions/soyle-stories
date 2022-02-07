package com.soyle.stories.usecase.character.buildNewCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.project.exceptions.ProjectDoesNotExist
import com.soyle.stories.usecase.project.getProjectOrError
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import java.util.*

class BuildNewCharacterUseCase(
    private val projects: ProjectRepository,
    private val characters: CharacterRepository,
) : BuildNewCharacter {

    override suspend fun invoke(
        projectId: Project.Id,
        name: NonBlankString,
        output: BuildNewCharacter.OutputPort
    ): Result<Character.Id> {
        return projects.getProjectOrError(projectId)
            .map { Character.buildNewCharacter(it.id, name) }
            .onSuccess { characters.addNewCharacter(it) }
            .onSuccess { output.characterCreated(CharacterCreated(projectId, it.id, it.displayName.value)) }
            .map { it.id }
    }

}