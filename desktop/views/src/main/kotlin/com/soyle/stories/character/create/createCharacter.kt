package com.soyle.stories.character.create

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope

fun createCharacter(
    projectScope: ProjectScope
) {
    val prompt = createCharacterPrompt(projectScope)
    projectScope.get<BuildNewCharacterController>().createCharacter(prompt)
        .invokeOnCompletion { prompt.close() }
}