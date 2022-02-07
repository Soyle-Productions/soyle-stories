package com.soyle.stories.domain.project.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateProjectOperationException(
    override val projectId: Project.Id,
    override val message: String
) : DuplicateOperationException(), ProjectException

fun ProjectAlreadyNamed(projectId: Project.Id, name: String) =
    DuplicateProjectOperationException(projectId, "$projectId already named $name")

fun ProjectAlreadyHasCharacter(projectId: Project.Id, characterId: Character.Id) =
    DuplicateProjectOperationException(projectId, "$projectId already has $characterId")