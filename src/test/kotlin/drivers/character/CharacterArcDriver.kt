package com.soyle.stories.desktop.config.drivers.character

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.repositories.CharacterArcRepository
import kotlinx.coroutines.runBlocking

class CharacterArcDriver private constructor(private val projectScope: ProjectScope) {

    fun getCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc>
    {
        val characterArcRepository = projectScope.get<CharacterArcRepository>()
        return runBlocking { characterArcRepository.listCharacterArcsForTheme(themeId) }
    }

    companion object {
        private var isFirstCall = true
        operator fun invoke(workbench: WorkBench): CharacterArcDriver
        {
            if (isFirstCall) {
                scoped<ProjectScope> { provide { CharacterArcDriver(this) } }
                isFirstCall = false
            }
            return workbench.scope.get()
        }
    }

}