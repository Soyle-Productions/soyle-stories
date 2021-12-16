package com.soyle.stories.domain.character

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import org.junit.jupiter.api.Test

class CharacterTest {

    val projectId = Project.Id()

    @Test
    fun `characters are built`() {
        Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
    }

}