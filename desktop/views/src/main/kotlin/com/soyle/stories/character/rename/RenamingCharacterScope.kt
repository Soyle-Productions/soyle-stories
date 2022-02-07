package com.soyle.stories.character.rename

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import tornadofx.Scope

class RenamingCharacterScope(
    val characterId: Character.Id,
    val currentName: NonBlankString,
    val projectScope: ProjectScope
) : Scope()