package com.soyle.stories.domain.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.CoupleOf
import java.util.*

fun makeTheme(
    id: Theme.Id = Theme.Id(),
    projectId: Project.Id = Project.Id(),
    name: String = "Theme ${UUID.randomUUID().toString().take(3)}",
    symbols: List<Symbol> = listOf(),
    centralConflict: String = "",
    centralMoralQuestion: String = "",
    themeLine: String = "",
    thematicRevelation: String = "",
    includedCharacters: Map<Character.Id, CharacterInTheme> = mapOf(),
    similaritiesBetweenCharacters: Map<CoupleOf<Character.Id>, String> = mapOf(),
    valueWebs: List<ValueWeb> = listOf()
): Theme = Theme(
    id, projectId, name, symbols, centralConflict, centralMoralQuestion, themeLine, thematicRevelation, includedCharacters, similaritiesBetweenCharacters, valueWebs
)