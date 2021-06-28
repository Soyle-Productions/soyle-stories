package com.soyle.stories.desktop.config.drivers.soylestories

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStories

class ScenarioContext private constructor(private val appScope: ApplicationScope) {

    var templateSectionToAdd: String? = null
    var templateSectionToMove: String? = null
    var updatedCharacterArc: CharacterArc? = null
    var sectionsSurroundingTemplateSection: Pair<CharacterArcSection, CharacterArcSection>? = null

    companion object {
        init {
            scoped<ApplicationScope> { provide { ScenarioContext(this) } }
        }
        operator fun invoke(application: SoyleStories): ScenarioContext = application.scope.get()
    }

}