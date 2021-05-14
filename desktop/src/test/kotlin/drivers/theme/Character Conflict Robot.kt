package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.desktop.view.theme.characterConflict.`Character Conflict View Access`.Companion.drive
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.characterConflict.CharacterConflict
import com.soyle.stories.theme.characterConflict.CharacterConflictScope
import com.soyle.stories.theme.themeList.ThemeList
import tornadofx.FX

fun ThemeList.givenCharacterConflictToolHasBeenOpenedFor(themeId: Theme.Id): CharacterConflict {
    return getOpenCharacterConflictToolFor(themeId) ?: openCharacterConflict(themeId).run {
        getOpenCharacterConflictToolFor(themeId) ?: error("character conflict tool not opened for $themeId")
    }
}

fun ThemeList.getOpenCharacterConflictToolFor(themeId: Theme.Id): CharacterConflict? {
    val characterConflictScope = (scope as ProjectScope).toolScopes.asSequence().filterIsInstance<CharacterConflictScope>()
        .find { it.themeId == themeId.uuid.toString() } ?: return null
    return (FX.getComponents(characterConflictScope)[CharacterConflict::class] as? CharacterConflict)
        ?.takeIf { it.currentStage?.isShowing == true }
}

fun CharacterConflict.promoteCharacter(characterId: Character.Id)
{
    drive {
        perspectiveCharacterSelection.show()
        val characterItem = perspectiveCharacterSelection.characterItem(characterId)!!
        characterItem.fire()
    }
}